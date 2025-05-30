package fap_sports.integrador.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<String> nombres = new ArrayList<>();
        String query = "SELECT usuario_nombre FROM usuarios";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String nombre = resultSet.getString("usuario_nombre");
                nombres.add(nombre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("titulo", "Lista de Nombres de Usuarios");
        model.addAttribute("nombres", nombres);
        return "vistas/usuarios";
    }
}