package fap_sports.integrador.controllers;

import fap_sports.integrador.models.Rol;
import fap_sports.integrador.models.Licenciado;
import fap_sports.integrador.services.LicenciadoService;
import fap_sports.integrador.services.RolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.ArrayList;

@Controller
public class RegistroLicenciadoController {

    private static final Logger logger = LoggerFactory.getLogger(RegistroLicenciadoController.class);

    private final LicenciadoService licenciadoService;
    private final RolService rolService;

    @Autowired
    public RegistroLicenciadoController(LicenciadoService licenciadoService, RolService rolService) {
        this.licenciadoService = licenciadoService;
        this.rolService = rolService;
    }

    @GetMapping("/registroLicenciado")
    public String mostrarRegistroForm(Model model) {
        List<Rol> roles = rolService.listarRoles();
        model.addAttribute("roles", roles); 
        return "vistas/registro";
    }

    @PostMapping("/registroLicenciado")
    public String registrarLicenciado(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("contrasenia") String contrasenia,
            @RequestParam("email") String email,
            @RequestParam("telefono") String telefono,
            @RequestParam("dni") String dni,
            @RequestParam(value = "roles", required = false) List<Long> rolesIds,
            Model model) {
        try {
            logger.info("Recibiendo datos del formulario: nombre={}, apellido={}, email={}, telefono={}, dni={}, roles={}", nombre, apellido, email, telefono, dni, rolesIds);

            Licenciado nuevoLicenciado = new Licenciado(nombre, apellido, contrasenia, email, telefono, dni);

            List<Rol> roles = new ArrayList<>();
            if (rolesIds != null && !rolesIds.isEmpty()) {
                for (Long rolId : rolesIds) {
                    Rol rol = rolService.obtenerRolPorId(rolId);
                    if (rol != null) {
                        roles.add(rol);
                    } else {
                        logger.warn("No se encontro el rol con ID: {}", rolId);
                    }
                }
            }
            nuevoLicenciado.setRoles(roles); 

            logger.info("Intentando registrar usuario: {}", nuevoLicenciado); 

            licenciadoService.registrarLicenciado(nuevoLicenciado);
            model.addAttribute("mensaje", "Usuario registrado con exito: " + nombre);
            return "vistas/exito";
        } catch (Exception e) {
            logger.error("Error al registrar el usuario", e);
            logger.error("Error al registrar el usuario: " + e.getMessage(), e); 
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "vistas/registro";
        }
    }
}