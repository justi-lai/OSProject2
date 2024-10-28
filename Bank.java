import java.util.concurrent.Semaphore;

public class Bank {

    static final int TELLERS_LEN = 3;
    static final int CUSTOMERS_LEN = 50;

    static Semaphore bankLock = new Semaphore(0);
    static Semaphore tellersLock = new Semaphore(0);

    static Teller[] tellers;
    static Customer[] customers;

    static int count = 0;

    static boolean customersLeft;

    public static void main(String[] args) {
        tellers = new Teller[TELLERS_LEN];
        customers = new Customer[CUSTOMERS_LEN];

        customersLeft = true;

        for (int i = 0; i < TELLERS_LEN; i++) {
            tellers[i] = new Teller(i);
            tellers[i].start();
        }
        bankReady();

        for (int i = 0; i < CUSTOMERS_LEN; i++) {
            customers[i] = new Customer(i);
            customers[i].start();
        }

        bankClose();
    }

    private static void bankReady() {
        try {
            for (int i = 0; i < TELLERS_LEN; i++) {
                tellers[i].tellerLock.acquire();
            }
            System.out.println("Bank is open.");
            for (int i = 0; i < TELLERS_LEN; i++) {
                tellers[i].tellerLock.release();
            }
            tellersLock.release(TELLERS_LEN);
            bankLock.release(2);
        }
        catch (InterruptedException e) {
            System.err.println("Unable to wait for all tellers ready: " + e);
        }
    }

    private static void bankClose() {
        for (int i = 0; i < CUSTOMERS_LEN; i++) {
            try {
                customers[i].join();
            }
            catch (InterruptedException e) {
                System.err.println("Error joining with Customer " + i + ": " + e);
            }
        }
        System.out.println("Customers all left.");
        customersLeft = false;
        for (int i = 0; i < TELLERS_LEN; i++) {
            try {
                tellers[i].join();
            }
            catch (InterruptedException e) {
                System.err.println("Error joining with Customer " + i + ": " + e);
            }
        }
        System.out.println("Tellers done.");
    }
}
