package ui;

public enum OpzioniOrdinamento
{   ALFABETICO ("Alfabetico"),
    VALUTAZIONE_CRESCENTE ("Valutazione Crescente"),
    VALUTAZIONE_DECRESCENTE("Valutazione Decrescente");

    private final String scelta;

    OpzioniOrdinamento(String scelta)
    {   this.scelta = scelta;
    }

    public String getGenere()
    {   return scelta;
    }

    public String toString()
    {   return scelta;
    }

}
