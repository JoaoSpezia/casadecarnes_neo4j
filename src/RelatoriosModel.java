import java.util.ArrayList;
import java.util.List;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;

public class RelatoriosModel {

    public static List<String[]> consProdsMaisVendidosValor(String filtroCypher, Session session) {
        List<String[]> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (v:Venda)-[:CONTÉM]->(rv:RegistrosVenda)-[:REFERE_SE_A]->(p:Produto) " +
            filtroCypher +
            " RETURN ID(p) AS idProduto, SUM(rv.quantidade * rv.preco) AS total " +
            "ORDER BY total DESC LIMIT 3"
        );
        while (res.hasNext()) {
            Record r = res.next();
            lista.add(new String[] {
                String.valueOf(r.get("idProduto").asInt()),
                String.valueOf(r.get("total").asDouble())
            });
        }
        return lista;
    }

    public static List<String[]> consProdsMaisVendidosQtde(String filtroCypher, Session session) {
        List<String[]> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (v:Venda)-[:CONTÉM]->(rv:RegistrosVenda)-[:REFERE_SE_A]->(p:Produto) " +
            filtroCypher +
            " RETURN ID(p) AS idProduto, SUM(rv.quantidade) AS total " +
            "ORDER BY total DESC LIMIT 3"
        );
        while (res.hasNext()) {
            Record r = res.next();
            lista.add(new String[] {
                String.valueOf(r.get("idProduto").asInt()),
                String.valueOf(r.get("total").asDouble())
            });
        }
        return lista;
    }

    public static List<String[]> consFuncMaisVendas(String filtroCypher, Session session) {
        List<String[]> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (v:Venda)-[:CONTÉM]->(rv:RegistrosVenda)-[:REFERE_SE_A]->(p:Produto), " +
            "(v)-[:REALIZADA_POR]->(f:Funcionario) " +
            filtroCypher +
            " RETURN f.matricula AS matricula, SUM(rv.quantidade * rv.preco) AS total " +
            "ORDER BY total DESC LIMIT 1"
        );
        if (res.hasNext()) {
            Record r = res.next();
            lista.add(new String[] {
                String.valueOf(r.get("matricula").asInt()),
                String.valueOf(r.get("total").asDouble())
            });
        }
        return lista;
    }
}