package com.example.springsecurityedu1.config;

import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Slf4j
@EnableMethodSecurity
public class SpringSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() throws Exception {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/*.html", "/html/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                         .requestMatchers( "/status",  "/view/login", "/view/signup", "/auth/signup").permitAll()
                        .anyRequest().authenticated())
                .csrf((csrf) -> csrf.disable())
                .formLogin(form -> form
                        .loginPage("/view/login")
                        .loginProcessingUrl("/login-process")
                        .usernameParameter("userid")
                        .passwordParameter("pw")
                        .successHandler((request, response, authentication) -> {
                           log.info("*** 인증을 수행한 계정명 = {}", authentication.getName());
                           response.sendRedirect("/view/memberpage");
                        })
                        .failureHandler((request, response, exception) -> {
                           log.info("*** 예외 메시지 = {}", exception.getMessage());
                           response.sendRedirect("/view/login");
                        }))
                .logout(withDefaults());

        return http.build();
    }
}