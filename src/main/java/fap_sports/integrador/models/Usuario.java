package fap_sports.integrador.models;

import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usu_id")
    private Long usuId;

    @Column(name = "usu_nombres", nullable = false)
    private String nombre;

    @Column(name = "usu_apellidos", nullable = false)
    private String apellido;

    @Column(name = "usu_fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "usu_correo", unique = true, nullable = false)
    private String email;

    @Column(name = "usu_contrasenia", unique = true, nullable = false)
    private String contrasenia;

    @Column(name = "usu_dni", unique = true, nullable = false)
    private String dni;

    @Column(name = "usu_telefono", unique = true, nullable = false)
    private String telefono;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "usu_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private List<Rol> roles;

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, LocalDate fechaNacimiento, String contrasenia, String email, String telefono, String dni, List<Rol> roles) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.contrasenia = contrasenia;
        this.email = email;
        this.telefono = telefono;
        this.dni = dni;
        this.roles = roles;
    }

    public Usuario(String nombre, String apellido, LocalDate fechaNacimiento, String contrasenia, String email, String telefono, String dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.contrasenia = contrasenia;
        this.email = email;
        this.telefono = telefono;
        this.dni = dni;
    }

    public Long getId() {
        return usuId;
    }

    public void setId(Long usuId) {
        this.usuId = usuId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + usuId +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", contrasenia='" + contrasenia + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", dni='" + dni + '\'' +
                ", roles=" + roles +
                '}';
    }

}