package command;

import libreria.Genere;
import libreria.Libreria;
import libreria.Libro;
import libreria.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AggiornaStatoLetturaCommandTest
{
    private Libreria libreria;
    private Libro libro;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();
        libro = new Libro.Builder("La Coscienza di Zeno", "Italo Svevo", "123", Genere.ROMANZO)
                .statoLettura(StatoLettura.DA_LEGGERE)
                .valutazione(0)
                .build();
        libreria.aggiungiLibro(libro);
    }

    @Test
    @DisplayName("execute() aggiorna lo stato di lettura del libro")
    void executeAggiornaStato() {
        AggiornaStatoLetturaCommand cmd = new AggiornaStatoLetturaCommand(libreria, StatoLettura.IN_LETTURA, libro.getISBN());
        cmd.execute();
        assertEquals(StatoLettura.IN_LETTURA,
                libreria.trovaLibro(libro.getISBN()).getStatoLettura(), "Dopo execute() lo stato deve essere aggiornato");
    }

    @Test
    @DisplayName("execute() con ISBN inesistente lancia NoSuchElementException e non modifica lo stato")
    void executeIsbnInesistente() {
        StatoLettura statoPre = libro.getStatoLettura();
        AggiornaStatoLetturaCommand cmd = new AggiornaStatoLetturaCommand(libreria, StatoLettura.LETTO, "ISBN-NON-ESISTE");
        assertThrows(NoSuchElementException.class, cmd::execute);
        assertEquals(statoPre, libreria.trovaLibro(libro.getISBN()).getStatoLettura(),
                "Lo stato del libro esistente non deve cambiare");
    }

    @Test
    @DisplayName("execute() con stato null lancia NullPointerException")
    void executeStatoNull() {
        AggiornaStatoLetturaCommand cmd = new AggiornaStatoLetturaCommand(libreria, null, libro.getISBN());
        assertThrows(NullPointerException.class, cmd::execute);
    }

    @Test
    @DisplayName("execute() con ISBN null lancia NullPointerException")
    void executeIsbnNull() {
        AggiornaStatoLetturaCommand cmd = new AggiornaStatoLetturaCommand(libreria, StatoLettura.LETTO, null);
        assertThrows(NullPointerException.class, cmd::execute);
    }

    @Test
    @DisplayName("execute() con ISBN vuoto/blank lancia IllegalArgumentException")
    void executeIsbnVuotoOBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new AggiornaStatoLetturaCommand(libreria, StatoLettura.LETTO, "").execute());
        assertThrows(IllegalArgumentException.class,
                () -> new AggiornaStatoLetturaCommand(libreria, StatoLettura.LETTO, "   ").execute());
    }
}
