package spofo.auth.service;

import java.util.Optional;

public interface AuthServerService {

    Optional<Long> verify(String idToken);
}
