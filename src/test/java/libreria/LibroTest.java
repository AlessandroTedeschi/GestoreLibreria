package libreria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibroTest {

    private Genere anyGenere() {
        return Genere.values()[0]; // primo valore disponibile dell'enum
    }

    @Test
    //test costruttore con parametri obbligatori e valori di default per gli altri parametri
    void build_parametriObbligatori_eDefault() {
        Libro l = new Libro.Builder("Titolo", "Autore", "ISBN1", anyGenere()).build();
        assertEquals("Titolo", l.getTitolo());
        assertEquals("Autore", l.getAutore());
        assertEquals("ISBN1", l.getISBN());
        assertEquals(StatoLettura.DA_LEGGERE, l.getStatoLettura());
        assertEquals(0, l.getValutazione());
    }

    @Test
    //test costruttore con stato del libro LETTO e scelta della valutazione da 1 a 5
    void build_Letto_eValutazioneValida_ok() {
        Libro l = new Libro.Builder("T", "A", "ISBN2", anyGenere())
                .statoLettura(StatoLettura.LETTO)
                .valutazione(4)
                .build();
        assertEquals(StatoLettura.LETTO, l.getStatoLettura());
        assertEquals(4, l.getValutazione());
    }

    @Test
    //test costruttore con stato del libro NON LETTO e valutazione diversa da 0 --> eccezione
    void build_nonLetto_conValutazioneNonZero() {
        assertThrows(IllegalArgumentException.class, () ->
                new Libro.Builder("T", "A", "ISBN3", anyGenere())
                        // stato default = DA_LEGGERE
                        .valutazione(1)
                        .build()
        );
    }

    @Test
    //test costruttore con stato del libro LETTO e valutazione non compresa tra 0 e 5
    void builder_Letto_evalutazioneFuoriRange() {
        assertThrows(IllegalArgumentException.class, () ->
                new Libro.Builder("T", "A", "ISBN4", anyGenere())
                        .statoLettura(StatoLettura.LETTO)
                        .valutazione(-1)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Libro.Builder("T", "A", "ISBN5", anyGenere())
                        .statoLettura(StatoLettura.LETTO)
                        .valutazione(6)
        );
    }

    @Test
    //test sul cambiamento dello stato di lettura di un libro
    void modificaStatoLettura() {
        Libro l = new Libro.Builder("T", "A", "ISBN6", anyGenere()).build();
        l.aggiornaStatoLettura(StatoLettura.LETTO);
        assertEquals(StatoLettura.LETTO, l.getStatoLettura());
    }

    @Test
    //aggiornamento valutazione su un libro NON LETTO
    void aggiornaValutazione_suNonLetto() {
        Libro l = new Libro.Builder("T", "A", "ISBN7", anyGenere()).build();
        assertThrows(IllegalStateException.class, () -> l.aggiornaValutazione(4));          //vietato
    }

    @Test
    //aggiornamento valutazione libro LETTO con valori validi (da 1 a 5)
    void aggiornaValutazione_suLetto_valoriValidi() {
        Libro l = new Libro.Builder("T", "A", "ISBN8", anyGenere())
                .statoLettura(StatoLettura.LETTO)
                .build();
        l.aggiornaValutazione(5);
        assertEquals(5, l.getValutazione());
        l.aggiornaValutazione(3);
        assertEquals(3, l.getValutazione());
    }

    @Test
    //aggiornamento valutazione libro LETTO con valori non validi (<1 o >5) --> eccezione
    void aggiornaValutazione_fuoriRange() {
        Libro l = new Libro.Builder("T", "A", "ISBN9", anyGenere())
                .statoLettura(StatoLettura.LETTO)
                .build();
        assertThrows(IllegalArgumentException.class, () -> l.aggiornaValutazione(-1));
        assertThrows(IllegalArgumentException.class, () -> l.aggiornaValutazione(6));
    }

    @Test
    //campi obbligatori del costruttore lasciati vuoti --> eccezione
    void vincoliObbligatori_delBuilder_nonInseriti() {
        assertThrows(IllegalStateException.class, () ->
                new Libro.Builder("", "A", "ISBN10", anyGenere()).build()
        );
        assertThrows(IllegalStateException.class, () ->
                new Libro.Builder("T", "", "ISBN11", anyGenere()).build()
        );
        assertThrows(IllegalStateException.class, () ->
                new Libro.Builder("T", "A", "", anyGenere()).build()
        );
        assertThrows(IllegalStateException.class, () ->
                new Libro.Builder("T", "A", "ISBN12", null).build()
        );
    }

    @Test
    void equals_e_hashCode_basatiSuISBN() {
        Libro l1 = new Libro.Builder("T1", "A1", "SAME", anyGenere()).build();
        Libro l2 = new Libro.Builder("T2", "A2", "SAME", anyGenere()).build();
        Libro l3 = new Libro.Builder("T3", "A3", "DIFF", anyGenere()).build();

        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
        assertNotEquals(l1, l3);
    }

    @Test
    void toString_contieneInfoPrincipali() {
        Libro l = new Libro.Builder("Tit", "Aut", "ISBN13", anyGenere())
                .statoLettura(StatoLettura.LETTO)
                .valutazione(4)
                .build();

        String s = l.toString();
        assertTrue(s.contains("Tit"));
        assertTrue(s.contains("Aut"));
        assertTrue(s.contains("ISBN13"));
        assertTrue(s.contains("valutazione"));
    }
}

