package ExAssRep;

public class Properties {
    public static String mode;
    public static String pathToMusFile;
    public static String pathToRosFile;
    public static String pathToExceptionsFile;

    static {
        mode=PredictExceptionsPerMethod.mode;
        if(mode.equals("win")){
            pathToMusFile="C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\ExAssistReplication" +
                    "\\EvalBaseLines\\train\\Mus_PerTryNoRuntimeLiteral_ProcessedFilesSample.txt";
            pathToRosFile="C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\ExAssistReplication\\" +
                    "EvalBaseLines\\train\\Ros_PerTryNoRuntimeLiteral_ProcessedFilesSample.txt";
            pathToExceptionsFile="C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\ExAssistReplication\\" +
                    "EvalBaseLines\\ExcpetionsToPredict.txt";
        } else if(mode.equals("linux")){
            pathToMusFile="/scratch/mondego/local/farima/drex/baselines/ExceptionsBaselines/src/train/Mus_Train_PerTryNoRunTimeLiteral_Consolidated_ExAssist.txt";
            pathToRosFile="/scratch/mondego/local/farima/drex/baselines/ExceptionsBaselines/src/train/Ros_Train_PerTryNoRunTimeLiteral_Consolidated_ExAssist.txt";
            pathToExceptionsFile="/scratch/mondego/local/farima/drex/baselines/ExceptionsBaselines/ExcpetionsToPredict.txt";
        }
    }

}
