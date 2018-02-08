package LDS;

import java.awt.*;
import java.io.*;
import java.util.*;
import TetrisCode.Tetris;
import java.awt.event.KeyEvent;

public class FocusTask {
    
    CurrentTime CT = new CurrentTime();

    int Xo = ControlCode.ScreenSizeX/2;
    int Yo = ControlCode.ScreenSizeY/2;

    Graphics2D g2;
    boolean Button1;
    boolean Button2;
    boolean Button3;
    boolean OldButton1;
    boolean OldButton2;
    boolean OldButton3; 
    int MouseX, MouseY;

    public static boolean IsTetrisTask;
    public static int SessionNo = 1;
    
    TextRotater TxtRot = new TextRotater();
    
    char CorrectAnswerChar1 = (char)0;
    char CorrectAnswerChar2 = (char)0;
    char SubjAnswerChar1 = (char)0;
    char SubjAnswerChar2 = (char)0;

    long DisplayAnswerStartTime;

    int RIGHT_SIDE_UP = 0;      
    int UPSIDE_DOWN = 1;

    //modes
    static int WAITING_TO_START_MODE = 0;
    static int MOUSE_MOVING_MODE = 1;
    static int DISPLAY_COLOR_MODE = 2;
    static int DISPLAY_NUMBERS_MODE = 3;
    static int WAITING_FOR_DECISION_MODE_1 = 4;
    static int WAITING_FOR_DECISION_MODE_2 = 5;
    static int SHOW_DECISION_MODE = 6;
    
    //timers needed
    static int WAITING_TO_START_TIME = 0;
    static int MOUSE_MOVING_TIME = 1;
    static int DISPLAY_COLOR_TIME = 2;
    static int TIME_TO_TARGET_TIME = 3;
    static int GET_DECISION_TIME_1 = 4;
    static int GET_DECISION_TIME_2 = 5;

    String ImageChosenString[] = {
        "IMAGE_0",
        "IMAGE_1"
    };
    
    String ModeStr[] = {
        "WAITING TO START",
        "MOUSE MOVING",
        "DISPLAY COLOR",
        "TIME TO TARGET",
        "WAITING FOR DECISION 1",
        "WAITING FOR DECISION 2",
        "SHOW DECISION"
    };

    boolean Answer1Given;
    boolean Answer2Given;
    
    boolean AcceptInput;
    
    long StartTime[] = new long[6];
    long StopTime[] = new long[6];

    public static int Mode = WAITING_TO_START_MODE;
    public static int OldMode = WAITING_TO_START_MODE;

    public static Random R = new Random();

    int Game;
    int LOW_FOCUS = 0;  //Right Side Up     
    int HIGH_FOCUS = 1; //Upside Down

    String GameStr[] = {
        "LOW_FOCUS",
        "HIGH_FOCUS"
    };  

    int ImageChosen = -1;

    int BlockNo = 0;
    
    char Tab = ',';

    boolean FirstTime = true;
    
    Font SymbolFont = new Font("Helvetica", Font.BOLD, 60);
    Font TextFont = new Font("Helvetica", Font.BOLD, 25);
    
    int Angle[] = new int[2];

    boolean StartTimerNow;
    Task B = null;
    
    int FirstTargetIndex;
    int SecondTargetIndex;
    boolean SecondLetterIsPresentInTarget;
    String TargetString = "";
    //String TargetLetter = "*";
    Color RingColor = null;
    double NumberDisplayStartTime;
    long DisplayColorStartTime;
    int SymbolIndex;
    char FirstTargetLetter = (char)0;
    char SecondTargetLetter = (char)0;
    static char FirstTargetLetterMemory = (char)0;
    static char SecondTargetLetterMemory = (char)0;
    
    long TaskStartTime;
    long AnswerStartTime;
    long NegativeDecisionTime;
    boolean DecisionTimeIsNegative;

    int Yoffset;
    boolean TargetNowKnown;
   
    public static Color LastCorrectColor = Color.BLACK;
    public static String LastCorrectStr = "";
    public static String LastAccuracyStr = "";
    public static String FirstSecondLetterStr = "";
    public static int LastPosition = -1;
    public static char LastSecondLetter = ' ';
    
    int XTarget = 0;
    int YTarget = 0;
                        
    public static int HowMuchToDecreaseTheDelay = -1;
    public static int ThreshHoldInAccuracyToDecreaseTheDelay;
    public static int ThreshHoldForSwitchingToTetris;

