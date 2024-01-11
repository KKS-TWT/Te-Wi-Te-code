package spofo.portfolio.domain;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static spofo.global.component.utils.CommonUtils.format;
import static spofo.global.component.utils.CommonUtils.getGlobalScale;
import static spofo.global.component.utils.CommonUtils.isZero;
import static spofo.global.component.utils.CommonUtils.toPercent;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import spofo.holdingstock.domain.HoldingStock;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.TradeLog;

@Getter
@Builder
public class PortfolioStatistic {

    private Portfolio portfolio;
    private BigDecimal totalAsset; // 각 종목별 현재가 * 수량의 합
    private BigDecimal totalBuy; // 각 종목별 매수가 * 수량의 합
    private BigDecimal totalGain; // 총 자산 - 총 매수가
    private BigDecimal gainRate; // 총 수익 / 총 자산

    public static PortfolioStatistic of(Portfolio portfolio, Map<String, Stock> stocks) {
        BigDecimal totalAsset = ZERO;
        BigDecimal totalBuy = ZERO;
        BigDecimal totalGain = ZERO;
        BigDecimal gainRate = ZERO;

        if (portfolio.getHoldingStocks() != null) {
            for (HoldingStock holdingStock : portfolio.getHoldingStocks()) {
                Stock stock = stocks.get(holdingStock.getStockCode());
                BigDecimal currentPrice = stock.getPrice();

                for (TradeLog tradeLog : holdingStock.getTradeLogs()) {
                    if (tradeLog.getType() == BUY) {
                        BigDecimal price = tradeLog.getPrice();
                        BigDecimal quantity = tradeLog.getQuantity();

                        totalAsset = totalAsset.add(currentPrice.multiply(quantity));
                        totalBuy = totalBuy.add(price.multiply(quantity));
                    }
                }
            }

            totalGain = totalAsset.subtract(totalBuy);

            if (!isZero(totalBuy)) {
                gainRate = toPercent(totalGain.divide(totalBuy, getGlobalScale(), HALF_UP));
            }
        }

        return PortfolioStatistic.builder()
                .portfolio(portfolio)
                .totalAsset(format(totalAsset))
                .totalBuy(format(totalBuy))
                .totalGain(format(totalGain))
                .gainRate(format(gainRate))
                .build();
    }

}
