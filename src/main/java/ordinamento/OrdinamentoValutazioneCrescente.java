package ordinamento;

import libreria.Libro;

public class OrdinamentoValutazioneCrescente implements SortingStrategy
{
    public int compare(Libro l1, Libro l2)
    {   Integer v1 = l1.getValutazione();
        Integer v2 = l2.getValutazione();
        return v1.compareTo(v2);
    }

    public String getNome()
    {   return "Ordinamento per valutazione crescente";
    }
}
