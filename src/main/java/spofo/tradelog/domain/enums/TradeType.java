package spofo.tradelog.domain.enums;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeType {

    BUY(1), SELL(2);

    private final int dbData;

    private static final Map<Integer, TradeType> map = Stream.of(TradeType.values())
            .collect(toMap(
                    TradeType::getDbData,
                    Function.identity(),
                    (oldValue, newValue) -> newValue,
                    HashMap::new)
            );

    public static TradeType findByDbValue(int dbValue) {
        return map.getOrDefault(dbValue, BUY);
    }
}
