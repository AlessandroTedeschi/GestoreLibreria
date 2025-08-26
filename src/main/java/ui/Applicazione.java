package ui;

import libreria.Libreria;
import persistenza.LibroDTO;
import persistenza.PersistenzaLibreria;


import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Applicazione {

    // unico file di persistenza dell’app
    private static final Path FILE_PATH = Path.of("libreria.json");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignore) {}

            // --- CARICAMENTO all’avvio ---
            Libreria tmp;
            try {
                PersistenzaLibreria pers = PersistenzaLibreria.getInstance(FILE_PATH);
                tmp = pers.load();                         // load restituisce una Libreria
                if (tmp == null) tmp = new Libreria();
            } catch (Exception ex) {
                System.err.println("Caricamento persistenza fallito: " + ex.getMessage());
                tmp = new Libreria();
            }
            final Libreria model = tmp; // <- variabile effettivamente finale

            // --- UI + MEDIATOR ---
            MainFrame frame = new MainFrame();
            new LibreriaMediator(model, frame);
            frame.setVisible(true);

            // --- SALVATAGGIO alla chiusura ---
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        PersistenzaLibreria pers = PersistenzaLibreria.getInstance(FILE_PATH);
                        pers.save(model);                  // può lanciare IOException
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });
    }
}


