package com.example.demo.config;

import com.example.demo.oauth.CustomOauth2UserService;
import com.example.demo.oauth.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomOauth2UserService oauth2UserService;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/processLogin")
                .usernameParameter("correo")
                .passwordParameter("contrasenia")
                .defaultSuccessUrl("/redirectByRole",true);


        http.authorizeRequests()
                .antMatchers("/cliente/**").hasAnyAuthority("cliente")
                .antMatchers("/admin","/admin/**").hasAnyAuthority("administrador","administradorG")
                .antMatchers("/plato","/plato/**","/restaurante","/restaurante/**","/cupon","/cupon/**", "/extra","/extra/**").hasAnyAuthority("administradorR")
                .antMatchers("/repartidor", "/repartidor/**").hasAuthority("repartidor")
                .anyRequest().permitAll()
                .and()
                .oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint().userService(oauth2UserService)
                .and()
                .successHandler(oAuth2LoginSuccessHandler);
        http.exceptionHandling().accessDeniedPage("/accessDenied");



        http.logout()

                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

    }



    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("select correo, contrasenia, estado FROM usuario WHERE correo = ?")
                .authoritiesByUsernameQuery("select u.correo, r.tipo from usuario u inner join " +
                        "rol r on (u.idrol = r.idrol) where u.correo = ? and u.estado = 1");


        System.out.println("query configure");



    }
}
