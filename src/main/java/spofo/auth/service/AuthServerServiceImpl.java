package spofo.auth.service;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static spofo.global.domain.enums.Server.AUTHSERVER;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import spofo.auth.domain.MemberInfo;
import spofo.global.domain.exception.TokenNotValid;

@Service
@RequiredArgsConstructor
public class AuthServerServiceImpl implements AuthServerService {

    private final RestClient restClient;

    @Override
    public Optional<Long> verify(String idToken) {

        try {
            MemberInfo memberInfo = restClient.get()
                    .uri(AUTHSERVER.getUri("/auth/members/search"))
                    .header(AUTHORIZATION, idToken)
                    .retrieve()
                    .body(MemberInfo.class);

            return ofNullable(memberInfo.getId());
        } catch (Exception e) {
            throw new TokenNotValid();
        }
    }
}
