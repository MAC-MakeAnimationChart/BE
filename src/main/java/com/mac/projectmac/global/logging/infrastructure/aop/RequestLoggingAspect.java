package com.mac.projectmac.global.logging.infrastructure.aop;

import com.mac.projectmac.global.logging.config.LoggingProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestLoggingAspect {

    private final LoggingProperties loggingProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Around("execution(* com.mac.projectmac.*..*Controller.*(..))")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentRequest();

        if (request == null || isExcluded(request.getRequestURI())) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        logRequestInfo(request, joinPoint);

        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[RequestLoggingAspect] {} {} 완료 - 처리시간={}ms", method, uri, elapsed);
        }
    }

    private void logRequestInfo(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String contentType = request.getContentType() != null ? request.getContentType() : "";

        if (contentType.contains("multipart/form-data")) {
            logMultipartRequest(request, method, uri);
        } else {
            String params = queryString != null ? "?" + queryString : "";
            String body = extractBody(joinPoint.getArgs());
            log.info("[RequestLoggingAspect] {} {}{} body={}", method, uri, params, body);
        }
    }

    private void logMultipartRequest(HttpServletRequest request, String method, String uri) {
        if (request instanceof MultipartHttpServletRequest multipartRequest) {
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            fileMap.forEach((name, file) ->
                    log.info("[RequestLoggingAspect] {} {} multipart - name={}, originalFilename={}, size={}bytes, contentType={}",
                            method, uri, name, file.getOriginalFilename(), file.getSize(), file.getContentType())
            );
        } else {
            log.info("[RequestLoggingAspect] {} {} multipart 요청 (파일 정보 없음)", method, uri);
        }
    }

    private String extractBody(Object[] args) {
        if (args == null || args.length == 0) return "없음";

        String body = Arrays.stream(args)
                .filter(arg -> arg != null && !(arg instanceof MultipartFile))
                .map(Object::toString)
                .reduce("", (a, b) -> a + b);

        int maxLength = loggingProperties.getBodyMaxLength();
        if (body.length() > maxLength) {
            return body.substring(0, maxLength) + " [truncated...]";
        }
        return body;
    }

    private boolean isExcluded(String uri) {
        List<String> excludeUrls = loggingProperties.getExcludeUrls();
        return excludeUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
