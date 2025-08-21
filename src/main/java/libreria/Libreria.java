package libreria;

import java.util.*;

public class Libreria
{
    private final Map<String, Libro> libreria = new LinkedHashMap<>();

    public int numeroLibri()
    {   return libreria.size();
    }

    public void aggiungiLibro(Libro l)
    {   if(l == null)
            throw new NullPointerException("Il libro non può essere vuoto");
        String isbn = l.getISBN();  //nessun controllo su isbn null o black, poichè viene fatto già dal costruttore del libro
        if(libreria.containsKey(isbn))
            throw new IllegalStateException("Il libro con codice " + isbn + " è già presente nella libreria");
        libreria.put(isbn, l);
    }

    public boolean rimuoviLibro(String isbn)
    {   if(isbn == null)
            throw new NullPointerException("ISBN non valido");
        if(isbn.isBlank())
            throw new IllegalArgumentException("ISBN non valido");
        return libreria.remove(isbn) != null;
    }

    public Libro trovaLibro(String isbn)
    {  if(isbn == null)
            throw new NullPointerException("ISBN non valido");
        if(isbn.isBlank())
            throw new IllegalArgumentException("ISBN non valido");
        Libro l = libreria.get(isbn);
        if(l==null)
            throw new NoSuchElementException("Nessun libro con codice " + isbn);
        return l;
    }

    public void aggiornaStatoLettura(String isbn, StatoLettura s)
    {   if(s==null)
            throw new NullPointerException("Stato lettura nullo");
        trovaLibro(isbn).aggiornaStatoLettura(s);
    }

    public void aggiornaValutazione(String isbn, int val)
    {   trovaLibro(isbn).aggiornaValutazione(val);
    }

    //metodo di ricerca di un libro per autore o titolo
    public List<Libro> cercaLibro(String s)
    {   if(s==null)
            throw new NullPointerException("Barra di ricerca nulla");
        String r = s.trim().toLowerCase();
        if(r.isBlank())
            throw new IllegalArgumentException("Barra di ricerca vuota");
        List<Libro> ret = new ArrayList<>();
        for(Libro l : libreria.values())
        {   if(l.getTitolo().toLowerCase().contains(r) || l.getAutore().toLowerCase().contains(r))
                ret.add(l);
        }
        return ret;
    }






}



