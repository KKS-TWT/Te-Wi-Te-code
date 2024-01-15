package spofo.tradelog.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spofo.tradelog.controller.response.TradeLogResponse;
import spofo.tradelog.service.TradeLogService;

@RestController
@RequiredArgsConstructor
public class TradeLogController {

    private final TradeLogService tradeLogService;

    @GetMapping("/portfolios/{portfolioId}/stocks/{stockId}/trade-log")
    public ResponseEntity<List<TradeLogResponse>> getTradeLogs(
            @PathVariable Long stockId,
            @PathVariable Long portfolioId) {
        List<TradeLogResponse> statistics = tradeLogService.getStatistics(stockId)
                .stream()
                .map(TradeLogResponse::from)
                .toList();
        return ok(statistics);
    }
}