    public FocusTask(Task B, int NoOfBlocks) {
        
        //reset timers
        for (int i = 0; i < 6; i++) StopTime[i] = -1;
    
        BlockNo = B.TaskCounter;
        
        B.TaskCounter++;
        
        Angle[0] = (int)(BlockNo * 360 / (double)NoOfBlocks);
        Angle[1] = Angle[0] + 180;
        while (Angle[1] >= 360) Angle[1] -= 360;
        
        W("MADE D");
        W("BlockNo="+BlockNo);
        W("ANGLE0="+Angle[0]+" Angle1="+Angle[1]);
        W("MADE E");
        
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
    
    public static String ReplaceCharAt(String str, int pos, char c) {
        //char c = character.charAt(0);
        StringBuffer buf = new StringBuffer(str);
        buf.setCharAt(pos-1, c);
        return buf.toString();
    }
    
    public void GetQue(int ImageNo) {
        
        Game = LOW_FOCUS;
        
        int RandomPercent = R.nextInt(101);
        
        if (RandomPercent <=  B.PresentGamePercent[ImageNo]) {
            Game = HIGH_FOCUS;
        }
        
//        if (Game == LOW_FOCUS) {  //rfg
//            RingColor = Color.YELLOW;
//        }
//        
//        if (Game == HIGH_FOCUS) {
//            RingColor = Color.GREEN;
//        }
          RingColor = Color.WHITE;
        
        //build TargetString of numbers
        
        TargetString = "";
        
        int Digit = 0;
        int NewDigit;
            
        for (int i = 0; i < B.NoOfSymbols; i++) {
            
            if (i == 0)
                
                Digit = R.nextInt(10);
            
            else {
                
                do {
                    NewDigit = R.nextInt(10);
                } while (Digit == NewDigit);
                
                Digit = NewDigit;
                
            }
                
            TargetString += "" + Digit;
            
        }
        
        FirstTargetLetter = Task.FirstLettersUsed.charAt(R.nextInt(Task.FirstLettersUsed.length()));
        
//        do {
//            FirstTargetIndex = R.nextInt(B.NoOfSymbols);
//        } while ((FirstTargetIndex < Task.FirstLetterIndex[0]) | (FirstTargetIndex > Task.FirstLetterIndex[1]));
        
        int Distance = R.nextInt(Task.LetterIndex[1] - Task.LetterIndex[0]) + 1; //get a number from 1 to 13
        
        int LastPossibleStartingPosition = Task.LetterIndex[1] - Task.LetterIndex[0] - Distance;
        
        if (LastPossibleStartingPosition == 0)
            FirstTargetIndex = 1;
        else
            FirstTargetIndex = 1 + R.nextInt(LastPossibleStartingPosition + 1);
        
        FirstTargetIndex += Task.LetterIndex[0] - 1;
        
        TargetString = ReplaceCharAt(TargetString, FirstTargetIndex, FirstTargetLetter);
        
        SecondTargetIndex = 0;
        SecondTargetLetter = '*';
        SecondLetterIsPresentInTarget = true;
        
        //one in three chance there is no target
        if (R.nextInt(3) == 0) SecondLetterIsPresentInTarget = false;
        
        FirstSecondLetterStr = "NO_SECOND_LETTER";
        FirstTargetLetterMemory = FirstTargetLetter;
            
        if (SecondLetterIsPresentInTarget) {
        
            do {
                SecondTargetLetter = Task.SecondLettersUsed.charAt(R.nextInt(Task.SecondLettersUsed.length()));
            } while (FirstTargetLetter == SecondTargetLetter);

//            do {
//                SecondTargetIndex = R.nextInt(B.NoOfSymbols);
//            } while ((SecondTargetIndex < Task.SecondLetterIndex[0]) | (SecondTargetIndex > Task.SecondLetterIndex[1]));

            SecondTargetIndex = FirstTargetIndex + Distance;
            
            TargetString = ReplaceCharAt(TargetString, SecondTargetIndex, SecondTargetLetter);

            SecondTargetLetterMemory = SecondTargetLetter;
            
            FirstSecondLetterStr = "FIRST_LETTER_FIRST";
            
        } else SecondTargetLetter = ' ';
                
        
        CorrectAnswerChar1 = FirstTargetLetter;
        CorrectAnswerChar2 = SecondTargetLetter;
        
    }
    
    public void DrawPicture(int ImageNo) {

        double DTR = 2 * Math.PI / 360.0;
        double AngleInRadians = Angle[ImageNo] * DTR;
        
//        XTarget = Xo + (int)(Task.BIG_CIRCLE_RADIUS * Math.sin(AngleInRadians)); //rfg
//        YTarget = Yo - (int)(Task.BIG_CIRCLE_RADIUS * Math.cos(AngleInRadians));
        XTarget = Xo;
        YTarget = Yo;
        
        ControlCode.Util.KeepMouseFromTarget(MouseX, MouseY, XTarget, YTarget);

        if (Parameters.DebugMode) {
            
            g2.setFont(TextFont);
            g2.setPaint(Color.MAGENTA);
            int x = XTarget + Task.IMAGE_RADIUS + 90;
            DrawString("IMAGE_" + ImageNo, x, YTarget - 45);
            DrawString(B.PresentGamePercent[ImageNo] + "%", x, YTarget - 15);
            DrawString("" + Angle[ImageNo] + " DEG", x, YTarget + 15);
            
        }
        
        //ControlCode.Util.DrawImage(g2, XTarget, YTarget, ImageNo, ControlCode.Util.FOCUS);
        
        if (ImageChosen == ImageNo) {
        
            if (Mode == DISPLAY_COLOR_MODE) {
               
                g2.setPaint(RingColor);
                
                g2.fillOval( //rfg
                        XTarget-Task.SYMBOL_RADIUS, YTarget-Task.SYMBOL_RADIUS, 
                        2 * Task.SYMBOL_RADIUS, 2 * Task.SYMBOL_RADIUS);
                
                long DisplayColorTime = CT.currentTimeMillis() - DisplayColorStartTime;

                if (DisplayColorTime > 1000) {
                
                    //start decision timer in case the target is in the 0 position
//                    StartTimer(GET_DECISION_TIME);
                    StopTimer(DISPLAY_COLOR_TIME);
                    StartTimer(TIME_TO_TARGET_TIME);
                    NumberDisplayStartTime = CT.floatCurrentTimeMillis();
                    Mode = DISPLAY_NUMBERS_MODE;
                }
                
            }
        
            if (Mode == DISPLAY_NUMBERS_MODE) {
                
                g2.setPaint(Color.BLACK);
                
                g2.fillOval(
                        XTarget-Task.SYMBOL_RADIUS, YTarget-Task.SYMBOL_RADIUS, 
                        2 * Task.SYMBOL_RADIUS, 2 * Task.SYMBOL_RADIUS);
                
                g2.setPaint(RingColor);
                
                double DisplayTime = CT.floatCurrentTimeMillis() - NumberDisplayStartTime;
                
                LastPosition = RIGHT_SIDE_UP;
//                if (Game == HIGH_FOCUS) LastPosition = UPSIDE_DOWN;
                
                if (DisplayTime < (B.SymbolDisplayTime * 16.6)) {
                    
                    int Position = RIGHT_SIDE_UP;
                    
//                    if (Game == HIGH_FOCUS) {
//                        if (TargetString.charAt(SymbolIndex) == SecondTargetLetter)
//                            Position = UPSIDE_DOWN;
//                    }
                    
                    FontMetrics Fm = g2.getFontMetrics();
                    
                    int Width = Fm.stringWidth(""+TargetString.charAt(SymbolIndex));
                    
                    TxtRot.RotateCharacter(Position,  g2, 
                            "" + TargetString.charAt(SymbolIndex), SymbolFont, 
                            XTarget - Width, YTarget - 23);
                    
                    LastSecondLetter = SecondTargetLetter;
                
                } else {
                    
                    SymbolIndex++;
                
                    if (SymbolIndex == TargetString.length()) {
                        
//                        if (!SecondLetterIsPresentInTarget) {
//                            
//                            TargetNowKnown = true;
//                            
//                            StopTimer(TIME_TO_TARGET_TIME);
//                            StartTimer(GET_DECISION_TIME_1);
//                            Mode = WAITING_FOR_DECISION_MODE;
//
//                            //if answer is given before now and
//                            //no target in target string then decision
//                            //time is negative
//                            if (AnswerGiven) {
//                                NegativeDecisionTime = CT.currentTimeMillis() - AnswerStartTime;
//                                DecisionTimeIsNegative = true;
//                                //start display answer timer
//                                DisplayAnswerStartTime = CT.currentTimeMillis();
//                                //skip WAITING_FOR_DECISION mode cause answer is already given
//                                Mode = SHOW_DECISION_MODE;
//                            }
//                            
//                        } else
                            Mode = WAITING_FOR_DECISION_MODE_1;
                            ControlCode.KeyPressed = false;
                            StopTimer(TIME_TO_TARGET_TIME);
                            StartTimer(GET_DECISION_TIME_1);
                            
                    } else {
                        
                        char C = TargetString.charAt(SymbolIndex);

                        if (C == SecondTargetLetter) {

                            TargetNowKnown = true;
                            
//                            //the subject has answered before target was shown   rfg
//                            if (AnswerGiven) {
//                                NegativeDecisionTime = CT.currentTimeMillis() - AnswerStartTime;
//                                DecisionTimeIsNegative = true;
//                            }

//                            StopTimer(TIME_TO_TARGET_TIME);
//                            StartTimer(GET_DECISION_TIME);
//                            Mode = GET_DECISION;
                        }                        

                    }
                    
                    NumberDisplayStartTime = CT.floatCurrentTimeMillis();
                
                }
                
            }
        
        }
        
        if (Mode == MOUSE_MOVING_MODE) {
            
//            double Radius = Math.sqrt(Math.pow(XTarget - MouseX,2) + Math.pow(YTarget - MouseY,2));
            double Radius = 0;

            //W("Radius="+Radius);            
            
            //Mouse within target circle?
            if (Radius < Task.IMAGE_RADIUS) {
                GetQue(ImageNo);
                Mode = DISPLAY_COLOR_MODE;
                StopTimer(MOUSE_MOVING_TIME);
                StartTimer(DISPLAY_COLOR_TIME);
                DisplayColorStartTime = CT.currentTimeMillis();
                
                ImageChosen = ImageNo;
                SymbolIndex = 0;
            }
            
        }
        
    }
    
    public void DrawString(String S, int X, int Y) { 
        FontMetrics Fm = g2.getFontMetrics();
        int w = Fm.stringWidth(S);
        g2.drawString(S, X - w/2, Y + 30); // + BigFontHeight/2);
    }
          
    public void DisplayFirstSecondLetter() {
        
        g2.setPaint(Color.WHITE);
        
        g2.setFont(TextFont);

        if (FirstSecondLetterStr.equals("NO_SECOND_LETTER")) {
            DrawString("1ST LETTER " + FirstTargetLetterMemory + "   " + "2ND LETTER _", Xo, Yoffset + 770);
        }

        if (FirstSecondLetterStr.equals("FIRST_LETTER_FIRST")) {
            
            FontMetrics FM = g2.getFontMetrics();
            int Width = 0;
            Width = FM.stringWidth("1ST LETTER   ");
            DrawString("1ST LETTER", Xo - 60 - Width/2, Yoffset + 770);
            Width = FM.stringWidth("2ND LETTER   ");
            DrawString("2ND LETTER", Xo + 160 - Width/2, Yoffset + 770);
                    
            TxtRot.RotateCharacter(RIGHT_SIDE_UP, g2, 
                "" + FirstTargetLetterMemory, TextFont, 
                Xo - 60, Yoffset + 770);

            int Position = LastPosition;
                                
            TxtRot.RotateCharacter(Position, g2, 
                "" + SecondTargetLetterMemory, TextFont, 
                Xo + 170, Yoffset + 770);
            
        }

        if (FirstSecondLetterStr.equals("SECOND_LETTER_FIRST")) {
            
            FontMetrics FM = g2.getFontMetrics();
            int Width = 0;
            Width = FM.stringWidth("2ND LETTER   ");
            DrawString("2ND LETTER", Xo - 60 - Width/2, Yoffset + 770);
            Width = FM.stringWidth("1ST LETTER   ");
            DrawString("1ST LETTER", Xo + 160 - Width/2, Yoffset + 770);
                    
            TxtRot.RotateCharacter(RIGHT_SIDE_UP, g2, 
                "" + FirstTargetLetterMemory, TextFont, 
                Xo + 170, Yoffset + 770);

            int Position = LastPosition;
                    
            TxtRot.RotateCharacter(Position, g2, 
                "" + SecondTargetLetterMemory, TextFont, 
                Xo - 60, Yoffset + 770);
            
        }

    }
    
    public boolean Update(Graphics2D g2in, int mXin, int mYin, boolean Button1In, boolean Button2In, boolean Button3In,
            Task Bin) {
        
        W(""+ControlCode.KeyPressed);
        // W(""+ControlCode.key);
        // even when holding down a key, its only not vk code 0 for 1 in every 5 update cycles
        // when holding down 2 only the second one is registered
        
      MouseX = mXin;
      MouseY = mYin;
      g2 = g2in;
      B = Bin;
      
      //if (ControlCode.key == KeyEvent.VK_A) System.exit(99);
      
//      W("BLOCKNO="+BlockNo);
//      W("ANGLE_0="+Angle[0]+" ANGLE_1="+Angle[1]);
//      W("TRIAL_NO="+TrialNo);
      
      Button1 = Button1In;
      Button2 = Button2In;
      Button3 = Button3In;
            
      if (Parameters.DebugMode) {
          Xo = ControlCode.ScreenSizeX/4;
      }

      Yoffset = Yo - 400;
      
      if (FirstTime) {
          
          g2.setPaint(LastCorrectColor);
          g2.setFont(TextFont);
          DrawString(LastCorrectStr, Xo, Yoffset + 700);
          
          if (B.ShowAccuracy) {
              g2.setPaint(Color.WHITE);
              DrawString(LastAccuracyStr, Xo, Yoffset + 735);
          }

          DisplayFirstSecondLetter();
          
          StartTimerNow = true;
          Answer1Given = false;
          Answer2Given = false;
          
          AcceptInput = false;
          
          boolean Start = ControlCode.Util.ReadNextButton(Xo, Yo, g2, MouseX, MouseY, Button1, OldButton1);
          
          if (Start) FirstTime = false;
          
          return false;
      }
        
      if (StartTimerNow) {
          AnswerStartTime = -1;
          DecisionTimeIsNegative = false;
          StartTimer(WAITING_TO_START_TIME);
          TargetNowKnown = false;
          TaskStartTime = CT.currentTimeMillis() - Task.MasterStartTime;
      }
      
      StartTimerNow = false;
      
      g2.setFont(TextFont);
      
      //instructions
//      g2.setPaint(Color.YELLOW); //rfg
//      DrawString("RIGHT SIDE UP", Xo, Yoffset + 10);
//      g2.setPaint(Color.GREEN);
//      DrawString("UPSIDE DOWN", Xo, Yoffset + 50);
//      g2.setPaint(Color.WHITE);
      
      if (Parameters.DebugMode) {
          
          for (int i = 0; i < ModeStr.length; i++) {
              g2.setPaint(Color.GRAY);
              if (i == Mode) g2.setPaint(Color.MAGENTA);
              g2.drawString(ModeStr[i], 890, Yoffset + 180 + i * 35);
              g2.setPaint(Color.MAGENTA);
              if (i < 6) if (StopTime[i] != -1)
                  g2.drawString(""+StopTime[i], 1220, Yoffset + 180 + i * 35);   //?????
          }
          g2.drawString("TRIAL_NO = " + (B.TrialNo + 1) + " OUT OF " + B.NoOfTrials, 
                  850, Yoffset + 600);
          g2.drawString("BLOCK_NO = " + (B.BlockCount + 1) + " OUT OF " + B.NoOfBlocks, 
                  850, Yoffset + 670);
              
      }
      
      g2.setFont(TextFont);
                  
      DrawPicture(0);
//      DrawPicture(1); //rfg
     
      if (Mode == WAITING_TO_START_MODE) {
          //W("MouseX = "+MouseX + " MouseY="+MouseY+" Xo="+Xo+" Yo="+Yo);
          if ((Math.abs(MouseX - Xo) > 10) | (Math.abs(MouseY - Yo) > 10)) {
              
              //reset timers
              for (int i = 0; i < 5; i++) StopTime[i] = -1;
        
              StopTimer(WAITING_TO_START_TIME);
              StartTimer(MOUSE_MOVING_TIME);
              Mode = MOUSE_MOVING_MODE;
          
          }
      }
      
      if (Mode == WAITING_FOR_DECISION_MODE_1) {
          AcceptInput = true;
          if (ControlCode.KeyPressed & !Answer1Given & AcceptInput) {
              Answer1Given = true;
              SubjAnswerChar1 = (char)ControlCode.key;
              //W(""+SubjAnswerChar1);
              StopTimer(GET_DECISION_TIME_1);
              StartTimer(GET_DECISION_TIME_2);
              ControlCode.KeyPressed = false;
              Mode = WAITING_FOR_DECISION_MODE_2;
          }
      
      }
          
      if (Mode == WAITING_FOR_DECISION_MODE_2) {
          
          if (ControlCode.KeyPressed & Answer1Given & !Answer2Given & AcceptInput) {
              Answer2Given = true;
              SubjAnswerChar2 = (char)ControlCode.key;
              StopTimer(GET_DECISION_TIME_2);
              ControlCode.KeyPressed = false;
              DisplayAnswerStartTime = CT.currentTimeMillis();
              Mode = SHOW_DECISION_MODE;
          }
                    
      }

//      if (Mode == WAITING_FOR_DECISION_MODE) {
//                        
//          if (AnswerGiven) {
//              //start display answer timer
//              DisplayAnswerStartTime = CT.currentTimeMillis();
//              Mode = SHOW_DECISION_MODE;
//          } 
//
//      }
      
      if (Mode == SHOW_DECISION_MODE) {
          
//          if (DecisionTimeIsNegative)
//              StopTime[GET_DECISION_TIME] = -NegativeDecisionTime;
        
          Color CorrectColor = Color.RED;
          String CorrectStr = "INCORRECT";
          
          if (OldMode != SHOW_DECISION_MODE) B.TotalAnswered++;
          
          if (CorrectAnswerChar1 == SubjAnswerChar1 & CorrectAnswerChar2 == SubjAnswerChar2) {
              CorrectColor = Color.GREEN;
              CorrectStr = "CORRECT";
              if (OldMode != SHOW_DECISION_MODE) B.TotalCorrect++;
          }
          if(SubjAnswerChar1 == ' ' & CorrectAnswerChar2 == ' ' & CorrectAnswerChar1 == SubjAnswerChar2) {
              CorrectColor = Color.GREEN;
              CorrectStr = "CORRECT";
              if (OldMode != SHOW_DECISION_MODE) B.TotalCorrect++;
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
          
          DisplayFirstSecondLetter();
          
          long Time = CT.currentTimeMillis() - DisplayAnswerStartTime;
      
          //after 1 second then write data and reset for next task
          if (Time >= 1000) {
              WriteDataLine();
              ImageChosen = -1;
              Mode = WAITING_TO_START_MODE;
              FirstTime = true;
              B.TrialNo++;
              if (B.TrialNo == B.NoOfTrials) return true;
              return false;
          }
          
      }
                
      OldButton1 = Button1;
      OldButton2 = Button2;
      OldButton3 = Button3;

      OldMode = Mode;
      
      g2.setPaint(RingColor);
      
      g2.setFont(TextFont);
      
      if (Parameters.DebugMode) {
          
          g2.drawString("TARGET = " + TargetString, 830, Yoffset + 500);
          g2.drawString("SYMBOL DISPLAY TIME = " + Task.SymbolDisplayTime, 830, Yoffset + 530);
          
          g2.setPaint(Color.WHITE);
      
          g2.drawString("FOCUS TASK", 900, Yoffset + 50);
      
      }
      
      return false;       

    }
    
    public boolean OutputData(String Data) {
 
    	try{

            String PreambleStr = //("" + CurrentTime) + Tab +
                   ControlCode.SubjNo + Tab + ControlCode.ExpCond + Tab + Parameters.ParameterFile + Tab;

            if (IsTetrisTask) PreambleStr += "" + SessionNo + Tab;
            
            String FileName = "EXPERIMENT/DATA/" + B.FileName + " FOCUS_TASK " + ControlCode.SubjNo + ".csv";

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

                String S = //"CONDITION_NO" //+Tab+"MISSION_TIME"
                 //+Tab+
                      "DATA_FILE"+Tab+"DATA_TAG"+Tab+"PARAMETER_FILE";
                 
                 if (IsTetrisTask) S += Tab + "SESSION_NO";
                 
                 S +=
                  Tab+"START_TIME"+Tab+"STOP_TIME"+Tab+"TRIAL_DURATION"
                 +Tab+"TASK_NO"+Tab+"REPETITION_NO"+Tab+"BLOCK_NO"+Tab+"TRIAL_NO"
                 +Tab + "ANGLE_0" + Tab + "ANGLE_1"
                 +Tab + "IMAGE_0_PROB_HIGH_FOCUS" + Tab + "IMAGE_1_PROB_HIGH_FOCUS"       
                 +Tab+"WAIT_FOR_MOUSE_TIME"+Tab+"ENTER_CIRCLE_TIME"+Tab+"TIME_TO_TARGET"+Tab+"DECISION_TIME_1"+Tab+"DECISION_TIME_2"
                 +Tab+"IMAGE_CHOSEN"
                 +Tab+"PROBABILITY"
                 +Tab+"FOCUS_TYPE"                        
                        
                 +Tab+"TARGET_STRING"
                 +Tab+"FIRST_TARGET_LETTER"+Tab+"FIRST_TARGET_INDEX"
                 +Tab+"SECOND_TARGET_LETTER"+Tab+"SECOND_TARGET_INDEX"
                 +Tab+"DISTANCE"
                 +Tab+"CORRECT_ANSWER_1"+Tab+"SUBJ_ANSWER_1"+Tab+"ACCURACY_1"
                 +Tab+"CORRECT_ANSWER_2"+Tab+"SUBJ_ANSWER_2"+Tab+"ACCURACY_2"
                 +Tab+"SUBJ_ANSWERS_SWAPPED"
                 +Tab+"VERSION"; //System.getProperty("user.dir");

                S += "\r\n";

                BW.write(S);

            }

            String S = PreambleStr + Data + "\r\n";

            BW.write(S);

            BW.close();

            B.FirstWriteToFile = false;

            //System.out.println("Done");

    	}catch(IOException e){
            W("EXCEPTION 2");
    		e.printStackTrace();
                 System.exit(3);
    	}
        
        //does not matter what you return, if data
        return false;

    }
    
    public void WriteDataLine() {
                        
        String CorrectStr1 = "INCORRECT";
        String CorrectStr2 = "INCORRECT";
        
        String AnswersSwapped = "FALSE";
        
        if (!SecondLetterIsPresentInTarget) { //if no second letter then see if values switched, cause it is a good answer
        
            if ((SubjAnswerChar1 == ' ') & (SubjAnswerChar2 != ' ')) {
                
                char Temp = SubjAnswerChar1;
                SubjAnswerChar1 = SubjAnswerChar2;
                SubjAnswerChar2 = Temp;
                AnswersSwapped = "TRUE";
                
            }
            
        }           
        
        if (CorrectAnswerChar1 == SubjAnswerChar1) CorrectStr1 = "CORRECT";
        if (CorrectAnswerChar2 == SubjAnswerChar2) CorrectStr2 = "CORRECT";
        
        
        long StopTrialTime = CT.currentTimeMillis() - Task.MasterStartTime;
        
        long DurationTime = StopTrialTime - TaskStartTime;
        
//        String SecondTargetIndexStr = "" + (SecondTargetIndex + 1);
//        
//        if (SecondTargetLetter == '*') SecondTargetIndexStr = "0";

        String S = "" + TaskStartTime + Tab + StopTrialTime + Tab + DurationTime 
                 + Tab + (Parameters.TaskCount+1)+ Tab + (B.RepetitionNo) + Tab + (B.BlockCount+1) + Tab + "" + (B.TrialNo+1) 
                 + Tab + Angle[0] + Tab + Angle[1]
                 + Tab + B.PresentGamePercent[0] + Tab + B.PresentGamePercent[1]
                 + Tab + "" + StopTime[0] + Tab  + "" + StopTime[1] + Tab + "" + StopTime[2] + Tab + "" + StopTime[3] + Tab + "" + StopTime[4]
                 + Tab + ImageChosenString[ImageChosen] + Tab + B.PresentGamePercent[ImageChosen] + Tab + GameStr[Game] 
                 + Tab + (TargetString + "!")
                 + Tab + FirstTargetLetter + Tab + FirstTargetIndex
                 + Tab + SecondTargetLetter + Tab + SecondTargetIndex
                 + Tab + (SecondTargetIndex - FirstTargetIndex)
                 + Tab + CorrectAnswerChar1 + Tab + SubjAnswerChar1 + Tab + CorrectStr1
                 + Tab + CorrectAnswerChar2 + Tab + SubjAnswerChar2 + Tab + CorrectStr2
                 + Tab + AnswersSwapped
                 + Tab + System.getProperty("user.dir");
                
        OutputData(S);
                            
    }
    
}
