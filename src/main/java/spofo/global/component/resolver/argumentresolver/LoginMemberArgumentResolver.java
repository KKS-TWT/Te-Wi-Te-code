package spofo.global.component.resolver.argumentresolver;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import spofo.auth.domain.MemberInfo;
import spofo.auth.domain.annotation.LoginMember;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 컨트롤러의 파라미터를 체크하는데 내부적으로 캐시가 동작하여 한 번 체크하면 다음부터는 캐시에 저장된 값을 사용함
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(LoginMember.class);
        boolean hasLoginMemberType =
                MemberInfo.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasLoginMemberType;
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        return getContext().getAuthentication().getPrincipal();
    }
}