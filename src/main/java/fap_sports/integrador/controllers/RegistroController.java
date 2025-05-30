package fap_sports.integrador.controllers;

import fap_sports.integrador.models.Rol;
import fap_sports.integrador.models.Usuario;
import fap_sports.integrador.services.RolService;
import fap_sports.integrador.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

@Controller
public class RegistroController {

    private static final Logger logger = LoggerFactory.getLogger(RegistroController.class);

    private final UsuarioService usuarioService;
    private final RolService rolService;

    @Autowired
    public RegistroController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping("/registro")
    public String mostrarRegistroForm(Model model) {
        List<Rol> roles = rolService.listarRoles();
        model.addAttribute("roles", roles); 
        return "vistas/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("fechaNacimiento") LocalDate fechaNacimiento,
            @RequestParam("contrasenia") String contrasenia,
            @RequestParam("email") String email,
            @RequestParam("telefono") String telefono,
            @RequestParam("dni") String dni,
            @RequestParam(value = "roles", required = false) List<Long> rolesIds,
            Model model) {

        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                model.addAttribute("error", "El nombre no puede estar vacio.");
                return "vistas/registro";
            }
            if (apellido == null || apellido.trim().isEmpty()) {
                model.addAttribute("error", "El apellido no puede estar vacio.");
                return "vistas/registro";
            }
            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("error", "El correo electronico es obligatorio.");
                return "vistas/registro";
            }
            if (contrasenia == null || contrasenia.length() < 8) {
                model.addAttribute("error", "La contraseña debe tener al menos 8 caracteres.");
                return "vistas/registro";
            }
            if (usuarioService.existeCorreo(email)) {
                model.addAttribute("error", "El correo ya esta registrado.");
                return "vistas/registro";
            }

            if (dni == null || dni.length() != 8 || !dni.matches("\\d+")) {
                model.addAttribute("error", "El DNI debe contener exactamente 8 digitos");
                return "vistas/registro";
            }

            if (usuarioService.existeDni(dni)) {
                model.addAttribute("error", "El DNI ya esta registrado.");
                return "vistas/registro";
            }

            if (telefono == null || telefono.length() != 9 || !dni.matches("\\d+")) {
                model.addAttribute("error", "El numero de telefono debe contener exactamente 9 digitos");
                return "vistas/registro";
            }

            if (usuarioService.existeTelefono(telefono)) {
                model.addAttribute("error", "El numero de telefono ya esta registrado.");
                return "vistas/registro";
            }

            logger.info("Recibiendo datos del formulario: nombre={}, apellido={}, fechaNacimiento={}, email={}, telefono={}, dni={}, roles={}",
                    nombre, apellido, fechaNacimiento, email, telefono, dni, rolesIds);

            Usuario nuevoUsuario = new Usuario(nombre, apellido, fechaNacimiento, contrasenia, email, telefono, dni);

            List<Rol> roles = new ArrayList<>();
            boolean rolEncontrado = false;

            if (rolesIds != null && !rolesIds.isEmpty()) {
                for (Long rolId : rolesIds) {
                    Rol rol = rolService.obtenerRolPorId(rolId);
                    if (rol != null) {
                        roles.add(rol);
                        rolEncontrado = true;
                    } else {
                        logger.warn("No se encontró el rol con ID: {}", rolId);
                    }
                }
            }

            if (!rolEncontrado) {
                Rol rolInvitado = rolService.obtenerRolPorId(3L);
                if (rolInvitado != null) {
                    roles.add(rolInvitado);
                } else {
                    logger.error("No se encontró el rol 'INVITADO' con ID 3");
                    model.addAttribute("error", "Error interno: no se pudo asignar un rol por defecto.");
                    return "vistas/registro";
                }
            }

            nuevoUsuario.setRoles(roles);

            logger.info("Intentando registrar usuario: {}", nuevoUsuario);

            usuarioService.registrarUsuario(nuevoUsuario);
            model.addAttribute("mensaje", "Usuario registrado con exito");
            return "vistas/registro";

        } catch (Exception e) {
            logger.error("Error al registrar el usuario", e);

            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            model.addAttribute("roles", rolService.listarRoles());
            return "vistas/registro";
        }
    }
}