import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import org.neo4j.driver.Session;

public class FuncionariosController {
    
    public void createFuncionario(Session session) throws SQLException, ParseException {
        try {
            int ultimaMatricula = FuncionariosModel.selectUltimaMat(session);
            int anoAtual = LocalDate.now().getYear();
            int novaMatricula;
            if (ultimaMatricula / 1000 == anoAtual) {
                novaMatricula = ultimaMatricula + 1;
            } else {
                novaMatricula = anoAtual * 1000 + 1;
            }

            List<CargosBean> listaCargos = CargosModel.listAll(session);
            listaCargos.sort(Comparator.comparing(CargosBean::getDescricao));
            JComboBox<CargosBean> selecaoCargos = new JComboBox<>();
            for (CargosBean cb : listaCargos) {
                selecaoCargos.addItem(cb);
            }
            JButton novoCargo = new JButton("Novo Cargo");
            novoCargo.addActionListener(e -> {
                try {
                    CargosController controller = new CargosController();
                    controller.createCargo(session);
                    selecaoCargos.removeAllItems();
                    List<CargosBean> atualizada = CargosModel.listAll(session);
                    atualizada.sort(Comparator.comparing(CargosBean::getDescricao));
                    for (CargosBean cb : atualizada) {
                        selecaoCargos.addItem(cb);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao criar Cargo.", "Cadastro de Funcionário", JOptionPane.ERROR_MESSAGE);
                }
            });
            JPanel panelCargo = new JPanel();
            panelCargo.setLayout(new BorderLayout(5, 5));
            panelCargo.add(new JLabel("Selecione o Cargo do Funcionário:"), BorderLayout.NORTH);
            panelCargo.add(selecaoCargos, BorderLayout.CENTER);
            panelCargo.add(novoCargo, BorderLayout.SOUTH);
            int resCargo = JOptionPane.showConfirmDialog(
                null,
                panelCargo,
                "Cadastro de Funcionário",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resCargo != JOptionPane.OK_OPTION || selecaoCargos.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Cadastro de Funcionário Cancelado!", "Cadastro de Funcionário", JOptionPane.WARNING_MESSAGE);
                return;
            }
            CargosBean selecionado = (CargosBean) selecaoCargos.getSelectedItem();
            int id_cargo = selecionado.getId();

            String nome = JOptionPane.showInputDialog(null, "Insira o Nome do Funcionário:", "Cadastro de Funcionário", JOptionPane.QUESTION_MESSAGE);
            if (nome == null) {
                JOptionPane.showMessageDialog(null, "Cadastro de Funcionário Cancelado pelo Usuário!", "Cadastro de Funcionário", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nome = nome.trim();
            while (nome.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nome não pode estar vazio!", "Erro", JOptionPane.ERROR_MESSAGE);
                nome = JOptionPane.showInputDialog(null, "Insira o Nome do Funcionário:", "Cadastro de Funcionário", JOptionPane.QUESTION_MESSAGE);
                if (nome == null) {
                    JOptionPane.showMessageDialog(null, "Cadastro de Funcionário Cancelado pelo Usuário!", "Cadastro de Funcionário", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                nome = nome.trim();
            }

            String cpf = "";
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            JFormattedTextField campoCPF = new JFormattedTextField(maskCPF);
            JPanel panelCPF = new JPanel(new GridLayout(0, 1));
            panelCPF.add(new JLabel("Insira o CPF do Funcionário:"));
            panelCPF.add(campoCPF);
            int resCPF = JOptionPane.showConfirmDialog(
                null,
                panelCPF,
                "Cadastro de Funcionário",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resCPF != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Cadastro de Funcionário Cancelado pelo Usuário!", "Cadastro de Funcionário", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cpf = campoCPF.getText().replaceAll("[^\\d]", "");
            while (cpf.length() != 11) {
                JOptionPane.showMessageDialog(null, "CPF inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                resCPF = JOptionPane.showConfirmDialog(
                    null,
                    panelCPF,
                    "Cadastro de Funcionário",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
                );
                if (resCPF != JOptionPane.OK_OPTION) {
                    JOptionPane.showMessageDialog(null, "Cadastro de Funcionário Cancelado pelo Usuário!", "Cadastro de Funcionário", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                cpf = campoCPF.getText().replaceAll("[^\\d]", "");
            }

            FuncionariosBean fb = new FuncionariosBean(novaMatricula, nome, cpf, id_cargo, true);
            FuncionariosModel.create(fb, session);
            JOptionPane.showMessageDialog(null, "Funcionário Cadastrado com Sucesso!", "Cadastro de Funcionário", 1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cadastro de Funcionário cancelado devido a Erro Interno!", "Cadastro de Funcionário", JOptionPane.WARNING_MESSAGE);
        }        
    }
    
    public void updateFuncionario(Session session) throws SQLException, ParseException {
        List<FuncionariosBean> listaFuncionarios = FuncionariosModel.listAllAtivos(session);
        if (listaFuncionarios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Não há Funcionários Cadastrados!", "Atualização de Funcionário", JOptionPane.WARNING_MESSAGE);
            return;
        }
        listaFuncionarios.sort(Comparator.comparing(FuncionariosBean::getNome));
        JComboBox<FuncionariosBean> selecaoFuncionarios = new JComboBox<>();
        for (FuncionariosBean fb : listaFuncionarios) {
            selecaoFuncionarios.addItem(fb);
        }
        JPanel painelFuncionarios = new JPanel(new GridLayout(0, 1));
        painelFuncionarios.add(new JLabel("Selecione o Funcionário que deseja Atualizar:"));
        painelFuncionarios.add(selecaoFuncionarios);
        int resFuncionarios = JOptionPane.showConfirmDialog(
            null,
            painelFuncionarios,
            "Atualização de Funcionário",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resFuncionarios != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Funcionário", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FuncionariosBean funcionarioSelecionado = (FuncionariosBean) selecaoFuncionarios.getSelectedItem();

        List<CargosBean> listaCargos = CargosModel.listAll(session);
        listaCargos.sort(Comparator.comparing(CargosBean::getDescricao));
        JComboBox<CargosBean> selecaoCargos = new JComboBox<>();
        for (CargosBean cb : listaCargos) {
            selecaoCargos.addItem(cb);
        }
        for (int i = 0; i < selecaoCargos.getItemCount(); i++) {
            if (selecaoCargos.getItemAt(i).getId() == funcionarioSelecionado.getIdCargo()) {
                selecaoCargos.setSelectedIndex(i);
                break;
            }
        }
        JButton novoCargo = new JButton("Novo Cargo");
        novoCargo.addActionListener(e -> {
            try {
                new CargosController().createCargo(session);
                selecaoCargos.removeAllItems();
                List<CargosBean> atualizada = CargosModel.listAll(session);
                atualizada.sort(Comparator.comparing(CargosBean::getDescricao));
                for (CargosBean cb : atualizada) {
                    selecaoCargos.addItem(cb);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro ao criar Cargo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel panelCargo = new JPanel(new BorderLayout(5, 5));
        panelCargo.add(new JLabel("Selecione o Cargo do Funcionário:"), BorderLayout.NORTH);
        panelCargo.add(selecaoCargos, BorderLayout.CENTER);
        panelCargo.add(novoCargo, BorderLayout.SOUTH);
        int resCargo = JOptionPane.showConfirmDialog(
            null,
            panelCargo,
            "Atualização de Funcionário",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resCargo != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Funcionário", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CargosBean cargoSelecionado = (CargosBean) selecaoCargos.getSelectedItem();

        String nome = funcionarioSelecionado.getNome();
        JTextField campoNome = new JTextField(nome);
        JPanel panelNome = new JPanel(new GridLayout(0, 1));
        panelNome.add(new JLabel("Insira o Nome do Funcionário:"));
        panelNome.add(campoNome);
        int resNome = JOptionPane.showConfirmDialog(
            null,
            panelNome,
            "Atualização de Funcionário",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resNome != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Funcionário", JOptionPane.WARNING_MESSAGE);
            return;
        }
        nome = campoNome.getText().trim();
        while (nome.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nome não pode estar Vazio!", "Erro", JOptionPane.ERROR_MESSAGE);
            resNome = JOptionPane.showConfirmDialog(
                null,
                panelNome,
                "Atualização de Funcionário",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resNome != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Funcionário", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nome = campoNome.getText().trim();
        }

        String cpf = funcionarioSelecionado.getCpf();
        MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
        maskCPF.setPlaceholderCharacter('_');
        JFormattedTextField campoCPF = new JFormattedTextField(maskCPF);
        campoCPF.setText(cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4"));
        JPanel panelCPF = new JPanel(new GridLayout(0, 1));
        panelCPF.add(new JLabel("Insira o CPF do Funcionário:"));
        panelCPF.add(campoCPF);
        int resCPF = JOptionPane.showConfirmDialog(
            null,
            panelCPF,
            "Atualização de Funcionário",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resCPF != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Funcionário", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cpf = campoCPF.getText().replaceAll("[^\\d]", "");
        while (cpf.length() != 11) {
            JOptionPane.showMessageDialog(null, "CPF inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            campoCPF.setText(funcionarioSelecionado.getCpf().replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4"));
            resCPF = JOptionPane.showConfirmDialog(
                null,
                panelCPF,
                "Atualização de Funcionário",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resCPF != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Funcionário", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cpf = campoCPF.getText().replaceAll("[^\\d]", "");
        }

        funcionarioSelecionado.setNome(nome);
        funcionarioSelecionado.setCpf(cpf);
        funcionarioSelecionado.setIdCargo(cargoSelecionado.getId());
        FuncionariosModel.update(funcionarioSelecionado, session);
        JOptionPane.showMessageDialog(null, "Funcionário Atualizado com Sucesso!", "Atualização de Funcionário", JOptionPane.INFORMATION_MESSAGE);
    }
    
    void mostraFuncionario(Session session) throws SQLException {
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);
        boolean continuar = true;

        do {
            JComboBox<FuncionariosBean> selecaoFuncionarios = new JComboBox<>();
            List<FuncionariosBean> listaFuncionarios = FuncionariosModel.listAllAtivos(session);
            if (listaFuncionarios.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Não há Funcionários Cadastrados!", "Consulta de Funcionário", JOptionPane.WARNING_MESSAGE);
                return;
            }
            listaFuncionarios.sort(Comparator.comparing(FuncionariosBean::getNome));
            for (FuncionariosBean fb : listaFuncionarios) {
                selecaoFuncionarios.addItem(fb);
            }
            JPanel painelFuncionarios = new JPanel(new GridLayout(0, 1));
            painelFuncionarios.add(new JLabel("Selecione o Funcionário que deseja Consultar:"));
            painelFuncionarios.add(selecaoFuncionarios);
            int resFuncionarios = JOptionPane.showConfirmDialog(
                null,
                painelFuncionarios,
                "Consulta de Funcionário",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );

            FuncionariosBean funcionarioSelecionado = null;
            if (resFuncionarios == JOptionPane.OK_OPTION) {
                funcionarioSelecionado = (FuncionariosBean) selecaoFuncionarios.getSelectedItem();
                if (funcionarioSelecionado != null) {
                    String consulta = "Funcionário Consultado:";
                    consulta += "\n  -  Matrícula: " + funcionarioSelecionado.getMatricula();
                    consulta += "\n  -  Nome: " + funcionarioSelecionado.getNome();
                    consulta += "\n  -  CPF: " + funcionarioSelecionado.getCpf().replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
                    CargosBean cargoFuncionario = null;
                    List<CargosBean> listaCargos = CargosModel.listAll(session);
                    for (CargosBean cb : listaCargos) {
                        if (cb.getId() == funcionarioSelecionado.getIdCargo()) {
                            cargoFuncionario = cb;
                            break;
                        }
                    }
                    consulta += "\n  -  Cargo: " + cargoFuncionario.getDescricao();
                    consulta += "\n  -  Salário: " + formatoMoeda.format(cargoFuncionario.getSalario());

                    consulta += "\n\nDeseja Consultar outro Funcionário?";
                    int resposta = JOptionPane.showConfirmDialog(
                        null,
                        consulta,
                        "Consulta de Funcionário",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );

                    if (resposta != JOptionPane.YES_OPTION) {
                        continuar = false;
                        JOptionPane.showMessageDialog(null, "Consulta Finalizada com Sucesso!", "Consulta de Funcionário", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Consulta Cancelada pelo Usuário!", "Consulta de Funcionário", JOptionPane.WARNING_MESSAGE);
                continuar = false;
            }
        } while (continuar);        
    }
    
    void updateStatusFuncionario(Session session) throws SQLException {
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);
        
        Object[] options = { "Ativos", "Inativos" };
        int opcaoStatus = JOptionPane.showOptionDialog(
            null,
            "Deseja consultar Funcionários Ativos ou Inativos?",
            "Alteração do Status do Funcionário",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        if (opcaoStatus == JOptionPane.CLOSED_OPTION) {
            JOptionPane.showMessageDialog(null, "Alteração Cancelada pelo Usuário!", "Alteração do Status do Funcionário", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<FuncionariosBean> selecaoFuncionarios = new JComboBox<>();
        List<FuncionariosBean> listaFuncionarios;
        String msg = "";
        if (opcaoStatus == 0) {
            listaFuncionarios = FuncionariosModel.listAllAtivos(session);
            msg = "Ativos";
        } else {
            listaFuncionarios = FuncionariosModel.listAllInativos(session);
            msg = "Inativos";
        }
        if (listaFuncionarios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Não há Funcionários " + msg + " Cadastrados!", "Alteração do Status do Funcionário", JOptionPane.WARNING_MESSAGE);
            return;
        }
        listaFuncionarios.sort(Comparator.comparing(FuncionariosBean::getNome));
        for (FuncionariosBean fb : listaFuncionarios) {
            selecaoFuncionarios.addItem(fb);
        }
        JPanel painelFuncionarios = new JPanel(new GridLayout(0, 1));
        painelFuncionarios.add(new JLabel("Selecione o Funcionário para Alterar o Status:"));
        painelFuncionarios.add(selecaoFuncionarios);
        int resFuncionarios = JOptionPane.showConfirmDialog(
            null,
            painelFuncionarios,
            "Alteração do Status do Funcionário",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resFuncionarios != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Alteração Cancelada pelo Usuário!", "Alteração do Status do Funcionário", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FuncionariosBean funcionarioSelecionado = (FuncionariosBean) selecaoFuncionarios.getSelectedItem();
        String status = "";
        if(funcionarioSelecionado.getAtivo()) {
            status = "Ativo";
        } else {
            status = "Inativo";
        }
        String consulta = "Dados do Funcionário (" + status + "):";
        consulta += "\n  -  Matrícula: " + funcionarioSelecionado.getMatricula();
        consulta += "\n  -  Nome: " + funcionarioSelecionado.getNome();
        consulta += "\n  -  CPF: " + funcionarioSelecionado.getCpf().replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        CargosBean cargoFuncionario = null;
        List<CargosBean> listaCargos = CargosModel.listAll(session);
        for (CargosBean cb : listaCargos) {
            if (cb.getId() == funcionarioSelecionado.getIdCargo()) {
                cargoFuncionario = cb;
                break;
            }
        }
        consulta += "\n  -  Cargo: " + cargoFuncionario.getDescricao();
        consulta += "\n  -  Salário: " + formatoMoeda.format(cargoFuncionario.getSalario());
        consulta += "\n\nDeseja alterar o status deste Funcionário?";
        int res = JOptionPane.showConfirmDialog(
            null,
            consulta,
            "Alteração do Status do Funcionário",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (res == JOptionPane.YES_OPTION) {
            boolean novoStatus = !funcionarioSelecionado.getAtivo();
            funcionarioSelecionado.setAtivo(novoStatus);

            FuncionariosModel.updateStatus(funcionarioSelecionado, session);
            JOptionPane.showMessageDialog(null, "Status do Funcionário Alterado com Sucesso!", "Alteração do Status do Funcionário", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Alteração Cancelada pelo Usuário!", "Alteração do Status do Funcionário", JOptionPane.WARNING_MESSAGE);
        }
    }

}