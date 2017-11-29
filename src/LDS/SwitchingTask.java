package LDS;

import java.awt.*;
import java.io.*;
import java.util.*;

public class SwitchingTask {
    
    CurrentTime CT = new CurrentTime();

    int Xo = ControlCode.ScreenSizeX/2;
    int Yo = ControlCode.ScreenSizeY/2;

    Graphics2D g2;
    boolean Button1;
    boolean Button3;
    boolean OldButton1;
    boolean OldButton3; 
    int MouseX, MouseY;

    String GameType = "";
    String NumberStr = "";
    String CorrectAnswerStr = "";
    String SubjAnswerStr = "";

    long DisplayAnswerStartTime;

    int NumberIndex;
    Color NumberColor;

    //modes
    static int WAITING_TO_START = 0;
    static int MOUSE_MOVING = 1;
    static int NUMBER_SHOWING = 2;
    static int SHOW_DECISION = 3;

    String ModeStr[] = {
        "WAITING TO START",
        "MOUSE MOVING",
        "NUMBER SHOWING",
        "SHOW DECISION"
    };

    String ImageChosenString[] = {
        "IMAGE_0",
        "IMAGE_1"
    };
    
    long StartTime[] = new long[3];
    long StopTime[] = new long[3];

    public static int Mode = WAITING_TO_START;
    public static int OldMode = WAITING_TO_START;

    public static Random R = new Random();

    int Game;
    int PresentGame[] = { R.nextInt(2), R.nextInt(2) }; //new int[2];
    int LESS_THAN_GREATER_THAN_5 = 0;      
    int ODD_EVEN = 1;

    String GameStr[] = {
      "<_>_FIVE",
      "ODD_EVEN"
    };              
    int ImageChosen = -1;

    int BlockNo = 0;

    char Tab = ',';

    boolean FirstTime = true;

    Font TextFont = new Font("Helvetica", Font.BOLD, 25);
    Font SymbolFont = new Font("Helvetica", Font.BOLD, 40);
    
    //static boolean FirstWriteToFile = true;
    public static long CurrentTime = 0;
    public static long TrialTime = 0;

    int Angle[] = new int[2];

    boolean StartTimerNow;
    Task B = null;

    long TaskStartTime;

    int Yoffset;
    
    public static Color LastCorrectColor = Color.BLACK;
    public static String LastCorrectStr = "";
    public static String LastAccuracyStr = "";
    
    String SwitchedStr[] = new String[2];
    public static boolean FirstSwitched[] = new boolean[2];
    public static String OldGame[] = { "NONE", "NONE" };
    
    int XTarget = 0;
    int YTarget = 0;

    public SwitchingTask(Task B, int NoOfBlocks) {
        
        BlockNo = B.TaskCounter;
        
        B.TaskCounter++;
        
        Angle[0] = (int)(BlockNo * 360 / (double)NoOfBlocks);
        Angle[1] = Angle[0] + 180;
        while (Angle[1] >= 360) Angle[1] -= 360;
        
//        for (int i = 0; i < 1000; i++) Order[i] = i;
        
        W("BlockNo="+BlockNo);
        W("ANGLE0="+Angle[0]+" Angle1="+Angle[1]);
        
    }
    
    public void W(String S) {
        System.out.println(S);
    }
    
    public void StartTimer(int TimeMode) {
        StartTime[TimeMode] = CT.currentTimeMillis();
    } 
  
    public void StopTimer(int TimeMode) {
        StopTime[TimeMode] = CT.currentTimeMillis() - StartTime[TimeMode];
    } 
    
    public void GetQue(int ImageNo) {
        
        String NumberArray[] = { 
            "1", "2", "3", "4",
            "6", "7", "8", "9" 
        };
        
        String LessThanGreaterThanAnswerArray[] = { 
            "LEFT", "LEFT", "LEFT", "LEFT", 
            "RIGHT", "RIGHT", "RIGHT", "RIGHT" }; 
        
        String OddEvenAnswerArray[] = { 
            "LEFT", "RIGHT", "LEFT", "RIGHT", 
            "RIGHT", "LEFT", "RIGHT", "LEFT" }; 
        
//        SwitchedStr[ImageNo] = "NOT_SWITCHED";
        
        int RandomPercent = R.nextInt(101);
        
        if (RandomPercent <= B.PresentGamePercent[ImageNo]) {
        
            //change game type
            if (PresentGame[ImageNo] == 0) 
                PresentGame[ImageNo] = 1;
            else
                PresentGame[ImageNo] = 0;
               
        }
        
        Game = PresentGame[ImageNo];
        
        NumberIndex = R.nextInt(8);
        
        NumberStr = NumberArray[NumberIndex];
        
        if (Game == LESS_THAN_GREATER_THAN_5) {
            NumberColor = Color.YELLOW;
            CorrectAnswerStr = LessThanGreaterThanAnswerArray[NumberIndex];
        }
        
        if (Game == ODD_EVEN) {
            NumberColor = Color.GREEN;
            CorrectAnswerStr = OddEvenAnswerArray[NumberIndex];
        }
        
    }
    
