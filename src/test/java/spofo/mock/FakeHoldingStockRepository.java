package spofo.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.service.port.HoldingStockRepository;
import spofo.portfolio.domain.Portfolio;

/**
 * FakeRepository는 Entity가 아닌 Domain을 관리한다.
 */
public class FakeHoldingStockRepository implements HoldingStockRepository {

    private long autoIncrement = 0;
    List<HoldingStock> data = new ArrayList<>();

    @Override
    public List<HoldingStock> findByPortfolioId(Long id) {
        return data.stream()
                .filter(item -> item.getPortfolio().getId().equals(id))
                .toList();
    }

    @Override
    public Optional<HoldingStock> findById(Long id) {
        return data.stream()
                .filter(item -> item.getId().equals(id))
                .findAny();
    }

    @Override
    public HoldingStock save(HoldingStock holdingStock) {
        if (holdingStock.getId() == null || holdingStock.getId() == 0) {
            HoldingStock newHoldingStock = holdingStock.builder()
                    .id(++autoIncrement)
                    .stockCode(holdingStock.getStockCode())
                    .portfolio(holdingStock.getPortfolio())
                    .build();
            data.add(newHoldingStock);
            return newHoldingStock;
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), holdingStock.getId()));
            data.add(holdingStock);
            return holdingStock;
        }
    }

    @Override
    public void delete(HoldingStock holdingStock) {
        data.removeIf(item -> Objects.equals(item.getId(), holdingStock.getId()));
    }

    @Override
    public void deleteByPortfolioId(Long id) {
        data.removeIf(item -> Objects.equals(item.getPortfolio().getId(), id));
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public Optional<HoldingStock> findByStockCode(Portfolio portfolio, String stockCode) {
        return data.stream()
                .filter(item -> item.getPortfolio().getId() == portfolio.getId())
                .filter(item -> Objects.equals(item.getStockCode(), stockCode))
                .findAny();
    }
}
