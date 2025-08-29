package ui;

import libreria.Genere;
import libreria.StatoLettura;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class BarraFiltri extends JPanel {

    private final JComboBox<StatoLettura> statoCombo = new JComboBox<>();
    private final JComboBox<Genere> genereCombo = new JComboBox<>();
    private final JComboBox<Integer> valutazioneMinCombo = new JComboBox<>();
    private final JButton clearBtn = new JButton("Pulisci filtri");


    private Consumer<StatoLettura> onStatoChanged = s -> {};
    private Consumer<Genere> onGenereChanged = g -> {};
    private Consumer<Integer> onValutazioneMinChanged = v -> {};
    private Runnable onClear = () -> {};

    public BarraFiltri() {
        super(new FlowLayout(FlowLayout.CENTER, 12, 6));

        Font labelFont = getFont().deriveFont(Font.PLAIN, 12f);
        Font comboFont = getFont().deriveFont(Font.PLAIN,12f);

        Dimension comboLg = new Dimension(100,25);
        Dimension comboSm = new Dimension(70,25);
        Dimension btnSize = new Dimension(90,25);

        JLabel lblStato = new JLabel("Stato:");
        lblStato.setFont(labelFont);

        JLabel lblGenere = new JLabel("Genere:");
        lblGenere.setFont(labelFont);

        JLabel lblValutazione = new JLabel("Valutazione:");
        lblValutazione.setFont(labelFont);

        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 2, 0));

        statoCombo.addItem(null);
        for (StatoLettura s : StatoLettura.values()) statoCombo.addItem(s);

        genereCombo.addItem(null);
        for (Genere g : Genere.values()) genereCombo.addItem(g);

        valutazioneMinCombo.addItem(null);
        for (int i = 1; i <= 5; i++) valutazioneMinCombo.addItem(i);

        // ---------- applica font e dimensioni ai combo/bottone ----------
        statoCombo.setFont(comboFont);
        genereCombo.setFont(comboFont);
        valutazioneMinCombo.setFont(comboFont);
        clearBtn.setFont(comboFont);

        statoCombo.setPreferredSize(comboLg);
        statoCombo.setMaximumSize(comboLg);
        genereCombo.setPreferredSize(comboLg);
        genereCombo.setMaximumSize(comboLg);
        valutazioneMinCombo.setPreferredSize(comboSm);
        valutazioneMinCombo.setMaximumSize(comboSm);
        clearBtn.setPreferredSize(btnSize);
        clearBtn.setMaximumSize(btnSize);

        // renderer che mostra "Tutti" per i null + forza il font anche nella lista a tendina
        statoCombo.setRenderer(tuttiRenderer("Tutti", comboFont));
        genereCombo.setRenderer(tuttiRenderer("Tutti", comboFont));
        valutazioneMinCombo.setRenderer(tuttiRenderer("Tutti", comboFont));

        // ---------- composizione UI (come prima, solo con etichette più grandi) ----------
        add(lblStato);
        add(statoCombo);
        add(lblGenere);
        add(genereCombo);
        add(lblValutazione);
        add(valutazioneMinCombo);
        add(clearBtn);

        // ---------- listener (uguali) ----------
        statoCombo.addActionListener(e -> onStatoChanged.accept((StatoLettura) statoCombo.getSelectedItem()));
        genereCombo.addActionListener(e -> onGenereChanged.accept((Genere) genereCombo.getSelectedItem()));
        valutazioneMinCombo.addActionListener(e -> onValutazioneMinChanged.accept((Integer) valutazioneMinCombo.getSelectedItem()));
        clearBtn.addActionListener(e -> { reset(); onClear.run(); });
    }

    // Renderer che gestisce "Tutti" e applica il font anche all'elenco
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

    public void reset() {
        statoCombo.setSelectedItem(null);
        genereCombo.setSelectedItem(null);
        valutazioneMinCombo.setSelectedItem(null);
    }

    // callback per il Mediator (immutate)
    public void setOnStatoChanged(Consumer<StatoLettura> c) { onStatoChanged = c != null ? c : s -> {}; }
    public void setOnGenereChanged(Consumer<Genere> c) { onGenereChanged = c != null ? c : g -> {}; }
    public void setOnValutazioneMinChanged(Consumer<Integer> c) { onValutazioneMinChanged = c != null ? c : v -> {}; }
    public void setOnClear(Runnable r) { onClear = r != null ? r : () -> {}; }
}

