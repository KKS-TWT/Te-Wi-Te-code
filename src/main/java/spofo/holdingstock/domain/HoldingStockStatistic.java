package spofo.holdingstock.domain;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static spofo.global.component.utils.CommonUtils.format;
import static spofo.global.component.utils.CommonUtils.getGlobalScale;
import static spofo.global.component.utils.CommonUtils.isZero;
import static spofo.global.component.utils.CommonUtils.toPercent;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Builder;
import lombok.Getter;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.TradeLog;

@Getter
@Builder
public class HoldingStockStatistic {

    private HoldingStockInfo holdingStockInfo;
    private BigDecimal totalAsset;
    private BigDecimal gain;
    private BigDecimal gainRate;
    private BigDecimal avgPrice;
    private BigDecimal currentPrice;
    private BigDecimal quantity;

    public static HoldingStockStatistic of(HoldingStock holdingStock, Stock stock) {
        BigDecimal currentPrice = stock.getPrice();

        BigDecimal totalAsset = ZERO;
        BigDecimal gain = ZERO;
        BigDecimal gainRate = ZERO;
        BigDecimal avgPrice = ZERO;
        BigDecimal quantity = ZERO;
        BigDecimal totalPrice = ZERO;

        if (holdingStock.getTradeLogs() != null) {
            for (TradeLog tradeLog : holdingStock.getTradeLogs()) {
                if (tradeLog.getType() == BUY) {
                    quantity = quantity.add(tradeLog.getQuantity()); // 보유 종목 수량 = 매매 이력 총 수량 다 더한 것
                    totalAsset = totalAsset.add(currentPrice.multiply(
                            tradeLog.getQuantity())); // 총 자산가치 = 매매이력의 수량 * 현재가 를 다 더한 것
                    totalPrice = totalPrice.add(
                            tradeLog.getPrice().multiply(tradeLog.getQuantity())); // 총 구매가
                }
            }

            if (!isZero(quantity)) {
                avgPrice = totalPrice.divide(quantity, getGlobalScale(), HALF_UP); // 평균 구매 단가
            }
            gain = totalAsset.subtract(totalPrice); // 수익 = 자산 가치 - 총 구매가

            if (!isZero(totalPrice)) {
                gainRate = toPercent(gain.divide(totalPrice, 5, RoundingMode.FLOOR)).setScale(
                        getGlobalScale(), HALF_UP); // 수익률 = 수익/평균단가 * 100
            }
        }

        return HoldingStockStatistic.builder()
                .holdingStockInfo(HoldingStockInfo.of(holdingStock, stock))
                .totalAsset(format(totalAsset))
                .gain(format(gain))
                .gainRate(format(gainRate))
                .avgPrice(format(avgPrice))
                .currentPrice(format(currentPrice))
                .quantity(format(quantity))
                .build();
    }
}
