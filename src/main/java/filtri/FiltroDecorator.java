package filtri;

import libreria.Libro;

public abstract class FiltroDecorator implements Filtro
{
    //filtro che sto decorando
    protected final Filtro inner;

    public FiltroDecorator(Filtro inner)
    {   if(inner == null) throw new NullPointerException("Filtro null");
        this.inner=inner;
    }

    @Override
    public abstract boolean test(Libro b);
}
