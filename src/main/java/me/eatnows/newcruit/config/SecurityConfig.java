package me.eatnows.newcruit.config;

import lombok.RequiredArgsConstructor;
import me.eatnows.newcruit.config.jwt.JwtAuthenticationFilter;
import me.eatnows.newcruit.config.jwt.JwtAuthorizationFilter;
import me.eatnows.newcruit.config.oauth.PrincipalOAuth2UserService;
import me.eatnows.newcruit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final UserRepository userRepository;
//    private final PrincipalOAuth2UserService principalOAuth2UserService;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        // 세션을 사용하지 않겠다. STATELESS 서버로 만들겠다.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(corsFilter) // @CrossOrigin : 인증없을때, 시큐리티 필터에 등록: 인증있을때
        .formLogin().disable()
        .httpBasic().disable()
        .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
        .authorizeRequests()
        .antMatchers("/api/user/**")
        .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
        .antMatchers("/api/manager/**")
        .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
        .antMatchers("/api/admin/**")
        .access("hasRole('ROLE_ADMIN')")
        .anyRequest().permitAll(); // 다른 접근은 로그인 인증 없이
//        .and()
//        .oauth2Login()
//        .userInfoEndpoint()
//        .userService(principalOAuth2UserService);

    }
}
