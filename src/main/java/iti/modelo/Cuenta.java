package iti.modelo;

public class Cuenta {
    private int id;
    private double saldo;
    private int usuarioId;

    public Cuenta(int id, double saldo, int usuarioId) {
        this.id = id;
        this.saldo = saldo;
        this.usuarioId = usuarioId;
    }

    public String toString() {
        return "Cuenta{" +
                "id=" + id +
                ", saldo=" + saldo +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
