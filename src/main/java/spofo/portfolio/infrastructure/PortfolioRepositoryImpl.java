package spofo.portfolio.infrastructure;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.service.port.PortfolioRepository;

@Repository
@RequiredArgsConstructor
public class PortfolioRepositoryImpl implements PortfolioRepository {

    private final PortfolioJpaRepository portfolioJpaRepository;

    @Override
    public List<Portfolio> findByMemberId(Long id) {
        return portfolioJpaRepository.findByMemberId(id).stream()
                .map(PortfolioEntity::toModel)
                .toList();
    }

    @Override
    public Optional<Portfolio> findById(Long id) {
        return portfolioJpaRepository.findById(id).map(PortfolioEntity::toModel);
    }

    @Override
    public Optional<Portfolio> findByIdWithTradeLogs(Long id) {
        return portfolioJpaRepository.findByIdWithTradeLogs(id)
                .map(PortfolioEntity::toModel);
    }

    @Override
    public List<Portfolio> findByMemberIdWithTradeLogs(Long id) {
        return portfolioJpaRepository.findByMemberIdWithTradeLogs(id).stream()
                .map(PortfolioEntity::toModel)
                .toList();
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        return portfolioJpaRepository.save(PortfolioEntity.from(portfolio)).toModel();
    }

    @Override
    public void delete(Portfolio portfolio) {
        portfolioJpaRepository.delete(PortfolioEntity.from(portfolio));
    }

    @Override
    public void deleteAll() {
        portfolioJpaRepository.deleteAll();
    }
}
