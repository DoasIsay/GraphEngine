package engine;

import lombok.Data;

import java.util.Optional;

/**
 * @author xiewenwu
 */

@Data
public abstract class Operator {
    Node node;

    public abstract void invoke();

    public abstract void register();

    public <T extends Operator> T dependOn(String dependName) {
        return (T) Optional.ofNullable(node.dependOn(dependName))
                .map(node -> node.getOperator())
                .orElse(null);
    }

    public String getName() {
        return node.getName();
    }
}
