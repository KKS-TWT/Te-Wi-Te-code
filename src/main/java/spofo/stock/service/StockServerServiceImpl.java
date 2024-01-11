package spofo.stock.service;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpMethod.GET;
import static spofo.global.domain.enums.Server.STOCKSERVER;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import spofo.stock.domain.Stock;

@Service
@RequiredArgsConstructor
public class StockServerServiceImpl implements StockServerService {

    private final RestClient restClient;

    @Override
    public Stock getStock(String stockCode) {
        return restClient.get()
                .uri(STOCKSERVER.getUri("/stocks/" + stockCode))
                .retrieve()
                .body(Stock.class);
    }

    @Override
    public Map<String, Stock> getStocks(List<String> stockCodes) {
        String uri = STOCKSERVER.getUri("/stocks");

        List<Stock> stocks = restClient.method(GET)
                .uri(uri)
                .body(Map.of("stockCodeList", stockCodes))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return stocks.stream()
                .collect(toMap(Stock::getCode, stock -> stock, (s1, s2) -> s1));
    }
}
