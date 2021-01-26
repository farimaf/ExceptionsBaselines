package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class getTrainTestSet {
    private static HashMap<String,Integer> trainSetNames=new HashMap<>();
    private static HashMap<String,Integer> testSetNames=new HashMap<>();

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
//                PrintWriter pwDebug=new PrintWriter("test.txt");

//                System.out.println();
                BufferedReader bf = new BufferedReader(new FileReader(trainsetPath));
                String line="";
                while ((line= bf.readLine())!=null){
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");
                    if (trainSetNames.containsKey(lineSplit[0]+"#"+lineMethodSplit[2])){
                        trainSetNames.put(lineSplit[0]+"#"+lineMethodSplit[2]+"#"+lineMethodSplit[4],trainSetNames.get(lineSplit[0]+"#"+lineMethodSplit[2])+1);

                    }
                    else{
                        trainSetNames.put(lineSplit[0]+"#"+lineMethodSplit[2]+"#"+lineMethodSplit[4],1);
                    }

                }

                bf = new BufferedReader(new FileReader(testsetPath));
                line="";
                int negativeIndexes=0;
                int not2numsign=0;
                int lineNum=0;
                while ((line= bf.readLine())!=null){
                    lineNum++;
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");
                    if (testSetNames.containsKey(lineSplit[0]+"#"+lineMethodSplit[2])){
                        testSetNames.put(lineSplit[0]+"#"+lineMethodSplit[2]+"#"+lineMethodSplit[4],testSetNames.get(lineSplit[0]+"#"+lineMethodSplit[2])+1);

                    }
                    else{
                        testSetNames.put(lineSplit[0]+"#"+lineMethodSplit[2]+"#"+lineMethodSplit[4],1);
                    }
                }

                bf = new BufferedReader(new FileReader(mainfilePath));
                line="";

                while ((line= bf.readLine())!=null){
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");

                    String whatToLook=lineSplit[0]+"#"+lineMethodSplit[1]+"#"+lineMethodSplit[3];

                        if (trainSetNames.containsKey(whatToLook)) {
                            if(trainSetNames.get(whatToLook)>0) {
                                pwTrain.write(line + System.lineSeparator());
                                trainSetNames.put(whatToLook,trainSetNames.get(whatToLook)-1);
                            }
                        } else if (testSetNames.containsKey(whatToLook)) {
                            if(testSetNames.get(whatToLook)>0) {
                                pwTest.write(line + System.lineSeparator());
                                testSetNames.put(whatToLook,testSetNames.get(whatToLook)-1);
                            }

                        }
                        else {
                            System.out.println(whatToLook);

                        }
//                    }
                }

                pwTest.close();
                pwTrain.close();
//                pwDebug.close();
                System.out.println("num neg index: "+negativeIndexes);
//                System.out.println("num not 2 num sign: "+not2numsign);
                System.out.println("file len: "+lineNum);
                System.out.println("train set len "+trainSetNames.size());
                System.out.println("test set len "+testSetNames.size());

//                for (int key:lens.keySet()){
//                    System.out.println(key+ ": "+ lens.get(key));
//                }

            }
            catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
