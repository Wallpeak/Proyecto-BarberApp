package es.studium.BarberApp;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class Controlador implements ActionListener {

    // Referencias a las vistas
    public static VistaInicio vistaInicio;
    public static VistaCitas vistaCitas;
    public static VistaInventario vistaInventario;

    // Constructor: recibe las instancias ya creadas de VistaInicio (JFrame con CardLayout),
    // VistaCitas y VistaInventario (ahora como JPanel)
    public Controlador(VistaInicio vi, VistaCitas vc, VistaInventario vi2) {
        vistaInicio = vi;
        vistaCitas = vc;
        vistaInventario = vi2;

        // Registrar ActionListeners en los botones del menú del JFrame principal (VistaInicio)
        vistaInicio.btnInicio.addActionListener(this);
        vistaInicio.btnCitas.addActionListener(this);
        vistaInicio.btnInventario.addActionListener(this);

        // Registrar ActionListeners en los botones de menú de las vistas que son paneles
        vistaCitas.btnInicio.addActionListener(this);
        vistaCitas.btnCitas.addActionListener(this);
        vistaCitas.btnInventario.addActionListener(this);

        vistaInventario.btnInicio.addActionListener(this);
        vistaInventario.btnCitas.addActionListener(this);
        vistaInventario.btnInventario.addActionListener(this);

        // Inicialmente se resalta el botón "INICIO" y se actualiza el título
        setSelectedMenu("INICIO");
        vistaInicio.cambiarVista(VistaInicio.INICIO);

        // Mostrar la ventana principal (VistaInicio con CardLayout)
        vistaInicio.setVisible(true);
    }

    // Método que se ejecuta al pulsar cualquiera de los botones del menú
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == vistaInicio.btnInicio || source == vistaCitas.btnInicio || source == vistaInventario.btnInicio) {
            setSelectedMenu("INICIO");
            vistaInicio.cambiarVista(VistaInicio.INICIO);
        } else if (source == vistaInicio.btnCitas || source == vistaCitas.btnCitas || source == vistaInventario.btnCitas) {
            setSelectedMenu("CITAS");
            vistaInicio.cambiarVista(VistaInicio.CITAS);
        } else if (source == vistaInicio.btnInventario || source == vistaCitas.btnInventario || source == vistaInventario.btnInventario) {
            setSelectedMenu("INVENTARIO");
            vistaInicio.cambiarVista(VistaInicio.INVENTARIO);
        }
    }

    // Restablece el borde y la fuente de todos los botones a su estado normal
    private void resetMenuButtons() {
        Border empty = BorderFactory.createEmptyBorder();

        // VistaInicio
        vistaInicio.btnInicio.setBorder(empty);
        vistaInicio.btnInicio.setFont(vistaInicio.btnInicio.getFont().deriveFont(Font.PLAIN));
        vistaInicio.btnCitas.setBorder(empty);
        vistaInicio.btnCitas.setFont(vistaInicio.btnCitas.getFont().deriveFont(Font.PLAIN));
        vistaInicio.btnInventario.setBorder(empty);
        vistaInicio.btnInventario.setFont(vistaInicio.btnInventario.getFont().deriveFont(Font.PLAIN));

        // VistaCitas
        vistaCitas.btnInicio.setBorder(empty);
        vistaCitas.btnInicio.setFont(vistaCitas.btnInicio.getFont().deriveFont(Font.PLAIN));
        vistaCitas.btnCitas.setBorder(empty);
        vistaCitas.btnCitas.setFont(vistaCitas.btnCitas.getFont().deriveFont(Font.PLAIN));
        vistaCitas.btnInventario.setBorder(empty);
        vistaCitas.btnInventario.setFont(vistaCitas.btnInventario.getFont().deriveFont(Font.PLAIN));

        // VistaInventario
        vistaInventario.btnInicio.setBorder(empty);
        vistaInventario.btnInicio.setFont(vistaInventario.btnInicio.getFont().deriveFont(Font.PLAIN));
        vistaInventario.btnCitas.setBorder(empty);
        vistaInventario.btnCitas.setFont(vistaInventario.btnCitas.getFont().deriveFont(Font.PLAIN));
        vistaInventario.btnInventario.setBorder(empty);
        vistaInventario.btnInventario.setFont(vistaInventario.btnInventario.getFont().deriveFont(Font.PLAIN));
    }

    // Establece el menú seleccionado: añade un borde inferior blanco (8px) y pone la fuente en negrita
    private void setSelectedMenu(String menu) {
        resetMenuButtons();

        Border indicator = BorderFactory.createMatteBorder(0, 0, 8, 0, Color.WHITE);

        if (menu.equals("INICIO")) {
            vistaInicio.btnInicio.setBorder(indicator);
            vistaInicio.btnInicio.setFont(vistaInicio.btnInicio.getFont().deriveFont(Font.BOLD));

            vistaCitas.btnInicio.setBorder(indicator);
            vistaCitas.btnInicio.setFont(vistaCitas.btnInicio.getFont().deriveFont(Font.BOLD));

            vistaInventario.btnInicio.setBorder(indicator);
            vistaInventario.btnInicio.setFont(vistaInventario.btnInicio.getFont().deriveFont(Font.BOLD));
        } else if (menu.equals("CITAS")) {
            vistaInicio.btnCitas.setBorder(indicator);
            vistaInicio.btnCitas.setFont(vistaInicio.btnCitas.getFont().deriveFont(Font.BOLD));

            vistaCitas.btnCitas.setBorder(indicator);
            vistaCitas.btnCitas.setFont(vistaCitas.btnCitas.getFont().deriveFont(Font.BOLD));

            vistaInventario.btnCitas.setBorder(indicator);
            vistaInventario.btnCitas.setFont(vistaInventario.btnCitas.getFont().deriveFont(Font.BOLD));
        } else if (menu.equals("INVENTARIO")) {
            vistaInicio.btnInventario.setBorder(indicator);
            vistaInicio.btnInventario.setFont(vistaInicio.btnInventario.getFont().deriveFont(Font.BOLD));

            vistaCitas.btnInventario.setBorder(indicator);
            vistaCitas.btnInventario.setFont(vistaCitas.btnInventario.getFont().deriveFont(Font.BOLD));

            vistaInventario.btnInventario.setBorder(indicator);
            vistaInventario.btnInventario.setFont(vistaInventario.btnInventario.getFont().deriveFont(Font.BOLD));
        }

        vistaInicio.setTitle("The Barber Shop - " + menu);
    }
}
