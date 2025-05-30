/* 
 * 
 * 
 */
package report;

/**
 * Records the average buffer occupancy and its variance with format:
 * <p>
 * <Simulation time> <average buffer occupancy % [0..100]> <variance>
 * </p>
 *
 *
 */
import java.util.*;
//import java.util.List;
//import java.util.Map;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;
import core.SimClock;
import core.UpdateListener;

public class StorageCapacityReport extends Report implements UpdateListener {

    /**
     * Record occupancy every nth second -setting id ({@value}). Defines the
     * interval how often (seconds) a new snapshot of buffer occupancy is taken
     * previous:5
     */
    public static final String STORAGE_REPORT_INTERVAL = "storageInterval";
    /**
     * Default value for the snapshot interval
     */
    public static final int DEFAULT_STORAGE_REPORT_INTERVAL = 180;

    private double lastRecord = Double.MIN_VALUE;
    private int interval;
    private Map<DTNHost, List<Integer>> usageStorage = new HashMap<>();
    private int updateCounter = 0;  //new added

    public StorageCapacityReport() {
        super();

        Settings settings = getSettings();
        if (settings.contains(STORAGE_REPORT_INTERVAL)) {
            interval = settings.getInt(STORAGE_REPORT_INTERVAL);
        } else {
            interval = -1;
            /* not found; use default */
        }

        if (interval < 0) {
            /* not found or invalid value -> use default */
            interval = DEFAULT_STORAGE_REPORT_INTERVAL;
        }
    }

    public void updated(List<DTNHost> hosts) {
        if (isWarmup()) {
            return;
        }
        if (SimClock.getTime() - lastRecord >= interval) {

            lastRecord = SimClock.getTime();
            for (DTNHost ho : hosts) {

                if (ho.getName().startsWith("ope8")) {
                    if (usageStorage.containsKey(ho)) {
                        usageStorage.get(ho).add(ho.getStorage());
                    } else {
                        List<Integer> temp = new ArrayList<>();
                        temp.add(ho.getStorage());
                        usageStorage.put(ho, temp);
                    }
//                    String temp;
//                    temp = "Node : " +ho.getName()+"------Waktu = " + SimClock.getTime() + "-----Storage Capacity = " + ho.getStorage() + "/" + ho.getStorageCapacity();
//                    write(temp);
                }
            }
//            printLine(hosts);

            updateCounter++; // new added
        }

    }

    public void done() {
        String intervalWaktu = "";

        System.out.println("Cek dulu");
        for (Map.Entry<DTNHost, List<Integer>> entry : usageStorage.entrySet()) {
            DTNHost host = entry.getKey();
            List<Integer> temp = entry.getValue();
            System.out.println("Cek");
            String output = host + " ";
            write(output);
            for (Integer integer : temp) {
                write(String.valueOf(integer));
            }
}

        super.done();
    }
}
