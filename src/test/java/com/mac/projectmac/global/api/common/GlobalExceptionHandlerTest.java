package com.mac.projectmac.global.api.common;

import com.mac.projectmac.global.domain.common.error.ErrorCode;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(new MockEnvironment());

    @Test
    void validationExceptionReturnsFieldErrorsInCommonResponse() throws Exception {
        TestRequest requestBody = new TestRequest("");
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(requestBody, "testRequest");
        bindingResult.addError(new FieldError(
                "testRequest",
                "name",
                requestBody.name(),
                false,
                null,
                null,
                "이름은 필수입니다."
        ));

        Method method = TestController.class.getDeclaredMethod("create", TestRequest.class);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/test");

        var response = handler.handleMethodArgumentNotValidException(exception, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("COMMON-VALIDATION-FAILED");
        assertThat(response.getBody().message()).isEqualTo("요청값 검증에 실패했습니다.");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
        assertThat(response.getBody().errors())
                .containsExactly(new ValidationError("name", "이름은 필수입니다."));
    }

    @Test
    void generalErrorResponseHasEmptyValidationErrors() {
        ApiErrorResponse response =
                ApiErrorResponse.of(400, "COMMON-BAD-REQUEST", "잘못된 요청입니다.", "/api/test");

        assertThat(response.errors()).isEmpty();
    }

    @Test
    void domainExceptionReturnsErrorCodeResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test/1");
        NotFoundException exception = new NotFoundException(TestErrorCode.TEST_NOT_FOUND);

        var response = handler.handleDomainException(exception, request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().code()).isEqualTo("TEST-404");
        assertThat(response.getBody().message()).isEqualTo("테스트 리소스를 찾을 수 없습니다.");
        assertThat(response.getBody().path()).isEqualTo("/api/test/1");
        assertThat(response.getBody().errors()).isEmpty();
    }

    @Test
    void badRequestExceptionHidesDetailMessageOutsideLocalProfile() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("name", "String");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

        var response = handler.handleBadRequestException(exception, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("COMMON-BAD-REQUEST");
        assertThat(response.getBody().message()).isEqualTo("요청 형식이 올바르지 않습니다.");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
        assertThat(response.getBody().errors()).isEmpty();
    }

    @Test
    void unexpectedExceptionHidesDetailMessageOutsideLocalProfile() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

        var response = handler.handleException(new RuntimeException("database password leaked"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().message()).isEqualTo("서버 오류가 발생했습니다.");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
        assertThat(response.getBody().errors()).isEmpty();
    }

    private record TestRequest(String name) {
    }

    private static class TestController {
        @SuppressWarnings("unused")
        void create(TestRequest request) {
        }
    }

    private enum TestErrorCode implements ErrorCode {
        TEST_NOT_FOUND("TEST-404", "테스트 리소스를 찾을 수 없습니다.");

        private final String code;
        private final String message;

        TestErrorCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
