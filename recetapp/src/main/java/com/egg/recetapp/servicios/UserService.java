package com.egg.recetapp.servicios;

import com.egg.recetapp.entidades.Photo;
import com.egg.recetapp.entidades.Users;
import com.egg.recetapp.enumeracion.Category;
import com.egg.recetapp.enumeracion.Rol;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.repositorios.UserRepository;
import com.egg.recetapp.servicios.mapper.UserMapper;
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

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PhotoService photoService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private MailService mailService;

    @Transactional
    public void createUser(String name, String nickName, Category category, String mail, String password, MultipartFile photo) throws ErrorServicio, IOException {

        if (userRepository.existsByMail(mail)) throw new EntityExistsException("This email is already registered.");
        validateUser(name, category, mail, password);

        Users user = userMapper.buildUser(name, nickName, category, mail, password, photo);

        userRepository.save(user);
        mailService.send("Thanks for registered. Welcome to Recetapp", "Welcome!", mail);


}

    @Transactional
    public void modificarUsuario(Long id, String nombre, String apodo, Category category, String mail, String contrasena, MultipartFile foto) throws ErrorServicio, IOException {
        validateUser(nombre, category, mail, contrasena);
        Optional<Users> respuesta = userRepository.findById(id);
        if (respuesta.isPresent()) {
            Users users = respuesta.get();

            users.setName(nombre);
            users.setNickName(apodo);
            users.setCategory(category);
            users.setMail(mail);

            String claveEnc = new BCryptPasswordEncoder().encode(contrasena);
            users.setPassword(claveEnc);

            Long idFoto = null;
            if (users.getPhoto() != null) {
                idFoto = users.getPhoto().getId();

            }
            Photo fo = photoService.modificar(idFoto, foto);
            users.setPhoto(fo);

            userRepository.save(users);
        } else {
            throw new ErrorServicio("No se ha podido modificar el usuario.");
        }
    }

    public int enviar(String mail) throws ErrorServicio {
        int codigoDeRecuperacion = (int) (Math.random() * 9000 + 1);
        mailService.send("Usted esta queriendo cambiar su contraseña de RecetApp", "Su código de recuperacion es " + codigoDeRecuperacion, mail);
        return codigoDeRecuperacion;
    }

    @Transactional
    public void cambiarContraseña(Integer codigoIngresado, String contrasena, String mail) throws ErrorServicio {
        try {

            Users usu = userRepository.findByMail(mail);

            String claveEnc = new BCryptPasswordEncoder().encode(contrasena);
            usu.setPassword(claveEnc);
            userRepository.save(usu);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServicio("No se ha podido cambiar la contraseña.");
        }

    }


    @Transactional
    public void darDeBaja(Long id) throws Exception {
        Optional<Users> respuesta = userRepository.findById(id);
        if (respuesta.isPresent()) {
            Users u = respuesta.get();
            u.setOn(false);
            userRepository.save(u);
        } else {
            throw new Exception("No se encontró el usuario que desea dar de baja");
        }
    }

    @Transactional
    public void darDeAlta(Long id) throws Exception {
        Optional<Users> respuesta = userRepository.findById(id);
        if (respuesta.isPresent()) {
            Users u = respuesta.get();
            u.setOn(true);
            userRepository.save(u);
        } else {
            throw new Exception("No se encontró el usuario que desea dar de baja");
        }
    }

    public void validateUser(String name, Category category, String mail, String password) throws ErrorServicio {
        if (name == null || name.trim().isEmpty()) {
            throw new ErrorServicio("Name can not be null");
        }
        if (category == null) {
            throw new ErrorServicio("Must select a category");
        }
        if (mail == null || mail.trim().isEmpty()) {
            throw new ErrorServicio("Email can not be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ErrorServicio("Password can not be empty");
        }

    }

    public Users buscarUsuarioPorNombre(String nombre) throws Exception {

        return userRepository.findByName(nombre);
    }

    public Users buscarUsuarioPorId(Long id) throws ErrorServicio {
        if (userRepository.existsById(id)) {
            return userRepository.findById(id).get();
        } else {
            throw new ErrorServicio("No se ha encontrado ningún usuario.");
        }
    }

    @Transactional
    public void eliminarUsuarioDeLaBaseDeDatosPorId(Long id) {
        userRepository.deleteById(id);
    }

    public List<Users> buscarUsuariosPorCategoria(Category category) {
        return userRepository.findByCategory(category);
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {//este metodo recibe el nombre de users lo busca en el repositorio y lo transforma en un users de spring security
        Users users = userRepository.findByMail(mail);
        if (users != null) {

            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + users.getRol());
            permisos.add(p1);

//            Esto me permite guardar el OBJETO USUARIO LOG, para luego ser utilizado
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", users);

            //el ultimo parametro solicita una lista de permisos
            User user = new User(users.getMail(), users.getPassword(), permisos);
            return user;

        } else {
            return null;
        }
    }

}
    

