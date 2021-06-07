package Random;

import java.util.HashSet;
import java.util.Random;

public class GetRandomSeed {
    public static void main(String[] args) {
        Random generator = new Random();
        HashSet<Integer> randomSet=new HashSet<>();
        while (randomSet.size()<5){
            int random_int = generator.nextInt(100);
            randomSet.add(random_int);
        }
        for (Integer i:randomSet) {
            System.out.println(i);
        }
    }
}
