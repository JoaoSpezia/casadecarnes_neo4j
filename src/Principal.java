import java.sql.SQLException;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.neo4j.driver.Session;

public class Principal {

    public static void main(String[] args) throws SQLException, ParseException {
        
        Locale.setDefault(new Locale("pt", "BR"));
        UIManager.put("OptionPane.yesButtonText", "Sim");
        UIManager.put("OptionPane.noButtonText", "Não");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "OK");
        
        Conexao c = new Conexao();
        Session session = c.getSession();
         
        int op;
        do{
            op = menu();
            switch (op) {
                case 0 -> new ProdutosController().createProduto(session);
                case 1 -> new ProdutosController().addQtddProduto(session);
                case 2 -> new ProdutosController().mostraProduto(session);
                case 3 -> new ProdutosController().updateProduto(session);
                case 4 -> new MarcasController().updateMarca(session);
                case 5 -> new CategoriasController().updateCategoria(session);
                case 6 -> new FuncionariosController().createFuncionario(session);
                case 7 -> new FuncionariosController().updateFuncionario(session);
                case 8 -> new FuncionariosController().mostraFuncionario(session);
                case 9 -> new FuncionariosController().updateStatusFuncionario(session);
                case 10 -> new CargosController().updateCargo(session);
                case 11 -> new VendasController().createVenda(session);
                case 12 -> new VendasController().mostraVenda(session);
                case 13 -> new RelatoriosController().relGeral(session);
            }
        } while(op>=0 && op<=13);  
        session.close();
    }    
    
    private static int menu() {
        String[] opcoes = {"Cadastrar Produto", "Adicionar Quantidade ao Produto", "Consultar Produto", "Alterar Produto", "Alterar Marca", "Alterar Categoria", "Cadastrar Funcionário", "Alterar Funcionário", "Consultar Funcionário", "Atualizar Status do Funcionário", "Alterar Cargo", "Registrar Venda", "Consultar Venda", "Gerar Relatório", "Sair"};
        JComboBox<String> selecao = new JComboBox<>(opcoes);
        JOptionPane.showMessageDialog( null, selecao, "Selecione uma Opção:", 1 );
        return selecao.getSelectedIndex();
    }
    
}
