package TaskA;

import java.security.SecureRandom;

public class Worker extends Thread{
    protected final int lock_cap;
    protected final int max_wait_time;
    protected FileLocker locker;
    protected Manager manager;
    protected final SecureRandom rnd = new SecureRandom();

    public Worker(int lock_cap, int max_wait_time, FileLocker locker, Manager manager) {
        this.lock_cap = lock_cap;
        this.max_wait_time = max_wait_time;
        this.locker = locker;
        this.manager = manager;
    }
}
