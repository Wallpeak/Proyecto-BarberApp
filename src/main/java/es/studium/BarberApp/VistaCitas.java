package es.studium.BarberApp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Vista donde aparece el calendario (izquierda) y, a la derecha,
 * la tabla “Tus Citas” que muestra las citas del día seleccionado.
 * - Doble clic sobre una fila → abre DialogoEditarCita.
 * - Mantener presionado medio segundo sobre una fila → abre DialogoEliminarCita.
 */
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
    /**
     * Lista actual de Cita para la fecha mostrada en la tabla.
     * Coincide con el orden de filas de modeloTusCitas.
     */
    private List<Cita> listaCitasActuales;

    public VistaCitas() {
        conexionBD = new ConexionBD();
        btnInicio = new JButton("Inicio");
        btnCitas = new JButton("Citas");
        btnInventario = new JButton("Inventario");
        setLayout(null);

        // -------------------
        // Panel de fondo con imagen
        // -------------------
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

        // -------------------
        // Panel principal redondeado
        // -------------------
        panelCompleto = new Calendario.RoundedPanel(30, new Color(255, 220, 220, 230));
        panelCompleto.setLayout(null);
        panelFondo.add(panelCompleto);

        // -------------------
        // Calendario
        // -------------------
        calendario = new Calendario();
        calendario.setFechaSeleccionadaListener(this);
        calendario.setOpaque(false);
        panelCompleto.add(calendario);

        // -------------------
        // Panel contenedor de “Tus Citas”
        // -------------------
        panelTablaCitas = new Calendario.RoundedPanel(25, new Color(255, 255, 255, 220));
        panelTablaCitas.setLayout(new BorderLayout());
        panelCompleto.add(panelTablaCitas);

        // -------------------
        // Modelo y JTable “Tus Citas”
        // -------------------
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

        // Renderizado personalizado de celdas y encabezado
        tablaTusCitas.setDefaultRenderer(Object.class,
            new RoundedCellRenderer(new Color(255, 255, 255, 220), false));
        tablaTusCitas.getTableHeader().setDefaultRenderer(
            new RoundedCellRenderer(new Color(255, 160, 140), true));

        scrollCitas = new JScrollPane(tablaTusCitas);
        scrollCitas.setOpaque(false);
        scrollCitas.getViewport().setOpaque(false);
        scrollCitas.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelTablaCitas.add(scrollCitas, BorderLayout.CENTER);

        // -------------------
        // Listener de ratón para “Tus Citas”
        // -------------------
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
                        // Obtener la Cita correspondiente
                        Cita cita = listaCitasActuales.get(fila);
                        String cliente = cita.getClienteNombre();
                        String servicio = cita.getServicioNombre();
                        String hora = cita.getHora();
                        int idCita = cita.getId();

                        String descripcion = cliente + " — " + servicio + " a las " + hora;
                        DialogoEliminarCita dialogo =
                            new DialogoEliminarCita(
                                (JFrame) SwingUtilities.getWindowAncestor(VistaCitas.this),
                                conexionBD,
                                idCita,
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
                    // Obtener la Cita correspondiente
                    Cita cita = listaCitasActuales.get(fila);
                    DialogoEditarCita dialogo =
                        new DialogoEditarCita(
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

        // -------------------
        // Ajuste de tamaños al redimensionar panelCompleto
        // -------------------
        panelCompleto.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = panelCompleto.getWidth();
                int h = panelCompleto.getHeight();
                int padding = (int)(w * 0.03);

                int calendarWidth = (int)(w * 0.55);
                int calendarHeight = h - 2 * padding;
                calendario.setBounds(padding, padding, calendarWidth, calendarHeight);

                int tablaX = padding + calendarWidth + (int)(w * 0.04);
                int tablaWidth = w - tablaX - padding;
                panelTablaCitas.setBounds(tablaX, padding, tablaWidth, calendarHeight);
            }
        });

        // -------------------
        // Ajuste de posiciones al redimensionar VistaCitas
        // -------------------
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panelFondo.setBounds(0, 0, getWidth(), getHeight());
                panelCompleto.setBounds(10, 10, getWidth() - 20, getHeight() - 20);
            }
        });

        // -------------------
        // Carga inicial de las citas de hoy
        // -------------------
        cargarTusCitas(LocalDate.now());
    }

    @Override
    public void fechaSeleccionada(LocalDate fecha) {
        cargarTusCitas(fecha);
    }

    /**
     * Recupera la fecha actualmente marcada en el calendario.
     */
    private LocalDate citaFecha() {
        return calendario.obtieneUltimaFechaSeleccionada();
    }

    /**
     * Carga las citas para la fecha indicada y actualiza la tabla.
     * Almacena en listaCitasActuales el List<Cita> consultado.
     */
    private void cargarTusCitas(LocalDate fecha) {
        modeloTusCitas.setRowCount(0);
        String fechaStr = fecha.toString();

        listaCitasActuales = conexionBD.consultarCitasPorFecha(fechaStr);
        for (Cita cita : listaCitasActuales) {
            modeloTusCitas.addRow(new Object[]{
                cita.getClienteNombre(),
                cita.getServicioNombre(),
                cita.getHora()
            });
        }
    }

}