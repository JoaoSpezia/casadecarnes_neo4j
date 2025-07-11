public class RegistrosVendaBean {
   private int id;
   private int idVenda;
   private int idProduto;
   private double quantidade;
   private double preco;
   
   public RegistrosVendaBean(int id, int idVenda, int idProduto, double quantidade, double preco) {
       this.id= id;
       this.idVenda = idVenda;
       this.idProduto = idProduto;
       this.quantidade = quantidade;
       this.preco = preco;
   }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}