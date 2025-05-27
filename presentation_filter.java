// filter 등록
@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<Filter> jwtFilterRegistration(JwtAuthFilter jwtAuthFilter) {
    FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();

    registration.setFilter(jwtAuthFilter);
    registration.addUrlPatterns("/*"); // 해당 경로에만 필터 적용
    registration.setOrder(1); // 필터 실행 순서

    return registration;
  }
}

// filter 구현
@Component
public class TestFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) {
    System.out.println("TestFilter 초기화 완료");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    System.out.println("요청 URI: " + req.getRequestURI());

    // 다음 필터(또는 컨트롤러)로 요청 전달
    chain.doFilter(request, response);

  }

  @Override
  public void destroy() {
    System.out.println("TestFilter 소멸");
  }
}

// filter interface
public interface Filter {
  default void init(FilterConfig filterConfig) throws ServletException {}

  void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException;

  default void destroy() {}
}


// interceptor 구현
@Component
public class AuthInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // true: 다음 단계로 진행
    // false: 중단, 다음 단계로 진행 x
    ...
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    // controller -> view rendering 전달 전에 ModelAndView/응답 데이터 조작
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    // 요청 완료 후 리소스 정리 등
  }
}


// interceptor 등록
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")     // 인터셉터 적용 경로
                .excludePathPatterns("/login"); // 제외 경로
    }
}

// AOP 구현 예시

@Aspect
@Component
public class LoggingAspect {

    // 포인트컷 설정
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    // Before Advice
    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("실행 전: " + joinPoint.getSignature().getName());
    }

    // AfterReturning Advice
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        System.out.println("실행 후: " + joinPoint.getSignature().getName());
        System.out.println("반환값: " + result);
    }

    // AfterThrowing Advice
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "e")
    public void handleException(JoinPoint joinPoint, Exception e) {
        System.out.println("예외 발생: " + e.getMessage());
    }
}

