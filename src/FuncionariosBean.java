import java.sql.Connection;
import org.neo4j.driver.Session;

public class FuncionariosBean {
    private int matricula;
    private String nome;
    private String cpf;
    private int idCargo;
    private boolean ativo;
    
   public FuncionariosBean(int matricula, String nome, String cpf, int idCargo, boolean ativo) {
       this.matricula= matricula;
       this.nome = nome;
       this.cpf = cpf;
       this.idCargo = idCargo;
       this.ativo = ativo;
   }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
   @Override
    public String toString() {
        String descCargo = "";
        
        Conexao c = new Conexao();
        try (Session session = c.getSession()) {            
            descCargo = FuncionariosModel.selectDescCargo(this, session);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.nome + " (" + descCargo + ")";
    }
}