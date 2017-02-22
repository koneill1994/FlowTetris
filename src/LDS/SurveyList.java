package LDS;

public class SurveyList {
    
    boolean DoneFlag;
    
    public static int BEFORE_LDS = 0;
    public static int BEFORE_TETRIS = 1;
    int BeforeMode;
    
    static int SurveyCount = 0;
    int MySurveyNo = 0;
    
    boolean SurveyDone;
    String FileName = "";
    int BeforeSessionNo = 0;
    boolean SendReady;
    String ModeString = "";
    int Time; //set to HOME_TIME or SCHOOL_TIME

    public SurveyList(String FileName, String BeforeLDSorTETRIS, int SessionNo) {
        
        W("SURVEY LIST SESSION NO="+SessionNo);
        
        MySurveyNo = ++SurveyCount;
        
        this.FileName = FileName;
        
        BeforeSessionNo = SessionNo;
        
        if (BeforeLDSorTETRIS.equals("BEFORE_LDS"))
            BeforeMode = BEFORE_LDS;
        
        if (BeforeLDSorTETRIS.equals("BEFORE_TETRIS"))
            BeforeMode = BEFORE_TETRIS;
        
    }
    
    public void W(String S) {
        System.out.println(S);
    }

}