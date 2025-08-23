package ordinamento;

import libreria.Libro;

import java.util.Comparator;

public interface SortingStrategy extends Comparator<Libro>
{
    String getNome();
}
