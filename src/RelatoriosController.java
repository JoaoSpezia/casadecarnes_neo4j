import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.MaskFormatter;
import org.neo4j.driver.Session;

public class RelatoriosController {
    
    public void relGeral(Session session) throws SQLException, ParseException {              
        MaskFormatter mascaraData = new MaskFormatter("##/##/####");
        mascaraData.setPlaceholderCharacter('_');
        JFormattedTextField campoDataDe = new JFormattedTextField(mascaraData);
        JFormattedTextField campoDataAte = new JFormattedTextField(mascaraData);
        campoDataDe.setColumns(8);
        campoDataAte.setColumns(8);
        campoDataDe.setHorizontalAlignment(JTextField.CENTER);
        campoDataAte.setHorizontalAlignment(JTextField.CENTER);
        
        String[] opcoes = {"Produtos mais Vendidos (R$)", "Produtos mais Vendidos (Qtde)", "Funcionário que mais Vendeu (R$)"};
        JComboBox<String> selecao = new JComboBox<>(opcoes);

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(new JLabel("🛈 Filtro por Período:"), gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 2, 2, 2);
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        painel.add(new JLabel("De (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        campoDataDe.setColumns(8);
        campoDataDe.setHorizontalAlignment(JTextField.CENTER);
        painel.add(campoDataDe, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        painel.add(new JLabel("Até (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        campoDataAte.setColumns(8);
        campoDataAte.setHorizontalAlignment(JTextField.CENTER);
        painel.add(campoDataAte, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 2, 2, 2);
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(selecao, gbc);

        gbc.gridy++;
        JLabel observacao = new JLabel("<html><i>Deixe os campos em branco para consultar todo o período<br>ou apenas um desses para consultar a partir/até certa data.</i></html>", SwingConstants.CENTER);
        observacao.setHorizontalAlignment(SwingConstants.CENTER);
        painel.add(observacao, gbc);

        int opcao = JOptionPane.showConfirmDialog(
            null, 
            painel, 
            "Relatórios", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );
        if (opcao != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Relatório Cancelado pelo Usuário!", "Relatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String StrDataDe = campoDataDe.getText().trim().replace("/", "");
        String StrDataAte = campoDataAte.getText().trim().replace("/", "");
        boolean dataDeIncompleta = StrDataDe.length() > 0 && StrDataDe.length() < 8;
        boolean dataAteIncompleta = StrDataDe.length() > 0 && StrDataDe.length() < 8;
        while (dataDeIncompleta || dataAteIncompleta) {
            JOptionPane.showMessageDialog(null, "Valores preenchidos incorretamente! Insira a Data Completa ou deixe em Branco.", "Relatórios", JOptionPane.ERROR_MESSAGE);
            opcao = JOptionPane.showConfirmDialog(
                null, 
                painel, 
                "Relatórios", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );
            if (opcao != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Relatório Cancelado pelo Usuário!", "Relatórios", JOptionPane.WARNING_MESSAGE);
                return;
            }
            StrDataDe = campoDataDe.getText().trim().replace("/", "");
            StrDataAte = campoDataAte.getText().trim().replace("/", "");
            dataDeIncompleta = StrDataDe.length() > 0 && StrDataDe.length() < 8;
            dataAteIncompleta = StrDataDe.length() > 0 && StrDataDe.length() < 8;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        sdf.setLenient(false);
        java.sql.Date dataDe = null;
        java.sql.Date dataAte = null;
        if (!StrDataDe.replaceAll("[_/]", "").isEmpty()) {
            java.util.Date data = sdf.parse(StrDataDe);
            dataDe = new java.sql.Date(data.getTime());
        }
        if (!StrDataAte.replaceAll("[_/]", "").isEmpty()) {
            java.util.Date data = sdf.parse(StrDataAte);
            dataAte = new java.sql.Date(data.getTime());
        }
        
        switch (selecao.getSelectedIndex()) {
            case 0 -> relProdsMaisVendidosValor(dataDe, dataAte, session);
            case 1 -> relProdsMaisVendidosQtde(dataDe, dataAte, session);
            case 2 -> relFuncMaisVendas(dataDe, dataAte, session);
        }
    }
    
    public void relProdsMaisVendidosValor(java.sql.Date dtIni, java.sql.Date dtFim, Session session) throws SQLException, ParseException {
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);
        List<String[]> prods = RelatoriosModel.consProdsMaisVendidosValor(formataDatasConsulta(dtIni, dtFim), session);
        if (prods.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma Venda encontrada no Período!", "Relatório", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ProdutosBean p = null;
        String valorStr = "";
        double valor = 0.0;
        String relatorio = "Produtos mais Vendidos ";
            relatorio += formataDatasRelatorio(dtIni, dtFim);
            for(int i=0; i<prods.size(); i++) {
                valorStr = prods.get(i)[1];
                valor = Double.parseDouble(valorStr);
                p = ProdutosModel.selectById(Integer.parseInt(prods.get(i)[0]), session);
                CategoriasBean c = CategoriasModel.selectById(p.getIdCategoria(), session);
                MarcasBean m = MarcasModel.selectById(p.getIdMarca(), session);
                relatorio += "\n\n" + (i+1) + "º Lugar (ID: " + p.getId() + "):";
                relatorio += "\n  -  Categoria: " + c.getDescricao();
                relatorio += "\n  -  Descrição: " + p.getDescricao() + " (" + m.getNome() + ")";
                relatorio += "\n  -  Valor Vendido: " + formatoMoeda.format(valor);
            }
        JOptionPane.showOptionDialog(
            null,
            relatorio,
            "Relatório",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[]{"Fechar"},
            "Fechar"
        );
    }
    
    public void relProdsMaisVendidosQtde(java.sql.Date dtIni, java.sql.Date dtFim, Session session) throws SQLException, ParseException {
        DecimalFormat formatoKg = new DecimalFormat("###,##0.000");
        DecimalFormat formatoUni = new DecimalFormat("###,##0");
        List<String[]> prods = RelatoriosModel.consProdsMaisVendidosQtde(formataDatasConsulta(dtIni, dtFim), session);
        if (prods.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma Venda encontrada no Período!", "Relatório", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ProdutosBean p = null;
        String qtdeStr = "";
        double qtde = 0.0;
        String relatorio = "Produtos mais Vendidos ";
            relatorio += formataDatasRelatorio(dtIni, dtFim);
            for(int i=0; i<prods.size(); i++) {
                qtdeStr = prods.get(i)[1];
                qtde = Double.parseDouble(qtdeStr);
                p = ProdutosModel.selectById(Integer.parseInt(prods.get(i)[0]), session);
                CategoriasBean c = CategoriasModel.selectById(p.getIdCategoria(), session);
                MarcasBean m = MarcasModel.selectById(p.getIdMarca(), session);
                relatorio += "\n\n" + (i+1) + "º Lugar (ID: " + p.getId() + "):";
                relatorio += "\n  -  Categoria: " + c.getDescricao();
                relatorio += "\n  -  Descrição: " + p.getDescricao() + " (" + m.getNome() + ")";
                if(c.getUniMed().equals("kg")) {
                    relatorio += "\n  -  Quantidade Vendida: " + formatoKg.format(qtde) + " kg(s)";
                } else {
                    relatorio += "\n  -  Quantidade Vendida: " + formatoUni.format(qtde) + " unidade(s)";
                }
            }
        JOptionPane.showOptionDialog(
            null,
            relatorio,
            "Relatório",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[]{"Fechar"},
            "Fechar"
        );
    }
    
    public void relFuncMaisVendas(java.sql.Date dtIni, java.sql.Date dtFim, Session session) throws SQLException, ParseException {
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);
        List<String[]> func = RelatoriosModel.consFuncMaisVendas(formataDatasConsulta(dtIni, dtFim), session);
        if (func.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma Venda encontrada no Período!", "Relatório", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FuncionariosBean f = FuncionariosModel.selectByMat(Integer.parseInt(func.get(0)[0]), session);
        String valorStr = func.get(0)[1];
        double valor = Double.parseDouble(valorStr);
        String relatorio = "Funcionário com mais Vendas ";
            relatorio += formataDatasRelatorio(dtIni, dtFim);
            relatorio += "\n  -  Matrícula: " + f.getMatricula();
            relatorio += " //  Nome: " + f.getNome();
            relatorio += "\n  -  Valor Vendido: " + formatoMoeda.format(valor);
        JOptionPane.showOptionDialog(
            null,
            relatorio,
            "Relatório",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[]{"Fechar"},
            "Fechar"
        );
    }
    
    public String formataDatasConsulta(java.sql.Date dtIni, java.sql.Date dtFim) {
        String filtro = "WHERE ";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (dtIni == null && dtFim == null) {
            return "";
        } else if (dtIni != null && dtFim == null) {
            String dataIniStr = sdf.format(dtIni);
            long tsIni = CampoData.converterDataParaTimestamp(dataIniStr);
            filtro += "v.data_horario >= " + tsIni;
        } else if (dtIni == null && dtFim != null) {
            String dataFimStr = sdf.format(dtFim);
            long tsFim = CampoData.converterDataParaTimestamp(dataFimStr);
            filtro += "v.data_horario <= " + tsFim;
        } else {
            String dataIniStr = sdf.format(dtIni);
            String dataFimStr = sdf.format(dtFim);
            long tsIni = CampoData.converterDataParaTimestamp(dataIniStr);
            long tsFim = CampoData.converterDataParaTimestamp(dataFimStr);
            filtro += "v.data_horario >= " + tsIni + " AND v.data_horario <= " + tsFim;
        }
        return filtro;
    }
    
    public String formataDatasRelatorio(java.sql.Date dtIni, java.sql.Date dtFim) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (dtIni == null && dtFim == null) {
            return "de todo o Período:";
        } else if(dtFim == null) {
            return "a partir de " + sdf.format(dtIni) + ":";
        } else if(dtIni == null) {
            return "até " + sdf.format(dtFim) + ":";
        }
        return "de " + sdf.format(dtIni) + " até " + sdf.format(dtFim) + ":";
    }

}