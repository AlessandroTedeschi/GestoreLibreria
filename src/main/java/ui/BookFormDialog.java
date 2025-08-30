package ui;

import libreria.Genere;
import libreria.Libro;
import libreria.StatoLettura;

import javax.swing.*;
import java.awt.*;

//finestra di dialogo che si apre quando si aggiunge/modifica un libro
public class BookFormDialog extends JDialog {

    private boolean confirmed = false;
    private Libro result;

    //tutti i campi del libro da compilare/modificare
    private final JTextField titolo = new JTextField();
    private final JTextField autore = new JTextField();
    private final JTextField isbn = new JTextField();
    private final JComboBox<Genere> genere = new JComboBox<>(Genere.values());
    private final JComboBox<StatoLettura> stato = new JComboBox<>(StatoLettura.values());
    private final JSpinner valutazione = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));

    private BookFormDialog(Window owner, String title, boolean editMode) {

        //blocca la finestra principale finchè la finestra di dialogo non è aperta
        super(owner, title, ModalityType.APPLICATION_MODAL);

        //codice che riguarda il font e gli accorgimenti grafici della finestra di dialogo
        setLayout(new BorderLayout(8,8));
        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("Titolo:"));           form.add(titolo);
        form.add(new JLabel("Autore:"));           form.add(autore);
        form.add(new JLabel("ISBN:"));             form.add(isbn);
        form.add(new JLabel("Genere:"));           form.add(genere);
        form.add(new JLabel("Stato lettura:"));    form.add(stato);
        form.add(new JLabel("Valutazione (0-5):"));form.add(valutazione);
        add(form, BorderLayout.CENTER);

        //bottoni ok e annulla per salvare o annullare l'azione di modifica/aggiunta
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Annulla");
        buttons.add(ok); buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);

        //dimensioni finestra di dialogo
        pack();
        setSize(getWidth() + 80, getHeight() + 40);
        setLocationRelativeTo(owner);

        if (editMode) isbn.setEditable(false); //quando un libro viene aggiunto allora isbn modificabile, quando un libro viene modificato isbn immodificabile

        //listener che ascolta la modifica del campo stato e modifica automaticamente il valore di valutazione
        stato.addActionListener(e -> {
            var s = (libreria.StatoLettura) stato.getSelectedItem();
            if (s != null && s != libreria.StatoLettura.LETTO) {
                valutazione.setValue(0);    //stato lettura=DA LEGGERE/IN LETTURA --> valutazione=0
            } else if (s == libreria.StatoLettura.LETTO && ((Integer)valutazione.getValue()) == 0) {
                valutazione.setValue(3);    //stato lettura=LETTO --> valutazione=3 (valore di default)
            }
        });

        //settiamo come bottone di default (cioè quello che viene attivato automaticamente quando l'utente preme invio) il bottone OK
        getRootPane().setDefaultButton(ok);
        ok.addActionListener(e -> onOk());
        cancel.addActionListener(e -> setVisible(false)); //chiude la finestra di dialogo alla pressione del bottone annulla

    }

    //metodo che viene chiamato alla pressione del bottone OK: analizza i campi del libro e se validi costruisce il libro
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
            setVisible(false); //se il libro viene costruito correttamente la finestra di dialogo deve chiudersi
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() != null ? ex.getMessage() : "Dati non validi.",
                    "Errore creazione libro", JOptionPane.ERROR_MESSAGE);
        }
    }

    //finestra di dialogo che viene mostrata quando si preme il tasto Aggiungi
    public static Result showNew(Component owner) {
        BookFormDialog dlg = new BookFormDialog(SwingUtilities.getWindowAncestor(owner), "Nuovo libro", false); //false --> isbn compilabile
        dlg.setVisible(true);
        return new Result(dlg.confirmed, dlg.result);
    }

    //finestra di dialogo che viene mostrata quando si preme il tasto Modifica
    public static Result showEdit(Component owner, Libro existing) {
        BookFormDialog dlg = new BookFormDialog(SwingUtilities.getWindowAncestor(owner), "Modifica libro", true); //true --> isbn non compilabile
        dlg.titolo.setText(existing.getTitolo());
        dlg.autore.setText(existing.getAutore());
        dlg.isbn.setText(existing.getISBN());
        dlg.genere.setSelectedItem(existing.getGenere());
        dlg.stato.setSelectedItem(existing.getStatoLettura());
        dlg.valutazione.setValue(existing.getValutazione() > 0 ? existing.getValutazione() : 3);
        dlg.setVisible(true);
        return new Result(dlg.confirmed, dlg.result);
    }

    //se l'utente ha premuto il bottone OK --> operazione confermata
    //se ha premuto il bottone Annulla --> operazione annullata
    public record Result(boolean confirmed, Libro libro) {}
}

