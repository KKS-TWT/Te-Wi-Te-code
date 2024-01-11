package spofo.holdingstock.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import spofo.holdingstock.infrastructure.HoldingStockEntity;

@Data
@AllArgsConstructor
@Builder
public class AddStockResponse {

    private Long portfolioId;

    public static AddStockResponse from(HoldingStockEntity holdingStockEntity) {
        return AddStockResponse.builder()
                .portfolioId(holdingStockEntity.getPortfolioEntity().getId())
                .build();
    }

}
