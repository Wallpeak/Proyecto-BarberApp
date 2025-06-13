package es.studium.BarberApp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Vista para el Inventario:
 * - Muestra una tabla de productos (ID oculto, Nombre, Precio, Stock) con celdas y encabezado redondeado.
 * - Un panel blanco semitransparente (rounded) contiene la tabla.
 * - Los botones “Agregar”, “Editar” y “Eliminar” se sitúan justo debajo de esa área de la tabla.
 * - El fondo general es una imagen, y todo va dentro de un panel redondeado semi-transparente.
 */
public class VistaInventario extends JPanel {
    private final ConexionBD conexionBD;
    private final JTable tablaInventario;
    private final DefaultTableModel modeloTabla;
    private final JButton btnAgregar, btnEditar, btnEliminar;
    private int filaSeleccionada = -1;

    public JButton btnInicio;
    public JButton btnCitas;
    public JButton btnInventario;

    private final JPanel panelFondo;
    private final RoundedPanel panelCompleto;
    private final RoundedPanel panelTablaInventario; // panel con fondo blanco semitransparente
    static Color backgroundColor;
    public VistaInventario() {
    	backgroundColor=this.backgroundColor;
        conexionBD = new ConexionBD();
        btnInicio = new JButton("Inicio");
        btnCitas = new JButton("Citas");
        btnInventario = new JButton("Inventario");
        setLayout(null);

        // ---------------------------------------------------
        // 1) Panel de fondo con imagen
        // ---------------------------------------------------
        panelFondo = new JPanel(null) {
            private final Image imagenFondo =
                new ImageIcon("src/main/resources/es/studium/recursos/FondoApp.png")
                    .getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        add(panelFondo);

        // ---------------------------------------------------
        // 2) Panel principal redondeado semi-transparente
        // ---------------------------------------------------
        panelCompleto = new RoundedPanel(30, new Color(255, 220, 220, 230));
        panelCompleto.setLayout(null);
        panelFondo.add(panelCompleto);

        // ---------------------------------------------------
        // 3) Definición del modelo: columna 0 = ID (oculta luego), 1 = Nombre, 2 = Precio, 3 = Stock
        // ---------------------------------------------------
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // ---------------------------------------------------
        // 4) Crear JTable con estilo idéntico a “Tus Citas”:
        //    celdas blancas semitransparentes, encabezado salmón, ambos con bordes redondeados.
        // ---------------------------------------------------
        tablaInventario = crearTablaEstilizada(modeloTabla);

        // Ocultar la columna “ID” (índice 0) en la vista
        TableColumnModel tcm = tablaInventario.getColumnModel();
        tcm.getColumn(0).setMinWidth(0);
        tcm.getColumn(0).setMaxWidth(0);
        tcm.getColumn(0).setPreferredWidth(0);

        // ---------------------------------------------------
        // 5) Panel blanco semitransparente que contiene la tabla
        // ---------------------------------------------------
        panelTablaInventario = new RoundedPanel(25, new Color(255, 255, 255, 220));
        panelTablaInventario.setLayout(new BorderLayout());
        panelCompleto.add(panelTablaInventario);

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Sin borde

        
        scroll.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // padding uniforme: 15px
        panelTablaInventario.add(scroll, BorderLayout.CENTER);


        // ---------------------------------------------------
        // 6) Botones “Agregar”, “Editar” y “Eliminar”
        // ---------------------------------------------------
        btnAgregar  = new JButton("Agregar");
        btnEditar   = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        configurarBoton(btnAgregar);
        configurarBoton(btnEditar);
        configurarBoton(btnEliminar);

        panelCompleto.add(btnAgregar);
        panelCompleto.add(btnEditar);
        panelCompleto.add(btnEliminar);

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        // ---------------------------------------------------
        // 7) Activar/desactivar botones según selección de fila
        // ---------------------------------------------------
        tablaInventario.getSelectionModel().addListSelectionListener(e -> {
            filaSeleccionada = tablaInventario.getSelectedRow();
            boolean seleccionado = (filaSeleccionada >= 0);
            tablaInventario.getSelectionBackground();
            btnEditar.setEnabled(seleccionado);
            btnEliminar.setEnabled(seleccionado);
        });

        // ---------------------------------------------------
        // 8) Acción “Agregar”
        // ---------------------------------------------------
        btnAgregar.addActionListener(e -> {
            DialogoAgregarProducto dialogo = new DialogoAgregarProducto(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                conexionBD
            );
            dialogo.setVisible(true);
            actualizarTabla();
        });

        // ---------------------------------------------------
        // 9) Acción “Editar”
        // ---------------------------------------------------
        btnEditar.addActionListener(e -> {
            if (filaSeleccionada >= 0) {
                int id     = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
                String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
                double precio = (double) modeloTabla.getValueAt(filaSeleccionada, 2);
                int stock     = (int)    modeloTabla.getValueAt(filaSeleccionada, 3);

                Producto producto = new Producto(id, nombre, precio, stock);
                DialogoEditarProducto dialogo = new DialogoEditarProducto(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    conexionBD,
                    producto
                );
                dialogo.setVisible(true);
                if (dialogo.isConfirmado()) {
                    actualizarTabla();
                }
            }
        });

        // ---------------------------------------------------
        // 10) Acción “Eliminar”
        // ---------------------------------------------------
        btnEliminar.addActionListener(e -> {
            if (filaSeleccionada >= 0) {
                int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
                String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
                String descripcion = nombre;

                DialogoEliminarProducto dialogo = new DialogoEliminarProducto(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    conexionBD,
                    id,
                    descripcion
                );
                dialogo.setVisible(true);
                if (dialogo.isConfirmado()) {
                    actualizarTabla();
                }
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un producto para eliminar.",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });

        // ---------------------------------------------------
        // 11) Ajuste de posiciones al redimensionar “VistaInventario”
        // ---------------------------------------------------
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panelFondo.setBounds(0, 0, getWidth(), getHeight());
                panelCompleto.setBounds(10, 10, getWidth() - 20, getHeight() - 20);

                int w = panelCompleto.getWidth();
                int h = panelCompleto.getHeight();
                int padding = (int)(w * 0.03);

                // 11.1) El panel de la tabla ocupa el 90% del ancho y 65% de la altura
                int tblW = (int)(w * 0.90);
                int tblH = (int)(h * 0.65);
                panelTablaInventario.setBounds(padding, padding, tblW, tblH);

                // 11.2) Botones en una fila bajo la tabla, centrados
                int btnW  = (int)(w * 0.20);
                int btnH  = (int)(h * 0.08);
                int spaceX = (int)(w * 0.05);

                int yBtn = padding + tblH + (int)(h * 0.03);
                int totalButtonsWidth = btnW * 3 + spaceX * 2;
                int startX = (w - totalButtonsWidth) / 2;

                btnAgregar.setBounds(startX, yBtn, btnW, btnH);
                btnEditar.setBounds(startX + btnW + spaceX, yBtn, btnW, btnH);
                btnEliminar.setBounds(startX + 2*(btnW + spaceX), yBtn, btnW, btnH);
            }
        });

        // ---------------------------------------------------
        // 12) Carga inicial de datos
        // ---------------------------------------------------
        actualizarTabla();
    }

    /**
     * Crea un JTable con el mismo estilo que “Tus Citas” en VistaCitas:
     * - Celdas blancas semitransparentes y bordes redondeados.
     * - Encabezado color salmón con texto blanco y bordes redondeados.
     */
    private JTable crearTablaEstilizada(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(28);
        tabla.setShowGrid(false);
        tabla.setOpaque(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.getTableHeader().setReorderingAllowed(false);

        // 1) Celdas: fondo blanco semitransparente
        tabla.setDefaultRenderer(Object.class,
            new RoundedCellRenderer(new Color(255, 255, 255, 220), false)
        );

        // 2) Encabezado: fondo salmón, texto blanco
        tabla.getTableHeader().setDefaultRenderer(
            new RoundedCellRenderer(new Color(255, 160, 140), true)
        );
        tabla.getTableHeader().setOpaque(false);
        tabla.getTableHeader().setBackground(new Color(0, 0, 0, 0));
        tabla.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        return tabla;
    }

    /**
     * Configura un botón con el mismo estilo usado en VistaCitas:
     * fondo rosado, texto blanco, fuente Segoe UI Bold, borde blanco, cursor mano.
     */
    private void configurarBoton(JButton boton) {
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        boton.setBackground(new Color(255, 128, 128));
        boton.setForeground(Color.WHITE);
        boton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Rellena el modelo de la tabla con los productos actuales de la BD */
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Producto> productos = conexionBD.obtenerInventario();
        for (Producto p : productos) {
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock()
            });
        }
        filaSeleccionada = -1;
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    // ---------------------------------------------------
    // 13) RoundedPanel: idéntico a VistaCitas
    // ---------------------------------------------------
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
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
        }
    }

    // ---------------------------------------------------
    // 14) RoundedCellRenderer: idéntico a VistaCitas
    // ---------------------------------------------------
    static class RoundedCellRenderer extends DefaultTableCellRenderer {
        private final Color backgroundColor;
        private final boolean isHeader;

        public RoundedCellRenderer(Color backgroundColor, boolean isHeader) {
            this.backgroundColor = backgroundColor;
            this.isHeader = isHeader;
            setOpaque(false); // para que se pinte manualmente el fondo
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Cambiar fuente y color de texto si es encabezado
            if (isHeader) {
                setFont(getFont().deriveFont(Font.BOLD));
                setForeground(Color.WHITE);
            } else {
                setFont(getFont().deriveFont(Font.PLAIN));
                if (isSelected) {
                    setForeground(Color.BLACK);
                    setBackground(backgroundColor);
                    // asegúrate de que el texto sea visible
                } else {
                    setForeground(Color.DARK_GRAY);
                }
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Suavizado
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 20;
            int width = getWidth();
            int height = getHeight();

            Color fillColor = backgroundColor;

            JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, this);
            if (table != null && table.getSelectedRow() == getY() / getHeight()) {
                fillColor = new Color(255, 200, 200, 200); // color diferente para seleccionada
            }

            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, width - 1, height - 1, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }}
}
