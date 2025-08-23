package ordinamento;

import libreria.Libro;

public class OrdinamentoValutazioneDecrescente implements SortingStrategy
{
    public int compare(Libro l1, Libro l2)
    {   Integer v1 = l1.getValutazione();
        Integer v2 = l2.getValutazione();
        return v2.compareTo(v1);
    }


    @Override
    public String getNome()
    {   return "Ordinamento per valutazione decrescente";
    }
}
