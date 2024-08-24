package Intervienen;

public class Transaccion {
    private int id;
    private int cuentaOrigenId;
    private int cuentaDestinoId;
    private double monto;
    private String tipo;

    public Transaccion(int id, int cuentaOrigenId, int cuentaDestinoId, double monto, String tipo) {
        this.id = id;
        this.cuentaOrigenId = cuentaOrigenId;
        this.cuentaDestinoId = cuentaDestinoId;
        this.monto = monto;
        this.tipo = tipo;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCuentaOrigenId() { return cuentaOrigenId; }
    public void setCuentaOrigenId(int cuentaOrigenId) { this.cuentaOrigenId = cuentaOrigenId; }

    public int getCuentaDestinoId() { return cuentaDestinoId; }
    public void setCuentaDestinoId(int cuentaDestinoId) { this.cuentaDestinoId = cuentaDestinoId; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}

