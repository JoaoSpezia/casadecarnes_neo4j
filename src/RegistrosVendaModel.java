import java.util.ArrayList;
import java.util.List;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import org.neo4j.driver.Values;

public class RegistrosVendaModel {

    public static void create(RegistrosVendaBean rv, Session session) {
        Result res = session.run(
            "MATCH (v:Venda), (p:Produto) " +
            "WHERE ID(v) = $idVenda AND ID(p) = $idProduto " +
            "CREATE (rv:RegistrosVenda {quantidade: $quantidade, preco: $preco}) " +
            "CREATE (v)-[:CONTÉM]->(rv) " +
            "CREATE (rv)-[:REFERE_SE_A]->(p) " +
            "RETURN ID(rv) AS id",
            Values.parameters(
                "idVenda", rv.getIdVenda(),
                "idProduto", rv.getIdProduto(),
                "quantidade", rv.getQuantidade(),
                "preco", rv.getPreco()
            )
        );
        if (res.hasNext()) {
            int idGerado = res.next().get("id").asInt();
            rv.setId(idGerado);
        }
    }

    public static List<RegistrosVendaBean> listAll(Session session) {
        List<RegistrosVendaBean> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (v:Venda)-[:CONTÉM]->(rv:RegistrosVenda)-[:REFERE_SE_A]->(p:Produto) " +
            "RETURN ID(rv) AS id, ID(v) AS idVenda, ID(p) AS idProduto, rv.quantidade AS quantidade, rv.preco AS preco"
        );
        while (res.hasNext()) {
            Record r = res.next();
            lista.add(new RegistrosVendaBean(
                r.get("id").asInt(),
                r.get("idVenda").asInt(),
                r.get("idProduto").asInt(),
                r.get("quantidade").asDouble(),
                r.get("preco").asDouble()
            ));
        }
        return lista;
    }

    public static List<RegistrosVendaBean> selectByVenda(VendasBean v, Session session) {
        List<RegistrosVendaBean> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (v:Venda)-[:CONTÉM]->(rv:RegistrosVenda)-[:REFERE_SE_A]->(p:Produto) " +
            "WHERE ID(v) = $idVenda " +
            "RETURN ID(rv) AS id, ID(v) AS idVenda, ID(p) AS idProduto, rv.quantidade AS quantidade, rv.preco AS preco",
            Values.parameters("idVenda", v.getId())
        );
        while (res.hasNext()) {
            Record r = res.next();
            lista.add(new RegistrosVendaBean(
                r.get("id").asInt(),
                r.get("idVenda").asInt(),
                r.get("idProduto").asInt(),
                r.get("quantidade").asDouble(),
                r.get("preco").asDouble()
            ));
        }
        return lista;
    }
}