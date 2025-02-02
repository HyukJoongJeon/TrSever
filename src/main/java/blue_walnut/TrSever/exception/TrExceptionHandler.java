package blue_walnut.TrSever.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class TrExceptionHandler extends ResponseEntityExceptionHandler {
        @ExceptionHandler(ParamValidateException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ParamValidateException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    // 재시도 가능 에러
    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorResponse> handleTokenRegistryException(RetryableException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 토큰 예외 처리
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorResponse> handleTokenRegistryException(TokenException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 결제 예외 처리
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentProcessingException(PaymentException ex) {
        return buildErrorResponse(ex, HttpStatus.PAYMENT_REQUIRED);
    }

    // 그 외의 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        return buildErrorResponse(new RuntimeException("알 수 없는 오류가 발생했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 에러 응답 객체 생성
    private ResponseEntity<ErrorResponse> buildErrorResponse(RuntimeException ex, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), getErrorCode(ex));
        log.error("Error occurred: {} - {}", errorResponse.getCode(), errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    // 예외 객체에서 ErrorCode 추출
    private String getErrorCode(RuntimeException ex) {
        if (ex instanceof TokenException) {
            return ((TokenException) ex).getCode();
        } else if (ex instanceof PaymentException) {
            return ((PaymentException) ex).getCode();
        } else if (ex instanceof RetryableException) {
            return ((RetryableException) ex).getCode();
        } else if (ex instanceof ParamValidateException) {
            return ((ParamValidateException) ex).getCode();
        }
        return "9999"; // 기본 시스템 오류 코드
    }

    // 에러 응답 구조
    public static class ErrorResponse {
        private String message;
        private String code;

        public ErrorResponse(String message, String code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }

    }
}
