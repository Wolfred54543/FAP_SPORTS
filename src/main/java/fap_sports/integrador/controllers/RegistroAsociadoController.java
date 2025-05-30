package fap_sports.integrador.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RegistroAsociadoController {

    @GetMapping("/asociados")
    public String asociados() {
        return "vistas/asociados";
    }
}

