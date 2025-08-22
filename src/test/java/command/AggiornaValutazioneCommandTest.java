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

public class AggiornaValutazioneCommandTest
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
    @DisplayName("execute() aggiorna la valutazione quando lo stato è LETTO")
    void executeAggiornaValutazioneStatoLetto() {
        libreria.aggiornaStatoLettura(libro.getISBN(), StatoLettura.LETTO);
        AggiornaValutazioneCommand cmd = new AggiornaValutazioneCommand(libreria, libro.getISBN(), 4);
        cmd.execute();
        assertEquals(4, libreria.trovaLibro(libro.getISBN()).getValutazione(),
                "La valutazione deve essere aggiornata");
    }

    @Test
    @DisplayName("Valori di bordo: 0 e 5 sono validi")
    void valoriDiBordo() {
        libreria.aggiornaStatoLettura(libro.getISBN(), StatoLettura.LETTO);
        new AggiornaValutazioneCommand(libreria, libro.getISBN(), 0).execute();
        assertEquals(0, libreria.trovaLibro(libro.getISBN()).getValutazione());
        new AggiornaValutazioneCommand(libreria, libro.getISBN(), 5).execute();
        assertEquals(5, libreria.trovaLibro(libro.getISBN()).getValutazione());
    }

    @Test
    @DisplayName("Valori fuori range (<0 o >5) lanciano IllegalArgumentException e non modificano lo stato")
    void valoriFuoriRange() {
        libreria.aggiornaStatoLettura(libro.getISBN(), StatoLettura.LETTO);
        int prima = libreria.trovaLibro(libro.getISBN()).getValutazione();
        assertThrows(IllegalArgumentException.class,
                () -> new AggiornaValutazioneCommand(libreria, libro.getISBN(), -1).execute());
        assertEquals(prima, libreria.trovaLibro(libro.getISBN()).getValutazione());
        assertThrows(IllegalArgumentException.class,
                () -> new AggiornaValutazioneCommand(libreria, libro.getISBN(), 6).execute());
        assertEquals(prima, libreria.trovaLibro(libro.getISBN()).getValutazione());
    }

    @Test
    @DisplayName("Se lo stato NON è LETTO lancia IllegalStateException")
    void statoNonLetto() {
        AggiornaValutazioneCommand cmd =
                new AggiornaValutazioneCommand(libreria, libro.getISBN(), 3);
        assertThrows(IllegalStateException.class, cmd::execute);
    }

    @Test
    @DisplayName("ISBN inesistente lancia NoSuchElementException")
    void isbnInesistente() {
        libreria.aggiornaStatoLettura(libro.getISBN(), StatoLettura.LETTO);
        AggiornaValutazioneCommand cmd =
                new AggiornaValutazioneCommand(libreria, "ISBN-NON-ESISTE", 3);

        assertThrows(NoSuchElementException.class, cmd::execute);
    }

    @Test
    @DisplayName("ISBN null → NullPointerException; vuoto/blank → IllegalArgumentException")
    void isbnNullVuotoBlank() {
        libreria.aggiornaStatoLettura(libro.getISBN(), StatoLettura.LETTO);
        assertThrows(NullPointerException.class,
                () -> new AggiornaValutazioneCommand(libreria, null, 3).execute());
        assertThrows(IllegalArgumentException.class,
                () -> new AggiornaValutazioneCommand(libreria, "", 3).execute());
        assertThrows(IllegalArgumentException.class,
                () -> new AggiornaValutazioneCommand(libreria, "   ", 3).execute());
    }
}
