import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FSNode implements Serializable {
    private String path;
    private long last_modified;
    private boolean isDir;
    public FSNode(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(json, FSNode.class);
    }

}
