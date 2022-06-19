import engine.Graph;
import engine.NodeConfig;
import engine.Operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiewenwu
 */
public class Test {
    public static class TestOperator1 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {
        }
    }

    public static class TestOperator2 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {
            dependOn("TestOperator5");
            dependOn("TestOperator1");
        }
    }

    public static class TestOperator3 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {
        }
    }

    public static class TestOperator4 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {
            dependOn("TestOperator2");
            dependOn("TestOperator1");
            dependOn("TestOperator5");
            dependOn("TestOperator3");
        }
    }

    public static class TestOperator5 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {
            dependOn("TestOperator1");
            dependOn("TestOperator7");
        }
    }

    public static class TestOperator6 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {
            dependOn("TestOperator3");
            dependOn("TestOperator4");
        }
    }

    public static class TestOperator7 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {
            dependOn("TestOperator3");
            //dependOn("TestOperator2");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Map<String, Class<? extends Operator>> classMap = new HashMap<>();
        classMap.put("TestOperator1", TestOperator1.class);
        classMap.put("TestOperator2", TestOperator2.class);
        classMap.put("TestOperator3", TestOperator3.class);
        classMap.put("TestOperator4", TestOperator4.class);
        classMap.put("TestOperator5", TestOperator5.class);
        classMap.put("TestOperator6", TestOperator6.class);
        classMap.put("TestOperator7", TestOperator7.class);

        List<NodeConfig> nodeConfigs = new ArrayList<>();
        NodeConfig nodeConfig = new NodeConfig();
        nodeConfig.setName("TestOperator1");
        nodeConfig.setOperator("TestOperator1");
        nodeConfigs.add(nodeConfig);

        nodeConfig = new NodeConfig();
        nodeConfig.setName("TestOperator2");
        nodeConfig.setOperator("TestOperator2");
        nodeConfigs.add(nodeConfig);

        nodeConfig = new NodeConfig();
        nodeConfig.setName("TestOperator3");
        nodeConfig.setOperator("TestOperator3");
        nodeConfigs.add(nodeConfig);

        nodeConfig = new NodeConfig();
        nodeConfig.setName("TestOperator4");
        nodeConfig.setOperator("TestOperator4");
        nodeConfigs.add(nodeConfig);

        nodeConfig = new NodeConfig();
        nodeConfig.setName("TestOperator5");
        nodeConfig.setOperator("TestOperator5");
        nodeConfigs.add(nodeConfig);

        nodeConfig = new NodeConfig();
        nodeConfig.setName("TestOperator6");
        nodeConfig.setOperator("TestOperator6");
        nodeConfigs.add(nodeConfig);

        nodeConfig = new NodeConfig();
        nodeConfig.setName("TestOperator7");
        nodeConfig.setOperator("TestOperator7");
        nodeConfigs.add(nodeConfig);

        Graph graph = new Graph();
        graph.setClassMap(classMap);
        graph.setNodeConfigs(nodeConfigs);

        graph.build();
        System.out.println("source:  " + graph.getSourceNodes());
        System.out.println("process: " + graph.getProcessNodes());
        System.out.println("sink:    " + graph.getSinkNodes());

        System.out.println(graph.toString());
        graph.start();

        Thread.sleep(10000);
        graph.stop();
    }
}