package es.studium.BarberApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogoEditarProducto extends JDialog {
    private final JTextField txtNombre, txtPrecio, txtStock;
    private boolean confirmado = false;
    private final ConexionBD conexion;
    private final Producto producto;

    public DialogoEditarProducto(JFrame parent, ConexionBD conexion, Producto producto) {
        super(parent, "Editar Producto", true);
        this.conexion = conexion;
        this.producto = producto;

        setSize(500, 380);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        // Fondo con imagen
        JPanel fondo = new JPanel() {
            private final Image fondoImg = new ImageIcon("src/main/resources/es/studium/recursos/FondoApp.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondoImg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        fondo.setLayout(new GridBagLayout());
        add(fondo, BorderLayout.CENTER);

        // Panel central translúcido con esquinas redondeadas
        VistaLogin.RoundedPanel panelContenido = new VistaLogin.RoundedPanel(30, new Color(255, 255, 255, 200));
        panelContenido.setLayout(new GridBagLayout());
        panelContenido.setPreferredSize(new Dimension(440, 300));
        panelContenido.setOpaque(false);
        panelContenido.setBorder(new EmptyBorder(20, 30, 20, 30));
        fondo.add(panelContenido);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Título centrado
        JLabel lblTitulo = new JLabel("Editar Producto");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelContenido.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Etiqueta Nombre
        gbc.gridy++;
        panelContenido.add(new JLabel("Nombre:"), gbc);

        // Campo Nombre
        gbc.gridx = 1;
        txtNombre = new JTextField(producto.getNombre());
        txtNombre.setPreferredSize(new Dimension(200, 25));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panelContenido.add(txtNombre, gbc);

        // Etiqueta Precio
        gbc.gridy++;
        gbc.gridx = 0;
        panelContenido.add(new JLabel("Precio:"), gbc);

        // Campo Precio
        gbc.gridx = 1;
        txtPrecio = new JTextField(String.valueOf(producto.getPrecio()));
        txtPrecio.setPreferredSize(new Dimension(200, 25));
        txtPrecio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panelContenido.add(txtPrecio, gbc);

        // Etiqueta Stock
        gbc.gridy++;
        gbc.gridx = 0;
        panelContenido.add(new JLabel("Stock:"), gbc);

        // Campo Stock
        gbc.gridx = 1;
        txtStock = new JTextField(String.valueOf(producto.getStock()));
        txtStock.setPreferredSize(new Dimension(200, 25));
        txtStock.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panelContenido.add(txtStock, gbc);

        // Botones guardar y cancelar en FlowLayout centrado
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);

        Color salmon = new Color(255, 140, 120);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(salmon);
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
                conexion.modificarArticulo(producto.getId(), nombre, precio, stock);
                confirmado = true;
                if (getParent() instanceof VistaInicio vistaInicio) {
                    vistaInicio.actualizarTablasInicio();
                }
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar cambios: " + ex.getMessage());
            }
        });
        panelBotones.add(btnGuardar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(salmon);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);

        panelContenido.add(panelBotones, gbc);
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
