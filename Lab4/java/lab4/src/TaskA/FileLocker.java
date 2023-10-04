package TaskA;

public class FileLocker {
    private int lock;
    private final int max_lock_cap;

    public FileLocker(int N){
        max_lock_cap = N;
        lock = 0;
    }

    // public int get_lock_val(){
    //     return this.lock;
    // }

    public boolean is_free(){
        if(lock == 0){
            return true;
        }
        return false;
    }

    public boolean is_full(){
        if(lock == max_lock_cap){
            return true;
        }
        return false;
    }

    public void acquire_lock(int thread_lock_cap) throws Exception{
        if(lock + thread_lock_cap > max_lock_cap){
            throw new Exception("\n ERROR: Max lock capacity has been exceeded!");
        }
        lock += thread_lock_cap;
    }

    public void release_lock(int thread_lock_cap){
        if(lock - thread_lock_cap < 0){
            System.out.println("\n ERROR: Lock capacity has reached negative value - release anomaly detected!");
            return;
        }
        lock -= thread_lock_cap;
    }
}
