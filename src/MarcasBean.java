public class MarcasBean {
   private int id;
   private String nome;
   private String cnpj;
   
   public MarcasBean(int id, String nome, String cnpj) {
       this.id= id;
       this.nome = nome;
       this.cnpj = cnpj;
   }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    
    public static String formatarCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return cnpj;

        return cnpj.substring(0, 2) + "." +
               cnpj.substring(2, 5) + "." +
               cnpj.substring(5, 8) + "/" +
               cnpj.substring(8, 12) + "-" +
               cnpj.substring(12);
    }
    
   @Override
    public String toString() {
        return this.nome + " (" + formatarCNPJ(this.cnpj) + ")";
    }
}