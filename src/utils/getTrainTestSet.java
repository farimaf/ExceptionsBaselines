package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class getTrainTestSet {
    private static HashSet<String> trainSetNames=new HashSet<>();
    private static HashSet<String> testSetNames=new HashSet<>();

    public static void main(String[] args) {
        if(args.length==3){
            String trainsetPath=args[0];
            String testsetPath=args[1];
            String mainfilePath=args[2];
            try {
//                String[] trainFilePathSplit=trainsetPath.split(Pattern.quote(System.getProperty("file.separator")));
//                String[] testFilePathSplit=testsetPath.split(Pattern.quote(System.getProperty("file.separator")));
                String[] mainfilePathSplit=mainfilePath.split(Pattern.quote(System.getProperty("file.separator")));

                PrintWriter pwTrain=new PrintWriter("Train_"+mainfilePathSplit[mainfilePathSplit.length-1]);
                PrintWriter pwTest=new PrintWriter("Test_"+mainfilePathSplit[mainfilePathSplit.length-1]);
                PrintWriter pwDebug=new PrintWriter("test.txt");

                System.out.println();
                BufferedReader bf = new BufferedReader(new FileReader(trainsetPath));
                String line="";
                while ((line= bf.readLine())!=null){
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");
                    trainSetNames.add(lineSplit[0]+"#"+lineMethodSplit[2]+"#"+lineMethodSplit[3]);
                }

                bf = new BufferedReader(new FileReader(testsetPath));
                line="";
                while ((line= bf.readLine())!=null){
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");
                    testSetNames.add(lineSplit[0]+"#"+lineMethodSplit[2]+"#"+lineMethodSplit[3]);
                    pwDebug.write(lineSplit[0]+"#"+lineMethodSplit[2]+"#"+lineMethodSplit[3]+System.lineSeparator());
                }

                bf = new BufferedReader(new FileReader(mainfilePath));
                line="";
                int negativeIndexes=0;
                int len3=0;
                int lineNum=0;
                HashMap<Integer,Integer> lens=new HashMap<>();
                while ((line= bf.readLine())!=null){
                    lineNum++;
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");
                    if(lens.containsKey(lineMethodSplit.length)){
                        lens.put(lineMethodSplit.length,lens.get(lineMethodSplit.length)+1);
                    }
                    else {
                        lens.put(lineMethodSplit.length,1);
                    }

                    String whatToLook=lineSplit[0]+"#"+lineMethodSplit[1]+"#"+lineMethodSplit[2];
                    String[] indexes=lineMethodSplit[2].split(",");
//                    System.out.println(indexes[0]);
//                    System.out.println(indexes[1]);
                    if(indexes[0].equals("-1") || indexes[1].equals("-1")){
                        negativeIndexes++;
                    }
//                    else if (lineMethodSplit.length!=3){
//                        not2numsign++;
//                    }
                    else {
                        len3++;
                        if (trainSetNames.contains(whatToLook)) {
                            pwTrain.write(line + System.lineSeparator());
                        } else if (testSetNames.contains(whatToLook)) {
                            pwTest.write(line + System.lineSeparator());
                        }
                        else {
                            System.out.println(whatToLook);

                        }
                    }
                }

                pwTest.close();
                pwTrain.close();
                pwDebug.close();
                System.out.println("num neg index: "+negativeIndexes);
//                System.out.println("num not 2 num sign: "+not2numsign);
                System.out.println("file len: "+lineNum);
                System.out.println("len 3 num: "+len3);
                System.out.println("train set len "+trainSetNames.size());
                System.out.println("test set len "+testSetNames.size());

                for (int key:lens.keySet()){
                    System.out.println(key+ ": "+ lens.get(key));
                }

            }
            catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
