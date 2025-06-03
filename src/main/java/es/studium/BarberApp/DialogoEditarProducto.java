package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;

public class DialogoEditarProducto extends JDialog {
    private final JTextField txtNombre;
    private final JTextField txtPrecio;
    private final JTextField txtStock;
    private boolean confirmado = false;

    public DialogoEditarProducto(JFrame parent, ConexionBD conexion, Producto producto) {
        super(parent, "Editar Producto", true);
        setSize(450, 330);
        setLocationRelativeTo(parent);
        setLayout(null);

        JPanel panel = new JPanel(null) {
            private final Image imagenFondo = new ImageIcon("src/main/resources/es/studium/recursos/FondoApp.png").getImage();
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setBounds(0, 0, 450, 330);
        add(panel);

        VistaLogin.RoundedPanel panelContenido = new VistaLogin.RoundedPanel(30, new Color(255, 255, 255, 230));
        panelContenido.setLayout(null);
        panelContenido.setBounds(25, 25, 400, 250);
        panel.add(panelContenido);

        JLabel lblTitulo = new JLabel("Editar producto");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBounds(120, 10, 200, 30);
        panelContenido.add(lblTitulo);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 60, 100, 20);
        panelContenido.add(lblNombre);

        txtNombre = new JTextField(producto.getNombre());
        txtNombre.setBounds(130, 60, 220, 25);
        panelContenido.add(txtNombre);

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(30, 100, 100, 20);
        panelContenido.add(lblPrecio);

        txtPrecio = new JTextField(String.valueOf(producto.getPrecio()));
        txtPrecio.setBounds(130, 100, 220, 25);
        panelContenido.add(txtPrecio);

        JLabel lblStock = new JLabel("Stock:");
        lblStock.setBounds(30, 140, 100, 20);
        panelContenido.add(lblStock);

        txtStock = new JTextField(String.valueOf(producto.getStock()));
        txtStock.setBounds(130, 140, 220, 25);
        panelContenido.add(txtStock);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(130, 190, 100, 30);
        btnGuardar.addActionListener(e -> {
            try {
                String nuevoNombre = txtNombre.getText().trim();
                double nuevoPrecio = Double.parseDouble(txtPrecio.getText().trim());
                int nuevoStock = Integer.parseInt(txtStock.getText().trim());

                conexion.modificarArticulo(producto.getId(), nuevoNombre, nuevoPrecio, nuevoStock);
                confirmado = true;
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Revisa los valores numéricos.");
            }
        });
        panelContenido.add(btnGuardar);
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
