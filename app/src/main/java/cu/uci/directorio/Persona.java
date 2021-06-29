package cu.uci.directorio;

/**
 * Created by Yannier on 3/4/2015.
 */
public class Persona {

    public String exp_0;
    public String nombres_1;
    public String apellidos_2;
    public String credencial_3;
    public String provincia_4_2_1;
    public String municipio_4_1;
    public String ci_5;
    public String categoria_6;
    public String sexo_8_1;
    public String usuario_9;
    public String corre_10;
    public String edificio_11_0;
    public String apto_11_1;
    public String telefono_11_2;
    public String area_12_1;
    public String foto_13_1;

    public Persona() {

        this.nombres_1 = "null";
    }

    public String toString(){
        return nombres_1 + "\n" +
                apellidos_2 + "\n" +
                credencial_3 + "\n" +
                provincia_4_2_1 + "\n" +
                municipio_4_1 + "\n" +
                ci_5 + "\n" +
                categoria_6 + "\n" +
                corre_10 + "\n" +
                edificio_11_0 + "\n" +
                apto_11_1 + "\n" +
                telefono_11_2 + "\n" +
                foto_13_1 + "\n";
    }
}
