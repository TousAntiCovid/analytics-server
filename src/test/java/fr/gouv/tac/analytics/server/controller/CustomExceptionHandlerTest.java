package fr.gouv.tac.analytics.server.controller;

import static fr.gouv.tac.analytics.server.controller.CustomExceptionHandler.PAYLOAD_TOO_LARGE;

import java.time.ZonedDateTime;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.gouv.tac.analytics.server.controller.vo.ErrorVo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.ZonedDateTime;

@ExtendWith(SpringExtension.class)
public class CustomExceptionHandlerTest {

    @Mock
    private ConstraintViolationException constraintViolationException;

    @Mock
    private JsonProcessingException jsonProcessingException;

    @InjectMocks
    private CustomExceptionHandler customExceptionHandler;

    @Test
    public void shouldManageAuthenticationException() {

        final OAuth2AuthenticationException oAuth2AuthenticationException = new OAuth2AuthenticationException(new OAuth2Error("someCode"), "someMessage");

        final ResponseEntity<ErrorVo> result = customExceptionHandler.exception(oAuth2AuthenticationException);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Assertions.assertThat(result.getBody().getMessage()).isEqualTo(oAuth2AuthenticationException.getMessage());
        Assertions.assertThat(result.getBody().getTimestamp()).isEqualToIgnoringSeconds(ZonedDateTime.now());
    }

    @Test
    public void shouldManageConstraintViolationException() {

        final String message = "error message";
        Mockito.when(constraintViolationException.getMessage()).thenReturn(message);

        final ResponseEntity<ErrorVo> result = customExceptionHandler.exception(constraintViolationException);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(result.getBody().getMessage()).isEqualTo(message);
        Assertions.assertThat(result.getBody().getTimestamp()).isEqualToIgnoringSeconds(ZonedDateTime.now());
    }

    @Test
    public void shouldManageJsonProcessingException() {

        final String message = "error message";
        Mockito.when(jsonProcessingException.getMessage()).thenReturn(message);

        final ResponseEntity<ErrorVo> result = customExceptionHandler.exception(jsonProcessingException);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(result.getBody().getMessage()).isEqualTo(message);
        Assertions.assertThat(result.getBody().getTimestamp()).isEqualToIgnoringSeconds(ZonedDateTime.now());
    }

    @Test
    public void shouldManageConstraintViolationExceptionInCaseOfTooLargePayload() {

        final String message = PAYLOAD_TOO_LARGE + " - error message";
        Mockito.when(constraintViolationException.getMessage()).thenReturn(message);

        final ResponseEntity<ErrorVo> result = customExceptionHandler.exception(constraintViolationException);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        Assertions.assertThat(result.getBody().getMessage()).isEqualTo(message);
        Assertions.assertThat(result.getBody().getTimestamp()).isEqualToIgnoringSeconds(ZonedDateTime.now());
    }

    @Test
    public void shouldManageEveryOtherException() {

        final Exception exception = new Exception("someMessage");

        final ResponseEntity<ErrorVo> result = customExceptionHandler.exception(exception);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(result.getBody().getMessage()).isEqualTo(exception.getMessage());
        Assertions.assertThat(result.getBody().getTimestamp()).isEqualToIgnoringSeconds(ZonedDateTime.now());
    }

}