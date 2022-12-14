package io.dino.learning.photoappapiusers.security;

import io.dino.learning.photoappapiusers.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private Environment environment;
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurity(Environment environment, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.environment = environment;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String gatewayIp = environment.getProperty("gateway.ip");
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/h2-console/**").hasIpAddress(gatewayIp)
                .antMatchers(HttpMethod.GET, "/actuator/health").hasIpAddress(gatewayIp)
                .antMatchers(HttpMethod.GET, "/actuator/circuitbreakerevents").hasIpAddress(gatewayIp)
                .antMatchers("/**").hasIpAddress(gatewayIp)
                .and()
                .addFilter(getAuthenticationFilter());
        // Once we add spring security, we have to disable frameOptions else we will get
        // blank page when going to H2 console.  NOTE!  Never leave this uncommented and push to repo,
        // as this opens possibility of ClickJacking.
        http.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        String loginEndPoint = environment.getProperty("login.url.path");
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment, authenticationManager());
        // By default, spring security process login via default url "/login".  But since we are using
        // API gateway to establish connection, our url looks like http://localhost:<port>/users-ws/login.
        // Hence, we customize it here to be /users/login or /users/auth or whatever we want.
        authenticationFilter.setFilterProcessesUrl(loginEndPoint);
        return authenticationFilter;
    }
}
