package spofo.small.portfolio.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static spofo.portfolio.domain.enums.Currency.KRW;
import static spofo.portfolio.domain.enums.IncludeType.N;
import static spofo.portfolio.domain.enums.PortfolioType.FAKE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.PortfolioUpdate;

public class PortfolioTest {

    @Test
    @DisplayName("PortfolioCreate로 포트폴리오 도메인을 생성한다.")
    void portfolioCreateToPortfolio() {
        //given
        PortfolioCreate portfolioCreate = PortfolioCreate.builder()
                .name("모의포트폴리오")
                .description("모의포트폴리오입니다")
                .currency(KRW)
                .type(FAKE)
                .build();

        //when
        Portfolio portfolio = Portfolio.of(portfolioCreate, 1L);

        //then
        assertThat(portfolio.getName()).isEqualTo(portfolioCreate.getName());
        assertThat(portfolio.getCurrency()).isEqualTo(portfolioCreate.getCurrency());
        assertThat(portfolio.getDescription()).isEqualTo(portfolioCreate.getDescription());
        assertThat(portfolio.getType()).isEqualTo(portfolioCreate.getType());
        assertThat(portfolio.getId()).isNull();
    }

    @Test
    @DisplayName("PortfolioUpdate로 포트폴리오 도메인을 생성한다.")
    void updatePortfolioToPortfolio() {
        //given
        Portfolio portfolio = Portfolio.builder().build();

        PortfolioUpdate portfolioUpdate = PortfolioUpdate.builder()
                .id(1L)
                .name("업데이트 포트폴리오")
                .description("업데이트 된 포트폴리오입니다")
                .currency(KRW)
                .includeType(N)
                .type(FAKE)
                .build();

        //when
        Portfolio updatedPortfolio = portfolio.update(portfolioUpdate, 1L);

        //then
        assertThat(updatedPortfolio.getId()).isEqualTo(1L);
        assertThat(updatedPortfolio.getName()).isEqualTo("업데이트 포트폴리오");
        assertThat(updatedPortfolio.getDescription()).isEqualTo("업데이트 된 포트폴리오입니다");
        assertThat(updatedPortfolio.getCurrency()).isEqualTo(portfolioUpdate.getCurrency());
        assertThat(updatedPortfolio.getIncludeType()).isEqualTo(portfolioUpdate.getIncludeType());
        assertThat(updatedPortfolio.getType()).isEqualTo(portfolioUpdate.getType());
    }
}
