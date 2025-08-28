package filtri;

import libreria.Genere;
import libreria.Libro;
import libreria.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatoLetturaFilterTest
{
    private Libro libro1, libro2, libro3, libro4;

    @BeforeEach
    void setUp() {
        libro1 = new Libro.Builder("La Coscienza di Zeno","Italo Svevo","123", Genere.ROMANZO)
                .statoLettura(StatoLettura.LETTO).valutazione(4).build();

        libro2 = new Libro.Builder("Divina Commedia","Dante Alighieri","456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO).valutazione(5).build();

        libro3 = new Libro.Builder("Il promessi sposi","Alessandro Manzoni","54525", Genere.ROMANZO)
                .statoLettura(StatoLettura.LETTO).valutazione(1).build();

        libro4 = new Libro.Builder("It","Stephen King","6542", Genere.FANTASY)
                .statoLettura(StatoLettura.IN_LETTURA).build();
    }

    @Test
    void testStatoLetto() {
        Filtro f = new StatoLetturaFilter(StatoLettura.LETTO);
        assertTrue(f.test(libro1));
        assertTrue(f.test(libro2));
        assertTrue(f.test(libro3));
        assertFalse(f.test(libro4));
    }

    @Test
    void testStatoInLettura() {
        Filtro f = new StatoLetturaFilter(StatoLettura.IN_LETTURA);
        assertFalse(f.test(libro1));
        assertFalse(f.test(libro2));
        assertFalse(f.test(libro3));
        assertTrue(f.test(libro4));
    }
}
