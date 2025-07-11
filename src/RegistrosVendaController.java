import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.neo4j.driver.Session;

public class RegistrosVendaController {
    
    public static boolean createRegVenda(VendasBean v, Session session) throws SQLException {    
        Locale brasil = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);   
        DecimalFormat formatoKg = new DecimalFormat("###,##0.000");
        DecimalFormat formatoUni = new DecimalFormat("###,##0");
        double totalVenda = 0.0;
        boolean continuar = true;
        boolean houveRegistro = false;

        while (true) {
            JComboBox<CategoriasBean> selecaoCategoria = new JComboBox<>();
            List<CategoriasBean> listaCategorias = CategoriasModel.listAll(session);
            if (listaCategorias.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhum Produto Cadastrado!", "Registro de Venda", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            listaCategorias.sort(Comparator.comparing(CategoriasBean::getDescricao));
            for (CategoriasBean cb : listaCategorias) {
                selecaoCategoria.addItem(cb);
            }
            JPanel panelCategoria = new JPanel(new GridLayout(0, 1));
            panelCategoria.add(new JLabel("<html><div style='text-align:center'><b>Registro de Venda</b></div><br>Selecione a Categoria do Produto:</html>"));
            panelCategoria.add(selecaoCategoria);
            int resCategoria = JOptionPane.showConfirmDialog(
                null, 
                panelCategoria, 
                "Registro de Venda", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resCategoria != JOptionPane.OK_OPTION) {
                if (!houveRegistro) {
                    Object[] opcoes = {"Continuar", "Encerrar"};
                    int escolha = JOptionPane.showOptionDialog(
                        null,
                        "Operação cancelada pelo Usuário!\nDeseja continuar o Registro ou encerrar a Venda?",
                        "Registro de Venda",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opcoes,
                        opcoes[0]
                    );
                    if (escolha == 1) {
                        continuar = false;
                        return false;
                    } else {
                        continue;
                    }
                } else {
                    Object[] opcoes = {"Registrar outro Produto", "Finalizar Venda"};
                    int escolha = JOptionPane.showOptionDialog(
                        null, 
                        "Operação cancelada pelo Usuário!\nDeseja registrar outro Produto ou finalizar a Venda?", 
                        "Registro de Venda", 
                        JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        opcoes, 
                        opcoes[0]
                    );
                    if (escolha == 1) {
                        JOptionPane.showMessageDialog(null, "Venda Finalizada com Sucesso!\nValor Total da Venda: " + formatoMoeda.format(totalVenda), "Registro de Venda", JOptionPane.INFORMATION_MESSAGE);
                        continuar = false;
                        return true;
                    } else {
                        continue;
                    }
                }
            }
            CategoriasBean categoriaSelecionada = (CategoriasBean) selecaoCategoria.getSelectedItem();

            JComboBox<ProdutosBean> selecaoProdutos = new JComboBox<>();
            List<ProdutosBean> listaProdutos = ProdutosModel.listAll(session);
            listaProdutos.sort(Comparator.comparing(ProdutosBean::getDescricao));
            int qtde = 0;
            for (ProdutosBean pb : listaProdutos) {
                if (pb.getIdCategoria() == categoriaSelecionada.getId()) {
                    selecaoProdutos.addItem(pb);
                    qtde++;
                }
            }
            if (qtde == 0) {
                if (!houveRegistro) {
                    Object[] opcoes = {"Continuar", "Encerrar"};
                    int escolha = JOptionPane.showOptionDialog(
                        null,
                        "Nenhum Produto encontrado para esta Categoria!\nDeseja continuar o Registro ou encerrar a Venda?",
                        "Registro de Venda",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opcoes,
                        opcoes[0]
                    );
                    if (escolha == 1) {
                        continuar = false;
                        return false;
                    } else {
                        continue;
                    }
                } else {
                    Object[] opcoes = {"Registrar outro Produto", "Finalizar Venda"};
                    int escolha = JOptionPane.showOptionDialog(
                        null, 
                        "Nenhum Produto encontrado para esta Categoria!\nDeseja registrar outro produto ou finalizar a venda?", 
                        "Registro de Venda", 
                        JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        opcoes, 
                        opcoes[0]
                    );
                    if (escolha == 1) {
                        JOptionPane.showMessageDialog(null, "Venda Finalizada com Sucesso!\nValor Total da Venda: " + formatoMoeda.format(totalVenda), "Registro de Venda", JOptionPane.INFORMATION_MESSAGE);
                        continuar = false;
                        return true;
                    } else {
                        continue;
                    }
                }
            }
            JPanel painelProdutos = new JPanel(new GridLayout(0, 1));
            painelProdutos.add(new JLabel("<html><div style='text-align:center'><b>Registro de Venda</b></div><br>Selecione o Produto:</html>"));
            painelProdutos.add(selecaoProdutos);
            int resProdutos = JOptionPane.showConfirmDialog(
                null, 
                painelProdutos, 
                "Registro de Venda", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resProdutos != JOptionPane.OK_OPTION) {
                if (!houveRegistro) {
                    Object[] opcoes = {"Continuar", "Encerrar"};
                    int escolha = JOptionPane.showOptionDialog(
                        null,
                        "Operação cancelada pelo Usuário!\nDeseja continuar o Registro ou encerrar a Venda?",
                        "Registro de Venda",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opcoes,
                        opcoes[0]
                    );
                    if (escolha == 1) {
                        continuar = false;
                        return false;
                    } else {
                        continue;
                    }
                } else {
                    Object[] opcoes = {"Registrar outro Produto", "Finalizar Venda"};
                    int escolha = JOptionPane.showOptionDialog(
                        null, 
                        "Operação cancelada pelo Usuário!\nDeseja registrar outro produto ou finalizar a venda?", 
                        "Registro de Venda", 
                        JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        opcoes, 
                        opcoes[0]
                    );
                    if (escolha == 1) {
                        JOptionPane.showMessageDialog(null, "Venda Finalizada com Sucesso!\nValor Total da Venda: " + formatoMoeda.format(totalVenda), "Registro de Venda", JOptionPane.INFORMATION_MESSAGE);
                        continuar = false;
                        return true;
                    } else {
                        continue;
                    }
                }
            }
            ProdutosBean ProdSelecionado = (ProdutosBean) selecaoProdutos.getSelectedItem();
            double estoqueAtual = ProdSelecionado.getQuantidade();
            double preco = ProdSelecionado.getPreco();

            JPanel panelQuantidade = new JPanel(new GridLayout(0, 1));            
            String uni_med = ProdutosModel.selectUniMed(ProdSelecionado, session);
            if (uni_med.equals("kg")) {
                panelQuantidade.add(new JLabel("<html><div style='text-align:center'><b>Registro de Venda</b></div><br>Insira a Quantidade:<br>(Estoque: " + estoqueAtual + " kg(s))</html>"));
            } else {
                panelQuantidade.add(new JLabel("<html><div style='text-align:center'><b>Registro de Venda</b></div><br>Insira a Quantidade:<br>(Estoque: " + estoqueAtual + " unidade(s))</html>"));
            }
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
                "Registro de Venda", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.INFORMATION_MESSAGE
            );
            if (resQuantidade != JOptionPane.OK_OPTION) {
                if (!houveRegistro) {
                    Object[] opcoes = {"Continuar", "Encerrar"};
                    int escolha = JOptionPane.showOptionDialog(
                        null,
                        "Operação cancelada pelo Usuário!\nDeseja continuar o Registro ou encerrar a Venda?",
                        "Registro de Venda",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opcoes,
                        opcoes[0]
                    );
                    if (escolha == 1) {
                        continuar = false;
                        return false;
                    } else {
                        continue;
                    }
                } else {
                    Object[] opcoes = {"Registrar outro Produto", "Finalizar Venda"};
                    int escolha = JOptionPane.showOptionDialog(
                        null, 
                        "Operação cancelada pelo Usuário!\nDeseja registrar outro produto ou finalizar a venda?", 
                        "Registro de Venda", 
                        JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        opcoes, 
                        opcoes[0]
                    );
                    if (escolha == 1) {
                        JOptionPane.showMessageDialog(null, "Venda Finalizada com Sucesso!\nValor Total da Venda: " + formatoMoeda.format(totalVenda), "Registro de Venda", JOptionPane.INFORMATION_MESSAGE);
                        continuar = false;
                        return true;
                    } else {
                        continue;
                    }
                }
            }
            double quantidade = 0.0;
            if(uni_med.equals("kg")) {
                quantidade = ((CampoKg) campoQuantidade).getValor();
            } else {
                quantidade = ((CampoUni) campoQuantidade).getQuantidade();
            }
            while (quantidade <= 0.0 || quantidade > estoqueAtual) {
                if(quantidade <= 0.0) {
                    JOptionPane.showMessageDialog(null, "Quantidade precisa ser maior que Zero!", "Registro de Venda", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Quantidade não disponível em Estoque!", "Registro de Venda", JOptionPane.ERROR_MESSAGE);
                }
                resQuantidade = JOptionPane.showConfirmDialog(
                    null,
                    panelQuantidade,
                    "Registro de Venda",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
                );
                if (resQuantidade != JOptionPane.OK_OPTION) {
                    if (!houveRegistro) {
                        Object[] opcoes = {"Continuar", "Encerrar"};
                        int escolha = JOptionPane.showOptionDialog(
                            null,
                            "Operação cancelada pelo Usuário!\nDeseja continuar o Registro ou encerrar a Venda?",
                            "Registro de Venda",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            opcoes,
                            opcoes[0]
                        );
                        if (escolha == 1) {
                            continuar = false;
                            return false;
                        } else {
                            continue;
                        }
                    } else {
                        Object[] opcoes = {"Registrar outro Produto", "Finalizar Venda"};
                        int escolha = JOptionPane.showOptionDialog(
                            null, 
                            "Operação cancelada pelo Usuário!\nDeseja registrar outro produto ou finalizar a venda?", 
                            "Registro de Venda", 
                            JOptionPane.DEFAULT_OPTION, 
                            JOptionPane.QUESTION_MESSAGE, 
                            null, 
                            opcoes, 
                            opcoes[0]
                        );
                        if (escolha == 1) {
                            JOptionPane.showMessageDialog(null, "Venda Finalizada com Sucesso!\nValor Total da Venda: " + formatoMoeda.format(totalVenda), "Registro de Venda", JOptionPane.INFORMATION_MESSAGE);
                            continuar = false;
                            return true;
                        } else {
                            continue;
                        }
                    }
                }
                if(categoriaSelecionada.getUniMed().equals("kg")) {
                    quantidade = ((CampoKg) campoQuantidade).getValor();
                } else {
                    quantidade = ((CampoUni) campoQuantidade).getQuantidade();
                }
            }

            RegistrosVendaBean rv = new RegistrosVendaBean(0, v.getId(), ProdSelecionado.getId(), quantidade, preco);
            RegistrosVendaModel.create(rv, session);
            houveRegistro = true;

            ProdSelecionado.setQuantidade(estoqueAtual - quantidade);
            ProdutosModel.update(ProdSelecionado, session);

            String strQuantidade = "";
            if (uni_med.equals("kg")) {
                strQuantidade = formatoKg.format(quantidade) + " kg(s)";
            } else {
                strQuantidade = formatoUni.format(quantidade) + " unidade(s)";
            }
            double subtotal = preco * quantidade;
            totalVenda += subtotal;

            String resumo = String.format(
                "Registro de Venda:\n\n  -  Produto: %s\n  -  Preço: %s\n  -  Quantidade: %s\n\n  -  Subtotal: %s\n  -  Total até o momento: %s",
                ProdSelecionado.getDescricao(),
                formatoMoeda.format(preco),
                strQuantidade,
                formatoMoeda.format(subtotal),
                formatoMoeda.format(totalVenda)
            );
            Object[] opcoes = {"Registrar outro Produto", "Finalizar Venda"};
            int escolha = JOptionPane.showOptionDialog(
                null, 
                resumo + "\n\nDeseja finalizar a venda?", 
                "Registro de Venda",
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                opcoes, 
                opcoes[0]
            );
            if (escolha == 1) {
                JOptionPane.showMessageDialog(null, "Venda Finalizada com Sucesso!\nValor Total da Venda: " + formatoMoeda.format(totalVenda), "Registro de Venda", JOptionPane.INFORMATION_MESSAGE);
                continuar = false;
                return true;
            }
        }
    }    
}