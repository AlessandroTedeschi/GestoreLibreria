package command;

import libreria.Libreria;
import libreria.Libro;

public class ModificaLibroCommand implements Command
{
    private final Libreria libreria;
    private final String isbn;
    private final Libro libroDopo;

    public ModificaLibroCommand(Libreria libreria, String isbn, Libro libroDopo) {
        this.libreria = libreria;
        this.isbn = isbn;
        this.libroDopo = libroDopo;
    }

    @Override public void execute() {
        libreria.rimuoviLibro(isbn);
        libreria.aggiungiLibro(libroDopo);
    }

}
