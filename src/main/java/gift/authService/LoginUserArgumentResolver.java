package gift.authService;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import gift.model.Login;
import io.jsonwebtoken.Claims;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private JwtToken jwtToken = new JwtToken();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Login.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Claims claims = jwtToken.validateToken(token);

            if (claims != null) {
                String email = claims.get("email", String.class);
                Long id = claims.get("id", Long.class);
                return new Login(id, email, null);
            }
        }

        return null;
    }
}
