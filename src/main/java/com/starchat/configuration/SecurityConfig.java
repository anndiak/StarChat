package com.starchat.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Bean
    public CustomLoginSuccessHandler customLoginSuccessHandler(SimpMessagingTemplate messagingTemplate) {
        return new CustomLoginSuccessHandler(messagingTemplate);
    }

    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler(SimpMessagingTemplate messagingTemplate) {
        return new CustomLogoutSuccessHandler(messagingTemplate);
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/register").permitAll()
                .antMatchers("/").authenticated()
                .antMatchers("/websocket-endpoint/**").permitAll()
                .and()
                .csrf()
                .ignoringAntMatchers("/websocket-endpoint/**")
                .and()
                .formLogin().loginPage("/login")
                .successHandler(customLoginSuccessHandler(simpMessagingTemplate))
                .and()
                .logout()
                .logoutSuccessHandler(customLogoutSuccessHandler(simpMessagingTemplate))
                .and()
                .csrf().disable()
                .headers().frameOptions().disable();
    }
}
