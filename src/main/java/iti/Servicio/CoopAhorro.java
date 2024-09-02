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
        public void registrarUsuario(String nombre, String correo, String cedula) throws SQLException {
            try (Connection conn = Connect.getConnection()) {
                String insertUsuario = "INSERT INTO usuarios (nombre, correo, cedula) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertUsuario, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, correo);
                    stmt.setString(3, cedula);
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
        Connection conn = null;
        PreparedStatement updateOrigen = null;
        PreparedStatement updateDestino = null;
        PreparedStatement insertTransaccion = null;

        int consultaorigen = consultacuenta(cuentaOrigenId);
        int consultadestino = consultacuenta(cuentaDestinoId);

        if (consultaorigen == 0) {
            System.out.println("No existe cuenta origen");
        }

        if (consultadestino == 0) {
            System.out.println("No existe cuenta destino");
        }

        if (consultaorigen ==1 && consultadestino==1 ){
            try {
                conn = Connect.getConnection();
                conn.setAutoCommit(false); // Desactivar auto-commit para gestionar la transacción manualmente


                // 1. Verificar si la cuenta origen tiene saldo suficiente
                double saldoOrigen = obtenerSaldo(cuentaOrigenId);
                if (saldoOrigen < monto) {
                    throw new SQLException("Saldo insuficiente en la cuenta origen.");
                }

                // 2. Debitar el monto de la cuenta origen
                String actualizarCuentaOrigen = "UPDATE cuentas SET saldo = saldo - ? WHERE id = ?";
                updateOrigen = conn.prepareStatement(actualizarCuentaOrigen);
                updateOrigen.setDouble(1, monto);
                updateOrigen.setInt(2, cuentaOrigenId);
                updateOrigen.executeUpdate();

                // 3. Acreditar el monto en la cuenta destino
                String actualizarCuentaDestino = "UPDATE cuentas SET saldo = saldo + ? WHERE id = ?";
                updateDestino = conn.prepareStatement(actualizarCuentaDestino);
                updateDestino.setDouble(1, monto);
                updateDestino.setInt(2, cuentaDestinoId);
                updateDestino.executeUpdate();

                // 4. Registrar la transacción
                String registrarTransaccion = "INSERT INTO transacciones (cuenta_origen_id, cuenta_destino_id, monto, tipo) VALUES (?, ?, ?, ?)";
                insertTransaccion = conn.prepareStatement(registrarTransaccion);
                insertTransaccion.setInt(1, cuentaOrigenId);
                insertTransaccion.setInt(2, cuentaDestinoId);
                insertTransaccion.setDouble(3, monto);
                insertTransaccion.setString(4, "TRANSFERENCIA");
                insertTransaccion.executeUpdate();

                conn.commit(); // Confirmar la transacción

            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback(); // Revertir la transacción en caso de error
                }
                throw e;
            } finally {
                if (updateOrigen != null) {
                    updateOrigen.close();
                }
                if (updateDestino != null) {
                    updateDestino.close();
                }
                if (insertTransaccion != null) {
                    insertTransaccion.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar el auto-commit
                    conn.close();
                }
            }
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

    public int consultacuenta(int cuentaId) throws SQLException {
            int conteo=0;
        try (Connection conn = Connect.getConnection()) {
            String selectHistorial = "SELECT count(*) as conteo FROM cuentas WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectHistorial)) {
                stmt.setInt(1, cuentaId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                               conteo= rs.getInt("conteo");
                    }
                }
            }
        }
        return conteo;
    }


}

