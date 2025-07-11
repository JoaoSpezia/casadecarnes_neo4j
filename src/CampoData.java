import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CampoData {
    public static long converterDataParaTimestamp(String data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // garante coerência com Neo4j
            Date date = sdf.parse(data);
            return date.getTime(); // retorna em milissegundos
        } catch (Exception e) {
            System.out.println("Erro ao converter data: " + e.getMessage());
            return 0;
        }
    }
}