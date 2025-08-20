package libreria;

public enum StatoLettura
{   DA_LEGGERE("Da leggere"),
    LETTO("Letto"),
    IN_LETTURA("In lettura");

    private final String statoLettura;

    StatoLettura(String statoLettura)
    {   this.statoLettura = statoLettura;
    }

    public String getStatoLettura()
    {   return statoLettura;
    }

    public String toString()
    {   return statoLettura;
    }
}
