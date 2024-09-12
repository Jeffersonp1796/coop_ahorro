package iti;

import iti.Servicio.CoopAhorro;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class MainGra {

    private static CoopAhorro coopAhorro = new CoopAhorro();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cooperativa de Ahorro");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new GridLayout(6, 1));

        JButton btnGestionarUsuarios = new JButton("Gestionar Usuarios");
        JButton btnRealizarDeposito = new JButton("Realizar Depósito");
        JButton btnRealizarTransferencia = new JButton("Realizar Transferencia");
        JButton btnConsultarSaldo = new JButton("Consultar Saldo");
        JButton btnConsultarHistorial = new JButton("Consultar Historial");
        JButton btnSalir = new JButton("Salir");

        frame.add(btnGestionarUsuarios);
        frame.add(btnRealizarDeposito);
        frame.add(btnRealizarTransferencia);
        frame.add(btnConsultarSaldo);
        frame.add(btnConsultarHistorial);
        frame.add(btnSalir);

        btnGestionarUsuarios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gestionarUsuarios();
            }
        });

        btnRealizarDeposito.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                realizarDeposito();
            }
        });

        btnRealizarTransferencia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                realizarTransferencia();
            }
        });

        btnConsultarSaldo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                consultarSaldo();
            }
        });

        btnConsultarHistorial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                consultarHistorial();
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }

    private static void gestionarUsuarios() {

        String[] opciones = {"Volver al Menú Principal", "Eliminar Usuario", "Editar Usuario", "Crear Usuario"};

            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una opción",
                    "Gestión de Usuarios",
                    0,
                    0,
                    new ImageIcon("/Users/jeffersonchiliguano/Desktop/usuario1.png"),
                    opciones,
                    opciones[0]
            );

            switch (seleccion) {
                case 3:
                    crearUsuario();
                    break;
                case 2:
                    editarUsuario();
                    break;
                case 1:
                    eliminarUsuario();
                    break;
                case 0:
                    return;
            }
    }


    private static void crearUsuario() {
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre del usuario:");
        String correo = JOptionPane.showInputDialog("Ingrese el correo del usuario:");
        String cedula = obtenerCedulaValida();

        if (nombre != null && correo != null && cedula != null) {
            try {
                coopAhorro.registrarUsuario(nombre, correo, cedula);
                JOptionPane.showMessageDialog(null, "Usuario registrado con éxito.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        }
    }

    private static void editarUsuario() {
        String usuarioIdStr = JOptionPane.showInputDialog("Ingrese el ID del usuario a editar:");
        if (usuarioIdStr != null) {
            try {
                int usuarioId = Integer.parseInt(usuarioIdStr);
                String nombre = JOptionPane.showInputDialog("Ingrese el nuevo nombre (deje en blanco para no cambiar):");
                String correo = JOptionPane.showInputDialog("Ingrese el nuevo correo (deje en blanco para no cambiar):");
                String cedula = obtenerCedulaValida();

                coopAhorro.editarUsuario(
                        usuarioId,
                        nombre != null && !nombre.isEmpty() ? nombre : null,
                        correo != null && !correo.isEmpty() ? correo : null,
                        cedula != null && !cedula.isEmpty() ? cedula : null
                );

                JOptionPane.showMessageDialog(null, "Usuario editado con éxito.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ID inválido.");
            }
        }
    }

    private static String obtenerCedulaValida() {
        String cedula;
        while (true) {
            cedula = JOptionPane.showInputDialog("Ingrese la cédula del usuario (10 dígitos):");
            if (cedula != null && validarCedula(cedula)) {
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Cédula inválida. Debe contener exactamente 10 dígitos numéricos.");
            }
        }
        return cedula;
    }

    private static boolean validarCedula(String cedula) {
        return cedula.matches("\\d{10}");
    }

    private static void eliminarUsuario() {
        String usuarioIdStr = JOptionPane.showInputDialog("Ingrese el ID del usuario a eliminar:");

        if (usuarioIdStr != null) {
            try {

                int usuarioId = Integer.parseInt(usuarioIdStr);

                int confirmacion = JOptionPane.showConfirmDialog(
                        null,
                        "¿Está seguro de que desea eliminar el usuario con ID " + usuarioId + "?",
                        "Confirmación de eliminación",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmacion == JOptionPane.YES_OPTION) {
                    coopAhorro.eliminarUsuario(usuarioId);
                    JOptionPane.showMessageDialog(null, "Usuario eliminado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(null, "Operación cancelada.");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ID inválido. Ingrese un número entero.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada.");
        }
    }


    private static void realizarDeposito() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField cuentaIdField = new JTextField();
        panel.add(new JLabel("Ingrese el ID de la cuenta:"));
        panel.add(cuentaIdField);

        JTextField montoField = new JTextField();
        panel.add(new JLabel("Ingrese el monto a depositar:"));
        panel.add(montoField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Realizar Depósito", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                if (cuentaIdField.getText().isEmpty() || montoField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
                    return;
                }

                int cuentaId = Integer.parseInt(cuentaIdField.getText());
                double monto = Double.parseDouble(montoField.getText());

                coopAhorro.depositar(cuentaId, monto);

                JOptionPane.showMessageDialog(null, "Depósito realizado con éxito.");

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Por favor, ingrese un ID y un monto válidos.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al realizar el depósito: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada.");
        }
    }



    private static void realizarTransferencia() {
        String cuentaOrigenId = JOptionPane.showInputDialog("Ingrese el ID de la cuenta origen:");
        String cuentaDestinoId = JOptionPane.showInputDialog("Ingrese el ID de la cuenta destino:");
        String monto = JOptionPane.showInputDialog("Ingrese el monto a transferir:");

        try {
            if (cuentaOrigenId == null || cuentaDestinoId == null || monto == null) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                return;
            }

            int cuentaOrigen = Integer.parseInt(cuentaOrigenId);
            int cuentaDestino = Integer.parseInt(cuentaDestinoId);
            double montoTransferir = Double.parseDouble(monto);

            if (montoTransferir <= 0) {
                JOptionPane.showMessageDialog(null, "El monto a transferir debe ser mayor que cero.");
                return;
            }

            int consultaOrigen = coopAhorro.consultacuenta(cuentaOrigen);
            int consultaDestino = coopAhorro.consultacuenta(cuentaDestino);

            if (consultaOrigen == 0) {
                JOptionPane.showMessageDialog(null, "No existe la cuenta origen.");
                return;
            }

            if (consultaDestino == 0) {
                JOptionPane.showMessageDialog(null, "No existe la cuenta destino.");
                return;
            }

            double saldoOrigen = coopAhorro.obtenerSaldo(cuentaOrigen);
            if (saldoOrigen < montoTransferir) {
                JOptionPane.showMessageDialog(null, "Saldo insuficiente en la cuenta origen.");
                return;
            }

            coopAhorro.transferir(cuentaOrigen, cuentaDestino, montoTransferir);
            JOptionPane.showMessageDialog(null, "Transferencia realizada con éxito.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor ingrese valores numéricos válidos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la transferencia: " + e.getMessage());
        }
    }


    private static void consultarSaldo() {
        String cuentaId = JOptionPane.showInputDialog("Ingrese el ID de la cuenta:");

        try {
            double saldo = coopAhorro.obtenerSaldo(Integer.parseInt(cuentaId));
            JOptionPane.showMessageDialog(null, "El saldo de la cuenta es: " + saldo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private static void consultarHistorial() {
        String cuentaId = JOptionPane.showInputDialog("Ingrese el ID de la cuenta:");

        try {
            var historial = coopAhorro.obtenerHistorial(Integer.parseInt(cuentaId));
            if (historial.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay transacciones para esta cuenta.");
            } else {
                JOptionPane.showMessageDialog(null, historial.toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}
