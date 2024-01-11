package spofo.global.domain.exception;

import static spofo.global.domain.exception.ErrorCode.MEMBER_NOT_VALID;

public class TokenNotValid extends PortfolioException {

    public TokenNotValid() {
        super(MEMBER_NOT_VALID);
    }
}
