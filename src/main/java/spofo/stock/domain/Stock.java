package spofo.stock.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    private String code;
    private String name;
    private BigDecimal price;
    private String market;
    private String sector;
    private String imageUrl;

}
