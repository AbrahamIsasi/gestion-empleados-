package com.gestion.empleados.security;


import com.gestion.empleados.entidades.Supervisor;
import com.gestion.empleados.repositorios.SupervisorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SupervisorRepository supervisorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1️⃣ USUARIO PROGRAMADOR (NO VIENE DE LA BD)
        if (username.equalsIgnoreCase("programador")) {

            String encryptedPassword = "$2a$10$Dowqo3ho5XW7uCEQqyPpEO9lz6vqnmGgnvUNxqYqtzbp0z9gDqTSu"; //

            return User.withUsername("programador")
                    .password(encryptedPassword)
                    .roles("ADMIN")  // acceso total
                    .build();
        }

        // 2️⃣ SUPERVISORES DE BD
        Supervisor supervisor = supervisorRepository.findByEmail(username);

        if (supervisor == null) {
            throw new UsernameNotFoundException("No existe el supervisor");
        }

        return new CustomUserDetails(supervisor);
    }
}


