package spofo.support.controller;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import spofo.auth.domain.MemberInfo;
import spofo.global.config.restclient.RestClientConfig;
import spofo.global.config.security.token.AuthenticationToken;
import spofo.holdingstock.controller.HoldingStockController;
import spofo.mock.FakeAuthServerService;
import spofo.portfolio.controller.PortfolioController;
import spofo.tradelog.controller.TradeLogController;

/**
 * @see ControllerTestMockBeanSupport
 */
@ActiveProfiles("test")
@WebMvcTest(controllers = {PortfolioController.class, HoldingStockController.class,
        TradeLogController.class})
@Import({FakeAuthServerService.class, RestClientConfig.class})
// 스프링 시큐리티를 사용하지 않을 때 필터 제외
@AutoConfigureMockMvc(addFilters = false)
public abstract class ControllerTestSupport extends ControllerTestMockBeanSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Controller 테스트를 할 때는 인증된 사용자임을 가정하고 테스트하므로
     * SecurityContextHolder에 회원정보를 넣어준다.
     */
    @BeforeEach
    public void setup() {
        Long testMemberId = 1L;

        MemberInfo memberInfo = MemberInfo.builder()
                .id(testMemberId)
                .build();

        AuthenticationToken authenticationToken = AuthenticationToken.builder()
                .principal(memberInfo)
                .build();

        getContext().setAuthentication(authenticationToken);
    }
}
