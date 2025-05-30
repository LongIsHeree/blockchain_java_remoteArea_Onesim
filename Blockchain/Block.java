package Blockchain;

import core.DTNHost;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Block {

    private String previousHash;
    private List<Transaction> transactions;
    private long timestamp;
    private int nonce;
    private String blockHash;
    private long intervalMining;
    private DTNHost minedBy;
    private double fee;

    public Block() {
        this.previousHash = "0";
    }

    public Block(String previousHash, List<Transaction> transactions, long timestamp) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.timestamp = timestamp;
        this.nonce = 0;
        this.intervalMining = 0;
        this.blockHash = calculateHash();
    }

    public Block(Block other) {
        this.previousHash = other.previousHash;
        this.transactions = new ArrayList<>(other.transactions); // Copy list transaksi
        this.timestamp = other.timestamp;
        this.nonce = other.nonce;
        this.blockHash = other.blockHash;
        this.intervalMining = other.intervalMining;
        this.fee = other.fee;
        this.minedBy = other.minedBy;
    }

    public long getIntervalMining() {
        return intervalMining;
    }

    public void setIntervalMining(long intervalMining) {
        this.intervalMining = intervalMining;
    }

    public String calculateHash() {
        StringBuilder data = new StringBuilder(previousHash + timestamp + nonce);
        for (Transaction tx : transactions) {
            data.append(tx.getTransactionHash());
        }
        return SecureTransaction.applySha256(data.toString());
    }

    public void recalculateHash(int difficulty) {
        String newHash = calculateHash();

        // Cek apakah hash yang dihasilkan memenuhi kriteria difficulty
        if (isHashValid(newHash, difficulty)) {
            this.blockHash = newHash;
            
        } else {
            // Jika tidak valid, lakukan mining ulang
            this.nonce = 0; // Reset nonce
            mineBlock(difficulty); // Lakukan mining ulang
        }
    }

    private boolean isHashValid(String hash, int difficulty) {
        String target = repeatZero(difficulty); // Buat target dengan jumlah 0 di awal
        return hash.substring(0, difficulty).equals(target);
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public void mineBlock(int difficulty) {
        String target = repeatZero(difficulty);
//        System.out.println("Target : "+ target );
//        System.out.println("Block Hash : "+ blockHash);
//        System.out.println("Difficulty : "+ difficulty);
        while (!blockHash.substring(0, difficulty).equals(target)) {
            nonce++;
            
           // System.out.println("Block MINED");
            blockHash = calculateHash();
            
        }
        
    }

    private String repeatZero(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("0");
        }
        return sb.toString();
    }

    private String applySHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHash() {
        return blockHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public DTNHost getMinedBy() {
        return minedBy;
    }

    public void setMinedBy(DTNHost minedBy) {
        this.minedBy = minedBy;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================== "
                + "BLOCK "
                + "======================================\n")
                .append("Previous Hash   : ").append(previousHash).append("\n")
                .append("Timestamp       : ").append(timestamp).append("\n")
                .append("Nonce           : ").append(nonce).append("\n")
                .append("Block Hash      : ").append(blockHash).append("\n")
                .append("Interval Mining : ").append(intervalMining).append("\n")
                .append("Mined By        : ").append(minedBy).append("\n")
                .append("Fee             : ").append(fee).append("\n")
                .append("Transactions    : \n");

        for (Transaction t : transactions) {
            sb.append(t.toString()).append("\n"); // Memanggil toString() Transaction
        }

        sb.append("======================================"
                + "======================================\n");
        return sb.toString();
    }
}
