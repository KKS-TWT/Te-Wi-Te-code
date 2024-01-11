package spofo.global.domain.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PORTFOLIO_NOT_FOUND(BAD_REQUEST, "포트폴리오를 찾을 수 없습니다."),
    MEMBER_NOT_VALID(UNAUTHORIZED, "인증된 사용자가 아닙니다."),
    FIELD_NOT_VALID(BAD_REQUEST, "인증된 사용자가 아닙니다."),
    HOLDING_STOCK_NOT_FOUND(BAD_REQUEST, "보유종목을 찾을 수 없습니다."),

    /*
    오류 코드 예시입니다
    DUPLICATED_ID(HttpStatus.BAD_REQUEST, "중복된 회원 아이디 입니다.");
     */;
    private final HttpStatus status;
    private final String message;
}
