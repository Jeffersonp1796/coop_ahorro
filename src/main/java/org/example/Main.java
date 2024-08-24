package org.example;

import Servicio.CoopAhorro;

public class Main {
    public static void main(String[] args) {
        CoopAhorro coopAhorro = new CoopAhorro();

        try {
            // Ejemplo de depósito
            coopAhorro.depositar(1, 500.0);

            // Ejemplo de transferencia
            coopAhorro.transferir(1, 2, 200.0);

            System.out.println("Operaciones realizadas con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


