package LDS;

import java.awt.*;
import java.util.ArrayList;
import TetrisCode.Tetris;

public class Task {
    
    static int BIG_CIRCLE_RADIUS = 170;
    static int IMAGE_RADIUS = 80;

    static int RING_RADIUS = 50;
    static int SYMBOL_RADIUS = 30;

    int MouseX, MouseY;
    Graphics2D g2;
    boolean Button1 = false;
    boolean Button3 = false;
    boolean OldButton1;
    boolean OldButton3;
    
    int TaskCounter = 0;
    int RepetitionNo = 1;
    
    CalibrateMouse CalMouse = new CalibrateMouse();
    public static long MasterStartTime; 
    DoneBtn ContinueBtn = new DoneBtn(0, 0);
    
    int PresentGamePercent[] = { 10, 90 };
    public static int PresentRandomGamePercent[] = { 10, 90 };
    public static boolean UseRandomGamePercent;
    
//    String LettersUsed[] = { "T", "P" };

    public static String FirstLettersUsed = "";
    public static String SecondLettersUsed = "";
    public static int NoOfSymbols;
    public static int LetterIndex[] = new int[2];
//    public static int SecondLetterIndex[] = new int[2];
    public static double OriginalSymbolDisplayTime;
    public static double SymbolDisplayTime;
    
    
    int BlockNo = 0;
    static int BlockCounter = 0;
    int NoOfBlocks;    
    int NoOfTrials;
    int TrialNo;
    int BlockCount = 0;
  
    public static Color TextBackgroundColor = new Color(0, 0, 0);
    public static Color TextColor = new Color(0, 0, 0);
    public static int TextYSpacing = 30;
    public static int TextFontSize = 15;
    public String TextLines[] = new String[200];
    public static int TextXStart = 0;
    public static int TextYStart = 0;
    
    public boolean IsTextBlock;
    boolean RandomizeTrials = true;
    boolean RandomizeNames;
    String Marker = "EMPTY";
    public static SwitchingTask ST = null;
    public static FocusTask FT = null;
    public static AttentionalBlinkTask ABT = null;
    int Order[] = new int[1000];
    
    ArrayList BlockList = new ArrayList();
  
    boolean FirstTime = true;
    boolean FirstWriteToFile = true;
    
    static int SWITCHING_TASK = 0;
    static int FOCUS_TASK = 1;
    static int TEXT_TASK = 2;
    static int TETRIS_TEXT_TASK = 3;
    int TaskMode;
 
    String FileName = "";
    
    boolean ShowAccuracy;
    boolean TrialsToCriterion;
    int CriterionPercent;
    int TasksToSkipBackwards;

    boolean TrialsLimited;
    int CriterionToTrials;
    
    boolean SkipTetris=false;
    boolean SkipTask=false;
    boolean AdaptiveTextFork=false;
    
    int TotalCorrect;
    int TotalAnswered;
    double PercentCorrect;
    
    public Task(int ModeIn) {
        
        BlockNo = BlockCounter++;
        
        TaskMode = ModeIn;
        
        if ((TaskMode == TEXT_TASK) | (TaskMode == TETRIS_TEXT_TASK)) {
            
            ContinueBtn.AddBtn("DONE");
            
            for (int i = 0; i < TextLines.length; i++) 
                TextLines[i] = "";
            
            IsTextBlock = true;

            return;
        
        }
        
        if(AdaptiveTextFork && Tetris.isAdaptive){
            for (int i = 0; i < 1000; i++) Order[i] = i-50;
            // Dijkstra have mercy on my soul
            // I know this is a hack but its orders of magnitude faster than
            //   trying to shift through and rewrite a mountain of spaghetti code to fix it
        } else{
            for (int i = 0; i < 1000; i++) Order[i] = i;
        }

    }
    
