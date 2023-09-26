import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;


public class BeeSearch {
    private final List<List<Boolean>> forest_map;
    private final Integer forest_size;

    private final Integer threads_num;

    private final AtomicBoolean WP_is_punished;
    private final AtomicInteger sector_counter;

    public class SearchThread extends Thread{

        SearchThread(String name){
            setName(name);
        }
        @Override
        public void run(){
            while(WP_is_punished.get() == false && sector_counter.get() < forest_size){
               // System.out.println("\n " + getName() + " SECTOR COUNTER : " + sector_counter + "\n");
                sector_counter.set(sector_counter.get() + 1);
                find_and_punish(sector_counter.get()-1);
            }
        }
    }
    

    public BeeSearch(Integer size){
        forest_size = size;
        forest_map = new ArrayList<>(size);
        threads_num = (int)Math.sqrt(size);
        WP_is_punished = new AtomicBoolean(false);
        sector_counter = new AtomicInteger(0);

        //Creating forest map
        for(int i = 0; i < forest_size; i++){
            List<Boolean> row = new ArrayList<>(forest_size);
            for(int j = 0; j < forest_size; j++){
                row.add(false);
            }
            forest_map.add(row);
        }

        //Setting Winnie position on the map
        SecureRandom rand = new SecureRandom();
        int row = rand.nextInt(forest_size);
        int column = rand.nextInt(forest_size);
        forest_map.get(row).set(column, true);
        System.out.println("WP last spotted at (" + row + " , " + column + ") part of the forest");
        System.out.println("Suspect photo:\n                                     ....                          \r\n" + //
                    "                                    W$$$$$u                            \r\n" + //
                    "                                    $$$$F**+           .oW$$$eu        \r\n" + //
                    "                                    ..ueeeWeeo..      e$$$$$$$$$       \r\n" + //
                    "                                .eW$$$$$$$$$$$$$$$b- d$$$$$$$$$$W      \r\n" + //
                    "                    ,,,,,,,uee$$$$$$$$$$$$$$$$$$$$$ H$$$$$$$$$$$~      \r\n" + //
                    "                 :eoC$$$$$$$$$$$C\"\"?$$$$$$$$$$$$$$$ T$$$$$$$$$$\"       \r\n" + //
                    "                  $$$*$$$$$$$$$$$$$e \"$$$$$$$$$$$$$$i$$$$$$$$F\"        \r\n" + //
                    "                  ?f\"!?$$$$$$$$$$$$$$ud$$$$$$$$$$$$$$$$$$$$*Co         \r\n" + //
                    "                  $   o$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$        \r\n" + //
                    "          !!!!m.*eeeW$$$$$$$$$$$f?$$$$$$$$$$$$$$$$$$$$$$$$$$$$$U       \r\n" + //
                    "          !!!!!! !$$$$$$$$$$$$$$  T$$$$$$$$$$$$$$$$$$$$$$$$$$$$$        \r\n" + //
                    "           *!!*.o$$$$$$$$$$$$$$$e,d$$$$$$$$$$$$$$$$$$$$$$$$$$$$$:     \r\n" + //
                    "          \"eee$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$C      \r\n" + //
                    "         b ?$$$$$$$$$$$$$$**$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$!      \r\n" + //
                    "         Tb \"$$$$$$$$$$$$$$*uL\"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$'      \r\n" + //
                    "          $$o.\"?$$$$$$$$F\" u$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$       \r\n" + //
                    "           $$$$en ```    .e$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$'        \r\n" + //
                    "            $$$B*  =*\"?.e$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$F        \r\n" + //
                    "             $$$W\"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\"         \r\n" + //
                    "              \"$$$o#$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\"          \r\n" + //
                    "             R: ?$$$W$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\" :!i.       \r\n" + //
                    "              !!n.?$???\"\"``.......,``````\"\"\"\"\"\"\"\"\"\"\"``   ...+!!!       \r\n" + //
                    "               !* ,+::!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*`         \r\n" + //
                    "               \"!?!!!!!!!!!!!!!!!!!!~ !!!!!!!!!!!!!!!!!!!~`            \r\n" + //
                    "               +!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!?!`             \r\n" + //
                    "             .!!!!!!!!!!!!!!!!!!!!!' !!!!!!!!!!!!!!!, !!!!             \r\n" + //
                    "            :!!!!!!!!!!!!!!!!!!!!!!' !!!!!!!!!!!!!!!!! `!!:            \r\n" + //
                    "         .+!!!!!!!!!!!!!!!!!!!!!~~!! !!!!!!!!!!!!!!!!!! !!!.           \r\n" + //
                    "        :!!!!!!!!!!!!!!!!!!!!!!!!!.`:!!!!!!!!!!!!!!!!!:: `!!+          \r\n" + //
                    "        \"~!!!!!!!!!!!!!!!!!!!!!!!!!!.~!!!!!!!!!!!!!!!!!!!!.`!!:        \r\n" + //
                    "            ~~!!!!!!!!!!!!!!!!!!!!!!! ;!!!!~` ..eeeeeeo.`+!.!!!!.      \r\n" + //
                    "          :..    `+~!!!!!!!!!!!!!!!!! :!;`.e$$$$$$$$$$$$$u .           \r\n" + //
                    "          $$$$$$beeeu..  `````~+~~~~~\" ` !$$$$$$$$$$$$$$$$ $b          \r\n" + //
                    "          $$$$$$$$$$$$$$$$$$$$$UU$U$$$$$ ~$$$$$$$$$$$$$$$$ $$o        \r\n" + //
                    "         !$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$. $$$$$$$$$$$$$$$~ $$$u        \r\n" + //
                    "         !$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$! $$$$$$$$$$$$$$$ 8$$$$.       \r\n" + //
                    "         !$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$X $$$$$$$$$$$$$$`u$$$$$W       \r\n" + //
                    "         !$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$! $$$$$$$$$$$$$\".$$$$$$$:      \r\n" + //
                    "          $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  $$$$$$$$$$$$F.$$$$$$$$$      \r\n" + //
                    "          ?$$$$$$$$$$$$$$$$$$$$$$$$$$$$f $$$$$$$$$$$$' $$$$$$$$$$.     \r\n" + //
                    "           $$$$$$$$$$$$$$$$$$$$$$$$$$$$ $$$$$$$$$$$$$  $$$$$$$$$$!     \r\n" + //
                    "           \"$$$$$$$$$$$$$$$$$$$$$$$$$$$ ?$$$$$$$$$$$$  $$$$$$$$$$!     \r\n" + //
                    "            \"$$$$$$$$$$$$$$$$$$$$$$$$Fib ?$$$$$$$$$$$b ?$$$$$$$$$      \r\n" + //
                    "              \"$$$$$$$$$$$$$$$$$$$$\"o$$$b.\"$$$$$$$$$$$  $$$$$$$$'      \r\n" + //
                    "             e. ?$$$$$$$$$$$$$$$$$ d$$$$$$o.\"?$$$$$$$$H $$$$$$$'       \r\n" + //
                    "            $$$W.`?$$$$$$$$$$$$$$$ $$$$$$$$$e. \"??$$$f .$$$$$$'        \r\n" + //
                    "           d$$$$$$o \"?$$$$$$$$$$$$ $$$$$$$$$$$$$eeeeee$$$$$$$\"          \r\n" + //
                    "           $$$$$$$$$bu \"?$$$$$$$$$ 3$$$$$$$$$$$$$$$$$$$$*$$\"           \r\n" + //
                    "          d$$$$$$$$$$$$$e. \"?$$$$$:`$$$$$$$$$$$$$$$$$$$$8              \r\n" + //
                    "  e$$e.   $$$$$$$$$$$$$$$$$$+  \"??f \"$$$$$$$$$$$$$$$$$$$$c             \r\n" + //
                    " $$$$$$$o $$$$$$$$$$$$$$$F\"          `$$$$$$$$$$$$$$$$$$$$b.           \r\n" + //
                    "M$$$$$$$$U$$$$$$$$$$$$$F\"              ?$$$$$$$$$$$$$$$$$$$$$u         \r\n" + //
                    "?$$$$$$$$$$$$$$$$$$$$F                   \"?$$$$$$$$$$$$$$$$$$$$u       \r\n" + //
                    " \"$$$$$$$$$$$$$$$$$$\"                       ?$$$$$$$$$$$$$$$$$$$$o     \r\n" + //
                    "   \"?$$$$$$$$$$$$$F                            \"?$$$$$$$$$$$$$$$$$$    \r\n" + //
                    "      \"??$$$$$$$F                                 \"\"?3$$$$$$$$$$$$F    \r\n" + //
                    "                                                .e$$$$$$$$$$$$$$$$'    \r\n" + //
                    "                                               u$$$$$$$$$$$$$$$$$      \r\n" + //
                    "                                              `$$$$$$$$$$$$$$$$\"       \r\n" + //
                    "                                               \"$$$$$$$$$$$$F\"         \r\n" + //
                    "                                                 \"\"?????\"\"            " );
    }

    public void start_search(){
        for(int i = 0; i < threads_num; i++){
            Thread thr = new SearchThread("Team " + i);
            thr.start();
        }
    }

    private void find_and_punish(int sector_id){
        if(WP_is_punished.get() == true){
            System.out.println("WP has already been found and punished!");
            return;
        }

        System.out.println("Search team " + Thread.currentThread().getName() + " conducting search in sector " + sector_id);
        List<Boolean> sector = forest_map.get(sector_id);
        int wp_id = sector.indexOf(true);
        if(wp_id != -1){
            System.out.println("WP located in area " + wp_id + " at sector " + sector_id + ". Permission to punish granted. Good job team " + Thread.currentThread().getName() + ".");
            WP_is_punished.set(true);
        }
        else{
            System.out.println(Thread.currentThread().getName() + " didn`t find WP at sector " + sector_id + "\n");
        }
    }
}
