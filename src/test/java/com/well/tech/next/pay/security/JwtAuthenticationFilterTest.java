package com.well.tech.next.pay.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private CustomUserDetailsService userDetailsService;
    private TestJwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        filter = new TestJwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderIsMissing() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.execute(request, response, chain);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void shouldContinueWhenTokenIsInvalid() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.isValid("invalid-token"))
                .thenReturn(false);

        filter.execute(request, response, chain);

        verify(jwtService).isValid("invalid-token");
        verifyNoInteractions(userDetailsService);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws Exception {

        UUID userId = UUID.randomUUID();
        String token = "valid-token";
        String email = "john@test.com";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        Claims claims = mock(Claims.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(jwtService.isValid(token))
                .thenReturn(true);

        when(jwtService.extractUserId(token))
                .thenReturn(userId);

        when(jwtService.extractClaims(token))
                .thenReturn(claims);

        when(claims.get("email", String.class))
                .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(userDetails.getAuthorities())
                .thenReturn(List.of());

        filter.execute(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNotNull();

        verify(jwtService).extractUserId(token);
        verify(jwtService).extractClaims(token);
        verify(userDetailsService).loadUserByUsername(email);
    }

    static class TestJwtAuthenticationFilter extends JwtAuthenticationFilter {

        TestJwtAuthenticationFilter(
                JwtService jwtService,
                CustomUserDetailsService userDetailsService) {

            super(jwtService, userDetailsService);
        }

        void execute(
                MockHttpServletRequest request,
                MockHttpServletResponse response,
                MockFilterChain chain) throws Exception {

            super.doFilterInternal(request, response, chain);
        }
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderDoesNotStartWithBearer() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.execute(request, response, chain);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }
}