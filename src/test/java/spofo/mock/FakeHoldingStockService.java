package spofo.mock;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import spofo.global.domain.exception.HoldingStockNotFound;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockCreate;
import spofo.holdingstock.domain.HoldingStockStatistic;
import spofo.holdingstock.service.port.HoldingStockRepository;
import spofo.portfolio.domain.Portfolio;
import spofo.tradelog.domain.TradeLogCreate;

@RequiredArgsConstructor
public class FakeHoldingStockService {

    private final HoldingStockRepository holdingStockRepository;

    public List<HoldingStock> getByPortfolioId(Long portfolioId) {
        return holdingStockRepository.findByPortfolioId(portfolioId);
    }

    public HoldingStockStatistic getStatistic(Long id) {
        return null;
    }

    public HoldingStock get(Long id) {
        return findById(id);
    }

    public HoldingStock get(Portfolio portfolio, String stockCode) {
        return holdingStockRepository.findByStockCode(portfolio, stockCode)
                .orElse(null);
    }

    public HoldingStock create(HoldingStockCreate holdingStockCreate, TradeLogCreate tradeLogCreate,
            Portfolio portfolio) {
        return null;
    }

    public void delete(Long id) {
    }

    public void deleteByPortfolioId(Long id) {
        holdingStockRepository.deleteByPortfolioId(id);
    }

    public List<HoldingStockStatistic> getHoldingStockStatistics(Long portfolioId) {
        return null;
    }

    private HoldingStock findById(Long id) {
        return getFrom(holdingStockRepository.findById(id));
    }

    private HoldingStock getFrom(Optional<HoldingStock> holdingStockOptional) {
        return holdingStockOptional.orElseThrow(HoldingStockNotFound::new);
    }
}
