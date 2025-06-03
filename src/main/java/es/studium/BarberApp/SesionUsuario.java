package es.studium.BarberApp;

public class SesionUsuario {
    public static String nombreUsuario = null;
    public static int usuarioId = -1;

    public static void cerrarSesion() {
        nombreUsuario = null;
        usuarioId = -1;
    }
}
