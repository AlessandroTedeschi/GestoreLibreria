package ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Consumer;

public class BarraSuperiore extends JPanel {
    private final JTextField searchField = new JTextField(30);
    private final JComboBox<OpzioniOrdinamento> sortCombo = new JComboBox<>(OpzioniOrdinamento.values());
    private final JButton addButton = new JButton("Aggiungi");

    private Consumer<String> onSearchChanged = s -> {};
    private Consumer<OpzioniOrdinamento> onSortChanged = s -> {};
    private Runnable onAdd = () -> {};

    public BarraSuperiore() {
        super(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,6,4,6);
        gc.gridy = 0;

        gc.gridx = 0; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        add(new JLabel("Cerca (titolo/autore):"), gc);

        gc.gridx = 1; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        add(searchField, gc);

        gc.gridx = 2; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        add(new JLabel("Ordina per:"), gc);

        gc.gridx = 3;
        add(sortCombo, gc);

        gc.gridx = 4;
        add(addButton, gc);

        DocumentListener dl = new DocumentListener() {
            private void fire(){ onSearchChanged.accept(searchField.getText()); }
            public void insertUpdate(DocumentEvent e){ fire(); }
            public void removeUpdate(DocumentEvent e){ fire(); }
            public void changedUpdate(DocumentEvent e){ fire(); }
        };
        searchField.getDocument().addDocumentListener(dl);
        sortCombo.addActionListener(e -> onSortChanged.accept((OpzioniOrdinamento) sortCombo.getSelectedItem()));
        addButton.addActionListener(e -> onAdd.run());
    }

    public void setOnSearchChanged(Consumer<String> c){ onSearchChanged = c != null ? c : s -> {}; }
    public void setOnSortChanged(Consumer<OpzioniOrdinamento> c){ onSortChanged = c != null ? c : s -> {}; }
    public void setOnAdd(Runnable r){ onAdd = r != null ? r : () -> {}; }
    public void setSearchText(String text) { searchField.setText(text != null ? text : ""); }
}


