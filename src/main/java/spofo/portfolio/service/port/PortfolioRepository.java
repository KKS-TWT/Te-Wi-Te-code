package spofo.portfolio.service.port;

import java.util.List;
import java.util.Optional;
import spofo.portfolio.domain.Portfolio;

public interface PortfolioRepository {

    List<Portfolio> findByMemberId(Long id);

    Optional<Portfolio> findById(Long id);

    Optional<Portfolio> findByIdWithTradeLogs(Long id);

    List<Portfolio> findByMemberIdWithTradeLogs(Long id);

    Portfolio save(Portfolio portfolio);

    void delete(Portfolio portfolio);

    void deleteAll();
}
