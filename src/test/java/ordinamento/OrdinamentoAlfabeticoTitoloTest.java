package ordinamento;

import libreria.Genere;
import libreria.Libreria;
import libreria.Libro;
import libreria.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdinamentoAlfabeticoTitoloTest
{
    private Libreria libreria;
    private Libro libro1;
    private Libro libro2;
    private Libro libro3;

    @BeforeEach
    void setUp()
    {   libreria = new Libreria();
    }

    @Test
    void ordinaPerTitolo(){
        libreria.aggiungiLibro(libro1 = new Libro.Builder("La Coscienza di Zeno","Italo Svevo", "123", Genere.ROMANZO).build());
        libreria.aggiungiLibro(libro2 = new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build());
        libreria.aggiungiLibro(libro3 = new Libro.Builder("Uno, Nessuno e Centomila", "Pirandello", "789", Genere.ROMANZO)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(4)
                .build());
        List<Libro> ordinati = libreria.ordinamentoLibri(new OrdinamentoAlfabeticoTitolo());
        assertEquals(List.of(libro2, libro1, libro3), ordinati, "L'ordinamento alfabetico per titolo non corrisponde");
        assertEquals(3, libreria.numeroLibri(), "Libreria non deve essere modificata dall'ordinamento");
    }
}
