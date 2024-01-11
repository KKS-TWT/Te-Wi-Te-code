package spofo.global.domain.exception.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResult {

    private final String errorCode;
    private final String errorMessage;
    private final List<FieldError> fieldErrors = new ArrayList<>();

    @Builder
    private ErrorResult(HttpStatus errorCode, String errorMessage) {
        this.errorCode = String.valueOf(errorCode.value());
        this.errorMessage = errorMessage;
    }

    public void addValidation(String fieldName, String errorMessage) {
        FieldError fieldError = FieldError.builder()
                .field(fieldName)
                .errorMessage(errorMessage)
                .build();

        fieldErrors.add(fieldError);
    }
}
