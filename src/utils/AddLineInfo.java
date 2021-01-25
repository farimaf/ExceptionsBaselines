package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class AddLineInfo {
    public static void main(String[] args) {
        if (args.length == 2) {
            HashMap<String,Integer[]> linesPositionInfoMap=new HashMap<>();
            try {
                String mainFilePath=args[0];
                String positionedFilePath=args[1];
                String[] mainFilePathSplit=mainFilePath.split(Pattern.quote(System.getProperty("file.separator")));
                String mainFileName=mainFilePathSplit[mainFilePathSplit.length-1];
                PrintWriter pw=new PrintWriter(mainFileName.substring(0,mainFileName.indexOf(".txt"))+"_WithLineInfo.txt");
                PrintWriter pwLinesWithNegIndex=new PrintWriter("LinesWithNegIndexes.txt");
                BufferedReader bf = new BufferedReader(new FileReader(positionedFilePath));
                String line="";
                while ((line= bf.readLine())!=null){
                    String[] lineSplit=line.split("@#@");
                    String[] lineMethodSplit=lineSplit[1].split("#");
                    String bodyTokens=lineMethodSplit[lineMethodSplit.length-3];
                    String[] bodyTokensSplit=bodyTokens.split("\\),");
                    String[] indexes=lineMethodSplit[lineMethodSplit.length-1].split(",");
                    int startIndex=Integer.parseInt(indexes[0]);
                    int endIndex=Integer.parseInt(indexes[1]);
                    Integer[] lineNums=new Integer[2];
                    if(startIndex>-1 && endIndex>-1) {
                        String startToken=bodyTokensSplit[startIndex].endsWith(")")?bodyTokensSplit[startIndex].substring(0,bodyTokensSplit[startIndex].length()-1):bodyTokensSplit[startIndex];
                        String endToken=bodyTokensSplit[endIndex];
                        lineNums[0]=Integer.parseInt(startToken.substring(startToken.indexOf("(")+1,startToken.indexOf(",")));
                        lineNums[1]=Integer.parseInt(endToken.substring(endToken.indexOf("(")+1,endToken.indexOf(",")));

                    }
                     else {
                        pwLinesWithNegIndex.write(line+System.lineSeparator());
                    }
                    String excpetions=lineMethodSplit[lineMethodSplit.length-2];

                    String lineToConsider=lineSplit[0]+"#"+excpetions+"#"+indexes[0]+","+indexes[1];
                    linesPositionInfoMap.put(lineToConsider,lineNums);
                }

                bf = new BufferedReader(new FileReader(mainFilePath));
                line="";
                while ((line= bf.readLine())!=null) {
                    String[] lineSplit = line.split("@#@");
                    String[] lineMethodSplit = lineSplit[1].split("#");
                    String[] indexes = lineMethodSplit[lineMethodSplit.length - 1].split(",");
                    String excpetions=lineMethodSplit[lineMethodSplit.length-2];
                    String lineToConsider=lineSplit[0]+"#"+excpetions+"#"+indexes[0]+","+indexes[1];
                    Integer[] lineNums=linesPositionInfoMap.get(lineToConsider);
                    String lineToWrite=line+"#"+lineNums[0]+","+lineNums[1];
                    pw.write(lineToWrite+System.lineSeparator());
                }

                pw.close();
                pwLinesWithNegIndex.close();
            }
            catch(IOException e){
                    e.printStackTrace();
                }

        }
        else{
                System.out.println("Provide the path to two files!");
            }
        }

}
