package cu.uci.campusuci;

/**
 * Created by Yannier on 4/4/2015.
 */
public class LoginUser {

    public String nombre = null;
    public String apellidos = null;
    public String foto = null;
    public String dni = null;
    public String categoria = null;
    public String exp = null;
    public String credencial = null;
    public String usuario = null;
    public String correo = null;
    public String edificio = null;
    public String apto = null;
    public String tel = null;
    public String provincia = null;
    public String municipio = null;
    public String sexo = null;
    public String area = null;
    public String password = null;


    public LoginUser() {
    }

    public void setUser(String n, String a, String f,String e,String ar, String d, String ap
            ,String exp, String cred, String user,String corr,String tel, String cat, String prov
            ,String mun, String sex, String pass){
        nombre = n;
        apellidos = a;
        foto = f;
        edificio = e;
        area = ar;
        dni = d;
        apto = ap;
        this.exp = exp;
        credencial = cred;
        usuario = user;
        correo = corr;
        this.tel = tel;
        categoria = cat;
        provincia = prov;
        municipio = mun;
        sexo = sex;
        password = pass;
    }
}
