package spofo.global.domain.exception;

import static spofo.global.domain.exception.ErrorCode.HOLDING_STOCK_NOT_FOUND;

public class HoldingStockNotFound extends PortfolioException {

    public HoldingStockNotFound() {
        super(HOLDING_STOCK_NOT_FOUND);
    }
}
