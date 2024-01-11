package spofo.portfolio.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import spofo.global.infrastructure.BaseEntity;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.infrastructure.HoldingStockEntity;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.IncludeType;
import spofo.portfolio.domain.enums.PortfolioType;

@Entity
@Getter
@Table(name = "portfolio")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'Y'")
    private IncludeType includeType;

    @Enumerated(EnumType.STRING)
    private PortfolioType type;

    @OneToMany(mappedBy = "portfolioEntity")
    private Set<HoldingStockEntity> holdingStockEntities = new LinkedHashSet<>();

    public static PortfolioEntity from(Portfolio portfolio) {
        if (portfolio == null) {
            return new PortfolioEntity();
        }

        PortfolioEntity entity = new PortfolioEntity();

        entity.id = portfolio.getId();
        entity.memberId = portfolio.getMemberId();
        entity.name = portfolio.getName();
        entity.description = portfolio.getDescription();
        entity.currency = portfolio.getCurrency();
        entity.includeType = portfolio.getIncludeType();
        entity.type = portfolio.getType();

        return entity;
    }

    public Portfolio toModel() {
        List<HoldingStock> holdingStocks = holdingStockEntities.stream()
                .map(HoldingStockEntity::toModel)
                .toList();

        return Portfolio.builder()
                .id(id)
                .memberId(memberId)
                .name(name)
                .description(description)
                .currency(currency)
                .includeType(includeType)
                .type(type)
                .holdingStocks(holdingStocks)
                .build();
    }
}
