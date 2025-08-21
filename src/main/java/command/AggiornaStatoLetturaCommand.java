package command;

import libreria.Libreria;
import libreria.StatoLettura;

public class AggiornaStatoLetturaCommand implements Command
{
    private final Libreria libreria;
    private final StatoLettura statoLettura;
    private final String isbn;

    public AggiornaStatoLetturaCommand(Libreria libreria, StatoLettura statoLettura, String isbn)
    {   this.libreria = libreria;
        this.statoLettura = statoLettura;
        this.isbn = isbn;
    }

    @Override
    public void execute()
    {   libreria.aggiornaStatoLettura(isbn, statoLettura);
    }
}
