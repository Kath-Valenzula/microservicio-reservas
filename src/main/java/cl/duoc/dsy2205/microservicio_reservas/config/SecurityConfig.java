package cl.duoc.dsy2205.microservicio_reservas.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cl.duoc.dsy2205.microservicio_reservas.security.AuthUsuario;
import cl.duoc.dsy2205.microservicio_reservas.security.AuthUsuarioRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/health").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(AuthUsuarioRepository usuarioRepository) {
        return username -> {
            if ("admin".equalsIgnoreCase(username)) {
                return User.withUsername("admin")
                        .password(passwordEncoder().encode("admin123"))
                        .roles("USER", "ADMIN")
                        .build();
            }
            AuthUsuario usuario = usuarioRepository.findByCorreoIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return User.withUsername(usuario.getCorreo())
                    .password(usuario.getPasswordHash())
                    .roles(resolveRoles(usuario))
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private String[] resolveRoles(AuthUsuario usuario) {
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            return new String[] { "PATIENT" };
        }
        String[] roles = usuario.getRoles().stream()
                .filter(role -> role != null && !role.isBlank())
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .toArray(String[]::new);
        if (roles.length == 0) {
            return new String[] { "PATIENT" };
        }
        return roles;
    }
}
