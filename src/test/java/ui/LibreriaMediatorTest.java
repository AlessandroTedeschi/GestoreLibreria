package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import command.GestoreComandi;
import libreria.Libreria;
import libreria.Libro;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.util.List;

class LibreriaMediatorTest {

    private Libreria model;
    private GestoreComandi invoker;
    private MainFrame frame;
    private LibreriaMediator mediator;

    /*usiamo un mock di Libreria (i mock servono per usare un oggetto finto al posto della libreria vera in modo da evitare
    tutta la logica di gestione dei libri), un mock di GestoreComandi*/
    @BeforeEach
    void setup() throws Exception {
        model = mock(Libreria.class);
        invoker = mock(GestoreComandi.class);
        when(invoker.hasUndo()).thenReturn(false); //nessun operazione annullabile all'inizio
        SwingUtilities.invokeAndWait(() -> frame = new MainFrame());
        SwingUtilities.invokeAndWait(() -> mediator = new LibreriaMediator(model, frame, invoker)); //mediator vero e proprio collegato ai vari mock
    }

    //verifica che che lo stato dei bottoni contenuti nella BarraDeiBottoni siano tutti disabilitati quando l'applicazione viene appena avviata
    @Test
    void initialButtonsStateReflectsNoSelectionAndNoUndo() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var btns = frame.getBarraDeiBottoni();
            JButton mod = (JButton) btns.getComponent(0);
            JButton rim = (JButton) btns.getComponent(1);
            JButton ann = (JButton) btns.getComponent(2);
            assertFalse(mod.isEnabled());
            assertFalse(rim.isEnabled());
            assertFalse(ann.isEnabled());
        });
    }


    /*verifica che digitando qualcosa nel campo di ricerca il mediator chiama la libreria model per ricarica la lista
        e che selezionando una riga della tabella (cioè un libro) si abilitano i bottoni Modifica e rimuovi*/
    @Test
    void ricercaRicaricaListaESelezioneRigaAbilitaIBottoni() throws Exception {
        //reload iniziale
        when(model.getLibri()).thenReturn(List.of());

        Libro a = mock(Libro.class);
        when(a.getTitolo()).thenReturn("Divina Commedia");
        when(a.getAutore()).thenReturn("Dante Alighieri");
        when(a.getISBN()).thenReturn("123");
        when(a.getValutazione()).thenReturn(5);
        Libro b = mock(Libro.class);
        when(b.getTitolo()).thenReturn("Uno, Nessuno e Centomila");
        when(b.getAutore()).thenReturn("Pirandello");
        when(b.getISBN()).thenReturn("456");
        when(b.getValutazione()).thenReturn(4);

        //scrittura nella barra di ricerca e ricarica tabella
        when(model.cercaLibro(anyString())).thenReturn(List.of(a, b));
        SwingUtilities.invokeAndWait(() -> {
            frame.getBarraSuperiore().setSearchText("space");
        });
        SwingUtilities.invokeAndWait(() -> {});
        verify(model, atLeastOnce()).cercaLibro(anyString());

        //seleziono una riga sulla tabella dei libri
        SwingUtilities.invokeAndWait(() -> {
            JScrollPane sp = (JScrollPane) frame.getListaLibri().getComponent(0);
            JTable table = (JTable) sp.getViewport().getView();
            table.setRowSelectionInterval(1, 1);
        });
        SwingUtilities.invokeAndWait(() -> {});
        //bottoni Modifica e Rimuovi devono essere abilitati
        SwingUtilities.invokeAndWait(() -> {
            var btns = frame.getBarraDeiBottoni();
            JButton mod = (JButton) btns.getComponent(0);
            JButton rim = (JButton) btns.getComponent(1);
            assertTrue(mod.isEnabled());
            assertTrue(rim.isEnabled());
        });
    }


    /*verifica che il bottone Annulla viene abilitato quando c'è effettivamente qualche comando che è possibile annullare e che questa azione
        venga notificata correttamente dall'invoker*/
    @Test
    void bottoneAnnullaAbilitatoSeEsisteOperazioneAnnullabile() throws Exception {
        when(invoker.hasUndo()).thenReturn(true);
        when(model.cercaLibro(anyString())).thenReturn(List.of());
        SwingUtilities.invokeAndWait(() -> frame.getBarraSuperiore().setSearchText("x"));
        SwingUtilities.invokeAndWait(() -> {});
        SwingUtilities.invokeAndWait(() -> {
            JButton ann = (JButton) frame.getBarraDeiBottoni().getComponent(2);
            assertTrue(ann.isEnabled());
        });
    }
}
