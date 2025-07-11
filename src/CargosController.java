import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.neo4j.driver.Session;

public class CargosController {
    
    public void createCargo(Session session) throws SQLException, ParseException {
        String descricao = JOptionPane.showInputDialog( null, "Insira a Descrição do Cargo:", "Cadastro de Cargo", 1 );
        if (descricao == null) {
            JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Cargo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        descricao = descricao.trim();
        while (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Descrição não pode estar Vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
            descricao = JOptionPane.showInputDialog( null, "Insira a Descrição do Cargo:", "Cadastro de Cargo", 1 );
            if (descricao == null) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Cargo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            descricao = descricao.trim();
        }
        
        CampoMoeda campoSalario = new CampoMoeda();
        JPanel panelSalario = new JPanel(new GridLayout(0, 1));
        panelSalario.add(new JLabel("Insira o Salário do Cargo:"));
        panelSalario.add(campoSalario);
        int res = JOptionPane.showConfirmDialog(
            null,
            panelSalario,
            "Cadastro de Cargo",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (res != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Cargo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double salario = campoSalario.getValor();
        while (salario <= 0.0) {
            JOptionPane.showMessageDialog(null, "Salário deve ser maior que Zero!", "Erro", JOptionPane.ERROR_MESSAGE);
            res = JOptionPane.showConfirmDialog(
                null,
                panelSalario,
                "Cadastro de Cargo",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (res != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Cargo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            salario = campoSalario.getValor();
        }
        
        CargosBean cb = new CargosBean(0, descricao, salario);
        CargosModel.create(cb, session);
        JOptionPane.showMessageDialog(null, "Cargo Cadastrado com Sucesso!", "Cadastro de Cargo", 1);
    }
    
    void updateCargo(Session session) throws SQLException, ParseException {
        JComboBox<CargosBean> selecaoCargo = new JComboBox<>();
        List<CargosBean> listaCargos = CargosModel.listAll(session);
        if (listaCargos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum Cargo Cadastrado!", "Atualização de Cargo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        listaCargos.sort(Comparator.comparing(CargosBean::getDescricao));
        for (CargosBean cb : listaCargos) {
            selecaoCargo.addItem(cb);
        }
        JPanel panelCargo = new JPanel(new GridLayout(0, 1));
        panelCargo.add(new JLabel("Selecione o Cargo que deseja Atualizar:"));
        panelCargo.add(selecaoCargo);
        int resCargo = JOptionPane.showConfirmDialog(
            null,
            panelCargo,
            "Atualização de Cargo",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resCargo != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização de Cargo Cancelada!", "Atualização de Cargo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CargosBean cargoSelecionado = (CargosBean) selecaoCargo.getSelectedItem();

        JTextField campoDescricao = new JTextField(cargoSelecionado.getDescricao());
        JPanel panelDescricao = new JPanel(new GridLayout(0, 1));
        panelDescricao.add(new JLabel("Insira a Descrição do Cargo:"));
        panelDescricao.add(campoDescricao);
        int resDescricao = JOptionPane.showConfirmDialog(
            null,
            panelDescricao,
            "Atualização de Cargo",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resDescricao != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização de Cargo Cancelada!", "Atualização de Cargo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String descricao = campoDescricao.getText().trim();
        while (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Descrição do Cargo não pode estar Vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
            panelDescricao.remove(campoDescricao);
            campoDescricao = new JTextField(cargoSelecionado.getDescricao());
            panelDescricao.add(campoDescricao);
            resDescricao = JOptionPane.showConfirmDialog(
                null,
                panelDescricao,
                "Atualização de Cargo",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resDescricao != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização de Cargo Cancelada!", "Atualização de Cargo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            descricao = campoDescricao.getText().trim();
        }

        CampoMoeda campoSalario = new CampoMoeda();
        campoSalario.preencherValor(cargoSelecionado.getSalario());
        JPanel panelSalario = new JPanel(new GridLayout(0, 1));
        panelSalario.add(new JLabel("Insira o Salário do Cargo:"));
        panelSalario.add(campoSalario);
        int resSalario = JOptionPane.showConfirmDialog(
            null,
            panelSalario,
            "Atualização de Cargo",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resSalario != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização de Cargo Cancelada!", "Atualização de Cargo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double salario = campoSalario.getValor();
        while (salario <= 0.0) {
            JOptionPane.showMessageDialog(null, "Salário deve ser maior que Zero!", "Erro", JOptionPane.ERROR_MESSAGE);
            campoSalario.preencherValor(cargoSelecionado.getSalario());
            resSalario = JOptionPane.showConfirmDialog(
                null,
                panelSalario,
                "Atualização de Cargo",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resSalario != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização de Cargo Cancelada!", "Atualização de Cargo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            salario = campoSalario.getValor();
        }

        cargoSelecionado.setDescricao(descricao);
        cargoSelecionado.setSalario(salario);
        CargosModel.update(cargoSelecionado, session);
        JOptionPane.showMessageDialog(null, "Cargo Atualizado com Sucesso!", "Atualização de Cargo", 1);
    }
    
}