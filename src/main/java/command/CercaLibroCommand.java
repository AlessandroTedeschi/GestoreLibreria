package command;

import libreria.Libreria;

public class CercaLibroCommand implements Command
{
    private final Libreria libreria;
    private final String s;

    public CercaLibroCommand(Libreria libreria, String s)
    {   this.libreria = libreria;
        this.s = s;
    }

    @Override
    public void execute()
    {   libreria.cercaLibro(s);
    }
}
