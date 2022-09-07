package com.egg.recetapp.servicios;

import com.egg.recetapp.entidades.Foto;
import com.egg.recetapp.entidades.Usuario;
import com.egg.recetapp.enumeracion.Categoria;
import com.egg.recetapp.enumeracion.Rol;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio ur;

    @Autowired
    private FotoServicio fs;

    @Autowired
    private RecetaServicio rs;

    @Autowired
    private NotificacionServicio ns;

    @Transactional
    public void crearUsuario(String nombre, String apodo, Categoria categoria, String mail, String contrasena, MultipartFile foto) throws ErrorServicio {
        try {
            Usuario respuesta = ur.findByMail(mail);
            if (respuesta != null) {

                if (respuesta.getMail().equals(mail)) {
                    if (respuesta.getEstaActivo() == false) {
                        respuesta.setEstaActivo(true);
                    } else {
                        throw new ErrorServicio("Este e-mail ya se encuentra registrado");
                    }
                }
            } else {
                validarUsuario(nombre, categoria, mail, contrasena);
                Usuario u1 = new Usuario();
                u1.setNombre(nombre);
                u1.setApodo(apodo);
                u1.setCategoria(categoria);
                u1.setMail(mail);
                u1.setRol(Rol.USUARIO);
                String claveEnc = new BCryptPasswordEncoder().encode(contrasena);
                u1.setContrasena(claveEnc);//se persiste con la clave encriptada
                u1.setEstaActivo(true);
                Foto f = fs.guardar(foto);
                u1.setFoto(f);

                ur.save(u1);
                ns.enviar("Bienvenido a RecetApp,gracias por registrarse!!", "Bienvenido", mail);
            }
        } catch (ErrorServicio a) {
            throw new ErrorServicio(a.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void modificarUsuario(Long id, String nombre, String apodo, Categoria categoria, String mail, String contrasena, MultipartFile foto) throws ErrorServicio, IOException {
        validarUsuario(nombre, categoria, mail, contrasena);
        Optional<Usuario> respuesta = ur.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            usuario.setNombre(nombre);
            usuario.setApodo(apodo);
            usuario.setCategoria(categoria);
            usuario.setMail(mail);

            String claveEnc = new BCryptPasswordEncoder().encode(contrasena);
            usuario.setContrasena(claveEnc);

            Long idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();

            }
            Foto fo = fs.modificar(idFoto, foto);
            usuario.setFoto(fo);

            ur.save(usuario);
        } else {
            throw new ErrorServicio("No se ha podido modificar el usuario.");
        }
    }

    public int enviar(String mail) throws ErrorServicio {
        int codigoDeRecuperacion = (int) (Math.random() * 9000 + 1);
        ns.enviar("Usted esta queriendo cambiar su contraseña de RecetApp", "Su código de recuperacion es " + codigoDeRecuperacion, mail);
        return codigoDeRecuperacion;
    }

    @Transactional
    public void cambiarContraseña(Integer codigoIngresado, String contrasena, String mail) throws ErrorServicio {
        try {

            Usuario usu = ur.findByMail(mail);

            String claveEnc = new BCryptPasswordEncoder().encode(contrasena);
            usu.setContrasena(claveEnc);
            ur.save(usu);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServicio("No se ha podido cambiar la contraseña.");
        }

    }


    @Transactional
    public void darDeBaja(Long id) throws Exception {
        Optional<Usuario> respuesta = ur.findById(id);
        if (respuesta.isPresent()) {
            Usuario u = respuesta.get();
            u.setEstaActivo(false);
            ur.save(u);
        } else {
            throw new Exception("No se encontró el usuario que desea dar de baja");
        }
    }

    @Transactional
    public void darDeAlta(Long id) throws Exception {
        Optional<Usuario> respuesta = ur.findById(id);
        if (respuesta.isPresent()) {
            Usuario u = respuesta.get();
            u.setEstaActivo(true);
            ur.save(u);
        } else {
            throw new Exception("No se encontró el usuario que desea dar de baja");
        }
    }

    public void validarUsuario(String nombre, Categoria categoria, String mail, String contrasena) throws ErrorServicio {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre no puede ser nulo");

        }
        if (categoria == null) {
            throw new ErrorServicio("Tenés que seleccionar una categoria");
        }
        if (mail == null || mail.trim().isEmpty()) {
            throw new ErrorServicio("El mail no puede estar vacío");

        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new ErrorServicio("La contraseña no puede estar vacía");

        }

    }

    public Usuario buscarUsuarioPorNombre(String nombre) throws Exception {

        return ur.findByNombre(nombre);
    }

    public Usuario buscarUsuarioPorId(Long id) throws ErrorServicio {
        if (ur.existsById(id)) {
            return ur.findById(id).get();
        } else {
            throw new ErrorServicio("No se ha encontrado ningún usuario.");
        }
    }

    @Transactional
    public void eliminarUsuarioDeLaBaseDeDatosPorId(Long id) {
        ur.deleteById(id);
    }

    public List<Usuario> buscarUsuariosPorCategoria(Categoria categoria) {
        return ur.findByCategoria(categoria);
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {//este metodo recibe el nombre de usuario lo busca en el repositorio y lo transforma en un usuario de spring security
        Usuario usuario = ur.findByMail(mail);
        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());
            permisos.add(p1);

//            Esto me permite guardar el OBJETO USUARIO LOG, para luego ser utilizado
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            //el ultimo parametro solicita una lista de permisos
            User user = new User(usuario.getMail(), usuario.getContrasena(), permisos);
            return user;

        } else {
            return null;
        }
    }

}
    

