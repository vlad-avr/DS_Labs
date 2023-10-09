package TaskA;

public class Recruit {
    private boolean direction;
    public Recruit(boolean dir){
        this.direction = dir;
    }

    public void changeDirection(){
        direction = !direction;
    }

    public boolean getDirection(){
        return this.direction;
    }
}
