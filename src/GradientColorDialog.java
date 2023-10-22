import javax.swing.JDialog;
import javax.swing.JColorChooser;
import java.awt.Color;
import java.awt.FlowLayout;

public class GradientColorDialog extends JDialog {
    private Color startColor;
    private Color endColor;

    public GradientColorDialog() {
        setTitle("Gradient Color Selection");
        setLayout(new FlowLayout());

        startColor = JColorChooser.showDialog(this, "Select Start Color", Color.RED);
        endColor = JColorChooser.showDialog(this, "Select End Color", Color.YELLOW);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setModal(true);
    }

    public Color getStartColor() {
        return startColor;
    }

    public Color getEndColor() {
        return endColor;
    }
}
