package Blockchain;

import core.SimScenario;
import java.util.ArrayList;
import java.util.List;

public class Blockchain {

    private List<Block> chain;
    private final int difficulty;

    public List<Block> getChain() {
        return chain;
    }

    public int getDifficulty() {
        return difficulty;
    }
    
    public Blockchain(int difficulty) {
        this.chain = new ArrayList<>();
        this.difficulty = difficulty;
    }

    public Block getLatestBlock() {
        if (chain.isEmpty()) {
            return new Block();
        }
        return chain.get(chain.size() - 1);
    }

    public void addBlock(Block newBlock) {
        chain.add(newBlock);
    }

    public void addBlockFromLocalChain(Localchain localChain) {
        List<Block> blockFromLocalchain = new ArrayList<>(localChain.getChain());

        for (Block b : blockFromLocalchain) {
            String previousHash = getLatestBlock().getHash();
            b.setPreviousHash(previousHash);
            b.recalculateHash(difficulty);
            addBlock(b);
        }
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Periksa apakah hash saat ini masih valid
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                return false;
            }

            // Periksa apakah hash sebelumnya cocok dengan hash blok sebelumnya
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }
        }
        return true;
    }

    public void printBlockchain() {
        for (Block block : chain) {
//            System.out.println("Index       : " + block.getIndex());
//            System.out.println("Timestamp   : " + block.getTimestamp());
//            System.out.println("Data        : " + block.getData());
            System.out.println("Hash        : " + block.getHash());
            System.out.println("Prev Hash   : " + block.getPreviousHash());
            System.out.println("-------------------------------");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("==== BLOCKCHAIN ====\n");

        for (Block block : chain) {
            sb.append("Prev Hash: ").append(block.getPreviousHash()).append("\n")
                    .append("Hash    : ").append(block.getHash()).append("\n")
                    .append("Mined By: ").append(block.getMinedBy() != null ? block.getMinedBy().toString() : "Unknown").append("\n")
                    .append("-----------------------\n");
        }

        return sb.toString();
    }

}
