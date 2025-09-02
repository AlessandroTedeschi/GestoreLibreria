package ordinamento;

import libreria.Libro;
import java.util.List;

public interface SortingStrategy
{
    List<Libro> ordina(List<Libro> libri);
}
