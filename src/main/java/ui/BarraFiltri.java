package ui;

import libreria.Genere;
import libreria.StatoLettura;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

//pannello dei filtri Valutazione, StatoLettura e Genere
public class BarraFiltri extends JPanel {

    //"menu" a tendina con le diverse opzioni e bottone per pulire i filtri
    private final JComboBox<StatoLettura> statoCombo = new JComboBox<>();
    private final JComboBox<Genere> genereCombo = new JComboBox<>();
    private final JComboBox<Integer> valutazioneCombo = new JComboBox<>();
    private final JButton clearBtn = new JButton("Pulisci filtri");


    private Consumer<StatoLettura> onStatoChanged = s -> {};
    private Consumer<Genere> onGenereChanged = g -> {};
    private Consumer<Integer> onValutazioneChanged = v -> {};
    private Runnable onClear = () -> {};

    public BarraFiltri() {
        super(new FlowLayout(FlowLayout.CENTER, 12, 6));

        //font e accorgimenti grafici per etichette, dati combo e bottone
        Font labelFont = getFont().deriveFont(Font.PLAIN, 12f);
        Font comboFont = getFont().deriveFont(Font.PLAIN,12f);
        Dimension comboLg = new Dimension(100,25);
        Dimension comboSm = new Dimension(70,25);
        Dimension btnSize = new Dimension(82,25);

        JLabel lblStato = new JLabel("Stato:");
        lblStato.setFont(labelFont);

        JLabel lblGenere = new JLabel("Genere:");
        lblGenere.setFont(labelFont);

        JLabel lblValutazione = new JLabel("Valutazione:");
        lblValutazione.setFont(labelFont);

        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 2, 0));

        //null per rappresentare il filtro "Tutti"
        statoCombo.addItem(null);
        for (StatoLettura s : StatoLettura.values()) statoCombo.addItem(s);
        genereCombo.addItem(null);
        for (Genere g : Genere.values()) genereCombo.addItem(g);
        valutazioneCombo.addItem(null);
        for (int i = 1; i <= 5; i++) valutazioneCombo.addItem(i);


        statoCombo.setFont(comboFont);
        genereCombo.setFont(comboFont);
        valutazioneCombo.setFont(comboFont);
        clearBtn.setFont(comboFont);
        statoCombo.setPreferredSize(comboLg);
        statoCombo.setMaximumSize(comboLg);
        genereCombo.setPreferredSize(comboLg);
        genereCombo.setMaximumSize(comboLg);
        valutazioneCombo.setPreferredSize(comboSm);
        valutazioneCombo.setMaximumSize(comboSm);
        clearBtn.setPreferredSize(btnSize);
        clearBtn.setMaximumSize(btnSize);

        //filtro null rinominato "Tutti"
        statoCombo.setRenderer(tuttiRenderer("Tutti", comboFont));
        genereCombo.setRenderer(tuttiRenderer("Tutti", comboFont));
        valutazioneCombo.setRenderer(tuttiRenderer("Tutti", comboFont));


        add(lblStato);
        add(statoCombo);
        add(lblGenere);
        add(genereCombo);
        add(lblValutazione);
        add(valutazioneCombo);
        add(clearBtn);

        //collegamento di ogni bottone al corrispondente evento al momento della pressione del bottone
        statoCombo.addActionListener(e -> onStatoChanged.accept((StatoLettura) statoCombo.getSelectedItem()));
        genereCombo.addActionListener(e -> onGenereChanged.accept((Genere) genereCombo.getSelectedItem()));
        valutazioneCombo.addActionListener(e -> onValutazioneChanged.accept((Integer) valutazioneCombo.getSelectedItem()));
        clearBtn.addActionListener(e -> { reset(); onClear.run(); });
    }

    //metodo che serve per scrivere all'interno della combo le diverse etichette (i valori)
    private static DefaultListCellRenderer tuttiRenderer(String label, Font font) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(
                        list, value == null ? label : value, index, isSelected, cellHasFocus);
                c.setFont(font);
                return c;
            }
        };
    }

    //resetta tutti i filtri portandoli allo stato "Tutti"
    public void reset() {
        statoCombo.setSelectedItem(null);
        genereCombo.setSelectedItem(null);
        valutazioneCombo.setSelectedItem(null);
    }

    //collegamento delle azioni da eseguire alla pressione del bottone o alla scelta di un filtro, eseguite da mediator
    public void setOnStatoChanged(Consumer<StatoLettura> c) { onStatoChanged = c != null ? c : s -> {}; }
    public void setOnGenereChanged(Consumer<Genere> c) { onGenereChanged = c != null ? c : g -> {}; }
    public void setOnValutazioneChanged(Consumer<Integer> c) { onValutazioneChanged = c != null ? c : v -> {}; }
    public void setOnClear(Runnable r) { onClear = r != null ? r : () -> {}; }
}

