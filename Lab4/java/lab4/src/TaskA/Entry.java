package TaskA;

public class Entry {
    public String name;
    public Integer number;

    public Entry(){};
    public Entry(String name, Integer number){
        this.name = name;
        this.number = number;
    }

    @Override
    public String toString(){
        return name + " - " + number.toString() + "\n";
    }
}
