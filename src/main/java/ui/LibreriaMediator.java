package ui;

import command.*;
import libreria.Genere;
import libreria.Libro;
import libreria.Libreria;
import libreria.StatoLettura;
import ordinamento.OrdinamentoAlfabeticoTitolo;
import ordinamento.OrdinamentoValutazioneCrescente;
import ordinamento.OrdinamentoValutazioneDecrescente;
import ordinamento.SortingStrategy;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class LibreriaMediator {

    private final Libreria model;
    private final MainFrame frame;
    private final GestoreComandi invoker;

    private StatoLettura filtroStato = null;
    private Genere filtroGenere = null;
    private Integer filtroValMin = null;

    private String currentQuery = "";
    private OpzioniOrdinamento currentSort = OpzioniOrdinamento.ALFABETICO;

    public LibreriaMediator(Libreria model, MainFrame frame, GestoreComandi invoker) {
        this.model = model;
        this.frame = frame;
        this.invoker = invoker;

        //chiamata quando cambia il campo di ricerca
        frame.getBarraSuperiore().setOnSearchChanged(q -> {
            currentQuery = (q == null) ? "" : q.trim();
            reloadList();
        });

        //chiamata quando cambia il campo di ordinamento
        frame.getBarraSuperiore().setOnSortChanged(s -> {
            currentSort = (s == null) ? OpzioniOrdinamento.ALFABETICO : s;
            reloadList();
        });

        //eventi della barra filtri
        frame.getBarraFiltri().setOnStatoChanged(s -> { filtroStato = s; reloadList(); });
        frame.getBarraFiltri().setOnGenereChanged(g -> { filtroGenere = g; reloadList(); });
        frame.getBarraFiltri().setOnValutazioneChanged(v -> { filtroValMin = v; reloadList(); });
        frame.getBarraFiltri().setOnClear(() -> {
            filtroStato = null; filtroGenere = null; filtroValMin = null;
            reloadList();
        });

        //quando viene cliccato il pulsante aggiungi viene chiamtato onAdd() per aggiungere un libro
        frame.getBarraSuperiore().setOnAdd(this::onAdd);

        //serve per attivare/disattivare i bottoni modifica e rimuovi quando si seleziona/deseleziona un libro
        frame.getListaLibri().setOnSelectionChanged(isbn -> updateButtonsState());

        //collega i bottoni rimuovi, modifica, annulla della barra dei bottoni alle rispettive azioni dichiarate in questa classe
        frame.getBarraDeiBottoni().setOnModifica(this::onEdit);
        frame.getBarraDeiBottoni().setOnRimuovi(this::onRemove);
        frame.getBarraDeiBottoni().setOnAnnulla(this::onUndo);

        //quando viene avviata l'applicazione vengono caricati tutti i libri
        reloadList();
    }


    //ricarica i dati salvati nella tabella dei libri ogni volta che un'azione dell'utente lo richiede
    public void reloadList() {
        try {
            List<Libro> lista;

            //vengono caricati i libri che soddisfano la ricerca dell'utente nella lista
            if (currentQuery == null || currentQuery.isBlank()) {
                lista = tuttiLibri();
            } else {
                lista = model.cercaLibro(currentQuery);
            }

            //ricarica i libri applicando i filtri scelti dall'utente
            if (filtroStato != null) {
                lista = lista.stream().filter(l -> l.getStatoLettura() == filtroStato).toList();
            }
            if (filtroGenere != null) {
                lista = lista.stream().filter(l -> l.getGenere() == filtroGenere).toList();
            }
            if (filtroValMin != null) {
                final int min = filtroValMin;
                lista = lista.stream().filter(l -> l.getValutazione() == min).toList();
            }

            //ricarica i libri in base all'ordinamento scelto dall'utente nella lista
            SortingStrategy strategy = switch (currentSort) {
                case ALFABETICO -> new OrdinamentoAlfabeticoTitolo();
                case VALUTAZIONE_CRESCENTE -> new OrdinamentoValutazioneCrescente();
                case VALUTAZIONE_DECRESCENTE -> new OrdinamentoValutazioneDecrescente();
            };
            lista = lista.stream().sorted(strategy).collect(Collectors.toList());

            //aggiorna l'interfaccia grafica, mostrando all'uente solo i libri contenuti nella lista (quelli che soddisfano la ricerca)
            //o lo specifico ordinamento scelto
            frame.getListaLibri().setBooks(lista);
            updateButtonsState();

        }
        catch (IllegalArgumentException ex) {
            frame.getListaLibri().setBooks(tuttiLibri());
            updateButtonsState();
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Errore nel caricamento: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Libro> tuttiLibri() {
        return model.getLibri();
    }


    //metodo chiamato alla pressione del bottone aggiungi
    private void onAdd() {

        //viene mostrata la finestra di dialogo per inserire un nuovo libro
        BookFormDialog.Result res = BookFormDialog.showNew(frame);

        //se l'utente non ha premuto "OK" per salvare il libro non viene salvato nulla
        if (res == null || !res.confirmed() || res.libro() == null) return;

        try {
            //il libro appena "compilato" nella finestra di dialogo viene aggiunto
            Libro nuovo = res.libro();
            UndoableCommand cmd = new AggiungiLibroCommand(nuovo, model);
            invoker.esegui(cmd);

            //viene ricaricata la lista
            currentQuery = "";
            frame.getBarraSuperiore().setSearchText("");
            reloadList();
            //updateButtonsState();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Aggiunta fallita: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    //metodo chiamato alla pressione del bottone modifica
    private void onEdit() {
        String isbn = frame.getListaLibri().getSelectedIsbn();
        if (isbn == null) return;

        //recupera dalla lista di Libri quello corrispondente all'isbn
        Libro vecchioLibrio;
        try { vecchioLibrio = model.trovaLibro(isbn); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Selezione non valida: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //viene aperta la finestra di dialogo uguale a quella del bottone aggiungi con i campi precompilati del libro selezionato
        BookFormDialog.Result res = BookFormDialog.showEdit(frame, vecchioLibrio);
        if (!res.confirmed() || res.libro() == null) return;

        final Libro nuovoLibro = res.libro();

        //se non è cambiato nulla l'istanza rimane quella già presente
        boolean same =
                java.util.Objects.equals(vecchioLibrio.getTitolo(), nuovoLibro.getTitolo()) &&
                        java.util.Objects.equals(vecchioLibrio.getAutore(), nuovoLibro.getAutore()) &&
                        vecchioLibrio.getGenere() == nuovoLibro.getGenere()      &&
                        vecchioLibrio.getStatoLettura() == nuovoLibro.getStatoLettura()&&
                        vecchioLibrio.getValutazione() == nuovoLibro.getValutazione();
        if (same) return;

        //viene aggiornata la lista con il nuovo libro (in realtà non viene direttamente modificata
        //l'istanza del libro, ma viene eliminata quella vecchia e aggiunta una nuova istanza con i nuovi campi
        try {
            Command cmd = new ModificaLibroCommand(model, vecchioLibrio.getISBN(), nuovoLibro);
            cmd.execute();
            reloadList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Modifica fallita: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    //metodo chiamato alla pressione del bottone rimuovi
    private void onRemove() {
        String isbn = frame.getListaLibri().getSelectedIsbn();
        if (isbn == null) return;

        //recupera dalla lista di Libri quello corrispondente all'isbn
        Libro daRimuovere;
        try { daRimuovere = model.trovaLibro(isbn); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Libro non trovato: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //richiesta di conferma all'utente
        int ok = JOptionPane.showConfirmDialog(frame,
                "Eliminare \"" + daRimuovere.getTitolo() + "\"?", "Conferma",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        //rimuove il libro e aggiorna la lista dei libri
        try {
            UndoableCommand cmd = new RimuoviLibroCommand(isbn, model);
            invoker.esegui(cmd);
            reloadList();
            //updateButtonsState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Eliminazione fallita: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    //metodo chiamato alla pressione del bottone annulla
    private void onUndo() {
        if (!invoker.hasUndo()) return;
        try {
            invoker.annullaUltimoComando();
            reloadList();
            //updateButtonsState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Annulla fallito: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateButtonsState() {
        boolean hasSel = frame.getListaLibri().getSelectedIsbn() != null;
        boolean canUndo = invoker.hasUndo();
        frame.getBarraDeiBottoni().setButtonsEnabled(hasSel, hasSel, canUndo);
    }
}

