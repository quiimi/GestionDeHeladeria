public class Cliente extends Persona {
    private int puntosFidelidad;

    public Cliente(String dni, String nombre) {
        super(dni, nombre);
        this.puntosFidelidad = 0;
    }

    public int getPuntosFidelidad() { return puntosFidelidad; }
    public void setPuntosFidelidad(int puntosFidelidad) { this.puntosFidelidad = puntosFidelidad; }
    public void sumarPuntos(int puntos) { this.puntosFidelidad += puntos; }
}