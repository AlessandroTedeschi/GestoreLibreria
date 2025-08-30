package ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Consumer;

//pannello dell'azione ricerca, dell'ordinamento e del bottone aggiungi
public class BarraSuperiore extends JPanel {

    //barra di ricerca, bottone Aggiungi e combo per scegliere ordinamento
    private final JTextField searchField = new JTextField();
    private final JComboBox<OpzioniOrdinamento> sortCombo = new JComboBox<>();
    private final JButton addButton = new JButton("Aggiungi");

    private Consumer<String> onSearchChanged = s -> {};
    private Consumer<OpzioniOrdinamento> onSortChanged = s -> {};
    private Runnable onAdd = () -> {};

    public BarraSuperiore() {
        super(new GridBagLayout());

        //font e accorgimenti grafici per etichette, dati combo, bottone e barra di ricerca
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.gridy = 0;

        setBorder(BorderFactory.createEmptyBorder(8, 8, 12, 8));

        Font baseLabel = UIManager.getFont("Label.font");
        if (baseLabel == null) baseLabel = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        Font labelBig  = baseLabel.deriveFont(14f);

        Font baseCtrl  = UIManager.getFont("TextField.font");
        if (baseCtrl == null) baseCtrl = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        Font ctrlSm    = baseCtrl.deriveFont(12f);

        JLabel lblSearch = new JLabel("Cerca (titolo/autore):");
        lblSearch.setFont(labelBig);

        JLabel lblSort = new JLabel("Ordina per:");
        lblSort.setFont(labelBig);

        searchField.setPreferredSize(new Dimension(200, 22));
        searchField.setMargin(new Insets(2, 6, 2, 6));

        sortCombo.setFont(ctrlSm);
        sortCombo.setPreferredSize(new Dimension(150, 22));

        addButton.setFont(ctrlSm);
        addButton.setMargin(new Insets(2, 10, 2, 10));
        addButton.setFocusPainted(false);

        gc.gridx = 0; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        add(lblSearch, gc);
        gc.gridx = 1; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        add(searchField, gc);
        gc.gridx = 2; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        add(lblSort, gc);
        gc.gridx = 3; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        add(sortCombo, gc);
        gc.gridx = 4; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        add(addButton, gc);

        for (OpzioniOrdinamento o : OpzioniOrdinamento.values()) sortCombo.addItem(o);

        //ogni modifica al testo nella barra di ricerca invoca il metodo onSearchCanghed per aggiornare la lista di libri in base al testo
        DocumentListener dl = new DocumentListener() {
            private void fire(){ onSearchChanged.accept(searchField.getText()); }
            //chiamato quando viene inserito del testo
            public void insertUpdate(DocumentEvent e){ fire(); }
            //chiamato quando viene rimosso del testo
            public void removeUpdate(DocumentEvent e){ fire(); }
            //chiamato quando viene modificato il testo
            public void changedUpdate(DocumentEvent e){ fire(); }
        };


        searchField.getDocument().addDocumentListener(dl);
        sortCombo.addActionListener(e -> onSortChanged.accept((OpzioniOrdinamento) sortCombo.getSelectedItem()));
        addButton.addActionListener(e -> onAdd.run());
    }

    //collegamento delle azioni da eseguire alla pressione del bottone, alla scelta di un ordinamento o all'aggiunta di testo nella barra di ricerca, eseguite da mediator
    public void setOnSearchChanged(Consumer<String> c){ onSearchChanged = (c != null) ? c : s -> {}; }
    public void setOnSortChanged(Consumer<OpzioniOrdinamento> c){ onSortChanged = (c != null) ? c : s -> {}; }
    public void setOnAdd(Runnable r){ onAdd = (r != null) ? r : () -> {}; }
    public void setSearchText(String text) { searchField.setText(text != null ? text : ""); }
}