    public void MakeTrials(int NoOfTrialsIn) {
        
        NoOfTrials = NoOfTrialsIn;
        
        for (int i = 0; i < NoOfBlocks; i++) {

//            W("MAKING BLOCK "+i);

//            W("NoOfBlocks="+NoOfBlocks);
            
            if (TaskMode == SWITCHING_TASK) {
                ST = new SwitchingTask(this, NoOfBlocks);
                BlockList.add(ST);
                
            }
            
            if (TaskMode == FOCUS_TASK) {
                FT = new FocusTask(this, NoOfBlocks);
                BlockList.add(FT);
                
            }
        
        }
        
    }
    
    public void W(String S) {
        System.out.println(S);
    }

    public void RandomizeOrder() {

       int Size = NoOfBlocks;
       
       for (int n = 0; n < 10000; n++) 
           
            for (int i = 0; i < Size; i++) {
                int Target1 = ControlCode.R.nextInt(Size);
                int Target2 = ControlCode.R.nextInt(Size);
                int Temp = Order[Target1];
                Order[Target1] = Order[Target2];
                Order[Target2] = Temp;
            }
        
    }
        
    public boolean Update(Graphics2D g2in, int mXin, int mYin, boolean Button1, boolean Button2, boolean Button3) {
        
      MouseX = mXin;
      MouseY = mYin;
      g2 = g2in;
//     W("IN BLOCK");
     
      if (IsTextBlock) {
          

          FocusTask.LastCorrectStr = "";
          FocusTask.LastAccuracyStr = "";
          FocusTask.FirstSecondLetterStr = "";
          SwitchingTask.LastCorrectStr = "";
          SwitchingTask.LastAccuracyStr = "";
          SwitchingTask.FirstSwitched[0] = SwitchingTask.FirstSwitched[1] = true;
          
          g2.setPaint(TextBackgroundColor);        
          g2.fillRect(0, 0, ControlCode.ScreenSizeX, ControlCode.ScreenSizeY);

          g2.setPaint(TextColor);
          g2.setFont(new Font("Helvetica", Font.PLAIN, TextFontSize));

          int x = TextXStart;
          int y = TextYStart + TextFontSize;
          //W("x="+x+" y="+y);

          for (int i = 0; i < 40; i++) {

              g2.drawString(TextLines[i], x, y); 
              y += TextYSpacing;

          }   

          ContinueBtn.Xo = (ControlCode.ScreenSizeX - ContinueBtn.buttonWidth)/2;
          ContinueBtn.Yo = ControlCode.ScreenSizeY - 3 * ContinueBtn.buttonHeighth;
          
          int ContinueNo = ContinueBtn.Update(g2, MouseX, MouseY, Button1);

          if (ContinueNo == 0) {
              ContinueBtn.SelectBtn(-1);
              //System.exit(123);
              return true;
          }

          return false;

      }

      if (RandomizeTrials) {
          RandomizeOrder();
      }
      
      RandomizeTrials = false;
      
      //if (G) g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
      //                    RenderingHints.VALUE_ANTIALIAS_ON);

      if (CalibrateMouse.CalibrateMouseMode != CalibrateMouse.MOUSE_CALIBRATED) {
          CalMouse.Update(g2in, mXin, mYin, Button1, Button3, this);
          return false;
      }
      
      int TaskNo = Order[BlockCount];

      if (UseRandomGamePercent) {
          PresentGamePercent[0] = PresentRandomGamePercent[0];
          PresentGamePercent[1] = PresentRandomGamePercent[1];
      }
      
      if (TaskMode == SWITCHING_TASK) {
          
          ST = (SwitchingTask)BlockList.get(TaskNo);
              
          if (ST.Update(g2, MouseX, MouseY, Button1, Button3, this)) {
              TrialNo = 0;
              BlockCount++;
              if (BlockCount == BlockList.size()) {
                  return true;
              }
          }
          
      }
      
      if (TaskMode == FOCUS_TASK) {
          
          FT = (FocusTask)BlockList.get(TaskNo);
              
          if (FT.Update(g2, MouseX, MouseY, Button1, Button2, Button3, this)) {
              TrialNo = 0;
              BlockCount++;
              if (BlockCount == BlockList.size()) return true;  //Parameters.ExperimentDone = true;
          }
          
      }
            
      OldButton1 = Button1;
      OldButton3 = Button3;

      return false;
      
    }
    
}
