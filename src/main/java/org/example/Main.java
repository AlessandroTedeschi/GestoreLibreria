package org.example;

import command.AggiungiLibroCommand;
import command.RimuoviLibroCommand;
import command.AggiornaStatoLetturaCommand;
import command.AggiornaValutazioneCommand;
import command.GestoreComandi;
import libreria.Genere;
import libreria.Libreria;
import libreria.Libro;
import libreria.StatoLettura;

import java.util.*;

public class Main {

    private static final Scanner IN = new Scanner(System.in);
    private static final Libreria LIBRERIA = new Libreria();
    private static final GestoreComandi GESTORE = new GestoreComandi();

    public static void main(String[] args) {
        Locale.setDefault(Locale.ITALY);
        System.out.println("=== Gestione Libreria Personale (CLI) ===");
        boolean running = true;
        while (running) {
            try {
                stampaMenu();
                System.out.print("> ");
                String scelta = IN.nextLine().trim().toLowerCase(Locale.ROOT);
                switch (scelta) {
                    case "1": aggiungiLibro(); break;
                    case "2": rimuoviLibro(); break;
                    case "3": aggiornaStato(); break;
                    case "4": aggiornaValutazione(); break;
                    case "5": cerca(); break;
                    case "6": stampaTutti(); break;
                    case "7": annullaUltimo(); break;
                    case "0": running = false; break;
                    default: System.out.println("Scelta non valida.");
                }
            } catch (Exception e) {
                // Non uccidiamo il loop: mostriamo l'errore in modo amichevole
                System.out.println("Errore: " + e.getMessage());
            }
            System.out.println();
        }
        System.out.println("Arrivederci!");
    }

    private static void stampaMenu() {
        System.out.println(
                "1) Aggiungi libro\n" +
                        "2) Rimuovi libro\n" +
                        "3) Aggiorna stato lettura\n" +
                        "4) Aggiorna valutazione\n" +
                        "5) Cerca (titolo/autore)\n" +
                        "6) Elenca tutti\n" +
                        "7) Annulla ultimo comando\n" +
                        "0) Esci"
        );
    }

    // --- Azioni ---

    private static void aggiungiLibro() {
        System.out.println("[Aggiungi libro]");
        String titolo = prompt("Titolo");
        String autore = prompt("Autore");
        String isbn = prompt("ISBN");
        Genere genere = promptGenere();
        StatoLettura stato = promptStatoLettura(true); // opzionale: invio vuoto = default in dominio
        Integer valutazione = promptValutazione(true); // opzionale

        Libro.Builder b = new Libro.Builder(titolo, autore, isbn, genere);
        if (stato != null) b.statoLettura(stato);
        if (valutazione != null) b.valutazione(valutazione);
        Libro libro = b.build();

        GESTORE.esegui(new AggiungiLibroCommand(libro, LIBRERIA));
        System.out.println("✅ Libro aggiunto.");
    }

    private static void rimuoviLibro() {
        System.out.println("[Rimuovi libro]");
        String isbn = prompt("ISBN");
        GESTORE.esegui(new RimuoviLibroCommand(isbn, LIBRERIA));
        System.out.println("✅ Libro rimosso.");
    }

    private static void aggiornaStato() {
        System.out.println("[Aggiorna stato lettura]");
        String isbn = prompt("ISBN");
        StatoLettura stato = promptStatoLettura(false);
        // NOTA: AggiornaStatoLetturaCommand è non-undoable → non finirà nella cronologia
        GESTORE.esegui(new AggiornaStatoLetturaCommand(LIBRERIA, stato, isbn));
        System.out.println("✅ Stato aggiornato.");
    }

    private static void aggiornaValutazione() {
        System.out.println("[Aggiorna valutazione]");
        String isbn = prompt("ISBN");
        int v = promptValutazione(false);
        // Portati allo stato LETTO? Dipende dalle tue regole. Qui non forziamo nulla:
        GESTORE.esegui(new AggiornaValutazioneCommand(LIBRERIA, isbn, v));
        System.out.println("✅ Valutazione aggiornata.");
    }

