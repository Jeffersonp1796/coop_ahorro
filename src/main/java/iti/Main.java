package iti;

import iti.Servicio.CoopAhorro;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private static CoopAhorro coopAhorro = new CoopAhorro();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("=== Cooperativa de Ahorro ===");
            System.out.println("1. Gestionar Usuarios");
            System.out.println("2. Realizar Depósito");
            System.out.println("3. Realizar Transferencia");
            System.out.println("4. Consultar Saldo");
            System.out.println("5. Consultar Historial");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            try {
                switch (opcion) {
                    case 1:
                        gestionarUsuarios();
                        break;
                    case 2:
                        realizarDeposito();
                        break;
                    case 3:
                        realizarTransferencia();
                        break;
                    case 4:
                        consultarSaldo();
                        break;
                    case 5:
                        consultarHistorial();
                        break;
                    case 6:
                        System.out.println("Saliendo...");
                        return;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void gestionarUsuarios() throws SQLException {
        while (true) {
            System.out.println("=== Gestión de Usuarios ===");
            System.out.println("1. Crear Usuario");
            System.out.println("2. Editar Usuario");
            System.out.println("3. Eliminar Usuario");
            System.out.println("4. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    crearUsuario();
                    break;
                case 2:
                    editarUsuario();
                    break;
                case 3:
                    eliminarUsuario();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void crearUsuario() throws SQLException {
        System.out.print("Ingrese el nombre del usuario: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese el correo del usuario: ");
        String correo = scanner.nextLine();
        System.out.print("Ingrese la cédula del usuario: ");
        String cedula = scanner.nextLine();

        coopAhorro.registrarUsuario(nombre, correo, cedula);
        System.out.println("Usuario registrado con éxito.");
    }

    private static void editarUsuario() throws SQLException {
        System.out.print("Ingrese el ID del usuario a editar: ");
        int usuarioId = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        System.out.print("Ingrese el nuevo nombre (deje en blanco para no cambiar): ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese el nuevo correo (deje en blanco para no cambiar): ");
        String correo = scanner.nextLine();
        System.out.print("Ingrese la nueva cédula (deje en blanco para no cambiar): ");
        String cedula = scanner.nextLine();

        coopAhorro.editarUsuario(usuarioId,
                nombre.isEmpty() ? null : nombre,
                correo.isEmpty() ? null : correo,
                cedula.isEmpty() ? null : cedula);

        System.out.println("Usuario editado con éxito.");
    }


    private static void eliminarUsuario() throws SQLException {
        System.out.print("Ingrese el ID del usuario a eliminar: ");
        int usuarioId = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        coopAhorro.eliminarUsuario(usuarioId);
        System.out.println("Usuario eliminado con éxito.");
    }

    private static void realizarDeposito() throws SQLException {
        System.out.print("Ingrese el ID de la cuenta: ");
        int cuentaId = scanner.nextInt();
        System.out.print("Ingrese el monto a depositar: ");
        double monto = scanner.nextDouble();
        scanner.nextLine(); // Limpiar el buffer

        coopAhorro.depositar(cuentaId, monto);
        System.out.println("Depósito realizado con éxito.");
    }

    private static void realizarTransferencia() throws SQLException {
        System.out.print("Ingrese el ID de la cuenta origen: ");
        int cuentaOrigenId = scanner.nextInt();
        System.out.print("Ingrese el ID de la cuenta destino: ");
        int cuentaDestinoId = scanner.nextInt();
        System.out.print("Ingrese el monto a transferir: ");
        double monto = scanner.nextDouble();
        scanner.nextLine(); // Limpiar el buffer

        coopAhorro.transferir(cuentaOrigenId, cuentaDestinoId, monto);
        System.out.println("Transferencia realizada con éxito.");
    }

    private static void consultarSaldo() throws SQLException {
        System.out.print("Ingrese el ID de la cuenta: ");
        int cuentaId = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        double saldo = coopAhorro.obtenerSaldo(cuentaId);
        System.out.println("El saldo de la cuenta es: " + saldo);
    }

    private static void consultarHistorial() throws SQLException {
        System.out.print("Ingrese el ID de la cuenta: ");
        int cuentaId = scanner.nextInt();
        scanner.nextLine();

        var historial = coopAhorro.obtenerHistorial(cuentaId);
        if (historial.isEmpty()) {
            System.out.println("No hay transacciones para esta cuenta.");
        } else {
            historial.forEach(System.out::println);
        }
    }
}
