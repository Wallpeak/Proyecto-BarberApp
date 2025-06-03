// DialogoEliminarProducto.java
package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;

/**
 * Pequeño diálogo de confirmación de borrado de un producto.
 */
public class DialogoEliminarProducto extends JDialog {
    private boolean confirmado = false;

    /**
     * Constructor para confirmar el borrado de un producto.
     *
     * @param parent      Ventana padre (usualmente el JFrame contenedor).
     * @param conexion    Instancia de ConexionBD.
     * @param productoId  ID del producto a eliminar.
     * @param descripcion Texto descriptivo (por ejemplo "Camiseta – S" o el nombre del producto).
     */
    public DialogoEliminarProducto(JFrame parent, ConexionBD conexion, int productoId, String descripcion) {
        super(parent, "Eliminar Producto", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(null);

        // Panel fondo con imagen
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

        // Panel interior redondeado semitransparente
        VistaLogin.RoundedPanel panelContenido = new VistaLogin.RoundedPanel(
            30, new Color(255, 255, 255, 230)
        );
        panelContenido.setLayout(null);
        panelContenido.setBounds(25, 25, 350, 130);
        panel.add(panelContenido);

        // Etiqueta de pregunta con la descripción del producto
        JLabel lblMensaje = new JLabel(
            "<html>¿Deseas eliminar el producto:<br><b>" + descripcion + "</b>?</html>"
        );
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMensaje.setBounds(20, 15, 310, 50);
        panelContenido.add(lblMensaje);

        // Botón “Eliminar”
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(60, 70, 100, 30);
        btnEliminar.addActionListener(e -> {
            try {
                conexion.eliminarArticulo(productoId);
                confirmado = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error al eliminar producto: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        panelContenido.add(btnEliminar);

        // Botón “Cancelar”
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(180, 70, 100, 30);
        btnCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });
        panelContenido.add(btnCancelar);
    }

    /**
     * @return true si el usuario confirmó el borrado; false en caso contrario.
     */
    public boolean isConfirmado() {
        return confirmado;
    }
}
