import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.neo4j.driver.Session;

public class ProdutosController {
    
    public void createProduto(Session session) throws SQLException, ParseException {        
        List<CategoriasBean> listaCategorias = CategoriasModel.listAll(session);
        CategoriasBean categoriaSelecionada = null;
        if (listaCategorias.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma Categoria Cadastrada! Encaminhando...", "Cadastro de Produto", JOptionPane.INFORMATION_MESSAGE);
            CategoriasController controller = new CategoriasController();
            controller.createCategoria(session);
            listaCategorias = CategoriasModel.listAll(session);
            if (listaCategorias.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Não foi possível criar Categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            categoriaSelecionada = listaCategorias.get(listaCategorias.size() - 1);
        } else {
            listaCategorias.sort(Comparator.comparing(CategoriasBean::getDescricao));
            JComboBox<CategoriasBean> selecaoCategorias = new JComboBox<>();
            for (CategoriasBean cb : listaCategorias) {
                selecaoCategorias.addItem(cb);
            }
            JButton novaCategoria = new JButton("Nova Categoria");
            novaCategoria.addActionListener(e -> {
                try {
                    CategoriasController controller = new CategoriasController();
                    controller.createCategoria(session);
                    selecaoCategorias.removeAllItems();
                    List<CategoriasBean> atualizada = CategoriasModel.listAll(session);
                    atualizada.sort(Comparator.comparing(CategoriasBean::getDescricao));
                    for (CategoriasBean cb : atualizada) {
                        selecaoCategorias.addItem(cb);
                    }
                    selecaoCategorias.setSelectedIndex(atualizada.size() - 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao criar categoria.");
                }
            });
            JPanel panelCategoria = new JPanel(new BorderLayout(5, 5));
            panelCategoria.add(new JLabel("Selecione a Categoria do Produto:"), BorderLayout.NORTH);
            panelCategoria.add(selecaoCategorias, BorderLayout.CENTER);
            panelCategoria.add(novaCategoria, BorderLayout.SOUTH);
            int resCategoria = JOptionPane.showConfirmDialog(
                    null, 
                    panelCategoria, 
                    "Cadastro de Produto", 
                    JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (resCategoria != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Produto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            categoriaSelecionada = (CategoriasBean) selecaoCategorias.getSelectedItem();
        }
        int id_categoria = categoriaSelecionada.getId();
        String uni_med = categoriaSelecionada.getUniMed();
        
        List<MarcasBean> listaMarcas = MarcasModel.listAll(session);
        MarcasBean marcaSelecionada = null;
        if (listaMarcas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma Marca Cadastrada! Encaminhando...", "Cadastro de Produto", JOptionPane.INFORMATION_MESSAGE);
            MarcasController controller = new MarcasController();
            controller.createMarca(session);
            listaMarcas = MarcasModel.listAll(session);
            if (listaMarcas.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Não foi possível criar Marca!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            marcaSelecionada = listaMarcas.get(listaMarcas.size() - 1);
        } else {
            listaMarcas.sort(Comparator.comparing(MarcasBean::getNome));
            JComboBox<MarcasBean> selecaoMarcas = new JComboBox<>();
            for (MarcasBean mb : listaMarcas) {
                selecaoMarcas.addItem(mb);
            }
            JButton novaMarca = new JButton("Nova Marca");
            novaMarca.addActionListener(e -> {
                try {
                    MarcasController controller = new MarcasController();
                    controller.createMarca(session);
                    selecaoMarcas.removeAllItems();
                    List<MarcasBean> atualizada = MarcasModel.listAll(session);
                    atualizada.sort(Comparator.comparing(MarcasBean::getNome));
                    for (MarcasBean mb : atualizada) {
                        selecaoMarcas.addItem(mb);
                    }
                    selecaoMarcas.setSelectedIndex(atualizada.size() - 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao criar Marca.");
                }
            });
            JPanel panelMarca = new JPanel(new BorderLayout(5, 5));
            panelMarca.add(new JLabel("Selecione a Marca do Produto:"), BorderLayout.NORTH);
            panelMarca.add(selecaoMarcas, BorderLayout.CENTER);
            panelMarca.add(novaMarca, BorderLayout.SOUTH);
            int resMarca = JOptionPane.showConfirmDialog(
                    null, 
                    panelMarca, 
                    "Cadastro de Produto", 
                    JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (resMarca != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Produto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            marcaSelecionada = (MarcasBean) selecaoMarcas.getSelectedItem();
        }
        int id_marca = marcaSelecionada.getId();
        
        String descricao = JOptionPane.showInputDialog(null, "Insira a Descrição do Produto:", "Cadastro de Produto", JOptionPane.QUESTION_MESSAGE);
        if (descricao == null) {
            JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Produto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        descricao = descricao.trim();
        while (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Descrição não pode estar vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
            descricao = JOptionPane.showInputDialog(null, "Insira a Descrição do Produto:", "Cadastro de Produto", JOptionPane.QUESTION_MESSAGE);
            if (descricao == null) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Produto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            descricao = descricao.trim();
        }
        
        CampoMoeda campoPreco = new CampoMoeda();
        JPanel panelPreco = new JPanel(new GridLayout(0, 1));
        if(uni_med.equals("kg")) {
            panelPreco.add(new JLabel("Insira o Preço do Kg do Produto:"));
        } else {
            panelPreco.add(new JLabel("Insira o Preço Unitário do Produto:"));
        }
        panelPreco.add(campoPreco);
        int resPreco = JOptionPane.showConfirmDialog(
            null,
            panelPreco,
            "Cadastro de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resPreco != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Produto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double preco = campoPreco.getValor();
        while (preco <= 0.0) {
            JOptionPane.showMessageDialog(null, "Preço deve ser maior que Zero!", "Erro", JOptionPane.ERROR_MESSAGE);
            resPreco = JOptionPane.showConfirmDialog(
                null,
                panelPreco,
                "Cadastro de Produto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resPreco != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Produto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            preco = campoPreco.getValor();
        }

        JPanel panelQuantidade = new JPanel(new GridLayout(0, 1));
        panelQuantidade.add(new JLabel("Insira a Quantidade do Produto:"));
        JTextField campoQuantidade;
        if(uni_med.equals("kg")) {
            campoQuantidade = new CampoKg();
        } else {
            campoQuantidade = new CampoUni();
        }
        panelQuantidade.add(campoQuantidade);
        int resQuantidade = JOptionPane.showConfirmDialog(
            null,
            panelQuantidade,
            "Cadastro de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resQuantidade != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Produto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double quantidade = 0.0;
        if(uni_med.equals("kg")) {
            quantidade = ((CampoKg) campoQuantidade).getValor();
        } else {
            quantidade = ((CampoUni) campoQuantidade).getQuantidade();
        }
        while (quantidade <= 0.0) {
            JOptionPane.showMessageDialog(null, "Quantidade deve ser maior que Zero!", "Erro", JOptionPane.ERROR_MESSAGE);
            resQuantidade = JOptionPane.showConfirmDialog(
                null,
                panelQuantidade,
                "Cadastro de Produto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resQuantidade != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Cadastro Cancelado pelo Usuário!", "Cadastro de Produto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (uni_med.equals("kg")) {
                quantidade = ((CampoKg) campoQuantidade).getValor();
            } else {
                quantidade = ((CampoUni) campoQuantidade).getQuantidade();
            }
        }
        
        ProdutosBean pb = new ProdutosBean(0, descricao, preco, quantidade, id_marca, id_categoria);
        ProdutosModel.create(pb, session);
        JOptionPane.showMessageDialog(null, "Produto Cadastrado com Sucesso!", "Cadastro de Produto", 1);        
    }
    
    void addQtddProduto(Session session) throws SQLException {
        JComboBox<CategoriasBean> selecaoCategoria = new JComboBox<>();
        List<CategoriasBean> listaCategorias = CategoriasModel.listAll(session);
        if (listaCategorias.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum Produto Cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        listaCategorias.sort(Comparator.comparing(CategoriasBean::getDescricao));
        for (CategoriasBean cb : listaCategorias) {
            selecaoCategoria.addItem(cb);
        }
        JPanel panelCategoria = new JPanel(new GridLayout(0, 1));
        panelCategoria.add(new JLabel("Selecione a Categoria do Produto:"));
        panelCategoria.add(selecaoCategoria);
        int resCategoria = JOptionPane.showConfirmDialog(
            null,
            panelCategoria,
            "Adicionar Quantidade",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resCategoria != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Operação Cancelada pelo Usuário!", "Adicionar Quantidade", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CategoriasBean categoriaSelecionada = (CategoriasBean) selecaoCategoria.getSelectedItem();
        int id_categoria = categoriaSelecionada.getId();
        
        JComboBox<ProdutosBean> selecaoProdutos = new JComboBox<>();
        List<ProdutosBean> listaProdutos = ProdutosModel.listAll(session);
        listaProdutos.sort(Comparator.comparing(ProdutosBean::getDescricao));
        int qtde = 0;
        for (ProdutosBean pb : listaProdutos) {
            if(pb.getIdCategoria() == id_categoria) {
                selecaoProdutos.addItem(pb);
                qtde++;
            }
        }
        if (qtde == 0) {
            JOptionPane.showMessageDialog(null, "Nenhum Produto cadastrado para esta Categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JPanel painelProdutos = new JPanel(new GridLayout(0, 1));
        painelProdutos.add(new JLabel("Selecione o Produto que deseja adicionar Quantidade:"));
        painelProdutos.add(selecaoProdutos);
        int resProdutos = JOptionPane.showConfirmDialog(
            null,
            painelProdutos,
            "Adicionar Quantidade",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resProdutos != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Operação Cancelada pelo Usuário!", "Adicionar Quantidade", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ProdutosBean produtoSelecionado = (ProdutosBean) selecaoProdutos.getSelectedItem();
        int id_produto = produtoSelecionado.getId();
        
        String uni_med = ProdutosModel.selectUniMed(produtoSelecionado, session);  
        JPanel panelQuantidade = new JPanel(new GridLayout(0, 1));
        panelQuantidade.add(new JLabel("Insira a Quantidade do Produto a ser Adicionada:"));
        JTextField campoQuantidade;
        if(uni_med.equals("kg")) {
            campoQuantidade = new CampoKg();
        } else {
            campoQuantidade = new CampoUni();
        }
        panelQuantidade.add(campoQuantidade);
        int resQuantidade = JOptionPane.showConfirmDialog(
            null,
            panelQuantidade,
            "Adicionar Quantidade",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resQuantidade != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Operação Cancelada pelo Usuário!", "Adicionar Quantidade", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double quantidade = 0.0;
        if(uni_med.equals("kg")) {
            quantidade = ((CampoKg) campoQuantidade).getValor();
        } else {
            quantidade = ((CampoUni) campoQuantidade).getQuantidade();
        }
        while (quantidade <= 0.0) {
            JOptionPane.showMessageDialog(null, "Quantidade deve ser maior que Zero!", "Erro", JOptionPane.ERROR_MESSAGE);
            resQuantidade = JOptionPane.showConfirmDialog(
                null,
                panelQuantidade,
                "Adicionar Quantidade",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resQuantidade != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Operação Cancelada pelo Usuário!", "Adicionar Quantidade", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (uni_med.equals("kg")) {
                quantidade = ((CampoKg) campoQuantidade).getValor();
            } else {
                quantidade = ((CampoUni) campoQuantidade).getQuantidade();
            }
        }
        
        produtoSelecionado.setQuantidade(produtoSelecionado.getQuantidade() + quantidade);
        ProdutosModel.updateQuantidade(produtoSelecionado, session);
        JOptionPane.showMessageDialog(null, "Quantidade Atualizada com Sucesso!", "Adicionar Quantidade", JOptionPane.INFORMATION_MESSAGE);
    }
    
    void updateProduto(Session session) throws SQLException {
        JComboBox<CategoriasBean> selecaoCategoria = new JComboBox<>();
        List<CategoriasBean> listaCategorias = CategoriasModel.listAll(session);
        if (listaCategorias.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum Produto Cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        listaCategorias.sort(Comparator.comparing(CategoriasBean::getDescricao));
        for (CategoriasBean cb : listaCategorias) {
            selecaoCategoria.addItem(cb);
        }
        JPanel panelCategoria = new JPanel(new GridLayout(0, 1));
        panelCategoria.add(new JLabel("Selecione a Categoria do Produto que deseja Atualizar:"));
        panelCategoria.add(selecaoCategoria);
        int resCategoria = JOptionPane.showConfirmDialog(
            null,
            panelCategoria,
            "Atualização de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resCategoria != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Produto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CategoriasBean categoriaSelecionada = (CategoriasBean) selecaoCategoria.getSelectedItem();
        int id_categoria = categoriaSelecionada.getId();
        
        JComboBox<ProdutosBean> selecaoProdutos = new JComboBox<>();
        List<ProdutosBean> listaProdutos = ProdutosModel.listAll(session);
        listaProdutos.sort(Comparator.comparing(ProdutosBean::getDescricao));
        int qtde = 0;
        for (ProdutosBean pb : listaProdutos) {
            if (pb.getIdCategoria() == id_categoria) {
                selecaoProdutos.addItem(pb);
                qtde++;
            }
        }
        if (qtde == 0) {
            JOptionPane.showMessageDialog(null, "Nenhum Produto cadastrado para esta Categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JPanel painelProdutos = new JPanel(new GridLayout(0, 1));
        painelProdutos.add(new JLabel("Selecione o Produto que deseja Atualizar:"));
        painelProdutos.add(selecaoProdutos);
        int resProdutos = JOptionPane.showConfirmDialog(
            null,
            painelProdutos,
            "Atualização de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resProdutos != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Produto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ProdutosBean ProdSelecionado = (ProdutosBean) selecaoProdutos.getSelectedItem();
        
        JButton novaCategoria = new JButton("Nova Categoria");
        novaCategoria.addActionListener(e -> {
            try {
                CategoriasController controller = new CategoriasController();
                controller.createCategoria(session);
                selecaoCategoria.removeAllItems();
                List<CategoriasBean> atualizada = CategoriasModel.listAll(session);
                atualizada.sort(Comparator.comparing(CategoriasBean::getDescricao));
                for (CategoriasBean cb : atualizada) {
                    selecaoCategoria.addItem(cb);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao criar Categoria.");
            }
        });
        panelCategoria = new JPanel();
        panelCategoria.setLayout(new BorderLayout(5, 5));
        panelCategoria.add(new JLabel("Selecione a Categoria do Produto:"), BorderLayout.NORTH);
        panelCategoria.add(selecaoCategoria, BorderLayout.CENTER);
        panelCategoria.add(novaCategoria, BorderLayout.SOUTH);
        resCategoria = JOptionPane.showConfirmDialog(
            null,
            panelCategoria,
            "Atualização de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resCategoria != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Produto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        categoriaSelecionada = (CategoriasBean) selecaoCategoria.getSelectedItem();
        id_categoria = categoriaSelecionada.getId();
        String uni_med = categoriaSelecionada.getUniMed();
        
        JComboBox<MarcasBean> selecaoMarcas = new JComboBox<>();
        List<MarcasBean> listaMarcas = MarcasModel.listAll(session);
        listaMarcas.sort(Comparator.comparing(MarcasBean::getNome));
        for (MarcasBean mb : listaMarcas) {
            selecaoMarcas.addItem(mb);
        }
        if (ProdSelecionado != null) {
            for (int i = 0; i < selecaoMarcas.getItemCount(); i++) {
                if (selecaoMarcas.getItemAt(i).getId() == ProdSelecionado.getIdMarca()) {
                    selecaoMarcas.setSelectedIndex(i);
                    break;
                }
            }
        }
        JButton novaMarca = new JButton("Nova Marca");
        novaMarca.addActionListener(e -> {
            try {
                MarcasController controller = new MarcasController();
                controller.createMarca(session);
                selecaoMarcas.removeAllItems();
                List<MarcasBean> atualizada = MarcasModel.listAll(session);
                atualizada.sort(Comparator.comparing(MarcasBean::getNome));
                for (MarcasBean mb : atualizada) {
                    selecaoMarcas.addItem(mb);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao criar marca.");
            }
        });
        JPanel panelMarca = new JPanel();
        panelMarca.setLayout(new BorderLayout(5, 5));
        panelMarca.add(new JLabel("Selecione a Marca do Produto:"), BorderLayout.NORTH);
        panelMarca.add(selecaoMarcas, BorderLayout.CENTER);
        panelMarca.add(novaMarca, BorderLayout.SOUTH);
        int resMarca = JOptionPane.showConfirmDialog(
            null,
            panelMarca,
            "Atualização de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resMarca != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Produto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MarcasBean marcaSelecionada = (MarcasBean) selecaoMarcas.getSelectedItem();
        int id_marca = marcaSelecionada.getId();
        
        JTextField campoDescricao = new JTextField(ProdSelecionado.getDescricao());
        JPanel panelDescricao = new JPanel(new GridLayout(0, 1));
        panelDescricao.add(new JLabel("Insira a Descrição do Produto:"));
        panelDescricao.add(campoDescricao);
        int resDescricao = JOptionPane.showConfirmDialog(
            null,
            panelDescricao,
            "Atualização de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resDescricao != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Marca", JOptionPane.WARNING_MESSAGE);
            return;
        }        
        String descricao = campoDescricao.getText().trim();
        while (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Descrição do Produto não pode estar Vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
            panelDescricao.remove(campoDescricao);
            campoDescricao = new JTextField(ProdSelecionado.getDescricao());
            panelDescricao.add(campoDescricao);
            resDescricao = JOptionPane.showConfirmDialog(
                null,
                panelDescricao,
                "Atualização de Produto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resDescricao != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Marca", JOptionPane.WARNING_MESSAGE);
                return;
            }
            descricao = campoDescricao.getText().trim();
        }
        
        CampoMoeda campoPreco = new CampoMoeda();
        campoPreco.preencherValor(ProdSelecionado.getPreco());
        JPanel panelPreco = new JPanel(new GridLayout(0, 1));
        if (uni_med.equals("kg")) {
            panelPreco.add(new JLabel("Insira o Preço do Kg do Produto:"));
        } else {
            panelPreco.add(new JLabel("Insira o Preço Unitário do Produto:"));
        }
        panelPreco.add(campoPreco);
        int resPreco = JOptionPane.showConfirmDialog(
            null,
            panelPreco,
            "Atualização de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resPreco != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Produto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double preco = campoPreco.getValor();
        while (preco <= 0.0) {
            JOptionPane.showMessageDialog(null, "Preço deve ser maior que Zero!", "Erro", JOptionPane.ERROR_MESSAGE);
            resPreco = JOptionPane.showConfirmDialog(
                null,
                panelPreco,
                "Atualização de Produto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resPreco != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Produto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            preco = campoPreco.getValor();
        }
        
        JPanel panelQuantidade = new JPanel(new GridLayout(0, 1));
        panelQuantidade.add(new JLabel("Insira a Quantidade do Produto:"));
        JTextField campoQuantidade;
        if (uni_med.equals("kg")) {
            campoQuantidade = new CampoKg();
            ((CampoKg) campoQuantidade).preencherValor(ProdSelecionado.getQuantidade());
        } else {
            campoQuantidade = new CampoUni();
            ((CampoUni) campoQuantidade).preencherQuantidade((int) ProdSelecionado.getQuantidade());
        }
        panelQuantidade.add(campoQuantidade);
        int resQuantidade = JOptionPane.showConfirmDialog(
            null,
            panelQuantidade,
            "Atualização de Produto",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (resQuantidade != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Produto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double quantidade = 0.0;
        if(uni_med.equals("kg")) {
            quantidade = ((CampoKg) campoQuantidade).getValor();
        } else {
            quantidade = ((CampoUni) campoQuantidade).getQuantidade();
        }
        while (quantidade <= 0.0) {
            JOptionPane.showMessageDialog(null, "Quantidade deve ser maior que Zero!", "Erro", JOptionPane.ERROR_MESSAGE);
            resQuantidade = JOptionPane.showConfirmDialog(
                null,
                panelQuantidade,
                "Atualização de Produto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resQuantidade != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Atualização Cancelada pelo Usuário!", "Atualização de Produto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(uni_med.equals("kg")) {
                quantidade = ((CampoKg) campoQuantidade).getValor();
            } else {
                quantidade = ((CampoUni) campoQuantidade).getQuantidade();
            }
        }
        
        ProdSelecionado.setIdCategoria(id_categoria);
        ProdSelecionado.setIdMarca(id_marca);
        ProdSelecionado.setDescricao(descricao);
        ProdSelecionado.setPreco(preco);
        ProdSelecionado.setQuantidade(quantidade);
        ProdutosModel.update(ProdSelecionado, session);
        JOptionPane.showMessageDialog(null, "Produto Atualizado com Sucesso!", "Atualização de Produto", JOptionPane.INFORMATION_MESSAGE);
    }
    
    void mostraProduto(Session session) throws SQLException {
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);
        DecimalFormat formatoKg = new DecimalFormat("###,##0.000");
        DecimalFormat formatoUni = new DecimalFormat("###,##0");
        boolean continuar = true;
        do {
            JComboBox<CategoriasBean> selecaoCategoria = new JComboBox<>();
            List<CategoriasBean> listaCategorias = CategoriasModel.listAll(session);
            if (listaCategorias.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhum Produto Cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            listaCategorias.sort(Comparator.comparing(CategoriasBean::getDescricao));
            for (CategoriasBean cb : listaCategorias) {
                selecaoCategoria.addItem(cb);
            }
            JPanel panelCategoria = new JPanel(new GridLayout(0, 1));
            panelCategoria.add(new JLabel("Selecione a Categoria do Produto que deseja Consultar:"));
            panelCategoria.add(selecaoCategoria);
            int resCategoria = JOptionPane.showConfirmDialog(
                null,
                panelCategoria,
                "Atualização de Produto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resCategoria != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Consulta Cancelada pelo Usuário!", "Consulta de Produto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            CategoriasBean categoriaSelecionada = (CategoriasBean) selecaoCategoria.getSelectedItem();
            int id_categoria = categoriaSelecionada.getId();

            JComboBox<ProdutosBean> selecaoProdutos = new JComboBox<>();
            List<ProdutosBean> listaProdutos = ProdutosModel.listAll(session);
            listaProdutos.sort(Comparator.comparing(ProdutosBean::getDescricao));
            int qtde = 0;
            for (ProdutosBean pb : listaProdutos) {
                if (pb.getIdCategoria() == id_categoria) {
                    selecaoProdutos.addItem(pb);
                    qtde++;
                }
            }
            if (qtde == 0) {
                JOptionPane.showMessageDialog(null, "Nenhum Produto cadastrado para esta Categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JPanel painelProdutos = new JPanel(new GridLayout(0, 1));
            painelProdutos.add(new JLabel("Selecione o Produto que deseja Consultar:"));
            painelProdutos.add(selecaoProdutos);
            int resProdutos = JOptionPane.showConfirmDialog(
                null,
                painelProdutos,
                "Consulta de Produto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resProdutos != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Consulta Cancelada pelo Usuário!", "Consulta de Produto", JOptionPane.WARNING_MESSAGE);
                return;                
            } else {
                ProdutosBean ProdSelecionado = (ProdutosBean) selecaoProdutos.getSelectedItem();
                String uniMed = ProdutosModel.selectUniMed(ProdSelecionado, session);
                String consulta = "Produto Consultado:";
                consulta += "\n  -  ID: " + ProdSelecionado.getId();
                consulta += "\n  -  Categoria: " + ProdutosModel.selectDescCategoria(ProdSelecionado, session);
                consulta += "\n  -  Marca: " + ProdutosModel.selectNomeMarca(ProdSelecionado, session);
                consulta += "\n  -  Descrição: " + ProdSelecionado.getDescricao();
                consulta += "\n  -  Preço: " + formatoMoeda.format(ProdSelecionado.getPreco());
                if (uniMed.equals("kg")) {
                    consulta += "\n  -  Quantidade: " +  formatoKg.format(ProdSelecionado.getQuantidade()) + " kg";
                } else {
                    consulta += "\n  -  Quantidade: " +  formatoUni.format(ProdSelecionado.getQuantidade()) + " unidade(s)";
                }
                consulta += "\n\nDeseja consultar outro produto?";
                int resposta = JOptionPane.showConfirmDialog(
                    null,
                    consulta,
                    "Consulta de Produto",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (resposta != JOptionPane.YES_OPTION) {
                    continuar = false;
                    JOptionPane.showMessageDialog(null, "Consulta Finalizada com Sucesso!", "Consulta de Produto", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } while (continuar);
    }
    
}