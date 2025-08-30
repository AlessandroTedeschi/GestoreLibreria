package libreria;

public enum Genere
{   ROMANZO("Romanzo"),
    BIOGRAFIA("Biografia"),
    FANTASY("Fantasy"),
    GIALLO("Giallo"),
    POEMA("Poema"),
    STORICO("Storico"),
    AVVENTURA("Avventura"),
    HORROR("Horror"),
    SCIENTIFICO("Scientifico"),
    MANUALE("Manuale"),
    POESIA("Poesia");

    private final String genere;

    Genere(String genere)
    {   this.genere = genere;
    }

    public String getGenere()
    {   return genere;
    }

    public String toString()
    {   return genere;
    }
}
