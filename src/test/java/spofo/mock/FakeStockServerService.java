package spofo.mock;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import spofo.stock.domain.Stock;
import spofo.stock.service.StockServerService;

public class FakeStockServerService implements StockServerService {

    private List<Stock> data = new ArrayList<>();

    public Stock save(Stock stock) {
        data.removeIf(item -> Objects.equals(item.getCode(), stock.getCode()));
        data.add(stock);

        return stock;
    }

    @Override
    public Stock getStock(String stockCode) {
        return data.stream()
                .filter(stock -> stock.getCode().equals(stockCode))
                .findAny()
                .orElse(null);
    }

    @Override
    public Map<String, Stock> getStocks(List<String> stockCodes) {
        return data.stream()
                .collect(toMap(Stock::getCode, identity(), (s1, s2) -> s1));
    }
}
