package ui;

import command.GestoreComandi;
import libreria.Libreria;
import persistenza.PersistenzaLibreria;


import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;


public class Applicazione {

    //unico file di persistenza dell’app
    private static final Path FILE_PATH = Path.of("libreria.json");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignore) {}

            Libreria tmp;

            //caricamento/creazione del gestore della persistenza
            try {
                PersistenzaLibreria pers = PersistenzaLibreria.getInstance(FILE_PATH);
                tmp = pers.load();
                if (tmp == null) tmp = new Libreria();
            } catch (Exception ex) {
                System.err.println("Caricamento persistenza fallito: " + ex.getMessage());
                tmp = new Libreria();
            }
            final Libreria model = tmp;

            //invoker utilizzato per chiamare i vari Command ed eseguire azioni di aggiunta/rimozione/modifica
            GestoreComandi invoker = new GestoreComandi();

            //creazione della finestra grafica principale
            MainFrame frame = new MainFrame();
            new LibreriaMediator(model, frame, invoker); //fa da "ponte" fra i dati (la libreria model), l'interfaccia utente (frame) e il gestore dei comandi (invoker)
            frame.setVisible(true);

            //salvataggio dei dati sul file prima che l'applicazione venga chiusa (garantisce persistenza)
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        PersistenzaLibreria pers = PersistenzaLibreria.getInstance(FILE_PATH);
                        pers.save(model);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });
    }
}


