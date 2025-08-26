package ui;

import javax.swing.*;
import java.awt.*;

public class BarraDeiBottoni extends JPanel {
    private final JButton btnModifica = new JButton("Modifica");
    private final JButton btnRimuovi  = new JButton("Rimuovi");
    private final JButton btnAnnulla  = new JButton("Annulla");

    private Runnable onModifica = () -> {};
    private Runnable onRimuovi  = () -> {};
    private Runnable onAnnulla  = () -> {};

    public BarraDeiBottoni() {
        super(new FlowLayout(FlowLayout.LEFT, 8, 6));

        // Font sicuro dal L&F (niente getFont() sul JDialog/frame)
        Font base  = UIManager.getFont("Button.font");
        if (base == null) base = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        Font btnSm = base.deriveFont(12f); // leggermente più piccolo/compatto

        // Bottoni compatti
        Insets compact = new Insets(2, 10, 2, 10);
        for (JButton b : new JButton[]{btnModifica, btnRimuovi, btnAnnulla}) {
            b.setFont(btnSm);
            b.setMargin(compact);
            b.setFocusPainted(false); // look più pulito
            add(b);
        }

        // Wiring
        btnModifica.addActionListener(e -> onModifica.run());
        btnRimuovi.addActionListener(e -> onRimuovi.run());
        btnAnnulla.addActionListener(e -> onAnnulla.run());

        setButtonsEnabled(false, false, false);
    }

    public void setButtonsEnabled(boolean modifica, boolean rimuovi, boolean annulla) {
        btnModifica.setEnabled(modifica);
        btnRimuovi.setEnabled(rimuovi);
        btnAnnulla.setEnabled(annulla);
    }

    public void setOnModifica(Runnable r){ onModifica = (r != null) ? r : () -> {}; }
    public void setOnRimuovi (Runnable r){ onRimuovi  = (r != null) ? r : () -> {}; }
    public void setOnAnnulla(Runnable r){ onAnnulla  = (r != null) ? r : () -> {}; }
}

