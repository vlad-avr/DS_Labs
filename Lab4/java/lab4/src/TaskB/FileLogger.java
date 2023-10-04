package TaskB;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileLogger extends EntityThread{
    
    public FileLogger(ReentrantReadWriteLock lock){
        super(lock);
    }
}
