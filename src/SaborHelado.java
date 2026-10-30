public class SaborHelado {
    private String nombre;
    private boolean esSinTacc;
    private boolean esVegano;
    private EstadoSabor estado;

    public SaborHelado(String nombre, boolean esSinTacc, boolean esVegano) {
        this.nombre = nombre;
        this.esSinTacc = esSinTacc;
        this.esVegano = esVegano;
        this.estado = EstadoSabor.DISPONIBLE;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public boolean isEsSinTacc() {
        return esSinTacc;
    }
    public void setEsSinTacc(boolean esSinTacc) {
        this.esSinTacc = esSinTacc;
    }
    public boolean isEsVegano() {
        return esVegano;
    }
    public void setEsVegano(boolean esVegano) {
        this.esVegano = esVegano;
    }

}
