package engine;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author xiewenwu
 */
@Data
public class NodeConfig {
    boolean sync;
    int timeout = 10;
    String name;
    String operator;
    List<String> depend = Collections.emptyList();
    Map<String, Object> config = Collections.emptyMap();
}
