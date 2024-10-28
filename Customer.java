public class Customer extends Thread{
    int id;
    String action;
    Teller teller;

    public Customer(int id) {
        this.id = id;
        if (Math.round(Math.random()) == 0) {
            action = "deposit";
        }
        else {
            action = "withdrawal";
        }
        System.out.printf("Customer %d wants to %s.%n", id, action);
    }

    @Override
    public void run() {
        begin();

        System.out.printf("Customer %d is in line.\n", id);

        selectTeller();
        introduceToTeller();
        askForAction();
        leave();
    }

    private void begin() {
        try {
            Bank.bankLock.acquire();
            System.out.printf("Customer %d has entered the bank.\n", id);
            Bank.bankLock.release();
        }
        catch (InterruptedException e) {
            System.err.println("Error in Customer " + id + ": " + e);
        }
    }

    private void selectTeller() {
        try {
            Bank.tellersLock.acquire();
            System.out.printf("Customer %d is selecting a teller.\n", id);
            for (int i = 0; i < Bank.TELLERS_LEN; i++) {
                if (Bank.tellers[i].tellerLock.tryAcquire(2)) {
                    teller = Bank.tellers[i];
                    System.out.printf("Customer %d goes to Teller %d.\n", id, teller.id);
                    return;
                }
            }
        }
        catch (InterruptedException e) {
            System.out.println("Error in Customer: Can't select Teller.");
        }

    }

    private void introduceToTeller() {
        try {
            teller.messageLock.acquire();
            System.out.printf("Customer %d introduces itself to Teller %d.\n", id, teller.id);
            teller.message = String.valueOf(id);
            teller.messageLock.release(2);
        }
        catch (InterruptedException e) {
            System.err.println("Error in Customer " + id + ": Can't introduce to teller");
        }
    }

    private void askForAction() {
        try {
            teller.messageLock.acquire(3);
            teller.message = action;
            System.out.printf("Customer %d asks for a %s transaction.\n", id, action);
            teller.messageLock.release(4);
        }
        catch (InterruptedException e) {
            System.out.println("Error in Customer: Can't ask for action.");
        }
    }

    private void leave() {
        try {
            teller.messageLock.acquire(5);
            if (teller.message.equals("completed")) {
                System.out.printf("Customer %d thanks Teller %d and leaves.\n", id, teller.id);
            }
            else {
                throw new InterruptedException();
            }
            teller.messageLock.release(6);
            teller.messageLock.acquire(7);
            teller.messageLock.release();
            teller.tellerLock.release();
            //Bank.tellersLock.release();
        }
        catch (InterruptedException e) {
            System.out.println("Error in Customer: Never received confirmation of transaction.");
        }
    }

    public void releaseTellersLock() {
        Bank.tellersLock.release();
    }
}