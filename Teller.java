public class Teller extends Thread {
    int id;

    Teller(int id) {
        this.id = id;
        try {
            Bank.tellerLock.acquire();
            Bank.messages[id] = String.format("Teller %d is ready.", id);
            Bank.tellerLock.release();
        }
        catch (InterruptedException e) {
            System.err.println("Error in Teller " + id + ": " + e);
        }

    }

    @Override
    public void run() {

    }
}
