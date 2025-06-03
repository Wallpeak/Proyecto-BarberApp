package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;

public class DialogoAgregarProducto extends JDialog {
    private JTextField txtNombre, txtPrecio, txtStock;
    private JButton btnGuardar;
    private final ConexionBD conexion;

    public DialogoAgregarProducto(JFrame parent, ConexionBD conexion) {
        super(parent, "Agregar producto", true);
        this.conexion = conexion;
        setSize(420, 300);
        setLocationRelativeTo(parent);
        setLayout(null);
        setUndecorated(true);

        JPanel fondo = new JPanel(null) {
            private final Image img = new ImageIcon("src/main/resources/es/studium/recursos/FondoApp.png").getImage();
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        fondo.setBounds(0, 0, 420, 300);
        add(fondo);

        JPanel panel = new VistaLogin.RoundedPanel(40, new Color(255, 255, 255, 230));
        panel.setLayout(null);
        panel.setBounds(10, 10, 400, 280);
        fondo.add(panel);

        panel.add(new JLabel("Nombre:")).setBounds(30, 30, 100, 25);
        txtNombre = new JTextField(); panel.add(txtNombre).setBounds(140, 30, 200, 25);

        panel.add(new JLabel("Precio:")).setBounds(30, 70, 100, 25);
        txtPrecio = new JTextField(); panel.add(txtPrecio).setBounds(140, 70, 200, 25);

        panel.add(new JLabel("Stock:")).setBounds(30, 110, 100, 25);
        txtStock = new JTextField(); panel.add(txtStock).setBounds(140, 110, 200, 25);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(140, 160, 100, 30);
        panel.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText();
                double precio = Double.parseDouble(txtPrecio.getText());
                int stock = Integer.parseInt(txtStock.getText());
                conexion.agregarArticulo(nombre, precio, stock);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos");
            }
        });
    }
}
