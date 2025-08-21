package command;

import libreria.Libreria;
import libreria.Libro;

public class RimuoviLibroCommand implements Command
{
    private final String isbn;
    private final Libreria libreria;

    public RimuoviLibroCommand(String isbn, Libreria libreria)
    {   this.isbn = isbn;
        this.libreria = libreria;
    }

    @Override
    public void execute()
    {   libreria.rimuoviLibro(isbn);
    }
}
