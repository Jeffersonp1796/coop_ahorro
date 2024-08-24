package iti.Servicio;

import iti.connect.Connect;
import iti.modelo.Transaccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoopAhorro {

    // Registrar un usuario
        public void registrarUsuario(String nombre, String correo) throws SQLException {
            try (Connection conn = Connect.getConnection()) {
                String insertUsuario = "INSERT INTO usuarios (nombre, correo) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertUsuario, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, correo);
                    stmt.executeUpdate();

                    // Obtener el ID del usuario recién creado
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int usuarioId = generatedKeys.getInt(1);
                            crearCuentaParaUsuario(usuarioId);
                        }
                    }
                }
            }
        }

        // Crear una cuenta para un usuario recién registrado
        private void crearCuentaParaUsuario(int usuarioId) throws SQLException {
            try (Connection conn = Connect.getConnection()) {
                String insertCuenta = "INSERT INTO cuentas (saldo, usuario_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertCuenta)) {
                    stmt.setDouble(1, 0.0); // Saldo inicial 0
                    stmt.setInt(2, usuarioId);
                    stmt.executeUpdate();
                }
            }
        }

    // Realizar un depósito
    public void depositar(int cuentaId, double monto) throws SQLException {
        try (Connection conn = Connect.getConnection()) {
            String updateSaldo = "UPDATE cuentas SET saldo = saldo + ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSaldo)) {
                stmt.setDouble(1, monto);
                stmt.setInt(2, cuentaId);
                stmt.executeUpdate();
            }
            registrarTransaccion(cuentaId, cuentaId, monto, "DEPOSITO");
        }
    }

    // Transferencia entre dos cuentas
    public void transferir(int cuentaOrigenId, int cuentaDestinoId, double monto) throws SQLException {
        try (Connection conn = Connect.getConnection()) {
            conn.setAutoCommit(false);

            String updateOrigen = "UPDATE cuentas SET saldo = saldo - ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateOrigen)) {
                stmt.setDouble(1, monto);
                stmt.setInt(2, cuentaOrigenId);
                stmt.executeUpdate();
            }

            String updateDestino = "UPDATE cuentas SET saldo = saldo + ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateDestino)) {
                stmt.setDouble(1, monto);
                stmt.setInt(2, cuentaDestinoId);
                stmt.executeUpdate();
            }

            registrarTransaccion(cuentaOrigenId, cuentaDestinoId, monto, "TRANSFERENCIA");

            conn.commit();
        }
    }

    // Registrar una transacción en el historial
    private void registrarTransaccion(int cuentaOrigenId, int cuentaDestinoId, double monto, String tipo) throws SQLException {
        try (Connection conn = Connect.getConnection()) {
            String insertTransaccion = "INSERT INTO transacciones (cuenta_origen_id, cuenta_destino_id, monto, tipo, fecha) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertTransaccion)) {
                stmt.setInt(1, cuentaOrigenId);
                stmt.setInt(2, cuentaDestinoId);
                stmt.setDouble(3, monto);
                stmt.setString(4, tipo);
                stmt.setDate(5, new java.sql.Date(new Date().getTime()));
                stmt.executeUpdate();
            }
        }
    }

    // Historial de transacciones de una cuenta
    public List<Transaccion> obtenerHistorial(int cuentaId) throws SQLException {
        List<Transaccion> historial = new ArrayList<>();
        try (Connection conn = Connect.getConnection()) {
            String selectHistorial = "SELECT * FROM transacciones WHERE cuenta_origen_id = ? OR cuenta_destino_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectHistorial)) {
                stmt.setInt(1, cuentaId);
                stmt.setInt(2, cuentaId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Transaccion transaccion = new Transaccion(
                                rs.getInt("id"),
                                rs.getInt("cuenta_origen_id"),
                                rs.getInt("cuenta_destino_id"),
                                rs.getDouble("monto"),
                                rs.getString("tipo"),
                                rs.getDate("fecha")
                        );
                        historial.add(transaccion);
                    }
                }
            }
        }
        return historial;
    }

    // Saldo de una cuenta
    public double obtenerSaldo(int cuentaId) throws SQLException {
        double saldo = 0;
        try (Connection conn = Connect.getConnection()) {
            String selectSaldo = "SELECT saldo FROM cuentas WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectSaldo)) {
                stmt.setInt(1, cuentaId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        saldo = rs.getDouble("saldo");
                    }
                }
            }
        }
        return saldo;
    }
}

