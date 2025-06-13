package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DialogoCrearCita extends JDialog {
    private final JTextField txtCliente;
    private final JComboBox<String> cbServicio;
    private final JComboBox<String> cbHora;
    private boolean confirmado = false;
    private ConexionBD conexion;
    private LocalDate fecha;
    private int usuarioId;
	private VistaCitas vistaCitas;
	private VistaInicio vistaInicio;

    public DialogoCrearCita(JFrame parent, ConexionBD conexion, LocalDate fecha, int usuarioId, VistaCitas vistaCitas) {
        super(parent, "Crear Cita", true);
        this.conexion = conexion;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
        this.vistaCitas = vistaCitas;  
        this.vistaInicio = vistaInicio;        

        this.conexion = conexion;
        this.fecha = fecha;
        this.usuarioId = usuarioId;

        setSize(540, 380);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Fondo con imagen
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
        JLabel lblTitulo = new JLabel("Nueva Cita");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBounds(160, 10, 150, 30);
        panel.add(lblTitulo);

        // Fecha fija (no editable)
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setBounds(40, 60, 100, 20);
        panel.add(lblFecha);

        JLabel lblFechaValor = new JLabel(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblFechaValor.setBounds(140, 60, 250, 25);
        panel.add(lblFechaValor);

        // Cliente
        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setBounds(40, 100, 100, 20);
        panel.add(lblCliente);

        txtCliente = new JTextField();
        txtCliente.setBounds(140, 100, 250, 25);
        panel.add(txtCliente);

        // Servicio
        JLabel lblServicio = new JLabel("Servicio:");
        lblServicio.setBounds(40, 140, 100, 20);
        panel.add(lblServicio);

        cbServicio = new JComboBox<>();
        List<Map<String, Object>> servicios = conexion.obtenerServicios();
        for (Map<String, Object> servicio : servicios) {
            cbServicio.addItem(servicio.get("nombre").toString());
        }
        cbServicio.setBounds(140, 140, 250, 25);
        panel.add(cbServicio);

        // Hora
        JLabel lblHora = new JLabel("Hora:");
        lblHora.setBounds(40, 180, 100, 20);
        panel.add(lblHora);

        cbHora = new JComboBox<>();
        String fechaParaConsulta = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<java.time.LocalTime> horasDisponibles = conexion.consultarHorasDisponibles(fechaParaConsulta);
        for (java.time.LocalTime lt : horasDisponibles) {
            cbHora.addItem(lt.toString());
        }
        cbHora.setBounds(140, 180, 250, 25);
        panel.add(cbHora);

        // Botones
        Color salmon = new Color(255, 140, 120);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(salmon);
        btnGuardar.setBounds(100, 240, 110, 30);
        panel.add(btnGuardar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(salmon);
        btnCancelar.setBounds(230, 240, 110, 30);
        panel.add(btnCancelar);

        // Acción guardar
        btnGuardar.addActionListener(e -> {
            String cliente = txtCliente.getText().trim();
            String servicio = (String) cbServicio.getSelectedItem();
            String horaSel = (String) cbHora.getSelectedItem();

            if (cliente.isEmpty() || !cliente.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ\\s]{3,}$")) {
                JOptionPane.showMessageDialog(this, "Nombre de cliente inválido (mínimo 3 letras).");
                return;
            }
            if (horaSel == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una hora.");
                return;
            }

            int servicioId = cbServicio.getSelectedIndex() + 1;

            try {
                conexion.agregarCita(cliente, servicioId, fechaParaConsulta, horaSel, usuarioId);
                confirmado = true;

                // Actualizar tablas en VistaInicio y VistaCitas si es posible
                if (vistaInicio != null) {
                    vistaInicio.actualizarTablasInicio();
                }
                if (vistaCitas != null) {
                    vistaCitas.cargarTusCitas(fecha);
                }

                JOptionPane.showMessageDialog(this, "Cita creada correctamente.");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear cita: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
