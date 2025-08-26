package ui;

import libreria.Libro;
import libreria.Libreria;
import ordinamento.SortingStrategy;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class LibreriaMediator {

    private final Libreria model;
    private final MainFrame frame;

    private String currentQuery = "";
    private OpzioniOrdinamento currentSort = OpzioniOrdinamento.ALFABETICO;

    private final Deque<UndoAction> undoStack = new ArrayDeque<>();

    public LibreriaMediator(Libreria model, MainFrame frame) {
        this.model = model;
        this.frame = frame;

        frame.getTopBar().setOnSearchChanged(q -> {
            currentQuery = (q == null) ? "" : q.trim();
            reloadList();
        });
        frame.getTopBar().setOnSortChanged(s -> {
            currentSort = (s == null) ? OpzioniOrdinamento.ALFABETICO : s;
            reloadList();
        });
        frame.getTopBar().setOnAdd(this::onAdd);

        frame.getBookList().setOnSelectionChanged(isbn -> updateButtonsState());

        frame.getBottomBar().setOnModifica(this::onEdit);
        frame.getBottomBar().setOnRimuovi(this::onRemove);
        frame.getBottomBar().setOnAnnulla(this::onUndo);

        // primo caricamento: tutti i libri
        reloadList();
    }

    /* ========================= OPERAZIONI ========================= */

    public void reloadList() {
        try {
            List<Libro> lista;

            if (currentQuery == null || currentQuery.isBlank()) {
                // TUTTI I LIBRI (niente ricerca vuota)
                lista = tuttiLibri();
            } else {
                // SOLO RISULTATI della ricerca
                lista = model.cercaLibro(currentQuery);
            }

            // Ordinamento lato UI
            Comparator<Libro> cmp = switch (currentSort) {
                case ALFABETICO -> Comparator.comparing(Libro::getTitolo, String.CASE_INSENSITIVE_ORDER);
                case VALUTAZIONE_CRESCENTE -> Comparator.comparingInt(Libro::getValutazione)
                        .thenComparing(Libro::getTitolo, String.CASE_INSENSITIVE_ORDER);
                case VALUTAZIONE_DECRESCENTE -> Comparator.comparingInt(Libro::getValutazione).reversed()
                        .thenComparing(Libro::getTitolo, String.CASE_INSENSITIVE_ORDER);
            };
            lista = lista.stream().sorted(cmp).collect(Collectors.toList());

            frame.getBookList().setBooks(lista);
            updateButtonsState();

        } catch (IllegalArgumentException ex) {
            // eventuale “Barra di ricerca vuota” o simili → fallback a tutti
            frame.getBookList().setBooks(tuttiLibri());
            updateButtonsState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Errore nel caricamento: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Libro> tuttiLibri() {
        return model.getLibri();
    }

    private void onAdd() {
        BookFormDialog.Result res = BookFormDialog.showNew(frame);
        if (res == null || !res.confirmed() || res.libro() == null) return;

        try {
            Libro nuovo = res.libro();

            // TODO (se vuoi usare il tuo Command): new AggiungiLibriCommand(model, List.of(nuovo)).execute();
            model.aggiungiLibro(nuovo);

            undoStack.push(UndoAction.added(nuovo));

            currentQuery = "";                   // svuota filtro → mostra tutti
            frame.getTopBar().setSearchText("");
            reloadList();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Aggiunta fallita: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        String isbn = frame.getBookList().getSelectedIsbn();
        if (isbn == null) return;

        Libro sel;
        try { sel = model.trovaLibro(isbn); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Selezione non valida: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BookFormDialog.Result res = BookFormDialog.showEdit(frame, sel);
        if (!res.confirmed() || res.libro() == null) return;

        try {
            // strategia semplice: rimuovi → aggiungi (ISBN invariato)
            model.rimuoviLibro(sel.getISBN());
            model.aggiungiLibro(res.libro());

            // (di default non metto la modifica nello stack undo; se vuoi la gestiamo)
            reloadList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Modifica fallita: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemove() {
        String isbn = frame.getBookList().getSelectedIsbn();
        if (isbn == null) return;

        Libro daRimuovere;
        try { daRimuovere = model.trovaLibro(isbn); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Libro non trovato: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(frame,
                "Eliminare \"" + daRimuovere.getTitolo() + "\"?", "Conferma",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            // TODO (se vuoi usare il tuo Command): new RimuoviLibroCommand(model, isbn).execute();
            boolean removed = model.rimuoviLibro(isbn);
            if (removed) {
                undoStack.push(UndoAction.removed(daRimuovere));
                reloadList();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Eliminazione fallita: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUndo() {
        if (undoStack.isEmpty()) return;
        UndoAction action = undoStack.pop();
        try {
            switch (action.type) {
                case ADD -> model.rimuoviLibro(action.libro.getISBN()); // annulla un'aggiunta
                case REMOVE -> model.aggiungiLibro(action.libro);       // annulla una rimozione
            }
            reloadList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Annulla fallito: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateButtonsState() {
        boolean hasSel = frame.getBookList().getSelectedIsbn() != null;
        boolean canUndo = !undoStack.isEmpty();
        frame.getBottomBar().setButtonsEnabled(hasSel, hasSel, canUndo);
    }

    /* -------------------- Undo support -------------------- */
    private enum Type { ADD, REMOVE }
    private static class UndoAction {
        final Type type; final Libro libro;
        UndoAction(Type t, Libro l){ type = t; libro = Objects.requireNonNull(l); }
        static UndoAction added(Libro l){ return new UndoAction(Type.ADD, l); }
        static UndoAction removed(Libro l){ return new UndoAction(Type.REMOVE, l); }
    }
}

