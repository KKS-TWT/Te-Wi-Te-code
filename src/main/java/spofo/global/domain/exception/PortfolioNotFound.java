package spofo.global.domain.exception;

import static spofo.global.domain.exception.ErrorCode.PORTFOLIO_NOT_FOUND;

public class PortfolioNotFound extends PortfolioException {

    public PortfolioNotFound() {
        super(PORTFOLIO_NOT_FOUND);
    }
}
