package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;

/**
 * Pequeño diálogo de confirmación de borrado de cita.
 */
public class DialogoEliminarCita extends JDialog {
    private boolean confirmado = false;

    /**
     * Constructor para confirmar el borrado de una cita.
     * @param parent    Ventana padre.
     * @param conexion  Instancia de ConexionBD.
     * @param citaId    ID de la cita a eliminar.
     * @param descripcion Texto descriptivo (por ejemplo “Juan Pérez – Corte” a las 10:00).
     */
    public DialogoEliminarCita(JFrame parent, ConexionBD conexion, int citaId, String descripcion) {
        super(parent, "Eliminar Cita", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        // Panel de fondo con imagen
        JPanel panel = new JPanel(null) {
            private final Image imagenFondo = new ImageIcon(
                "src/main/resources/es/studium/recursos/FondoApp.png"
            ).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setBounds(0, 0, 400, 200);
        add(panel);

        // Panel interior redondeado translúcido
        VistaLogin.RoundedPanel panelContenido = new VistaLogin.RoundedPanel(30, new Color(255, 255, 255, 230));
        panelContenido.setLayout(null);
        panelContenido.setBounds(25, 25, 350, 130);
        panel.add(panelContenido);

        // Mensaje
        JLabel lblMensaje = new JLabel("<html>¿Deseas eliminar la cita:<br><b>" + descripcion + "</b>?</html>");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMensaje.setBounds(20, 15, 310, 50);
        panelContenido.add(lblMensaje);

        // Botón “Eliminar”
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(60, 80, 100, 35);
        btnEliminar.setBackground(new Color(255, 153, 153));
        btnEliminar.setForeground(Color.BLACK);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminar.addActionListener(e -> {
            try {
                conexion.eliminarCita(citaId);
                confirmado = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cita: " + ex.getMessage());
            }
        });
        panelContenido.add(btnEliminar);

        // Botón “Cancelar”
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(190, 80, 100, 35);
        btnCancelar.setBackground(new Color(255, 153, 153));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });
        panelContenido.add(btnCancelar);
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
