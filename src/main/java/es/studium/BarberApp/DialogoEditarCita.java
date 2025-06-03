// DialogoEditarCita.java
package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;

public class DialogoEditarCita extends JDialog {
    private final JTextField txtCliente;
    private final JComboBox<String> cbServicio;
    private final JComboBox<String> cbHora;
    private boolean confirmado = false;

    /**
     * Constructor para editar una cita existente.
     * @param parent  Ventana padre.
     * @param conexion Instancia de ConexionBD.
     * @param cita    Objeto Cita con datos a editar (incluye id, fecha, hora, cliente, servicio).
     */
    public DialogoEditarCita(JFrame parent, ConexionBD conexion, Cita cita) {
        super(parent, "Editar Cita", true);
        setSize(450, 330);
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
        panel.setBounds(0, 0, 450, 330);
        add(panel);

        // Panel interior redondeado
        VistaLogin.RoundedPanel panelContenido = new VistaLogin.RoundedPanel(30, new Color(255, 255, 255, 230));
        panelContenido.setLayout(null);
        panelContenido.setBounds(25, 25, 400, 250);
        panel.add(panelContenido);

        // Título
        JLabel lblTitulo = new JLabel("Editar Cita");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBounds(140, 10, 200, 30);
        panelContenido.add(lblTitulo);

        // Fecha (no editable)
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setBounds(30, 60, 100, 20);
        panelContenido.add(lblFecha);

        String fechaStr = cita.getFecha(); // “yyyy-MM-dd”
        JLabel lblFechaValor = new JLabel(fechaStr);
        lblFechaValor.setBounds(130, 60, 220, 25);
        panelContenido.add(lblFechaValor);

        // Cliente
        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setBounds(30, 100, 100, 20);
        panelContenido.add(lblCliente);

        txtCliente = new JTextField(cita.getClienteNombre());
        txtCliente.setBounds(130, 100, 220, 25);
        panelContenido.add(txtCliente);

        // Servicio
        JLabel lblServicio = new JLabel("Servicio:");
        lblServicio.setBounds(30, 140, 100, 20);
        panelContenido.add(lblServicio);

        cbServicio = new JComboBox<>();
        cbServicio.setBounds(130, 140, 220, 25);
        // Poblar servicios y seleccionar el actual
        List<String> servicios = conexion.obtenerServicios();
        for (String s : servicios) {
            cbServicio.addItem(s);
        }
        cbServicio.setSelectedIndex(cita.getServicioId() - 1);
        panelContenido.add(cbServicio);

        // Hora
        JLabel lblHora = new JLabel("Hora:");
        lblHora.setBounds(30, 180, 100, 20);
        panelContenido.add(lblHora);

        cbHora = new JComboBox<>();
        cbHora.setBounds(130, 180, 220, 25);
        // Poblar horas disponibles para esa fecha, pero dejar la hora original al principio
        String horaOrig = cita.getHora(); // “HH:mm:ss” o “HH:mm”
        cbHora.addItem(horaOrig);
        List<java.time.LocalTime> horasDisponibles = conexion.consultarHorasDisponibles(fechaStr);
        for (java.time.LocalTime lt : horasDisponibles) {
            String hs = lt.toString();
            if (!hs.equals(horaOrig)) {
                cbHora.addItem(hs);
            }
        }
        panelContenido.add(cbHora);

        // Botón Guardar
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(140, 215, 120, 30);
        btnGuardar.addActionListener(e -> {
            String cliente = txtCliente.getText().trim();
            String servicioSel = (String) cbServicio.getSelectedItem();
            String horaSel = (String) cbHora.getSelectedItem();

            // Validaciones
            if (cliente.isEmpty() || !cliente.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ\\s]{3,}$")) {
                JOptionPane.showMessageDialog(this, "Nombre de cliente inválido.");
                return;
            }
            if (horaSel == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una hora.");
                return;
            }
            int servicioId = cbServicio.getSelectedIndex() + 1;
            try {
                conexion.modificarCita(cita.getId(), cliente, servicioId, cita.getFecha(), horaSel, cita.getUsuarioId());
                confirmado = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar cita: " + ex.getMessage());
            }
        });
        panelContenido.add(btnGuardar);
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
