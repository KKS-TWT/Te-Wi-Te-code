package spofo.stock.service;

import java.util.List;
import java.util.Map;
import spofo.stock.domain.Stock;

public interface StockServerService {

    // 단건 조회
    Stock getStock(String stockCode);

    // 목록조회
    Map<String, Stock> getStocks(List<String> stockCodes);
}
