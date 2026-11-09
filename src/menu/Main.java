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

        Duenio duenioInicial = new Duenio("23432543", "Administrador Duenio", "admin");
        sistema.setDuenioActual(duenioInicial);

        int opcionAcceso = 0;

        do {
            System.out.println("\n==========================================");
            System.out.println("--- SISTEMA DE GESTION DE HELADERIA ---");
            System.out.println("==========================================");
            System.out.println("1. Ingresar como DUEÑO (Administracion)");
            System.out.println("2. Ingresar como CAJERO (Operacion)");
            System.out.println("3. Salir del Sistema");

            opcionAcceso = Menu.leerEntero(teclado, "Seleccione su tipo de acceso: ");

            switch (opcionAcceso) {
                case 1:
                    menuDuenio(teclado, sistema);
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

    private static void menuDuenio(Scanner teclado, HeladeriaSystem sistema) {
        int opcion = 0;

        do {
            System.out.println("\n--- MENU DE ADMINISTRACION (DUEÑO) ---");
            System.out.println("1. Registrar Nuevo Cajero / Empleado");
            System.out.println("2. Eliminar Cajero / Empleado");
            System.out.println("3. Listar Empleados");
            System.out.println("4. Dar de Alta Nuevo Sabor / Bacha");
            System.out.println("5. Recargar Bacha de Helado");
            System.out.println("6. Ver Estado General de Bachas");
            System.out.println("7. Gestionar / Actualizar Precios");
            System.out.println("8. Volver al Menu Principal");

            opcion = Menu.leerEntero(teclado, "Ingrese una opcion: ");

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese DNI del nuevo cajero: ");
                    String dni = teclado.nextLine();
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
                    System.out.println("\n--- LISTADO DE EMPLEADOS ---");
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
                    System.out.println("\n--- SABORES EN MOSTRADOR PARA RECARGAR ---");
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
                    System.out.println("\n--- ESTADO DE BACHAS EN MOSTRADOR ---");
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
                    System.out.println("Volviendo al menu principal...");
                    break;

                default:
                    System.out.println("ERROR: Opcion invalida.");
                    break;
            }

        } while (opcion != 8);
    }

    private static void gestionarPrecios(Scanner teclado, HeladeriaSystem sistema) {
        System.out.println("\n--- LISTADO DE PRECIOS ACTUALES ---");
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
            System.out.println("\n--- MENU DE OPERACION (CAJERO: " + sistema.getCajeroActivo().getNombre() + ") ---");
            System.out.println("1. Seleccionar / Cambiar Cajero Activo");
            System.out.println("2. Abrir Caja");
            System.out.println("3. Cerrar Caja (Resumen y Registro de Ventas)");
            System.out.println("4. Registrar Cliente");
            System.out.println("5. Listar Clientes");
            System.out.println("6. Tomar Pedido Local");
            System.out.println("7. Tomar Pedido Delivery");
            System.out.println("8. Reservar Catering para Evento");
            System.out.println("9. Volver al Menu Principal");

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
                    System.out.println("\n--- LISTADO DE CLIENTES REGISTRADOS ---");
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

        } while (opcion != 9);
    }
}