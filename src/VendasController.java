import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.neo4j.driver.Session;

public class VendasController {
    
    public void createVenda(Session session) throws SQLException {
        JComboBox<FuncionariosBean> selecaoFuncionarios = new JComboBox<>();
        List<FuncionariosBean> listaFuncionarios = FuncionariosModel.listAllAtivos(session);
        listaFuncionarios.sort(Comparator.comparing(FuncionariosBean::getNome));
        int qtde = 0;
        for (FuncionariosBean fb : listaFuncionarios) {
            if(FuncionariosModel.selectDescCargo(fb, session).equals("Atendente") || FuncionariosModel.selectDescCargo(fb, session).equals("Caixa") || FuncionariosModel.selectDescCargo(fb, session).equals("Gerente")) {
                selecaoFuncionarios.addItem(fb);
                qtde++;
            }
        }
        Integer matricula = null;
        if(qtde > 0) {
            JPanel painelFuncionarios = new JPanel(new GridLayout(0, 1));
            painelFuncionarios.add(new JLabel("Selecione o Funcionário responsável pela Venda:"));
            painelFuncionarios.add(selecaoFuncionarios);
            Object[] opcoes = {"Confirmar", "Nenhum Responsável"};
            FuncionariosBean funcionarioSelecionado = null;
            int resFuncionarios = JOptionPane.showOptionDialog(
                null,
                painelFuncionarios,
                "Venda",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
            );
            if (resFuncionarios == 0) {
                funcionarioSelecionado = (FuncionariosBean) selecaoFuncionarios.getSelectedItem();
                matricula = funcionarioSelecionado.getMatricula();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum Funcionário atribuído à Venda!", "Venda", JOptionPane.INFORMATION_MESSAGE);
        }
        
        VendasBean vb = new VendasBean(0, LocalDateTime.now(), matricula);
        VendasModel.create(vb, session);
        
        boolean vendaFinalizada = RegistrosVendaController.createRegVenda(vb, session);
        if (vendaFinalizada) {
            this.updateDataHora(vb, session);
        } else {
            VendasModel.delete(vb, session);
            JOptionPane.showMessageDialog(null, "Venda Cancelada!", "Registro de Venda", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    void updateDataHora(VendasBean v, Session session) throws SQLException {
        v.setDataHora(LocalDateTime.now());
        VendasModel.update(v, session);
    }
 
    void mostraVenda(Session session) throws SQLException {
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);
        DecimalFormat formatoKg = new DecimalFormat("###,##0.000");
        DecimalFormat formatoUni = new DecimalFormat("###,##0");
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy (EEEE)", brasil);
        boolean continuar = true;
        do {
            List<VendasBean> listaVendas = VendasModel.listAll(session);
            if (listaVendas.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhuma Venda Registrada!", "Consulta de Venda", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Map<LocalDate, List<VendasBean>> vendasPorData = new TreeMap<>();
            for (VendasBean vb : listaVendas) {
                LocalDate data = vb.getDataHora().toLocalDate();
                vendasPorData.putIfAbsent(data, new ArrayList<>());
                vendasPorData.get(data).add(vb);
            }
            Map<String, LocalDate> mapaDatas = new LinkedHashMap<>();
            for (LocalDate data : vendasPorData.keySet()) {
                String dataFormatada = data.format(formatoData);
                mapaDatas.put(dataFormatada, data);
            }
            JComboBox<String> selecaoDatas = new JComboBox<>();
            for (String dataFormatada : mapaDatas.keySet()) {
                selecaoDatas.addItem(dataFormatada);
            }
            JPanel painelDatasVendas = new JPanel(new GridLayout(0, 1));
            painelDatasVendas.add(new JLabel("Selecione a Data da Venda que deseja Consultar:"));
            painelDatasVendas.add(selecaoDatas);
            int resDatasVendas = JOptionPane.showConfirmDialog(
                null,
                painelDatasVendas,
                "Consulta de Venda",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resDatasVendas != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Consulta Cancelada pelo Usuário!", "Consulta de Venda", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String dataSelecionadaTexto = (String) selecaoDatas.getSelectedItem();
            LocalDate dataSelecionada = mapaDatas.get(dataSelecionadaTexto);
            List<VendasBean> vendasDaData = vendasPorData.get(dataSelecionada);
            JComboBox<VendasBean> selecaoVendas = new JComboBox<>();
            for (VendasBean vb : vendasDaData) {
                selecaoVendas.addItem(vb);
            }
            JPanel painelVendas = new JPanel(new GridLayout(0, 1));
            painelVendas.add(new JLabel("Selecione qual das Vendas que deseja Consultar:"));
            painelVendas.add(selecaoVendas);
            int resVendas = JOptionPane.showConfirmDialog(
                null,
                painelVendas,
                "Consulta de Venda",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resVendas != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Consulta Cancelada pelo Usuário!", "Consulta de Venda", JOptionPane.WARNING_MESSAGE);
                return;
            }            
            VendasBean vendaSelecionada = (VendasBean) selecaoVendas.getSelectedItem();
            List<RegistrosVendaBean> itens = RegistrosVendaModel.selectByVenda(vendaSelecionada, session);
            String consulta = "Venda Consultada";
            if(vendaSelecionada.getMatricula() != null) {
                consulta += " (Responsável: " + VendasModel.selectNomeFunc(vendaSelecionada, session) + "):\n\n";
            } else {
                consulta += " (Sem Responsável):\n\n";
            }
            double totalVenda = 0.0;
            for (RegistrosVendaBean item : itens) {
                ProdutosBean produto = ProdutosModel.selectById(item.getIdProduto(), session);
                CategoriasBean categoria = CategoriasModel.selectById(produto.getIdCategoria(), session);
                double subtotal = item.getPreco() * item.getQuantidade();
                totalVenda += subtotal;
                String quantidade = "";
                if (categoria.getUniMed().equals("kg")) {
                    quantidade = formatoKg.format(item.getQuantidade()) + " kg(s)";
                } else {
                    quantidade = formatoUni.format(item.getQuantidade()) + " unidade(s)";
                }
                consulta += "  -  ID Produto: " + produto.getId();
                consulta += " (" + produto.getDescricao() + ")";
                consulta += " // Preço: " + formatoMoeda.format(item.getPreco());
                consulta += " e Quantidade: " + quantidade;
                consulta += " // Subtotal: " + formatoMoeda.format(subtotal) + "\n";
            }
            consulta += "  -  Valor Total da Venda: " + formatoMoeda.format(totalVenda);
            consulta += "\n\nDeseja consultar outra Venda?";
            int res = JOptionPane.showConfirmDialog(
                null,
                consulta,
                "Consulta de Venda",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (res != JOptionPane.YES_OPTION) {
                continuar = false;
                JOptionPane.showMessageDialog(null, "Consulta Finalizada com Sucesso!", "Consulta de Venda", JOptionPane.INFORMATION_MESSAGE);
            }
        } while (continuar);
    }

}