package cu.uci.cuota;

/**
 * Created by Yannier on 26/02/2015.
 */
public class Usuario {

    public float cuota;
    public float cuota_usada;
    public String nivel_nav;
    public String usuario;

    public Usuario() {
    }

    public float getCuota() {
        return cuota;
    }

    public void setCuota(float cuota) {
        this.cuota = cuota;
    }

    public float getCuota_usada() {
        return cuota_usada;
    }

    public void setCuota_usada(float cuota_usada) {
        this.cuota_usada = cuota_usada;
    }

    public String getNivel_nav() {
        return nivel_nav;
    }

    public void setNivel_nav(String nivel_nav) {
        this.nivel_nav = nivel_nav;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Cuota: " + cuota + "\n");
        builder.append("Cuota Usada: " + cuota_usada + "\n");
        builder.append("Nivel de Navegacion: " + nivel_nav + "\n");
        builder.append("Usuario: " + usuario);

        return builder.toString();
    }
}
