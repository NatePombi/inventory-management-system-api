package com.nate.inventorymanagementsystemapi.util;

import com.nate.inventorymanagementsystemapi.model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private String username = "tester";
    private Role role = Role.USER;

    @Test
    void testGenerateToken(){
        String token = JwtUtil.generateToken(username,role);

        assertNotNull(token,"token should not be null");
        assertTrue(JwtUtil.tokenValidation(token));
    }


    @Test
    void testExtractUsername(){
        String token = JwtUtil.generateToken(username,role);

        String extracted = JwtUtil.extractUsername(token);

        assertNotNull(token,"token should not be null");
        assertEquals(username,extracted,"username should be the same as the extracted one");
    }

    @Test
    void testTokenValidation_Fail(){
        String badToken = "fake-token-test";

        assertNotNull(badToken);
        assertFalse(JwtUtil.tokenValidation(badToken),"should fail because not a valid token");
    }
}
