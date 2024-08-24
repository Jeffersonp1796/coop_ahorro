package iti.modelo;

public class Usuario {
    private int id;
    private String nombre;
    private String correo;

    public Usuario(int id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
    }

    // Getters y setters
    public int getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
