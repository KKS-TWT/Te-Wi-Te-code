package spofo.portfolio.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

public enum IncludeType {
    Y, N;

    @JsonCreator
    public static IncludeType from(String s) {
        return Objects.equals(s.toLowerCase(), "true") ? IncludeType.Y : IncludeType.N;
    }

    @JsonValue
    public boolean to() {
        return Objects.equals(this, Y);
    }
}
