package ar.edu.ajedrez.core.session;
import java.util.Optional;
/** Puerto de salida. El núcleo no conoce archivos, carpetas ni el formato de guardado. */
public interface IGameRepository {
    Optional<SavedMatch> load();
    void save(SavedMatch match);
}
