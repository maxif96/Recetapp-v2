package com.egg.recetapp.security;


import com.egg.recetapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@org.springframework.context.annotation.Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Configuration extends WebSecurityConfigurerAdapter {

    @Autowired
    public UserService userService;

    @Autowired
    public void globalConfigure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
    
    
  
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/index","/ingreso","/registro","/cambiarcontrasena","/recibirMail","/css/*", "/js/*", "/img/*","/**").permitAll()
                .antMatchers("/usuario/modificar-usuario/**").hasRole("USUARIO")
                .antMatchers("/usuario/buscar-usuario/nombre").hasRole("USUARIO")
                .antMatchers("/usuario/dar-de-baja/**").hasRole("USUARIO")
                .antMatchers("/usuario/buscar-usuario/categoria").hasRole("USUARIO") 
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/ingreso")
                .permitAll()
                .loginProcessingUrl("/logincheck")
                .usernameParameter("mail")
                .passwordParameter("contrasena")
                .defaultSuccessUrl("/usuario/miperfil")
                .permitAll()
                .and().logout().permitAll()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index")
                .permitAll().
                and().csrf().disable();
    }
}