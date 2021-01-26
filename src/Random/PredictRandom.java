package Random;

import ExAssRep.Properties;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

public class PredictRandom {
    private static HashMap<Integer,String> excepFreqMap=new HashMap<>();
    private static HashMap<String,String> trueLabels=new HashMap<>();
    private static HashMap<String, ArrayList<String>> allPredictions=new HashMap<>();
    private static int totalFreqNum=0;
    public static String mode;

    private static void loadExceptions(){
        try {
            BufferedReader bf = new BufferedReader(new FileReader(Properties.pathToExceptionsFreqFile));
            String line="";
            int rangePointer=0;
            while ((line= bf.readLine())!=null){
                String[] lineSplit=line.split(":");
                totalFreqNum+=Integer.parseInt(lineSplit[1]);
                int startRange=rangePointer;
                int endRange=rangePointer+(Integer.parseInt(lineSplit[1]));
                for (int i = startRange; i <endRange ; i++) {
                    excepFreqMap.put(i,lineSplit[0]);
                }
                rangePointer=rangePointer+Integer.parseInt(lineSplit[1]);
            }
            System.out.println("total num freq: "+totalFreqNum);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        if(args.length==2){
            mode=args[1];
            loadExceptions();
            String predDir="predictions_random";
            File directory = new File(predDir);
            if (! directory.exists()){
                directory.mkdir();
            }
            try {
                String[] filePathSplit=args[0].split(Pattern.quote(System.getProperty("file.separator")));
                PrintWriter pwPred=new PrintWriter(predDir+File.separator+"Predictions_"+filePathSplit[filePathSplit.length-1]);
                PrintWriter pwLinesWithNegIndex=new PrintWriter(predDir+File.separator+"LinesWithNegIndexes.txt");

                BufferedReader bf = new BufferedReader(new FileReader(args[0]));
                String line="";
                int numLine=0;
                while ((line= bf.readLine())!=null){
                    numLine++;
                    String[] lineSplit=line.split("@#@");
                    String[] methodExcepSplit=lineSplit[1].split("#");
//                    String[] methods=methodExcepSplit[0].split(",");
                    String[] exceptions=methodExcepSplit[2].split(",");
                    String exceptionToConsider=exceptions[0];//cause we only consider the first exception type
                    trueLabels.put(line,exceptionToConsider);
                    String[] indexes=methodExcepSplit[3].split(",");
                    int startTryIndex=Integer.parseInt(indexes[0]);
                    int endTryIndex=Integer.parseInt(indexes[1]);
                    if(startTryIndex>-1 && endTryIndex>-1) {
                        ArrayList<String> predictions=predictExcepMethod();
                        allPredictions.put(line,predictions);
                    }
                    else {
                        pwLinesWithNegIndex.write(line+System.lineSeparator());
                    }

                }

                int numTop1True=0;
                int numTop2True=0;
                int numTop3True=0;
                int numTop5True=0;
                int numTop10True=0;
                for(String method:allPredictions.keySet()) {
//                    if (ro_c.get(method) > pecentile50Codes) {
                    String predLineToWrite = method + "@#@";
                    int counter = 0;
                    ArrayList<String> predictions=allPredictions.get(method);
                    for (String excePred : predictions) {
                        predLineToWrite += excePred+ ",";
                        if (++counter > 10) {
                            break;
                        }
                    }
                    pwPred.write(predLineToWrite.substring(0, predLineToWrite.length() - 1) + System.lineSeparator());

                        numTop1True = isTrueTopK(trueLabels.get(method), 1, predictions) ? numTop1True + 1 : numTop1True;
                        numTop2True = isTrueTopK(trueLabels.get(method), 2, predictions) ? numTop2True + 1 : numTop2True;
                        numTop3True = isTrueTopK(trueLabels.get(method), 3, predictions) ? numTop3True + 1 : numTop3True;
                        numTop5True = isTrueTopK(trueLabels.get(method), 5, predictions) ? numTop5True + 1 : numTop5True;
                        numTop10True = isTrueTopK(trueLabels.get(method), 10, predictions) ? numTop10True + 1 : numTop10True;

//                    }
                }
                pwPred.close();
                pwLinesWithNegIndex.close();
                System.out.println("Num all samples: "+allPredictions.size());
                System.out.println("Num rows in true labels: "+trueLabels.size());
                System.out.println("total num lines read: "+numLine);
                System.out.println("Num top 1 true predictions: "+numTop1True);
                System.out.println("Num top 2 true predictions: "+numTop2True);
                System.out.println("Num top 3 true predictions: "+numTop3True);
                System.out.println("Num top 5 true predictions: "+numTop5True);
                System.out.println("Num top 10 true predictions: "+numTop10True);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<String> predictExcepMethod(){
        ArrayList<String> preds=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {

            int random_int = (int)(Math.random() * (totalFreqNum - 0 + 1) + 0);
            while (preds.contains(excepFreqMap.get(random_int))){
                random_int = (int)(Math.random() * (totalFreqNum - 0 + 1) + 0);
            }
            preds.add(excepFreqMap.get(random_int));

        }
        return preds;
    }

    private static boolean isTrueTopK(String trueLabel,int k,ArrayList<String> predictions){
        boolean isTrue=false;
        int counter=0;
        for(String pred:predictions){
            counter++;
            System.out.println("========================");
            System.out.println(pred);
            System.out.println(trueLabel);
            if(pred.equals(trueLabel)  && counter<=k){
                isTrue=true;
                break;
            }
        }
        return isTrue;
    }
}
