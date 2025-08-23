package persistenza;

import libreria.Genere;
import libreria.Libro;
import libreria.StatoLettura;

public class LibroDTO
{   public String titolo;
    public String autore;
    public String isbn;
    public Genere genere;
    public StatoLettura statoLettura;
    public Integer valutazione; // può essere null

    // Costruttore vuoto richiesto da Gson
    public LibroDTO() {}

    // --- Conversioni ---
    public static LibroDTO fromDomain(Libro l) {
        LibroDTO d = new LibroDTO();
        d.titolo = l.getTitolo();
        d.autore = l.getAutore();
        d.isbn = l.getISBN();
        d.genere = l.getGenere();
        d.statoLettura = l.getStatoLettura();
        d.valutazione = l.getValutazione();
        return d;
    }

    public Libro toDomain() {
        Libro.Builder b = new Libro.Builder(titolo, autore, isbn, genere);
        if (statoLettura != null)
            b.statoLettura(statoLettura);
        if (valutazione != null)
            b.valutazione(valutazione);
        return b.build();
    }

}
