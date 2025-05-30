package fap_sports.integrador.services;

import fap_sports.integrador.models.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import fap_sports.integrador.models.Usuario;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registrarUsuario(Usuario usuario) {
        String contraseniaEncriptada = passwordEncoder.encode(usuario.getContrasenia());
        usuario.setContrasenia(contraseniaEncriptada);

        logger.info("Encriptando contraseña para el usuario: {}", usuario.getEmail());

        try {
            String sql = "INSERT INTO usuarios (usu_nombres, usu_apellidos, usu_fecha_nacimiento, usu_contrasenia, usu_correo, usu_telefono, usu_dni) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING usu_id";
            logger.info("Ejecutando consulta SQL: {}", sql);
            logger.info("Valores: nombre={}, apellido={}, fechaNacimiento{}, contrasenia={}, email={}, telefono={}, dni={}",
                    usuario.getNombre(), usuario.getApellido(), usuario.getFechaNacimiento(), usuario.getContrasenia(), usuario.getEmail(), usuario.getTelefono(), usuario.getDni());

            @SuppressWarnings("deprecation")
            Long usuarioId = jdbcTemplate.queryForObject(sql, new Object[]{
                            usuario.getNombre(),
                            usuario.getApellido(),
                            usuario.getFechaNacimiento(),
                            usuario.getContrasenia(),
                            usuario.getEmail(),
                            usuario.getTelefono(),
                            usuario.getDni()
                    },
                    Long.class);

            List<Rol> roles = usuario.getRoles();
            if (roles != null && !roles.isEmpty()) {
                for (Rol rol : roles) {
                    String sqlUsuariosRoles = "INSERT INTO usuarios_roles (usu_id, rol_id) VALUES (?, ?)";
                    logger.info("Ejecutando consulta SQL: {}", sqlUsuariosRoles);
                    logger.info("Valores: usu_id={}, rol_id={}", usuarioId, rol.getId());
                    jdbcTemplate.update(sqlUsuariosRoles, usuarioId, rol.getId());
                }
            }

            logger.info("Usuario registrado con exito: {}", usuario.getEmail());

        } catch (DataAccessException e) {
            logger.error("Error al insertar el usuario", e);
            logger.error("Error al insertar el usuario: " + e.getMessage(), e); 
            System.err.println("Error al insertar el usuario: " + e.getMessage());
            throw new RuntimeException("Error al registrar el usuario", e);
        }
    }
    public boolean existeCorreo(String correo) {
            String sql = "SELECT COUNT(*) FROM usuarios WHERE usu_correo = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, correo);
            return count != null && count > 0;
        }

        public boolean existeDni(String dni) {
            String sql = "SELECT COUNT(*) FROM usuarios WHERE usu_dni = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, dni);
            return count != null && count > 0;
        }

        public boolean existeTelefono(String telefono) {
            String sql = "SELECT COUNT(*) FROM usuarios WHERE usu_telefono = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, telefono);
            return count != null && count > 0;
        }
}