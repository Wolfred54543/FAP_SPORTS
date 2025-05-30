package fap_sports.integrador.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String sqlUsuario = "SELECT usu_id, usu_nombres, usu_contrasenia, usu_correo FROM usuarios WHERE usu_correo = ?";
        List<Map<String, Object>> usuarios = jdbcTemplate.queryForList(sqlUsuario, username);

        if (usuarios.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        Map<String, Object> usuario = usuarios.get(0);
        String nombreUsuario = (String) usuario.get("usu_nombres");
        String contrasenia = (String) usuario.get("usu_contrasenia");
        Long idUsuario = (Long) usuario.get("usu_id");
        String correoUsuario = (String) usuario.get("usu_correo");


        String sqlRoles = "SELECT r.id FROM roles r " +
                          "INNER JOIN usuarios_roles ur ON r.id = ur.rol_id " +
                          "WHERE ur.usu_id = ?";
        List<Map<String, Object>> rolesResult = jdbcTemplate.queryForList(sqlRoles, idUsuario);

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Map<String, Object> rol : rolesResult) {
            Long idRol = (Long) rol.get("id");

            if (idRol == null) {
                throw new IllegalStateException("El rol no tiene definido el 'rol_id'.");
            }

            String roleName;
            switch (idRol.intValue()) {
                case 1:
                    roleName = "ADMINISTRADOR";
                    break;
                case 2:
                    roleName = "DELEGADO";
                    break;
                case 3:
                    roleName = "INVITADO";
                    break;
                default:
                    roleName = "DEFAULT";
            }
            authorities.add(new SimpleGrantedAuthority(roleName));
        }

        return new User(correoUsuario, contrasenia, authorities);
    }
}