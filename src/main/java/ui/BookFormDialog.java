package ui;

import libreria.Genere;
import libreria.Libro;
import libreria.StatoLettura;

import javax.swing.*;
import java.awt.*;

public class BookFormDialog extends JDialog {
    private boolean confirmed = false;
    private Libro result;

    private final JTextField titolo = new JTextField();
    private final JTextField autore = new JTextField();
    private final JTextField isbn = new JTextField();
    private final JComboBox<Genere> genere = new JComboBox<>(Genere.values());
    private final JComboBox<StatoLettura> stato = new JComboBox<>(StatoLettura.values());
    private final JSpinner valutazione = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));

    private BookFormDialog(Window owner, String title, boolean editMode) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("Titolo:"));           form.add(titolo);
        form.add(new JLabel("Autore:"));           form.add(autore);
        form.add(new JLabel("ISBN:"));             form.add(isbn);
        form.add(new JLabel("Genere:"));           form.add(genere);
        form.add(new JLabel("Stato lettura:"));    form.add(stato);
        form.add(new JLabel("Valutazione (0-5):"));form.add(valutazione);
        if (editMode) isbn.setEditable(false);
        add(form, BorderLayout.CENTER);

        stato.addActionListener(e -> {
            var s = (libreria.StatoLettura) stato.getSelectedItem();
            if (s != null && s != libreria.StatoLettura.LETTO) {
                valutazione.setValue(0);
            } else if (s == libreria.StatoLettura.LETTO && ((Integer)valutazione.getValue()) == 0) {
                valutazione.setValue(3);
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Annulla");
        buttons.add(ok); buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(ok);
        ok.addActionListener(e -> onOk());
        cancel.addActionListener(e -> setVisible(false));

        pack();
        setLocationRelativeTo(owner);
    }

    private void onOk() {
        String t = titolo.getText().trim();
        String a = autore.getText().trim();
        String i = isbn.getText().trim();
        Genere g = (Genere) genere.getSelectedItem();
        StatoLettura s = (StatoLettura) stato.getSelectedItem();
        int v = (Integer) valutazione.getValue();
        if (s == libreria.StatoLettura.LETTO && v == 0) {
            JOptionPane.showMessageDialog(this,
                    "Se il libro è segnato come LETTO, la valutazione deve essere tra 1 e 5.",
                    "Valutazione mancante", JOptionPane.WARNING_MESSAGE);
            return;
        }


        if (t.isEmpty() || a.isEmpty() || i.isEmpty() || g == null || s == null) {
            JOptionPane.showMessageDialog(this, "Titolo, Autore, ISBN, Genere e Stato sono obbligatori.",
                    "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            result = new Libro.Builder(t, a, i, g)
                    .statoLettura(s)
                    .valutazione(v)
                    .build();
            confirmed = true;
            setVisible(false);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() != null ? ex.getMessage() : "Dati non validi.",
                    "Errore creazione libro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Result showNew(Component owner) {
        BookFormDialog dlg = new BookFormDialog(SwingUtilities.getWindowAncestor(owner), "Nuovo libro", false);
        dlg.setVisible(true);
        return new Result(dlg.confirmed, dlg.result);
    }

    public static Result showEdit(Component owner, Libro existing) {
        BookFormDialog dlg = new BookFormDialog(SwingUtilities.getWindowAncestor(owner), "Modifica libro", true);
        dlg.titolo.setText(existing.getTitolo());
        dlg.autore.setText(existing.getAutore());
        dlg.isbn.setText(existing.getISBN());
        dlg.genere.setSelectedItem(existing.getGenere());
        dlg.stato.setSelectedItem(existing.getStatoLettura());
        dlg.valutazione.setValue(existing.getValutazione() > 0 ? existing.getValutazione() : 3);
        dlg.setVisible(true);
        return new Result(dlg.confirmed, dlg.result);
    }

    public record Result(boolean confirmed, Libro libro) {}
}

