package engine;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author xiewenwu
 */

@Getter
@Setter
public class Graph {
    Map<String, Class<? extends Operator>> classMap = new HashMap<>();
    Map<String, Operator> operatorMap = new HashMap<>();
    Set<Operator> deadOperator = new HashSet<>();
    Set<Operator> sourceOperator = new HashSet<>();
    Set<Operator> processOperator = new HashSet<>();
    Set<Operator> sinkOperator = new HashSet<>();
    List<OperatorConfig> operatorConfigs = Collections.emptyList();

    public Graph() {
    }

    public void addOperator(Operator operator) {
        operatorMap.put(operator.getName(), operator);
    }

    void transform() {
        for (OperatorConfig config : operatorConfigs) {
            String name = config.getName();
            try {
                addOperator(classMap.get(name).newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    void perf() {
        Iterator<Map.Entry<String, Operator>> iterator = operatorMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Operator> entry = iterator.next();
            Operator operator = entry.getValue();
            List<Operator> consumers = operator.getConsumers();

            if (operator.getDepends() == 0 && (consumers == null || consumers.isEmpty())) {
                iterator.remove();
                deadOperator.add(operator);
                continue;
            }

            if (operator.getDepends() == 0) {
                sourceOperator.add(operator);
                continue;
            }

            if (consumers == null || consumers.isEmpty()) {
                sinkOperator.add(operator);
                continue;
            }

            processOperator.add(operator);
        }
    }

    void generate() {
        operatorMap.values().forEach(operator -> {
            operator.setEngine(this);
            operator.register();
        });
    }

    public void build() {
        transform();
        generate();
        perf();
    }

    public void start() {
        sourceOperator.forEach(operator -> {
            Executor.execute(operator);
        });
    }

    public static void stop() {
        Executor.stop();
    }

    public <T extends Operator> T getOperator(String name) {
        return (T) operatorMap.get(name);
    }
}