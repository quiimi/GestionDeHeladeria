package menu;
import modelo.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    public static int leerEntero(Scanner teclado, String mensaje) {
        int valor = 0;
        boolean valido = false;
        do {
            System.out.print(mensaje);
            if (teclado.hasNextInt()) {
                valor = teclado.nextInt();
                teclado.nextLine();
                valido = true;
            } else {
                System.out.println("ERROR: Debe ingresar un numero entero valido.");
                teclado.nextLine();
            }
        } while (!valido);
        return valor;
    }

    public static double leerDouble(Scanner teclado, String mensaje) {
        double valor = 0;
        boolean valido = false;
        do {
            System.out.print(mensaje);
            if (teclado.hasNextDouble()) {
                valor = teclado.nextDouble();
                teclado.nextLine();
                if (valor > 0) {
                    valido = true;
                } else {
                    System.out.println("ERROR: El valor ingresado debe ser mayor a 0.");
                }
            } else {
                System.out.println("ERROR: Debe ingresar un numero valido.");
                teclado.nextLine();
            }
        } while (!valido);
        return valor;
    }

    public static void mostrarSaboresDisponibles(HeladeriaSystem sistema) {
        System.out.println("\n--- SABORES DISPONIBLES EN MOSTRADOR ---");
        boolean hayDisponibles = false;
        for (String clave : sistema.getMostradorBachas().keySet()) {
            Bacha<SaborHelado> b = sistema.getMostradorBachas().get(clave);
            if (b.getSabor().getEstado() == EstadoSabor.DISPONIBLE && b.getCantidadActualKg() > 0) {
                System.out.println("- " + b.getSabor().getNombre() + " | Stock: " + b.getCantidadActualKg() + " kg");
                hayDisponibles = true;
            }
        }
        if (!hayDisponibles) {
            System.out.println("No hay sabores disponibles con stock en este momento.");
        }
    }

    public static Map<SaborHelado, Double> armarProductosYSabores(Scanner teclado, HeladeriaSystem sistema, List<Producto> productos) {
        Map<SaborHelado, Double> consumo = new HashMap<>();

        int tipoProd = 0;
        do {
            System.out.println("\nSeleccione Tipo de Producto:");
            System.out.println("1. Cono Helado");
            System.out.println("2. Pote Helado");
            System.out.println("3. Postre Helado");
            tipoProd = leerEntero(teclado, "Opcion: ");
            if (tipoProd < 1 || tipoProd > 3) {
                System.out.println("ERROR: Opcion invalida. Seleccione 1, 2 o 3.");
            }
        } while (tipoProd < 1 || tipoProd > 3);

        List<SaborHelado> saboresSeleccionados = new ArrayList<>();

        if (tipoProd == 1) {
            int bochas = 0;
            do {
                bochas = leerEntero(teclado, "Ingrese cantidad de bochas (1 a 3): ");
                if (bochas < 1 || bochas > 3) {
                    System.out.println("ERROR: La cantidad de bochas debe ser entre 1 y 3.");
                }
            } while (bochas < 1 || bochas > 3);

            // Asignación automática de precio según constante en BD por bocha
            double precioBocha = sistema.getPrecio("PRECIO_BOCHA");
            double precioTotalCono = bochas * precioBocha;
            double pesoPorBocha = 0.100;

            System.out.println("Precio total calculado del cono: $" + precioTotalCono);

            for (int i = 1; i <= bochas; i++) {
                SaborHelado saborValido = null;
                do {
                    mostrarSaboresDisponibles(sistema);
                    System.out.print("Ingrese nombre del sabor para la bocha " + i + ": ");
                    String nomSabor = teclado.nextLine().trim().toLowerCase();
                    Bacha<SaborHelado> bacha = sistema.getMostradorBachas().get(nomSabor);

                    if (bacha == null) {
                        System.out.println("ERROR: El sabor '" + nomSabor + "' no existe en mostrador.");
                    } else if (bacha.getSabor().getEstado() != EstadoSabor.DISPONIBLE) {
                        System.out.println("ERROR: El sabor '" + bacha.getSabor().getNombre() + "' no esta disponible.");
                    } else {
                        double acumulado = consumo.getOrDefault(bacha.getSabor(), 0.0);
                        if (bacha.getCantidadActualKg() < (acumulado + pesoPorBocha)) {
                            System.out.println("ERROR: Stock insuficiente de '" + bacha.getSabor().getNombre() + "'. Quedan " + bacha.getCantidadActualKg() + " kg.");
                        } else {
                            saborValido = bacha.getSabor();
                        }
                    }
                } while (saborValido == null);

                saboresSeleccionados.add(saborValido);
                consumo.put(saborValido, consumo.getOrDefault(saborValido, 0.0) + pesoPorBocha);
            }
            productos.add(new ConoHelado(1, "Cono Helado", precioTotalCono, bochas, saboresSeleccionados));

        } else if (tipoProd == 2) {
            double kilosPote = 0;
            boolean pesoValido = false;

            // Validación de pesos comerciales permitidos (1/4 kg, 1/2 kg, 1 kg)
            do {
                kilosPote = leerDouble(teclado, "Ingrese peso del pote en Kilos (0.25, 0.5 o 1.0): ");
                if (kilosPote == 0.25 || kilosPote == 0.5 || kilosPote == 1.0) {
                    pesoValido = true;
                } else {
                    System.out.println("ERROR: Peso no valido. Solo se comercializan potes de 0.25 kg (1/4), 0.5 kg (1/2) o 1.0 kg (1 kg).");
                }
            } while (!pesoValido);

            // Límite de sabores según peso validado
            int maxSabores = (kilosPote == 0.25) ? 2 : (kilosPote == 0.5) ? 3 : 4;
            System.out.println("Nota: Para un pote de " + kilosPote + " kg se permite un maximo de " + maxSabores + " sabor(es).");

            int cantSabores = 0;
            do {
                cantSabores = leerEntero(teclado, "Ingrese cantidad de sabores a incluir (1 a " + maxSabores + "): ");
                if (cantSabores < 1 || cantSabores > maxSabores) {
                    System.out.println("ERROR: Cantidad invalida. Para " + kilosPote + " kg se permiten entre 1 y " + maxSabores + " sabores.");
                }
            } while (cantSabores < 1 || cantSabores > maxSabores);

            // Asignación automática de precio según constante en BD por Kilo
            double precioPorKg = sistema.getPrecio("PRECIO_KILO");
            double precioTotalPote = kilosPote * precioPorKg;
            double kgPorSabor = kilosPote / cantSabores;

            System.out.println("Precio total calculado del pote: $" + precioTotalPote);

            for (int i = 1; i <= cantSabores; i++) {
                SaborHelado saborValido = null;
                do {
                    mostrarSaboresDisponibles(sistema);
                    System.out.print("Ingrese nombre del sabor " + i + ": ");
                    String nomSabor = teclado.nextLine().trim().toLowerCase();
                    Bacha<SaborHelado> bacha = sistema.getMostradorBachas().get(nomSabor);

                    if (bacha == null) {
                        System.out.println("ERROR: El sabor '" + nomSabor + "' no existe en mostrador.");
                    } else if (bacha.getSabor().getEstado() != EstadoSabor.DISPONIBLE) {
                        System.out.println("ERROR: El sabor '" + bacha.getSabor().getNombre() + "' no esta disponible.");
                    } else {
                        double acumulado = consumo.getOrDefault(bacha.getSabor(), 0.0);
                        if (bacha.getCantidadActualKg() < (acumulado + kgPorSabor)) {
                            System.out.println("ERROR: Stock insuficiente de '" + bacha.getSabor().getNombre() + "'. Quedan " + bacha.getCantidadActualKg() + " kg.");
                        } else {
                            saborValido = bacha.getSabor();
                        }
                    }
                } while (saborValido == null);

                saboresSeleccionados.add(saborValido);
                consumo.put(saborValido, consumo.getOrDefault(saborValido, 0.0) + kgPorSabor);
            }
            productos.add(new PoteHelado(2, "Pote Helado", precioTotalPote, kilosPote, saboresSeleccionados));

        } else if (tipoProd == 3) {
            System.out.println("\n--- TIPOS DE POSTRE DISPONIBLES ---");
            TipoPostre[] opcionesPostre = TipoPostre.values();
            for (int i = 0; i < opcionesPostre.length; i++) {
                System.out.println((i + 1) + ". " + opcionesPostre[i]);
            }

            int selPostre = 0;
            do {
                selPostre = leerEntero(teclado, "Seleccione una opcion de postre (1 a " + opcionesPostre.length + "): ");
                if (selPostre < 1 || selPostre > opcionesPostre.length) {
                    System.out.println("ERROR: Seleccion fuera de rango.");
                }
            } while (selPostre < 1 || selPostre > opcionesPostre.length);

            TipoPostre postreElegido = opcionesPostre[selPostre - 1];
            double precioPostre = sistema.getPrecio("POSTRE_" + postreElegido.name());
            System.out.println("Precio asignado para " + postreElegido + ": $" + precioPostre);

            productos.add(new PostreHelado(3, postreElegido.name(), precioPostre, postreElegido));
        }

        return consumo;
    }
}