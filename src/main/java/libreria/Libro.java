package libreria;

public class Libro
{   //attributi immutabili
    private final String titolo;
    private final String autore;
    private final String ISBN;
    private final Genere genere;
    //attributi mutabili
    private StatoLettura statoLettura;
    private int valutazione;

    public static class Builder
    {   //Parametri obbligatori
        private String titolo;
        private String autore;
        private String ISBN;
        private Genere genere;

        //Parametri opzionali
        private StatoLettura statoLettura = StatoLettura.DA_LEGGERE; //valore di default
        private int valutazione = 0; //valore di default

        public Builder(String titolo, String autore, String ISBN, Genere genere)
        {   this.titolo = titolo;
            this.autore = autore;
            this.ISBN = ISBN;
            this.genere = genere;
        }

        /*public Builder titolo(String titolo)
        {   this.titolo = titolo; return this;
        }

        public Builder autore(String autore)
        {   this.autore = autore; return this;
        }

        public Builder ISBN(String ISBN)
        {   this.ISBN = ISBN; return this;
        }

        public Builder genere(Genere genere)
        {   this.genere = genere; return this;}*/

        public Builder statoLettura(StatoLettura statoLettura)
        {   this.statoLettura = statoLettura; return this;
        }

        public Builder valutazione(int valutazione)
        {   if(valutazione<0 || valutazione>5)
            throw new IllegalArgumentException("La valutazione deve essere compresa tra 0 e 5");
            this.valutazione = valutazione; return this;
        }

        public Libro build()
        {   if (titolo == null || titolo.isBlank())
            throw new IllegalStateException("Il titolo è obbligatorio");
            if (autore == null || autore.isBlank())
                throw new IllegalStateException("L'autore è obbligatorio");
            if (ISBN == null || ISBN.isBlank())
                throw new IllegalStateException("Il codice ISBN è obbligatorio");
            if (genere == null)
                throw new IllegalStateException("Il genere è obbligatorio");

            if(statoLettura != StatoLettura.LETTO && valutazione != 0)
                throw new IllegalArgumentException("Se lo stato del libro non è LETTO, la valutazione deve essere 0");

            return new Libro(this); //passo al costruttore di Libro l'istanza di Builder con i vari parametri
        }
    }

    //Costruttore di Libro che usa l'istanza di Builder creata
    public Libro(Builder builder)
    {   this.titolo = builder.titolo;
        this.autore = builder.autore;
        this.ISBN = builder.ISBN;
        this.genere = builder.genere;
        this.statoLettura = builder.statoLettura;
        this.valutazione = builder.valutazione;
    }


    public String getTitolo()
    {   return titolo;
    }
    public String getAutore()
    {   return autore;
    }
    public String getISBN()
    {   return ISBN;
    }
    public Genere getGenere()
    {   return genere;
    }
    public int getValutazione()
    {   return valutazione;
    }
    public StatoLettura getStatoLettura()
    {   return statoLettura;
    }

    public void aggiornaValutazione(int nuovaValutazione)
    {   if(statoLettura != StatoLettura.LETTO)
            throw new IllegalStateException("Puoi assegnare una valutazione al libro solo se lo stato è LETTO");
        if(nuovaValutazione<0 || nuovaValutazione>5)
            throw new IllegalArgumentException("La valutazione deve essere compresa tra 0 e 5");
        this.valutazione = nuovaValutazione;
    }

    public void aggiornaStatoLettura(StatoLettura nuovoStato)
    {   this.statoLettura = nuovoStato;
    }

    public boolean equals(Object o)
    {   if(o == this)
        return true;
        if(!(o instanceof Libro))
            return false;
        Libro l = (Libro) o;
        return ISBN.equals(l.getISBN());  //due libri sono uguali se è uguale il loro ISBN
    }

    public int hashCode()
    {   return  ISBN.hashCode();
    }

    public String toString()
    {   return "Libro {" +
            "titolo='" + titolo + '\'' +
            ", autore='" + autore + '\'' +
            ", isbn='" + ISBN + '\'' +
            ", genere=" + genere +
            ", stato=" + statoLettura +
            ", valutazione=" + valutazione +
            '}';
    }
}

