package TaskA;

public class Writer extends Worker{
    public Writer(int lock_cap, int max_wait_time, FileLocker locker, Manager manager) {
        super(lock_cap, max_wait_time, locker, manager);
    }
}
