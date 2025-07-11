import java.util.ArrayList;
import java.util.List;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import org.neo4j.driver.Values;

public class VendasModel {

    public static void create(VendasBean v, Session session) {
        long timestamp = v.getDataHora()
                          .atZone(java.time.ZoneId.systemDefault())
                          .toInstant()
                          .toEpochMilli();
        
        Result res = session.run(
            "CREATE (v:Venda {data_horario: $data_horario}) RETURN ID(v) AS nodeId",
            Values.parameters("data_horario", timestamp)
        );

        if (res.hasNext()) {
            int nodeId = res.next().get("nodeId").asInt();
            v.setId(nodeId);
            session.run(
                "MATCH (v:Venda) WHERE ID(v) = $nodeId SET v.id = $id",
                Values.parameters("nodeId", nodeId, "id", nodeId)
            );
            if (v.getMatricula() != null) {
                session.run(
                    "MATCH (v:Venda), (f:Funcionario {matricula: $matricula}) " +
                    "WHERE ID(v) = $id " +
                    "CREATE (v)-[:REALIZADA_POR]->(f)",
                    Values.parameters("id", nodeId, "matricula", v.getMatricula())
                );
            }
        }
    }

    public static List<VendasBean> listAll(Session session) {
        List<VendasBean> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (v:Venda) " +
            "OPTIONAL MATCH (v)-[:REALIZADA_POR]->(f:Funcionario) " +
            "RETURN ID(v) AS id, v.data_horario AS data_horario, f.matricula AS matricula"
        );

        while (res.hasNext()) {
            Record r = res.next();
            java.time.LocalDateTime dataVenda = null;
            if (!r.get("data_horario").isNull()) {
                long timestamp = r.get("data_horario").asLong();
                dataVenda = java.time.Instant.ofEpochMilli(timestamp)
                                             .atZone(java.time.ZoneId.systemDefault())
                                             .toLocalDateTime();
            }

            Integer matricula = null;
            if (!r.get("matricula").isNull()) {
                matricula = r.get("matricula").asInt();
            }

            lista.add(new VendasBean(
                r.get("id").asInt(),
                dataVenda,
                matricula
            ));
        }
        return lista;
    }

    public static void update(VendasBean v, Session session) {
        session.run(
            "MATCH (v:Venda) WHERE ID(v) = $id " +
            "SET v.data_horario = $dataHora, v.matricula = $matricula",
            Values.parameters(
                "id", v.getId(),
                "dataHora", v.getDataHora().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
                "matricula", v.getMatricula()
            )
        );
    }

    public static double selectValorTotal(VendasBean v, Session session) {
        Result res = session.run(
            "MATCH (v:Venda)-[:CONTÉM]->(rv:RegistrosVenda) WHERE ID(v) = $id " +
            "RETURN SUM(rv.quantidade * rv.preco) AS total",
            Values.parameters("id", v.getId())
        );
        if (res.hasNext() && !res.peek().get("total").isNull()) {
            return res.next().get("total").asDouble();
        }
        return 0.0;
    }

    public static String selectNomeFunc(VendasBean v, Session session) {
        Result res = session.run(
            "MATCH (f:Funcionario)<-[:REALIZADA_POR]-(v:Venda) WHERE ID(v) = $id RETURN f.nome AS nome",
            Values.parameters("id", v.getId())
        );
        if (res.hasNext()) {
            return res.next().get("nome").asString();
        }
        return "";
    }

    public static void delete(VendasBean v, Session session) {
        session.run(
            "MATCH (v:Venda) WHERE ID(v) = $id DETACH DELETE v",
            Values.parameters("id", v.getId())
        );
    }
}