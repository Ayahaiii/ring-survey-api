package com.monetware.ringsurvey.system.config;

import com.monetware.ringsurvey.system.exception.AuthExceptionEntryPoint;
import com.monetware.ringsurvey.system.exception.CustomAccessDeniedHandler;
import com.monetware.ringsurvey.system.filter.TokenFilter;
import com.monetware.ringsurvey.system.token.MyJwtAccessTokenConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * 资源服务器配置
 *
 * @author Simo
 * @date 2019-07-29
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * 资源服务请求自定义异常捕获配置
     *
     * @param resources
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.authenticationEntryPoint(new AuthExceptionEntryPoint())
                .accessDeniedHandler(customAccessDeniedHandler);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin().disable()
                .logout().disable()
                .cors()
                .and()
                .csrf()
                .disable();

        // 配置不认证URL
        httpSecurity.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/swagger**",
                        "/webjars/**",
                        "/resources/**",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/druid/**",
                        "/druid**",
                        "/js/**/**/**",
                        "/resource/**/**/**",
                        "/project-file/**/**/**",
                        "/questionnaires/library/**/**",
                        "/app/android/apk/**").permitAll()
                .antMatchers("/**/**/download",
                        "/**/**/export",
                        "/w1/login/**/**",
                        "/w1/out/**",
                        "/w1/test/**",
                        "/w1/app/get").permitAll()
                .anyRequest().authenticated();

        httpSecurity.addFilterAfter(new TokenFilter(), BasicAuthenticationFilter.class);
    }

    /**
     * Token 生成方式 JWT
     *
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * JWT生成Token
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final MyJwtAccessTokenConverter converter = new MyJwtAccessTokenConverter();
        // Token 对接令牌
        converter.setSigningKey("monetware@sh");
        return converter;
    }

}
