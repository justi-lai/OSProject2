import java.util.concurrent.Semaphore;

public class Bank {

    static final int TELLERS_LEN = 3;
    static final int CUSTOMERS_LEN = 50;

    static Semaphore tellerLock = new Semaphore(TELLERS_LEN);
    static Semaphore bankLock = new Semaphore(0);

    static String[] messages = new String[3];

    public static void main(String[] args) {
        Teller[] tellers = new Teller[TELLERS_LEN];
        Customer[] customers = new Customer[CUSTOMERS_LEN];

        for (int i = 0; i < TELLERS_LEN; i++) {
            tellers[i] = new Teller(i);
            tellers[i].start();
        }
        for (int i = 0; i < CUSTOMERS_LEN; i++) {
            customers[i] = new Customer(i);
            customers[i].start();
        }

        bankReady();


        for (int i = 0; i < CUSTOMERS_LEN; i++) {
            try {
                customers[i].join();
            }
            catch (InterruptedException e) {
                System.err.println("Error joining with Customer " + i + ": " + e);
            }
        }
        for (int i = 0; i < TELLERS_LEN; i++) {
            try {
                tellers[i].join();
            }
            catch (InterruptedException e) {
                System.err.println("Error joining with Teller " + i + ": " + e);
            }
        }
    }

    private static void bankReady() {
        try {
            tellerLock.acquire(TELLERS_LEN);
            for (int i = 0; i < TELLERS_LEN; i++) {
                System.out.println(messages[i]);
            }
            System.out.println("Bank is open.");
            bankLock.release(2);
        }
        catch (InterruptedException e) {
            System.err.println("Unable to wait for all tellers ready: " + e);
        }
    }
}
