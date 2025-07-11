import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.neo4j.driver.Session;

public class CategoriasController {
    
    public void createCategoria(Session session) throws SQLException {
        String descricao = JOptionPane.showInputDialog(null, "Insira a Descrição da Categoria:", "Cadastro de Categoria", JOptionPane.QUESTION_MESSAGE);
        if (descricao == null) {
            JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Categoria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        descricao = descricao.trim();
        while (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Descrição da Categoria não pode estar Vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
            descricao = JOptionPane.showInputDialog(null, "Insira a Descrição da Categoria:", "Cadastro de Categoria", JOptionPane.QUESTION_MESSAGE);
            if (descricao == null) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Categoria", JOptionPane.WARNING_MESSAGE);
                return;
            }
            descricao = descricao.trim();
        }

        String uni_medida = null;
        int resultado = -1;
        do {
            String[] opcoes = {"kg", "unidade"};
            JComboBox<String> selecao = new JComboBox<>(opcoes);

            JPanel painel = new JPanel(new GridLayout(0, 1));
            painel.add(new JLabel("Selecione a Unidade de Medida da Categoria:"));
            painel.add(selecao);

            int res = JOptionPane.showConfirmDialog(
                null,
                painel,
                "Cadastro de Categoria",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );

            if (res != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Categoria", JOptionPane.WARNING_MESSAGE);
                return;
            }

            resultado = selecao.getSelectedIndex();
            uni_medida = (String) selecao.getSelectedItem();
        } while (resultado < 0 || uni_medida == null || uni_medida.isBlank());

        CategoriasBean cb = new CategoriasBean(0, descricao, uni_medida);
        CategoriasModel.create(cb, session);
        JOptionPane.showMessageDialog(null, "Categoria Cadastrada com Sucesso!", "Cadastro de Categoria", JOptionPane.INFORMATION_MESSAGE);
    }
    
    void updateCategoria(Session session) throws SQLException {
        JComboBox<CategoriasBean> selecaoCategoria = new JComboBox<>();
        List<CategoriasBean> listaCategorias = CategoriasModel.listAll(session);
        if (listaCategorias.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma Categoria Cadastrada!", "Atualização de Categoria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        listaCategorias.sort(Comparator.comparing(CategoriasBean::getDescricao));
        for (CategoriasBean cb : listaCategorias) {
            selecaoCategoria.addItem(cb);
        }

        JPanel panelCategoria = new JPanel(new GridLayout(0, 1));
        panelCategoria.add(new JLabel("Selecione a Categoria que deseja Atualizar:"));
        panelCategoria.add(selecaoCategoria);
        int resCategoria = JOptionPane.showConfirmDialog(
            null,
            panelCategoria,
            "Atualização de Categoria",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resCategoria != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização de Categoria Cancelada!", "Atualização de Categoria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CategoriasBean categoriaSelecionada = (CategoriasBean) selecaoCategoria.getSelectedItem();
        if (categoriaSelecionada == null) {
            JOptionPane.showMessageDialog(null, "Nenhuma Categoria Selecionada!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField campoDescricao = new JTextField(categoriaSelecionada.getDescricao());
        JPanel panelDescricao = new JPanel(new GridLayout(0, 1));
        panelDescricao.add(new JLabel("Insira a Descrição da Categoria:"));
        panelDescricao.add(campoDescricao);
        int resDescricao = JOptionPane.showConfirmDialog(
            null,
            panelDescricao,
            "Atualização de Categoria",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resDescricao != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização de Categoria Cancelada!", "Atualização de Categoria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String descricao = campoDescricao.getText().trim();
        while (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Descrição da Categoria não pode estar Vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
            campoDescricao.setText(categoriaSelecionada.getDescricao());
            resDescricao = JOptionPane.showConfirmDialog(
                null,
                panelDescricao,
                "Atualização de Categoria",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resDescricao != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização de Categoria Cancelada!", "Atualização de Categoria", JOptionPane.WARNING_MESSAGE);
                return;
            }
            descricao = campoDescricao.getText().trim();
        }

        String[] opcoes = {"kg", "unidade"};
        JComboBox<String> selecaoUniMed = new JComboBox<>(opcoes);
        selecaoUniMed.setSelectedItem(categoriaSelecionada.getUniMed());
        JPanel panelUniMed = new JPanel(new GridLayout(0, 1));
        panelUniMed.add(new JLabel("Selecione a Unidade de Medida da Categoria:"));
        panelUniMed.add(selecaoUniMed);
        int resultado = -1;
        String uni_medida = null;
        do {
            resultado = JOptionPane.showConfirmDialog(
                null,
                panelUniMed,
                "Atualização de Categoria",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resultado == JOptionPane.OK_OPTION) {
                uni_medida = (String) selecaoUniMed.getSelectedItem();
            } else {
                JOptionPane.showMessageDialog(null, "Atualização de Categoria Cancelada!", "Atualização de Categoria", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } while (uni_medida == null || uni_medida.isBlank());

        categoriaSelecionada.setDescricao(descricao);
        categoriaSelecionada.setUniMed(uni_medida);
        CategoriasModel.update(categoriaSelecionada, session);
        JOptionPane.showMessageDialog(null, "Categoria Atualizada com Sucesso!", "Atualização de Categoria", JOptionPane.INFORMATION_MESSAGE);
    }
    
}