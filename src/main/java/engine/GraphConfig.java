package engine;

import lombok.Data;

import java.util.List;

/**
 * @author xiewenwu
 */
@Data
public class GraphConfig {
    int timeout;
    List<NodeConfig> nodes;
}
