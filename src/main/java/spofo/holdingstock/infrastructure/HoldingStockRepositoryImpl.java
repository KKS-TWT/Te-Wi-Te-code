package spofo.holdingstock.infrastructure;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.service.port.HoldingStockRepository;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.infrastructure.PortfolioEntity;

@Repository
@RequiredArgsConstructor
public class HoldingStockRepositoryImpl implements HoldingStockRepository {

    private final HoldingStockJpaRepository holdingStockJpaRepository;

    @Override
    public List<HoldingStock> findByPortfolioId(Long id) {
        return holdingStockJpaRepository.findByPortfolioId(id).stream()
                .map(HoldingStockEntity::toModel)
                .toList();
    }

    @Override
    public Optional<HoldingStock> findById(Long id) {
        return holdingStockJpaRepository.findById(id).map(HoldingStockEntity::toModel);
    }

    @Override
    public HoldingStock save(HoldingStock holdingStock) {
        return holdingStockJpaRepository.save(HoldingStockEntity.from(holdingStock)).toModel();
    }

    @Override
    public void delete(HoldingStock holdingStock) {
        holdingStockJpaRepository.delete(HoldingStockEntity.from(holdingStock));
    }

    @Override
    public void deleteByPortfolioId(Long id) {
        holdingStockJpaRepository.deleteByPortfolioEntityId(id);
    }

    @Override
    public void deleteAll() {
        holdingStockJpaRepository.deleteAll();
    }

    @Override
    public Optional<HoldingStock> findByStockCode(Portfolio portfolio, String stockCode) {
        PortfolioEntity portfolioEntity = PortfolioEntity.from(portfolio);
        return holdingStockJpaRepository
                .findByPortfolioEntityAndStockCode(portfolioEntity, stockCode)
                .map(HoldingStockEntity::toModel);
    }
}
