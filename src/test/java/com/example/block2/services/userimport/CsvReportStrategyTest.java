package com.example.block2.services.userimport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.block2.entity.User;
import com.example.block2.enums.UserReportType;

@SpringBootTest
class CsvReportStrategyTest {

    @Autowired
    private CsvUserReportStrategy csvUserReportStrategy;

    @Test
    public void testGenerateReport() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("test1");
        user1.setEmail("test1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("test2");
        user2.setEmail("test2@example.com");

        List<User> users = Arrays.asList(user1, user2);

        byte[] report = csvUserReportStrategy.generateReport(users);

        assertNotNull(report);
        assertTrue(report.length > 0);
    }

    @Test
    public void testGetType() {
        assertEquals(UserReportType.CSV, csvUserReportStrategy.getType());
    }

    @Test
    public void testGetFileExtension() {
        assertEquals(".csv", csvUserReportStrategy.getFileExtension());
    }
}