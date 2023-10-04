package TaskB;

import java.security.SecureRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Garden {
    public Integer[][] garden_map;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final SecureRandom rnd = new SecureRandom();

    public Garden(int rows, int cols){
        garden_map = new Integer[rows][cols];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                garden_map[i][j] = rnd.nextInt(3);
            }
        }
    }

    public void start(){
        Nature nature = new Nature(lock, this, rnd);
        Gardener gardener = new Gardener(lock, this, rnd);
        Logger logger = new Logger(lock, this, rnd);
        FileLogger f_logger = new FileLogger(lock, this, rnd);
        nature.start();
        gardener.start();
        logger.start();
        f_logger.start();
    }
}
