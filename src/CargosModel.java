import java.util.ArrayList;
import java.util.List;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.Values;

public class CargosModel {
    
    public static void create(CargosBean c, Session session) {
        Result res = session.run(
            "CREATE (cargo:Cargo {descricao: $descricao, salario: $salario}) " +
            "RETURN ID(cargo) AS id",
            Values.parameters(
                "descricao", c.getDescricao(),
                "salario", c.getSalario()
            )
        );
        if (res.hasNext()) {
            Record r = res.next();
            int idGerado = r.get("id").asInt();
            c.setId(idGerado);
        }
    }

    public static List<CargosBean> listAll(Session session) {
        List<CargosBean> lista = new ArrayList<>();
        Result res = session.run("MATCH (c:Cargo) RETURN ID(c) AS id, c.descricao AS descricao, c.salario AS salario");
        while (res.hasNext()) {
            Record r = res.next();
            int id = r.get("id").asInt();
            String descricao = r.get("descricao").asString();
            double salario = r.get("salario").asDouble();
            lista.add(new CargosBean(id, descricao, salario));
        }
        return lista;
    }
    
    public static void update(CargosBean c, Session session) {
        session.run(
            "MATCH (c:Cargo) WHERE ID(c) = $id SET c.descricao = $descricao, c.salario = $salario",
            org.neo4j.driver.Values.parameters(
                "id", c.getId(),
                "descricao", c.getDescricao(),
                "salario", c.getSalario()
            )
        );
    }

}