package ui;

import javax.swing.*;
import java.awt.*;

//pannello dei bottoni modifica, annulla, rimuovi
public class BarraDeiBottoni extends JPanel {

    //i tre bottoni che deve contenere questo pannello
    private final JButton btnModifica = new JButton("Modifica");
    private final JButton btnRimuovi  = new JButton("Rimuovi");
    private final JButton btnAnnulla  = new JButton("Annulla");

    private Runnable onModifica = () -> {};
    private Runnable onRimuovi  = () -> {};
    private Runnable onAnnulla  = () -> {};

    public BarraDeiBottoni() {
        super(new FlowLayout(FlowLayout.LEFT, 8, 6));

        //codice che riguarda il font e gli accorgimenti grafici dei bottoni
        Font base  = UIManager.getFont("Button.font");
        if (base == null) base = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        Font btnSm = base.deriveFont(12f);
        Insets compact = new Insets(2, 10, 2, 10);
        for (JButton b : new JButton[]{btnModifica, btnRimuovi, btnAnnulla}) {
            b.setFont(btnSm);
            b.setMargin(compact);
            b.setFocusPainted(false);
            add(b);
        }

        //collegamento di ogni bottone al corrispondente evento al momento della pressione del bottone
        btnModifica.addActionListener(e -> onModifica.run());
        btnRimuovi.addActionListener(e -> onRimuovi.run());
        btnAnnulla.addActionListener(e -> onAnnulla.run());

        setButtonsEnabled(false, false, false);
    }

    //bottoni disabilitati all'avvio dell'applicazione
    public void setButtonsEnabled(boolean modifica, boolean rimuovi, boolean annulla) {
        btnModifica.setEnabled(modifica); //bottone abilitato solo quando si seleziona un libro
        btnRimuovi.setEnabled(rimuovi); //bottone abilitato solo quando si seleziona un libro
        btnAnnulla.setEnabled(annulla); //bottone abilitato solo quando lo stack delle azioni annullabili non è vuoto
    }

    //registra l'azione da fare quando servirà, non la esegue
    //l'azione da eseguire è esplicitata nel mediator
    public void setOnModifica(Runnable r){ onModifica = (r != null) ? r : () -> {}; }
    public void setOnRimuovi (Runnable r){ onRimuovi  = (r != null) ? r : () -> {}; }
    public void setOnAnnulla(Runnable r){ onAnnulla  = (r != null) ? r : () -> {}; }
}

