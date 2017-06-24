package org.ayfaar.app.utils;

import java.util.LinkedList;
import java.util.List;

public class Transaction implements Runnable {
    private List<Operation> register = new LinkedList<>();

    public Transaction register(Runnable run, Runnable rollback) {
        register.add(new Operation(run, rollback));
        return this;
    }

    public void run() {
        List<Runnable> rollbackChain = new LinkedList<>();
        register.stream().forEachOrdered(operation -> {
            try {
                operation.run.run();
                rollbackChain.add(0, operation.rollback);
            } catch (Exception e) {
                rollbackChain.forEach(Runnable::run);
                throw new RuntimeException("Transaction aborted and reverted", e);
            }
        });
    }

    private class Operation {
        private final Runnable run;
        private final Runnable rollback;

        private Operation(Runnable run, Runnable rollback) {
            this.run = run;
            this.rollback = rollback;
        }
    }
}
