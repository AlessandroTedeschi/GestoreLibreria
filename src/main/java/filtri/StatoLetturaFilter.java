package filtri;

import libreria.Libro;
import libreria.StatoLettura;

public class StatoLetturaFilter implements Filtro
{
    private final StatoLettura stato;

    public StatoLetturaFilter(StatoLettura stato)
    {   this.stato = stato;
    }

    @Override public boolean test(Libro b)
    {   return b.getStatoLettura() == stato;
    }
}
