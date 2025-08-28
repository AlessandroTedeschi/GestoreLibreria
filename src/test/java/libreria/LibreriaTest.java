package libreria;

import filtri.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LibreriaTest {

    private Libreria libreria;
    private Libro libro1;
    private Libro libro2;
    private Libro libro3;
    private Libro libro4;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();

        libro1 = new Libro.Builder("La Coscienza di Zeno","Italo Svevo", "123",Genere.ROMANZO)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(4)
                .build();

        libro2 = new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build();

        libro3 = new Libro.Builder("Il promessi sposi", "Alessandro Manzoni", "54525", Genere.ROMANZO)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(1)
                .build();

        libro4 = new Libro.Builder("It", "Stephen King", "6542", Genere.FANTASY)
                .statoLettura(StatoLettura.IN_LETTURA).build();
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

    @Test
    void filtra_nullRestituisceTutti() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        libreria.aggiungiLibro(libro3);
        libreria.aggiungiLibro(libro4);
        List<Libro> res = libreria.filtraggio(null);
        assertEquals(4, res.size());
        assertTrue(res.containsAll(List.of(libro1, libro2, libro3, libro4)));
    }


    //FILTRA
    @Test
    void filtra_perStatoLetto() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        libreria.aggiungiLibro(libro3);
        libreria.aggiungiLibro(libro4);
        Filtro f = new StatoLetturaFilter(StatoLettura.LETTO);
        List<Libro> res = libreria.filtraggio(f);
        assertEquals(3, res.size());
        assertTrue(res.containsAll(List.of(libro1, libro2, libro3)));
        assertFalse(res.contains(libro4));
    }

    @Test
    void filtra_perGenereRomanzo() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        libreria.aggiungiLibro(libro3);
        libreria.aggiungiLibro(libro4);
        Filtro f = new GenereFilter(Genere.ROMANZO);
        List<Libro> res = libreria.filtraggio(f);
        assertEquals(2, res.size());
        assertTrue(res.containsAll(List.of(libro1, libro3)));
        assertFalse(res.contains(libro2));
        assertFalse(res.contains(libro4));
    }

    @Test
    void filtra_perValutazione4() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        libreria.aggiungiLibro(libro3);
        libreria.aggiungiLibro(libro4);
        Filtro f = new ValutazioneFilter(4);
        List<Libro> res = libreria.filtraggio(f);
        assertEquals(1, res.size());
        assertTrue(res.containsAll(List.of(libro1)));
    }

    @Test
    void filtra_and_statoLetto_e_valutazione4() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        libreria.aggiungiLibro(libro3);
        libreria.aggiungiLibro(libro4);
        Filtro f = new FiltroAnd(
                new StatoLetturaFilter(StatoLettura.LETTO),
                new ValutazioneFilter(4)
        );
        List<Libro> res = libreria.filtraggio(f);
        assertEquals(1, res.size());
        assertTrue(res.containsAll(List.of(libro1)));
        assertFalse(res.contains(libro2));
        assertFalse(res.contains(libro3));
        assertFalse(res.contains(libro4));
    }

    @Test
    void filtra_and_triplo_stato_genere_valutazione() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        libreria.aggiungiLibro(libro3);
        libreria.aggiungiLibro(libro4);
        Filtro f = new FiltroAnd(
                new FiltroAnd(
                        new StatoLetturaFilter(StatoLettura.LETTO),
                        new GenereFilter(Genere.POESIA)
                ),
                new ValutazioneFilter(5)
        );
        List<Libro> res = libreria.filtraggio(f);
        // Solo "Divina Commedia"
        assertEquals(1, res.size());
        assertEquals(libro2, res.get(0));
    }

    @Test
    void filtra_nessunaCorrispondenza() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        libreria.aggiungiLibro(libro3);
        libreria.aggiungiLibro(libro4);
        Filtro f = new FiltroAnd(
                new GenereFilter(Genere.FANTASY),
                new StatoLetturaFilter(StatoLettura.LETTO)
        );
        List<Libro> res = libreria.filtraggio(f);
        assertTrue(res.isEmpty());
    }


}

