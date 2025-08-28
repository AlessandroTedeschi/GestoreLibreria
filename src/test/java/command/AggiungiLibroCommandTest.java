package command;

import libreria.Genere;
import libreria.Libreria;
import libreria.Libro;
import libreria.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class AggiungiLibroCommandTest {

    private Libreria libreria;
    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();
        libro1 = new Libro.Builder("La Coscienza di Zeno", "Italo Svevo", "123", Genere.ROMANZO).build();
        libro2 = new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build();
    }

    @Test
    @DisplayName("execute() aggiunge il libro alla libreria")
    void executeAggiungeLibro() {
        AggiungiLibroCommand cmd = new AggiungiLibroCommand(libro1, libreria);
        assertEquals(0, libreria.numeroLibri(), "Pre-condizione: libreria vuota");
        cmd.execute();
        assertEquals(1, libreria.numeroLibri(), "Dopo execute() la libreria deve contenere 1 libro");
    }

    @Test
    @DisplayName("undo() dopo execute() rimuove il libro")
    void undoDopoExecuteRimuoveLibro() {
        AggiungiLibroCommand cmd = new AggiungiLibroCommand(libro2, libreria);
        cmd.execute();
        assertEquals(1, libreria.numeroLibri(), "Pre-condizione: aggiunta avvenuta");
        cmd.undo();
        assertEquals(0, libreria.numeroLibri(), "Dopo undo() non deve restare il libro");
    }

    @Test
    @DisplayName("undo() senza execute() è un'operazione nulla e non lancia eccezioni")
    void undoSenzaExecuteNonFaNulla() {
        AggiungiLibroCommand cmd = new AggiungiLibroCommand(libro1, libreria);
        assertDoesNotThrow(cmd::undo, "undo() prima di execute() non deve lanciare");
        assertEquals(0, libreria.numeroLibri(), "La libreria resta vuota");
    }

    @Test
    @DisplayName("execute() con libro null propaga NullPointerException da Libreria")
    void executeConLibroNullLanciaEccezione() {
        AggiungiLibroCommand cmd = new AggiungiLibroCommand(null, libreria);
        assertThrows(NullPointerException.class, cmd::execute,
                "Libreria.aggiungiLibro(null) dovrebbe lanciare NullPointerException");
        assertEquals(0, libreria.numeroLibri());
    }

    @Test
    @DisplayName("execute() con libreria null causa NullPointerException")
    void executeConLibreriaNullLanciaEccezione() {
        AggiungiLibroCommand cmd = new AggiungiLibroCommand(libro2, null);
        assertThrows(NullPointerException.class, cmd::execute,
                "Invocare execute() con libreria null deve causare NPE");
    }

    @Test
    @DisplayName("execute() due volte con lo stesso libro: ci si aspetta un errore di duplicato")
    void executeDueVolteStessoLibro() {
        AggiungiLibroCommand cmd = new AggiungiLibroCommand(libro2, libreria);
        cmd.execute();
        assertThrows(IllegalStateException.class, cmd::execute,
                "Aggiungere due volte lo stesso ISBN dovrebbe lanciare IllegalStateException");
        assertEquals(1, libreria.numeroLibri(), "La libreria non deve contenere duplicati");
    }

    @Test
    @DisplayName("undo() è idempotente: chiamato due volte non lancia e non cambia lo stato")
    void doppioUndo() {
        AggiungiLibroCommand cmd = new AggiungiLibroCommand(libro1, libreria);
        cmd.execute();
        org.junit.jupiter.api.Assertions.assertEquals(1, libreria.numeroLibri(), "Pre-condizione: dopo execute() deve esserci 1 libro");
        cmd.undo();
        org.junit.jupiter.api.Assertions.assertEquals(0, libreria.numeroLibri(), "Dopo il primo undo() la libreria torna vuota");
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(cmd::undo, "Il secondo undo() non deve lanciare");
        org.junit.jupiter.api.Assertions.assertEquals(0, libreria.numeroLibri(), "Il secondo undo() non deve cambiare lo stato");
    }

}
