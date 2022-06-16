import com.alibaba.fastjson.JSONObject;
import engine.Graph;
import engine.Operator;
import engine.OperatorConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiewenwu
 */
public class Test {
    public static class FakeOperator extends Operator {
        public FakeOperator() {
            setName("FakeOperator");
        }

        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {

        }
    }

    public static class FakeOperator1 extends Operator {
        public FakeOperator1() {
            setName("FakeOperator1");
        }

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
        public FakeOperator2() {
            setName("FakeOperator2");
        }

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
        public FakeOperator3() {
            setName("FakeOperator3");
        }

        @Override
        public void invoke() {
            System.out.println("invoke: " + this.getClass().getSimpleName());
        }

        @Override
        public void register() {
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Map<String, Class<? extends Operator>> map = new HashMap<>();
        map.put("FakeOperator", FakeOperator.class);
        map.put("FakeOperator1", FakeOperator1.class);
        map.put("FakeOperator2", FakeOperator2.class);
        map.put("FakeOperator3", FakeOperator3.class);

        List<OperatorConfig> operatorConfigs = new ArrayList<>();
        OperatorConfig operatorConfig = new OperatorConfig();
        operatorConfig.setName("FakeOperator");
        operatorConfigs.add(operatorConfig);

        operatorConfig = new OperatorConfig();
        operatorConfig.setName("FakeOperator1");
        operatorConfigs.add(operatorConfig);

        operatorConfig = new OperatorConfig();
        operatorConfig.setName("FakeOperator2");
        operatorConfigs.add(operatorConfig);

        operatorConfig = new OperatorConfig();
        operatorConfig.setName("FakeOperator3");
        operatorConfigs.add(operatorConfig);

        Graph graph = new Graph();
        graph.setOperatorConfigs(operatorConfigs);
        graph.setClassMap(map);

        System.out.println(JSONObject.toJSONString(graph));
        graph.build();
        System.out.println(JSONObject.toJSONString(graph));
        graph.start();

        Thread.sleep(10000);
        graph.stop();

    }
}