package ordinamento;

import libreria.Libro;

import java.text.Collator;
import java.util.Locale;

public class OrdinamentoAlfabeticoTitolo implements SortingStrategy
{
    private final Collator c = Collator.getInstance(Locale.ITALIAN);  //serve per il confronto tenendo conto delle regole linguistiche della regione selezionata

    public OrdinamentoAlfabeticoTitolo()
    {   c.setStrength(Collator.PRIMARY);    //livello di accortezza nell'ordinamento
    }

    public int compare(Libro l1, Libro l2)
    {   return c.compare(safe(l1.getTitolo()), safe(l2.getTitolo()));
    }

    private String safe(String s)
    {   return s == null ? "" : s.trim();   //gestione della stringa null (viene sostituita con "")
    }

    public String getNome()
    {   return "Ordinamento alfabetico (A-Z)";
    }
}
