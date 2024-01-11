package spofo.small.tradelog.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.mock.FakeStockServerService;
import spofo.mock.FakeTradeLogRepository;
import spofo.stock.domain.Stock;
import spofo.tradelog.controller.port.TradeLogService;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;
import spofo.tradelog.domain.TradeLogStatistic;
import spofo.tradelog.service.TradeLogServiceImpl;

public class TradeLogServiceTest {

    private TradeLogService tradeLogService;
    private FakeStockServerService fakeStockServerService;
    private FakeTradeLogRepository fakeTradeLogRepository;

    private static final String TEST_STOCK_CODE = "101010";

    @BeforeEach
    void setup() {
        fakeTradeLogRepository = new FakeTradeLogRepository();
        fakeStockServerService = new FakeStockServerService();
        tradeLogService = new TradeLogServiceImpl(fakeTradeLogRepository, fakeStockServerService);

        Stock stock = Stock.builder()
                .code(TEST_STOCK_CODE)
                .name("삼성전자")
                .price(getBD(66000))
                .sector("반도체")
                .build();
        fakeStockServerService.save(stock);
    }

    @Test
    @DisplayName("매매이력 1건을 생성한다.")
    void holdingStockCreate() {
        // given
        TradeLogCreate tradeLogCreate = getTradeLogCreate(TEN, ONE);
        HoldingStock holdingStock = getHoldingStock();

        // when
        TradeLog savedTradeLog = tradeLogService.create(tradeLogCreate, holdingStock);

        // then
        assertThat(savedTradeLog.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("1개의 종목이력 ID로 1개 매매이력 통계를 조회한다.")
    void getTradeLogStatistics1() {
        // given
        HoldingStock holdingStock = getHoldingStock();
        Stock stock = fakeStockServerService.getStock(TEST_STOCK_CODE);
        TradeLogCreate tradeLogCreate1 = getTradeLogCreate(getBD(33_000), ONE);

        TradeLog log1 = TradeLog.of(tradeLogCreate1, holdingStock, stock);

        fakeTradeLogRepository.save(log1);

        // when
        List<TradeLogStatistic> statistics = tradeLogService.getStatistics(1L);

        // then
        assertThat(statistics.get(0).getTotalPrice()).isEqualTo(getBD(33_000));
        assertThat(statistics.get(0).getGain()).isEqualTo(getBD(33_000));
    }

    @Test
    @DisplayName("1개의 종목이력 ID로 2개 매매이력 통계를 조회한다.")
    void getTradeLogStatistics2() {
        // given
        HoldingStock holdingStock = getHoldingStock();
        Stock stock = fakeStockServerService.getStock(TEST_STOCK_CODE);
        TradeLogCreate tradeLogCreate1 = getTradeLogCreate(getBD(33_000), ONE);
        TradeLogCreate tradeLogCreate2 = getTradeLogCreate(getBD(66_000), getBD(2));

        TradeLog log1 = TradeLog.of(tradeLogCreate1, holdingStock, stock);
        TradeLog log2 = TradeLog.of(tradeLogCreate2, holdingStock, stock);

        fakeTradeLogRepository.save(log1);
        fakeTradeLogRepository.save(log2);

        // when
        List<TradeLogStatistic> statistics = tradeLogService.getStatistics(1L);

        // then
        assertThat(statistics.get(0).getTotalPrice()).isEqualTo(getBD(33_000));
        assertThat(statistics.get(0).getGain()).isEqualTo(getBD(33_000));

        assertThat(statistics.get(1).getTotalPrice()).isEqualTo(getBD(132000));
        assertThat(statistics.get(1).getGain()).isEqualTo(ZERO);
    }

    private TradeLogCreate getTradeLogCreate(BigDecimal price, BigDecimal quantity) {
        return TradeLogCreate.builder()
                .type(BUY)
                .price(price)
                .tradeDate(now())
                .quantity(quantity)
                .build();
    }

    private HoldingStock getHoldingStock() {
        return HoldingStock.builder()
                .id(1L)
                .stockCode(TEST_STOCK_CODE)
                .build();
    }
}
