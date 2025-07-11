import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

public class CampoUni extends JTextField {
    private int quantidade = 0;
    private final KeyAdapter filtro;

    public CampoUni() {
        super();
        setHorizontalAlignment(JTextField.LEFT);
        setEditable(true);

        filtro = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                if (Character.isDigit(c)) {
                    quantidade = quantidade * 10 + Character.getNumericValue(c);
                    atualizarTexto();
                } else if (c == KeyEvent.VK_BACK_SPACE && quantidade > 0) {
                    quantidade = quantidade / 10;
                    atualizarTexto();
                }

                e.consume();
            }
        };

        addKeyListener(filtro);
        atualizarTexto();
    }

    private void atualizarTexto() {
        setText(quantidade + " unidade(s)");
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void preencherQuantidade(double quantidade) {
        removeKeyListener(filtro);
        this.quantidade = (int) quantidade;
        atualizarTexto();
        addKeyListener(filtro);
    }
}
