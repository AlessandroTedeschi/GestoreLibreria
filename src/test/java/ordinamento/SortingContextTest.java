package ordinamento;

import libreria.Genere;
import libreria.Libreria;
import libreria.Libro;
import libreria.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortingContextTest {

    private Libreria libreria;
    private Libro libro1;
    private Libro libro2;
    private Libro libro3;
    private SortingContext context;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();

        libro1 = new Libro.Builder("La Coscienza di Zeno","Italo Svevo", "123", Genere.ROMANZO)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(4)
                .build();

        libro2 = new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build();

        libro3 = new Libro.Builder("Uno, Nessuno e Centomila", "Pirandello", "789", Genere.ROMANZO)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(2)
                .build();

        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        libreria.aggiungiLibro(libro3);

        // inizializzo il context con una strategy a piacere
        context = new SortingContext(new OrdinamentoAlfabeticoTitolo());
    }

    @Test
    void ordinaPerValutazioneCrescente() {
        context.setStrategy(new OrdinamentoValutazioneCrescente());
        List<Libro> ordinati = context.ordina(libreria.getLibri());

        assertEquals(List.of(libro3, libro1, libro2), ordinati,
                "Valori attesi: 2, 4, 5");
        assertEquals(3, libreria.numeroLibri(), "La libreria deve mantenere 3 libri");
    }

    @Test
    void ordinaPerValutazioneDecrescente() {
        context.setStrategy(new OrdinamentoValutazioneDecrescente());
        List<Libro> ordinati = context.ordina(libreria.getLibri());

        assertEquals(List.of(libro2, libro1, libro3), ordinati,
                "Valori attesi: 5, 4, 2");
        assertEquals(3, libreria.numeroLibri());
    }

    @Test
    void ordinaPerTitoloAlfabetico() {
        context.setStrategy(new OrdinamentoAlfabeticoTitolo());
        List<Libro> ordinati = context.ordina(libreria.getLibri());

        assertEquals(List.of(libro2, libro1, libro3), ordinati,
                "Ordine atteso: 'Divina Commedia', 'La Coscienza di Zeno', 'Uno, Nessuno e Centomila'");
        assertEquals(3, libreria.numeroLibri());
    }
}

