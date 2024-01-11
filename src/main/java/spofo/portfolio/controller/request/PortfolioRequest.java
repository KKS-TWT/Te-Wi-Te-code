package spofo.portfolio.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.PortfolioType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRequest {

    @NotBlank(message = "포트폴리오 이름은 필수 입력입니다.")
    private String name;

    private String description;

    @NotNull(message = "포트폴리오 통화는 필수 입력입니다.")
    private Currency currency;

    @NotNull(message = "포트폴리오 타입은 필수 입력입니다.")
    private PortfolioType type;

    public PortfolioCreate toPortfolioCreate() {
        return PortfolioCreate.builder()
                .name(name)
                .description(description)
                .currency(currency)
                .type(type)
                .build();
    }
}