    private static void cerca() {
        System.out.println("[Cerca]");
        String q = prompt("Testo da cercare (titolo o autore)");
        List<Libro> risultati = LIBRERIA.cercaLibro(q);
        if (risultati.isEmpty()) {
            System.out.println("Nessun risultato.");
        } else {
            System.out.println("Risultati:");
            stampaLista(risultati);
        }
    }

    private static void stampaTutti() {
        System.out.println("[Tutti i libri]");
        List<Libro> tutti = new ArrayList<>(LIBRERIA.getLibri()); // se la tua implementazione non consente vuoto, sostituisci con un getter
        // In alternativa, se hai LIBRERIA.getLibri():
        // List<Libro> tutti = new ArrayList<>(LIBRERIA.getLibri());
        if (tutti.isEmpty()) {
            System.out.println("(nessun libro)");
        } else {
            stampaLista(tutti);
        }
        System.out.println("Totale: " + LIBRERIA.numeroLibri());
    }

    private static void annullaUltimo() {
        System.out.println("[Annulla ultimo comando]");
        try {
            GESTORE.annullaUltimoComando();
            System.out.println("↩️  Ultimo comando annullato.");
        } catch (NoSuchElementException ex) {
            // se la tua implementazione lancia quando non c'è storia; in alternativa può essere un no-op
            System.out.println("Nessun comando da annullare.");
        }
    }

    // --- Prompt helper ---

    private static String prompt(String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = IN.nextLine();
            if (s != null) s = s.trim();
            if (s != null && !s.isBlank()) return s;
            System.out.println("Valore non valido, riprova.");
        }
    }

    private static Genere promptGenere() {
        System.out.println("Genere disponibili: ");
        for (Genere g : Genere.values()) System.out.print(g.name() + " ");
        System.out.println();
        while (true) {
            System.out.print("Genere: ");
            String s = IN.nextLine().trim().toUpperCase(Locale.ROOT);
            try {
                return Genere.valueOf(s);
            } catch (Exception e) {
                System.out.println("Genere non valido, riprova.");
            }
        }
    }

    private static StatoLettura promptStatoLettura(boolean opzionale) {
        System.out.println("Stati: ");
        for (StatoLettura st : StatoLettura.values()) System.out.print(st.name() + " ");
        System.out.println();
        while (true) {
            System.out.print("Stato" + (opzionale ? " (invio per default)" : "") + ": ");
            String s = IN.nextLine().trim();
            if (opzionale && s.isBlank()) return null;
            try {
                return StatoLettura.valueOf(s.toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                System.out.println("Stato non valido, riprova.");
            }
        }
    }

    private static Integer promptValutazione(boolean opzionale) {
        while (true) {
            System.out.print("Valutazione 0..5" + (opzionale ? " (invio per saltare)" : "") + ": ");
            String s = IN.nextLine().trim();
            if (opzionale && s.isBlank()) return null;
            try {
                int v = Integer.parseInt(s);
                if (v < 0 || v > 5) throw new IllegalArgumentException("Fuori intervallo");
                return v;
            } catch (Exception e) {
                System.out.println("Numero non valido, riprova.");
            }
        }
    }

    private static void stampaLista(List<Libro> libri) {
        System.out.printf("%-15s | %-30s | %-20s | %-12s | %-5s%n",
                "ISBN", "Titolo", "Autore", "Stato", "Val");
        System.out.println("-------------------------------------------------------------------------------------------");
        for (Libro l : libri) {
            String stato = l.getStatoLettura() != null ? l.getStatoLettura().name() : "-";
            String val = String.valueOf(l.getValutazione());
            System.out.printf("%-15s | %-30s | %-20s | %-12s | %-5s%n",
                    l.getISBN(), truncate(l.getTitolo(), 30), truncate(l.getAutore(), 20), stato, val);
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
