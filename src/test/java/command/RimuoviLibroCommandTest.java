package command;

import libreria.Genere;
import libreria.Libreria;
import libreria.Libro;
import libreria.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;


import static org.junit.jupiter.api.Assertions.*;

class RimuoviLibroCommandTest
{   private Libreria libreria;
    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();
        libro1 = new Libro.Builder("La Coscienza di Zeno", "Italo Svevo", "123", Genere.ROMANZO).build();
        libro2 = new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build();
    }

    @Test
    @DisplayName("execute() rimuove un libro esistente")
    void executeRimuoveLibroEsistente() {
        libreria.aggiungiLibro(libro1);
        assertEquals(1, libreria.numeroLibri(), "Pre-condizione: libreria con 1 libro");
        RimuoviLibroCommand cmd = new RimuoviLibroCommand(libro1.getISBN(), libreria);
        cmd.execute();
        assertEquals(0, libreria.numeroLibri(), "Dopo execute() il libro deve essere rimosso");
        assertThrows(NoSuchElementException.class, () -> libreria.trovaLibro(libro1.getISBN()),
                "Dopo la rimozione, cercare l'ISBN deve fallire");
        assertFalse(libreria.rimuoviLibro(libro1.getISBN()), "Rimuovere di nuovo non deve fare nulla");
    }

    @Test
    @DisplayName("undo() dopo execute() ripristina il libro rimosso")
    void undoRipristinaLibro() {
        libreria.aggiungiLibro(libro2);
        RimuoviLibroCommand cmd = new RimuoviLibroCommand(libro2.getISBN(), libreria);
        cmd.execute();
        assertEquals(0, libreria.numeroLibri());
        cmd.undo();
        assertEquals(1, libreria.numeroLibri(), "Dopo undo() il libro deve essere ripristinato");
        assertDoesNotThrow(() -> libreria.trovaLibro(libro2.getISBN()));
    }

    @Test
    @DisplayName("undo() è idempotente: chiamato due volte non lancia e non cambia lo stato")
    void doppioUndo() {
        libreria.aggiungiLibro(libro1);
        RimuoviLibroCommand cmd = new RimuoviLibroCommand(libro1.getISBN(), libreria);
        cmd.execute();
        assertEquals(0, libreria.numeroLibri());
        cmd.undo();
        assertEquals(1, libreria.numeroLibri());
        assertDoesNotThrow(cmd::undo, "Il secondo undo() non deve lanciare");
        assertEquals(1, libreria.numeroLibri(), "Il secondo undo() non deve cambiare lo stato");
    }

    @Test
    @DisplayName("execute() dopo un undo() rimuove nuovamente lo stesso libro")
    void executeDopoUndo() {
        libreria.aggiungiLibro(libro2);
        RimuoviLibroCommand cmd = new RimuoviLibroCommand(libro2.getISBN(), libreria);
        cmd.execute();
        assertEquals(0, libreria.numeroLibri(), "Dopo execute() il libro è rimosso");
        cmd.undo();
        assertEquals(1, libreria.numeroLibri(), "Dopo undo() il libro è ripristinato");
        cmd.execute();
        assertEquals(0, libreria.numeroLibri(), "Rieseguendo execute() il libro è nuovamente rimosso");
    }

    @Test
    @DisplayName("execute() con ISBN inesistente lancia NoSuchElementException e non modifica lo stato")
    void executeConIsbnInesistente() {
        libreria.aggiungiLibro(libro1);
        int prima = libreria.numeroLibri();
        RimuoviLibroCommand cmd = new RimuoviLibroCommand("ISBN-NON-PRESENTE", libreria);
        assertThrows(NoSuchElementException.class, cmd::execute,
                "Rimuovere un ISBN inesistente deve lanciare NoSuchElementException");
        assertEquals(prima, libreria.numeroLibri(), "Lo stato della libreria deve rimanere invariato");
    }

    @Test
    @DisplayName("execute() con ISBN null lancia NullPointerException")
    void executeConIsbnNull() {
        libreria.aggiungiLibro(libro1);
        RimuoviLibroCommand cmd = new RimuoviLibroCommand(null, libreria);
        assertThrows(NullPointerException.class, cmd::execute,
                "ISBN null deve causare NullPointerException");
    }

    @Test
    @DisplayName("execute() con ISBN vuoto/blank lancia IllegalArgumentException")
    void executeConIsbnVuoto() {
        libreria.aggiungiLibro(libro1);
        RimuoviLibroCommand cmd1 = new RimuoviLibroCommand("", libreria);
        RimuoviLibroCommand cmd2 = new RimuoviLibroCommand("   ", libreria);
        assertThrows(IllegalArgumentException.class, cmd1::execute, "ISBN vuoto non è valido");
        assertThrows(IllegalArgumentException.class, cmd2::execute, "ISBN blank non è valido");
    }

    @Test
    @DisplayName("undo() senza execute() è un'operazione nulla")
    void undoSenzaExecute() {
        libreria.aggiungiLibro(libro1);
        int prima = libreria.numeroLibri();
        RimuoviLibroCommand cmd = new RimuoviLibroCommand(libro1.getISBN(), libreria);
        assertDoesNotThrow(cmd::undo, "Chiamare undo() senza execute() non deve lanciare");
        assertEquals(prima, libreria.numeroLibri(), "Lo stato non deve cambiare");
    }
}
