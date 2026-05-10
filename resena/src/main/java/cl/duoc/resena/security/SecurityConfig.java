package cl.duoc.resena.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) //para dejar preguntar postmancito
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v1/**").permitAll() //para pruebas, de ahí lo quito o directamente no usaría spring security, hay que verlo
                .requestMatchers("/v1").permitAll()
                .anyRequest().permitAll()
                
            );
        return http.build();
    }
    

}