    public void DrawPicture(int ImageNo) {

        double DTR = 2 * Math.PI / 360.0;
        double AngleInRadians = Angle[ImageNo] * DTR;
        
        XTarget = Xo + (int)(Task.BIG_CIRCLE_RADIUS * Math.sin(AngleInRadians));
        YTarget = Yo - (int)(Task.BIG_CIRCLE_RADIUS * Math.cos(AngleInRadians));
        
        ControlCode.Util.KeepMouseFromTarget(MouseX, MouseY, XTarget, YTarget);
        
        if (Parameters.DebugMode) {
            
            g2.setFont(TextFont);
            g2.setPaint(Color.MAGENTA);
            int x = XTarget + Task.IMAGE_RADIUS + 90;
            DrawString("IMAGE_" + ImageNo, x, YTarget - 45);
            DrawString(B.PresentGamePercent[ImageNo] + "%", x, YTarget - 15);
            DrawString("" + Angle[ImageNo] + " DEG", x, YTarget + 15);
            
        }
        
        ControlCode.Util.DrawImage(g2, XTarget, YTarget, ImageNo, ControlCode.Util.SWITCHING);
        
         if (ImageChosen == ImageNo) {
        
            if (Mode >= NUMBER_SHOWING) {
                
                g2.setPaint(Color.BLACK);
                g2.fillOval(
                        XTarget-Task.SYMBOL_RADIUS, YTarget-Task.SYMBOL_RADIUS, 
                        2 * Task.SYMBOL_RADIUS, 2 * Task.SYMBOL_RADIUS);
                g2.setPaint(NumberColor);
                g2.setFont(SymbolFont);
                DrawString2(NumberStr, XTarget, YTarget);
                
            }
        
        }
        
        if (Mode == MOUSE_MOVING) {
            
            double Radius = Math.sqrt(Math.pow(XTarget - MouseX,2) + Math.pow(YTarget - MouseY,2));

            //W("Radius="+Radius);            
            
            //Mouse within target circle?
            if (Radius < Task.IMAGE_RADIUS) {
                GetQue(ImageNo);
                Mode = NUMBER_SHOWING;
                StopTimer(MOUSE_MOVING);
                StartTimer(NUMBER_SHOWING);
                ImageChosen = ImageNo;
            }
            
        }        
        
    }
    
    public void DrawString(String S, int X, int Y) { 
        FontMetrics Fm = g2.getFontMetrics();
        int w = Fm.stringWidth(S);
        g2.drawString(S, X - w/2, Y + 30);
    }
      
    public void DrawString2(String S, int X, int Y) { 
        FontMetrics Fm = g2.getFontMetrics();
        int w = Fm.stringWidth(S);
        g2.drawString(S, X - w/2, Y + 14);
    }
      
