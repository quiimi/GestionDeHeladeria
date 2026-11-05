package modelo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeladeriaSystem {

    private PersonaDAO personaDAO;
    private ProductoDAO productoDAO;
    private VentaDAO ventaDAO;

    private Map<String, Cliente> clientesMap;
    private Map<String, Bacha<SaborHelado>> mostradorBachas;
    private Cajero cajeroActivo;
    private Duenio duenioActual;

    public HeladeriaSystem() {
        this.personaDAO = new PersonaDAO();
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();

        this.clientesMap = new HashMap<>();
        this.mostradorBachas = new HashMap<>();

        DatabaseInitializer.inicializarBaseDeDatos();
        cargarDatosDesdeBD();
    }

    private void cargarDatosDesdeBD() {
        List<Cliente> lista = personaDAO.listarClientes();
        for (Cliente c : lista) {
            clientesMap.put(c.getDni(), c);
        }

        List<SaborHelado> sabores = productoDAO.obtenerTodosLosSabores();
        for (SaborHelado s : sabores) {
            mostradorBachas.put(s.getNombre().toLowerCase(), new Bacha<>(s, 10.0));
        }
    }

    public boolean registrarCliente(String dni, String nombre) {
        Cliente nuevo = new Cliente(dni, nombre);
        if (personaDAO.guardarPersona(nuevo)) {
            clientesMap.put(dni, nuevo);
            System.out.println("Cliente " + nombre + " registrado con exito.");
            return true;
        }
        return false;
    }

    public Cliente buscarCliente(String dni) {
        if (!clientesMap.containsKey(dni)) {
            Persona p = personaDAO.buscarPorDni(dni);
            if (p instanceof Cliente) {
                clientesMap.put(dni, (Cliente) p);
            }
        }
        return clientesMap.get(dni);
    }

    public void setCajeroActivo(Cajero cajero) {
        this.cajeroActivo = cajero;
        personaDAO.guardarPersona(cajero);
    }

    public void setDuenioActual(Duenio duenio) {
        this.duenioActual = duenio;
        personaDAO.guardarPersona(duenio);
    }

    public void darDeAltaSabor(String nombre, boolean sinTacc, boolean vegano, double capacidadBachaKg) {
        SaborHelado sabor = new SaborHelado(nombre, sinTacc, vegano);
        if (productoDAO.guardarSabor(sabor)) {
            Bacha<SaborHelado> bacha = new Bacha<>(sabor, capacidadBachaKg);
            mostradorBachas.put(nombre.toLowerCase(), bacha);
            System.out.println("Sabor " + nombre + " ingresado al mostrador.");
        }
    }

    public void recargarHeladoBacha(String nombreSabor, double kg) throws BachaLlenaException {
        Bacha<SaborHelado> bacha = mostradorBachas.get(nombreSabor.toLowerCase());
        if (bacha != null) {
            bacha.agregarHelado(kg);
            productoDAO.actualizarEstadoSabor(bacha.getSabor().getNombre(), bacha.getSabor().getEstado());
        }
    }

    public boolean realizarPedidoLocal(String dniCliente, int numeroMesa, List<Producto> productos, List<SaborHelado> saboresElegidos)
            throws SaborNoDisponibleException, BachaVaciaException {

        if (cajeroActivo == null) {
            throw new IllegalStateException("ERROR: No hay cajero activo asignado.");
        }

        cajeroActivo.tomarPedido(saboresElegidos);

        for (SaborHelado s : saboresElegidos) {
            Bacha<SaborHelado> bacha = mostradorBachas.get(s.getNombre().toLowerCase());
            if (bacha != null) {
                bacha.eliminarHelado(0.250);
                productoDAO.actualizarEstadoSabor(s.getNombre(), s.getEstado());
            }
        }

        Cliente cliente = buscarCliente(dniCliente);
        PedidoLocal pedido = new PedidoLocal(cliente, cajeroActivo, numeroMesa);
        for (Producto p : productos) {
            pedido.agregarProducto(p);
        }

        boolean exito = ventaDAO.registrarPedido(pedido);
        if (exito && cliente != null) {
            cliente.sumarPuntos((int) (pedido.calcularMontoTotal() / 100));
            personaDAO.guardarPersona(cliente);
        }
        return exito;
    }

    public boolean reservarCatering(String dniCliente, LocalDateTime fechaEvento, int cantPersonas, double senia, double total)
            throws ReservaEventoAgotadaException, ClienteNoRegistradoException {

        Cliente cliente = buscarCliente(dniCliente);
        if (cliente == null) {
            throw new ClienteNoRegistradoException("ERROR: El cliente con DNI " + dniCliente + " no se encuentra registrado.");
        }

        Catering catering = new Catering(cliente, fechaEvento, cantPersonas, senia, total);
        return ventaDAO.registrarCatering(catering);
    }

    public Cajero getCajeroActivo() { return cajeroActivo; }
    public Duenio getDuenioActual() { return duenioActual; }
    public Map<String, Bacha<SaborHelado>> getMostradorBachas() { return mostradorBachas; }
}

