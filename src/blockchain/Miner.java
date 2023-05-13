package blockchain;

import blockchain.utils.StringUtil;

import java.security.KeyPair;
import java.util.Date;
import java.util.Random;

public class Miner implements Runnable {
    private final Integer id;
    private final KeyPair keyPair;
    private final String message;
    private final Blockchain blockchain;

    public Miner(Integer id, String message, Blockchain blockchain) {
        this.id = id;
        this.keyPair = StringUtil.generateKeys();
        this.message = message;
        this.blockchain = blockchain;
    }

    private Long getProofOfWork(String input, int numberOfZeros) {
        StringBuilder zeros = new StringBuilder();
        for (int i = 0; i < numberOfZeros; i++) {
            zeros.append("0");
        }

        Random random = new Random();
        long magicNumber = random.nextLong();
        String generatedHash = StringUtil.applySha256(input + magicNumber);
        while (!(generatedHash.startsWith(zeros.toString()) && !generatedHash.startsWith(zeros.toString() + '0'))) {
            magicNumber = random.nextLong();
            generatedHash = StringUtil.applySha256(input + magicNumber);
        }
        return magicNumber;
    }

    @Override
    public void run() {
        boolean success = false;

        while (!success) {
            Block lastBlock = blockchain.getLastBlock();

            Long myBlockId = lastBlock.getId() + 1;
            Long myBlockTimestamp = new Date().getTime();
            String myMessage = (lastBlock.getId() == 0L) ? "No transactions" : message;
            byte[] myMessageSign = StringUtil.sign(myMessage, keyPair.getPrivate());
            String myBlockPreviousHash = lastBlock.getHash();

            String myBlockFields = id.toString() + myBlockId.toString() + myBlockTimestamp.toString() +
                    myMessage + myBlockPreviousHash;
            long start = System.currentTimeMillis();
            Long myMagicNumber = getProofOfWork(myBlockFields, blockchain.getNumberOfZeros());
            long finish = System.currentTimeMillis();
            long time = finish - start;

            String myBlockHash = StringUtil.applySha256(myBlockFields + myMagicNumber);

            Block myBlock = new Block(id, myBlockId, myBlockTimestamp, myMessage, myMessageSign,
                    keyPair.getPublic(), myBlockPreviousHash, myMagicNumber, myBlockHash);
            success = blockchain.addNewBlock(myBlock, time);
        }
    }
}
