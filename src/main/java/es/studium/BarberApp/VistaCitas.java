package es.studium.BarberApp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class VistaCitas extends JPanel implements Calendario.FechaSeleccionadaListener {
    private final ConexionBD conexionBD;
    private final JPanel panelFondo;
    private final Calendario calendario;
    private final JTable tablaTusCitas;
    private final DefaultTableModel modeloTusCitas;
    private final JScrollPane scrollCitas;
    private final Calendario.RoundedPanel panelCompleto;
    private final Calendario.RoundedPanel panelTablaCitas;
    public JButton btnInicio;
    public JButton btnCitas;
    public JButton btnInventario;
    private List<Cita> listaCitasActuales;
    private LocalDate fechaHoy;
    public VistaCitas() {
        conexionBD = new ConexionBD();
        btnInicio = new JButton("Inicio");
        btnCitas = new JButton("Citas");
        btnInventario = new JButton("Inventario");
        fechaHoy= LocalDate.now();
       
        setLayout(null);

        // Panel de fondo con imagen
        panelFondo = new JPanel(null) {
            private final Image imagenFondo =
                new ImageIcon("src/main/resources/es/studium/recursos/FondoApp.png").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        add(panelFondo);

        // Panel contenedor
        panelCompleto = new Calendario.RoundedPanel(30, new Color(255, 220, 220, 230));
        panelCompleto.setLayout(null);
        panelFondo.add(panelCompleto);

        // Calendario funcional
        JFrame frame = new JFrame(); // solo para crear el calendario
        calendario = new Calendario(frame);
        calendario.setFechaSeleccionadaListener(this);
        calendario.setOpaque(false);
        panelCompleto.add(calendario);

        // Panel de la tabla de citas
        panelTablaCitas = new Calendario.RoundedPanel(25, new Color(255, 255, 255, 220));
        panelTablaCitas.setLayout(new BorderLayout());
        panelCompleto.add(panelTablaCitas);

        // Configuración de la tabla
        modeloTusCitas = new DefaultTableModel(new Object[]{"Cliente", "Servicio", "Hora"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTusCitas = new JTable(modeloTusCitas);
        tablaTusCitas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaTusCitas.setRowHeight(28);
        tablaTusCitas.setShowGrid(false);
        tablaTusCitas.setIntercellSpacing(new Dimension(0, 0));
        tablaTusCitas.getTableHeader().setReorderingAllowed(false);
        tablaTusCitas.setDefaultRenderer(Object.class,
            new RoundedCellRenderer(new Color(255, 255, 255, 220), false));
        tablaTusCitas.getTableHeader().setDefaultRenderer(
            new RoundedCellRenderer(new Color(255, 160, 140), true));

        scrollCitas = new JScrollPane(tablaTusCitas);
        scrollCitas.setOpaque(false);
        scrollCitas.getViewport().setOpaque(false);
        scrollCitas.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelTablaCitas.add(scrollCitas, BorderLayout.CENTER);

        // Acciones sobre la tabla (editar o eliminar citas)
        tablaTusCitas.addMouseListener(new MouseAdapter() {
            private Timer holdTimer;
            private int filaPresionada = -1;

            @Override
            public void mousePressed(MouseEvent e) {
                int fila = tablaTusCitas.rowAtPoint(e.getPoint());
                if (fila < 0) return;
                filaPresionada = fila;

                holdTimer = new Timer(500, ev -> {
                    if (filaPresionada == fila) {
                        Cita cita = listaCitasActuales.get(fila);
                        String descripcion = cita.getClienteNombre() + " — " + cita.getServicioNombre() + " a las " + cita.getHora();
                        DialogoEliminarCita dialogo = new DialogoEliminarCita(
                            (JFrame) SwingUtilities.getWindowAncestor(VistaCitas.this),
                            conexionBD,
                            cita.getId(),
                            descripcion
                        );
                        dialogo.setVisible(true);
                        if (dialogo.isConfirmado()) {
                            cargarTusCitas(citaFecha());
                        }
                    }
                });
                holdTimer.setRepeats(false);
                holdTimer.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (holdTimer != null && holdTimer.isRunning()) {
                    holdTimer.stop();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaTusCitas.rowAtPoint(e.getPoint());
                    if (fila < 0) return;
                    Cita cita = listaCitasActuales.get(fila);
                    DialogoEditarCita dialogo = new DialogoEditarCita(
                        (JFrame) SwingUtilities.getWindowAncestor(VistaCitas.this),
                        conexionBD,
                        cita
                    );
                    dialogo.setVisible(true);
                    if (dialogo.isConfirmado()) {
                        cargarTusCitas(citaFecha());
                    }
                }
            }
        });

        // Redimensionamiento dinámico
        panelCompleto.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = panelCompleto.getWidth();
                int h = panelCompleto.getHeight();
                int padding = (int)(w * 0.025);

                int calendarWidth = (int)(w * 0.60);
                int calendarHeight = h - 2 * padding;
                calendario.setBounds(padding, padding, calendarWidth, calendarHeight);

                int tablaX = padding + calendarWidth + (int)(w * 0.015);
                int tablaWidth = w - tablaX - padding;
                panelTablaCitas.setBounds(tablaX, padding, tablaWidth, calendarHeight);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panelFondo.setBounds(0, 0, getWidth(), getHeight());
                panelCompleto.setBounds(10, 10, getWidth() - 20, getHeight() - 20);
                panelCompleto.revalidate();
                panelCompleto.repaint();
            }
        });

        // Cargar citas del día actual
        cargarTusCitas(LocalDate.now());
    }

    // Callback desde Calendario
    @Override
    public void fechaSeleccionada(LocalDate fecha) {
        cargarTusCitas(fecha);
    }

    private LocalDate citaFecha() {
        return calendario.obtieneUltimaFechaSeleccionada();
    }

    public void cargarTusCitas(LocalDate fecha) {
        modeloTusCitas.setRowCount(0);
        String fechaStr = fecha.toString();
        int usuarioId = SesionUsuario.usuarioId;

        listaCitasActuales = conexionBD.obtenerCitasPorUsuarioYFecha(usuarioId, fechaStr);

        for (Cita cita : listaCitasActuales) {
            modeloTusCitas.addRow(new Object[]{
                cita.getClienteNombre(),
                cita.getServicioNombre(),
                cita.getHora()
            });
        }
    }
}
