package spofo.portfolio.controller.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.enums.IncludeType;
import spofo.portfolio.domain.enums.PortfolioType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSimpleResponse {

    private Long id;
    private String name;
    private PortfolioType type;
    private IncludeType includeType;
    private BigDecimal gain;
    private BigDecimal gainRate;

    public static PortfolioSimpleResponse from(Portfolio portfolio, BigDecimal gain,
            BigDecimal gainRate) {
        return PortfolioSimpleResponse.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .type(portfolio.getType())
                .includeType(portfolio.getIncludeType())
                .gain(gain)
                .gainRate(gainRate)
                .build();
    }
}
