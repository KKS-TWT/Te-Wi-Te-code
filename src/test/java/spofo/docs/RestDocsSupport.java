package spofo.docs;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

	/*
	// 스프링 컨텍스트와 restDoc을 연결해준다.
	// 그러나 spring rest doc 작성을 위해서 스프링 컨테이너를 실행해야 하는 단점이 있다.
	@BeforeEach
	void setup(WebApplicationContext webApplicationContext,
		RestDocumentationContextProvider provider) {

		// 스프링의 컨텍스트를 넣는데 restDoc 설정을 연결해준다.
		this.mockMvc = webAppContextSetup(webApplicationContext)
			.apply(documentationConfiguration(provider))
			.build();
	}
	 */

    // 스프링 컨테이너 없이 Spring rest doc 설정하기
    @BeforeEach
    void setup(RestDocumentationContextProvider provider) {
        this.mockMvc = standaloneSetup(initController())
                .apply(documentationConfiguration(provider))
                .build();
    }

    protected abstract Object initController();

    protected Attribute field(String key, String value) {
        return new Attribute(key, value);
    }

    protected Attribute koreanTitle(String value) {
        return field("koreanTitle", value);
    }

    protected <T extends Enum<T>> String enumDesc(Class<T> enumType, String desc) {
        String formattedValues = Arrays.stream(enumType.getEnumConstants())
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String message = "다음 중 1개의 값 (" + formattedValues + ")";

        return hasText(desc) ? desc + ", " + message : message;
    }

    protected HeaderDescriptor contentTypeHeader() {
        return headerWithName(CONTENT_TYPE)
                .attributes(koreanTitle("컨텐츠 타입"))
                .attributes(field("type", STRING.toString()))
                .description(APPLICATION_JSON_VALUE)
                .optional();
    }

    protected HeaderDescriptor authorizationHeader() {
        return headerWithName(AUTHORIZATION)
                .attributes(koreanTitle("인증 토큰"))
                .attributes(field("type", STRING.toString()))
                .description("Bearer 토큰");
    }
}