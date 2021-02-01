package ExAssRep;

public class PositionRow {
    public PositionRow(){

    }

    public PositionRow(int startLineNum, int endLineNum) {
        this.startLineNum = startLineNum;
        this.endLineNum = endLineNum;
    }

    public int startLineNum;
    public int endLineNum;
    public String line;//optional, used in exass only for writing
}
