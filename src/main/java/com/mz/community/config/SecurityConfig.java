package com.mz.community.config;

import com.mz.community.util.CommunityConstant;
import com.mz.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resource/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //auth
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow",
                        "/changePass"
                ).hasAnyAuthority(
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR,
                        AUTHORITY_USER
                ).antMatchers(
                    "/discuss/top",
                        "/discuss/wonderful"
                ).hasAnyAuthority(
                AUTHORITY_MODERATOR
                ).antMatchers(
                "/discuss/delete",
                "/data/**",
                "/tagManagement",
                "/addTags",
                "/deleteAllES",
                "/actuator/**"
        )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                ).anyRequest().permitAll()
                .and().csrf().disable();
        //none Authorization
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        //Asynchronous request
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "Need to sign in"));
                        } else {//Synchronous request
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                }).accessDeniedHandler(new AccessDeniedHandler() {
            //Insufficient permissions
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                String xRequestedWith = request.getHeader("x-requested-with");
                //Asynchronous request
                if ("XMLHttpRequest".equals(xRequestedWith)) {
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(CommunityUtil.getJSONString(403, "You do not have permission to access this feature"));
                } else {//Synchronous request
                    response.sendRedirect(request.getContextPath() + "/denied");
                }
            }
        });
        //The underlying security layer intercepts the path named logout by default, and processes the exit.
        //Override security exit logic using your own written logic
        http.logout().logoutUrl("/OverrideSecurityLogout");
    }
}
