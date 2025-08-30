package ui;

import libreria.Libro;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

//pannello che mostra la tabella con i libri salvati
public class ListaLibri extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;
    private Consumer<String> onSelectionChangedIsbn = s -> {};

    public ListaLibri() {

        super(new BorderLayout());

        //creazione colonne con "nome", accorgimenti grafici e font della tabella
        model = new DefaultTableModel(
                new Object[]{"Titolo", "Autore", "ISBN", "Genere", "Stato Lettura", "Valutazione"}, 0
        ) {
            //tabella in modalità SOLO LETTURA (NON EDITABILE)
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        Font cellFont   = table.getFont().deriveFont(13f);
        Font headerFont = table.getTableHeader().getFont().deriveFont(Font.BOLD, 13f);
        table.setFont(cellFont);
        table.getTableHeader().setFont(headerFont);
        table.setRowHeight(22);
        table.setIntercellSpacing(new Dimension(0, 4));
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            { setOpaque(true); setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6)); }
            @Override public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                java.awt.Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                comp.setFont(cellFont);
                return comp;
            }
        });

        //la selezione avviene UNA RIGA ALLA VOLTA
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //ogni volta che viene cambiata la riga selezionata viene chiamato un listener per ricavare l'ISBN del libro selezionato
        table.getSelectionModel().addListSelectionListener(selectionListener());

        //scrollbar
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    //listener attivato quando viene selezionata una riga --> restituisce isbn libro selezionato
    private ListSelectionListener selectionListener() {
        return e -> { if (!e.getValueIsAdjusting()) onSelectionChangedIsbn.accept(getSelectedIsbn()); };
    }

    //metodo che crea una riga della tabella per ogni libro. Viene chiamato quando deve essere aggiornata la vista
    //ad es.: cambiamento filtro, ordinamento diverso, ecc..
    public void setBooks(List<Libro> books) {
        model.setRowCount(0);
        for (Libro b : books) {
            model.addRow(new Object[]{
                    b.getTitolo(), b.getAutore(), b.getISBN(),
                    b.getGenere(), b.getStatoLettura(), b.getValutazione()
            });
        }
    }

    //restituisce il codice isbn del libro selezionato attualmente
    public String getSelectedIsbn() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return (String) model.getValueAt(row, 2);
    }

    public void setOnSelectionChanged(Consumer<String> listener) {
        this.onSelectionChangedIsbn = (listener != null) ? listener : s -> {};
    }
}
