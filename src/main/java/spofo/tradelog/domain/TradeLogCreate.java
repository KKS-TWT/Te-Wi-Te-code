package spofo.tradelog.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spofo.tradelog.domain.enums.TradeType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeLogCreate {

    private TradeType type;
    private BigDecimal price;
    private LocalDateTime tradeDate;
    private BigDecimal quantity;
}
