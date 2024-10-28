import java.util.concurrent.Semaphore;

public class Teller extends Thread {
    int id;
    Customer customer;
    Semaphore tellerLock = new Semaphore(0);
    Semaphore messageLock = new Semaphore(0);
    String message;

    public Teller(int id) {
        this.id = id;
        System.out.printf("Teller %d is ready.\n", id);
        tellerLock.release(2);
        messageLock.release();
    }

    @Override
    public void run() {
        while (Bank.count != Bank.CUSTOMERS_LEN) {
            System.out.printf("Teller %d is waiting for a Customer.\n", id);
            findCustomer();
            handleTransaction();
            reset();
        }
    }

    private void findCustomer() {
        try {
            Bank.count++;
            messageLock.acquire(2);
            customer = Bank.customers[Integer.parseInt(message)];
            messageLock.release(3);
            System.out.printf("Teller %d is serving Customer %d\n", id, customer.id);
        }
        catch (InterruptedException e) {
            System.out.println("Error in Teller: Can't read customer intro.");
        }
    }

    private void handleTransaction() {
        try {
            messageLock.acquire(4);
            if (message.equals("withdrawal")) {
                withdraw();
            }
            else {
                deposit();
            }
            message = "completed";
            System.out.printf("Teller %d informs Customer %d that the transaction is finished.\n", id, customer.id);
            messageLock.release(5);
        }
        catch (InterruptedException e) {
            System.out.println("Error in Teller: Can't handle transaction.");
        }
    }

    private void withdraw() {
        System.out.printf("Teller %d is handling the withdrawal transaction.\n", id);
        goToManager();
        goToSafe();
        System.out.printf("Teller %d has finished the withdrawal.\n", id);
    }

    private void goToManager() {
        System.out.printf("Teller %d is going to the manager.\n", id);
        try {
            Manager.managerLock.acquire();
            int time = (int) Math.round(Math.random()*25) + 5;
            sleep(time);
            System.out.printf("Teller %d got the manager's permission.\n", id);
            Manager.managerLock.release();
        }
        catch (InterruptedException e) {
            System.out.println("Error in Teller: Can't get to manager.");
        }
    }

    private void deposit() {
        System.out.println("Teller is handling the deposit transaction.");
        goToSafe();
        System.out.printf("Teller %d has finished the deposit.\n", id);
    }

    private void goToSafe() {
        System.out.printf("Teller %d is going to the safe.\n", id);
        try {
            Safe.safeLock.acquire();
            System.out.printf("Teller %d is in the safe.\n", id);
            int time = (int) Math.round(Math.random()*40) + 10;
            sleep(time);
            Safe.safeLock.release();
        }
        catch (InterruptedException e) {
            System.out.println("Error in Teller: Can't get to safe.");
        }
    }
    private void reset() {
        try {
            messageLock.acquire(6);
        }
        catch (InterruptedException e) {
            System.out.println("Error in Teller: Can't reset");
        }
        message = "";
        messageLock.release(7);
        try {
            tellerLock.acquire();
            tellerLock.release(2);
            customer.releaseTellersLock();
        }
        catch (InterruptedException e) {
            System.out.println("Error in Teller: Can't reset 2");
        }
    }
}
