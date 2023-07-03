//package team.router.recycle.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import team.router.recycle.domain.jwt.JwtAccessDeniedHandler;
//import team.router.recycle.domain.jwt.JwtAuthenticationEntryPoint;
//import team.router.recycle.domain.jwt.JwtFilter;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@EnableWebSecurity
//public class SecurityConfig {
//    private final JwtFilter jwtFilter;
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .headers(headers -> headers
//                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
//                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//                        .requestMatchers("/", "/css/**", "/images/**", "/js/**").permitAll()
//                        .requestMatchers(PathRequest.toH2Console()).permitAll()
//                        //
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/kakao/**").permitAll()
//                        .anyRequest().authenticated())
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/"));
////                .csrf(AbstractHttpConfigurer::disable)
////                .cors(Customizer.withDefaults())
////                .formLogin(AbstractHttpConfigurer::disable)
////                .httpBasic(AbstractHttpConfigurer::disable)
////                .exceptionHandling(exception -> exception
////                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
////                        .accessDeniedHandler(jwtAccessDeniedHandler)
////                )
////                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .authorizeHttpRequests(authorize -> authorize
////                        .requestMatchers("/**").permitAll()
////                        .requestMatchers("/api/auth/**").permitAll()
////                        .anyRequest().authenticated()
////                );
////                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://router-bike.site", "https://re-cycle-test.vercel.app"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowedMethods(List.of("*"));
////        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
//}
