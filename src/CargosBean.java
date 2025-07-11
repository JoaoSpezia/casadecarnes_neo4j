import java.text.NumberFormat;
import java.util.Locale;

public class CargosBean {
   private int id;
   private String descricao;
   private double salario;
   
   public CargosBean(int id, String descricao, double salario) {
       this.id= id;
       this.descricao = descricao;
       this.salario = salario;
   }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }
    
    private String formatarMoeda(double valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return nf.format(valor);
    }
    
   @Override
    public String toString() {
        return this.descricao + " (" + formatarMoeda(this.salario) + ")";
    }
}