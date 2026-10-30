public abstract class Usuario extends Persona {
    protected String username;

    public Usuario(String dni, String nombre, String username) {
        super(dni, nombre);
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }


}
