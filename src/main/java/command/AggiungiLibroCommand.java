package command;

import libreria.Libreria;
import libreria.Libro;

public class AggiungiLibroCommand implements UndoableCommand
{
    private final Libro libro;
    private final Libreria libreria;
    private boolean eseguito;

    public AggiungiLibroCommand(Libro libro, Libreria libreria)
    {   this.libro = libro;
        this.libreria = libreria;
    }

    @Override
    public void execute()
    {   libreria.aggiungiLibro(libro);
        eseguito=true;
    }

    public void undo()
    {   if(!eseguito)
            return;
        libreria.rimuoviLibro(libro.getISBN());
        eseguito=false;
    }
}
