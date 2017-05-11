package TetrisCode;

import java.awt.*;
import java.io.*;
import java.util.*;

public class Parameters {

  Font BigFont = new Font("Helvetica", Font.PLAIN, 24);
  long Counter = 0;
   
  int MouseX, MouseY;
  Graphics2D g2;
  
  boolean InsideTextTrial = false;
  
  public static String SubjNo = "EMPTY";
  public static String ExpCond = "EMPTY";
  public static String ParameterFile = "EMPTY";
  
  public static long StartTime = 0;
  
  static int TaskCount = 0;
  
  static boolean ExperimentDone;
  boolean SeeExperimenter;
  
  int TextFontHeight = 25;
  Font ErrorFont = new Font("Helvetica", Font.PLAIN, 25);
  long FrameCounter = 0;
    
  Frame Frame;
  String Cmd = "";
  String Arg = "";
  
  ArrayList TaskList = new ArrayList();
  boolean FirstTime = true;
  
  public static String ErrorCode = "";
  int LineNo = 0;
  
  static boolean DebugMode;
  static int PointsSubtractedPerNewBlock;
  static int PointsAddedPerRemovedBlockFromFullRow;
  static int RemoveBiggestPartialRowIfBlockInRow;
  static int PointsSubtractedPerEmptySpaceFromPartialRow;
  static int MinimumDelayMilliseconds;
  static int TimeLimitInSeconds;
  public static int MaxLevels;
  public static int MaxSecondsInLevel = 0;
  
  
  public Parameters(String ParameterFileName) {
        
        ParameterFile = ParameterFileName;
        
        try {
            
            FileInputStream fstream = new FileInputStream(
                    "PARAMETERS/" + ParameterFileName+".txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String S;
            
            //Read File Line By Line
            while ((S = br.readLine()) != null)   {
                
                System.out.println(S);
            
                LineNo++;
                
                Arg = S;

                Cmd = GetString(1);

                if (Cmd.charAt(0) == '/') {
                    
                } else
                    
                if (Cmd.equals("STOP")) {
                    break;
                } else
                               
                if (Cmd.equals("DEBUG_MODE")) {
                    if (GetString(2).equals("TRUE")) DebugMode = true;
                } else

                if (Cmd.equals("ADAPTIVE_TETRIS")) {
                    if (GetString(2).equals("FALSE")) Tetris.isAdaptive = false;
                    else if (GetString(2).equals("TRUE")) Tetris.isAdaptive = true;
                } else
                    
//------------------------------------------------------------------------------                
               
                if (Cmd.equals("NUMBER_OF_COLUMNS")) {
                    Tetris.COLUMNS = (byte)GetInteger(2);
                } else
               
                if (Cmd.equals("NUMBER_OF_ROWS")) {
                    Tetris.ROWS = (byte)GetInteger(2);
                } else
               
                if (Cmd.equals("POINTS_SUBTRACTED_PER_NEW_BLOCK")) {
                    PointsSubtractedPerNewBlock = GetInteger(2);
                } else
               
                if (Cmd.equals("POINTS_ADDED_PER_REMOVED_BLOCK_FROM_FULL_ROW")) {
                    PointsAddedPerRemovedBlockFromFullRow = GetInteger(2);
                } else

                if (Cmd.equals("REMOVE_BIGGEST_PARTIAL_ROW_IF_BLOCK_IS_IN_ROW")) {
                    RemoveBiggestPartialRowIfBlockInRow = GetInteger(2) - 1;
                } else

                if (Cmd.equals("POINTS_SUBTRACTED_PER_REMOVED_BLOCK_FROM_PARTIAL_ROW")) {
                    PointsSubtractedPerEmptySpaceFromPartialRow = GetInteger(2);
                } else
                
                if (Cmd.equals("MINIMUM_DELAY_MILLISECONDS")) {
                    MinimumDelayMilliseconds = GetInteger(2);
                } else
                
                if (Cmd.equals("TIME_LIMIT_IN_SECONDS")) {
                    TimeLimitInSeconds = GetInteger(2);
                    //System.out.println("TLIS="+TimeLimitInSeconds);
                } else
                    
////////////////////////////////////////////////////////////////////////////////

                if (Cmd.equals("MAXIMUM_LEVEL")) {
                    MaxLevels = GetInteger(2);
                } else
        
                if (Cmd.equals("SECONDS_IN_MAXIMUM_LEVEL_TO_SWITCH_TO_FOCUS_TASK")) {
                    MaxSecondsInLevel = GetInteger(2);
                } else
                
                //make sure to input the value in ms
                if (Cmd.equals("DROP_PERCENTAGE_TIME_WINDOW")) {
                    Tetris.DropPercentageTimeWindow = GetInteger(2);
                } else
                    
                if (Cmd.equals("QUEUE_HISTORY_LENGTH")) {
                    Tetris.queue_history = GetInteger(2);
                } else
                    
                // for example:  (put in actual documentation later)
                    
                // SIZE_LAST_ROW_REMOVED,8,<,
                    
                // for "If the size of the last row removed is less than 8, switch tasks
                    
                if (Cmd.equals("SIZE_LAST_ROW_REMOVED")) {
                    Tetris.SwitchCondition_Measure = Cmd;
                    Tetris.SwitchCondition_Value = (double) GetInteger(2);
                    Tetris.SwitchCondition_Comparison = GetString(3).charAt(0);
                } else
                    
                if (Cmd.equals("SPEED_LEVEL")) {
                    Tetris.SwitchCondition_Measure = Cmd;
                    Tetris.SwitchCondition_Value = (double) GetInteger(2);
                    Tetris.SwitchCondition_Comparison = GetString(3).charAt(0);
                } else
                           
                if (Cmd.equals("HEIGHT_VARIANCE")) {
                    Tetris.SwitchCondition_Measure = Cmd;
                    Tetris.SwitchCondition_Value = (double) GetInteger(2);
                    Tetris.SwitchCondition_Comparison = GetString(3).charAt(0);
                } else
                    
                if (Cmd.equals("DROP_DURATION_VARIANCE")) {
                    Tetris.SwitchCondition_Measure = Cmd;
                    Tetris.SwitchCondition_Value = (double) GetInteger(2);
                    Tetris.SwitchCondition_Comparison = GetString(3).charAt(0);
                } else
                    
                if (Cmd.equals("DROP_PERCENTAGE")) {
                    Tetris.SwitchCondition_Measure = Cmd;
                    Tetris.SwitchCondition_Value = (double) GetInteger(2);
                    Tetris.SwitchCondition_Comparison = GetString(3).charAt(0);
                } else   
                    
////////////////////////////////////////////////////////////////////////////////                    
                
                {
                    ErrorCode = "DO NOT RECOGNIZE PARAMETER FILE LINE " + LineNo
                              + " LINE = '" + Arg + "'";
                    return;
                }
                   
            }

            in.close();

            W("CLOSING FILE");
            
        } catch (Exception e) {
            ErrorCode = "LINE " + LineNo + " PARAMETER FILE ERROR: " + e.getMessage();
            System.err.println("LINE " + LineNo + "Parameter File Error: " + e.getMessage());
        }
            
    }
    
    public void W(String S) {
        System.out.println(S);
    }

    public boolean ValidFileName(String FileName) {
        
        if (FileName.length() == 0) return false;
        
        char FirstLetter = FileName.charAt(0);
        
        if ((FirstLetter >= 'A') & (FirstLetter <= 'Z')) return true;
        
        return false;
        
    }
    
    public String GetRestOfString(int I) {
        
        int A = I-1;
        int c;
        int CommaCount = 0;
        
        String ArgString = "";
       
        //find start
        for (c = 0; c < Arg.length(); c++) {
            if (Arg.charAt(c) == ',') {
                CommaCount++;
                c++;
            }
            if (CommaCount == A) break;
        }
        
        //find end
        for (; c < Arg.length(); c++) {
            //if (Arg.charAt(c) == ',') break;
            ArgString += Arg.charAt(c);
        }
        
        return ArgString;
        
    }

    public String GetString(int I) {
        
        int A = I-1;
        int c;
        int CommaCount = 0;
        
        String ArgString = "";
       
        //find start
        for (c = 0; c < Arg.length(); c++) {
            if (Arg.charAt(c) == ',') {
                CommaCount++;
                c++;
            }
            if (CommaCount == A) break;
        }
        
        //find end
        for (; c < Arg.length(); c++) {
            if (Arg.charAt(c) == ',') break;
            ArgString += Arg.charAt(c);
        }
        
        return ArgString;
        
    }

    public boolean NotANumber(String Integer) {
        
        for (int i = 0; i < Integer.length(); i++) {
            char c = Integer.charAt(i);
            if ((c < '0') | (c > '9')) return true;
        }
        
        return false;
        
    } 
    
    public int GetInteger(int I) {
        
        if (NotANumber(GetString(I))) {
            ErrorCode = "LINE " + LineNo + "   '" + GetString(I)+"'   IS NOT AN INTEGER";
            return 0;
        }
        
        return Integer.parseInt(GetString(I));
    
    }
    
    public int GetRgbInteger(int I) {
        
        if (NotANumber(GetString(I))) {
            ErrorCode = "LINE " + LineNo + "   '" + GetString(I)+"'   IS NOT AN INTEGER";
            return 0;
        }
        
        int Answer = Integer.parseInt(GetString(I));
        
        if (Answer > 255) {
            ErrorCode = "LINE " + LineNo + "   '" + GetString(I)+"'   IS GREATER THAN 255";
            return 0;            
        }
        
        return Answer;
    
    }
    
    public void DrawString(String S, int X, int Y, int TextFontHeight) {
        FontMetrics Fm = g2.getFontMetrics();
        int w = Fm.stringWidth(S);
        g2.drawString(S, X - w/2, Y - TextFontHeight/2 + TextFontHeight);
    }
      

    public boolean Update(Graphics2D g2in, int mXin, int mYin, boolean Button1, boolean Button2, boolean Button3) {
        
      MouseX = mXin;
      MouseY = mYin;
      g2 = g2in;
      
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                          RenderingHints.VALUE_ANTIALIAS_ON);

      if (!ErrorCode.equals("")) {
          g2.setPaint(Color.RED);
          g2.setFont(BigFont);
//          DrawString(ErrorCode, 
//                  ControlCode.ScreenSizeX/2, ControlCode.ScreenSizeY/2, TextFontHeight);
          return false;
      } 
            
//W("BLOCK_COUNT="+TaskCount + " Size"+BlockList.size());

      if (!ExperimentDone) {
  
//          B = (Task)TaskList.get(TaskCount);
//
//          if (B.Update(g2, MouseX, MouseY, Button1, Button2, Button3)) {
//              
//              B.RepetitionNo++;
//              
//              B.TrialNo = 0;
//              B.BlockCount = 0;
//              
//              B.TotalAnswered = B.TotalCorrect = 0;
//              
//              if (B.TrialsToCriterion) {
//                  
//                  if (B.PercentCorrect >= B.CriterionPercent) {
//                  
//                      TaskCount++;
//                  
//                  } else {
//                    
//                      if (B.TrialsLimited) {
//                       
//                          if (B.RepetitionNo > B.CriterionToTrials) {
//                              ExperimentDone = true;
//                              SeeExperimenter = true;
//                          } else  
//                              TaskCount -= B.TasksToSkipBackwards;
//                          
//                      } else
//                      
//                          TaskCount -= B.TasksToSkipBackwards;
//                  
//                  }
//                  
//              } else TaskCount++;
//          
//          }
//          
//          g2.setPaint(Color.WHITE);
//          g2.setFont(ErrorFont);
//          if (DebugMode) g2.drawString("TASK "+(TaskCount + 1), 60, 40);
          
      } else {
          
          g2.setFont(ErrorFont);
          g2.setPaint(Color.CYAN);
          
//          if (SeeExperimenter)
//              DrawString("SEE EXPERIMENTER", ControlCode.ScreenSizeX/2, ControlCode.ScreenSizeY/2, 0);
//          else
//              DrawString("EXPERIMENT DONE", ControlCode.ScreenSizeX/2, ControlCode.ScreenSizeY/2, 0);
          
          return false;
      
      }
      
      if (TaskCount == TaskList.size()) {
          ExperimentDone = true;
      }
      
      return false;
      
    }    

}
