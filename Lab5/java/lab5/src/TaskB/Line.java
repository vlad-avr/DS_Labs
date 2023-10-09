package TaskB;

import java.security.SecureRandom;

public class Line {
    private String line;

    public Line(int N, SecureRandom rnd) {
        line = "";
        for (int i = 0; i < N; i++) {
            int toss = rnd.nextInt(4);
            switch (toss) {
                case 0:
                    line += "A";
                    break;
                case 1:
                    line += "B";
                    break;
                case 2:
                    line += "C";
                    break;
                case 3:
                    line += "D";
                    break;
                default: break;
            }
        }
    }
}
