package spofo.global.domain.exception.advice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.status;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import spofo.global.domain.exception.ErrorCode;
import spofo.global.domain.exception.PortfolioException;
import spofo.global.domain.exception.dto.ErrorResult;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResult> portfolioExHandler(PortfolioException e) {
        ErrorCode errorCode = e.getErrorCode();

        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(errorCode.getStatus())
                .errorMessage(errorCode.getMessage())
                .build();

        return status(errorCode.getStatus()).body(errorResult);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> bindExHandler(BindException e) {
        String errorMessage = e.hasGlobalErrors() ?
                e.getBindingResult().getGlobalError().getDefaultMessage()
                : "잘못된 요청입니다.";

        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(BAD_REQUEST)
                .errorMessage(errorMessage)
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            errorResult.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return badRequest().body(errorResult);
    }
}
