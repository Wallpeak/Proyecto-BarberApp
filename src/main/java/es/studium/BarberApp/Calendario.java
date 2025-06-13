package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Locale;

public class Calendario extends JPanel {

    private LocalDate mesActual;
    private JPanel panelDias;
    private JLabel lblMesAno;
    private FechaSeleccionadaListener listener;
    private JFrame framePadre; // Para pasar al diálogo

    private final Color COLOR_FONDO = new Color(255, 240, 240, 200);
    private final Color COLOR_SELECCION = new Color(255, 180, 160, 180);
    private final Color COLOR_HOY = new Color(255, 100, 100, 150);
    private final Color COLOR_DIAS = new Color(200, 90, 90);
    private final Color COLOR_LETRAS = new Color(80, 20, 20);

    private LocalDate fechaSeleccionada;

    public Calendario(JFrame framePadre) {
        this.framePadre = framePadre;

        mesActual = LocalDate.now().withDayOfMonth(1);
        fechaSeleccionada = LocalDate.now();

        setOpaque(false);
        setLayout(new BorderLayout());

        // Panel superior con mes y botones
        JPanel panelArriba = new RoundedPanel(20, COLOR_FONDO);
        panelArriba.setLayout(new BorderLayout());
        panelArriba.setPreferredSize(new Dimension(0, 40));
        add(panelArriba, BorderLayout.NORTH);

        // Botón mes anterior
        JButton btnPrev = crearBoton("<");
        btnPrev.addActionListener(e -> {
            mesActual = mesActual.minusMonths(1);
            actualizarCalendario();
        });
        panelArriba.add(btnPrev, BorderLayout.WEST);

        // Etiqueta mes y año
        lblMesAno = new JLabel("", SwingConstants.CENTER);
        lblMesAno.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMesAno.setForeground(COLOR_LETRAS);
        panelArriba.add(lblMesAno, BorderLayout.CENTER);

        // Botón mes siguiente
        JButton btnNext = crearBoton(">");
        btnNext.addActionListener(e -> {
            mesActual = mesActual.plusMonths(1);
            actualizarCalendario();
        });
        panelArriba.add(btnNext, BorderLayout.EAST);

        // Panel con los días
        panelDias = new JPanel(new GridLayout(0, 7, 4, 4));
        panelDias.setOpaque(false);
        add(panelDias, BorderLayout.CENTER);

        actualizarCalendario();

        // Para que el panel sea totalmente resizable:
        // BorderLayout y GridLayout con 0 filas hace que se adapte automáticamente
        // No es necesario cambiar nada más aquí, solo que el contenedor padre tenga layout que permita crecer.
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(COLOR_LETRAS);
        btn.setBackground(new Color(255, 210, 210));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(COLOR_SELECCION);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setOpaque(false);
                btn.setBackground(new Color(255, 210, 210));
            }
        });
        return btn;
    }

    private void actualizarCalendario() {
        panelDias.removeAll();

        // Mostrar nombre días de la semana
        DayOfWeek[] diasSemana = DayOfWeek.values();
        for (DayOfWeek dia : diasSemana) {
            JLabel lbl = new JLabel(dia.getDisplayName(TextStyle.SHORT, new Locale("es")), SwingConstants.CENTER);
            lbl.setForeground(COLOR_DIAS);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panelDias.add(lbl);
        }

        LocalDate primerDiaMes = mesActual;
        int diaSemanaPrimerDia = primerDiaMes.getDayOfWeek().getValue();
        // En java lunes=1, domingo=7. Queremos que lunes sea primera columna

        // Espacios vacíos antes del primer día
        for (int i = 1; i < diaSemanaPrimerDia; i++) {
            panelDias.add(new JLabel(""));
        }

        int diasMes = mesActual.lengthOfMonth();
        for (int dia = 1; dia <= diasMes; dia++) {
            LocalDate fechaActual = mesActual.withDayOfMonth(dia);
            DiaPanel diaPanel = new DiaPanel(dia, fechaActual);
            panelDias.add(diaPanel);
        }

        lblMesAno.setText(mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es")) + " " + mesActual.getYear());
        revalidate();
        repaint();
    }

    public void setFechaSeleccionadaListener(FechaSeleccionadaListener listener) {
        this.listener = listener;
    }

    public LocalDate obtieneUltimaFechaSeleccionada() {
        return fechaSeleccionada;
    }

    // Interfaz para comunicar fecha seleccionada
    public interface FechaSeleccionadaListener {
        void fechaSeleccionada(LocalDate fecha);
    }

    // Panel para cada día
    private class DiaPanel extends JPanel {
        private final int dia;
        private final LocalDate fecha;
		private VistaCitas vistaCitas;
		

        public DiaPanel(int dia, LocalDate fecha) {
            this.dia = dia;
            this.fecha = fecha;
            setPreferredSize(new Dimension(40, 40));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Si es doble clic con botón izquierdo
                    if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                        abrirDialogoCrearCita(fecha);
                    } else {
                        fechaSeleccionada = fecha;
                        if (listener != null) {
                            listener.fechaSeleccionada(fecha);
                        }
                        actualizarCalendario();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setToolTipText("Fecha: " + fecha.toString());
                }
            });
        }

        private void abrirDialogoCrearCita(LocalDate fecha) {
            ConexionBD bd = new ConexionBD();  // O usa una instancia compartida si la tienes
            
            int usuarioId = SesionUsuario.usuarioId; // ID del usuario activo
            
            // Comprobar que el usuario existe en la base de datos antes de abrir el diálogo
            if (!bd.existeUsuario(usuarioId)) {
                JOptionPane.showMessageDialog(null, "Usuario no válido o no existe en la base de datos.");
                return;
            }
            
            // Usar el JFrame padre real (por ejemplo framePadre) para centrar el diálogo
            DialogoCrearCita dialogo = new DialogoCrearCita(framePadre, bd, fecha, usuarioId, vistaCitas);
            
            dialogo.setLocationRelativeTo(framePadre);
            dialogo.setVisible(true);
            
            // Aquí puedes refrescar el calendario o tabla tras cerrar el diálogo, si lo deseas
            // actualizarCalendario();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo si es hoy
            if (fecha.equals(LocalDate.now())) {
                g2.setColor(COLOR_HOY);
                g2.fillOval(5, 5, getWidth() - 10, getHeight() - 10);
            }

            // Fondo si está seleccionado
            if (fecha.equals(fechaSeleccionada)) {
                g2.setColor(COLOR_SELECCION);
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 15, 15);
            }

            // Texto del día
            g2.setColor(COLOR_LETRAS);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            String texto = String.valueOf(dia);
            int anchoTexto = fm.stringWidth(texto);
            int altoTexto = fm.getAscent();
            int x = (getWidth() - anchoTexto) / 2;
            int y = (getHeight() + altoTexto) / 2 - 4;
            g2.drawString(texto, x, y);

            g2.dispose();
        }
    }

    // Panel redondeado genérico para usar en calendario (si quieres reutilizar)
    static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color bgColor;

        public RoundedPanel(int arc, Color bgColor) {
            this.arc = arc;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
        }
    }
}