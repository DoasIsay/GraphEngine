import com.google.gson.Gson;
import engine.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

        @Override
        public void register() {
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

        @Override
        public void register() {
            depend("TestOperator5");
            depend("TestOperator1");
        }
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

        @Override
        public void register() {
        }
    }

    @Load(type = "operator")
    public static class TestOperator4 extends Operator<String> {
        @Setter
        @Getter
        @Output
        String output;

        @Override
        public void clean() {
            output = null;
        }

        @Override
        public void invoke(String value) {
            emit("output", value, value);
            System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + output);
        }

        @Override
        public void register() {
            depend("TestOperator2");
            depend("TestOperator1");
            depend("TestOperator5");
            depend("TestOperator3");
        }
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
            System.out.println("invoke " + this.getClass().getSimpleName() + " output: " + testOperator7.outputString);
        }

        TestOperator7 testOperator7;
        @Override
        public void register() {
            depend("TestOperator1");
            testOperator7 = depend("TestOperator7");
        }
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

        @Depend(name = "TestOperator4.output")
        TestOperator4 testOperator4;


        @Override
        public void register() {
            depend("TestOperator3");
        }
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
        public void test() {};
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
    }
}