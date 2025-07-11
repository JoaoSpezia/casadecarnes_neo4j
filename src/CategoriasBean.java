public class CategoriasBean {
   private int id;
   private String descricao;
   private String uniMed;
   
   public CategoriasBean(int id, String descricao, String uniMed) {
       this.id= id;
       this.descricao = descricao;
       this.uniMed = uniMed;
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

    public String getUniMed() {
        return uniMed;
    }

    public void setUniMed(String uniMed) {
        this.uniMed = uniMed;
    }
    
   @Override
    public String toString() {
        return this.descricao + " (" + this.uniMed + ")";
    }
}