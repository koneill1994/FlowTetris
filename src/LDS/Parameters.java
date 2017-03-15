package LDS;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import TetrisCode.Tetris;

public class Parameters {

  public static int LDS_MODE = 0;
  public static int TETRIS_MODE = 1;  
  public static int Mode = Parameters.LDS_MODE;
    
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
  Task B = null;
  
  static boolean ExperimentDone;
  boolean SeeExperimenter;
  
  int TextFontHeight = 25;
  Font ErrorFont = new Font("Helvetica", Font.PLAIN, 25);
  long FrameCounter = 0;
    
  JFrame Frame;
  String Cmd = "";
  String Arg = "";
  
  ArrayList TaskList = new ArrayList();
  boolean FirstTime = true;
  
  public static String ErrorCode = "";
  int LineNo = 0;
  
  static boolean DebugMode;
  boolean InsideTask;
  
  public boolean OnlyTetris = false; // for debug purposes

  Random R = new Random();
  
  public static ArrayList SurveyArrayList = new ArrayList();
  
  public Parameters(JFrame FrameIn, String ParameterFileName) {
        
        ParameterFile = ParameterFileName;
        
        Frame = FrameIn;

        try {
            
            FileInputStream fstream = new FileInputStream(
                    "EXPERIMENT/PARAMETERS/" + ParameterFileName+".txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String S;
            
            //Read File Line By Line
            while ((S = br.readLine()) != null)   {
                
                System.out.println(S);
            
                LineNo++;
                
                Arg = S;

                Cmd = GetString(1);

                if (Cmd.equals("STOP")) {
                    break;
                } else
                               
                if (Cmd.equals("DEBUG_MODE")) {
                    if (GetString(2).equals("TRUE")) DebugMode = true;
                } else

//------------------------------------------------------------------------------                
               
                if (Cmd.equals("BEGIN_TEXT_TASK")) {
                    if (InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING END_TASK STATEMENT";
                        return;
                    }
                    B = new Task(Task.TEXT_TASK);
                    InsideTask = true;
                } else
               
                if (Cmd.equals("TEXT_BACKGROUND_COLOR_RGB")) {
                    Color C = new Color(GetRgbInteger(2), GetRgbInteger(3), GetRgbInteger(4));
                    Task.TextBackgroundColor = C;
                } else

                if (Cmd.equals("TEXT_COLOR_RGB")) {
                    Color C = new Color(GetRgbInteger(2), GetRgbInteger(3), GetRgbInteger(4));
                    Task.TextColor = C;
                } else

                if (Cmd.equals("TEXT_FONT_SIZE")) {
                    Task.TextFontSize = GetInteger(2);
                } else
                
                if (Cmd.equals("TEXT_Y_START")) {
                    Task.TextYStart = GetInteger(2);
                } else
                
                if (Cmd.equals("TEXT_X_START")) {
                    Task.TextXStart = GetInteger(2);
                } else
                
                if (Cmd.equals("TEXT_Y_SPACING")) {
                    Task.TextYSpacing = GetInteger(2);
                } else
                                
                if (Cmd.equals("TEXT_ON_LINE")) {
                     if (InsideTask) {
                         int LineNo = GetInteger(2);
                         //W("LINENO="+LineNo);
                         if (LineNo > 99) {
                             ErrorCode = "LINE " + LineNo + " LINE NUMBER MUST BE LESS THAN 100";
                             return;
                         }
//                        W("GET2="+GetInteger(2));
                         B.TextLines[GetInteger(2)] = GetRestOfString(3);
                        
                     }
                } else
                    
//------------------------------------------------------------------------------

                if (Cmd.equals("BEGIN_SWITCHING_TASK")) {
                    if (InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING END_TASK STATEMENT";
                        return;
                    }
                    B = new Task(Task.SWITCHING_TASK);
                    B.FileName = GetString(2);
                    if (!ValidFileName(B.FileName)) {
                        ErrorCode = "LINE " + LineNo + " NOT A VALID FILE NAME";
                        return;
                    }
                    InsideTask = true;    
                } else

                if (Cmd.equals("BEGIN_FOCUS_TASK")) {
                    if (InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING END_TASK STATEMENT";
                        return;
                    }
                    B = new Task(Task.FOCUS_TASK);
                    B.FileName = GetString(2);
                    if (!ValidFileName(B.FileName)) {
                        ErrorCode = "LINE " + LineNo + " NOT A VALID FILE NAME";
                        return;
                    }
                    InsideTask = true;    
                } else

                if (Cmd.equals("END_TASK")) {
                    if (!InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
                        return;
                    }
                    TaskList.add(B);
                    InsideTask = false;
                } else
                    
                if (Cmd.equals("SET_NO_OF_BLOCKS")) {
                    if (!InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
                        return;
                    }
                    B.NoOfBlocks = GetInteger(2);
                } else

                if (Cmd.equals("SET_NO_OF_TRIALS")) {
                    if (!InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
                        return;
                    }
                    B.MakeTrials(GetInteger(2));
                } else

                if (Cmd.equals("SET_PROBABILITY_OF_SWITCHING") | Cmd.equals("SET_PROBABILITY_OF_HIGH_FOCUS")) {
                    if (!InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
                        return;
                    }
                    B.PresentGamePercent[0] = GetInteger(2);
                    B.PresentGamePercent[1] = GetInteger(3);
                } else

                if (Cmd.equals("SET_RANDOM_PROBABILITY_OF_SWITCHING") | Cmd.equals("SET_RANDOM_PROBABILITY_OF_HIGH_FOCUS")) {
                    if (!InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
                        return;
                    }
                    Task.UseRandomGamePercent = true;
                    Task.PresentRandomGamePercent[0] = GetInteger(2);
                    Task.PresentRandomGamePercent[1] = GetInteger(3);
                    //50% chance that it will be reversed
                    if (R.nextInt(2) == 0) {
                        int Temp = Task.PresentRandomGamePercent[0];
                        Task.PresentRandomGamePercent[0] = Task.PresentRandomGamePercent[1];
                        Task.PresentRandomGamePercent[1] = Temp;
                    }
                } else
//------------------------------------------------------------------------------               
                if (Cmd.equals("DEBUG_TETRIS_ONLY")){
                    OnlyTetris=true;
                } else                    
                    
//------------------------------------------------------------------------------
                    
                if (Cmd.equals("NO_OF_SYMBOLS")) {
//                    if (InsideTask) {
//                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
//                        return;
//                    }
                    B.NoOfSymbols = GetInteger(2);
                } else

                if (Cmd.equals("FIRST_LETTERS")) {
//                    if (!InsideTask) {
//                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
//                        return;
//                    }
                    Task.FirstLettersUsed = GetString(2);
                } else

                if (Cmd.equals("SECOND_LETTERS")) {
//                    if (!InsideTask) {
//                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
//                        return;
//                    }
                    Task.SecondLettersUsed = GetString(2);
                } else

                if (Cmd.equals("LETTER_INDEX")) {
                    Task.LetterIndex[0] = GetInteger(2);
                    Task.LetterIndex[1] = GetInteger(3);
                } else

//                if (Cmd.equals("SECOND_LETTER_INDEX")) {
//                    Task.SecondLetterIndex[0] = GetInteger(2);
//                    Task.SecondLetterIndex[1] = GetInteger(3);
//                } else

                if (Cmd.equals("SYMBOL_DISPLAY_TIME")) {
                    Task.SymbolDisplayTime = GetInteger(2);
                    Task.OriginalSymbolDisplayTime = Task.SymbolDisplayTime;
                } else

//------------------------------------------------------------------------------
                    
                if (Cmd.equals("SHOW_ACCURACY")) {
                    if (!InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
                        return;
                    }
                    if (GetString(2).equals("TRUE")) B.ShowAccuracy = true;
                } else

                if (Cmd.equals("CRITERION_TO_PERCENT")) {
                    if (!InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
                        return;
                    }
                    B.TrialsToCriterion = true;
                    B.CriterionPercent = GetInteger(2);
                    B.TasksToSkipBackwards = GetInteger(3);
                    if (B.TaskCounter <= B.TasksToSkipBackwards)
                        ErrorCode = "LINE " + LineNo + " SKIPPING BACK BEFORE BEGINING";
                } else

                if (Cmd.equals("CRITERION_TO_TRIALS")) {
                    if (!InsideTask) {
                        ErrorCode = "LINE " + LineNo + " MISSING TASK STATEMENT";
                        return;
                    }
                    B.TrialsLimited = true;
                    B.CriterionToTrials = GetInteger(2);
                } else

////////////////////////////////////////////////////////////////////////////////
                
                if (Cmd.equals("HOW_MUCH_TO_DECREASE_THE_DELAY")) {
                    FocusTask.IsTetrisTask = true;
                    FocusTask.HowMuchToDecreaseTheDelay = GetInteger(2);
                } else

                if (Cmd.equals("THRESHHOLD_IN_ACCURACY_TO_DECREASE_THE_DELAY")) {
                    FocusTask.ThreshHoldInAccuracyToDecreaseTheDelay = GetInteger(2);
                } else
                    
                if (Cmd.equals("THRESHHOLD_FOR_SWITCHING_TO_TETRIS")) {
                    FocusTask.ThreshHoldForSwitchingToTetris = GetInteger(2);
                } else

////////////////////////////////////////////////////////////////////////////////
                                    
                if (Cmd.equals("TAKE_SURVEY")) {
                    SurveyArrayList.add(new Survey(GetString(2), GetString(3), GetInteger(4)));
                } else
                
////////////////////////////////////////////////////////////////////////////////
                    
                if (Cmd.charAt(0) == '/') {
                        
                    continue;
                
                }
                
                else
                    if (Cmd.length() == 0) {
                        ErrorCode = "LINE " + LineNo + " BLANK LINES NOT ALLOWED";
                        return;
                    }
                
                else {
                    ErrorCode = "DO NOT RECOGNIZE PARAMETER FILE LINE " + LineNo
                              + " LINE = '" + Arg + "'";
                    return;
                }
                   
            }

            in.close();

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
          DrawString(ErrorCode, 
                  ControlCode.ScreenSizeX/2, ControlCode.ScreenSizeY/2, TextFontHeight);
          return false;
      } 
          
      W("SurveyArrayList.size()="+SurveyArrayList.size());

      if (SurveyArrayList.size() > 0) {

          Survey SurveyCode = (Survey)SurveyArrayList.get(0);

          boolean RunSurvey = false;
          
          if ((SurveyCode.BeforeMode == SurveyCode.BEFORE_LDS) & (SurveyCode.SessionNo == FocusTask.SessionNo)) 
              RunSurvey = true;
          
          if ((SurveyCode.BeforeMode == SurveyCode.BEFORE_TETRIS) & (SurveyCode.SessionNo == TetrisCode.Tetris.SessionNo))
              RunSurvey = true;
          
          if (RunSurvey) {
          
              ControlCode.Frame.setSize(1680, 1024);
 
              if (SurveyCode.Update(g2, MouseX, MouseY, Button1)) {

                  W("SURVEY DONE");

                  SurveyArrayList.remove(0);

                  W("2 SurveyArrayList.size()="+SurveyArrayList.size());

                  if (Mode == TETRIS_MODE) SwitchToTetris();
    //              return;

              }
          
          } else if (Mode == TETRIS_MODE) SwitchToTetris();

      } else if (Mode == TETRIS_MODE) SwitchToTetris();


      if(OnlyTetris) {
        SwitchToTetris();
      }
      
      // ^^ set this to true when you want to test just tetris
      
//W("BLOCK_COUNT="+TaskCount + " Size"+BlockList.size());

      if (!ExperimentDone) {
  
          B = (Task)TaskList.get(TaskCount);

          if (B.Update(g2, MouseX, MouseY, Button1, Button2, Button3)) {
              
              B.RepetitionNo++;
              
              B.TrialNo = 0;
              B.BlockCount = 0;
              
              B.TotalAnswered = B.TotalCorrect = 0;
              
              if (FocusTask.IsTetrisTask) {
              
                  if (B.TrialsToCriterion) {

                      if (B.PercentCorrect >= B.CriterionPercent) {

                          TaskCount++;

                      } else {

                          if (B.TrialsLimited) {

                              if (B.RepetitionNo > B.CriterionToTrials) {
                                  ExperimentDone = true;
                                  SeeExperimenter = true;
                              } else  
                                  TaskCount -= B.TasksToSkipBackwards;

                          } else

                              TaskCount -= B.TasksToSkipBackwards;

                      }

                  } else TaskCount++;
                  
              } else { //IsTetrisTask
                  
                  if (B.IsTextBlock) {
                      TaskCount++;
                      return false;
                  }
                                        
                  if (B.PercentCorrect >= FocusTask.ThreshHoldInAccuracyToDecreaseTheDelay) {

                      if (Task.SymbolDisplayTime > 0) {
                          
                          Task.SymbolDisplayTime -= FocusTask.HowMuchToDecreaseTheDelay;
                          if (Task.SymbolDisplayTime < 0) Task.SymbolDisplayTime = 0;
 
                          TaskCount -= 1;
                          
                      }

                  }
                  
                  if (B.PercentCorrect <= FocusTask.ThreshHoldForSwitchingToTetris) {
                      
                      B.RepetitionNo = 1;
                      B.RandomizeTrials = true;
                      //SwitchToTetris();
                      Mode = TETRIS_MODE;
                      TaskCount -= 1;
                      
                  }
                  
              }
          
          }
          
          g2.setPaint(Color.WHITE);
          g2.setFont(ErrorFont);
          if (DebugMode) g2.drawString("TASK "+(TaskCount + 1), 60, 40);
          
      } else {
          
          g2.setFont(ErrorFont);
          g2.setPaint(Color.CYAN);
          
          if (SeeExperimenter)
              DrawString("SEE EXPERIMENTER", ControlCode.ScreenSizeX/2, ControlCode.ScreenSizeY/2, 0);
          else
              DrawString("EXPERIMENT DONE", ControlCode.ScreenSizeX/2, ControlCode.ScreenSizeY/2, 0);
          
          return false;
      
      }
      
      if (TaskCount == TaskList.size()) {
          ExperimentDone = true;
      }
      
      return false;
      
    }  
    
    public void SwitchToTetris() {
        
        FocusTask.SessionNo++;
        
        //reset Symbol DisplayTime for Focus Task
        Task.SymbolDisplayTime = Task.OriginalSymbolDisplayTime;
        
        //Tetris.StartTime = System.currentTimeMillis();
        // I have a feeling this is subverting the original intent of these variables
        // but it solves a current issue
        Tetris.StartTime = System.nanoTime();
        Tetris.StartTimeInLevel = System.nanoTime();
        Tetris.timer.setPaused(false);
        Tetris.frame.setVisible(true);
        ControlCode.Frame.setVisible(false);
        ControlCode.t.suspend();
        
    }

}
