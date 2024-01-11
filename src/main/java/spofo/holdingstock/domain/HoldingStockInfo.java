package spofo.holdingstock.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import spofo.stock.domain.Stock;

@Getter
@Builder
public class HoldingStockInfo {

    private HoldingStock holdingStock;
    private String code;
    private String name;
    private BigDecimal price;
    private String market;
    private String sector;
    private String imageUrl;

    public static HoldingStockInfo of(HoldingStock holdingStock, Stock stock) {
        return HoldingStockInfo.builder()
                .holdingStock(holdingStock)
                .code(stock.getCode())
                .name(stock.getName())
                .price(stock.getPrice())
                .market(stock.getMarket())
                .sector(stock.getSector())
                .imageUrl(stock.getImageUrl())
                .build();
    }
}
