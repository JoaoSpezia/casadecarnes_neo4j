import java.util.ArrayList;
import java.util.List;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.Values;

public class CategoriasModel {

    public static void create(CategoriasBean c, Session session) {
        Result res = session.run(
            "CREATE (cat:Categoria {descricao: $descricao, uni_medida: $uni_medida}) " +
            "RETURN ID(cat) AS id",
            Values.parameters(
                "descricao", c.getDescricao(),
                "uni_medida", c.getUniMed()
            )
        );
        if (res.hasNext()) {
            Record r = res.next();
            int idGerado = r.get("id").asInt();
            c.setId(idGerado);
        }
    }

    public static List<CategoriasBean> listAll(Session session) {
        List<CategoriasBean> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (cat:Categoria) " +
            "RETURN ID(cat) AS id, cat.descricao AS descricao, cat.uni_medida AS uni_medida"
        );
        while (res.hasNext()) {
            Record r = res.next();
            int id = r.get("id").asInt();
            String descricao = r.get("descricao").asString();
            String uniMed = r.get("uni_medida").asString();
            lista.add(new CategoriasBean(id, descricao, uniMed));
        }
        return lista;
    }

    public static void update(CategoriasBean c, Session session) {
        session.run(
            "MATCH (cat:Categoria) WHERE ID(cat) = $id " +
            "SET cat.descricao = $descricao, cat.uni_medida = $uni_medida",
            Values.parameters(
                "id", c.getId(),
                "descricao", c.getDescricao(),
                "uni_medida", c.getUniMed()
            )
        );
    }

    public static CategoriasBean selectById(int id, Session session) {
        Result res = session.run(
            "MATCH (cat:Categoria) WHERE ID(cat) = $id " +
            "RETURN ID(cat) AS id, cat.descricao AS descricao, cat.uni_medida AS uni_medida",
            Values.parameters("id", id)
        );
        if (res.hasNext()) {
            Record r = res.next();
            return new CategoriasBean(
                r.get("id").asInt(),
                r.get("descricao").asString(),
                r.get("uni_medida").asString()
            );
        } else {
            return null;
        }
    }
}