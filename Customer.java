public class Customer extends Thread{
    int id;
    String action;

    Customer(int id) {
        this.id = id;
        if (Math.round(Math.random()) == 0) {
            action = "deposit";
        }
        else {
            action = "withdraw";
        }
        System.out.printf("Customer %d wants to %s.%n", id, action);
    }

    @Override
    public void run() {
        if (!begin()) {
            return;
        }



        Bank.bankLock.release();
    }

    private boolean begin() {
        try {
            Bank.bankLock.acquire();
            return true;
        }
        catch (InterruptedException e) {
            System.err.println("Error in Customer " + id + ": " + e);
            return false;
        }
    }
}