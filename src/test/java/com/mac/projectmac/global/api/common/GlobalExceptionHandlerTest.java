package com.mac.projectmac.global.api.common;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    private record TestRequest(String name) {
    }

    private static class TestController {
        @SuppressWarnings("unused")
        void create(TestRequest request) {
        }
    }
}
