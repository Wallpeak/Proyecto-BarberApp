package es.studium.BarberApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class VistaLogin extends JPanel {
    private RoundedPanel panelLogin;
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JCheckBox chkRecordar;
    private JLabel lblRegistro, lblError;
    private LoginListener listener;

    public interface LoginListener {
        void onLoginSuccess(String usuario, int tipoUsuario);
        void onShowRegistro();
    }

    public void setLoginListener(LoginListener l) {
        this.listener = l;
    }

    public VistaLogin() {
        setLayout(new BorderLayout());

        JPanel panelFondo = new JPanel(null) {
            private final Image imagenFondo =
                new ImageIcon("src/main/resources/es/studium/recursos/FondoApp.png").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        add(panelFondo, BorderLayout.CENTER);

        panelLogin = new RoundedPanel(40, new Color(255, 255, 255, 230));
        panelLogin.setLayout(null);
        panelFondo.add(panelLogin);

        JLabel lblTitulo = new JLabel("Iniciar sesión");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setBounds(140, 20, 200, 30);
        panelLogin.add(lblTitulo);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUsuario.setBounds(40, 70, 100, 25);
        panelLogin.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(150, 70, 220, 25);
        panelLogin.add(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblContrasena.setBounds(40, 110, 100, 25);
        panelLogin.add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(150, 110, 220, 25);
        panelLogin.add(txtContrasena);

        chkRecordar = new JCheckBox("Recordar Usuario");
        chkRecordar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkRecordar.setOpaque(false);
        chkRecordar.setBounds(150, 150, 200, 25);
        panelLogin.add(chkRecordar);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setBounds(160, 190, 100, 30);
        panelLogin.add(btnEntrar);

        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        lblError.setBounds(40, 225, 340, 20);
        panelLogin.add(lblError);

        lblRegistro = new JLabel("<html><u style='color:blue'>¿No tienes cuenta? ¡Regístrate ahora!</u></html>");
        lblRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegistro.setBounds(90, 250, 260, 20);
        panelLogin.add(lblRegistro);

        btnEntrar.addActionListener(e -> {
            String usuario = txtUsuario.getText().trim();
            String pass = new String(txtContrasena.getPassword());

            int tipo = new ConexionBD().autenticarUsuario(usuario, pass);
            if (tipo >= 0) {
                lblError.setText("");

                if (chkRecordar.isSelected()) {
                    guardarUsuario(usuario);
                } else {
                    borrarUsuarioGuardado();
                }

                if (listener != null) listener.onLoginSuccess(usuario, tipo);
            } else {
                lblError.setText("Usuario o contraseña incorrecto");
            }
        });

        lblRegistro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) listener.onShowRegistro();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = getWidth();
                int alto = getHeight();
                panelFondo.setBounds(0, 0, ancho, alto);
                panelLogin.setBounds((ancho - 420) / 2, (alto - 300) / 2, 420, 300);
            }
        });

        cargarUsuarioGuardado();
    }

    private void guardarUsuario(String usuario) {
        try {
            File file = new File(System.getProperty("user.home"), "recordar.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(usuario);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarUsuarioGuardado() {
        try {
            File file = new File(System.getProperty("user.home"), "recordar.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String usuario = reader.readLine();
                    if (usuario != null) {
                        txtUsuario.setText(usuario);
                        chkRecordar.setSelected(true);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void borrarUsuarioGuardado() {
        File file = new File(System.getProperty("user.home"), "recordar.txt");
        if (file.exists()) {
            file.delete();
        }
    }

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
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
            g2.dispose();
        }
    }
}
