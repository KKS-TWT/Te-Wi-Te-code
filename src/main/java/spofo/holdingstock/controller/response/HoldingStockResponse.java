package spofo.holdingstock.controller.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import spofo.holdingstock.domain.HoldingStockStatistic;

@Data
@Builder
public class HoldingStockResponse {

    private Long id;
    private String code;
    private String name;
    private String sector;
    private BigDecimal totalAsset;
    private BigDecimal gain;
    private BigDecimal gainRate;
    private BigDecimal avgPrice;
    private BigDecimal currentPrice;
    private BigDecimal quantity;
    private String imagePath;

    public static HoldingStockResponse from(HoldingStockStatistic statistic) {
        return HoldingStockResponse.builder()
                .id(statistic.getHoldingStockInfo().getHoldingStock().getId())
                .code(statistic.getHoldingStockInfo().getCode())
                .name(statistic.getHoldingStockInfo().getName())
                .sector(statistic.getHoldingStockInfo().getSector())
                .totalAsset(statistic.getTotalAsset())
                .gain(statistic.getGain())
                .gainRate(statistic.getGainRate())
                .avgPrice(statistic.getAvgPrice())
                .currentPrice(statistic.getCurrentPrice())
                .quantity(statistic.getQuantity())
                .imagePath(statistic.getHoldingStockInfo().getImageUrl())
                .build();
    }
}
