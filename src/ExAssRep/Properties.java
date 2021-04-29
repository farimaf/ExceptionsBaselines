package ExAssRep;

import Random.PredictRandom;

public class Properties {
    public static String mode;
    public static String pathToMusFile;
    public static String pathToRosFile;
    public static String pathToExceptionsFile;
    public static String pathToExceptionsFreqFile;
    static {
        if(PredictExceptionsPerMethod.mode!=null) {
            mode = PredictExceptionsPerMethod.mode;
        }
        else {
            mode= PredictRandom.mode;
        }
        if(mode.equals("win")){
            pathToMusFile="C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\ExAssistReplication" +
                    "\\EvalBaseLines\\train\\Mus_PerTryNoRuntimeLiteral_ProcessedFilesSample.txt";
            pathToRosFile="C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\ExAssistReplication\\" +
                    "EvalBaseLines\\train\\Ros_PerTryNoRuntimeLiteral_ProcessedFilesSample.txt";
            pathToExceptionsFile="C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\ExAssistReplication\\" +
                    "EvalBaseLines\\ExcpetionsToPredict.txt";
            pathToExceptionsFreqFile="C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\ExAssistReplication\\" +
                    "EvalBaseLines\\ExcpetionsToPredictWithFreq.txt";
        } else if(mode.equals("linux")){
            pathToMusFile="/scratch/mondego/local/farima/drex/baselines/ExceptionsBaselines/src/train/Mus_Train_PerTryNoRunTimeLiteral_Consolidated_ExAssist_WithLineInfo.txt";
            pathToRosFile="/scratch/mondego/local/farima/drex/baselines/ExceptionsBaselines/src/train/Ros_Train_PerTryNoRunTimeLiteral_Consolidated_ExAssist_WithLineInfo.txt";
            pathToExceptionsFile="/scratch/mondego/local/farima/drex/baselines/ExceptionsBaselines/ExcpetionsToPredict.txt";
            pathToExceptionsFreqFile="/scratch/mondego/local/farima/drex/baselines/ExceptionsBaselines/ExcpetionsToPredictWithFreq.txt";
        }
    }

}
