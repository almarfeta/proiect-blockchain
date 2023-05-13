package blockchain;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Blockchain blockchain = new Blockchain();
        int numberOfMiners = 15;
        Random random = new Random();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfMiners);
        for (int i = 1; i <= numberOfMiners; i++) {
            String message = "miner" + (random.nextInt(numberOfMiners) + 1)
                    + " sent " + (random.nextInt(100) + 1) + " VC "
                    + " to miner" + (random.nextInt(numberOfMiners) + 1);
            executor.execute(new Miner(i, message, blockchain));
        }
        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        blockchain.printBlockchain();
    }
}
