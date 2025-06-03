package es.studium.BarberApp;

import javax.swing.SwingUtilities;

public class BarberApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crea la vista principal (JFrame)
            VistaInicio vistaInicio = new VistaInicio();

            // Mostrar la aplicación (ya contiene el panel de login al inicio)
            vistaInicio.setVisible(true);
        });
    }
}
