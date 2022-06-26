# GraphEngine
    支持依赖注入
    支持有向无环图的构建运行，DFS打印图，环检测
    支持图缓存池，一次构图多次重复使用，对于超大图可减少由构图产生的时间及内存开销
    算子即是数据载体又是计算逻辑，多路径并行执行，无锁，无需异步同步等待，上游算子运行完毕立即级联触发下游依赖算子

source: 

    [TestOperator3(0, 3), TestOperator1(0, 3)]

process: 

    [TestOperator7(1, 1), TestOperator2(2, 1), TestOperator4(4, 1), TestOperator5(2, 2)]

sink: 

    [TestOperator6(2, 0)]

graph:

    start -> TestOperator3(0, 3) -> TestOperator6(2, 0) -> end
                                 -> TestOperator7(1, 1) -> TestOperator5(2, 2) -> TestOperator2(2, 1) -> TestOperator4(4, 1) -> TestOperator6(2, 0) -> end
                                                                               -> TestOperator4(4, 1) -> TestOperator6(2, 0) -> end
                                 -> TestOperator4(4, 1) -> TestOperator6(2, 0) -> end

    start -> TestOperator1(0, 3) -> TestOperator2(2, 1) -> TestOperator4(4, 1) -> TestOperator6(2, 0) -> end
                                 -> TestOperator4(4, 1) -> TestOperator6(2, 0) -> end
                                 -> TestOperator5(2, 2) -> TestOperator2(2, 1) -> TestOperator4(4, 1) -> TestOperator6(2, 0) -> end
                                                        -> TestOperator4(4, 1) -> TestOperator6(2, 0) -> end

test code:

    import engine.*;
    import lombok.Getter;
    
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    
    /**
     * @author xiewenwu
     */
    public class Test {
        public static class TestOperator1 extends Operator<String> {
            String output;
    
            @Override
            public void clean() {
                output = null;
            }
    
            @Override
            public void invoke(String value) {
                output = value;
                System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + output);
            }
    
            @Override
            public void register() {
            }
        }
    
        public static class TestOperator2 extends Operator<String> {
            String output;
    
            @Override
            public void clean() {
                output = null;
            }
    
            @Override
            public void invoke(String value) {
                output = value;
                System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + output);
            }
    
            @Override
            public void register() {
                dependOn("TestOperator5");
                dependOn("TestOperator1");
            }
        }
    
        public static class TestOperator3 extends Operator<String> {
            @Getter
            String output;
    
            @Override
            public void clean() {
                output = null;
            }
    
            @Override
            public void invoke(String value) {
                output = value;
                System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + output);
            }
    
            @Override
            public void register() {
            }
        }
    
        public static class TestOperator4 extends Operator<String> {
            String output;
    
            @Override
            public void clean() {
                output = null;
            }
    
            @Override
            public void invoke(String value) {
                output = value;
                System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + output);
            }
    
            @Override
            public void register() {
                dependOn("TestOperator2");
                dependOn("TestOperator1");
                dependOn("TestOperator5");
                dependOn("TestOperator3");
            }
        }
    
        public static class TestOperator5 extends Operator<String> {
            String output;
    
            @Override
            public void clean() {
                output = null;
            }
    
            @Override
            public void invoke(String value) {
                output = value;
                System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + testOperator7.outputString);
            }
    
            TestOperator7 testOperator7;
            @Override
            public void register() {
                dependOn("TestOperator1");
                testOperator7 = dependOn("TestOperator7");
            }
        }
    
        public static class TestOperator6 extends Operator<String> {
            String output;
    
            @Override
            public void clean() {
                output = null;
            }
    
            @Override
            public void invoke(String value) {
                output = value;
                System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + output);
            }
    
            @Depend(name = "TestOperator4")
            TestOperator4 testOperator4;
    
            @Override
            public void register() {
                dependOn("TestOperator3");
            }
        }
    
        public static class TestOperator7 extends Operator<String> {
            @OutPut(value = "testOutPutString")
            String outputString;
    
            @OutPut
            byte outPutByte;
    
            @OutPut
            int outPutInt;
    
            @OutPut
            float outPutFloat;
    
            @OutPut
            Double outPutDouble;
    
            @OutPut
            char outPutChar;
    
            @OutPut
            Boolean outPutBoolean;
    
            @Override
            public void invoke(String value) {
                System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + outputString);
                outputString = testOperator3.getOutput();
            }
    
            @Depend(name = "TestOperator3")
            TestOperator3 testOperator3;
        }
    
        public static void main(String[] args) throws Exception {
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
    
            GraphPool graphPool = new GraphPool(classMap, nodeConfigs);
            Graph graph = graphPool.getResource();
    
            System.out.println("source:  " + graph.getSourceNodes());
            System.out.println("process: " + graph.getProcessNodes());
            System.out.println("sink:    " + graph.getSinkNodes());
            System.out.println(graph.toString());
    
            graph.run("test");
            graphPool.getResource().run("test1");
            graphPool.getResource().run("test2");
            graphPool.getResource().run("test3");
    
            Thread.sleep(1000);
    
            Executor.stop();
        }
    }
    