    public boolean Update(Graphics2D g2in, int mXin, int mYin, boolean Button1In, boolean Button3In,
            Task Bin) {
        
      MouseX = mXin;
      MouseY = mYin;
      g2 = g2in;
      B = Bin;
      
//      W("BLOCKNO="+BlockNo);
//      W("ANGLE_0="+Angle[0]+" ANGLE_1="+Angle[1]);
//      W("TRIAL_NO="+TrialNo);
      
      Button1 = Button1In;
      Button3 = Button3In;
            
      Yoffset = Yo - 400;

      if (Parameters.DebugMode) {
          Xo = ControlCode.ScreenSizeX/4;
      }
      
      if (FirstTime) {
          
          g2.setPaint(LastCorrectColor);
          g2.setFont(TextFont);
          DrawString(LastCorrectStr, Xo, Yoffset + 700);
          
          if (B.ShowAccuracy) {
              g2.setPaint(Color.WHITE);
              DrawString(LastAccuracyStr, Xo, Yoffset + 735);
          }

          StartTimerNow = true;
          
          boolean Start = ControlCode.Util.ReadNextButton(Xo, Yo, g2, MouseX, MouseY, Button1, OldButton1);
          
          if (Start) FirstTime = false;
          
          return false;
          
      }
        
        
      if (StartTimerNow) {
          StartTimer(WAITING_TO_START);
          TaskStartTime = CT.currentTimeMillis() - Task.MasterStartTime;
      }
      
      StartTimerNow = false;
      
      g2.setFont(TextFont);
      
      g2.setPaint(Color.YELLOW);
      DrawString("LESS / GREATER THAN 5", Xo, Yoffset + 30);
      g2.setPaint(Color.GREEN);
      DrawString("ODD / EVEN", Xo, Yoffset + 70);
      
      if (Parameters.DebugMode) {
          
          for (int i = 0; i < ModeStr.length; i++) {
              g2.setPaint(Color.GRAY);
              if (i == Mode) g2.setPaint(Color.MAGENTA);
              g2.drawString(ModeStr[i], 800, Yoffset + 100 + i * 35);
              g2.setPaint(Color.MAGENTA);
              if (i < 3) if (StopTime[i] != -1)
                  g2.drawString(""+StopTime[i], 1100, Yoffset + 100 + i * 35);
          }
          g2.drawString("TRIAL_NO = " + (B.TrialNo + 1) + " OUT OF " + B.NoOfTrials, 
                  850, Yoffset + 600);
          g2.drawString("BLOCK_NO = " + (B.BlockCount + 1) + " OUT OF " + B.NoOfBlocks, 
                  850, Yoffset + 670);
              
      }
      
      g2.setFont(TextFont);
                  
      DrawPicture(0);
      DrawPicture(1);
     
      if (Mode == WAITING_TO_START) {
          //W("MouseX = "+MouseX + " MouseY="+MouseY+" Xo="+Xo+" Yo="+Yo);
          if ((Math.abs(MouseX - Xo) > 10) | (Math.abs(MouseY - Yo) > 10)) {
              StopTimer(WAITING_TO_START);
              StartTimer(MOUSE_MOVING);
              Mode = MOUSE_MOVING;
          }
      }
      
      if (Mode == NUMBER_SHOWING) {
          
          if (Button1 & !OldButton1) {
              Mode = SHOW_DECISION;
              SubjAnswerStr = "LEFT";
          }
          
          if (Button3 & !OldButton3) {
              Mode = SHOW_DECISION;
              SubjAnswerStr = "RIGHT";
          }
          
          if (Mode == SHOW_DECISION) {
              DisplayAnswerStartTime = CT.currentTimeMillis();
              StopTimer(NUMBER_SHOWING);
              WriteDataLine();
          }
          
      }
            
      if (Mode == SHOW_DECISION) {
          
          Color CorrectColor = Color.RED;
          String CorrectStr = "INCORRECT";

          if (OldMode != SHOW_DECISION) B.TotalAnswered++;
          
          if (CorrectAnswerStr.equals(SubjAnswerStr)) {
              CorrectColor = Color.GREEN;
              CorrectStr = "CORRECT";
              if (OldMode != SHOW_DECISION) B.TotalCorrect++;
          }
          
          g2.setPaint(CorrectColor);
          g2.setFont(TextFont);
          DrawString(CorrectStr, Xo, Yoffset + 700);
          
          LastCorrectColor = CorrectColor;
          LastCorrectStr = CorrectStr;
          
          if (B.TotalAnswered > 0) {
              
              B.PercentCorrect = ((int)(10 * 100.0 * (double)B.TotalCorrect/(double)B.TotalAnswered))/10.0;
          
              if (B.ShowAccuracy) {
                  g2.setPaint(Color.WHITE);
                  DrawString("ACCURACY  " + B.PercentCorrect + "%", Xo, Yoffset + 735);
                  LastAccuracyStr = "ACCURACY  " + B.PercentCorrect + "%";
              }
          
          }
          
          long Time = CT.currentTimeMillis() - DisplayAnswerStartTime;
      
          if (Time >= 1000) {
              ImageChosen = -1;
              Mode = WAITING_TO_START;
              FirstTime = true;
              B.TrialNo++;
              if (B.TrialNo == B.NoOfTrials) return true;
              return false;
          }
          
      }
                
      g2.setPaint(Color.WHITE);
      g2.setFont(TextFont);
      
      if (Parameters.DebugMode)
          g2.drawString("SWITCHING TASK", 890, Yoffset + 50);
          
      OldButton1 = Button1;
      OldButton3 = Button3;

      OldMode = Mode;
      
      return false;       

    }
    
