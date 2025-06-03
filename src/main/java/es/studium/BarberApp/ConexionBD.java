package es.studium.BarberApp;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/barberapp";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "Studium2023;";

    /**
     * Abre conexión con la base de datos.
     */
    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    // ------------------ Inventario ------------------
    public void agregarArticulo(String nombre, double precio, int stock) {
        String sql = "INSERT INTO Inventario (nombre, precio, stock) VALUES (?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setDouble(2, precio);
            stmt.setInt(3, stock);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modificarArticulo(int id, String nombre, double precio, int stock) {
        String sql = "UPDATE Inventario SET nombre = ?, precio = ?, stock = ? WHERE articulo_id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setDouble(2, precio);
            stmt.setInt(3, stock);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarArticulo(int id) {
        String sql = "DELETE FROM Inventario WHERE articulo_id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> consultarInventarioOrdenado() {
        List<String> inventario = new ArrayList<>();
        String sql = "SELECT nombre, stock FROM Inventario ORDER BY nombre ASC";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                inventario.add(rs.getString("nombre") + " - Stock: " + rs.getInt("stock"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventario;
    }

    public List<String> consultarMenorStock() {
        List<String> articulos = new ArrayList<>();
        String sql = "SELECT nombre, stock FROM Inventario ORDER BY stock ASC LIMIT 5";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                articulos.add(rs.getString("nombre") + " - Stock: " + rs.getInt("stock"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articulos;
    }
    public int obtenerIdArticuloPorNombre(String nombre) {
        int id = -1;
        String sql = "SELECT articulo_id FROM Inventario WHERE nombre = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("articulo_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    // ------------------ Citas ------------------
    public void agregarCita(String clienteNombre, int servicioId, String fecha, String hora, int usuarioId) {
        String sql = "INSERT INTO Citas (cliente_nombre, servicio_id, fecha, hora, usuario_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clienteNombre);
            stmt.setInt(2, servicioId);
            stmt.setString(3, fecha);
            stmt.setString(4, hora);
            stmt.setInt(5, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modificarCita(int id, String clienteNombre, int servicioId, String fecha, String hora, int usuarioId) {
        String sql = "UPDATE Citas SET cliente_nombre = ?, servicio_id = ?, fecha = ?, hora = ?, usuario_id = ? WHERE cita_id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clienteNombre);
            stmt.setInt(2, servicioId);
            stmt.setString(3, fecha);
            stmt.setString(4, hora);
            stmt.setInt(5, usuarioId);
            stmt.setInt(6, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarCita(int id) {
        String sql = "DELETE FROM Citas WHERE cita_id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Cita> consultarCitasPorFecha(String fecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.cita_id, c.cliente_nombre, c.servicio_id, s.nombre AS servicio_nombre, c.fecha, c.hora, c.usuario_id"
                   + " FROM Citas c JOIN Servicios s ON c.servicio_id = s.servicio_id"
                   + " WHERE c.fecha = ? ORDER BY c.hora ASC";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    citas.add(new Cita(
                        rs.getInt("cita_id"),
                        rs.getString("cliente_nombre"),
                        rs.getInt("servicio_id"),
                        rs.getString("servicio_nombre"),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getInt("usuario_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }
    public List<Cita> obtenerCitasPorUsuarioYFecha(int usuarioId, LocalDate fechaSeleccionada) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT id, cliente_nombre, servicio_id, servicio_nombre, fecha, hora, usuario_id " +
                     "FROM citas " +
                     "WHERE usuario_id = ? AND fecha = ? " +
                     "ORDER BY hora";
        try (Connection conn = conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, fechaSeleccionada.toString()); // Formato "YYYY-MM-DD"

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String clienteNombre = rs.getString("cliente_nombre");
                    int servicioId = rs.getInt("servicio_id");
                    String servicioNombre = rs.getString("servicio_nombre");
                    String fecha = rs.getString("fecha");
                    String hora = rs.getString("hora");
                    int usrId = rs.getInt("usuario_id");

                    Cita cita = new Cita(id, clienteNombre, servicioId, servicioNombre, fecha, hora, usrId);
                    citas.add(cita);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }


    public Cita obtenerProximaCita(String fecha) {
        List<Cita> citas = consultarCitasPorFecha(fecha);
        return citas.isEmpty() ? null : citas.get(0);
    }

    public List<String> consultarHorasPorFecha(String fecha) {
        List<String> horas = new ArrayList<>();
        String sql = "SELECT hora FROM Citas WHERE fecha = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    horas.add(rs.getString("hora"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return horas;
    }

    public List<LocalTime> consultarHorasDisponibles(String fecha) {
        List<LocalTime> disponibles = new ArrayList<>();
        List<LocalTime> ocupadas = new ArrayList<>();
        String sql = "SELECT hora FROM Citas WHERE fecha = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ocupadas.add(LocalTime.parse(rs.getString("hora")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(14, 30);
        while (!inicio.isAfter(fin)) {
            if (!ocupadas.contains(inicio)) disponibles.add(inicio);
            inicio = inicio.plusMinutes(30);
        }
        inicio = LocalTime.of(18, 0);
        fin = LocalTime.of(21, 30);
        while (!inicio.isAfter(fin)) {
            if (!ocupadas.contains(inicio)) disponibles.add(inicio);
            inicio = inicio.plusMinutes(30);
        }
        return disponibles;
    }

    public List<String> obtenerServicios() {
        List<String> servicios = new ArrayList<>();
        String sql = "SELECT nombre FROM Servicios";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) servicios.add(rs.getString("nombre"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicios;
    }

    /**
     * Obtiene todos los datos de una cita en una fecha y hora específicas.
     * Devuelve un Map con keys: "id", "nombre", "servicio_id", "hora".
     */
    public Map<String, String> obtenerCitaPorFechaYHora(String fecha, String hora) {
        Map<String, String> cita = new HashMap<>();
        String sql = "SELECT cita_id, cliente_nombre, servicio_id, hora FROM Citas WHERE fecha = ? AND hora = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fecha);
            ps.setString(2, hora);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cita.put("id", String.valueOf(rs.getInt("cita_id")));
                    cita.put("nombre", rs.getString("cliente_nombre"));
                    cita.put("servicio_id", String.valueOf(rs.getInt("servicio_id")));
                    cita.put("hora", rs.getString("hora"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cita;
    }
    
    
    public int autenticarUsuario(String usuario, String contrasena) {
        int tipoUsuario = -1;
        try {
            Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            String sql = "SELECT tipo_usuario FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tipoUsuario = rs.getInt("tipo_usuario");
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipoUsuario;
    }

    /**
     * Inserta un nuevo usuario con tipousuario = 0.
     */
    public void agregarUsuario(String nombreUsuario, String correo, String contrasena) {
        String sql = "INSERT INTO usuarios (nombre_usuario, correo, contrasena, tipo_usuario) VALUES (?, ?, ?, 0)";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, correo);
            ps.setString(3, contrasena);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int obtenerIdUsuario(String nombreUsuario) {
        int id = -1;
        String sql = "SELECT usuario_id FROM usuarios WHERE nombre_usuario = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("usuario_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    public List<Cita> consultarCitasPorUsuario(int usuarioId) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.cita_id, c.cliente_nombre, c.servicio_id, s.nombre AS servicio_nombre, c.fecha, c.hora, c.usuario_id "
                   + "FROM Citas c JOIN Servicios s ON c.servicio_id = s.servicio_id "
                   + "WHERE c.usuario_id = ? ORDER BY c.fecha, c.hora";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    citas.add(new Cita(
                        rs.getInt("cita_id"),
                        rs.getString("cliente_nombre"),
                        rs.getInt("servicio_id"),
                        rs.getString("servicio_nombre"),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getInt("usuario_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }
    public List<Producto> obtenerInventario() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT articulo_id, nombre, precio, stock FROM Inventario";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Producto(
                    rs.getInt("articulo_id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean usuarioYaExiste(String nombreUsuario, String correo) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nombre_usuario = ? OR correo = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Integer> obtenerServiciosConId() {
        Map<String, Integer> mapa = new HashMap<>();
        String sql = "SELECT servicio_id, nombre FROM Servicios";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getString("nombre"), rs.getInt("servicio_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }
    public ArrayList<Object[]> obtenerArticulosConMenorStock() {
        ArrayList<Object[]> lista = new ArrayList<>();
        String sql = "SELECT nombre, stock FROM inventario WHERE stock < 10 ORDER BY stock ASC LIMIT 10";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{rs.getString("nombre"), rs.getInt("stock")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public ArrayList<Object[]> obtenerCitasPorUsuarioYFechaHoy(int idUsuario) {
        ArrayList<Object[]> lista = new ArrayList<>();
        // Seleccionamos hora, cliente_nombre y nombre del servicio (resuelto con JOIN).
        String sql = "SELECT C.hora, C.cliente_nombre AS cliente, S.nombre AS servicio " +
                     "FROM Citas C " +
                     "INNER JOIN Servicios S ON C.servicio_id = S.servicio_id " +
                     "WHERE C.usuario_id = ? AND C.fecha = CURDATE() " +
                     "ORDER BY C.hora ASC";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario); 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // rs.getTime("hora") devuelve un java.sql.Time,
                    // pero lo obtendremos como String para que aparezca "HH:mm:ss" o parecido.
                    lista.add(new Object[]{
                        rs.getString("hora"),
                        rs.getString("cliente"),
                        rs.getString("servicio")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}