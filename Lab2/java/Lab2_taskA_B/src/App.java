
public class App {
    public static void main(String[] args) throws Exception {
        BeeSearch beeSearch = new BeeSearch(100);
        beeSearch.start_search();
        Robbery robbery = new Robbery(30);
        robbery.start_heist();
    }
}
