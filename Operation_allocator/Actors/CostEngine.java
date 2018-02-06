package Actors;

public class CostEngine {

    //DEVE ESSERE TOTALMENTE RIFATTO
    public CostEngine() {

    }

    //CODICE ERRATO, NECESSARIA INTRODUZIONE DELL'ALBERO
    public double computeCost(Operation op, Provider executor, String enc) {
        double cost = 0;
        if (enc.equals("homomorphic")) {
            cost += addHomomorphicCost(op);
        } else if (enc.equals("asymmetric")) {
            cost += addAsymmetricCost(op);
        }
        if (!op.getExecutor().selfDescription().equals(executor.selfDescription()))
            cost += addMotionCost(op, executor);
        cost += addExecutionCost(op, executor);
        return cost;
    }

    public double addHomomorphicCost(Operation op) {
        return op.getOp_metric().outputSize * op.getExecutor().getMetrics().Kc2;
    }

    public double addAsymmetricCost(Operation op) {
        return op.getOp_metric().outputSize * op.getExecutor().getMetrics().Kc2;
    }

    public double addMotionCost(Operation op, Provider destination) {
        return op.getOp_metric().outputSize * (op.getExecutor().getMetrics().Km + destination.getMetrics().Km);
    }

    public double addExecutionCost(Operation op, Provider executor) {
        return op.getOp_metric().CPU_time * executor.getMetrics().Kcpu + op.getOp_metric().IO_time * executor.getMetrics().Kio;
    }


}
