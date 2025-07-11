import java.util.ArrayList;
import java.util.List;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.Values;

public class FuncionariosModel {

    public static void create(FuncionariosBean f, Session session) {
        session.run(
            "CREATE (f:Funcionario {matricula: $matricula, nome: $nome, cpf: $cpf, ativo: true}) " +
            "WITH f " +
            "MATCH (c:Cargo) WHERE ID(c) = $idCargo " +
            "MERGE (f)-[:OCUPA]->(c)",
            Values.parameters(
                "matricula", f.getMatricula(),
                "nome", f.getNome(),
                "cpf", f.getCpf(),
                "idCargo", f.getIdCargo()
            )
        );
    }

    public static List<FuncionariosBean> listAllAtivos(Session session) {
        List<FuncionariosBean> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (f:Funcionario)-[:OCUPA]->(c:Cargo) " +
            "WHERE f.ativo = true " +
            "RETURN f.matricula AS matricula, f.nome AS nome, f.cpf AS cpf, ID(c) AS idCargo, f.ativo AS ativo"
        );
        while (res.hasNext()) {
            Record r = res.next();
            lista.add(new FuncionariosBean(
                r.get("matricula").asInt(),
                r.get("nome").asString(),
                r.get("cpf").asString(),
                r.get("idCargo").asInt(),
                r.get("ativo").asBoolean()
            ));
        }
        return lista;
    }

    public static List<FuncionariosBean> listAllInativos(Session session) {
        List<FuncionariosBean> lista = new ArrayList<>();
        Result res = session.run(
            "MATCH (f:Funcionario)-[:OCUPA]->(c:Cargo) " +
            "WHERE f.ativo = false " +
            "RETURN f.matricula AS matricula, f.nome AS nome, f.cpf AS cpf, ID(c) AS idCargo, f.ativo AS ativo"
        );
        while (res.hasNext()) {
            Record r = res.next();
            lista.add(new FuncionariosBean(
                r.get("matricula").asInt(),
                r.get("nome").asString(),
                r.get("cpf").asString(),
                r.get("idCargo").asInt(),
                r.get("ativo").asBoolean()
            ));
        }
        return lista;
    }

    public static FuncionariosBean selectByMat(int matricula, Session session) {
        Result res = session.run(
            "MATCH (f:Funcionario)-[:OCUPA]->(c:Cargo) " +
            "WHERE f.matricula = $matricula " +
            "RETURN f.matricula AS matricula, f.nome AS nome, f.cpf AS cpf, ID(c) AS idCargo, f.ativo AS ativo",
            Values.parameters("matricula", matricula)
        );
        if (res.hasNext()) {
            Record r = res.next();
            return new FuncionariosBean(
                r.get("matricula").asInt(),
                r.get("nome").asString(),
                r.get("cpf").asString(),
                r.get("idCargo").asInt(),
                r.get("ativo").asBoolean()
            );
        }
        return null;
    }

    public static String selectDescCargo(FuncionariosBean f, Session session) {
        Result res = session.run(
            "MATCH (f:Funcionario)-[:OCUPA]->(c:Cargo) " +
            "WHERE f.matricula = $matricula " +
            "RETURN c.descricao AS descricao",
            Values.parameters("matricula", f.getMatricula())
        );
        if (res.hasNext()) {
            return res.next().get("descricao").asString();
        }
        return "";
    }

    public static int selectUltimaMat(Session session) {
        Result res = session.run("MATCH (f:Funcionario) RETURN MAX(f.matricula) AS maxMatricula");
        if (res.hasNext()) {
            return res.next().get("maxMatricula").asInt(0);
        }
        return 0;
    }

    public static void update(FuncionariosBean f, Session session) {
        session.run(
            "MATCH (f:Funcionario)-[r:OCUPA]->(c:Cargo) " +
            "WHERE f.matricula = $matricula " +
            "SET f.nome = $nome, f.cpf = $cpf " +
            "DELETE r " +
            "WITH f " +
            "MATCH (novo:Cargo) WHERE ID(novo) = $idCargo " +
            "MERGE (f)-[:OCUPA]->(novo)",
            Values.parameters(
                "matricula", f.getMatricula(),
                "nome", f.getNome(),
                "cpf", f.getCpf(),
                "idCargo", f.getIdCargo()
            )
        );
    }

    public static void updateStatus(FuncionariosBean f, Session session) {
        session.run(
            "MATCH (f:Funcionario) WHERE f.matricula = $matricula " +
            "SET f.ativo = $ativo",
            Values.parameters(
                "matricula", f.getMatricula(),
                "ativo", f.getAtivo()
            )
        );
    }
}