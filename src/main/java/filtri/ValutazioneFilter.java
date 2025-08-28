package filtri;

import libreria.Libro;

public class ValutazioneFilter implements Filtro
{
    private final int n;

    public ValutazioneFilter(int n)
    {   this.n = n;
    }

    @Override
    public boolean test(Libro b)
    {   return b.getValutazione() == n;
    }


}
