package blockchain;

import blockchain.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Blockchain {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private final List<Block> blocks;
    private int numberOfZeros;

    public Blockchain() {
        this.blocks = new ArrayList<>();
        this.numberOfZeros = 0;
    }

    private int getVirtualCoinsForMiner(Integer minerId) {
        int total = 100;
        for (Block block : blocks) {
            if (block.getBlockData().equals("No transactions")) {
                continue;
            }
            String[] words = block.getBlockData().split(" ");
            if (words[0].equals("miner" + minerId)) {
                total -= Integer.parseInt(words[2]);
            }
            if (words[5].equals("miner" + minerId)) {
                total += Integer.parseInt(words[2]);
            }
        }
        return total;
    }

    private boolean validateBlockchain(Block newBlock) {
        if (!StringUtil.hasNumberOfZeros(newBlock.getHash(), numberOfZeros)) {
            return false;
        }
        if (!StringUtil.verify(newBlock.getBlockData(), newBlock.getSignature(), newBlock.getPublicKey())) {
            return false;
        }
        if (blocks.size() == 0) {
            if (newBlock.getId() != 1L) {
                return false;
            }
            if (!Objects.equals("0", newBlock.getPreviousHash())) {
                return false;
            }
        } else {
            if (newBlock.getId() != blocks.get(blocks.size() - 1).getId() + 1L) {
                return false;
            }
            if (!Objects.equals(blocks.get(blocks.size() - 1).getHash(), newBlock.getPreviousHash())) {
                return false;
            }
            if (getVirtualCoinsForMiner(newBlock.getMinerId()) < 0) {
                return false;
            }
        }

        return true;
    }

    public Block getLastBlock() {
        readLock.lock();
        if (blocks.size() == 0) {
            readLock.unlock();
            return new Block(null, 0L, null, null, null,
                    null, null, null, "0");
        }
        Block lastBlock = blocks.get(blocks.size() - 1);
        readLock.unlock();
        return new Block(lastBlock);
    }

    public int getNumberOfZeros() {
        return numberOfZeros;
    }

    public boolean addNewBlock(Block newBlock, long timeSpent) {
        writeLock.lock();
        if (validateBlockchain(newBlock)) {
            blocks.add(new Block(newBlock));
            blocks.get(blocks.size() - 1).setGeneratedTime(timeSpent);
            if (timeSpent < 10) {
                numberOfZeros++;
                blocks.get(blocks.size() - 1).setNumberOfZerosStatus("N was increased to " + numberOfZeros);
            } else if (timeSpent < 60) {
                blocks.get(blocks.size() - 1).setNumberOfZerosStatus("N stays the same");
            } else {
                numberOfZeros--;
                blocks.get(blocks.size() - 1).setNumberOfZerosStatus("N was decreased to " + numberOfZeros);
            }
            writeLock.unlock();
            return true;
        }
        writeLock.unlock();
        return false;
    }

    public void printBlockchain() {
        blocks.forEach(System.out::println);
    }
}
