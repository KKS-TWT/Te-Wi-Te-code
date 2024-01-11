package spofo.portfolio.controller.response;

import lombok.Builder;
import lombok.Data;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.IncludeType;
import spofo.portfolio.domain.enums.PortfolioType;

@Data
@Builder
public class PortfolioResponse {

    private final Long id;
    private final Long memberId;
    private final String name;
    private final String description;
    private final Currency currency;
    private final IncludeType includeType;
    private final PortfolioType type;

    public static PortfolioResponse from(Portfolio portfolio) {
        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .memberId(portfolio.getMemberId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .currency(portfolio.getCurrency())
                .includeType(portfolio.getIncludeType())
                .type(portfolio.getType())
                .build();
    }
}
