package cn.fh.jobdep.api;

import cn.fh.jobdep.api.vo.DepResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(Throwable.class)
    public DepResponse<Object> handleException(Throwable throwable) {
        DepResponse<Object> body = new DepResponse<>(-1, throwable.getMessage());

        log.warn("response = {}", body);
        return body;
    }
}
