import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JTextField;

public class CampoMoeda extends JTextField {
    private long valorEmCentavos = 0;
    private final NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final KeyAdapter filtro;

    public CampoMoeda() {
        super();
        setHorizontalAlignment(JTextField.LEFT);
        setEditable(true);

        filtro = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                if (Character.isDigit(c)) {
                    valorEmCentavos = valorEmCentavos * 10 + Character.getNumericValue(c);
                    atualizarTexto();
                } else if (c == KeyEvent.VK_BACK_SPACE && valorEmCentavos > 0) {
                    valorEmCentavos = valorEmCentavos / 10;
                    atualizarTexto();
                }

                e.consume();
            }
        };

        addKeyListener(filtro);
        atualizarTexto();
    }

    private void atualizarTexto() {
        setText(formato.format(valorEmCentavos / 100.0));
    }

    public double getValor() {
        return valorEmCentavos / 100.0;
    }

    public void preencherValor(double valor) {
        removeKeyListener(filtro);
        valorEmCentavos = Math.round(valor * 100);
        atualizarTexto();
        addKeyListener(filtro);
    }
}
