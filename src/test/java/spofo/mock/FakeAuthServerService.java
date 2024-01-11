package spofo.mock;

import java.util.Optional;
import spofo.auth.service.AuthServerService;

public class FakeAuthServerService implements AuthServerService {

    @Override
    public Optional<Long> verify(String idToken) {
        return Optional.of(1L);
    }
}
