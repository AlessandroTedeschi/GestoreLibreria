package command;

import libreria.Libreria;
import libreria.Libro;

public class AggiungiLibroCommand implements Command
{
    private final Libro libro;
    private final Libreria libreria;

    public AggiungiLibroCommand(Libro libro, Libreria libreria)
    {   this.libro = libro;
        this.libreria = libreria;
    }

    @Override
    public void execute()
    {   libreria.aggiungiLibro(libro);
    }
}
