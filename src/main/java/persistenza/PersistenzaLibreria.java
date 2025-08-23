package persistenza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import libreria.Libreria;
import libreria.Libro;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PersistenzaLibreria
{
    //Singleton
    private static volatile PersistenzaLibreria INSTANCE;

    public static PersistenzaLibreria getInstance(Path filePath) {
        Objects.requireNonNull(filePath, "filePath nullo");
        if (INSTANCE == null) {
            synchronized (PersistenzaLibreria.class) {  //serve per garantire che più thread non creino più istanze contemporaneamente
                if (INSTANCE == null)
                    INSTANCE = new PersistenzaLibreria(filePath);
            }
        }
        return INSTANCE;
    }

    private final Path filePath;
    private final Gson gson;

    private PersistenzaLibreria(Path filePath) {
        this.filePath = filePath.toAbsolutePath();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    //Salva la libreria come JSON
    public synchronized void save(Libreria libreria) throws IOException {
        Objects.requireNonNull(libreria, "La libreria non può essere null");
        Collection<Libro> books = libreria.getLibri();
        List<LibroDTO> dto = new ArrayList<>(books.size());
        for (Libro l : books)
            dto.add(LibroDTO.fromDomain(l));
        Files.createDirectories(filePath.getParent());

        //creazione file temporaneo. Se salvassi direttamente sul file json principale, in caso si crash
        //esso rimarrebbe corrotto a metà
        Path tmp = Files.createTempFile(filePath.getParent(), "libreria-", ".json.tmp");
        try (BufferedWriter w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            gson.toJson(dto, w);
        }
        Files.move(tmp, filePath,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
    }

    //Carica la libreria da JSON. Se il file non esiste → libreria vuota
    public synchronized Libreria load() throws IOException {
        Libreria libreria = new Libreria();
        if (!Files.exists(filePath))
            return libreria;

        Type listType = new TypeToken<List<LibroDTO>>(){}.getType();
        try (BufferedReader r = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            List<LibroDTO> dtoList = gson.fromJson(r, listType);
            if (dtoList == null)
                return libreria;
            for (LibroDTO dto : dtoList) {
                libreria.aggiungiLibro(dto.toDomain());
            }
        } catch (JsonParseException e) {
            throw new IOException("JSON non valido: " + filePath, e);
        }
        return libreria;
    }

    /* Il "synchronized" serve per non permettere a due thread di effettuare un'operazione contemporaneamente sullo stesso file json,
        evitando inconsistenza. E' stata una scelta "riservata per il futuro": nonostante l'applicazione è single-thread, è già pronta
        ad un cambiamento in multi-thread a livello di persistenza
    */


}
