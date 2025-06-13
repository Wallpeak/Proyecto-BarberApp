package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VistaRegistro extends JPanel {
    private RoundedPanel panelRegistro;
    private JTextField txtUsuario, txtCorreo;
    private JPasswordField txtContrasena, txtRepetir;
    private JButton btnRegistrar;
    private JLabel lblLogin;
    RegistroListener listener;

    public interface RegistroListener {
        void onRegistroSuccess();
    }

    public VistaRegistro() {
        setLayout(null);

        JPanel panelFondo = new JPanel(null) {
            private final Image imagenFondo = new ImageIcon(
                getClass().getResource("/es/studium/recursos/FondoApp.png")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelFondo.setBounds(0, 0, 1200, 720);
        add(panelFondo);

        panelRegistro = new RoundedPanel(40, new Color(255, 255, 255, 230));
        panelRegistro.setLayout(null);
        panelFondo.add(panelRegistro);

        JLabel lblTitulo = new JLabel("Registro de usuario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setBounds(100, 20, 300, 40);
        panelRegistro.add(lblTitulo);

        Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(fuenteCampos);
        lblUsuario.setBounds(40, 80, 100, 25);
        panelRegistro.add(lblUsuario);
        txtUsuario = new JTextField();
        txtUsuario.setFont(fuenteCampos);
        txtUsuario.setBounds(180, 80, 180, 25);
        panelRegistro.add(txtUsuario);

        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setFont(fuenteCampos);
        lblCorreo.setBounds(40, 120, 100, 25);
        panelRegistro.add(lblCorreo);
        txtCorreo = new JTextField();
        txtCorreo.setFont(fuenteCampos);
        txtCorreo.setBounds(180, 120, 180, 25);
        panelRegistro.add(txtCorreo);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(fuenteCampos);
        lblContrasena.setBounds(40, 160, 100, 25);
        panelRegistro.add(lblContrasena);
        txtContrasena = new JPasswordField();
        txtContrasena.setFont(fuenteCampos);
        txtContrasena.setBounds(180, 160, 180, 25);
        panelRegistro.add(txtContrasena);

        JLabel lblRepetir = new JLabel("Repetir contraseña:");
        lblRepetir.setFont(fuenteCampos);
        lblRepetir.setBounds(40, 200, 150, 25);
        panelRegistro.add(lblRepetir);
        txtRepetir = new JPasswordField();
        txtRepetir.setFont(fuenteCampos);
        txtRepetir.setBounds(180, 200, 180, 25);
        panelRegistro.add(txtRepetir);

        btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setBounds(180, 245, 120, 35);
        panelRegistro.add(btnRegistrar);

        lblLogin = new JLabel("<html><u style='color:blue'>¿Ya tienes cuenta? Inicia sesión</u></html>");
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.setBounds(130, 295, 250, 20);
        panelRegistro.add(lblLogin);

        btnRegistrar.addActionListener(e -> {
            String usuario = txtUsuario.getText().trim();
            String correo = txtCorreo.getText().trim();
            String pass1 = new String(txtContrasena.getPassword());
            String pass2 = new String(txtRepetir.getPassword());

            if (usuario.isEmpty() || correo.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }

            if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "El correo electrónico no es válido.");
                return;
            }

            if (!pass1.equals(pass2)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
                return;
            }

            ConexionBD bd = new ConexionBD();
            if (bd.usuarioYaExiste(usuario)) {
                JOptionPane.showMessageDialog(this, "El usuario o correo ya está registrado.");
                return;
            }

            String hashed = hashPassword(pass1);
            bd.agregarUsuario(usuario,hashed, correo, 0);
            JOptionPane.showMessageDialog(this, "Registro completado correctamente.");

            if (listener != null) listener.onRegistroSuccess();
        });

        lblLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (listener != null) listener.onRegistroSuccess();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth(), h = getHeight(), pw = 450, ph = 340;
                panelFondo.setBounds(0, 0, w, h);
                panelRegistro.setBounds((w - pw) / 2, (h - ph) / 2, pw, ph);
            }
        });
    }

    public void setRegistroListener(RegistroListener l) {
        this.listener = l;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return password; // fallback si falla el hash
        }
    }



    static class RoundedPanel extends JPanel {
        private final int arc; private final Color bg;
        public RoundedPanel(int arc, Color bg) { this.arc = arc; this.bg = bg; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
            g2.dispose();
        }
    }
}
