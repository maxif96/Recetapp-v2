package com.egg.recetapp.service;

import com.egg.recetapp.entities.Users;
import com.egg.recetapp.enumerations.Category;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.repositories.UserRepository;
import com.egg.recetapp.service.mapper.UserMapper;
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
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public void createUser(String name, String nickName, Category category, String mail, String password, MultipartFile photo) throws ServiceError, IOException {

        if (userRepository.existsByMail(mail)) throw new EntityExistsException("El email ya se encuentra registrado.");
        validateUser(name, category, mail, password);

        Users user = userMapper.buildUser(name, nickName, category, mail, password, photo);

        userRepository.save(user);
        mailService.send("Gracias por registrarte. Bienvenido a Recetapp", "Bienvenido!", mail);


    }

    @Transactional
    public void updateUser(Long id, String name, String nickName, Category category, String mail, String password, MultipartFile photo) throws ServiceError, IOException {
        validateUser(name, category, mail, password);
        if (!userRepository.existsById(id)) throw new EntityNotFoundException("Usuario no encontrado.");
        Users user = userMapper.buildUser(name, nickName, category, mail, password, photo);
        user.setId(id);
        userRepository.save(user);
    }

    public int send(String mail) {
        int recoveryCode = (int) (Math.random() * 9000 + 1);
        mailService.send("Estás tratando de cambiar tu contraseña", "Tu código de recuperación es: " + recoveryCode, mail);
        return recoveryCode;
    }

    @Transactional
    public void updatePassword(String password, String mail) throws ServiceError {
        if (!userRepository.existsByMail(mail)) throw new EntityNotFoundException("Usuario no encontrado.");
        Users user = userRepository.findByMail(mail);

        String encrypt = new BCryptPasswordEncoder().encode(password);
        user.setPassword(encrypt);
        userRepository.save(user);

    }


    @Transactional
    public void softDelete(Long id) throws Exception {
        Users user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

        user.setOn(false);
        userRepository.save(user);

    }

    public void validateUser(String name, Category category, String mail, String password) throws ServiceError {
        if (name == null || name.trim().isEmpty()) {
            throw new ServiceError("El nombre no puede estar vacío.");
        }
        if (category == null) {
            throw new ServiceError("Debe seleccionar una categoría.");
        }
        if (mail == null || mail.trim().isEmpty()) {
            throw new ServiceError("El email no puede estar vacío.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ServiceError("La contraseña no puede estar vacía");
        }

    }

    public Users findUserByName(String name) {
        return userRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));
    }

    public Users findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User was not found"));
    }

    public List<Users> findUserByCategory(Category category) {
        if (userRepository.findByCategory(category).isEmpty())
            throw new EntityNotFoundException("Ningún usuario ha sido encontrado.");
        return userRepository.findByCategory(category);
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Users users = userRepository.findByMail(mail);
        if (users != null) {

            List<GrantedAuthority> authorities = new ArrayList();
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + users.getRol());
            authorities.add(p1);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", users);

            User user = new User(users.getMail(), users.getPassword(), authorities);
            return user;
        } else {
            return null;
        }
    }

}
    

