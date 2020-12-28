package me.eatnows.newcruit.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.eatnows.newcruit.config.auth.PrincipalDetails;
import me.eatnows.newcruit.domain.User;
import me.eatnows.newcruit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

/**
 * UsernamePasswordAuthenticationFilter
 * login 요청헤서 username, passwrod를 전송하면 (post메소드)
 * UsernamePasswordAuthenticationFilter 동작을 함
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    /**
     * /login으로 요청이 오면 로그인 시도를 위해서 실행되는 함수
     *
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 로그인 시도중");

        // 로그인 정보를 받아서 로그인 시도
        // authenticationManager로 로그인 시도를 하면 PrincipalDetailsService의 loadUserByUsername()함수 실행됨.
        // principalDetails를 세션에 담고 (권한 관리를 위해서)
        // JWT토큰을 만들어서 응답

        ObjectMapper om = new ObjectMapper();
        User user = null;
        try {
            user = om.readValue(request.getInputStream(), User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 인자로 Username과 Password를 넣어준다.
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        System.out.println("authenticationToken : " + authenticationToken);

        // PrincipalDetailsService의 loadByUsername() 메소드가 실행
        // 로그인한 정보가 담김
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        System.out.println("authentication : " + authentication);

        // authentication 객체가 session 영역에 저장됨 => 로그인 되었다는 의미
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println(principalDetails.getUser().getEmail());

        System.out.println("===========================");

        // authentication 객체가 session 영역에 저장을 해야하고 그 방법은 return 해주면 된다.
        // return의 이유는 권한 관리를 security가 대신 해주기 때문에 편하게하려고 하는 것.
        // 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없지만, 권한 처리 때문에 session에 넣어준다.
        return authentication;
    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨
    // 여기서 JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행 : 인증완료");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // RSA 방식은 아니고 Hash암호방식
        String jwtToken = JWT.create()
                .withSubject("test토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 10))) // 만료시간
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("email", principalDetails.getUser().getEmail())
                .sign(Algorithm.HMAC512("eatnows"));


        response.addHeader("Authorization", "Bearer " + jwtToken);
    }
}
