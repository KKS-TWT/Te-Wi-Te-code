package spofo.portfolio.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spofo.global.domain.exception.PortfolioNotFound;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.service.HoldingStockService;
import spofo.portfolio.controller.request.PortfolioSearchCondition;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.portfolio.domain.PortfolioUpdate;
import spofo.portfolio.domain.TotalPortfoliosStatistic;
import spofo.portfolio.service.port.PortfolioRepository;
import spofo.stock.domain.Stock;
import spofo.stock.service.StockServerService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingStockService holdingStockService;
    private final StockServerService stockServerService;

    public TotalPortfoliosStatistic getPortfoliosStatistic(Long memberId,
            PortfolioSearchCondition condition) {
        List<Portfolio> portfolios = portfolioRepository.findByMemberIdWithTradeLogs(memberId);
        List<PortfolioStatistic> portfolioStatistics =
                getPortfolioStatistics(filter(portfolios, condition));
        return TotalPortfoliosStatistic.of(portfolioStatistics);
    }

    public List<PortfolioStatistic> getPortfolios(Long memberId,
            PortfolioSearchCondition condition) {
        List<Portfolio> portfolios =
                filter(portfolioRepository.findByMemberIdWithTradeLogs(memberId), condition);

        return getPortfolioStatistics(portfolios);
    }

    public Portfolio getPortfolio(Long id) {
        return findById(id);
    }

    public PortfolioStatistic getPortfolioStatistic(Long id) {
        Portfolio portfolio = getPortfolioFrom(portfolioRepository.findByIdWithTradeLogs(id));
        return getPortfolioStatistics(List.of(portfolio)).get(0);
    }

    @Transactional
    public Portfolio create(PortfolioCreate request, Long memberId) {
        Portfolio portfolio = Portfolio.of(request, memberId);
        return portfolioRepository.save(portfolio);
    }

    @Transactional
    public Portfolio update(PortfolioUpdate request, Long id, Long memberId) {
        Portfolio savedPortfolio = findById(id);
        Portfolio updatedPortfolio = savedPortfolio.update(request, memberId);
        return portfolioRepository.save(updatedPortfolio);
    }

    @Transactional
    public void delete(Long id) {
        Portfolio portfolio = findById(id);
        holdingStockService.deleteByPortfolioId(id);
        portfolioRepository.delete(portfolio);
    }

    private Portfolio findById(Long id) {
        return getPortfolioFrom(portfolioRepository.findById(id));
    }

    private Portfolio getPortfolioFrom(Optional<Portfolio> portfolioOptional) {
        return portfolioOptional.orElseThrow(PortfolioNotFound::new);
    }

    private List<PortfolioStatistic> getPortfolioStatistics(List<Portfolio> portfolios) {
        List<String> stockCodes = getStockCodes(portfolios);
        Map<String, Stock> stocks = stockServerService.getStocks(stockCodes);
        return portfolios.stream()
                .map(portfolio -> PortfolioStatistic.of(portfolio, stocks))
                .toList();
    }

    private List<String> getStockCodes(List<Portfolio> portfolios) {
        return portfolios.stream()
                .flatMap(portfolio -> portfolio.getHoldingStocks().stream()
                        .map(HoldingStock::getStockCode))
                .distinct()
                .toList();
    }

    private Predicate<Portfolio> searchCondition(PortfolioSearchCondition condition) {
        if (condition == null) {
            return x -> true;
        }

        Predicate<Portfolio> result = x -> true;

        if (condition.getType() != null) {
            result = result.and(portfolio -> portfolio.getType().equals(condition.getType()));
        }
        return result;
    }

    private List<Portfolio> filter(List<Portfolio> portfolios,
            PortfolioSearchCondition condition) {
        return portfolios.stream()
                .filter(searchCondition(condition))
                .toList();
    }
}
