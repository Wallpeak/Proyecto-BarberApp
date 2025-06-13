package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;

/**
 * Pequeño diálogo de confirmación de borrado de un producto.
 */
public class DialogoEliminarProducto extends JDialog {
    private boolean confirmado = false;

    public DialogoEliminarProducto(JFrame parent, ConexionBD conexion, int productoId, String descripcion) {
        super(parent, "Eliminar Producto", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel fondo con imagen y GridBagLayout para centrar panelContenido
        JPanel panel = new JPanel(new GridBagLayout()) {
            private final Image imagenFondo = new ImageIcon(
                "src/main/resources/es/studium/recursos/FondoApp.png"
            ).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Panel interior redondeado y semitransparente
        VistaLogin.RoundedPanel panelContenido = new VistaLogin.RoundedPanel(30, new Color(255, 255, 255, 230));
        panelContenido.setPreferredSize(new Dimension(350, 130));
        panelContenido.setLayout(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(panelContenido, gbc);

        // Mensaje
        JLabel lblMensaje = new JLabel("<html>¿Deseas eliminar el producto:<br><b>" + descripcion + "</b>?</html>");
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
                conexion.eliminarArticulo(productoId);
                confirmado = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + ex.getMessage());
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

        setContentPane(panel);
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
