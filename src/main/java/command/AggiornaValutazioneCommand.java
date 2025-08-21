package command;

import libreria.Libreria;

public class AggiornaValutazioneCommand implements Command
{
    private final Libreria libreria;
    private final String isbn;
    private final int val;

    public AggiornaValutazioneCommand(Libreria libreria, String isbn, int val)
    {   this.libreria = libreria;
        this.isbn = isbn;
        this.val = val;
    }

    @Override
    public void execute()
    {   libreria.aggiornaValutazione(isbn, val);
    }
}
