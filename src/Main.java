import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        HeladeriaSystem sistema = new HeladeriaSystem();

        Duenio duenio = new Duenio("11111111", "Carlos Gomez", "duenio1");
        Cajero cajero = new Cajero("22222222", "Ana Lopez", "cajero1");

        sistema.setDuenioActual(duenio);
        sistema.setCajeroActivo(cajero);

        sistema.darDeAltaSabor("Chocolate", false, false, 10.0);
        sistema.darDeAltaSabor("Frutilla", false, true, 10.0);
        sistema.darDeAltaSabor("Dulce de Leche", false, false, 10.0);

        int opcion = 0;

        do {
            System.out.println("\n--- SISTEMA DE GESTION DE HELADERIA ---");
            System.out.println("1. Registrar Cliente");
            System.out.println("2. Abrir Caja (Cajero)");
            System.out.println("3. Cerrar Caja (Cajero)");
            System.out.println("4. Realizar Pedido Local");
            System.out.println("5. Reservar Evento (Catering)");
            System.out.println("6. Recargar Bacha de Helado");
            System.out.println("7. Ver Estado de Mostrador");
            System.out.println("8. Salir");
            System.out.print("Ingrese una opcion: ");

            if (teclado.hasNextInt()) {
                opcion = teclado.nextInt();
                teclado.nextLine();
            } else {
                System.out.println("ERROR: Debe ingresar un numero entero.");
                teclado.nextLine();
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese DNI del cliente: ");
                    String dni = teclado.nextLine();
                    System.out.print("Ingrese Nombre del cliente: ");
                    String nombre = teclado.nextLine();
                    sistema.registrarCliente(dni, nombre);
                    break;

                case 2:
                    if (sistema.getCajeroActivo() != null) {
                        sistema.getCajeroActivo().abrirCaja();
                    } else {
                        System.out.println("ERROR: No hay cajero activo asignado.");
                    }
                    break;

                case 3:
                    if (sistema.getCajeroActivo() != null) {
                        sistema.getCajeroActivo().cerrarCaja();
                    } else {
                        System.out.println("ERROR: No hay cajero activo asignado.");
                    }
                    break;

                case 4:
                    System.out.print("Ingrese DNI del cliente: ");
                    String dniCliente = teclado.nextLine();
                    Cliente cliente = sistema.buscarCliente(dniCliente);

                    if (cliente == null) {
                        System.out.println("ERROR: Cliente no registrado. Registre al cliente primero.");
                        break;
                    }

                    System.out.print("Ingrese numero de mesa: ");
                    int mesa = teclado.nextInt();
                    teclado.nextLine();

                    List<SaborHelado> sabores = new ArrayList<>();
                    Bacha<SaborHelado> bachaChocolate = sistema.getMostradorBachas().get("chocolate");
                    if (bachaChocolate != null) {
                        sabores.add(bachaChocolate.getSabor());
                    }

                    List<Producto> productos = new ArrayList<>();
                    ConoHelado cono = new ConoHelado(1, "Cono Doble", 1200.0, 2, sabores);
                    productos.add(cono);

                    try {
                        boolean ok = sistema.realizarPedidoLocal(dniCliente, mesa, productos, sabores);
                        if (ok) {
                            System.out.println("Pedido local procesado y registrado con exito.");
                        } else {
                            System.out.println("ERROR: No se pudo registrar el pedido en la base de datos.");
                        }
                    } catch (SaborNoDisponibleException e) {
                        System.out.println(e.getMessage());
                    } catch (BachaVaciaException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case 5:
                    System.out.print("Ingrese DNI del cliente: ");
                    String dniCatering = teclado.nextLine();
                    System.out.print("Ingrese cantidad de personas: ");
                    int cantPersonas = teclado.nextInt();
                    System.out.print("Ingrese monto senia: ");
                    double senia = teclado.nextDouble();
                    System.out.print("Ingrese monto total: ");
                    double total = teclado.nextDouble();
                    teclado.nextLine();

                    try {
                        boolean okCat = sistema.reservarCatering(dniCatering, LocalDateTime.now().plusDays(7), cantPersonas, senia, total);
                        if (okCat) {
                            System.out.println("Reserva de catering registrada con exito.");
                        }
                    } catch (ReservaEventoAgotadaException e) {
                        System.out.println(e.getMessage());
                    } catch (ClienteNoRegistradoException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case 6:
                    System.out.print("Ingrese nombre del sabor a recargar: ");
                    String saborRecarga = teclado.nextLine();
                    System.out.print("Ingrese kilos a agregar: ");
                    double kg = teclado.nextDouble();
                    teclado.nextLine();

                    try {
                        sistema.recargarHeladoBacha(saborRecarga, kg);
                        System.out.println("Bacha recargada con exito.");
                    } catch (BachaLlenaException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case 7:
                    System.out.println("\n--- ESTADO DE BACHAS EN MOSTRADOR ---");
                    for (String clave : sistema.getMostradorBachas().keySet()) {
                        Bacha<SaborHelado> b = sistema.getMostradorBachas().get(clave);
                        System.out.println("Sabor: " + b.getSabor().getNombre()
                                + " | Stock: " + b.getCantidadActualKg() + " kg / " + b.getCapacidadMaxKg() + " kg"
                                + " | Estado: " + b.getSabor().getEstado());
                    }
                    break;

                case 8:
                    System.out.println("Saliendo del programa...");
                    break;

                default:
                    System.out.println("ERROR: Opcion invalida. Reintente.");
                    break;
            }

        } while (opcion != 8);

        teclado.close();
    }
}