package Random;

import java.util.Random;

public class GetRandomSeed {
    public static void main(String[] args) {
        Random generator = new Random(100);
        for (int i = 0; i <5 ; i++) {

            int random_int = generator.nextInt();
            System.out.println(random_int);

        }
    }
}
