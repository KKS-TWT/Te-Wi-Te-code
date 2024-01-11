package spofo.global.component.converter;

import static spofo.tradelog.domain.enums.TradeType.BUY;
import static spofo.tradelog.domain.enums.TradeType.findByDbValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import spofo.tradelog.domain.enums.TradeType;

@Converter
public class TradeTypeConverter implements AttributeConverter<TradeType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TradeType type) {
        return type == null ? BUY.getDbData() : type.getDbData();
    }

    @Override
    public TradeType convertToEntityAttribute(Integer dbData) {
        return findByDbValue(dbData);
    }
}
