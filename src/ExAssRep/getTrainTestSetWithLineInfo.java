package ExAssRep;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class getTrainTestSetWithLineInfo {
    private static HashMap<String, ArrayList<PositionRow>> trainSetNames=new HashMap<>();
    private static HashMap<String, ArrayList<PositionRow>> testSetNames=new HashMap<>();
    private static HashMap<String, ArrayList<PositionRow>> mainfileNames=new HashMap<>();
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
                PrintWriter pwNeg=new PrintWriter("NegIndex_"+mainfilePathSplit[mainfilePathSplit.length-1]);

//                PrintWriter pwDebug=new PrintWriter("test.txt");

//                System.out.println();
                BufferedReader bf = new BufferedReader(new FileReader(trainsetPath));
                String line="";
                while ((line= bf.readLine())!=null){
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");
                    String exceptions=lineMethodSplit[2];
                    String[] lineNums=lineMethodSplit[4].split(",");
                    PositionRow currPosRow=new PositionRow(Integer.parseInt(lineNums[0]),Integer.parseInt(lineNums[1]));
                    if (trainSetNames.containsKey(lineSplit[0]+"#"+exceptions)){
                        trainSetNames.get(lineSplit[0]+"#"+exceptions).add(currPosRow);
//                        trainSetNames.put(lineSplit[0]+"#"+exceptions,trainSetNames.get(lineSplit[0]+"#"+lineMethodSplit[2])+1);

                    }
                    else{
                        ArrayList<PositionRow> positionRows=new ArrayList<>();
                        positionRows.add(currPosRow);
                        trainSetNames.put(lineSplit[0]+"#"+exceptions,positionRows);
                    }

                }

                bf = new BufferedReader(new FileReader(testsetPath));
                line="";
                while ((line= bf.readLine())!=null){
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");
                    String exceptions=lineMethodSplit[2];
                    String[] lineNums=lineMethodSplit[4].split(",");
                    PositionRow currPosRow=new PositionRow(Integer.parseInt(lineNums[0]),Integer.parseInt(lineNums[1]));
                    if (testSetNames.containsKey(lineSplit[0]+"#"+exceptions)){
                        testSetNames.get(lineSplit[0]+"#"+exceptions).add(currPosRow);
//                        trainSetNames.put(lineSplit[0]+"#"+exceptions,trainSetNames.get(lineSplit[0]+"#"+lineMethodSplit[2])+1);

                    }
                    else{
                        ArrayList<PositionRow> positionRows=new ArrayList<>();
                        positionRows.add(currPosRow);
                        testSetNames.put(lineSplit[0]+"#"+exceptions,positionRows);
                    }
                }

                bf = new BufferedReader(new FileReader(mainfilePath));
                line="";

                while ((line= bf.readLine())!=null){
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");

                    String exceptions=lineMethodSplit[1];
                    String[] lineNums=lineMethodSplit[3].split(",");
                    if(!lineNums[0].equals("null")&&!lineNums[1].equals("null")) {
                        PositionRow currPosRow = new PositionRow(Integer.parseInt(lineNums[0]), Integer.parseInt(lineNums[1]));
                        currPosRow.line = line;
                        if (mainfileNames.containsKey(lineSplit[0] + "#" + exceptions)) {
                            mainfileNames.get(lineSplit[0] + "#" + exceptions).add(currPosRow);
//                        trainSetNames.put(lineSplit[0]+"#"+exceptions,trainSetNames.get(lineSplit[0]+"#"+lineMethodSplit[2])+1);

                        } else {
                            ArrayList<PositionRow> positionRows = new ArrayList<>();
                            positionRows.add(currPosRow);
                            mainfileNames.put(lineSplit[0] + "#" + exceptions, positionRows);
                        }
                    }
                    else {
                        pwNeg.write(line+System.lineSeparator());
                    }
                }

                for(String key:mainfileNames.keySet()){
                    if(trainSetNames.containsKey(key)&&testSetNames.containsKey(key)){
                        ArrayList<PositionRow> currentRows=mainfileNames.get(key);
                        ArrayList<PositionRow> trainRows=trainSetNames.get(key);
                        ArrayList<PositionRow> testRows=testSetNames.get(key);
                        for (PositionRow row: currentRows){
                            int startLine=row.startLineNum;
                            int endLine=row.endLineNum;
                            String candidRow="";
                            int minStart=Integer.MAX_VALUE;
                            int minEnd=Integer.MAX_VALUE;
                            for (PositionRow trainRow: trainRows){
                                if(startLine>=trainRow.startLineNum&&endLine<=trainRow.endLineNum){
                                    int startDist=startLine-trainRow.startLineNum;
                                    int endDist=trainRow.endLineNum-endLine;
                                    if(startDist<minStart || endDist<minEnd){
                                        candidRow="train";
                                        minStart=startDist;
                                        minEnd=endDist;
                                    }
                                }
                            }
                            for (PositionRow testRow: testRows){
                                if(startLine>=testRow.startLineNum&&endLine<=testRow.endLineNum){
                                    int startDist=startLine-testRow.startLineNum;
                                    int endDist=testRow.endLineNum-endLine;
                                    if(startDist<minStart || endDist<minEnd){
                                        candidRow="test";
                                        minStart=startDist;
                                        minEnd=endDist;
                                    }
                                }
                            }
                            if (candidRow.equals("train")){
                                String lineToWrite=row.line.substring(0,row.line.lastIndexOf("#"));
                                pwTrain.write(lineToWrite + System.lineSeparator());
                            }
                            else if(candidRow.equals("test")){
                                String lineToWrite=row.line.substring(0,row.line.lastIndexOf("#"));
                                pwTest.write(lineToWrite + System.lineSeparator());
                            }

                        }
                        trainSetNames.remove(key);
                        testSetNames.remove(key);
                    }
                    else if(trainSetNames.containsKey(key)){
                        ArrayList<PositionRow> currentRows=mainfileNames.get(key);
                        for (PositionRow row: currentRows){
                            String lineToWrite=row.line.substring(0,row.line.lastIndexOf("#"));
                            pwTrain.write(lineToWrite + System.lineSeparator());
                        }
                        trainSetNames.remove(key);

                    }
                    else if(testSetNames.containsKey(key)){
                        ArrayList<PositionRow> currentRows=mainfileNames.get(key);
                        for (PositionRow row: currentRows){
                            String lineToWrite=row.line.substring(0,row.line.lastIndexOf("#"));
                            pwTest.write(lineToWrite + System.lineSeparator());
                        }
                        testSetNames.remove(key);

                    }
                }

                pwTest.close();
                pwTrain.close();
                pwNeg.close();
//                pwDebug.close();
//                System.out.println("num neg index: "+negativeIndexes);
////                System.out.println("num not 2 num sign: "+not2numsign);
//                System.out.println("file len: "+lineNum);
//                System.out.println("len 3 num: "+len3);
//                System.out.println("train set len "+trainSetNames.size());
//                System.out.println("test set len "+testSetNames.size());

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
