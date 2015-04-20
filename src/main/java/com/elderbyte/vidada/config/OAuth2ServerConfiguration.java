package com.elderbyte.vidada.config;

import com.elderbyte.vidada.domain.security.KnownAuthority;
import com.elderbyte.vidada.security.AjaxLogoutSuccessHandler;
import com.elderbyte.vidada.security.Http401UnauthorizedEntryPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
public class OAuth2ServerConfiguration {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Inject
        private Http401UnauthorizedEntryPoint authenticationEntryPoint;

        @Inject
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
            .and()
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .and()
                .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
                .disable()
                .headers()
                .frameOptions().disable()
            .and()
                .authorizeRequests()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/register").permitAll()
                .antMatchers("/api/logs/**").hasAnyAuthority(KnownAuthority.ADMIN)
                .antMatchers("/api/**").authenticated()
                .antMatchers("/websocket/tracker").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/websocket/**").permitAll()
                .antMatchers("/metrics/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/health/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/trace/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/dump/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/shutdown/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/beans/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/configprops/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/info/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/autoconfig/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/env/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/trace/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/api-docs/**").hasAuthority(KnownAuthority.ADMIN)
                .antMatchers("/protected/**").authenticated();

        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter implements EnvironmentAware {

        private static final String ENV_OAUTH = "authentication.oauth.";
        private static final String PROP_CLIENTID = "clientid";
        private static final String PROP_SECRET = "secret";
        private static final String PROP_TOKEN_VALIDITY_SECONDS = "tokenValidityInSeconds";

        private RelaxedPropertyResolver propertyResolver;

        @Inject
        private DataSource dataSource;

        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(dataSource);
        }

        @Inject
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {

            endpoints
                    .tokenStore(tokenStore())
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                .inMemory()
                .withClient(propertyResolver.getProperty(PROP_CLIENTID))
                .scopes("read", "write")
                .authorities(KnownAuthority.ADMIN, KnownAuthority.USER)
                .authorizedGrantTypes("password", "refresh_token")
                .secret(propertyResolver.getProperty(PROP_SECRET))
                .accessTokenValiditySeconds(propertyResolver.getProperty(PROP_TOKEN_VALIDITY_SECONDS, Integer.class, 1800));
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_OAUTH);
        }
    }
}
