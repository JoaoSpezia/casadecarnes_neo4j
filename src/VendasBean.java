import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.neo4j.driver.Session;

public class VendasBean {
   private int id;
   private LocalDateTime dataHora;
   private Integer matricula;
   
   public VendasBean(int id, LocalDateTime dataHora, Integer matricula) {
       this.id= id;
       this.dataHora = dataHora;
       this.matricula = matricula;
   }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getMatricula() {
        return matricula;
    }

    public void setMatricula(Integer matricula) {
        this.matricula = matricula;
    }
    
   @Override
    public String toString() {
        double valor = 0.0;
        
        Conexao c = new Conexao();
        try (Session session = c.getSession()) {             
            valor = VendasModel.selectValorTotal(this, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        return "Horário: " + this.dataHora.format(formatoHora) + " - Valor: " + formatoMoeda.format(valor);
    }
}