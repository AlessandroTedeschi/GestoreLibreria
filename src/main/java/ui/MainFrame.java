package ui;

import javax.swing.*;
import java.awt.*;

//FINESTRA GRAFICA PRINCIPALE
public class MainFrame extends JFrame {
    private final BarraSuperiore topBar = new BarraSuperiore();
    private final ListaLibri listPanel = new ListaLibri();
    private final BarraDeiBottoni bottomBar = new BarraDeiBottoni();

    public MainFrame() {
        super("Libreria personale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 640);
        setLocationRelativeTo(null);

        add(topBar, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    public BarraSuperiore getBarraSuperiore(){ return topBar; }
    public ListaLibri getListaLibri(){ return listPanel; }
    public BarraDeiBottoni getBarraDeiBottoni(){ return bottomBar; }
}

