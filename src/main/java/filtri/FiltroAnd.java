package filtri;

import libreria.Libro;

public final class FiltroAnd extends FiltroDecorator
{
    //secondo filtro da combinare a quello di FiltroDecorator
    private final Filtro other;

    public FiltroAnd(Filtro f1, Filtro f2)
    {   super(f1);
        if(f2==null)
            throw new IllegalArgumentException("Filtro " + f2 + " null");
        this.other=f2;
    }

    public boolean test(Libro b)
    {   return inner.test(b) && other.test(b);
    }
}
