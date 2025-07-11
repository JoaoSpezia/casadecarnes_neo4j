import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import org.neo4j.driver.Session;

public class MarcasController {
    
    public void createMarca(Session session) throws SQLException, ParseException {
        String nome = JOptionPane.showInputDialog( null, "Insira o Nome da Marca:", "Cadastro de Marca", 1 );
        if (nome == null) {
            JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Marca", JOptionPane.WARNING_MESSAGE);
            return;
        }
        nome = nome.trim();
        while (nome.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nome não pode estar Vazio!", "Erro", JOptionPane.ERROR_MESSAGE);
            nome = JOptionPane.showInputDialog( null, "Insira o Nome da Marca:", "Cadastro de Marca", 1 );
            if (nome == null) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Marca", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nome = nome.trim();
        }
        
        String cnpj = "";
        MaskFormatter maskCNPJ = new MaskFormatter("##.###.###/####-##");
        maskCNPJ.setPlaceholderCharacter('_');
        JFormattedTextField campoCNPJ = new JFormattedTextField(maskCNPJ);
        JLabel labelCNPJ = new JLabel("Insira o CNPJ da Marca:");
        JPanel panelCNPJ = new JPanel(new GridLayout(0, 1));
        panelCNPJ.add(labelCNPJ);
        panelCNPJ.add(campoCNPJ);
        int resCNPJ = JOptionPane.showConfirmDialog(
                null,
                panelCNPJ,
                "Cadastro de Marca",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );
        if (resCNPJ != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Marca", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cnpj = campoCNPJ.getText().replaceAll("[^\\d]", "");
        while (cnpj.length() != 14) {
            JOptionPane.showMessageDialog(null, "CNPJ inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            resCNPJ = JOptionPane.showConfirmDialog(
                null,
                panelCNPJ,
                "Cadastro de Marca",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resCNPJ != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Marca", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cnpj = campoCNPJ.getText().replaceAll("[^\\d]", "");
        }
        
        MarcasBean mb = new MarcasBean(0, nome, cnpj);
        MarcasModel.create(mb, session);
        JOptionPane.showMessageDialog(null, "Marca Cadastrada com Sucesso!", "Cadastro de Marca", 1);
    }
    
    void updateMarca(Session session) throws SQLException, ParseException {
        JComboBox<MarcasBean> selecaoMarca = new JComboBox<>();
        List<MarcasBean> listaMarca = MarcasModel.listAll(session);
        if (listaMarca.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma Marca Cadastrada!", "Atualização de Marca", JOptionPane.WARNING_MESSAGE);
            return;
        }
        listaMarca.sort(Comparator.comparing(MarcasBean::getNome));
        for (MarcasBean mb : listaMarca) {
            selecaoMarca.addItem(mb);
        }
        JPanel panelMarca = new JPanel(new GridLayout(0, 1));
        panelMarca.add(new JLabel("Selecione a Marca que deseja Atualizar:"));
        panelMarca.add(selecaoMarca);
        int resMarca = JOptionPane.showConfirmDialog(
            null,
            panelMarca,
            "Atualização de Marca",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resMarca != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização de Marca Cancelada!", "Atualização de Marca", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MarcasBean marcaSelecionada = (MarcasBean) selecaoMarca.getSelectedItem();
        
        JTextField campoNome = new JTextField(marcaSelecionada.getNome());
        JPanel panelNome = new JPanel(new GridLayout(0, 1));
        panelNome.add(new JLabel("Insira o Nome da Marca:"));
        panelNome.add(campoNome);
        int resNome = JOptionPane.showConfirmDialog(
            null,
            panelNome,
            "Atualização de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resNome != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização de Marca Cancelada!", "Atualização de Marca", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nome = campoNome.getText().trim();
        while (nome.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nome da Marca não pode estar Vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
            panelNome.remove(campoNome);
            campoNome = new JTextField(marcaSelecionada.getNome());
            panelNome.add(campoNome);
            resNome = JOptionPane.showConfirmDialog(
                null,
                panelNome,
                "Atualização de Cargo",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resNome != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização de Marca Cancelada!", "Atualização de Marca", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nome = campoNome.getText().trim();
        }
        
        String cnpj = marcaSelecionada.getCnpj();
        MaskFormatter maskCNPJ = new MaskFormatter("##.###.###/####-##");
        maskCNPJ.setPlaceholderCharacter('_');
        String cnpjForm = cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        JFormattedTextField campoCNPJ = new JFormattedTextField(maskCNPJ);
        campoCNPJ.setText(cnpjForm);
        JLabel labelCNPJ = new JLabel("Insira o CNPJ da Marca:");
        JPanel panelCNPJ = new JPanel(new GridLayout(0, 1));
        panelCNPJ.add(labelCNPJ);
        panelCNPJ.add(campoCNPJ);
        int resCNPJ = JOptionPane.showConfirmDialog(
                null,
                panelCNPJ,
                "Atualização de Marca",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );
        if (resCNPJ != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização de Marca Cancelada!", "Atualização de Marca", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cnpj = campoCNPJ.getText().replaceAll("[^\\d]", "");
        while (cnpj.length() != 14) {
            JOptionPane.showMessageDialog(null, "CNPJ inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            campoCNPJ.setText(cnpjForm);
            resCNPJ = JOptionPane.showConfirmDialog(
                null,
                panelCNPJ,
                "Atualização de Marca",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resCNPJ != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização de Marca Cancelada!", "Atualização de Marca", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cnpj = campoCNPJ.getText().replaceAll("[^\\d]", "");
        }

        marcaSelecionada.setNome(nome);
        marcaSelecionada.setCnpj(cnpj);
        MarcasModel.update(marcaSelecionada, session);
        JOptionPane.showMessageDialog(null, "Marca Atualizada com Sucesso!", "Atualização de Marca", 1);
    }

}