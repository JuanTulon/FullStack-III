package com.mascotas.mascotas.util;

public class RutUtils {

    public static boolean validarFormatoRut(String rutCompleto) {
        return rutCompleto != null && rutCompleto.matches("^\\d{1,8}-[\\dKk]$");
    }

    public static boolean validarRut(String rutCompleto) {
        if (!validarFormatoRut(rutCompleto)) return false;
        String[] partes = rutCompleto.split("-");
        return validarDV(partes[0], partes[1].toUpperCase());
    }

    public static boolean validarDV(String run, String dvRut) {
        int suma = 0;
        int factor = 2;
        for (int i = run.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(run.charAt(i)) * factor;
            factor = (factor == 7) ? 2 : factor + 1;
        }
        int d = 11 - (suma % 11);
        String dvCalculado = (d == 10) ? "K" : ((d == 11) ? "0" : String.valueOf(d));
        return dvRut.equals(dvCalculado);
    }

    public static String calcularDV(int run) {
        int suma = 0;
        int factor = 2;
        String runStr = String.valueOf(run);
        for (int i = runStr.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(runStr.charAt(i)) * factor;
            factor = (factor == 7) ? 2 : factor + 1;
        }
        int d = 11 - (suma % 11);
        return (d == 10) ? "K" : ((d == 11) ? "0" : String.valueOf(d));
    }
}
