package spofo.support.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import spofo.holdingstock.controller.port.HoldingStockService;
import spofo.portfolio.controller.port.PortfolioService;
import spofo.tradelog.controller.port.TradeLogService;

/**
 * ControllerTestMockBeanSupport를 상속받아 다양한 ControllerTestSupport를 구성할 수 있다. 예를들어 Spring Security를
 * 사용하는 ControllerTestSupport와 Spring Security를 사용하지 않는 ControllerTestSupport에서 상속받아 사용한다.
 *
 * @see ControllerTestSupport
 */
public abstract class ControllerTestMockBeanSupport {

    @MockBean
    protected PortfolioService portfolioService;

    @MockBean
    protected HoldingStockService holdingStockService;

    @MockBean
    protected TradeLogService tradeLogService;
}
