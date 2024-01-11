package spofo.small.portfolio.controller.request;

import static org.assertj.core.api.Assertions.assertThat;
import static spofo.portfolio.domain.enums.Currency.KRW;
import static spofo.portfolio.domain.enums.PortfolioType.REAL;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.portfolio.controller.request.PortfolioRequest;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.PortfolioType;

public class PortfolioRequestTest {

    @Test
    @DisplayName("PortfolioRequest로 PortfolioCreate 도메인을 생성한다.")
    void portfolioRequestToPortfolioCreate() {
        // given
        String name = "포트폴리오 이름";
        String description = "포트폴리오 설명";
        Currency currency = KRW;
        PortfolioType type = REAL;

        PortfolioRequest request = PortfolioRequest.builder()
                .name(name)
                .description(description)
                .currency(currency)
                .type(type)
                .build();

        // when
        PortfolioCreate portfolioCreate = request.toPortfolioCreate();

        // then
        assertThat(portfolioCreate.getName()).isEqualTo(name);
        assertThat(portfolioCreate.getDescription()).isEqualTo(description);
        assertThat(portfolioCreate.getCurrency()).isEqualTo(currency);
        assertThat(portfolioCreate.getType()).isEqualTo(type);
    }
}
