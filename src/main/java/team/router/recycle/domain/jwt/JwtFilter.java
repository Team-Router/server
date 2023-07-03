//package team.router.recycle.domain.jwt;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.Part;
//import jakarta.validation.constraints.NotNull;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Enumeration;
//
//@RequiredArgsConstructor
//public class JwtFilter extends OncePerRequestFilter {
//
//    public static final String AUTHORIZATION_HEADER = "Authorization";
//    public static final String BEARER_PREFIX = "Bearer ";
//
//    private final TokenProvider tokenProvider;
//
//    // 실제 필터링 로직은 doFilterInternal 에 들어감
//    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
//    @Override
//    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws IOException, ServletException {
//        logger.info("===path, method===");
//        logger.info("path : " + request.getServletPath());
//        logger.info("method : " + request.getMethod());
//        logger.info("===header===");
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while(headerNames.hasMoreElements()){
//            String name = headerNames.nextElement();
//            logger.info(name + " : " + request.getHeader(name));
//        }
//        logger.info("===part===");
//        if(request.getContentType().equals("multipart/form-data")){
//            Collection<Part> parts = request.getParts();
//            for (Part part : parts) {
//                logger.info(part.getName() + " : " + part.getSize());
//            }
//        }
//
//        if(request.getHeader(AUTHORIZATION_HEADER) == null){
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = resolveToken(request);
//        if (token == null) {
//            throw new RuntimeException("토큰이 존재하지 않습니다.");
//        }
//
//        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
//            Authentication authentication = tokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    // Request Header 에서 토큰 정보를 꺼내오기
//    private String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}
