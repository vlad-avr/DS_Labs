package TaskB;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Gardener extends EntityThread{
    
    public Gardener(ReentrantReadWriteLock lock){
        super(lock);
    }
}
