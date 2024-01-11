package spofo.holdingstock.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HoldingStockCreate {

    private String stockCode;
}
