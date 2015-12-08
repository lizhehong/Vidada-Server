package com.elderbyte.vidada.config;

import com.elderbyte.server.security.jwt.JwtAuthenticationProvider;
import com.elderbyte.server.security.jwt.JwtFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        logger.info("configure @AuthenticationManagerBuilder");
        auth.authenticationProvider(new JwtAuthenticationProvider());
    }



    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        logger.info("configureGlobal @AuthenticationManagerBuilder");

        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.info("configure @HttpSecurity");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(
            SessionCreationPolicy.STATELESS);
        http.addFilterAfter(new JwtFilter(authenticationManagerBean()), AnonymousAuthenticationFilter.class);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        logger.info("configuring @WebSecurity...");

        web.ignoring()
            .antMatchers("/api/auth/**")
            .antMatchers("/api/register")
            .antMatchers("/api/activate")

            .antMatchers("/scripts/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/assets/**")
            .antMatchers("/swagger-ui/**")
            .antMatchers("/test/**")
            .antMatchers("/console/**");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
