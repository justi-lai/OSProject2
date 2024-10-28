import java.util.concurrent.Semaphore;

public class Safe {
    static Semaphore safeLock = new Semaphore(2);
}
