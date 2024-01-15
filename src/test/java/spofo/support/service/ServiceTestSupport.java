package spofo.support.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import spofo.holdingstock.service.HoldingStockService;
import spofo.holdingstock.service.port.HoldingStockRepository;
import spofo.portfolio.service.PortfolioService;
import spofo.portfolio.service.port.PortfolioRepository;
import spofo.stock.service.StockServerService;
import spofo.support.annotation.CustomServiceTest;
import spofo.tradelog.service.TradeLogService;
import spofo.tradelog.service.port.TradeLogRepository;

/**
 * Repository를 @SpyBean으로 선언한 이유는 1:N의 관계를 테스트 할 때
 * 양쪽 다 저장 후, 1쪽에서 N의 객체를 조회하려 할 때 1은 외래키의 주인이 아니므로
 * 같은 트랜잭션 내에서 조회가 불가능한 이슈가 있으므로
 * 필요 시 Mocking 할 수도 있고, 실제 로직을 사용할 수도 있는 @SpyBean으로 선언하였다.
 */
@CustomServiceTest
public abstract class ServiceTestSupport {

    @Autowired
    protected PortfolioService portfolioService;

    @Autowired
    protected HoldingStockService holdingStockService;

    @Autowired
    protected TradeLogService tradeLogService;

    @SpyBean
    protected PortfolioRepository portfolioRepository;

    @SpyBean
    protected HoldingStockRepository holdingStockRepository;

    @SpyBean
    protected TradeLogRepository tradeLogRepository;

    @MockBean
    protected StockServerService mockStockServerService;
}
