package command;

import libreria.Libreria;
import libreria.Libro;

public class RimuoviLibroCommand implements UndoableCommand
{
    private final String isbn;
    private final Libreria libreria;
    private Libro libroRimosso;
    private boolean eseguito;

    public RimuoviLibroCommand(String isbn, Libreria libreria)
    {   this.isbn = isbn;
        this.libreria = libreria;
    }

    @Override
    public void execute()
    {   libroRimosso = libreria.trovaLibro(isbn);
        libreria.rimuoviLibro(isbn);
        eseguito = true;
    }

    public void undo()
    {   if(!eseguito)
            return;
        libreria.aggiungiLibro(libroRimosso);
        eseguito = false;
    }
}
