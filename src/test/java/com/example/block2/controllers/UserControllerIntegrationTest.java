package com.example.block2.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;

import com.example.block2.dto.RoleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.block2.dto.GroupResponseDto;
import com.example.block2.dto.UserDto;
import com.example.block2.dto.UserFilterDto;
import com.example.block2.entity.Role;
import com.example.block2.enums.UserReportType;
import com.example.block2.mapper.RoleMapper;
import com.example.block2.repositories.RoleRepository;
import com.example.block2.utils.RoleTestUtils;
import com.example.block2.utils.UserTestUtils;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    RoleMapper roleMapper;

    private UserDto userDto;

    @BeforeEach
    void setup() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        roleRepository.save(role); // Save the role to the database

        userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("testPassword");
        userDto.setEmail("test@example.com");
        userDto.setRole(role);
    }

    @Test
    void getUser_GetsExistingUser_ReturnsUser() {
        // Given
        ResponseEntity<UserDto> createdResponse = restTemplate.postForEntity("/api/v1/users", userDto, UserDto.class);
        Long createdUserId = Objects.requireNonNull(createdResponse.getBody()).getId();

        // When
        ResponseEntity<UserDto> response = restTemplate.getForEntity("/api/v1/users/" + createdUserId, UserDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userDto.getUsername(), response.getBody().getUsername());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void updateUser_UpdatesUserFields_ReturnsUpdatedUser() {
        // Given
        ResponseEntity<UserDto> createdResponse = restTemplate.postForEntity("/api/v1/users", userDto, UserDto.class);
        Long createdUserId = Objects.requireNonNull(createdResponse.getBody()).getId();
        userDto.setUsername("updatedUsername");

        // When
        HttpEntity<UserDto> requestEntity = new HttpEntity<>(userDto);
        ResponseEntity<UserDto> response = restTemplate.exchange(
                "/api/v1/users/" + createdUserId, HttpMethod.PUT, requestEntity, UserDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updatedUsername", response.getBody().getUsername());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void deleteUser_DeletesExistingUser_ReturnsOkStatus() {
        // Given
        ResponseEntity<UserDto> createdResponse = restTemplate.postForEntity("/api/v1/users", userDto, UserDto.class);
        Long createdUserId = Objects.requireNonNull(createdResponse.getBody()).getId();

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/users/" + createdUserId, HttpMethod.DELETE, null, Void.class);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void listUsers_GetsUserList_ReturnsUserList() {
        // Given
        UserFilterDto filter = new UserFilterDto();
        HttpEntity<UserFilterDto> requestEntity = new HttpEntity<>(filter);

        // When
        ResponseEntity<GroupResponseDto<UserDto>> response = restTemplate.exchange(
                "/api/v1/users/_list", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
                });

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void generateReport_GeneratesUserReport_ReturnsReport() {
        // Given
        UserFilterDto filter = new UserFilterDto();
        filter.setRoleId(1L);
        filter.setReportType(UserReportType.CSV);
        HttpEntity<UserFilterDto> requestEntity = new HttpEntity<>(filter);

        // When
        ResponseEntity<byte[]> response = restTemplate.exchange(
                "/api/v1/users/_report", HttpMethod.POST, requestEntity, byte[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void uploadFile_UploadsValidFile_ReturnsOkStatus() {
        // Given
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource("test data".getBytes()) {
            @Override
            public String getFilename() {
                return "test.txt";
            }
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/users/upload", HttpMethod.POST, requestEntity, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}