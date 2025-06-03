package es.studium.BarberApp;

public class Cita {
    private int id;
    private String clienteNombre;
    private int servicioId;
    private String servicioNombre;
    private String fecha;
    private String hora;
    private int usuarioId;

    public Cita(int id, String clienteNombre, int servicioId, String servicioNombre,
                String fecha, String hora, int usuarioId) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.fecha = fecha;
        this.hora = hora;
        this.usuarioId = usuarioId;
    }

    public int getId() { return id; }
    public String getClienteNombre() { return clienteNombre; }
    public int getServicioId() { return servicioId; }
    public String getServicioNombre() { return servicioNombre; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public int getUsuarioId() { return usuarioId; }
}
