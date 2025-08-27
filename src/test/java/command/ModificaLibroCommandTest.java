package command;

import libreria.Libreria;
import libreria.Libro;
import libreria.Genere;
import libreria.StatoLettura;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per ModificaLibroCommand: sostituisce un libro esistente mantenendo lo stesso ISBN.
 */
public class ModificaLibroCommandTest {

    @Test
    void modificaLibro_sostituisceDatiMantenendoStessoIsbn() {
        Libreria libreria = new Libreria();

        Libro libro1 = new Libro.Builder("La Coscienza di Zeno", "Italo Svevo", "123", Genere.ROMANZO)
                .statoLettura(StatoLettura.DA_LEGGERE)
                .valutazione(0)
                .build();

        Libro libro2 = new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build();

        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);

        //nuovo stato del libro1
        Libro nuovolibro1 = new Libro.Builder("La Coscienza di Zeno", "Italo Svevo", "123", Genere.ROMANZO)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(4)
                .build();

        //sostituzione
        ModificaLibroCommand cmd = new ModificaLibroCommand(libreria, libro1.getISBN(), nuovolibro1);
        cmd.execute();

        //verifichiamo che il numero di libri non sia variato tra prima e dopo la modifica
        assertEquals(2, libreria.getLibri().size(), "La libreria deve mantenere lo stesso numero di libri");

        //il libro con quell'ISBN ora ha i campi aggiornati
        Libro trovato = trovaPerIsbn(libreria, libro1.getISBN())
                .orElseThrow(() -> new AssertionError("Libro con ISBN " + libro1.getISBN() + " non trovato dopo la modifica"));

        assertEquals("La Coscienza di Zeno", trovato.getTitolo());
        assertEquals("Italo Svevo", trovato.getAutore());
        assertEquals(Genere.ROMANZO, trovato.getGenere());
        assertEquals(StatoLettura.LETTO, trovato.getStatoLettura());
        assertEquals(4, trovato.getValutazione());

        //l'altro libro è rimasto intatto
        Libro altroTrovato = trovaPerIsbn(libreria, libro2.getISBN())
                .orElseThrow(() -> new AssertionError("L'altro libro non dovrebbe essere stato toccato"));
        assertEquals("Divina Commedia", altroTrovato.getTitolo());
        assertEquals("Dante Alighieri", altroTrovato.getAutore());
        assertEquals(5, altroTrovato.getValutazione());
    }

    private Optional<Libro> trovaPerIsbn(Libreria libreria, String isbn) {
        try {
            Libro l = libreria.trovaLibro(isbn);
            return Optional.ofNullable(l);
        } catch (Exception ignore) {
            return libreria.getLibri().stream()
                    .filter(b -> isbn.equals(b.getISBN()))
                    .findFirst();
        }
    }
}

