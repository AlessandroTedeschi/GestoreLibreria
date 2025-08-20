package libreria;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Libreria
{
    private final Map<String, Libro> libreria = new LinkedHashMap<>();

    public int numeroLibri()
    {   return libreria.size();
    }

    public void aggiungi(Libro l)
    {   if(l == null)
            throw new NullPointerException("Il libro non può essere vuoto");
        String isbn = l.getISBN();  //nessun controllo su isbn null o black, poichè viene fatto già dal costruttore del libro
        if(libreria.containsKey(isbn))
            throw new IllegalStateException("Il libro con codice " + isbn + " è già presente nella libreria");
        libreria.put(isbn, l);
    }

    public boolean rimuovi(String isbn)
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
        if(!libreria.containsKey(isbn))
            throw new NoSuchElementException("Il libro con codice " + isbn + " non è presente nella libreria");
        return libreria.get(isbn);
    }

    public void aggiornaStatoLettura(String isbn, StatoLettura s)
    {  if(isbn == null)
            throw new NullPointerException("ISBN non valido");
        if(isbn.isBlank())
            throw new IllegalArgumentException("ISBN non valido");
        if(s==null)
            throw new NullPointerException("Stato lettura nullo");
        Libro l = libreria.get(isbn);
        if(l == null)
            throw new NoSuchElementException("Nessun libro con codice " + isbn);
        l.aggiornaStatoLettura(s);
    }

    public void aggiornaValutazione(String isbn, int val)
    {   if(isbn==null || isbn.isBlank())
            throw new IllegalArgumentException("Codice ISBN non valido");
        Libro l = libreria.get(isbn);
        if(l==null)
            throw new NoSuchElementException("Nessun libro con codice " + isbn);
        l.aggiornaValutazione(val);
    }





}



