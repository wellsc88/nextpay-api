package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.entity.RefreshToken;
import com.well.tech.next.pay.entity.User;
import com.well.tech.next.pay.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService service;

    private User user;

    @BeforeEach
    void setup() throws Exception {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());

        Field field =
                RefreshTokenService.class
                        .getDeclaredField("refreshExpiration");

        field.setAccessible(true);

        field.set(
                service,
                604800000L
        );
    }

    @Test
    void shouldCreateNewRefreshTokenWhenUserHasNoToken() {

        when(repository.findByUser(user))
                .thenReturn(Optional.empty());

        when(repository.save(any(RefreshToken.class)))
                .thenAnswer(
                        invocation -> invocation.getArgument(0)
                );

        RefreshToken result =
                service.createOrUpdate(user);

        assertNotNull(result);

        assertNotNull(
                result.getToken()
        );

        assertEquals(
                user,
                result.getUser()
        );

        assertTrue(
                result.getExpiresAt()
                        .isAfter(Instant.now())
        );

        verify(repository)
                .findByUser(user);

        verify(repository)
                .save(any(RefreshToken.class));
    }

    @Test
    void shouldUpdateExistingRefreshToken() {

        RefreshToken token =
                RefreshToken.builder()
                        .token("old-token")
                        .user(user)
                        .expiresAt(
                                Instant.now()
                        )
                        .build();

        when(repository.findByUser(user))
                .thenReturn(
                        Optional.of(token)
                );

        when(repository.save(token))
                .thenReturn(token);

        RefreshToken result =
                service.createOrUpdate(user);

        assertNotEquals(
                "old-token",
                result.getToken()
        );

        assertTrue(
                result.getExpiresAt()
                        .isAfter(Instant.now())
        );

        verify(repository)
                .save(token);
    }

    @Test
    void shouldFindTokenSuccessfully() {

        RefreshToken token =
                RefreshToken.builder()
                        .token("abc")
                        .user(user)
                        .build();

        when(repository.findByToken("abc"))
                .thenReturn(
                        Optional.of(token)
                );

        RefreshToken result =
                service.findByToken("abc");

        assertEquals(
                token,
                result
        );

        verify(repository)
                .findByToken("abc");
    }

    @Test
    void shouldThrowExceptionWhenTokenNotFound() {

        when(repository.findByToken("invalid"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.findByToken(
                                        "invalid"
                                )
                );

        assertEquals(
                "Refresh token not found",
                exception.getMessage()
        );
    }

    @Test
    void shouldDeleteExpiredToken() {

        RefreshToken token =
                RefreshToken.builder()
                        .token("expired")
                        .user(user)
                        .expiresAt(
                                Instant.now()
                                        .minusSeconds(60)
                        )
                        .build();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.verifyExpiration(token)
        );

        verify(repository)
                .delete(token);
    }

    @Test
    void shouldNotDeleteValidToken() {

        RefreshToken token =
                RefreshToken.builder()
                        .token("valid")
                        .user(user)
                        .expiresAt(
                                Instant.now()
                                        .plusSeconds(600)
                        )
                        .build();

        assertDoesNotThrow(
                () ->
                        service.verifyExpiration(token)
        );

        verify(repository, never())
                .delete(any());
    }

    @Test
    void shouldDeleteTokenByValue() {

        RefreshToken token =
                RefreshToken.builder()
                        .token("abc")
                        .user(user)
                        .expiresAt(
                                Instant.now()
                                        .plusSeconds(600)
                        )
                        .build();

        when(repository.findByToken("abc"))
                .thenReturn(
                        Optional.of(token)
                );

        service.deleteByToken("abc");

        verify(repository)
                .delete(token);
    }
}