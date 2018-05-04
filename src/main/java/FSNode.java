import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FSNode implements Serializable {
    private String path;
    private long last_modified;
    private long created;
    private boolean isDir;
}
