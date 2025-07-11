import java.util.ArrayList;
import java.util.List;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.Values;

public class MarcasModel {

    public static void create(MarcasBean m, Session session) {
        Result res = session.run(
            "CREATE (marca:Marca {nome: $nome, cnpj: $cnpj}) " +
            "RETURN ID(marca) AS id",
            Values.parameters(
                "nome", m.getNome(),
                "cnpj", m.getCnpj()
            )
        );
        if (res.hasNext()) {
            Record r = res.next();
            int idGerado = r.get("id").asInt();
            m.setId(idGerado);
        }
    }

    public static List<MarcasBean> listAll(Session session) {
        List<MarcasBean> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (m:Marca) RETURN ID(m) AS id, m.nome AS nome, m.cnpj AS cnpj"
        );
        while (res.hasNext()) {
            Record r = res.next();
            int id = r.get("id").asInt();
            String nome = r.get("nome").asString();
            String cnpj = r.get("cnpj").asString();
            lista.add(new MarcasBean(id, nome, cnpj));
        }
        return lista;
    }

    public static MarcasBean selectById(int id, Session session) {
        Result res = session.run(
            "MATCH (m:Marca) WHERE ID(m) = $id RETURN ID(m) AS id, m.nome AS nome, m.cnpj AS cnpj",
            Values.parameters("id", id)
        );
        if (res.hasNext()) {
            Record r = res.next();
            return new MarcasBean(
                r.get("id").asInt(),
                r.get("nome").asString(),
                r.get("cnpj").asString()
            );
        }
        return null;
    }

    public static void update(MarcasBean m, Session session) {
        session.run(
            "MATCH (m:Marca) WHERE ID(m) = $id " +
            "SET m.nome = $nome, m.cnpj = $cnpj",
            Values.parameters(
                "id", m.getId(),
                "nome", m.getNome(),
                "cnpj", m.getCnpj()
            )
        );
    }
}