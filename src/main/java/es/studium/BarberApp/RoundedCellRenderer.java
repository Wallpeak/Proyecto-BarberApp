package es.studium.BarberApp;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class RoundedCellRenderer extends DefaultTableCellRenderer {
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
    }
}
