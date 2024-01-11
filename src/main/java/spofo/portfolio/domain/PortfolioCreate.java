package spofo.portfolio.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.PortfolioType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioCreate {

    private String name;
    private String description;
    private Currency currency;
    private PortfolioType type;

}
