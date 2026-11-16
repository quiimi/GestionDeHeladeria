package modelo;

import dao.*;
import excepciones.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeladeriaSystem {
    public static final int PUNTOS_POR_COMPRA = 10;
    public static final int PUNTOS_POR_CATERING = 50;

    private PersonaDAO personaDAO;
    private ProductoDAO productoDAO;
    private VentaDAO ventaDAO;
    private PrecioDAO precioDAO;

    private Map<String, Cliente> clientesMap;
    private Map<String, Cajero> cajerosMap;
    private Map<String, Bacha<SaborHelado>> mostradorBachas;
    private Map<String, Double> preciosMap;
    private Map<TipoPostre, Integer> stockPostres;

    private Cajero cajeroActivo;
    private Duenio duenioActual;

    // Inicializa el sistema y carga los datos guardados en la base de datos.
    public HeladeriaSystem() {
        this.personaDAO = new PersonaDAO();
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();
        this.precioDAO = new PrecioDAO();

        this.clientesMap = new HashMap<>();
        this.cajerosMap = new HashMap<>();
        this.mostradorBachas = new HashMap<>();
        this.preciosMap = new HashMap<>();
        this.stockPostres = new HashMap<>();

        DatabaseInitializer.inicializarBaseDeDatos();
        cargarDatosDesdeBD();
    }

    // Carga clientes, cajeros, sabores, precios y stock desde la base de datos.
    private void cargarDatosDesdeBD() {
        List<Cliente> listaClientes = personaDAO.listarClientes();
        for (Cliente c : listaClientes) {
            clientesMap.put(c.getDni(), c);
        }

        List<Cajero> listaCajeros = personaDAO.listarEmpleados();
        for (Cajero caj : listaCajeros) {
            cajerosMap.put(caj.getDni(), caj);
        }

        this.mostradorBachas = productoDAO.obtenerTodasLasBachas();
        this.preciosMap = precioDAO.obtenerTodosLosPrecios();
        this.stockPostres = productoDAO.obtenerStockPostres();
    }

    // Devuelve el precio asociado al concepto indicado.
    public double getPrecio(String concepto) {
        return preciosMap.getOrDefault(concepto, 0.0);
    }

    // Actualiza el precio en la base de datos y en el mapa local.
    public boolean actualizarPrecio(String concepto, double nuevoPrecio) {
        if (precioDAO.actualizarPrecio(concepto, nuevoPrecio)) {
            preciosMap.put(concepto, nuevoPrecio);
            return true;
        }
        return false;
    }

    // Devuelve el mapa con los precios del sistema.
    public Map<String, Double> getPreciosMap() {
        return preciosMap;
    }

    // Busca un cajero por DNI y lo establece como usuario activo.
    public boolean setCajeroActivoPorDni(String dni) {
        if (cajerosMap.containsKey(dni)) {
            this.cajeroActivo = cajerosMap.get(dni);
            return true;
        }
        Persona p = personaDAO.buscarPorDni(dni);
        // Un cajero dado de baja no puede iniciar sesion
        if (p instanceof Cajero && personaDAO.estaActivo(dni)) {
            Cajero c = (Cajero) p;
            cajerosMap.put(dni, c);
            this.cajeroActivo = c;
            return true;
        }
        return false;
    }


    // Registra un cajero nuevo o reactiva uno que estaba dado de baja.
    public boolean registrarCajero(String dni, String nombre, String username) {
        Persona existente = personaDAO.buscarPorDni(dni);

        // DNI nuevo: alta normal
        if (existente == null) {
            Cajero nuevoCajero = new Cajero(dni, nombre, username);

            if (personaDAO.guardarPersona(nuevoCajero)) {
                cajerosMap.put(dni, nuevoCajero);
                System.out.println("Cajero " + nombre + " (DNI: " + dni + ") registrado correctamente.");
                return true;
            }
            return false;
        }

        // El DNI pertenece a un cliente o al dueño
        if (!(existente instanceof Cajero)) {
            System.out.println("ERROR: El DNI " + dni + " ya pertenece a otra persona registrada.");
            return false;
        }

        // Ya es cajero y está activo
        if (personaDAO.estaActivo(dni)) {
            System.out.println("ERROR: Ya existe un cajero activo con el DNI " + dni + ".");
            return false;
        }

        // Era un cajero dado de baja: se reactiva conservando sus datos
        return reactivarCajero(dni);
    }

    // Busca un cajero que esté registrado pero dado de baja.
    public Cajero buscarCajeroDadoDeBaja(String dni) {
        Persona p = personaDAO.buscarPorDni(dni);
        if (p instanceof Cajero && !personaDAO.estaActivo(dni)) {
            return (Cajero) p;
        }
        return null;
    }


    // Vuelve a dar de alta un cajero que estaba dado de baja.
    public boolean reactivarCajero(String dni) {
        Cajero cajeroOriginal = buscarCajeroDadoDeBaja(dni);
        if (cajeroOriginal == null) {
            System.out.println("ERROR: No existe un cajero dado de baja con el DNI " + dni + ".");
            return false;
        }

        if (personaDAO.reactivarCajero(dni)) {
            cajerosMap.put(dni, cajeroOriginal);
            System.out.println("Cajero " + cajeroOriginal.getNombre()
                    + " (DNI: " + dni + ") dado de alta nuevamente.");
            return true;
        }

        return false;
    }

    // Da de baja al cajero indicado y lo quita de los datos activos.
    public boolean eliminarEmpleado(String dni) {
        if (personaDAO.darDeBajaCajero(dni)) {
            cajerosMap.remove(dni);

            if (cajeroActivo != null && cajeroActivo.getDni().equals(dni)) {
                cajeroActivo = null;
            }

            System.out.println("Cajero con DNI " + dni + " dado de baja.");
            return true;
        }
        System.out.println("ERROR: No existe un cajero activo con el DNI " + dni + ".");
        return false;
    }

    // Registra un cliente nuevo y lo agrega al mapa de clientes.
    public boolean registrarCliente(String dni, String nombre) throws ClienteYaExisteException {
        if (clientesMap.containsKey(dni) || personaDAO.buscarPorDni(dni) != null) {
            throw new ClienteYaExisteException("ERROR: Ya existe una persona registrada con el DNI " + dni + ".");
        }
        Cliente nuevo = new Cliente(dni, nombre);
        if (personaDAO.guardarPersona(nuevo)) {
            clientesMap.put(dni, nuevo);
            System.out.println("Cliente " + nombre + " registrado con exito.");
            return true;
        }
        return false;
    }

    // Busca un cliente por DNI y lo carga en el mapa si hace falta.
    public Cliente buscarCliente(String dni) {
        if (!clientesMap.containsKey(dni)) {
            Persona p = personaDAO.buscarPorDni(dni);
            if (p instanceof Cliente) {
                clientesMap.put(dni, (Cliente) p);
            }
        }
        return clientesMap.get(dni);
    }

    // Registra un sabor nuevo y crea su bacha en el mostrador.
    public void darDeAltaSabor(String nombre, boolean sinTacc, boolean vegano, double capacidadBachaKg) {
        SaborHelado sabor = new SaborHelado(nombre, sinTacc, vegano);
        if (productoDAO.guardarSabor(sabor, capacidadBachaKg)) {
            Bacha<SaborHelado> bacha = new Bacha<>(sabor, capacidadBachaKg);
            mostradorBachas.put(nombre.toLowerCase(), bacha);
            System.out.println("Sabor " + nombre + " ingresado al mostrador.");
        }
    }

    // Agrega kilos de helado a una bacha y actualiza su stock.
    public void recargarHeladoBacha(String nombreSabor, double kg) throws BachaLlenaException {
        Bacha<SaborHelado> bacha = mostradorBachas.get(nombreSabor.toLowerCase());
        if (bacha == null) {
            throw new IllegalArgumentException("ERROR: El sabor '" + nombreSabor + "' no existe en el mostrador.");
        }
        bacha.agregarHelado(kg);
        productoDAO.actualizarEstadoYStockSabor(bacha.getSabor().getNombre(), bacha.getSabor().getEstado(), bacha.getCantidadActualKg());
    }

    // Registra un pedido para consumir en el local y actualiza los stocks.
    public boolean realizarPedidoLocal(
            String dniCliente,
            int numeroMesa,
            List<Producto> productos,
            Map<SaborHelado, Double> consumoKilosPorSabor)
            throws SaborNoDisponibleException, BachaVaciaException {

        if (cajeroActivo == null || !cajeroActivo.isCajaAbierta()) {
            throw new IllegalStateException(
                    "ERROR: La caja debe estar abierta para registrar pedidos."
            );
        }

        if (!validarStockPostres(productos)) {
            return false;
        }

        validarConsumoBachas(consumoKilosPorSabor);

        cajeroActivo.tomarPedido(
                new ArrayList<>(consumoKilosPorSabor.keySet())
        );

        Cliente cliente = buscarCliente(dniCliente);

        PedidoLocal pedido = new PedidoLocal(
                cliente, cajeroActivo, numeroMesa
        );

        for (Producto p : productos) {
            pedido.agregarProducto(p);
        }

        boolean exito = ventaDAO.registrarPedido(pedido);

        if (exito) {

            descontarConsumoBachas(consumoKilosPorSabor);

            if (!descontarStockPostres(productos)) {
                System.out.println(
                        "ERROR: No se pudo actualizar el stock de los postres."
                );
                return false;
            }

            double total = pedido.calcularMontoTotal();

            cajeroActivo.registrarVenta(total, "LOCAL");

            if (cliente != null) {
                cliente.sumarPuntos(PUNTOS_POR_COMPRA);
                personaDAO.actualizarPuntosCliente(cliente);
            }
        }

        return exito;
    }

    // Registra un pedido para delivery y actualiza los stocks.
    public boolean realizarPedidoDelivery(
            String dniCliente,
            String direccion,
            double distanciaKm,
            List<Producto> productos,
            Map<SaborHelado, Double> consumoKilosPorSabor)
            throws SaborNoDisponibleException,
            BachaVaciaException,
            DistanciaEntregaExcedidaException {

        if (cajeroActivo == null || !cajeroActivo.isCajaAbierta()) {
            throw new IllegalStateException(
                    "ERROR: La caja debe estar abierta para registrar pedidos."
            );
        }

        if (distanciaKm > 15.0) {
            throw new DistanciaEntregaExcedidaException(
                    "ERROR: La distancia de entrega supera el limite maximo permitido (15 km)."
            );
        }

        if (!validarStockPostres(productos)) {
            return false;
        }

        validarConsumoBachas(consumoKilosPorSabor);

        cajeroActivo.tomarPedido(
                new ArrayList<>(consumoKilosPorSabor.keySet())
        );

        Cliente cliente = buscarCliente(dniCliente);

        PedidoDelivery pedido = new PedidoDelivery(
                cliente, cajeroActivo, direccion, distanciaKm
        );

        for (Producto p : productos) {
            pedido.agregarProducto(p);
        }

        boolean exito = ventaDAO.registrarPedido(pedido);

        if (exito) {

            descontarConsumoBachas(consumoKilosPorSabor);

            if (!descontarStockPostres(productos)) {
                System.out.println(
                        "ERROR: No se pudo actualizar el stock de los postres."
                );
                return false;
            }

            double total = pedido.calcularMontoTotal();

            cajeroActivo.registrarVenta(total, "DELIVERY");

            if (cliente != null) {
                cliente.sumarPuntos(PUNTOS_POR_COMPRA);
                personaDAO.actualizarPuntosCliente(cliente);
            }
        }

        return exito;
    }

    // Registra una reserva de catering y suma los puntos correspondientes al cliente.
    public boolean reservarCatering(String dniCliente, LocalDateTime fechaEvento, int cantPersonas, double senia)
            throws ReservaEventoAgotadaException, ClienteNoRegistradoException {
        if (cantPersonas <= 0) {
            throw new IllegalArgumentException(
                    "ERROR: La cantidad de personas debe ser mayor a cero."
            );
        }

        if (cajeroActivo == null || !cajeroActivo.isCajaAbierta()) {
            throw new IllegalStateException("ERROR: La caja debe estar abierta para registrar catering.");
        }

        Cliente cliente = buscarCliente(dniCliente);
        if (cliente == null) {
            throw new ClienteNoRegistradoException("ERROR: El cliente con DNI " + dniCliente + " no se encuentra registrado.");
        }

        double precioPorPersona = getPrecio("Catering");
        if (precioPorPersona <= 0) {
            precioPorPersona = 5000.0;
        }

        double total = cantPersonas * precioPorPersona;

        if (senia < 0 || senia > total) {
            throw new IllegalArgumentException(
                    "ERROR: La seña debe estar entre cero y el monto total."
            );
        }

        Catering catering = new Catering(cliente, cajeroActivo, fechaEvento, cantPersonas, senia, total);
        boolean exito = ventaDAO.registrarCatering(catering);

        if (exito) {
            cajeroActivo.registrarVenta(total, "CATERING");
            cliente.sumarPuntos(PUNTOS_POR_CATERING);
            personaDAO.actualizarPuntosCliente(cliente);
            System.out.println("Catering registrado | Personas: " + cantPersonas
                    + " | Precio/persona: $" + precioPorPersona
                    + " | Total registrado en caja: $" + total
                    + " | Seña recibida: $" + senia);
        }
        return exito;
    }

    // Devuelve el cajero que tiene la sesión activa.
    public Cajero getCajeroActivo() {
        return cajeroActivo;
    }

    // Devuelve el dueño que tiene la sesión activa.
    public Duenio getDuenioActual() {
        return duenioActual;
    }

    // Comprueba si ya hay un dueño registrado en el sistema.
    public boolean hayDuenioRegistrado() {
        if (duenioActual == null) {
            duenioActual = personaDAO.buscarDuenio();
        }
        return duenioActual != null;
    }

    // Registra al dueño de la heladería si sus datos son válidos.
    public boolean registrarDuenio(String dni, String nombre, String username) {
        if (dni.isEmpty() || nombre.isEmpty() || username.isEmpty()) {
            throw new IllegalArgumentException("ERROR: Todos los campos son obligatorios.");
        }
        if (personaDAO.buscarPorDni(dni) != null) {
            throw new IllegalArgumentException("ERROR: Ya existe una persona registrada con el DNI " + dni + ".");
        }
        Duenio nuevo = new Duenio(dni, nombre, username);
        if (personaDAO.guardarPersona(nuevo)) {
            this.duenioActual = nuevo;
            return true;
        }
        return false;
    }
    // Busca al dueño por DNI e inicia su sesión.
    public boolean iniciarSesionDuenio(String dni) {
        Persona p = personaDAO.buscarPorDni(dni);
        if (p instanceof Duenio) {
            this.duenioActual = (Duenio) p;
            return true;
        }
        return false;
    }

    // Muestra por consola el historial de ventas registrado.
    public void mostrarHistorialVentas() {
        List<String> historial = ventaDAO.obtenerHistorialVentas();

        System.out.println("\n========== HISTORIAL DE VENTAS ==========");

        if (historial.isEmpty()) {
            System.out.println("Todavía no hay ventas registradas.");
            return;
        }
        for (String venta : historial) {
            System.out.println("-----------------------------------------");
            System.out.println(venta);
        }
        System.out.println("-----------------------------------------");
        System.out.println("Cantidad de operaciones: " + historial.size());
        System.out.println("=========================================");
    }


    // Devuelve la cantidad disponible del tipo de postre indicado.
    public int getStockPostre(TipoPostre tipo) {
        return stockPostres.getOrDefault(tipo, 0);
    }

    // Comprueba que haya stock suficiente para los postres del pedido.
    private boolean validarStockPostres(List<Producto> productos) {
        Map<TipoPostre, Integer> cantidades = new HashMap<>();

        for (Producto producto : productos) {
            if (producto instanceof PostreHelado) {
                PostreHelado postre = (PostreHelado) producto;

                cantidades.put(
                        postre.getTipoPostre(),
                        cantidades.getOrDefault(postre.getTipoPostre(), 0) + 1
                );
            }
        }

        for (Map.Entry<TipoPostre, Integer> entrada : cantidades.entrySet()) {
            int disponible = getStockPostre(entrada.getKey());

            if (disponible < entrada.getValue()) {
                System.out.println("ERROR: Stock insuficiente de "
                        + entrada.getKey()
                        + ". Disponible: " + disponible);
                return false;
            }
        }

        return true;
    }

    // Descuenta del stock las unidades de postres incluidas en el pedido.
    private boolean descontarStockPostres(List<Producto> productos) {
        Map<TipoPostre, Integer> cantidades = new HashMap<>();

        for (Producto producto : productos) {
            if (producto instanceof PostreHelado) {
                PostreHelado postre = (PostreHelado) producto;

                cantidades.put(
                        postre.getTipoPostre(),
                        cantidades.getOrDefault(postre.getTipoPostre(), 0) + 1
                );
            }
        }

        for (Map.Entry<TipoPostre, Integer> entrada : cantidades.entrySet()) {
            TipoPostre tipo = entrada.getKey();
            int cantidad = entrada.getValue();

            if (!productoDAO.descontarStockPostre(tipo, cantidad)) {
                return false;
            }

            stockPostres.put(tipo, getStockPostre(tipo) - cantidad);
        }

        return true;
    }

    // Comprueba que las bachas tengan los kilos necesarios para el pedido.
    private void validarConsumoBachas(
            Map<SaborHelado, Double> consumoKilosPorSabor)
            throws BachaVaciaException {

        for (Map.Entry<SaborHelado, Double> entrada
                : consumoKilosPorSabor.entrySet()) {

            SaborHelado sabor = entrada.getKey();
            Double kgConsumidos = entrada.getValue();

            if (kgConsumidos == null || kgConsumidos <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad de helado debe ser mayor a cero."
                );
            }

            Bacha<SaborHelado> bacha =
                    mostradorBachas.get(sabor.getNombre().toLowerCase());

            if (bacha == null ||
                    bacha.getCantidadActualKg() < kgConsumidos) {

                throw new BachaVaciaException(
                        "No hay suficiente stock del sabor "
                                + sabor.getNombre()
                );
            }
        }
    }

    // Descuenta de las bachas los kilos utilizados y actualiza la base de datos.
    private void descontarConsumoBachas(
            Map<SaborHelado, Double> consumoKilosPorSabor)
            throws BachaVaciaException {

        for (Map.Entry<SaborHelado, Double> entrada
                : consumoKilosPorSabor.entrySet()) {

            SaborHelado sabor = entrada.getKey();
            double kgConsumidos = entrada.getValue();

            Bacha<SaborHelado> bacha =
                    mostradorBachas.get(sabor.getNombre().toLowerCase());

            bacha.eliminarHelado(kgConsumidos);

            boolean actualizado = productoDAO.actualizarEstadoYStockSabor(
                    sabor.getNombre(),
                    sabor.getEstado(),
                    bacha.getCantidadActualKg()
            );

            if (!actualizado) {
                System.err.println(
                        "ERROR: No se pudo actualizar el stock en la BD de "
                                + sabor.getNombre()
                );
            }
        }
    }


    // Devuelve el mapa de bachas que se encuentran en el mostrador.
    public Map<String, Bacha<SaborHelado>> getMostradorBachas() {
        return mostradorBachas;
    }

    // Devuelve la lista de empleados registrados.
    public List<Cajero> obtenerListaEmpleados() {
        return personaDAO.listarEmpleados();
    }

    // Devuelve la lista de clientes registrados.
    public List<Cliente> obtenerListaClientes() {
        return personaDAO.listarClientes();
    }
}
