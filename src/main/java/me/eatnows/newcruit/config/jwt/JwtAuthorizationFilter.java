package me.eatnows.newcruit.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import me.eatnows.newcruit.config.auth.PrincipalDetails;
import me.eatnows.newcruit.domain.User;
import me.eatnows.newcruit.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 시큐리티가 filter를 가지고 있는 필터중에 BasicAuthenticationFilter
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 거치게 되어있다.
// 권한이나 인증이 필요한 주소가 아니라면 이 필터는 사용안됨
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    // 인증이나 권한이 필요한 주소요청이 있을 때 해당 필터를 거친다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 주소 요청이 됨.");

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader : " + jwtHeader);

        // header가 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        // jwt 토큰을 검증해서 정상적인 사용자인지 확인
        String jwtToken = request.getHeader("Authorization").replace("Bearer ","");

        String userEmail = JWT.require(Algorithm.HMAC512("eatnows"))
                .build().verify(jwtToken)
                .getClaim("email")
                .asString();

        // 서명이 정상적으로 됨
        if (userEmail != null) {
            // 찾으면 정상적인 유저
            User userEntity = userRepository.findByEmail(userEmail).get();

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
            
            // 임의로 authentication 객체를 만드는것이라서 비밀번호는 null
            // JWT 토큰 서명을 통해 서명이 정상이면 객체를 생성
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            // 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }
}
