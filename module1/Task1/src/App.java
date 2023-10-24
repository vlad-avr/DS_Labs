import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class App {
    public static void main(String[] args) throws Exception {
        List<String> books = new ArrayList<>();
        // Semaphore locker to manage access to the library (if 1 thread is taking the
        // books from the shelves or puts them back on - other threads have to wait)
        Semaphore semaphore = new Semaphore(1);
        SecureRandom rnd = new SecureRandom();
        int N_books = 20;
        int N_readers = 5;
        for (int i = 0; i < N_books; i++) {
            books.add("Book " + i);
        }
        for (int i = 0; i < N_readers; i++) {
            // Threads will read books indefinitely (stop program manually because these
            // guys are real book worms))))
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Number of times a reader can take books from the library
                    int max_takes = 10;
                    int cur_take = 0;
                    while (cur_take < max_takes) {
                        if (rnd.nextBoolean()) {
                            // Take home (take less books for longer period of time)
                            try {
                                semaphore.acquire();
                                List<String> books_taken = new ArrayList<>();
                                int books_num = rnd.nextInt(N_books / 4);
                                String books_str = "\n" + Thread.currentThread().getName()
                                        + " is reading books at home : ";
                                for (int i = 0; i < books_num && !books.isEmpty(); i++) {
                                    books_taken.add(books.remove(rnd.nextInt(books.size())));
                                    books_str += books_taken.get(i) + " ,";
                                }
                                semaphore.release();
                                if (!books_taken.isEmpty()) {
                                    System.out.println(books_str);
                                }
                                Thread.sleep(books_taken.size() * 800);
                                semaphore.acquire();
                                for (String book : books_taken) {
                                    books.add(book);
                                }
                                semaphore.release();
                                if (books_taken.size() != 0) {
                                    cur_take++;
                                    System.out.println(
                                            "\n" + Thread.currentThread().getName() + " has returned all the books!");
                                }
                                Thread.sleep(rnd.nextInt(1000) + 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Take to reading room (more books - less time)
                            try {
                                semaphore.acquire();
                                List<String> books_taken = new ArrayList<>();
                                String books_str = "\n" + Thread.currentThread().getName()
                                        + " is reading books at library : ";
                                int books_num = rnd.nextInt(N_books / 2);
                                for (int i = 0; i < books_num && !books.isEmpty(); i++) {
                                    books_taken.add(books.remove(rnd.nextInt(books.size())));
                                    books_str += books_taken.get(i) + " ,";
                                }
                                semaphore.release();
                                if (!books_taken.isEmpty()) {
                                    System.out.println(books_str);
                                }
                                Thread.sleep(books_taken.size() * 400);
                                semaphore.acquire();
                                for (String book : books_taken) {
                                    books.add(book);
                                }
                                semaphore.release();
                                if (!books_taken.isEmpty()) {
                                    cur_take++;
                                    System.out.println(
                                            "\n" + Thread.currentThread().getName() + " has returned all the books!");
                                }
                                Thread.sleep(rnd.nextInt(1000) + 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            thread.setName("Reader " + i);
            thread.start();
        }
    }
}
