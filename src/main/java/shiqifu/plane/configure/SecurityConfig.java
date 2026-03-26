package shiqifu.plane.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import shiqifu.plane.handle.AuthenticationEntryPoint;
import shiqifu.plane.handle.LogoutHandle;
import shiqifu.plane.util.JwtUtil;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import java.io.IOException;
import java.util.*;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final LogoutHandle logoutHandle;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    public SecurityConfig(JwtUtil jwtUtil, LogoutHandle logoutHandle, AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtUtil = jwtUtil;
        this.logoutHandle = logoutHandle;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));


        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public SecurityFilterChain springSecurityFilter(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .authorizeHttpRequests(
                        author->{
                            author.requestMatchers("/auth/login").permitAll()
                                    .requestMatchers("/auth/register").permitAll()
                                    .requestMatchers("/auth/send").permitAll()
                                    .requestMatchers("/auth/update").permitAll()
                                    .anyRequest().authenticated();
                        }
                )
                        .addFilterBefore(new jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex->ex.authenticationEntryPoint(authenticationEntryPoint))
                .logout(logout -> logout.logoutSuccessHandler(logoutHandle).permitAll());
        return http.build();
    }
    public class jwtAuthenticationFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String username;
            if(authHeader==null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request,response);
                return;
            }
            jwt=authHeader.substring(7);
            try {
                username=jwtUtil.extractUsername(jwt);
                if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
                    if(jwtUtil.isTokenValid(jwt,username)){
                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
                                null,
                                authorities);
                        SecurityContextHolder.getContext().setAuthentication(token);
                    }else{
                        handleAuthenticationFailure(response, "Token 无效");
                        return;
                    }
                }
            }
            catch(Exception e) {
                log.warn("JWT Authentication failed: {}", e.getMessage());
                handleAuthenticationFailure(response,"Token 已过期或无效");
                SecurityContextHolder.clearContext();
                return;
            }
            filterChain.doFilter(request,response);
        }
    }
    private void handleAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> error = new HashMap<>();
        error.put("code", 401);
        error.put("message", message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
