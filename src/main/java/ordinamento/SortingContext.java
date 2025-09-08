package ordinamento;

import libreria.Libreria;
import libreria.Libro;

import java.util.List;

public class SortingContext
{
    private SortingStrategy strategy;

    public SortingContext(SortingStrategy strategy) { this.strategy = strategy; }
    public void setStrategy(SortingStrategy strategy) { this.strategy = strategy; }

    public List<Libro> ordina(List<Libro> libri) {
        if (strategy == null) throw new IllegalStateException("Nessuna strategy impostata");
        return strategy.ordina(libri);
    }
}
