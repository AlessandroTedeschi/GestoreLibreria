package ui;

import javax.swing.*;
import java.awt.*;

//FINESTRA GRAFICA PRINCIPALE
public class MainFrame extends JFrame {
    private final BarraSuperiore topBar = new BarraSuperiore();
    private final ListaLibri listPanel = new ListaLibri();
    private final BarraDeiBottoni bottomBar = new BarraDeiBottoni();
    private final BarraFiltri barraFiltri = new BarraFiltri();

    public MainFrame() {
        super("Libreria personale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));

        topBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraFiltri.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(topBar);
        top.add(Box.createVerticalStrut(4));
        top.add(barraFiltri);

        add(top, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

    }

    public BarraSuperiore getBarraSuperiore(){ return topBar; }
    public ListaLibri getListaLibri(){ return listPanel; }
    public BarraDeiBottoni getBarraDeiBottoni(){ return bottomBar; }
    public BarraFiltri getBarraFiltri() { return barraFiltri; }
}

