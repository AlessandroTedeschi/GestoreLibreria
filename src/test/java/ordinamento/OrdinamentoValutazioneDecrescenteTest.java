package ordinamento;

import libreria.Genere;
import libreria.Libreria;
import libreria.Libro;
import libreria.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdinamentoValutazioneDecrescenteTest {
    private Libreria libreria;
    private Libro libro1;
    private Libro libro2;
    private Libro libro3;

    @BeforeEach
    void setUp()
    {   libreria = new Libreria();
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
    }

    @Test
    void ordinaPerValutazioneCrescente() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        libreria.aggiungiLibro(libro3);
        List<Libro> ordinati = libreria.ordinamentoLibri(new OrdinamentoValutazioneDecrescente());
        assertEquals(List.of(libro2, libro1, libro3), ordinati, "Valori attesi: 5, 4, 2");
        assertEquals(3, libreria.numeroLibri());
    }
}
