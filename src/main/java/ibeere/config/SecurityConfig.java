package ibeere.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ibeere.security.CustomAuthenticationFailureHandler;
import ibeere.user.auth.CookieUserDetailsService;
import ibeere.user.auth.LoginUserDetailsService;
import ibeere.user.auth.email.EmailAuthenticationProvider;
import ibeere.user.auth.google.GoogleAuthenticationProvider;
import ibeere.user.auth.twitter.TwitterAuthenticationProvider;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CookieUserDetailsService cookieUserDetailsService;

    @Autowired
    private LoginUserDetailsService loginUserDetailsService;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private TwitterAuthenticationProvider twitterAuthenticationProvider;

    @Autowired
    private GoogleAuthenticationProvider googleAuthenticationProvider;

    @Autowired
    private EmailAuthenticationProvider emailAuthenticationProvider;

    @Autowired
    private CustomAuthenticationFailureHandler myFailureHandler = new CustomAuthenticationFailureHandler();

    public SecurityConfig() {
        super();
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationEventPublisher(new DefaultAuthenticationEventPublisher());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .cors()
                .configurationSource(corsConfigurationSource)
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                .authenticationProvider(emailAuthenticationProvider)
                .authenticationProvider(googleAuthenticationProvider)
                .authenticationProvider(twitterAuthenticationProvider)
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()

                .and()
                .formLogin()
                .failureHandler(myFailureHandler)
                .and()
                .rememberMe()
                .tokenValiditySeconds(7862400) // 13 weeks /  3 months
                .userDetailsService(cookieUserDetailsService)
                .key("uniqueAndSecret")
                .and()
                .logout()
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    @Bean
    public PasswordEncoder encoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}