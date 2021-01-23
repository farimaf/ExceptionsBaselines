package ExAssRep;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class TrainMethodExceptionScoreCalculation {
    private static HashMap<String,Integer> n_m=new HashMap<>();
    private static HashMap<Pair<String,String>,Integer> n_me=new HashMap<>();
    private static HashMap<Pair<String,String>,Double> mu_m_e=new HashMap<>();
    private static HashMap<String,Double> ro_m=new HashMap<>();
    private static PrintWriter pwLinesWithNegIndex;
    public static void main(String[] args) {
        if(args.length==1){
            try {
                pwLinesWithNegIndex=new PrintWriter("LinesWithNegIndexes");
                BufferedReader bf = new BufferedReader(new FileReader(args[0]));
                String line="";
                while ((line= bf.readLine())!=null){
                    processLine(line);
                }
                pwLinesWithNegIndex.close();

                calculateMus();
                String[] filePathSplit=args[0].split(Pattern.quote(System.getProperty("file.separator")));
                PrintWriter pw=new PrintWriter("Mus_"+filePathSplit[filePathSplit.length-1]);
                for (Pair<String,String> methodExcepPair:mu_m_e.keySet()){
                    String lineToWrite= methodExcepPair.getKey()+":"+methodExcepPair.getValue()+":"+mu_m_e.get(methodExcepPair);
                    pw.write(lineToWrite+System.lineSeparator());
                }
                pw.close();

                calculateRos();;
                pw=new PrintWriter("Ros_"+filePathSplit[filePathSplit.length-1]);
                for (String method:ro_m.keySet()){
                    String lineToWrite= method+":"+ro_m.get(method);
                    pw.write(lineToWrite+System.lineSeparator());
                }
                pw.close();

                double ros_50_percentile=findPercentile(50.0);
                System.out.println("50th percentile value for Ros is: "+ros_50_percentile);

            }
            catch (IOException e){
                e.printStackTrace();
            }

        }
        else{
            System.out.println("Provide the path to train dataset!");
        }
    }

    public static void processLine(String line){
        String[] lineSplit=line.split("@#@");
        String[] methodExcepSplit=lineSplit[1].split("#");
        String[] methods=methodExcepSplit[0].split(",");
        String[] exceptions=methodExcepSplit[1].split(",");
        String exceptionToConsider=exceptions[0];//cause we only consider the first exception type
        String[] indexes=methodExcepSplit[2].split(",");
        int startTryIndex=Integer.parseInt(indexes[0]);
        int endTryIndex=Integer.parseInt(indexes[1]);
        if(startTryIndex>-1 && endTryIndex>-1) {
            ArrayList<String> methodsInTryBlock = new ArrayList<>();
            for (int i = startTryIndex; i <= endTryIndex; i++) {
                methodsInTryBlock.add(methods[i]);
            }
            putN_m(methods);//consider any method call cause this is the total num of methods
            putN_me(methodsInTryBlock, exceptionToConsider);
        } else {
            pwLinesWithNegIndex.write(line+System.lineSeparator());
        }

    }

    private static void putN_m(String[] methods){
       // int increaseNum=exceptions.length;//to account for try blocks that have more than one exception, methods in these try blocks should be counted accordingly
        for(String method:methods){
            if (n_m.containsKey(method)){
                n_m.put(method,n_m.get(method)+1);
            } else {
                n_m.put(method,1);
            }
        }

    }

    private static void putN_me(ArrayList<String> methods,String exception){
        for(String method:methods){
           // for (String excep:exceptions){
                Pair<String,String> methodExcepPair=new Pair<>(method,exception);
                if (n_me.containsKey(methodExcepPair)){
                    n_me.put(methodExcepPair,n_me.get(methodExcepPair)+1);
                } else {
                    n_me.put(methodExcepPair,1);
                }
          //  }
        }
    }

    private static void calculateMus(){
        for (Pair<String,String> methodExcepPair:n_me.keySet()){
            String method=methodExcepPair.getKey();
            int methodFreq=n_m.get(method);
            double mu=n_me.get(methodExcepPair)/((double)methodFreq);
            if(mu>1){
                System.out.println("mu larger than 1: Mu is: "+mu+" ,method: "+method+ " : " +" ,method freq: "+methodFreq+" ,exception: "+methodExcepPair.getValue());
                mu=1.0;
            }
            mu_m_e.put(methodExcepPair,mu);
        }
    }
    private static void calculateRos(){
        for (Pair<String,String> methodExcepPair:mu_m_e.keySet()){
            String method=methodExcepPair.getKey();
            double mu_meth=mu_m_e.get(methodExcepPair);
            if(ro_m.containsKey(method)){
                ro_m.put(method,ro_m.get(method)+mu_meth);
            }
            else {
                ro_m.put(method,mu_meth);
            }
        }
    }

    public static Double findPercentile(double percentile) {
        List<Double> ros=new ArrayList<Double>(ro_m.values());
        Collections.sort(ros);
        int index = (int) Math.ceil(percentile / 100.0 * ros.size());
        return ros.get(index-1);
    }
}
