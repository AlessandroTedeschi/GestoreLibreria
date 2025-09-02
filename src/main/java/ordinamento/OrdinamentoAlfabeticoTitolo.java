package ordinamento;

import libreria.Libro;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdinamentoAlfabeticoTitolo implements SortingStrategy
{
    private final Collator c = Collator.getInstance(Locale.ITALIAN);  //serve per il confronto tenendo conto delle regole linguistiche della regione selezionata

    public OrdinamentoAlfabeticoTitolo()
    {   c.setStrength(Collator.PRIMARY);    //livello di accortezza nell'ordinamento
    }

    @Override
    public List<Libro> ordina(List<Libro> input) {
        List<Libro> out = new ArrayList<>(input);
        out.sort((a, b) -> c.compare(
                a.getTitolo() == null ? "" : a.getTitolo(),
                b.getTitolo() == null ? "" : b.getTitolo()
        ));
        return out;
    }
}
