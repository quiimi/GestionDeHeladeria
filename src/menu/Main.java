package menu;

import excepciones.*;
import modelo.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        HeladeriaSystem sistema = new HeladeriaSystem();

        if (!sistema.hayDuenioRegistrado()) {
            registrarPrimerDuenio(teclado, sistema);
        }

        int opcionAcceso = 0;

        do {
            mostrarMenuAcceso();

            opcionAcceso = Menu.leerEntero(teclado, "Seleccione su tipo de acceso: ");

            switch (opcionAcceso) {
                case 1:
                    System.out.print("Ingrese su DNI de dueño: ");
                    String dniDuenio = teclado.nextLine().trim();
                    if (sistema.iniciarSesionDuenio(dniDuenio)) {
                        menuDuenio(teclado, sistema);
                    } else {
                        System.out.println("ERROR: No existe un dueño registrado con ese DNI.");
                    }
                    break;
                case 2:
                    menuCajero(teclado, sistema);
                    break;
                case 3:
                    System.out.println("Cerrando aplicacion...");
                    break;
                default:
                    System.out.println("ERROR: Opcion invalida. Reintente.");
                    break;
            }

        } while (opcionAcceso != 3);

        teclado.close();
    }

    private static void registrarPrimerDuenio(Scanner teclado, HeladeriaSystem sistema) {
        System.out.println("\n============================================================");
        System.out.println("                  CONFIGURACION INICIAL");
        System.out.println("============================================================");
        System.out.println("No hay un dueño registrado. Cargue sus datos para continuar.");

        boolean registrado = false;
        do {
            System.out.print("DNI: ");
            String dni = teclado.nextLine().trim();
            System.out.print("Nombre completo: ");
            String nombre = teclado.nextLine().trim();
            System.out.print("Username: ");
            String username = teclado.nextLine().trim();

            try {
                registrado = sistema.registrarDuenio(dni, nombre, username);
                if (!registrado) {
                    System.out.println("ERROR: No se pudo guardar el dueño. Reintente.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (!registrado);

        System.out.println("Dueño registrado correctamente.");
    }

    private static void menuDuenio(Scanner teclado, HeladeriaSystem sistema) {
        int opcion = 0;

        do {
            mostrarMenuDuenio();

            opcion = Menu.leerEntero(teclado, "Ingrese una opcion: ");

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese DNI del nuevo cajero: ");
                    String dni = teclado.nextLine();

                    // Si era un cajero dado de baja, se reactiva con sus datos guardados
                    if (sistema.buscarCajeroDadoDeBaja(dni) != null) {
                        sistema.reactivarCajero(dni);
                        break;
                    }

                    System.out.print("Ingrese Nombre completo: ");
                    String nombre = teclado.nextLine();
                    System.out.print("Ingrese Username de acceso: ");
                    String user = teclado.nextLine();
                    sistema.registrarCajero(dni, nombre, user);
                    break;

                case 2:
                    System.out.print("Ingrese DNI del empleado a eliminar: ");
                    String dniEliminar = teclado.nextLine();
                    sistema.eliminarEmpleado(dniEliminar);
                    break;

                case 3:
                    System.out.println("\n------------------------------------------------------------");
                    System.out.println("                    EMPLEADOS REGISTRADOS");
                    System.out.println("------------------------------------------------------------");
                    List<Cajero> empleados = sistema.obtenerListaEmpleados();
                    if (empleados.isEmpty()) {
                        System.out.println("No hay empleados registrados.");
                    } else {
                        for (Cajero emp : empleados) {
                            System.out.println("DNI: " + emp.getDni() + " | Nombre: " + emp.getNombre() + " | Username: " + emp.getUsername());
                        }
                    }
                    break;

                case 4:
                    System.out.print("Ingrese nombre del nuevo sabor: ");
                    String saborNombre = teclado.nextLine();
                    System.out.print("Es apto para celiacos (true/false)?: ");
                    boolean sinTacc = teclado.nextBoolean();
                    System.out.print("Es apto para veganos (true/false)?: ");
                    boolean vegano = teclado.nextBoolean();
                    teclado.nextLine();

                    double capacidad = Menu.leerDouble(teclado, "Ingrese capacidad maxima de la bacha en kg: ");
                    sistema.darDeAltaSabor(saborNombre, sinTacc, vegano, capacidad);
                    break;

                case 5:
                    System.out.println("\n------------------------------------------------------------");
                    System.out.println("                  SABORES DISPONIBLES");
                    System.out.println("------------------------------------------------------------");
                    if (sistema.getMostradorBachas().isEmpty()) {
                        System.out.println("No hay sabores ingresados en el sistema.");
                        break;
                    }

                    // 1. Mostrar todos los sabores cargados con su capacidad y stock actual
                    for (String clave : sistema.getMostradorBachas().keySet()) {
                        Bacha<SaborHelado> b = sistema.getMostradorBachas().get(clave);
                        System.out.println("- " + b.getSabor().getNombre()
                                + " | Stock: " + b.getCantidadActualKg() + " kg / " + b.getCapacidadMaxKg() + " kg"
                                + " | Estado: " + b.getSabor().getEstado());
                    }

                    String saborRecarga = "";
                    boolean saborValido = false;

                    // 2. Validar que el nombre ingresado pertenezca a un sabor existente
                    do {
                        System.out.print("\nIngrese nombre del sabor a recargar: ");
                        saborRecarga = teclado.nextLine().trim();

                        if (sistema.getMostradorBachas().containsKey(saborRecarga.toLowerCase())) {
                            saborValido = true;
                        } else {
                            System.out.println("ERROR: El sabor '" + saborRecarga + "' no existe. Elija uno de la lista.");
                        }
                    } while (!saborValido);

                    double kg = Menu.leerDouble(teclado, "Ingrese kilos a agregar: ");

                    try {
                        sistema.recargarHeladoBacha(saborRecarga, kg);
                        System.out.println("Bacha recargada con exito.");
                    } catch (BachaLlenaException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case 6:
                    System.out.println("\n------------------------------------------------------------");
                    System.out.println("                   ESTADO DE LAS BACHAS");
                    System.out.println("------------------------------------------------------------");
                    for (String clave : sistema.getMostradorBachas().keySet()) {
                        Bacha<SaborHelado> b = sistema.getMostradorBachas().get(clave);
                        System.out.println("Sabor: " + b.getSabor().getNombre()
                                + " | Stock: " + b.getCantidadActualKg() + " kg / " + b.getCapacidadMaxKg() + " kg"
                                + " | Estado: " + b.getSabor().getEstado());
                    }
                    break;

                case 7:
                    gestionarPrecios(teclado, sistema);
                    break;

                case 8:
                    sistema.mostrarHistorialVentas();
                    break;

                case 9:
                    System.out.println("Volviendo al menu principal...");
                    break;

                default:
                    System.out.println("ERROR: Opcion invalida.");
                    break;
            }

            System.out.println();
        } while (opcion != 9);
    }

    private static void gestionarPrecios(Scanner teclado, HeladeriaSystem sistema) {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("                    PRECIOS ACTUALES");
        System.out.println("------------------------------------------------------------");
        Map<String, Double> precios = sistema.getPreciosMap();
        List<String> claves = new ArrayList<>(precios.keySet());

        for (int i = 0; i < claves.size(); i++) {
            System.out.println((i + 1) + ". " + claves.get(i) + ": $" + precios.get(claves.get(i)));
        }

        int seleccion = Menu.leerEntero(teclado, "Seleccione numero de precio a modificar (0 para cancelar): ");
        if (seleccion < 1 || seleccion > claves.size()) {
            System.out.println("Operacion cancelada.");
            return;
        }

        String conceptoElegido = claves.get(seleccion - 1);
        double nuevoValor = Menu.leerDouble(teclado, "Ingrese nuevo valor para " + conceptoElegido + ": $");

        if (sistema.actualizarPrecio(conceptoElegido, nuevoValor)) {
            System.out.println("Precio de " + conceptoElegido + " actualizado con exito a $" + nuevoValor);
        } else {
            System.out.println("ERROR: No se pudo actualizar el precio en la base de datos.");
        }
    }

    private static void menuCajero(Scanner teclado, HeladeriaSystem sistema) {
        if (sistema.getCajeroActivo() == null) {
            System.out.print("Ingrese DNI del cajero para iniciar sesion: ");
            String dniSesion = teclado.nextLine();
            if (sistema.setCajeroActivoPorDni(dniSesion)) {
                System.out.println("Sesion iniciada para el cajero: " + sistema.getCajeroActivo().getNombre());
            } else {
                System.out.println("ERROR: No se encontro un cajero registrado con el DNI ingresado.");
                return;
            }
        }

        int opcion = 0;

        do {
            mostrarMenuCajero(sistema.getCajeroActivo().getNombre());

            opcion = Menu.leerEntero(teclado, "Ingrese una opcion: ");

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese DNI del nuevo cajero activo: ");
                    String nuevoDni = teclado.nextLine();
                    if (sistema.setCajeroActivoPorDni(nuevoDni)) {
                        System.out.println("Cajero activo actualizado a: " + sistema.getCajeroActivo().getNombre());
                    } else {
                        System.out.println("ERROR: No se encontro un cajero registrado con ese DNI.");
                    }
                    break;

                case 2:
                    sistema.getCajeroActivo().abrirCaja();
                    break;

                case 3:
                    sistema.getCajeroActivo().cerrarCaja();
                    break;

                case 4:
                    System.out.print("Ingrese DNI del cliente: ");
                    String dniCli = teclado.nextLine();
                    System.out.print("Ingrese Nombre del cliente: ");
                    String nomCli = teclado.nextLine();

                    try {
                        sistema.registrarCliente(dniCli, nomCli);
                    } catch (ClienteYaExisteException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 5:
                    System.out.println("\n------------------------------------------------------------");
                    System.out.println("                   CLIENTES REGISTRADOS");
                    System.out.println("------------------------------------------------------------");
                    List<Cliente> clientes = sistema.obtenerListaClientes();
                    if (clientes.isEmpty()) {
                        System.out.println("No hay clientes registrados.");
                    } else {
                        for (Cliente c : clientes) {
                            System.out.println("DNI: " + c.getDni() + " | Nombre: " + c.getNombre() + " | Puntos: " + c.getPuntosFidelidad());
                        }
                    }
                    break;

                case 6:
                    if (!sistema.getCajeroActivo().isCajaAbierta()) {
                        System.out.println("ERROR: La caja debe estar abierta para realizar pedidos.");
                        break;
                    }

                    System.out.print("Ingrese DNI del cliente: ");
                    String dniCliente = teclado.nextLine();
                    Cliente cliente = sistema.buscarCliente(dniCliente);
                    if (cliente == null) {
                        System.out.println("ERROR: Cliente no registrado. Registre al cliente primero.");
                        break;
                    }

                    int mesa = 0;
                    do {
                        mesa = Menu.leerEntero(teclado, "Ingrese numero de mesa (1 a 10): ");
                        if (mesa < 1 || mesa > 10) {
                            System.out.println("ERROR: Numero de mesa invalido. El local dispone de 10 mesas.");
                        }
                    } while (mesa < 1 || mesa > 10);

                    List<Producto> productosLocal = new ArrayList<>();
                    Map<SaborHelado, Double> consumoLocal = Menu.armarProductosYSabores(teclado, sistema, productosLocal);
                    if (productosLocal.isEmpty()) break;

                    try {
                        boolean ok = sistema.realizarPedidoLocal(dniCliente, mesa, productosLocal, consumoLocal);
                        if (ok) {
                            System.out.println("Pedido local procesado y registrado con exito.");
                        } else {
                            System.out.println("ERROR: No se pudo registrar el pedido.");
                        }
                    } catch (SaborNoDisponibleException | BachaVaciaException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case 7:
                    if (!sistema.getCajeroActivo().isCajaAbierta()) {
                        System.out.println("ERROR: La caja debe estar abierta para realizar pedidos.");
                        break;
                    }

                    System.out.print("Ingrese DNI del cliente: ");
                    String dniDelivery = teclado.nextLine();
                    Cliente clienteDel = sistema.buscarCliente(dniDelivery);
                    if (clienteDel == null) {
                        System.out.println("ERROR: Cliente no registrado. Registre al cliente primero.");
                        break;
                    }

                    System.out.print("Ingrese direccion de entrega: ");
                    String direccion = teclado.nextLine();
                    double distancia = Menu.leerDouble(teclado, "Ingrese distancia en km: ");

                    // Validacion inmediata antes de solicitar productos y armar sabores
                    if (distancia > 15.0) {
                        System.out.println("ERROR: La distancia de entrega (" + distancia + " km) excede el limite maximo permitido de 15 km.");
                        break;
                    }

                    List<Producto> productosDelivery = new ArrayList<>();
                    Map<SaborHelado, Double> consumoDelivery = Menu.armarProductosYSabores(teclado, sistema, productosDelivery);
                    if (productosDelivery.isEmpty()) break;

                    try {
                        boolean okDel = sistema.realizarPedidoDelivery(dniDelivery, direccion, distancia, productosDelivery, consumoDelivery);
                        if (okDel) {
                            System.out.println("Pedido delivery procesado y registrado con exito.");
                        } else {
                            System.out.println("ERROR: No se pudo registrar el pedido delivery.");
                        }
                    } catch (SaborNoDisponibleException | BachaVaciaException | DistanciaEntregaExcedidaException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case 8:
                    if (!sistema.getCajeroActivo().isCajaAbierta()) {
                        System.out.println("ERROR: La caja debe estar abierta para reservar catering.");
                        break;
                    }

                    System.out.print("Ingrese DNI del cliente: ");
                    String dniCatering = teclado.nextLine();
                    if (sistema.buscarCliente(dniCatering) == null) {
                        System.out.println("ERROR: Cliente no registrado. Registre al cliente primero.");
                        break;
                    }

                    int cantPersonas = Menu.leerEntero(teclado, "Ingrese cantidad de personas: ");
                    double precioPersona = sistema.getPrecio("Catering");
                    if (precioPersona <= 0) {
                        precioPersona = 5000.0;
                    }

                    double totalCalculado = cantPersonas * precioPersona;
                    System.out.println("Precio por persona: $" + precioPersona);
                    System.out.println("Monto total del catering: $" + totalCalculado);

                    double senia = 0;
                    do {
                        senia = Menu.leerDouble(teclado, "Ingrese monto de seña abonado: ");
                        if (senia > totalCalculado) {
                            System.out.println("ERROR: La seña no puede ser mayor al monto total ($" + totalCalculado + ").");
                        }
                    } while (senia > totalCalculado);

                    try {
                        boolean okCat = sistema.reservarCatering(dniCatering, LocalDateTime.now().plusDays(7), cantPersonas, senia);
                        if (okCat) {
                            System.out.println("Reserva de catering registrada con exito.");
                        }
                    } catch (ReservaEventoAgotadaException | ClienteNoRegistradoException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case 9:
                    System.out.println("Volviendo al menu principal...");
                    break;

                default:
                    System.out.println("ERROR: Opcion invalida.");
                    break;
            }

            System.out.println();
        } while (opcion != 9);
    }

    // ------------------------------------------------------------
    // METODOS DE PRESENTACION DE MENUS
    // ------------------------------------------------------------

    private static void mostrarMenuAcceso() {
        System.out.println("\n");
        System.out.println("============================================================");
        System.out.println("                  HELADERIA - SISTEMA DE GESTION");
        System.out.println("============================================================");
        System.out.println();
        System.out.println("  ACCESO AL SISTEMA");
        System.out.println("  ----------------------------------------------------------");
        System.out.println("    [1]  Ingresar como DUENO       - Administracion");
        System.out.println("    [2]  Ingresar como CAJERO      - Operacion");
        System.out.println();
        System.out.println("    [3]  Salir del sistema");
        System.out.println("  ----------------------------------------------------------");
    }

    private static void mostrarMenuDuenio() {
        System.out.println("\n");
        System.out.println("============================================================");
        System.out.println("                    PANEL DE ADMINISTRACION");
        System.out.println("============================================================");

        System.out.println("\n  GESTION DE PERSONAL");
        System.out.println("    [1]  Registrar nuevo cajero / empleado");
        System.out.println("    [2]  Dar de baja cajero / empleado");
        System.out.println("    [3]  Listar empleados");

        System.out.println("\n  GESTION DE HELADOS");
        System.out.println("    [4]  Dar de alta nuevo sabor / bacha");
        System.out.println("    [5]  Recargar bacha de helado");
        System.out.println("    [6]  Ver estado general de bachas");

        System.out.println("\n  ADMINISTRACION");
        System.out.println("    [7]  Gestionar / actualizar precios");
        System.out.println("    [8]  Consultar historial de ventas");

        System.out.println("\n    [9]  Volver al menu principal");
        System.out.println("------------------------------------------------------------");
    }

    private static void mostrarMenuCajero(String nombreCajero) {
        System.out.println("\n");
        System.out.println("============================================================");
        System.out.println("                       PANEL DE CAJA");
        System.out.println("============================================================");
        System.out.println("  Cajero activo: " + nombreCajero);
        System.out.println("------------------------------------------------------------");

        System.out.println("\n  CAJA");
        System.out.println("    [1]  Seleccionar / cambiar cajero activo");
        System.out.println("    [2]  Abrir caja");
        System.out.println("    [3]  Cerrar caja y ver resumen");

        System.out.println("\n  CLIENTES");
        System.out.println("    [4]  Registrar cliente");
        System.out.println("    [5]  Listar clientes");

        System.out.println("\n  PEDIDOS Y RESERVAS");
        System.out.println("    [6]  Tomar pedido local");
        System.out.println("    [7]  Tomar pedido delivery");
        System.out.println("    [8]  Reservar catering para evento");

        System.out.println("\n    [9]  Volver al menu principal");
        System.out.println("------------------------------------------------------------");
    }
}

