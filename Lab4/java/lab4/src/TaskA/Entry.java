package TaskA;

public class Entry {
    public String name;
    public Integer number;

    public Entry() {
    };

    public Entry(String name, Integer number) {
        this.name = name;
        this.number = number;
    }

    public Entry(String parse_str) {
        this.name = parse_str.substring(0, parse_str.indexOf(" "));
        try {
            this.number = Integer.parseInt(parse_str.substring(parse_str.lastIndexOf(" "), parse_str.indexOf("\n")));
        } catch (NumberFormatException exception) {
            this.number = 0;
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public String toString() {
        return name + " - " + number.toString() + "\n";
    }
}
