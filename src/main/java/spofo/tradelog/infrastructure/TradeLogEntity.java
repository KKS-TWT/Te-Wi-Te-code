package spofo.tradelog.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import spofo.global.component.converter.TradeTypeConverter;
import spofo.holdingstock.infrastructure.HoldingStockEntity;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.enums.TradeType;

@Entity
@Getter
@Table(name = "trade_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Convert(converter = TradeTypeConverter.class)
    private TradeType type;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(updatable = false, nullable = false)
    private LocalDateTime tradeDate;

    @Column(precision = 30, scale = 15, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal marketPrice;

    @Column(updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "holding_stock_id")
    private HoldingStockEntity holdingStockEntity;

    public static TradeLogEntity from(TradeLog tradeLog) {
        if (tradeLog == null) {
            return new TradeLogEntity();
        }
        
        TradeLogEntity entity = new TradeLogEntity();

        entity.id = tradeLog.getId();
        entity.type = tradeLog.getType();
        entity.price = tradeLog.getPrice();
        entity.tradeDate = tradeLog.getTradeDate();
        entity.quantity = tradeLog.getQuantity();
        entity.marketPrice = tradeLog.getMarketPrice();
        entity.createdAt = tradeLog.getCreatedAt();
        entity.holdingStockEntity = HoldingStockEntity.from(tradeLog.getHoldingStock());

        return entity;
    }

    public TradeLog toModel() {
        return TradeLog.builder()
                .id(id)
                .type(type)
                .price(price)
                .tradeDate(tradeDate)
                .quantity(quantity)
                .marketPrice(marketPrice)
                .createdAt(createdAt)
                .build();
    }

}
