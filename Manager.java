import java.util.concurrent.Semaphore;

public class Manager {
    static Semaphore managerLock = new Semaphore(1);
}
