package spofo.portfolio.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.IncludeType;
import spofo.portfolio.domain.enums.PortfolioType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioUpdate {

    @NotNull(message = "포트폴리오 아이디는 필수 입력입니다.")
    private Long id;

    @NotBlank(message = "포트폴리오 이름은 필수 입력입니다.")
    private String name;

    private String description;

    @NotNull(message = "포트폴리오 통화는 필수 입력입니다.")
    private Currency currency;

    @NotNull(message = "포트폴리오 포함여부는 필수 입력입니다.")
    private IncludeType includeType;

    @NotNull(message = "포트폴리오 타입은 필수 입력입니다.")
    private PortfolioType type;

}