    public boolean OutputData(String Data) {
 
    	try{

            String PreambleStr =
                   ControlCode.SubjNo + Tab + ControlCode.ExpCond + Tab + Parameters.ParameterFile + Tab;

            String FileName = "EXPERIMENT/DATA/" + B.FileName + " SWITCHING_TASK " + ControlCode.SubjNo + ".csv";

            File file = new File(FileName);

            if (Data.equals("CHECK_EXISTANCE")) {
                if (file.exists()) return true; //yes it exists
                else return false; //no, it does not
            }

            if (B.FirstWriteToFile) {
                W("DELTING DATA");
                file.delete();
                file.createNewFile();
            }

            //true = append file
            FileWriter fileWritter = new FileWriter(FileName,true);
            BufferedWriter BW = new BufferedWriter(fileWritter);

            if (B.FirstWriteToFile) {

                String S = 
                      "DATA_FILE"+Tab+"DATA_TAG"+Tab+"PARAMETER_FILE"
                 +Tab+"START_TIME"+Tab+"STOP_TIME"+Tab+"TRIAL_DURATION"
                 +Tab+"TASK_NO"+Tab+"REPETITION_NO"+Tab+"BLOCK_NO"+Tab+"TRIAL_NO"
                 +Tab + "ANGLE_0" + Tab + "ANGLE_1"
                 +Tab + "IMAGE_0_PROB_SWITCHING" + Tab + "IMAGE_1_PROB_SWITCHING"       
                 +Tab+"WAIT_FOR_MOUSE_TIME"+Tab+"ENTER_CIRCLE_TIME"+Tab+"DECISION_TIME"
                 +Tab+"IMAGE_CHOSEN"
                 +Tab+"PROBABILITY"
                 +Tab+"GAME_TYPE"
                 +Tab+"SWITCHED"
                 +Tab+"NUMBER"
                 +Tab+"CORRECT_ANSWER"+Tab+"SUBJ_ANSWER"+Tab+"ACCURACY"
                 +Tab+"VERSION";

                S += "\r\n";

                BW.write(S);

            }

            String S = PreambleStr + Data + "\r\n";

            BW.write(S);

            BW.close();

            B.FirstWriteToFile = false;

    	}catch(IOException e) {
            W("EXCEPTION 1");
    		e.printStackTrace();
                 System.exit(7);
    	}
        
        //does not matter what you return, if data
        return false;

    }
    
    public void WriteDataLine() {
                        
        //String S = ("" + BlockNo);
         
        String CorrectStr = "INCORRECT";
        
        if (CorrectAnswerStr.equals(SubjAnswerStr)) CorrectStr = "CORRECT";
        
        long StopTrialTime = CT.currentTimeMillis() - Task.MasterStartTime;
        
        long DurationTime = StopTrialTime - TaskStartTime;
        
        SwitchedStr[ImageChosen] = "NOT_SWITCHED";
        
        if (!OldGame[ImageChosen].equals(GameStr[Game])) SwitchedStr[ImageChosen] = "SWITCHED";
        
        if (FirstSwitched[ImageChosen]) SwitchedStr[ImageChosen] = "NONE";
        FirstSwitched[ImageChosen] = false;
        
        OldGame[ImageChosen] = GameStr[Game];
        
        String S = "" + TaskStartTime + Tab + StopTrialTime + Tab + DurationTime 
                 + Tab + (Parameters.TaskCount+1) + Tab + (B.RepetitionNo) + Tab + (B.BlockCount+1) + Tab + "" + (B.TrialNo+1) 
                 + Tab + Angle[0] + Tab + Angle[1]
                 + Tab + B.PresentGamePercent[0] + Tab + B.PresentGamePercent[1]
                 + Tab + "" + StopTime[0] + Tab  + "" + StopTime[1] + Tab + "" + StopTime[2]
                 + Tab + ImageChosenString[ImageChosen] + Tab + B.PresentGamePercent[ImageChosen]
                 + Tab + GameStr[Game] 
                 + Tab + SwitchedStr[ImageChosen]
                 + Tab + NumberStr + Tab + CorrectAnswerStr + Tab + SubjAnswerStr + Tab + CorrectStr
                 +Tab + System.getProperty("user.dir");
                
        OutputData(S);
                            
    }
    
}
