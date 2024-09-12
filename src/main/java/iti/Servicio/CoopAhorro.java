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

        public void registrarUsuario(String nombre, String correo, String cedula) throws SQLException {
            try (Connection conn = Connect.getConnection()) {
                String insertUsuario = "INSERT INTO usuarios (nombre, correo, cedula) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertUsuario, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, correo);
                    stmt.setString(3, cedula);
                    stmt.executeUpdate();

                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int usuarioId = generatedKeys.getInt(1);
                            crearCuentaParaUsuario(usuarioId);
                        }
                    }
                }
            }
        }

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
                conn.setAutoCommit(false);


                double saldoOrigen = obtenerSaldo(cuentaOrigenId);
                if (saldoOrigen < monto) {
                    throw new SQLException("Saldo insuficiente en la cuenta origen.");
                }

                String actualizarCuentaOrigen = "UPDATE cuentas SET saldo = saldo - ? WHERE id = ?";
                updateOrigen = conn.prepareStatement(actualizarCuentaOrigen);
                updateOrigen.setDouble(1, monto);
                updateOrigen.setInt(2, cuentaOrigenId);
                updateOrigen.executeUpdate();

                String actualizarCuentaDestino = "UPDATE cuentas SET saldo = saldo + ? WHERE id = ?";
                updateDestino = conn.prepareStatement(actualizarCuentaDestino);
                updateDestino.setDouble(1, monto);
                updateDestino.setInt(2, cuentaDestinoId);
                updateDestino.executeUpdate();

                String registrarTransaccion = "INSERT INTO transacciones (cuenta_origen_id, cuenta_destino_id, monto, tipo) VALUES (?, ?, ?, ?)";
                insertTransaccion = conn.prepareStatement(registrarTransaccion);
                insertTransaccion.setInt(1, cuentaOrigenId);
                insertTransaccion.setInt(2, cuentaDestinoId);
                insertTransaccion.setDouble(3, monto);
                insertTransaccion.setString(4, "TRANSFERENCIA");
                insertTransaccion.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
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
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }
        }

    }


    public void editarUsuario(int usuarioId, String nuevoNombre, String nuevoCorreo, String nuevaCedula) throws SQLException {
        try (Connection conn = Connect.getConnection()) {
            StringBuilder updateQuery = new StringBuilder("UPDATE usuarios SET ");
            boolean hasPrevious = false;

            if (nuevoNombre != null) {
                updateQuery.append("nombre = ?");
                hasPrevious = true;
            }

            if (nuevoCorreo != null) {
                if (hasPrevious) updateQuery.append(", ");
                updateQuery.append("correo = ?");
                hasPrevious = true;
            }

            if (nuevaCedula != null) {
                if (hasPrevious) updateQuery.append(", ");
                updateQuery.append("cedula = ?");
            }

            updateQuery.append(" WHERE id = ?");

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery.toString())) {
                int index = 1;

                if (nuevoNombre != null) {
                    stmt.setString(index++, nuevoNombre);
                }
                if (nuevoCorreo != null) {
                    stmt.setString(index++, nuevoCorreo);
                }
                if (nuevaCedula != null) {
                    stmt.setString(index++, nuevaCedula);
                }

                stmt.setInt(index, usuarioId);

                stmt.executeUpdate();
            }
        }
    }


    public void eliminarUsuario(int usuarioId) throws SQLException {
        Connection conn = null;
        PreparedStatement deleteCuentas = null;
        PreparedStatement deleteUsuario = null;

        try {
            conn = Connect.getConnection();
            conn.setAutoCommit(false);

            String deleteCuentasSQL = "DELETE FROM cuentas WHERE usuario_id = ?";
            deleteCuentas = conn.prepareStatement(deleteCuentasSQL);
            deleteCuentas.setInt(1, usuarioId);
            deleteCuentas.executeUpdate();


            String deleteUsuarioSQL = "DELETE FROM usuarios WHERE id = ?";
            deleteUsuario = conn.prepareStatement(deleteUsuarioSQL);
            deleteUsuario.setInt(1, usuarioId);
            int filasAfectadas = deleteUsuario.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el usuario con ID: " + usuarioId);
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (deleteCuentas != null) {
                deleteCuentas.close();
            }
            if (deleteUsuario != null) {
                deleteUsuario.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }




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

