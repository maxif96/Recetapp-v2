package com.egg.recetapp.seguridad;

// ****** CONFIGURACIÓN DE LA SEGURIDAD DE NUESTRA PAGINA WEB ******//

import com.egg.recetapp.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


// Debemos crear una  clase, en este caso se llama SeguridadConfiguracion
// la cual tendra las modificaciones de la configuración que ya inyecta
// Spring por defecto.

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SeguridadConfiguracion extends WebSecurityConfigurerAdapter {

    @Autowired
    public UsuarioServicio usuarioServicio;

    @Autowired
    public void globalConfigure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(usuarioServicio)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
    
    
  
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/index","/ingreso","/registro","/cambiarcontrasena","/recibirMail","/css/*", "/js/*", "/img/*","/**").permitAll()
// estas paginas son las de acceso publico.Y se agregan estos archivos para que todo el mundo pueda ver nuestro diseño//
                .antMatchers("/usuario/modificar-usuario/**").hasRole("USUARIO")//se pone doble asterisco en lugar de id.
                .antMatchers("/usuario/buscar-usuario/nombre").hasRole("USUARIO")
                .antMatchers("/usuario/dar-de-baja/**").hasRole("USUARIO")
                .antMatchers("/usuario/buscar-usuario/categoria").hasRole("USUARIO") 
                .anyRequest().authenticated() //cualquier ingreso tiene que ser autenticado.
        
                 //esto es para que en lugar de error forbidden, porque solo puede acceder el usuario,nos ponga el formulario de login para que la persona se logee y pueda acceder
                .and()
                .formLogin().loginPage("/ingreso") //pagina donde se loguea,sino por defecto aparece el de SpringSecurity
                .permitAll()
                
                .loginProcessingUrl("/logincheck") //esto esta mal,no tenemos tal pagina
                .usernameParameter("mail") //se le pasa el mail como parametro
                .passwordParameter("contrasena") //se le pasa la contraseña
                .defaultSuccessUrl("/usuario/miperfil") //si hay exito en el login se redirige a miPerfil
                .permitAll()
                .and().logout().permitAll()
                .logoutUrl("/logout")//necesitamos en metodo para desloguearnos. y remitir la url del controlador aca.FALTA//SI NO TENGO LOGOUT QUEDA LOGUEADO
                .logoutSuccessUrl("/index")//cuando hay exito en el logout se dirige al index
                .permitAll().
                and().csrf().disable();
        
       
    }
}