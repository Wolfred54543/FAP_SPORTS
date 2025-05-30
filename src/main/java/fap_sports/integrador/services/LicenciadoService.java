package fap_sports.integrador.services;

import fap_sports.integrador.models.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import fap_sports.integrador.models.Licenciado;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LicenciadoService {

    private static final Logger logger = LoggerFactory.getLogger(LicenciadoService.class);

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LicenciadoService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registrarLicenciado(Licenciado licenciado) {
        String contraseniaEncriptada = passwordEncoder.encode(licenciado.getContrasenia());
        licenciado.setContrasenia(contraseniaEncriptada);

        logger.info("Encriptando contraseña para el usuario: {}", licenciado.getEmail());

        try {
            String sql = "INSERT INTO usuarios (nombre_usuario, apellido_usuario, contrasenia_usuario, correo_usuario, telefono_usuario, dni_usuario) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
            logger.info("Ejecutando consulta SQL: {}", sql);
            logger.info("Valores: nombre={}, apellido={}, contrasenia={}, email={}, telefono={}, dni={}",
                    licenciado.getNombre(), licenciado.getApellido(), licenciado.getContrasenia(), licenciado.getEmail(), licenciado.getTelefono(), licenciado.getDni());

            @SuppressWarnings("deprecation")
            Long usuarioId = jdbcTemplate.queryForObject(sql, new Object[]{
                            licenciado.getNombre(),
                            licenciado.getApellido(),
                            licenciado.getContrasenia(),
                            licenciado.getEmail(),
                            licenciado.getTelefono(),
                            licenciado.getDni()
                    },
                    Long.class);

            List<Rol> roles = licenciado.getRoles();
            if (roles != null && !roles.isEmpty()) {
                for (Rol rol : roles) {
                    String sqlUsuariosRoles = "INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES (?, ?)";
                    logger.info("Ejecutando consulta SQL: {}", sqlUsuariosRoles);
                    logger.info("Valores: usuario_id={}, rol_id={}", usuarioId, rol.getId());
                    jdbcTemplate.update(sqlUsuariosRoles, usuarioId, rol.getId());
                }
            }

            logger.info("Usuario registrado con exito: {}", licenciado.getEmail());

        } catch (DataAccessException e) {
            logger.error("Error al insertar el usuario", e);
            logger.error("Error al insertar el usuario: " + e.getMessage(), e); 
            System.err.println("Error al insertar el usuario: " + e.getMessage());
            throw new RuntimeException("Error al registrar el usuario", e);
        }
    }
}