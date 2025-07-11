import org.neo4j.driver.Session;

public class ProdutosBean {
    private int id;
    private String descricao;
    private double preco;
    private double quantidade;
    private int idMarca;
    private int idCategoria;
    
   public ProdutosBean(int id, String descricao, double preco, double quantidade, int idMarca, int idCategoria) {
       this.id= id;
       this.descricao = descricao;
       this.preco = preco;
       this.quantidade= quantidade;
       this.idMarca = idMarca;
       this.idCategoria = idCategoria;
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

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
    
   @Override
    public String toString() {
        String nomeMarca = "";
        
        Conexao c = new Conexao();
        try (Session session = c.getSession()) {            
            nomeMarca = ProdutosModel.selectNomeMarca(this, session);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.descricao + " - " + nomeMarca;
    }
}