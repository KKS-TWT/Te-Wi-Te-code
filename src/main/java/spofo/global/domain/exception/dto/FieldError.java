package spofo.global.domain.exception.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FieldError {

    private final String field;
    private final String errorMessage;

    @Builder
    public FieldError(String field, String errorMessage) {
        this.field = field;
        this.errorMessage = errorMessage;
    }
}