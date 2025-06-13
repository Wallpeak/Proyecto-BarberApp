package es.studium.BarberApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DialogoEditarCita extends JDialog {
    private final JTextField txtCliente;
    private final JComboBox<String> cbServicio;
    private JComboBox<String> cbHora;
    private boolean confirmado = false;

    public DialogoEditarCita(JFrame parent, ConexionBD conexion, Cita cita) {
        super(parent, "Editar Cita", true);
        setSize(500, 380);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Panel con fondo personalizado
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

        // Panel translúcido principal
        VistaLogin.RoundedPanel panelContenido = new VistaLogin.RoundedPanel(30, new Color(255, 255, 255, 200));
        panelContenido.setLayout(new GridBagLayout());
        panelContenido.setPreferredSize(new Dimension(440, 300));
        panelContenido.setOpaque(false);
        panelContenido.setBorder(new EmptyBorder(20, 30, 20, 30));
        fondo.add(panelContenido);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Título
        JLabel lblTitulo = new JLabel("Editar Cita");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelContenido.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Fecha (formateada)
        gbc.gridy++;
        panelContenido.add(new JLabel("Fecha:"), gbc);

        gbc.gridx = 1;
        LocalDate fecha = LocalDate.parse(cita.getFecha(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JLabel lblFecha = new JLabel(fechaFormateada);
        panelContenido.add(lblFecha, gbc);

        // Cliente
        gbc.gridy++;
        gbc.gridx = 0;
        panelContenido.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        txtCliente = new JTextField(cita.getClienteNombre());
        txtCliente.setPreferredSize(new Dimension(200, 25));
        txtCliente.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panelContenido.add(txtCliente, gbc);

     // Servicio
        gbc.gridy++;
        gbc.gridx = 0;
        panelContenido.add(new JLabel("Servicio:"), gbc);

        gbc.gridx = 1;
        cbServicio = new JComboBox<>();
        List<Map<String, Object>> servicios = conexion.obtenerServicios();
        for (Map<String, Object> servicio : servicios) {
            cbServicio.addItem((String) servicio.get("nombre"));
        }
        // Seleccionar el servicio actual de la cita
        for (int i = 0; i < servicios.size(); i++) {
            if (servicios.get(i).get("nombre").equals(cita.getServicioNombre())) {
                cbServicio.setSelectedIndex(i);
                break;
            }
        }
        panelContenido.add(cbServicio, gbc);

        // Hora
        gbc.gridy++;
        gbc.gridx = 0;
        panelContenido.add(new JLabel("Hora:"), gbc);

        gbc.gridx = 1;
        cbHora = new JComboBox<>();
        String horaOriginal = cita.getHora();
        cbHora.addItem(horaOriginal); // Añadir hora actual

        List<java.time.LocalTime> horasDisponibles = conexion.consultarHorasDisponibles(cita.getFecha());
        for (java.time.LocalTime hora : horasDisponibles) {
            String hs = hora.toString();
            if (!hs.equals(horaOriginal)) {
                cbHora.addItem(hs);
            }
        }
        panelContenido.add(cbHora, gbc);

        // Botones
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);
        Color salmon = new Color(255, 140, 120);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(salmon);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.addActionListener(e -> {
            String cliente = txtCliente.getText().trim();
            String servicioNombre = (String) cbServicio.getSelectedItem();
            String horaSel = (String) cbHora.getSelectedItem();

            if (cliente.isEmpty() || !cliente.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ\\s]{3,}$")) {
                JOptionPane.showMessageDialog(this, "Nombre de cliente inválido.");
                return;
            }
            if (horaSel == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una hora.");
                return;
            }

            try {
                int servicioId = cbServicio.getSelectedIndex() + 1;

                conexion.modificarCita(
                	    cita.getId(),
                	    cliente,
                	    servicioId,
                	    cita.getFecha(),
                	    horaSel,
                	    SesionUsuario.usuarioId // <-- aquí debe ir el ID del usuario autenticado
                	);


                confirmado = true;
                JOptionPane.showMessageDialog(this, "Cita Modificada con exito.");

                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar cita: " + ex.getMessage());
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
