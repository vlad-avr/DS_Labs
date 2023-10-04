package TaskB;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Logger extends EntityThread{
    
    public Logger(ReentrantReadWriteLock lock){
        super(lock);
    }
}
