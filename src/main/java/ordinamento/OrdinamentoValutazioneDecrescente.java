package ordinamento;

import libreria.Libro;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class OrdinamentoValutazioneDecrescente implements SortingStrategy
{
    private final Collator collator;

    public OrdinamentoValutazioneDecrescente() { this(Locale.ITALY); }
    public OrdinamentoValutazioneDecrescente(Locale locale) {
        this.collator = Collator.getInstance(locale);
        this.collator.setStrength(Collator.PRIMARY);
    }

    @Override
    public List<Libro> ordina(List<Libro> input) {
        List<Libro> out = new ArrayList<>(input);
        out.sort(
                Comparator.comparingInt(Libro::getValutazione).reversed()  // <— int + reversed
                        .thenComparing((a, b) -> collator.compare(
                                a.getTitolo() == null ? "" : a.getTitolo(),
                                b.getTitolo() == null ? "" : b.getTitolo()
                        ))
        );
        return out;
    }
}
