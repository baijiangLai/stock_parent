package com.lbj.stock.security.config;

import com.lbj.stock.security.filter.JwtAuthorizationFilter;
import com.lbj.stock.security.filter.JwtLoginAuthenticationFilter;
import com.lbj.stock.security.handler.StockAccessDenyHandler;
import com.lbj.stock.security.handler.StockAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Description
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定义公共的无需被拦截的资源
     * @return
     */
    private String[] getPubPath(){
        //公共访问资源
        String[] urls = {
                "/**/*.css","/**/*.js","/favicon.ico","/doc.html",
                "/druid/**","/webjars/**","/v2/api-docs","/api/captcha",
                "/swagger/**","/swagger-resources/**","/swagger-ui.html"
        };
        return urls;
    }

    /**
     * 配置过滤规则
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //登出功能
        http.logout().logoutUrl("/api/logout").invalidateHttpSession(true);
        //开启允许iframe 嵌套。security默认禁用ifram跨域与缓存
        http.headers().frameOptions().disable().cacheControl().disable();
        //session禁用
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();//禁用跨站请求伪造
        http.authorizeRequests()//对资源进行认证处理
                .antMatchers(getPubPath()).permitAll()//公共资源都允许访问
                .anyRequest().authenticated();  //除了上述资源外，其它资源，只有 认证通过后，才能有权访问
        //自定义的过滤器
        http.addFilterBefore(jwtLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        //配置授权过滤器，过滤一切资源
        http.addFilterBefore(jwtAuthorizationFilter(),JwtLoginAuthenticationFilter.class);

        //配置权限访问拒绝处理器
        http.exceptionHandling().accessDeniedHandler(new StockAccessDenyHandler())
                .authenticationEntryPoint(new StockAuthenticationEntryPoint());
    }

    /**
     * 自定义认证过滤器bean
     * @return
     * @throws Exception
     */
    @Bean
    public JwtLoginAuthenticationFilter jwtLoginAuthenticationFilter() throws Exception {
        JwtLoginAuthenticationFilter filter = new JwtLoginAuthenticationFilter("/api/login");
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setRedisTemplate(redisTemplate);
        return filter;
    }

    /**
     * 定义密码加密器，实现对明文密码的加密和匹配操作
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义授权过滤器
     * @return
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(){
        return new JwtAuthorizationFilter();
    }
}
