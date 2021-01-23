package ExAssRep;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

public class PredictExceptionsPerMethod {
    public static String mode;
    private static HashMap<String,HashMap<String,Double>> mu_m_e=new HashMap<>();//key is method, val is hashmap with key of exception and value of mu for this method and exception
    private static HashMap<String,Double> ro_c=new HashMap<>();//key is code (method) address and line nums, val is the ro value for this code fragment
    private static HashMap<String,Double> ro_m=new HashMap<>();
    private static HashSet<String> allExceptions=new HashSet<>();
    private static void loadMus(){
        try {
            BufferedReader bf = new BufferedReader(new FileReader(Properties.pathToMusFile));
            String line="";
            while ((line= bf.readLine())!=null){
                String[] lineSplit=line.split(":");
                String method=lineSplit[0];
                String exception=lineSplit[1];
                double mu=Double.parseDouble(lineSplit[2]);
                if(mu_m_e.containsKey(method)){
                    HashMap<String,Double> excepMuMap=mu_m_e.get(method);
                    excepMuMap.put(exception,mu);
                    mu_m_e.put(method,excepMuMap);
                }else {
                    HashMap<String,Double> excepMuMap=new HashMap<>();
                    excepMuMap.put(exception,mu);
                    mu_m_e.put(method,excepMuMap);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
    private static void loadRos(double percentileValue){
        try {
            BufferedReader bf = new BufferedReader(new FileReader(Properties.pathToRosFile));
            String line="";
            while ((line= bf.readLine())!=null){
                String[] lineSplit=line.split(":");
                String method=lineSplit[0];
                double ro=Double.parseDouble(lineSplit[1]);
                if(ro>=percentileValue){
                    ro_m.put(method,ro);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void loadExceptions(){
        try {
            BufferedReader bf = new BufferedReader(new FileReader(Properties.pathToExceptionsFile));
            String line="";
            while ((line= bf.readLine())!=null){
                allExceptions.add(line);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        if(args.length==3){
            double percentileValue=Double.parseDouble(args[1]);
            mode=args[2];
            loadExceptions();
            loadMus();
            loadRos(percentileValue);
            try {
                String[] filePathSplit=args[0].split(Pattern.quote(System.getProperty("file.separator")));
                PrintWriter pwRo=new PrintWriter("Ros_code_"+filePathSplit[filePathSplit.length-1]);
                PrintWriter pwPred=new PrintWriter("Predictions_"+filePathSplit[filePathSplit.length-1]);

                BufferedReader bf = new BufferedReader(new FileReader(args[0]));
                String line="";
                while ((line= bf.readLine())!=null){
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
//                        ro_c.put(lineSplit[0],calculateRoForCodeFrag(methods));
                        double roCurrentCode=calculateRoForCodeFrag(methodsInTryBlock);
                        pwRo.write(lineSplit[0]+"@#@"+roCurrentCode+System.lineSeparator());
                        LinkedHashMap<String,Double> predictions=predictExcepMethod(methodsInTryBlock);
                        String predLineToWrite=lineSplit[0]+"@#@";
                        int counter=0;
                        for(String excePred:predictions.keySet()){
                            predLineToWrite+=excePred+":"+predictions.get(excePred)+",";
                            if(++counter>10){
                                break;
                            }
                        }
                        pwPred.write(predLineToWrite.substring(0,predLineToWrite.length()-1)+System.lineSeparator());
                    }

                }
//                String[] filePathSplit=args[0].split(Pattern.quote(System.getProperty("file.separator")));
//                PrintWriter pw=new PrintWriter("Mus_"+filePathSplit[filePathSplit.length-1]);
//                pw.close();
                pwPred.close();
                pwRo.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }

        }
        else{
            System.out.println("Provide the path to test dataset and percentile value and mode!");
        }
    }

    private static double calculateRoForCodeFrag(ArrayList<String> methods){
        double roCode;
        double ro_minuses_multiply=1;
        for(String method:methods){
            if(ro_m.containsKey(method)){
                double ro_meth_minus=1-ro_m.get(method);
                ro_minuses_multiply=ro_minuses_multiply*ro_meth_minus;
            }
        }
        if(ro_minuses_multiply==1.0){
            roCode=-1;
        }
        else {
            roCode = 1 - ro_minuses_multiply;
        }
        return roCode;
    }

    private static LinkedHashMap<String,Double> predictExcepMethod(ArrayList<String> methods){
        LinkedHashMap<String,Double> predictions=new LinkedHashMap<>();
        HashMap<String,Double> mu_c_e_allexcep=new HashMap<>();
        for (String excep:allExceptions){
            double mu_minuses_multiply=1;
            for (String method:methods){
                if(mu_m_e.containsKey(method)){
                    HashMap<String,Double> mu_method=mu_m_e.get(method);
                    if(mu_method.containsKey(excep)){
                        double mu_meth_minus=1-mu_method.get(excep);
                        mu_minuses_multiply=mu_minuses_multiply*mu_meth_minus;
                    }

                }
            }
            double mu_c_e=1-mu_minuses_multiply;
            mu_c_e_allexcep.put(excep,mu_c_e);
        }
        predictions=sortByValue(mu_c_e_allexcep);
        return predictions;
    }
//    private static boolean isExceptionProne(ArrayList<String> methods,double percentileVal){
//        double roCode;
//        double ro_minuses_multiply=1;
//        for(String method:methods){
//            if(ro_m.containsKey(method)){
//                double ro_meth_minus=1-ro_m.get(method);
//                ro_minuses_multiply=ro_minuses_multiply*ro_meth_minus;
//            }
//        }
//        roCode=1-ro_minuses_multiply;
//        if(roCode>percentileVal){
//            return true;
//        }
//        return false;
//    }
    public static LinkedHashMap<String, Double> sortByValue(HashMap<String, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        LinkedHashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
