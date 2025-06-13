package es.studium.BarberApp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class VistaInicio extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public JButton btnInicio = crearMenuButton("INICIO");
    public JButton btnCitas = crearMenuButton("CITAS");
    public JButton btnInventario = crearMenuButton("INVENTARIO");
    public JButton btnLogin;

    public static final String INICIO = "INICIO";
    public static final String CITAS = "CITAS";
    public static final String INVENTARIO = "INVENTARIO";
    public static final String REGISTRO = "REGISTRO";
    public static final String LOGIN = "INICIAR SESION";

    private JPanel panelInicio;
    public VistaCitas panelCitas;
    public VistaInventario panelInventario;
    private VistaLogin panelLogin;
    private VistaRegistro panelRegistro;
    private ConexionBD bd;
    private JTable tablaInventario;
    private JTable tablaCitasHoy;
    private DefaultTableModel modeloInventario;
    private DefaultTableModel modeloCitas;

    public VistaInicio() {
    	bd=new ConexionBD();
        setTitle("THE BARBER SHOP");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 450));

        // Inicializar modelos para las tablas con columnas adecuadas
        modeloInventario = new DefaultTableModel(new Object[]{"Artículo", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloCitas = new DefaultTableModel(new Object[]{"Cliente", "Servicio", "Hora"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Crear tablas con el estilo de VistaCitas
        tablaInventario = crearTablaEstilizada(modeloInventario);
        tablaCitasHoy = crearTablaEstilizada(modeloCitas);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        panelInicio = crearPanelInicio();
        panelCitas = new VistaCitas();
        panelInventario = new VistaInventario();
        panelLogin = new VistaLogin();
        panelRegistro = new VistaRegistro();

        // Listener de login
        panelLogin.setLoginListener(new VistaLogin.LoginListener() {
            @Override
            public void onLoginSuccess(String usuario, int tipoUsuario) {
                cardPanel.remove(panelLogin);
                cardPanel.revalidate();
                cardPanel.repaint();
                setUserSession(usuario, tipoUsuario);
            }

            @Override
            public void onShowRegistro() {
                cambiarVista(REGISTRO);
            }
        });

        // Listener para volver a login tras registro
        panelRegistro.setRegistroListener(() -> cambiarVista(LOGIN));

        // Agregar paneles al CardLayout
        cardPanel.add(panelInicio, INICIO);
        cardPanel.add(panelCitas, CITAS);
        cardPanel.add(panelInventario, INVENTARIO);
        cardPanel.add(panelLogin, LOGIN);
        cardPanel.add(panelRegistro, REGISTRO);

        setLayout(new BorderLayout());
        setJMenuBar(crearMenu());
        add(cardPanel, BorderLayout.CENTER);

        // Mostrar primero el login
        cambiarVista(LOGIN);
        btnInicio.setEnabled(false);
        btnCitas.setEnabled(false);
        btnInventario.setEnabled(false);
    }

    private JMenuBar crearMenu() {
        JMenuBar menuBar = new JMenuBar() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
                g2d.setColor(Color.WHITE);
                String text = "THE BARBER SHOP";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - fm.getDescent();
                g2d.drawString(text, x, y);
            }
        };

        menuBar.setPreferredSize(new Dimension(getWidth(), 50));
        menuBar.setBackground(Color.BLACK);
        menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));

        btnInicio.addActionListener(e -> cambiarVista(INICIO));
        btnCitas.addActionListener(e -> cambiarVista(CITAS));
        btnInventario.addActionListener(e -> cambiarVista(INVENTARIO));

        menuBar.add(Box.createRigidArea(new Dimension(10, 0)));
        menuBar.add(btnInicio);
        menuBar.add(crearSeparator());
        menuBar.add(btnCitas);
        menuBar.add(crearSeparator());
        menuBar.add(btnInventario);
        menuBar.add(Box.createHorizontalGlue());

        btnLogin = new JButton("INICIAR SESIÓN");
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(160, 50));
        btnLogin.addActionListener(e -> cambiarVista(LOGIN));
        menuBar.add(btnLogin);

        JButton btnHamburger = new JButton(" ☰ ");
        btnHamburger.setFocusPainted(false);
        btnHamburger.setBorderPainted(false);
        btnHamburger.setContentAreaFilled(false);
        btnHamburger.setForeground(Color.WHITE);
        btnHamburger.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnHamburger.setPreferredSize(new Dimension(50, 50));

        JPopupMenu menuAyuda = new JPopupMenu();
        menuAyuda.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        menuAyuda.setBackground(Color.BLACK);
        JMenuItem itemAyuda = new JMenuItem("Ayuda");
        itemAyuda.setBackground(Color.BLACK);
        itemAyuda.setForeground(Color.WHITE);
        itemAyuda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuAyuda.add(itemAyuda);
        itemAyuda.addActionListener(e -> menuAyuda.setVisible(false));
        btnHamburger.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                menuAyuda.show(btnHamburger,
                        -menuAyuda.getPreferredSize().width + btnHamburger.getWidth(),
                        btnHamburger.getHeight());
            }
        });
        menuBar.add(btnHamburger);

        return menuBar;
    }

    public void cambiarVista(String vista) {
        cardLayout.show(cardPanel, vista);
        actualizarTablasInicio();
        setTitle("The Barber Shop - " + vista);    }

    public void setUserSession(String usuario, int tipoUsuario) {
        SesionUsuario.nombreUsuario = usuario;
        SesionUsuario.usuarioId = new ConexionBD().obtenerIdUsuario(usuario);

        btnLogin.setText("Bienvenido, " + usuario + "  |  Cerrar sesión");
        for (ActionListener al : btnLogin.getActionListeners()) {
            btnLogin.removeActionListener(al);
        }
        btnLogin.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Deseas cerrar sesión?",
                    "Cerrar sesión", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                SesionUsuario.cerrarSesion();
                btnLogin.setText("INICIAR SESIÓN");
                for (ActionListener al2 : btnLogin.getActionListeners()) {
                    btnLogin.removeActionListener(al2);
                }
                btnLogin.addActionListener(ev -> cambiarVista(LOGIN));
                btnInicio.setEnabled(false);
                btnCitas.setEnabled(false);
                btnInventario.setEnabled(false);

                boolean loginExists = false;
                for (Component c : cardPanel.getComponents()) {
                    if (c == panelLogin) {
                        loginExists = true;
                        break;
                    }
                }
                if (!loginExists) {
                    cardPanel.add(panelLogin, LOGIN);
                    cardPanel.revalidate();
                    cardPanel.repaint();
                }
                cambiarVista(LOGIN);
            }
        });

        btnInicio.setEnabled(true);
        btnCitas.setEnabled(true);
        btnInventario.setEnabled(tipoUsuario == 1);

        actualizarTablasInicio();
        cambiarVista(INICIO);
    }

    private static JButton crearMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        Dimension buttonSize = new Dimension(130, 50);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.LIGHT_GRAY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
        });
        return button;
    }

    private JLabel crearSeparator() {
        JLabel separator = new JLabel("|");
        separator.setForeground(Color.WHITE);
        separator.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return separator;
    }
    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(null) {
            private final Image imagenFondo =
                new ImageIcon("src/main/resources/es/studium/recursos/FondoApp.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        int margenExterior = 20;

        // Etiquetas con contorno
        OutlineLabel lblInventario = new OutlineLabel("ARTÍCULOS CON MENOR STOCK");
        lblInventario.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblInventario);

        OutlineLabel lblCitas = new OutlineLabel("CITAS DEL DÍA");
        lblCitas.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblCitas);

        // Panel redondeado semitransparente para Inventario
        RoundedPanel panelInventarioFondo = new RoundedPanel(
            new Color(255, 255, 255, 220), 25
        );
        panelInventarioFondo.setLayout(new BorderLayout(10, 10));
        panel.add(panelInventarioFondo);

        // Tabla de Inventario
        tablaInventario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaInventario.setRowHeight(28);
        tablaInventario.setShowGrid(false);
        tablaInventario.setIntercellSpacing(new Dimension(0, 0));
        tablaInventario.getTableHeader().setReorderingAllowed(false);
        tablaInventario.setDefaultRenderer(Object.class,
            new RoundedCellRenderer(new Color(255, 255, 255, 220), false));
        tablaInventario.getTableHeader().setDefaultRenderer(
            new RoundedCellRenderer(new Color(255, 160, 140), true));
        tablaInventario.getTableHeader().setOpaque(false);
        tablaInventario.getTableHeader().setBackground(new Color(0, 0, 0, 0));
        tablaInventario.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scrollInventario = new JScrollPane(tablaInventario);
        scrollInventario.setOpaque(false);
        scrollInventario.getViewport().setOpaque(false);
        scrollInventario.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelInventarioFondo.add(scrollInventario, BorderLayout.CENTER);

        // Panel redondeado semitransparente para Citas
        RoundedPanel panelCitasFondo = new RoundedPanel(
            new Color(255, 255, 255, 220), 25
        );
        panelCitasFondo.setLayout(new BorderLayout(10, 10));
        panel.add(panelCitasFondo);

        // Tabla de Citas del Día
        tablaCitasHoy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCitasHoy.setRowHeight(28);
        tablaCitasHoy.setShowGrid(false);
        tablaCitasHoy.setIntercellSpacing(new Dimension(0, 0));
        tablaCitasHoy.getTableHeader().setReorderingAllowed(false);
        tablaCitasHoy.setDefaultRenderer(Object.class,
            new RoundedCellRenderer(new Color(255, 255, 255, 220), false));
        tablaCitasHoy.getTableHeader().setDefaultRenderer(
            new RoundedCellRenderer(new Color(255, 160, 140), true));
        tablaCitasHoy.getTableHeader().setOpaque(false);
        tablaCitasHoy.getTableHeader().setBackground(new Color(0, 0, 0, 0));
        tablaCitasHoy.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scrollCitas = new JScrollPane(tablaCitasHoy);
        scrollCitas.setOpaque(false);
        scrollCitas.getViewport().setOpaque(false);
        scrollCitas.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelCitasFondo.add(scrollCitas, BorderLayout.CENTER);

        // Ajuste de posiciones
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = panel.getWidth();
                int h = panel.getHeight();

                int marginY = margenExterior + 30;
                int availableWidth = w - 3 * margenExterior;
                int panelWidth = availableWidth / 2;
                int panelHeight = h - marginY - margenExterior;

                panelInventarioFondo.setBounds(
                    margenExterior,
                    marginY,
                    panelWidth,
                    panelHeight
                );
                panelCitasFondo.setBounds(
                    margenExterior + panelWidth + margenExterior,
                    marginY,
                    panelWidth,
                    panelHeight
                );

                lblInventario.setBounds(
                    panelInventarioFondo.getX(),
                    margenExterior,
                    panelInventarioFondo.getWidth(),
                    30
                );
                lblCitas.setBounds(
                    panelCitasFondo.getX(),
                    margenExterior,
                    panelCitasFondo.getWidth(),
                    30
                );
            }
        });

        
        actualizarTablasInicio();

        return panel;
    }



    public void actualizarTablasInicio() {
        modeloInventario.setRowCount(0);
        modeloCitas.setRowCount(0);

        ConexionBD conexion = new ConexionBD();

        // Obtener artículos con menor stock y llenar la tabla
        ArrayList<Object[]> articulos = conexion.consultarMenorStock();
        for (Object[] fila : articulos) {
            modeloInventario.addRow(new Object[]{fila[0], fila[1]});
        }

        // Obtener citas del usuario activo para hoy y llenar la tabla
        ArrayList<Object[]> citas = conexion.obtenerCitasPorUsuarioYFechaHoy(SesionUsuario.usuarioId);
        for (Object[] fila : citas) {
        	modeloCitas.addRow(new Object[]{fila[1], fila[2], fila[3]});        }
    }


    /**
     * Crea un JTable con el mismo estilo que “Tus Citas” en VistaCitas:
     * celdas blancas semitransparentes y encabezado con color, ambos con bordes redondeados.
     */
    private JTable crearTablaEstilizada(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.setRowHeight(35);
        tabla.setShowGrid(false);
        tabla.setOpaque(false);
        tabla.setIntercellSpacing(new Dimension(10, 10));

        // Renderizado de celdas: fondo blanco semitransparente
        tabla.setDefaultRenderer(Object.class,
            new RoundedCellRenderer(new Color(255, 255, 255, 220), false));

        // Encabezado: fondo color salmón, texto blanco
        tabla.getTableHeader().setDefaultRenderer(
            new RoundedCellRenderer(new Color(255, 160, 140), true));
        tabla.getTableHeader().setOpaque(false);
        tabla.getTableHeader().setBackground(new Color(255, 220, 220, 230));
        tabla.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        return tabla;
    }

    // Panel redondeado
    static class RoundedPanel extends JPanel {
        private final Color backgroundColor;
        private final int cornerRadius;

        public RoundedPanel(Color bgColor, int radius) {
            super();
            backgroundColor = bgColor;
            cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setColor(backgroundColor);
            graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
            graphics.setColor(Color.WHITE);
            graphics.setStroke(new BasicStroke(2));
            graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
        }
    }

    // JLabel con contorno negro
    static class OutlineLabel extends JLabel {
        private static final long serialVersionUID = 1L;

        public OutlineLabel(String text) {
            super(text);
            setFont(new Font("Segoe UI Black", Font.BOLD, 20));
            setForeground(Color.WHITE);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            String text = getText();
            if (text == null || text.isEmpty()) {
                super.paintComponent(g);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            FontMetrics fm = g2.getFontMetrics();
            int x = 0;
            int y = fm.getAscent();

            g2.setColor(Color.BLACK);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    g2.drawString(text, x + dx, y + dy);
                }
            }

            g2.setColor(getForeground());
            g2.drawString(text, x, y);

            g2.dispose();
        }
    }

    // Renderizador de celdas (idéntico a VistaCitas)
    static class RoundedCellRenderer extends JLabel implements javax.swing.table.TableCellRenderer {
        private final Color background;
        private final boolean isHeader;

        public RoundedCellRenderer(Color background, boolean isHeader) {
            this.background = background;
            this.isHeader = isHeader;
            setOpaque(false);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", isHeader ? Font.BOLD : Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            setText(value != null ? value.toString() : "");
            setForeground(isHeader ? Color.WHITE : new Color(70, 30, 30));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}
