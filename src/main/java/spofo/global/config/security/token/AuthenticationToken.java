package spofo.global.config.security.token;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class AuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;

    @Builder
    public AuthenticationToken(Object principal,
            Collection<? extends GrantedAuthority> authorities) {
        // 인증이 된 후에 사용되는 토큰 (권한정보가 있음)
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }


    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
