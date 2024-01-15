package spofo.support.service;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import spofo.holdingstock.service.HoldingStockService;
import spofo.holdingstock.service.port.HoldingStockRepository;
import spofo.portfolio.service.PortfolioService;
import spofo.portfolio.service.port.PortfolioRepository;
import spofo.stock.service.StockServerService;
import spofo.support.annotation.CustomServiceIntegrationTest;
import spofo.tradelog.service.TradeLogService;
import spofo.tradelog.service.port.TradeLogRepository;

/**
 * ServiceIntegrationTest에서는 테스트에 @Transactional을 붙이지 않고
 * tearDown하는 메서드를 추가해줌으로써 보다 실제와 비슷한 환경에서 테스트한다.
 * 그리고 외부 의존성 (StockServer)를 제외하고 mocking 하지 않는다.
 */
@CustomServiceIntegrationTest
public abstract class ServiceTestSupport {

    @Autowired
    protected PortfolioService portfolioService;

    @Autowired
    protected PortfolioRepository portfolioRepository;

    @Autowired
    protected HoldingStockService holdingStockService;

    @Autowired
    protected HoldingStockRepository holdingStockRepository;

    @Autowired
    protected TradeLogService tradeLogService;

    @Autowired
    protected TradeLogRepository tradeLogRepository;

    @MockBean
    protected StockServerService mockStockServerService;

    @AfterEach
    public void tearDown() {
        tradeLogRepository.deleteAll();
        holdingStockRepository.deleteAll();
        portfolioRepository.deleteAll();
    }
}
