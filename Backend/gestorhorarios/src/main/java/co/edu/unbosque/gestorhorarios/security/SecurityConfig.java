package co.edu.unbosque.gestorhorarios.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final List<String> allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService,
            @Value("${app.frontend.allowed-origins:http://localhost:4200}") String allowedOriginsRaw) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.allowedOrigins = Arrays.stream(allowedOriginsRaw.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .collect(Collectors.toList());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        .requestMatchers("/usuarios/**").hasAuthority("ROLE_ADMINISTRADOR")
                        .requestMatchers("/parametros-sistema/**")
                        .hasAuthority("ROLE_ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/asignaturas/**", "/semestres/**", "/grupos/**",
                                "/docentes/**", "/aulas/**", "/recursos-aula/**", "/docentes-asignaturas/**",
                                "/restricciones-horarias-docente/**", "/asignaturas-recursos-requeridos/**",
                                "/aulas-recursos/**")
                        .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_PLANIFICADOR")
                        .requestMatchers(HttpMethod.PUT, "/asignaturas/**", "/semestres/**", "/grupos/**",
                                "/docentes/**", "/aulas/**", "/recursos-aula/**", "/docentes-asignaturas/**",
                                "/restricciones-horarias-docente/**", "/asignaturas-recursos-requeridos/**",
                                "/aulas-recursos/**")
                        .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_PLANIFICADOR")
                        .requestMatchers(HttpMethod.DELETE, "/asignaturas/**", "/semestres/**", "/grupos/**",
                                "/docentes/**", "/aulas/**", "/recursos-aula/**", "/docentes-asignaturas/**",
                                "/restricciones-horarias-docente/**", "/asignaturas-recursos-requeridos/**",
                                "/aulas-recursos/**")
                        .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_PLANIFICADOR")
                        .requestMatchers(HttpMethod.POST, "/horarios/**", "/sesiones-clase/**")
                        .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_PLANIFICADOR", "ROLE_ESTUDIANTE", "ROLE_CONSULTA")
                        .requestMatchers(HttpMethod.PUT, "/horarios/**", "/sesiones-clase/**")
                        .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_PLANIFICADOR", "ROLE_ESTUDIANTE", "ROLE_CONSULTA")
                        .requestMatchers(HttpMethod.DELETE, "/horarios/**", "/sesiones-clase/**")
                        .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_PLANIFICADOR", "ROLE_ESTUDIANTE", "ROLE_CONSULTA")
                        .requestMatchers(HttpMethod.POST, "/**")
                        .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_PLANIFICADOR", "ROLE_ESTUDIANTE")
                        .requestMatchers(HttpMethod.PUT, "/**")
                        .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_PLANIFICADOR", "ROLE_ESTUDIANTE")
                        .requestMatchers(HttpMethod.DELETE, "/**")
                        .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_PLANIFICADOR", "ROLE_ESTUDIANTE")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
