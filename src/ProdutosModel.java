import java.util.ArrayList;
import java.util.List;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import org.neo4j.driver.Values;

public class ProdutosModel {

    public static void create(ProdutosBean p, Session session) {
        Result res = session.run(
            "CREATE (prod:Produto {descricao: $descricao, preco: $preco, quantidade: $quantidade}) " +
            "WITH prod " +
            "MATCH (m:Marca), (c:Categoria) " +
            "WHERE ID(m) = $idMarca AND ID(c) = $idCategoria " +
            "MERGE (prod)-[:FABRICADO_POR]->(m) " +
            "MERGE (prod)-[:PERTENCE_A]->(c) " +
            "RETURN ID(prod) AS id",
            Values.parameters(
                "descricao", p.getDescricao(),
                "preco", p.getPreco(),
                "quantidade", p.getQuantidade(),
                "idMarca", p.getIdMarca(),
                "idCategoria", p.getIdCategoria()
            )
        );
        if (res.hasNext()) {
            int idGerado = res.next().get("id").asInt();
            p.setId(idGerado);
        }
    }

    public static List<ProdutosBean> listAll(Session session) {
        List<ProdutosBean> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (prod:Produto)-[:FABRICADO_POR]->(m:Marca), (prod)-[:PERTENCE_A]->(c:Categoria) " +
            "RETURN ID(prod) AS id, prod.descricao AS descricao, prod.preco AS preco, " +
            "prod.quantidade AS quantidade, ID(m) AS idMarca, ID(c) AS idCategoria"
        );
        while (res.hasNext()) {
            Record r = res.next();
            lista.add(new ProdutosBean(
                r.get("id").asInt(),
                r.get("descricao").asString(),
                r.get("preco").asDouble(),
                r.get("quantidade").asDouble(),
                r.get("idMarca").asInt(),
                r.get("idCategoria").asInt()
            ));
        }
        return lista;
    }

    public static ProdutosBean selectById(int idProduto, Session session) {
        Result res = session.run(
            "MATCH (prod:Produto)-[:FABRICADO_POR]->(m:Marca), (prod)-[:PERTENCE_A]->(c:Categoria) " +
            "WHERE ID(prod) = $id " +
            "RETURN ID(prod) AS id, prod.descricao AS descricao, prod.preco AS preco, " +
            "prod.quantidade AS quantidade, ID(m) AS idMarca, ID(c) AS idCategoria",
            Values.parameters("id", idProduto)
        );
        if (res.hasNext()) {
            Record r = res.next();
            return new ProdutosBean(
                r.get("id").asInt(),
                r.get("descricao").asString(),
                r.get("preco").asDouble(),
                r.get("quantidade").asDouble(),
                r.get("idMarca").asInt(),
                r.get("idCategoria").asInt()
            );
        }
        return null;
    }

    public static String selectNomeMarca(ProdutosBean p, Session session) {
        Result res = session.run(
            "MATCH (prod:Produto)-[:FABRICADO_POR]->(m:Marca) " +
            "WHERE ID(prod) = $id " +
            "RETURN m.nome AS nomeMarca",
            Values.parameters("id", p.getId())
        );
        if (res.hasNext()) {
            return res.next().get("nomeMarca").asString();
        }
        return "";
    }

    public static String selectDescCategoria(ProdutosBean p, Session session) {
        Result res = session.run(
            "MATCH (prod:Produto)-[:PERTENCE_A]->(c:Categoria) " +
            "WHERE ID(prod) = $id " +
            "RETURN c.descricao AS descCategoria",
            Values.parameters("id", p.getId())
        );
        if (res.hasNext()) {
            return res.next().get("descCategoria").asString();
        }
        return "";
    }

    public static String selectUniMed(ProdutosBean p, Session session) {
        Result res = session.run(
            "MATCH (prod:Produto)-[:PERTENCE_A]->(c:Categoria) " +
            "WHERE ID(prod) = $id " +
            "RETURN c.uni_medida AS uni_medida",
            Values.parameters("id", p.getId())
        );
        if (res.hasNext()) {
            return res.next().get("uni_medida").asString();
        }
        return "";
    }

    public static void update(ProdutosBean p, Session session) {
        session.run(
            "MATCH (prod:Produto)-[rm:FABRICADO_POR]->(:Marca), (prod)-[rc:PERTENCE_A]->(:Categoria) " +
            "WHERE ID(prod) = $id " +
            "DELETE rm, rc " +
            "WITH prod " +
            "MATCH (m:Marca), (c:Categoria) " +
            "WHERE ID(m) = $idMarca AND ID(c) = $idCategoria " +
            "SET prod.descricao = $descricao, prod.preco = $preco, prod.quantidade = $quantidade " +
            "MERGE (prod)-[:FABRICADO_POR]->(m) " +
            "MERGE (prod)-[:PERTENCE_A]->(c)",
            Values.parameters(
                "id", p.getId(),
                "descricao", p.getDescricao(),
                "preco", p.getPreco(),
                "quantidade", p.getQuantidade(),
                "idMarca", p.getIdMarca(),
                "idCategoria", p.getIdCategoria()
            )
        );
    }

    public static void updateQuantidade(ProdutosBean p, Session session) {
        session.run(
            "MATCH (prod:Produto) WHERE ID(prod) = $id SET prod.quantidade = $quantidade",
            Values.parameters(
                "id", p.getId(),
                "quantidade", p.getQuantidade()
            )
        );
    }
}