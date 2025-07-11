import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.SessionConfig;

public class Conexao {
    private final Driver driver;
    private final String database = "casadecarnes";

    public Conexao() {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "casadecarnes";

        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void fechar() {
        driver.close();
    }

    public void testarConexao() {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            Result result = session.run("RETURN 'Conexão bem-sucedida com Neo4j!' AS mensagem");
            String mensagem = result.single().get("mensagem").asString();
            System.out.println(mensagem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return driver.session(SessionConfig.forDatabase(database));
    }
}
