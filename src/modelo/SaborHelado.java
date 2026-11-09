package modelo;

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
    public boolean isSinTacc() {
        return esSinTacc;
    }
    public void setSinTacc(boolean esSinTacc) {
        this.esSinTacc = esSinTacc;
    }
    public boolean isVegano() {
        return esVegano;
    }
    public void setVegano(boolean esVegano) {
        this.esVegano = esVegano;
    }

    public EstadoSabor getEstado() {
        return estado;
    }

    public void setEstado(EstadoSabor estado) {
        this.estado = estado;
    }
}
