package filtri;

import libreria.Genere;
import libreria.Libro;

public class GenereFilter implements Filtro
{
    private final Genere genere;

    public GenereFilter(Genere genere)
    { this.genere = genere;
    }


    @Override public boolean test(Libro b) {
        return b.getGenere() == genere;
    }
}
