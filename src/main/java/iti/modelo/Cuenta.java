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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "id=" + id +
                ", saldo=" + saldo +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
