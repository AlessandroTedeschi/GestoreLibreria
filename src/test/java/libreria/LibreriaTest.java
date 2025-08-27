package libreria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LibreriaTest {

    private Libreria libreria;
    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();

        libro1 = new Libro.Builder("La Coscienza di Zeno","Italo Svevo", "123",Genere.ROMANZO).build();

        libro2 = new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build();
    }

    //AGGIUNTA
    @Test
    void testAggiungiLibroValido() {
        libreria.aggiungiLibro(libro1);
        assertEquals(1, libreria.numeroLibri());
        assertSame(libro1, libreria.trovaLibro("123"));
    }

    @Test
    void testAggiungiLibroNullo() {
        assertThrows(NullPointerException.class, () -> libreria.aggiungiLibro(null));
    }

    @Test
    void testAggiungiLibroConISBNVuoto() {
        assertThrows(IllegalStateException.class, () ->
                new Libro.Builder("Uno, Nessuno e Centomila", "Pirandello", "  ", Genere.ROMANZO).build()
        );

    }

    @Test
    void testAggiungiLibroDuplicato() {
        libreria.aggiungiLibro(libro1);
        assertThrows(IllegalStateException.class, () -> libreria.aggiungiLibro(libro1));
    }

    //RIMOZIONE
    @Test
    void testRimuoviLibroEsistente() {
        libreria.aggiungiLibro(libro1);
        assertTrue(libreria.rimuoviLibro("123"));
        assertEquals(0, libreria.numeroLibri());
    }

    @Test
    void testRimuoviLibroNonEsistente() {
        assertFalse(libreria.rimuoviLibro("999"));
    }

    @Test
    void testRimuoviISBNNonValido() {
        assertThrows(IllegalArgumentException.class, () -> libreria.rimuoviLibro(""));
        assertThrows(NullPointerException.class, () -> libreria.rimuoviLibro(null));
    }

    //TROVA LIBRO
    @Test
    void testTrovaLibroEsistente() {
        libreria.aggiungiLibro(libro2);
        Libro trovato = libreria.trovaLibro("456");
        assertEquals("Divina Commedia", trovato.getTitolo());
    }

    @Test
    void testTrovaLibroNonEsistente() {
        assertThrows(NoSuchElementException.class, () -> libreria.trovaLibro("000"));
    }

    //CERCA
    @Test
    void testCercaPerTitoloParziale() {
        libreria.aggiungiLibro(libro1);
        List<Libro> trovati = libreria.cercaLibro("coscienza");
        assertEquals(1, trovati.size());
        assertEquals("La Coscienza di Zeno", trovati.get(0).getTitolo());
    }

    @Test
    void testCercaPerAutoreCaseInsensitive() {
        libreria.aggiungiLibro(libro2);
        List<Libro> trovati = libreria.cercaLibro("dante");
        assertEquals(1, trovati.size());
        assertEquals("Dante Alighieri", trovati.get(0).getAutore());
    }

    @Test
    void testCercaNessunRisultato() {
        libreria.aggiungiLibro(libro1);
        List<Libro> trovati = libreria.cercaLibro("Shakespeare");
        assertTrue(trovati.isEmpty());
    }
}

