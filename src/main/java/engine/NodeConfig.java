package engine;

import lombok.Data;

import java.util.Map;

/**
 * @author xiewenwu
 */

@Data
public class NodeConfig {
    boolean async;
    String name;
    String operator;
    Map<String, Object> config;
}
