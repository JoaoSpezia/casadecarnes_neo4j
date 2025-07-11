import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import javax.swing.JTextField;

public class CampoKg extends JTextField {
    private double valor = 0.0;
    private final DecimalFormat formato = new DecimalFormat("###,##0.000");
    private final StringBuilder digitos = new StringBuilder();
    private final KeyAdapter filtro;

    public CampoKg() {
        super();
        setHorizontalAlignment(JTextField.LEFT);
        setEditable(true);

        filtro = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c)) {
                    digitos.append(c);
                    atualizarValor();
                } else if (c == KeyEvent.VK_BACK_SPACE && digitos.length() > 0) {
                    digitos.deleteCharAt(digitos.length() - 1);
                    atualizarValor();
                }
                e.consume();
            }
        };

        addKeyListener(filtro);
        atualizarTexto();
    }

    private void atualizarValor() {
        if (digitos.length() == 0) {
            valor = 0.0;
        } else {
            valor = Double.parseDouble(digitos.toString()) / 1000.0;
        }
        atualizarTexto();
    }

    private void atualizarTexto() {
        setText(formato.format(valor) + " kg");
    }

    public double getValor() {
        return valor;
    }

    public void preencherValor(double valor) {
        removeKeyListener(filtro);
        this.valor = valor;
        digitos.setLength(0);
        String semPonto = String.valueOf((int) Math.round(valor * 1000));
        digitos.append(semPonto);
        atualizarTexto();
        addKeyListener(filtro);
    }
}
