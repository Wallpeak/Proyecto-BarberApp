package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;

public class DialogoAgregarProducto extends JDialog {
    private final JTextField txtNombre, txtPrecio, txtStock;
    private boolean confirmado = false;
    private final ConexionBD conexion;

    public DialogoAgregarProducto(JFrame parent, ConexionBD conexion) {
        super(parent, "Agregar Producto", true);
        this.conexion = conexion;

        setSize(540, 380);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel de fondo con imagen
        JPanel fondo = new JPanel(new GridBagLayout()) {
            private final Image img = new ImageIcon("src/main/resources/es/studium/recursos/FondoApp.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        setContentPane(fondo);

        // Panel blanco translúcido y centrado
        VistaLogin.RoundedPanel panel = new VistaLogin.RoundedPanel(30, new Color(255, 255, 255, 200));
        panel.setPreferredSize(new Dimension(440, 300));
        panel.setLayout(null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        fondo.add(panel, gbc);

        // Título
        JLabel lblTitulo = new JLabel("Nuevo Producto");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBounds(140, 10, 200, 30);
        panel.add(lblTitulo);

        // Nombre
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(40, 60, 100, 20);
        panel.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(140, 60, 250, 25);
        panel.add(txtNombre);

        // Precio
        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(40, 100, 100, 20);
        panel.add(lblPrecio);

        txtPrecio = new JTextField();
        txtPrecio.setBounds(140, 100, 250, 25);
        panel.add(txtPrecio);

        // Stock
        JLabel lblStock = new JLabel("Stock:");
        lblStock.setBounds(40, 140, 100, 20);
        panel.add(lblStock);

        txtStock = new JTextField();
        txtStock.setBounds(140, 140, 250, 25);
        panel.add(txtStock);

        // Botones
        Color salmon = new Color(255, 140, 120);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(salmon);
        btnGuardar.setBounds(100, 200, 110, 30);
        panel.add(btnGuardar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(salmon);
        btnCancelar.setBounds(230, 200, 110, 30);
        panel.add(btnCancelar);

        // Acciones botones
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String precioStr = txtPrecio.getText().trim();
            String stockStr = txtStock.getText().trim();

            if (nombre.isEmpty() || !nombre.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ0-9\\s]{2,}$")) {
                JOptionPane.showMessageDialog(this, "Nombre inválido (mínimo 2 caracteres alfanuméricos).");
                return;
            }

            double precio;
            int stock;
            try {
                precio = Double.parseDouble(precioStr);
                if (precio <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introduce un precio válido (mayor que 0).");
                return;
            }

            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introduce un stock válido (0 o mayor).");
                return;
            }

            try {
                conexion.agregarArticulo(nombre, precio, stock);
                confirmado = true;

                JOptionPane.showMessageDialog(this,
                        "Producto agregado:\nNombre: " + nombre +
                        "\nPrecio: " + precio +
                        "\nStock: " + stock);

                if (getParent() instanceof VistaInicio vistaInicio) {
                    vistaInicio.actualizarTablasInicio();
                }

                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar producto: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
