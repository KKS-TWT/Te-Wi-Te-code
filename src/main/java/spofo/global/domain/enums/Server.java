package spofo.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Server {

    AUTHSERVER("https://auth.spofo.net"),
    STOCKSERVER("https://stock.spofo.net");

    private String url;

    public String getUri(String uri) {
        return this.url + uri;
    }
}
