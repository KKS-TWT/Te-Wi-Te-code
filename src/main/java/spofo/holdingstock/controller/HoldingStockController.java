package spofo.holdingstock.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spofo.holdingstock.controller.request.HoldingStockRequest;
import spofo.holdingstock.controller.response.HoldingStockResponse;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockCreate;
import spofo.holdingstock.domain.HoldingStockStatistic;
import spofo.holdingstock.service.HoldingStockService;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.service.PortfolioService;
import spofo.tradelog.domain.TradeLogCreate;
import spofo.tradelog.service.TradeLogService;

@RestController
@RequiredArgsConstructor
public class HoldingStockController {

    private final HoldingStockService holdingStockService;
    private final PortfolioService portfolioService;
    private final TradeLogService tradeLogService;

    @GetMapping("/portfolios/{portfolioId}/stocks")
    public ResponseEntity<List<HoldingStockResponse>> getHoldingStocks(
            @PathVariable Long portfolioId) {
        List<HoldingStockResponse> response =
                holdingStockService.getHoldingStockStatistics(portfolioId).stream()
                        .map(HoldingStockResponse::from)
                        .toList();

        return ok(response);
    }

    @GetMapping("/portfolios/{portfolioId}/stocks/{stockId}")
    public ResponseEntity<HoldingStockResponse> getStatistic(
            @PathVariable("portfolioId") Long portfolioId,
            @PathVariable("stockId") Long stockId) {
        HoldingStockStatistic statistic = holdingStockService.getStatistic(stockId);
        return ok(HoldingStockResponse.from(statistic));
    }

    @PutMapping("/portfolios/{portfolioId}/stocks")
    public ResponseEntity<Map<String, Long>> create(
            @RequestBody @Valid HoldingStockRequest request, @PathVariable Long portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolio(portfolioId);
        HoldingStockCreate holdingStockCreate = request.toHoldingStockCreate();
        TradeLogCreate tradeLogCreate = request.toTradeLogCreate();
        HoldingStock savedHoldingStock = holdingStockService.get(portfolio, request.getCode());

        if (savedHoldingStock == null) {
            holdingStockService.create(holdingStockCreate, tradeLogCreate, portfolio);
        } else {
            tradeLogService.create(tradeLogCreate, savedHoldingStock);
        }

        Map<String, Long> response = Map.of("id", portfolioId);

        return created(URI.create("/portfolios/" + portfolioId))
                .body(response);
    }

    @PostMapping("/portfolios/{portfolioId}/stocks/{stockId}")
    public ResponseEntity<Map<String, Long>> addTradeLog(
            @RequestBody @Valid HoldingStockRequest request,
            @PathVariable Long portfolioId,
            @PathVariable Long stockId) {
        HoldingStock holdingStock = holdingStockService.get(stockId);
        TradeLogCreate tradeLogCreate = request.toTradeLogCreate();
        tradeLogService.create(tradeLogCreate, holdingStock);

        Map<String, Long> response = Map.of("id", stockId);

        return created(URI.create("/portfolios/" + portfolioId + "/stocks/" + stockId))
                .body(response);
    }

    @DeleteMapping("/portfolios/{portfolioId}/stocks/{stockId}")
    public ResponseEntity<Void> delete(
            @PathVariable("portfolioId") Long portfolioId,
            @PathVariable("stockId") Long stockId) {
        holdingStockService.delete(stockId);
        return ok().build();
    }
}
