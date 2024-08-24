package iti;

import iti.Servicio.CoopAhorro;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        CoopAhorro coopAhorro = new CoopAhorro();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=== Cooperativa de Ahorro ===");
            System.out.println("1. Registrar Usuario");
            System.out.println("2. Realizar Depósito");
            System.out.println("3. Realizar Transferencia");
            System.out.println("4. Consultar Saldo");
            System.out.println("5. Consultar Historial");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir la nueva línea

            try {
                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese el nombre del usuario: ");
                        String nombre = scanner.nextLine();
                        System.out.print("Ingrese el correo del usuario: ");
                        String correo = scanner.nextLine();
                        coopAhorro.registrarUsuario(nombre, correo);
                        System.out.println("Usuario registrado con éxito.");
                        break;
                    case 2:
                        System.out.print("Ingrese el ID de la cuenta: ");
                        int cuentaId = scanner.nextInt();
                        System.out.print("Ingrese el monto a depositar: ");
                        double montoDeposito = scanner.nextDouble();
                        coopAhorro.depositar(cuentaId, montoDeposito);
                        System.out.println("Depósito realizado con éxito.");
                        break;
                    case 3:
                        System.out.print("Ingrese el ID de la cuenta origen: ");
                        int cuentaOrigenId = scanner.nextInt();
                        System.out.print("Ingrese el ID de la cuenta destino: ");
                        int cuentaDestinoId = scanner.nextInt();
                        System.out.print("Ingrese el monto a transferir: ");
                        double montoTransferencia = scanner.nextDouble();
                        coopAhorro.transferir(cuentaOrigenId, cuentaDestinoId, montoTransferencia);
                        System.out.println("Transferencia realizada con éxito.");
                        break;
                    case 4:
                        System.out.print("Ingrese el ID de la cuenta: ");
                        int cuentaSaldoId = scanner.nextInt();
                        double saldo = coopAhorro.obtenerSaldo(cuentaSaldoId);
                        System.out.println("El saldo de la cuenta es: " + saldo);
                        break;
                    case 5:
                        System.out.print("Ingrese el ID de la cuenta: ");
                        int cuentaHistorialId = scanner.nextInt();
                        var historial = coopAhorro.obtenerHistorial(cuentaHistorialId);
                        historial.forEach(System.out::println);
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
}


