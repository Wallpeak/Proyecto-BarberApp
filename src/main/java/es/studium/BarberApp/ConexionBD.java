package es.studium.BarberApp;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/barberapp";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "Studium2023;";

    /**
     * Abre conexión con la base de datos.
     * @return Connection establecida
     * @throws SQLException Si ocurre un error en la conexión
     */
    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    // ------------------ Métodos para Inventario ------------------

    /**
     * Inserta un nuevo artículo en el inventario.
     */
    public void agregarArticulo(String nombre, double precio, int stock) {
        String sql = "INSERT INTO Inventario (nombre, precio, stock) VALUES (?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setDouble(2, precio);
            stmt.setInt(3, stock);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar artículo: " + e.getMessage());
        }
    }

    /**
     * Modifica un artículo existente por ID.
     */
    public void modificarArticulo(int id, String nombre, double precio, int stock) {
        String sql = "UPDATE Inventario SET nombre = ?, precio = ?, stock = ? WHERE articulo_id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setDouble(2, precio);
            stmt.setInt(3, stock);
            stmt.setInt(4, id);
            int filas = stmt.executeUpdate();
            if (filas == 0) {
                System.err.println("No se encontró artículo con id: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar artículo: " + e.getMessage());
        }
    }

    /**
     * Elimina un artículo del inventario por ID.
     */
    public void eliminarArticulo(int id) {
        String sql = "DELETE FROM Inventario WHERE articulo_id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            if (filas == 0) {
                System.err.println("No se encontró artículo con id: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar artículo: " + e.getMessage());
        }
    }

    /**
     * Consulta todos los artículos ordenados por nombre.
     * @return Lista de strings con nombre y stock
     */
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
            System.err.println("Error al consultar inventario: " + e.getMessage());
        }
        return inventario;
    }

    /**
     * Consulta los 5 artículos con menor stock.
     * @return Lista de strings con nombre y stock
     */
    public ArrayList<Object[]> consultarMenorStock() {
        ArrayList<Object[]> articulos = new ArrayList<>();
        String sql = "SELECT nombre, stock FROM Inventario ORDER BY stock ASC LIMIT 18";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Crear un array con dos elementos: nombre y stock
                Object[] fila = new Object[2];
                fila[0] = rs.getString("nombre");
                fila[1] = rs.getInt("stock");
                articulos.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar menor stock: " + e.getMessage());
        }
        return articulos;
    }

    /**
     * Obtiene el ID del artículo dado su nombre.
     * @param nombre nombre del artículo
     * @return id del artículo o -1 si no se encuentra
     */
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
            System.err.println("Error al obtener ID de artículo: " + e.getMessage());
        }
        return id;
    }

    /**
     * Devuelve lista completa de productos con todos sus datos.
     */
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
            System.err.println("Error al obtener inventario: " + e.getMessage());
        }
        return lista;
    }

    // ------------------ Métodos para Citas ------------------

    /**
     * Agrega una nueva cita.
     */
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
            System.err.println("Error al agregar cita: " + e.getMessage());
        }
    }

    /**
     * Modifica una cita existente.
     */
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
            int filas = stmt.executeUpdate();
            if (filas == 0) {
                System.err.println("No se encontró cita con id: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar cita: " + e.getMessage());
        }
    }

    /**
     * Elimina una cita por id.
     */
    public void eliminarCita(int id) {
        String sql = "DELETE FROM Citas WHERE cita_id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            if (filas == 0) {
                System.err.println("No se encontró cita con id: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar cita: " + e.getMessage());
        }
    }

    /**
     * Consulta las citas de una fecha concreta ordenadas por hora.
     * @param fecha en formato "YYYY-MM-DD"
     * @return Lista de citas
     */
    public List<Cita> consultarCitasPorFecha(String fecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.cita_id, c.cliente_nombre, c.servicio_id, s.nombre AS servicio_nombre, c.fecha, c.hora, c.usuario_id "
                   + "FROM Citas c JOIN Servicios s ON c.servicio_id = s.servicio_id "
                   + "WHERE c.fecha = ? ORDER BY c.hora ASC";
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
            System.err.println("Error al consultar citas por fecha: " + e.getMessage());
        }
        return citas;
    }

    /**
     * Consulta las citas de un usuario concreto ordenadas por fecha y hora.
     * @param usuarioId ID del usuario
     * @return Lista de citas
     */
    public List<Cita> consultarCitasPorUsuario(int usuarioId) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.cita_id, c.cliente_nombre, c.servicio_id, s.nombre AS servicio_nombre, c.fecha, c.hora, c.usuario_id "
                   + "FROM Citas c JOIN Servicios s ON c.servicio_id = s.servicio_id "
                   + "WHERE c.usuario_id = ? ORDER BY c.fecha ASC, c.hora ASC";
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
            System.err.println("Error al consultar citas por usuario: " + e.getMessage());
        }
        return citas;
    }

    /**
     * Consulta las citas de un usuario en una fecha concreta.
     * @param usuarioId ID del usuario
     * @param fecha Fecha en formato "YYYY-MM-DD"
     * @return Lista de citas
     */
    public List<Cita> obtenerCitasPorUsuarioYFecha(int usuarioId, String fecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.cita_id, c.cliente_nombre, c.servicio_id, s.nombre AS servicio_nombre, c.fecha, c.hora, c.usuario_id "
                   + "FROM Citas c JOIN Servicios s ON c.servicio_id = s.servicio_id "
                   + "WHERE c.usuario_id = ? AND c.fecha = ? ORDER BY c.hora ASC";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, fecha);
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
            System.err.println("Error al obtener citas por usuario y fecha: " + e.getMessage());
        }
        return citas;
    }

    // ------------------ Métodos para Servicios ------------------

    /**
     * Obtiene la lista completa de servicios.
     * @return lista de servicios
     */
    public List<Map<String, Object>> obtenerServicios() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT servicio_id, nombre, precio FROM Servicios";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("servicio_id", rs.getInt("servicio_id"));
                fila.put("nombre", rs.getString("nombre"));
                fila.put("precio", rs.getDouble("precio"));
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener servicios: " + e.getMessage());
        }
        return lista;
    }


    // ------------------ Métodos para Usuarios ------------------

    /**
     * Autentica al usuario y devuelve el tipo de usuario.
     * @param usuario nombre de usuario
     * @param contrasena contraseña
     * @return tipo de usuario o -1 si no existe o error
     */
    public int autenticarUsuario(String usuario, String contrasena) {
        int tipoUsuario = -1;
        String sql = "SELECT tipo_usuario FROM Usuarios WHERE nombre_usuario = ? AND contrasena = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tipoUsuario = rs.getInt("tipo_usuario");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticarUsuario: " + e.getMessage());
        }
        return tipoUsuario;
    }

    /**
     * Verifica si un nombre de usuario ya existe.
     * @param nombreUsuario nombre usuario
     * @return true si existe, false si no
     */
    public boolean usuarioYaExiste(String nombreUsuario) {
        String sql = "SELECT 1 FROM Usuarios WHERE nombre_usuario = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error en usuarioYaExiste: " + e.getMessage());
        }
        return false;
    }

    /**
     * Agrega un nuevo usuario.
     */
    public void agregarUsuario(String nombreUsuario, String contrasena, String correo, int tipoUsuario) {
        String sql = "INSERT INTO Usuarios (nombre_usuario, contrasena,correo, tipo_usuario) VALUES (?, ?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            ps.setString(3, correo);
            ps.setInt(4, tipoUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar usuario: " + e.getMessage());
        }
    }

    public List<LocalTime> consultarHorasDisponibles(String fecha) {
        List<LocalTime> horasDisponibles = new ArrayList<>();
        // Horario de atención
        LocalTime inicio = LocalTime.of(9, 0);
        LocalTime fin = LocalTime.of(18, 0);
        // Intervalo de 30 minutos
        Duration intervalo = Duration.ofMinutes(30);

        // Primero obtengo las horas ocupadas en esa fecha desde la BD
        Set<LocalTime> horasOcupadas = new HashSet<>();

        String sql = "SELECT hora FROM Citas WHERE fecha = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    horasOcupadas.add(rs.getTime("hora").toLocalTime());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar horas ocupadas: " + e.getMessage());
            return horasDisponibles; // vacío en caso de error
        }

        // Ahora genero todas las posibles horas en el rango y filtro las ocupadas
        for (LocalTime hora = inicio; !hora.isAfter(fin.minus(intervalo)); hora = hora.plus(intervalo)) {
            if (!horasOcupadas.contains(hora)) {
                horasDisponibles.add(hora);
            }
        }

        return horasDisponibles;
    }

    public boolean existeUsuario(int usuarioId) {
        String sql = "SELECT 1 FROM Usuarios WHERE usuario_id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Si hay resultado, el usuario existe
            }
        } catch (SQLException e) {
            System.err.println("Error comprobando existencia de usuario: " + e.getMessage());
        }
        return false; // Si hay error o no existe, retornamos false
    }

    public ArrayList<Object[]> obtenerCitasPorUsuarioYFechaHoy(int usuarioId) {
        ArrayList<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.cita_id, c.cliente_nombre, s.nombre AS nombre_servicio, c.hora " +
                     "FROM citas c " +
                     "JOIN servicios s ON c.servicio_id = s.servicio_id " +
                     "WHERE c.usuario_id = ? AND c.fecha = CURRENT_DATE";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[4];
                    fila[0] = rs.getInt("cita_id");
                    fila[1] = rs.getString("cliente_nombre");
                    fila[2] = rs.getString("nombre_servicio");
                    fila[3] = rs.getString("hora");
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener citas: " + e.getMessage());
        }
        return lista;
    }


    public int obtenerIdUsuario(String nombreUsuario) {
        int idUsuario = -1;
        String sql = "SELECT usuario_id FROM Usuarios WHERE nombre_usuario = ?";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idUsuario = rs.getInt("usuario_id");
            }

            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener ID de usuario: " + e.getMessage());
        }

        return idUsuario;
    }


}
