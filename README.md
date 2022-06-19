# GraphEngine
    支持有向无环图的构建运行，DFS打印图，环检测
    算子即是数据载体又是计算逻辑，多路径并行执行，无锁无需同步等待，上游算子运行完毕级联触发下游依赖算子

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
