package ui;

import libreria.Libreria;

import javax.swing.*;

public class Applicazione
{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignore) {}
            Libreria model = new Libreria();            // usa il tuo costruttore reale / eventuale caricamento
            MainFrame frame = new MainFrame();
            new LibreriaMediator(model, frame);          // wiring completo
            frame.setVisible(true);
        });
    }
}
