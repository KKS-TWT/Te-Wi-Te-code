package spofo.portfolio.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.PortfolioType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnePortfolioResponse {

    private Long id;
    private Long memberId;
    private String name;
    private String description;
    private Currency currency;
    private PortfolioType type;

    public static OnePortfolioResponse from(Portfolio portfolio) {
        return OnePortfolioResponse.builder()
                .id(portfolio.getId())
                .memberId(portfolio.getMemberId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .currency(portfolio.getCurrency())
                .type(portfolio.getType())
                .build();
    }
}
