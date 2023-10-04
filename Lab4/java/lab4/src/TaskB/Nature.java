package TaskB;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Nature extends EntityThread{
    
    public Nature(ReentrantReadWriteLock lock){
        super(lock);
    }
}
