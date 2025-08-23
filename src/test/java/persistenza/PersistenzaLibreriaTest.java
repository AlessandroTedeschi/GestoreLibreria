package persistenza;

import libreria.Genere;
import libreria.Libreria;
import libreria.Libro;
import libreria.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenzaLibreriaTest
{
    @TempDir
    Path tempDir;

    private Path storePath;

    @BeforeEach
    void setUp() throws Exception {
        //ogni test userà un file json diverso nella cartella temporanea (ogni test è isolato)
        storePath = tempDir.resolve("libreria.json");
        // Reset del Singleton via reflection, per isolare i test
        resetSingleton();
    }

    //resetta il Singleton
    private static void resetSingleton() throws Exception {
        Field f = PersistenzaLibreria.class.getDeclaredField("INSTANCE");   //recupera la singola istanza di LibreriaPersistenza
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    @DisplayName("prima save() e poi load() ricostruisce i libri correttamente")
    void cicloSalvaCarica() throws Exception {
        //creazione e salvataggio della Libreria su file json
        PersistenzaLibreria repo = PersistenzaLibreria.getInstance(storePath);
        Libreria l1 = new Libreria();
        Libro a = new Libro.Builder("La Coscienza di Zeno","Italo Svevo", "123",Genere.ROMANZO).build();
        Libro b = new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build();
        l1.aggiungiLibro(a);
        l1.aggiungiLibro(b);
        repo.save(l1);

        //ricarico libreria
        resetSingleton();   //in modo tale che quando vado a ricaricare non utilizza l'istanza precedente
        PersistenzaLibreria repo2 = PersistenzaLibreria.getInstance(storePath);
        Libreria l2 = repo2.load();

        assertEquals(2, l2.numeroLibri(), "Devono esserci 2 libri dopo il load");
        //verifica corretto caricamento dei dati
        Libro a2 = l2.trovaLibro("123");
        assertEquals("La Coscienza di Zeno", a2.getTitolo());
        assertEquals("Italo Svevo", a2.getAutore());
        assertEquals(Genere.ROMANZO, a2.getGenere());
        assertEquals(StatoLettura.DA_LEGGERE, a2.getStatoLettura(), "Default atteso");
        assertEquals(0, a2.getValutazione(), "Default atteso");

        Libro b2 = l2.trovaLibro("456");
        assertEquals("Divina Commedia", b2.getTitolo());
        assertEquals(StatoLettura.LETTO, b2.getStatoLettura());
        assertEquals(5, b2.getValutazione());
    }


    @Test
    @DisplayName("load() su file json inesistente restituisce una Libreria vuota")
    void loadSuFileInesistente() throws Exception {
        assertFalse(Files.exists(storePath));
        PersistenzaLibreria repo = PersistenzaLibreria.getInstance(storePath);
        Libreria l = repo.load();
        assertEquals(0, l.numeroLibri());
    }

    @Test
    @DisplayName("Un secondo save() sovrascrive il contenuto precedente con JSON valido")
    void sovrascritturaLibreriaNuova() throws Exception {
        PersistenzaLibreria repo = PersistenzaLibreria.getInstance(storePath);

        //salvataggio prima libreria con un libro
        Libreria l1 = new Libreria();
        l1.aggiungiLibro(new Libro.Builder("La Coscienza di Zeno","Italo Svevo", "123",Genere.ROMANZO).build());
        repo.save(l1);
        String firstJson = Files.readString(storePath, StandardCharsets.UTF_8);
        assertTrue(firstJson.contains("123"));

        //salvataggio seconda libreria con 2 libri (deve sostituire il file)
        Libreria l2 = new Libreria();
        l2.aggiungiLibro(new Libro.Builder("Divina Commedia", "Dante Alighieri", "456", Genere.POESIA)
                .statoLettura(StatoLettura.LETTO)
                .valutazione(5)
                .build());
        l2.aggiungiLibro(new Libro.Builder("Uno, Nessuno e Centomila", "Pirandello", "789", Genere.ROMANZO).build());
        repo.save(l2);

        String secondJson = Files.readString(storePath, StandardCharsets.UTF_8);
        assertFalse(secondJson.contains("123"), "Il vecchio contenuto non deve restare, è stato sovrascritto");
        assertTrue(secondJson.contains("456"));
        assertTrue(secondJson.contains("789"));

        //verifica
        resetSingleton();
        PersistenzaLibreria repo2 = PersistenzaLibreria.getInstance(storePath);
        Libreria loaded = repo2.load();
        assertEquals(2, loaded.numeroLibri());
        assertDoesNotThrow(() -> loaded.trovaLibro("456"));
        assertDoesNotThrow(() -> loaded.trovaLibro("789"));
    }

    @Test
    @DisplayName("load() con JSON corrotto lancia IOException")
    void loadJsonCorrotto() throws Exception {
        // Scrivo contenuto non-JSON nel file
        Files.writeString(storePath, "QUESTO NON È JSON", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        PersistenzaLibreria repo = PersistenzaLibreria.getInstance(storePath);
        assertThrows(IOException.class, repo::load, "JSON non valido dovrebbe causare IOException");
    }

    @Test
    @DisplayName("save(null) lancia NullPointerException")
    void saveNullLanciaEccezione() throws Exception {
        PersistenzaLibreria repo = PersistenzaLibreria.getInstance(storePath);
        assertThrows(NullPointerException.class, () -> repo.save(null));
    }

}
