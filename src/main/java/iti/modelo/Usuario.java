package iti.modelo;

public class Usuario {
    private int id;
    private String nombre;
    private String correo;
    private String cedula;

    public Usuario(int id, String nombre, String correo, String cedula) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.cedula = cedula;

    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", cedula='" + cedula + '\'' +
                '}';
    }
}
