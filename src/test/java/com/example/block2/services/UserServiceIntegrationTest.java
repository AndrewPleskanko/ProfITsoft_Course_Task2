package com.example.block2.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.block2.dto.GroupResponseDto;
import com.example.block2.dto.RoleDto;
import com.example.block2.dto.UserDto;
import com.example.block2.dto.UserFilterDto;
import com.example.block2.entity.Role;
import com.example.block2.entity.User;
import com.example.block2.exceptions.EntityNotFoundException;
import com.example.block2.mapper.RoleMapper;
import com.example.block2.mapper.UserMapper;
import com.example.block2.repositories.RoleRepository;
import com.example.block2.repositories.UserRepository;
import com.example.block2.utils.RoleTestUtils;
import com.example.block2.utils.UserTestUtils;

@SpringBootTest
public class UserServiceIntegrationTest extends BaseServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    Role roleUser;

    @BeforeEach
    void setUp() {
        //Given
        RoleDto roleUserDto = RoleTestUtils.createRoleDto(1, "ROLE_USER");
        RoleDto roleAdminDto = RoleTestUtils.createRoleDto(2, "ROLE_ADMIN");
        Role roleAdmin = roleMapper.toEntity(roleAdminDto);
        roleUser = roleMapper.toEntity(roleUserDto);
        new User("test1", "test123", "presF@gmail.com", roleUser);
        new User("test2", "test234", "test2@gmail.com", roleAdmin);
        UserTestUtils.createUserDto("john", "123", roleUser, "testUser@gmail.com");
        UserTestUtils.createUserDto("admin", "admin", roleAdmin, "testAdmin@gmail.com");
    }


    @Test
    public void createUser_createsNewUser_returnsCreatedUser() {
        // Given
        UserDto userDto = new UserDto(null, "testCreateUser",
                "testCreatePassword", "testCreateUser@gmail.com", roleUser);

        // When
        User createdUserDto = userService.createUser(userDto);

        // Then
        assertNotNull(createdUserDto.getId());
        assertEquals(userDto.getUsername(), createdUserDto.getUsername());
        assertEquals(userDto.getEmail(), createdUserDto.getEmail());
        assertEquals(userDto.getRole().getId(), createdUserDto.getRole().getId());
        }

    @Test
    public void getAllUsers_createsAndGetsUsers_returnsUsers() {
        // Given
        User user1 = userRepository.save(new User("testUser1", "testPassword1", "testUser1@gmail.com", roleUser));
        userRepository.save(new User("testUser2", "testPassword2", "testUser2@gmail.com", roleUser));

        // When
        List<UserDto> users = userService.getAllUsers();

        // Then
        assertEquals(6, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals(user1.getUsername())));
    }


    @Test
    void getUserById_getsUserById_returnsUser() {
        // Given
        User user = new User("testUser", "testPassword", "testUser@gmail.com", roleUser);
        User savedUser = userRepository.save(user);
        Long id = savedUser.getId();

        // When
        User result = userService.getUser(id);

        // Then
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getRole().getId(), result.getRole().getId());
    }

    @Test
    public void updateUser_updatesUser_returnsUpdatedUser() {
        // Given
        UserDto userDto = new UserDto(null, "testUpdateUser",
                "testUpdatePassword", "testUpdateUser@gmail.com", roleUser);
        User createdUserDto = userService.createUser(userDto);
        createdUserDto.setUsername("updatedUsername");
        createdUserDto.setPassword("updatedPassword");

        // When
        User updatedUserDto = userService.updateUser(createdUserDto.getId(), userMapper.toDto(createdUserDto));

        // Then
        assertEquals(createdUserDto.getId(), updatedUserDto.getId());
        assertEquals("updatedUsername", updatedUserDto.getUsername());
    }

    @Test
    public void deleteUser_deletesUser_userDoesNotExist() {
        // Given
        Role role = roleRepository.save(new Role("ROLE_USER"));
        User user = new User("testUser", "testPassword", "testUser@gmail.com", role);
        User createdUserDto = userService.createUser(userMapper.toDto(user));

        // When
        userService.deleteUser(createdUserDto.getId());

        // Then
        assertFalse(userRepository.existsById(createdUserDto.getId()));
    }

    @Test
    public void listUsers_listsUsers_returnsUsers() {
        // Given
        Role role = roleRepository.save(new Role("USER"));
        userRepository.save(new User("testUser1345444444442",
                "testPassword1", "testUser1@gmail.com", role));
        userRepository.save(new User("testUser22467358467534",
                "testPassword2", "testUser2@gmail.com", role));
        UserFilterDto filter = new UserFilterDto();
        filter.setUsername("testUser");
        Pageable pageable = PageRequest.of(0, 5);

        // When
        GroupResponseDto<UserDto> users = userService.listUsers(filter, pageable);

        // Then
        assertEquals(3, users.getContent().size());
    }

    @Test
    public void createUser_withNullInput_throwsRuntimeException() {
        // When & Then
        assertThrows(RuntimeException.class, () -> userService.createUser(null));
    }

    @Test
    public void getUser_withInvalidId_throwsUserNotFoundException() {
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(-1L));
    }

    @Test
    public void updateUser_withInvalidId_throwsRuntimeException() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setUsername("Test User");

        // When & Then
        assertThrows(RuntimeException.class, () -> userService.updateUser(-1L, userDto));
    }

    @Test
    public void deleteUser_withInvalidId_throwsUserNotFoundException() {
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(-1L));
    }
}