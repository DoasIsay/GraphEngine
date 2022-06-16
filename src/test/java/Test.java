import com.alibaba.fastjson.JSONObject;
import engine.*;
import engine.Node;
import engine.NodeConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiewenwu
 */
public class Test {
    public static class FakeOperator extends Operator {

        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {

        }
    }

    public static class FakeOperator1 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }


        @Override
        public void register() {
            dependOn("FakeOperator");
        }
    }

    public static class FakeOperator2 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }


        @Override
        public void register() {
            dependOn("FakeOperator1");
            dependOn("FakeOperator3");
        }
    }

    public static class FakeOperator3 extends Operator {
        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {
            dependOn("FakeOperator1");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Map<String, Class<? extends Operator>> map = new HashMap<>();
        map.put("FakeOperator", FakeOperator.class);
        map.put("FakeOperator1", FakeOperator1.class);
        map.put("FakeOperator2", FakeOperator2.class);
        map.put("FakeOperator3", FakeOperator3.class);

        List<NodeConfig> nodeConfigs = new ArrayList<>();
        NodeConfig nodeConfig = new NodeConfig();
        nodeConfig.setName("FakeOperator");
        nodeConfig.setOperator("FakeOperator");
        nodeConfigs.add(nodeConfig);

        nodeConfig = new NodeConfig();
        nodeConfig.setName("FakeOperator1");
        nodeConfig.setOperator("FakeOperator1");
        nodeConfigs.add(nodeConfig);

        nodeConfig = new NodeConfig();
        nodeConfig.setName("FakeOperator2");
        nodeConfig.setOperator("FakeOperator2");
        nodeConfigs.add(nodeConfig);

        nodeConfig = new NodeConfig();
        nodeConfig.setName("FakeOperator3");
        nodeConfig.setOperator("FakeOperator3");
        nodeConfigs.add(nodeConfig);

        Graph graph = new Graph();
        graph.setNodeConfigs(nodeConfigs);
        graph.setClassMap(map);

        System.out.println(JSONObject.toJSONString(graph));
        graph.build();
        System.out.println(JSONObject.toJSONString(graph));
        graph.start();

        Thread.sleep(10000);
        graph.stop();

    }
}