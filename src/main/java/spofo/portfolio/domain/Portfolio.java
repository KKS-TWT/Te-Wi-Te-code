package spofo.portfolio.domain;

import static spofo.portfolio.domain.enums.IncludeType.Y;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import spofo.holdingstock.domain.HoldingStock;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.IncludeType;
import spofo.portfolio.domain.enums.PortfolioType;

@Getter
@Builder
public class Portfolio {

    private final Long id;
    private final Long memberId;
    private final String name;
    private final String description;
    private final Currency currency;
    private final IncludeType includeType;
    private final PortfolioType type;
    private final List<HoldingStock> holdingStocks;

    public static Portfolio of(PortfolioCreate create, Long memberId) {
        return Portfolio.builder()
                .name(create.getName())
                .description(create.getDescription())
                .currency(create.getCurrency())
                .includeType(Y)
                .type(create.getType())
                .memberId(memberId)
                .build();
    }

    public Portfolio update(PortfolioUpdate update, Long memberId) {
        return Portfolio.builder()
                .id(update.getId())
                .name(update.getName())
                .description(update.getDescription())
                .currency(update.getCurrency())
                .includeType(update.getIncludeType())
                .type(update.getType())
                .memberId(memberId)
                .build();
    }
}
