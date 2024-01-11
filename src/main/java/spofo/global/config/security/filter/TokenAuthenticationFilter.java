package spofo.global.config.security.filter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import spofo.auth.domain.MemberInfo;
import spofo.auth.service.AuthServerService;
import spofo.global.config.security.token.AuthenticationToken;
import spofo.global.domain.exception.ErrorCode;
import spofo.global.domain.exception.TokenNotValid;
import spofo.global.domain.exception.dto.ErrorResult;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthServerService authServerService;
    private final ObjectMapper objectMapper;

    private static final String[] WHITE_LIST = {"/health-check"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (needsAuth(request.getRequestURI())) {
            try {
                String idToken = request.getHeader(AUTHORIZATION);

                Long memberId = authServerService.verify(idToken)
                        .orElseThrow(() -> new TokenNotValid());

                MemberInfo memberInfo = MemberInfo.builder()
                        .id(memberId)
                        .build();

                AuthenticationToken authenticationToken = AuthenticationToken.builder()
                        .principal(memberInfo)
                        .build();

                getContext().setAuthentication(authenticationToken);

                filterChain.doFilter(request, response);
            } catch (TokenNotValid ex) {
                handleTokenNotValidException(response, ex);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void handleTokenNotValidException(HttpServletResponse response,
            TokenNotValid ex) throws IOException {
        ErrorCode errorCode = ex.getErrorCode();

        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(errorCode.getStatus())
                .errorMessage(errorCode.getMessage())
                .build();

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(errorResult));
    }

    private boolean needsAuth(String requestURI) {
        return !PatternMatchUtils.simpleMatch(WHITE_LIST, requestURI);
    }
}
