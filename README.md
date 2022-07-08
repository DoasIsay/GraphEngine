# GraphEngine
    一种基于依赖数据驱动调度的并发图引擎编程框架，通过分析节点算子间的依赖关系自动推导编排并发任务
    用户只需专注于算子开发，无需关注多线程间复杂的同步通信问题即可完成高效的并发任务编排
    图中节点算子即是数据载体又是计算逻辑，多路径并发执行，无锁，无需同步异步等待，上游算子执行完毕立即驱动调度下游依赖算子运行
    支持有向无环图的构建运行，DFS打印图执行路径，图环检测，图死节点剔除
    支持依赖注入，支持图/算子重用，一次构图即可多次重复使用，对于超大图可减少由构图产生的时间及内存开销
    
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

    import com.google.gson.Gson;
    import engine.*;
    import lombok.Data;
    import lombok.Getter;
    
    import java.io.FileReader;
    import java.util.List;
    
    /**
     * @author xiewenwu
     */
    public class Test {
        @Load(type = "operator")
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
        }
    
        @Load(type = "operator")
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
    
            @Depend(name = "TestOperator1")
            TestOperator1 testOperator1;
            @Depend(name = "TestOperator5")
            TestOperator5 testOperator5;
        }
    
        @Load(type = "operator")
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
        }
    
        @Load(type = "operator")
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
    
            @Depend(name = "TestOperator1")
            TestOperator1 testOperator1;
            @Depend(name = "TestOperator2")
            TestOperator2 testOperator2;
            @Depend(name = "TestOperator3")
            TestOperator3 testOperator3;
            @Depend(name = "TestOperator5")
            TestOperator5 testOperator5;
        }
    
        @Load(type = "operator")
        public static class TestOperator5 extends Operator<String> {
            String output;
    
            @Override
            public void clean() {
                output = null;
            }
    
            @Override
            public void invoke(String value) {
                output = value;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + testOperator7.outputString);
            }
    
            @Depend(name = "TestOperator1")
            TestOperator1 testOperator1;
            @Depend(name = "TestOperator7")
            TestOperator7 testOperator7;
        }
    
        @Load(type = "operator")
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
    
            @Depend(name = "TestOperator3")
            TestOperator3 testOperator3;
            @Depend(name = "TestOperator4")
            TestOperator4 testOperator4;
        }
    
        @Load(type = "operator")
        public static class TestOperator7 extends Operator<String> {
            @Output(value = "testOutPutString")
            String outputString;
    
            @Output
            byte outPutByte;
    
            @Output(value = "1")
            int outPutInt;
    
            @Output
            float outPutFloat;
    
            @Output(value = "1.23")
            Double outPutDouble;
    
            @Output
            char outPutChar;
    
            @Output
            Boolean outPutBoolean;
    
            @Override
            public void invoke(String value) {
                System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + outputString);
                outputString = testOperator3.getOutput();
            }
    
            @Depend(name = "TestOperator3")
            TestOperator3 testOperator3;
        }
    
        @Load(type = "operator")
        public static class TestOperator8 extends Operator<String> {
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
        }
    
        @Data
        static class Config {
            List<NodeConfig> nodes;
        }
    
        public static void main(String[] args) throws Exception {
            Config config = new Gson().fromJson(new FileReader("src/test/java/config.json"), Config.class);
            GraphPool graphPool = new GraphPool(config.getNodes());
            Graph graph = graphPool.getResource();
    
            System.out.println("source:  " + graph.getSourceNodes());
            System.out.println("process: " + graph.getProcessNodes());
            System.out.println("sink:    " + graph.getSinkNodes());
            System.out.println("dead:    " + graph.getDeadNodes());
    
            System.out.println(graph.toString());
    
            graph.run("value1");
            graphPool.getResource().run("value2");
            graphPool.getResource().run("value3");
            graphPool.getResource().run("value4");
    
            Thread.sleep(1000);
    
            Executor.stop();
            Timer.stop();
        }
    }