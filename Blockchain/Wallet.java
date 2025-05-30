package Blockchain;

import java.security.*;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class Wallet {
    private PrivateKey privateKey; // Untuk menandatangani transaksi
    private PublicKey publicKey;   // Sebagai alamat wallet
    private double balance;        // Saldo wallet
    static {
        Security.addProvider(new BouncyCastleProvider()); // Pastikan BC terdaftar
    }
    public Wallet() {
        
        generateKeyPair();
        this.balance = 0; // Saldo awal
    }

    /**
     * Generate pasangan public key dan private key.
     */
//    private void generateKeyPair() {
//        try {
//            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//            keyGen.initialize(2048);
//            KeyPair pair = keyGen.generateKeyPair();
//            this.privateKey = pair.getPrivate();
//            this.publicKey = pair.getPublic();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate key pair", e);
//        }
//    }

    private void generateKeyPair() {
        try {
            // 1. Gunakan algoritma "ECDSA", bukan "RSA"
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC"); // Provider BC

            // 2. Pilih kurva elliptic (contoh: secp256k1 untuk Bitcoin)
            ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
            keyGen.initialize(ecSpec);

            // 3. Generate key pair
            KeyPair pair = keyGen.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate EC key pair", e);
        }
    }
    
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public double getBalance() {
        return balance;
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public void deductBalance(double amount) {
        if (amount > this.balance) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance -= amount;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "publicKey=" + publicKey.hashCode() + // Hash code untuk representasi sederhana
                ", balance=" + balance +
                '}';
    }
}
