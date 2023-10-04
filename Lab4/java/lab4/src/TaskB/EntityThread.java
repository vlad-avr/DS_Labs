package TaskB;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EntityThread {
    protected ReentrantReadWriteLock lock;

    public EntityThread(ReentrantReadWriteLock lock){
        this.lock = lock;
    }
}
