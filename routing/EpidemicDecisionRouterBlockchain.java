package routing;

import Blockchain.Block;
import Blockchain.Blockchain;
import Blockchain.Transaction;
import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SimScenario;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EpidemicDecisionRouterBlockchain implements RoutingDecisionEngine {

    
    protected LinkedList<Double> resourcesList;
    /**
     * threshold for block mining verification (algorithmTwo) settings ({@value})
     */
    private static final String THRESHOLD = "threshold";
    /**
     * nilai default dari threshold (algorithmTwo)
     */
    private static final int DEFAULT_THRESHOLD = 7;
    
    /**
     * The world instance
     */
    private static final String MAX_TRX = "maxTrx";
    private Double lastRecord = Double.MIN_VALUE;
    private int interval;
    private List<Block> minedBlock;
    private int threshold;
    private int counter;
    private int maxTrx;

    public EpidemicDecisionRouterBlockchain(Settings s) {
        minedBlock = new ArrayList<>();
        counter = 0;
        if (s.contains(THRESHOLD)) {
            threshold = s.getInt(THRESHOLD);
        } else {
            threshold = DEFAULT_THRESHOLD;
        }
        if (s.contains(MAX_TRX)) {
            maxTrx = s.getInt(MAX_TRX);
        }
    }

    public EpidemicDecisionRouterBlockchain(EpidemicDecisionRouterBlockchain proto) {
        minedBlock = new ArrayList<>();
        resourcesList = new LinkedList<>();
        interval = proto.interval;
        lastRecord = proto.lastRecord;
        this.threshold = proto.threshold;
        this.counter = proto.counter;
        this.maxTrx = proto.maxTrx;
    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer) {
        if (isOperatorProxy(thisHost)) {
            if (SimClock.getTime() > 15000 && SimClock.getTime() < 20000) {
                thisHost.groupTransactions();
            }
        }
    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer) {

    }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
        DTNHost host = con.getOtherNode(peer);

        /*
        Ada waktu 20000 ms untuk melakukan inisialisasi awal terlebih dahulu.
        Di sini kami berasumsi bahwa 10000 ms pertama dilakukan para miner
        untuk melakukan/membangkitkan transaksi. Transaksi akan dibungkus
        ke dalam message, dan destinasi hanya akan ke Operator Proxy dari setiap
        area. Lalu setelah 10000 ms berikutnya, maka transaksi-transaksi ini 
        akan dikumpulkan dan dibuat grup untuk nanti dilakukan proses mining 
        oleh para miner
         */
        if (SimClock.getTime() >= 20000) {
            forwarding_algorithmOne(host, peer);
            /*
                Mining dilakukan oleh Operator proxy dan Miner dengan cara
                membagikan list transaksi berisikian transaksi-transaksi yang
                sudah dibuat miner tadi ke para miner di area tersebut. Lalu para
                miner akan membungkus ke dalam satu blok dengan nonce masih 0,
                dan pada proses ini miner-miner akan mencari nilai nonce sehingga
                hash dapat mencapai tingkut difficulty yang sudah diatur, masing-
                masing miner akan dicatat waktu durasi mining mereka, tujuannya
                untuk memilih blok terbaik dengan interval waktu mining tercepat, 
                lalu akan disimpan ke dalam selectedBlock. Saat selectedBlock
                terisi, proses selanjutnya yaitu memverifikasi blok.
             */
            mining_algorithmOne(host, peer);

            /*
                Setelah memilih blok terbaik, operator proxy kembali membagikan
                hasil blok terbaik tersebut kepada miner untuk diverifikasi, hasil
                verifikasi beriringan dengan bertambahnya nilai v, jika nilai v
                sudah memenuhi threshold, blok dianggap valid dan ditambahkan ke
                dalam chain lokal yaitu localChain yang dimiliki oleh para Operator
                Proxy. Proses mining dan verification berlangsung berurutan, jika
                verifikasi selesai namun masih ada list transaksi di Operator Proxy
                yang belum diurus, maka kembali lagi ke proses mining. Proses selan-
                jutnya akan dilakukan saat list transaksi pada Operator Proxy sudah 
                tidak ada lagi.
             */
            verification_algorithmTwo(host, peer);

        }
    }

    /**
     * Mengimplementasikan algoritma penambangan dimana Operator Proxy memilih
     * daftar transaksi terbaik, menugaskannya ke penambang (Miner), dan
     * mencatat waktu penambangan. Blok dengan hasil terbaik kemudian dipilih
     * dan disimpan dalam rantai blok lokal (Localchai).
     *
     * Algoritma bekerja dengan langkah-langkah: 1. Operator Proxy memilih
     * daftar transaksi terbaik dari kumpulan transaksi yang ada 2. Menugaskan
     * proses penambangan ke Miner yang belum pernah ditugaskan sebelumnya 3.
     * Miner melakukan proses penambangan (proof-of-work) dan mencatat waktu
     * yang dibutuhkan 4. Setelah 7 Miner berpartisipasi, blok dengan waktu
     * penambangan terbaik akan dipilih 5. Blok terpilih ditambahkan ke rantai=
     * blok lokal dan daftar transaksi diperbarui
     *
     * @param host DTNHost yang bertindak sebagai Operator Proxy untuk mengelola
     * proses penambangan
     * @param peer DTNHost yang bertindak sebagai Miner untuk melakukan operasi
     * penambangan
     */
    private void forwarding_algorithmOne(DTNHost host, DTNHost peer) {
        if (isOperatorProxy(host) && isHome(peer)) {
            if (!peer.getVisitedOperatorProxy().contains(host)) {
                System.out.println(host + " datang ke Home!");
                peer.getTrxHome().addAll(host.getTrx());

                peer.getVisitedOperatorProxy().add(host);
                host.getTrx().clear();

                System.out.println("Home sudah menerima Transaksi!");
                System.out.println("Jumlah Grouped Transaction: " + peer.getTrxHome().size());
            }
        }
        if (isHome(host) && isCollector(peer)) {
            if (host.getVisitedOperatorProxy().size() == 8 && peer.getTrxCol().isEmpty()) {
                peer.getTrxCol().addAll(host.getTrxHome());

                if (!peer.getTrxCol().isEmpty()) {
                    System.out.println("Collector sudah menerima trx, size: " + peer.getTrxCol().size());
                    System.out.println("Difficulty : "+SimScenario.getInstance().getDifficulty());
                }
                host.getTrxHome().clear();
            }
        }
        if (isCollector(host) && isInternet(peer)) {
            if (peer.getTrxInter().isEmpty()) {
                peer.getTrxInter().addAll(host.getTrxCol());

                if (!peer.getTrxInter().isEmpty()) {
                    System.out.println("Internet sudah menerima trx, size: " + peer.getTrxInter().size());
                }
                host.getTrxCol().clear();
            }
        }
        if (isInternet(host) && isAdmin(peer)) {
            if (peer.getTrxAdmin().isEmpty() && !(peer.isStartedAppending())) {
                peer.getTrxAdmin().addAll(host.getTrxInter());
                if (!peer.getTrxAdmin().isEmpty()) {
                    System.out.println("Admin sudah menerima trx, size: " + peer.getTrxAdmin().size());
                    System.out.println("Memulai proses mining!!!");
                }
            }
        }

    }

    private void mining_algorithmOne(DTNHost host, DTNHost peer) {
        if (isAdmin(host)) {

            if (!host.getTrxAdmin().isEmpty() && host.getSelectedBlock() == null) {

                List<List<Transaction>> trx = host.getTrxAdmin();
                Blockchain blockChain = host.getMainChain();
                String previousHash = blockChain.getLatestBlock().getHash();

                // System.out.println("Sisa trxAdmin: " + trx.size());
                if (isMiner(peer)) {

                    if (!host.getVisitedMiner().contains(peer)) { // jika baru pertama kali bertemu

                        host.getVisitedMiner().add(peer);

                        int indexBestTRX = getBestTranx(trx);
                        List<Transaction> bestTransactionList = new ArrayList<>(trx.get(indexBestTRX));
                        for (int i = 0; i < bestTransactionList.size(); i++) {
                            if (!bestTransactionList.get(i).verifySignature()) {
                                System.out.println("Transaksi " + bestTransactionList.get(i).getTransactionHash() + " tidak valid!");
                                bestTransactionList.remove(i);
                            }
                        }

                        Block b = new Block(previousHash, bestTransactionList, System.currentTimeMillis());

                        b.setFee(getFee(bestTransactionList));
                        b.setMinedBy(peer);

                        long begin = System.currentTimeMillis();

                        b.mineBlock(blockChain.getDifficulty());

                        long end = System.currentTimeMillis();
                        long time = end - begin;
                        // System.out.println("Durasi : "+ time);
                        b.setIntervalMining(time);

                        minedBlock.add(b);
                    }
                }

                if (host.getVisitedMiner().size() == 15) {

                    host.getVisitedMiner().clear();

                    int indexBestTRX = getBestTranx(trx);
                    host.getTrxAdmin().remove(indexBestTRX);

                    int index = getBestMinedBlock(minedBlock);

                    Block selectedBlock = new Block(minedBlock.get(index));
                    host.setSelectedBlock(selectedBlock);

                    System.out.println("Satu Block sudah terpilih!");
                    minedBlock.clear();
                }
            }
        }
    }

    /**
     * Mengimplementasikan algoritma verifikasi dimana Operator Proxy
     * memvalidasi blok yang telah ditambang melalui konsensus beberapa Miner.
     * Blok akan dianggap valid jika mencapai threshold verifikasi tertentu
     * sebelum ditambahkan ke rantai blok lokal.
     *
     * Algoritma bekerja dengan langkah-langkah: 1. Operator Proxy memastikan
     * blok yang dipilih valid dengan memverifikasi hash blok 2. Mengirim blok
     * ke Miner yang belum pernah melakukan verifikasi sebelumnya 3. Setiap
     * Miner menghitung ulang hash blok dan membandingkan dengan hash target 4.
     * Jika jumlah verifikasi valid mencapai threshold yang ditentukan: a. Blok
     * ditambahkan ke rantai blok lokal b. Reset semua parameter dan status
     * verifikasi c. Memperbarui status kesiapan penyimpanan jika semua
     * transaksi telah diproses
     *
     * @param host DTNHost yang bertindak sebagai Operator Proxy untuk mengelola
     * proses verifikasi
     * @param peer DTNHost yang bertindak sebagai Miner untuk melakukan operasi
     * verifikasi
     */
    private void verification_algorithmTwo(DTNHost host, DTNHost peer) {
        if (isAdmin(host)) {
            if (host.getSelectedBlock() != null) {

                Blockchain mainChain = host.getMainChain();
                Block selectedBlock = host.getSelectedBlock();

                if (isMiner(peer)) {

                    if (!host.getVisitedMiner().contains(peer)) {// jika baru pertama kali bertemu

                        host.getVisitedMiner().add(peer);

                        String targetHash = selectedBlock.calculateHash();

                        Block b = new Block(selectedBlock);
                        String hash = b.calculateHash();

                        if (targetHash.equals(hash)) {
                            host.setV(host.getV() + 1);
                        }
                    }

                    if (host.getV() == threshold) {
                        if (!(host.getV() > threshold)) {

                            //tambahkan selectedBlock ke dalam blockchain
                            mainChain.addBlock(new Block(selectedBlock));
                            System.out.println("Size blockchain : " + mainChain.getChain().size());
                            //reset v
                            host.setV(0);

                            //reset visitedMiner
                            host.getVisitedMiner().clear();

                            // reset selectedBlock
                            host.setSelectedBlock(null);
                            System.out.println("Satu block sudah diverifikasi dan ditambah ke main chain!");
                            System.out.println("Sisa trx admin: " + host.getTrxAdmin().size());
                            host.setStartedAppending(true);
                            if (host.getTrxAdmin().isEmpty()) {
                                host.setAppendingDone(true);
                                System.out.println("Waktu Simulasi : " + SimClock.getTime() + " s");
                                // peer.setAppendingDone(true);
                               //System.exit(0);

                            }

                        }
                    }

                }

//                System.out.println(mainChain.toString());
            }
        }

    }

    /**
     * Algoritma distribusi reward kepada miner berdasarkan kontribusi
     * penambangan blok.
     *
     * Algoritma bekerja dalam 2 fase: A. Informasikan status selesai: 1. Home
     * memberitahu Operator Proxy bahwa proses penyambungan selesai 2. Operator
     * Proxy menandai proses sebagai selesai
     *
     * B. Pembagian reward: 1. Operator Proxy memverifikasi miner yang belum
     * menerima reward 2. Menghitung total fee dari semua blok yang ditambang
     * miner tersebut 3. Menambahkan balance ke wallet miner 4. Mencatat miner
     * yang sudah menerima reward
     *
     * @param host DTNHost yang bertindak sebagai penerima/prosesor reward
     * @param peer DTNHost yang bertindak sebagai sumber/pemberi reward
     */
    private void reward_algorithmSix(DTNHost host, DTNHost peer) {

        
        if (isInternet(host) && isAdmin(peer)) {

            if (peer.isAppendingDone()) {
                host.setAppendingDone(true);
            }
        }

        /* Mulai bagikan fee ke miner */
        if (isMiner(host) && isAdmin(peer)) {

            if (peer.isAppendingDone() && !peer.getRewardedMiners().contains(host)) {

                List<Block> list = peer.getMainChain().getChain();

                Iterator<Block> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Block b = iterator.next();
                    DTNHost miner = b.getMinedBy();
                    double fee = b.getFee();

                    if (miner.equals(host)) {
                        System.out.println("Memberikan reward ke "
                                + miner + ".....");
                        host.getWallet().addBalance(fee);
                        iterator.remove();
                    }
                }
                peer.getRewardedMiners().add(host);
                if (list.isEmpty()) {

                    for (DTNHost h : SimScenario.getInstance().getHosts()) {
                        if (isMiner(h)) {
                            System.out.println("Wallet " + h + ": " + h.getWallet().getBalance());
                        }
                    }
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Finds the index of the transaction list with the highest total amount.
     *
     * @param trx A list of transaction lists to be evaluated.
     * @return The index of the transaction list with the highest total amount.
     */
    private int getBestTranx(List<List<Transaction>> trx) {
        int index = -1;
        double maxTotal = 0;
        for (int i = 0; i < trx.size(); i++) {
            double tempTotal = 0;
            for (Transaction t : trx.get(i)) {
                tempTotal += t.getAmount();
            }

            if (tempTotal > maxTotal) {
                maxTotal = tempTotal;
                index = i;
            }
        }
        return index;
    }

    /**
     * Calculates the total transaction fee based on a percentage of the total
     * transaction amount.
     * @param t A list of transactions whose total fee is to be calculated.
     * @return The calculated transaction fee, which is a percentage of the
     * total transaction amount.
     */
    private double getFee(List<Transaction> t) {
        double total = 0;
        for (Transaction tr : t) {
            total += tr.getAmount();
        }
        return 0.01 * total;
    }

    /**
     * Finds the index of the block with the shortest mining time.
     *
     * @param minedBlock A list of mined blocks to be evaluated.
     * @return The index of the block that was mined in the shortest time.
     */
    private int getBestMinedBlock(List<Block> minedBlock) {
        if (minedBlock.isEmpty()) {
            return -1; // Return -1 jika daftar kosong
        }

        int index = 0;
        long min = minedBlock.get(0).getIntervalMining();

        for (int i = 1; i < minedBlock.size(); i++) {
            if (minedBlock.get(i).getIntervalMining() < min) {
                min = minedBlock.get(i).getIntervalMining();
                index = i;
            }
        }
        return index;
    }

    @Override
    public boolean newMessage(Message m) {
        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost) {
        return m.getTo() == aHost;
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost) {
        if (isOperatorProxy(thisHost)) {
            Transaction trx = (Transaction) m.getProperty("transaction");
            if (trx != null&& counter < maxTrx) {
                addTransactionToBuffer(thisHost, trx);
                counter++;
            }
        }

        return !thisHost.getRouter().hasMessage(m.getId());
    }

    private void addTransactionToBuffer(DTNHost host, Transaction trx) {
        host.addTransactionToBuffer(trx);
    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost, DTNHost thisHost) {
        if (SimClock.getTime() > 10000) {
            return false;
        }
        if (isMiner(thisHost) && isMiner(otherHost)) {
            if (!isSameArea(thisHost, thisHost)) {
                return false;
            }
        }
        if (isOperatorProxy(thisHost) && isMiner(otherHost)) {
            if (!isSameAreaOpe(thisHost, otherHost)) {
                return false;
            }
        }
        return isNode(thisHost) && (isOperatorProxy(otherHost) || isNode(otherHost));
    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost) {
        return m.getTo() == otherHost;
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld) {
        return true;
    }

    @Override
    public RoutingDecisionEngine replicate() {
        return new EpidemicDecisionRouterBlockchain(this);
    }

    @Override
    public void update(DTNHost thisHost) {
    }

    private boolean isOperatorProxy(DTNHost host) {
        return host.toString().startsWith("ope");
    }

    private boolean isMiner(DTNHost host) {
        return host.toString().startsWith("min");
    }

    private boolean isHome(DTNHost host) {
        return host.toString().startsWith("home");
    }

    private boolean isInternet(DTNHost host) {
        return host.toString().startsWith("inter");
    }

    private boolean isCollector(DTNHost host) {
        return host.toString().startsWith("col");
    }

    private boolean isNode(DTNHost host) {
        return host.toString().startsWith("node");
    }

    private boolean isAdmin(DTNHost host) {
        return host.toString().startsWith("adm");
    }

    private boolean isSameArea(DTNHost c1, DTNHost c2) {
        return c1.toString().substring(1, 9).equals(c2.toString().substring(1, 9));
    }

    private boolean isSameAreaOpe(DTNHost ope, DTNHost m) {
        return ope.toString().substring(3).equals(m.toString().substring(5));
    }
}
