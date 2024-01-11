package spofo.portfolio.controller;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spofo.auth.domain.MemberInfo;
import spofo.auth.domain.annotation.LoginMember;
import spofo.portfolio.controller.port.PortfolioService;
import spofo.portfolio.controller.request.PortfolioRequest;
import spofo.portfolio.controller.request.PortfolioSearchCondition;
import spofo.portfolio.controller.response.PortfolioResponse;
import spofo.portfolio.controller.response.PortfolioStatisticResponse;
import spofo.portfolio.controller.response.PortfoliosStatisticResponse;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.portfolio.domain.PortfolioUpdate;
import spofo.portfolio.domain.TotalPortfoliosStatistic;

@RestController
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/portfolios/total")
    public ResponseEntity<PortfoliosStatisticResponse> getPortfoliosStatistic(
            @ModelAttribute PortfolioSearchCondition condition,
            @LoginMember MemberInfo memberInfo) {
        TotalPortfoliosStatistic statistic
                = portfolioService.getPortfoliosStatistic(memberInfo.getId(), condition);

        return ok(PortfoliosStatisticResponse.from(statistic));
    }

    @GetMapping("/portfolios")
    public ResponseEntity<List<PortfolioStatisticResponse>> getPortfolioSimple(
            @ModelAttribute PortfolioSearchCondition condition,
            @LoginMember MemberInfo memberInfo) {
        List<PortfolioStatisticResponse> portfolios
                = portfolioService.getPortfolios(memberInfo.getId(), condition)
                .stream()
                .map(PortfolioStatisticResponse::from)
                .toList();

        return ok(portfolios);
    }

    @GetMapping("/portfolios/{portfolioId}")
    public ResponseEntity<PortfolioResponse> getPortfolio(
            @PathVariable Long portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolio(portfolioId);

        return ok(PortfolioResponse.from(portfolio));
    }

    @GetMapping("/portfolios/{portfolioId}/total")
    public ResponseEntity<PortfolioStatisticResponse> getPortfolioStatistic(
            @PathVariable Long portfolioId) {
        PortfolioStatistic portfolio = portfolioService.getPortfolioStatistic(portfolioId);

        return ok(PortfolioStatisticResponse.from(portfolio));
    }

    @PostMapping("/portfolios")
    public ResponseEntity<Map<String, Long>> create(@RequestBody @Valid PortfolioRequest request,
            @LoginMember MemberInfo memberInfo) {
        PortfolioCreate portfolioCreate = request.toPortfolioCreate();
        Portfolio portfolio = portfolioService.create(portfolioCreate, memberInfo.getId());
        Long portfolioId = portfolio.getId();

        Map<String, Long> response = Map.of("id", portfolioId);

        return created(URI.create("/portfolios/" + portfolioId))
                .body(response);
    }

    @PutMapping("/portfolios/{portfolioId}")
    public ResponseEntity<Void> update(
            @PathVariable Long portfolioId,
            @RequestBody @Valid PortfolioUpdate request,
            @LoginMember MemberInfo memberInfo) {
        portfolioService.update(request, portfolioId, memberInfo.getId());
        return ok().build();
    }

    @DeleteMapping("/portfolios/{portfolioId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long portfolioId) {
        portfolioService.delete(portfolioId);
        return ok().build();
    }
}
