package dobby.motor.enlazador;

import java.io.IOException;
import java.nio.file.Path;

public interface ProveedorCodigo {
    String leer(Path ruta) throws IOException;
}
