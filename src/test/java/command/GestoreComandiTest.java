package command;

import libreria.Genere;
import libreria.Libreria;
import libreria.Libro;
import libreria.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GestoreComandiTest
{
    private Libreria libreria;
    private GestoreComandi gestore;
    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();
        gestore = new GestoreComandi();
        libro1 = new Libro.Builder("La Coscienza di Zeno", "Italo Svevo", "123", Genere.ROMANZO).build();
        libro2 = new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build();
    }

    @Test
    @DisplayName("Esegue un comando annullabile, lo traccia e annullaUltimoComando() ripristina lo stato")
    void eseguiTracciaEUndoRipristina() {
        assertEquals(0, libreria.numeroLibri());
        AggiungiLibroCommand addCmd = new AggiungiLibroCommand(libro1, libreria);   //command annulabile
        gestore.esegui(addCmd);
        assertEquals(1, libreria.numeroLibri(), "Dopo execute() deve esserci 1 libro");
        assertTrue(gestore.hasUndo(), "Il gestore deve avere qualcosa da annullare");
        gestore.annullaUltimoComando();
        assertEquals(0, libreria.numeroLibri(), "Dopo annullaUltimoComando() il libro deve sparire");
        assertFalse(gestore.hasUndo(), "Dopo annullaUltimoComando() la cronologia deve risultare vuota");
    }





    @Test
    @DisplayName("annullaUltimoComando() a cronologia vuota è un'operazione nulla (non lancia, non cambia lo stato)")
    void undoSenzaStoriaNoOp() {
        libreria.aggiungiLibro(libro1);
        int prima = libreria.numeroLibri();
        assertDoesNotThrow(gestore::annullaUltimoComando, "Chiamare annullaUltimoComando() senza storia non deve lanciare");
        assertEquals(prima, libreria.numeroLibri(), "Stato inalterato");
        assertFalse(gestore.hasUndo());
    }

    @Test
    @DisplayName("clearStory() svuota la cronologia")
    void clearStorySvuotaCronologia() {
        gestore.esegui(new AggiungiLibroCommand(libro1, libreria));
        gestore.esegui(new AggiungiLibroCommand(libro2, libreria));
        assertTrue(gestore.hasUndo());
        gestore.clearStory();
        assertFalse(gestore.hasUndo(), "Dopo clearStory() non devono esserci undo disponibili");
    }

    @Test
    @DisplayName("LIFO: l'ultimo command annullabile è il primo ad essere annullato")
    void lifoOrder() {
        gestore.esegui(new AggiungiLibroCommand(libro1, libreria)); // primo
        gestore.esegui(new AggiungiLibroCommand(libro2, libreria)); // secondo
        assertEquals(2, libreria.numeroLibri());
        //eseguo undo dal gestore (deve togliere libro2)
        gestore.annullaUltimoComando();
        //resta solo libro1
        assertEquals(1, libreria.numeroLibri());
        assertDoesNotThrow(() -> libreria.trovaLibro(libro1.getISBN()));
        // libro2 non dovrebbe esserci più
        assertThrows(java.util.NoSuchElementException.class, () -> libreria.trovaLibro(libro2.getISBN()));
        //secondo undo (toglie libro1)
        gestore.annullaUltimoComando();
        //libreria vuota e nessun altro undo
        assertEquals(0, libreria.numeroLibri());
        assertFalse(gestore.hasUndo());
    }
}
