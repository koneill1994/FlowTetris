
package LDS;

import static LDS.Parameters.ErrorCode;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Survey {
    
    public static int SurveyMessageMode;
    
    int BreakMessageMode;
    
    CurrentTime CT = new CurrentTime();

    SurveyBtn SurveyButton[] = { new SurveyBtn(420-50, 206), new SurveyBtn(1260+50, 206), 
                                 new SurveyBtn(420-50, 718), new SurveyBtn(1260+50, 718) }; 
    
    DoneBtn DoneWithQuestionBtn = new DoneBtn(0, 0);
  
//    SurveyInstructions SI = null;
    
    Graphics2D g2;
    Font BigFont = new Font("Helvetica", Font.PLAIN, 18);
    int TextHeight = 20;
    int TextYSpacing = 20;
    Color TextColor = Color.WHITE;
    String Cmd = "";
    String Arg = "";
    String Answers[] = new String[1000];
    int LineNo = 0;
    int QuestionNo = 0;
    ArrayList QuestionList = new ArrayList();
    String Instructions = "";
    //boolean AnswerAllPlayers;
    //boolean SurveyInstructions;
    String FileName = "";
    int MySurveyNo;
    int SessionNo;
    boolean SendReady;
    boolean SendReadyForBreakFlag;
    boolean WaitingForOtherPlayersFlag = true;
    boolean BreakBeforeSession;
    public static int BreakBeforeSessionTime;
    boolean GetProgressBarStartTime;
    long ProgressBarStartTime;
    public boolean DuringSchoolTime;
    static boolean StartBreakCountdown;
    String ModeString; //either HOME_TIME or SCHOOL_TIME
    int Order[] = new int[1000];
    
    //for showing JPG slides
    boolean JpgSlideViewer;
    boolean JpgFirstTime = true;
    int JpgNo;
    int JpgX;
    int JpgY;
    double JpgScaleFactor;
    String JpgFolder;
    String JpgFileName;
    int JpgNoOfSlides;
    public static BufferedImage BuffImage[] = new BufferedImage[1];
    int ImageWidth;
    int ImageHeight;
    
    int NoOfButtons;
    
    boolean Flag, Flag2;
    
    public static boolean DisplayPlayersPoints;
    
    public Survey(String FileNameIn, int SessionNoIn, int MySurveyNoIn, String ReadyString, String ModeString) {
        
        SessionNo = SessionNoIn;
        MySurveyNo = MySurveyNoIn;
        FileName = FileNameIn;
        ReadSurvey();
        DoneWithQuestionBtn.OldButton1 = true;
        DoneWithQuestionBtn.AddBtn("NEXT");
        DoneWithQuestionBtn.SelectBtn(-1);
        DoneWithQuestionBtn.Xo = (1920 - DoneWithQuestionBtn.buttonWidth)/2;
        DoneWithQuestionBtn.Yo = 970;
        
    }
    
    public void W(String S) {
        System.out.println(S);
    }
    
    public void RandomizeQuestions() {

        int N = QuestionList.size();
        
        for (int n = 0; n < 1000; n++) {
            Answers[n] = "";
            Order[n] = n;
        }
            
        if (N < 2) return;
        
        for (int n = 0; n < 1000; n++)
        
            for (int i = 0; i < N; i++) {
                int Target1 = ControlCode.R.nextInt(N);
                int Target2 = ControlCode.R.nextInt(N);
                int Temp = Order[Target1];
                Order[Target1] = Order[Target2];
                Order[Target2] = Temp;
            }
        
    }
    
    public void ReadSurvey() {
           
       SurveyBtn.PlayerCounter = 0;
       
       try {
            FileInputStream fstream = new FileInputStream( 
                    "SURVEYS/" + FileName + ".txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String S;
            
            //Read File Line By Line
            while ((S = br.readLine()) != null)   {
                
                W(S);
            
                LineNo++;
                
                Arg = S;

                Cmd = GetString(1);

                if (Cmd.equals("STOP")) {
                    //System.exit(10);
                    break;
                } else
                    
//                if (Cmd.equals("SURVEY_INSTRUCTIONS")) {
//                    
//                    SurveyInstructions = true;
//                    
//                    SI = new SurveyInstructions();
//                                
//                    while ((S = br.readLine()) != null)   {
//                
//                        SI.InstructionList.add(S);
//                                   
//                    }
//                    
//                    break;
//
//                } else
//                                                            
                                          
                if (Cmd.equals("TEXT_COLOR_RGB")) {
                    TextColor = new Color(GetInteger(2), GetInteger(3), GetInteger(4));
                } else
                    
                if (Cmd.equals("TEXT_Y_SPACING")) {
                    TextYSpacing = GetInteger(2);
                } else

                if (Cmd.equals("TEXT_HEIGHT")) {
                    TextHeight = GetInteger(2);
                } else
               
                if (Cmd.equals("ANSWER")) {
                    SurveyButton[0].AddBtn(GetString(2));
                    W("ADDING ANSWER = "+GetString(2));
                    NoOfButtons++;
                    W("XXX NO OF BUTTONS="+SurveyBtn.NoOfButtons);
                    //System.exit(10);;
                } else
                    
                if (Cmd.equals("INSTRUCTIONS")) {
                    Instructions = GetRestOfString(Arg);
                    W("INSTRUCTIONS="+Instructions);
                    //System.exit(9);
                } else
                    
                if (Cmd.equals("QUESTION")) {
                    QuestionList.add(GetRestOfString(Arg));
                } else
                    
                if (Cmd.equals("BREAK_BEFORE_SESSION_TIME")) {
                    BreakBeforeSession = true;
                    BreakBeforeSessionTime = GetInteger(2);
                } else
                    
                if (Cmd.equals("JPG_SLIDE_VIEWER")) {
                    JpgSlideViewer = true;
                } else
                    
                if (Cmd.equals("JPG_X")) {
                    JpgX = GetInteger(2);
                } else
                    
                if (Cmd.equals("JPG_Y")) {
                    JpgY = GetInteger(2);
                } else
                    
                if (Cmd.equals("JPG_SCALE_FACTOR")) {
                    JpgScaleFactor = GetFloat(2);
                } else
                    
                if (Cmd.equals("JPG_FOLDER")) {
                    JpgFolder = GetString(2);
                } else
                    
                if (Cmd.equals("JPG_FILE_NAME")) {
                    JpgFileName = GetString(2);
                } else
                    
                if (Cmd.equals("JPG_NO_OF_SLIDES")) {
                    JpgNoOfSlides = GetInteger(2);
                } else
                    
                if (Cmd.charAt(0) == '/') continue;
                
                else {
                    ErrorCode = "ERROR: DO NOT RECOGNIZE SURVEY FILE LINE "+LineNo+" LINE = '"+Arg+"'";
                    W("ErrorCode");
                    return;
                }
                   
            }
 
            in.close();
            
//            C.TransmitToServer(Client.MyName + ",AT END OF LOAD  NO OF BUTTONS="+SurveyBtn.NoOfButtons);
            
            if (QuestionList.size() > 0) RandomizeQuestions();
            
        } catch (Exception e) {
            ErrorCode = "Parameter File Error: " + e.getMessage();
            System.err.println("Parameter File Error: " + e.getMessage());
        }
            
    }
    
    //get rest of string after first comma
    public String GetRestOfString(String Arg) {
        
        String RestOfArg = "";
        int i = 0;
        
        //find first comma
        for (; i < Arg.length(); i++) {
            if (Arg.charAt(i) == ',') {
                i++;
                break;
            }
        }
        
        //copy rest of string
        for (; i < Arg.length(); i++) {
            if ((i <= Arg.length() - 2) | //add character if not last character
                    ((i == Arg.length() - 1) & Arg.charAt(i) != ',')) //add last character if not comma
                        RestOfArg += Arg.charAt(i);
        }
        
        return RestOfArg;
    
    }
    
    public void DrawProgressBar(long DurationTime, long TotalDurationTime) {
        
        int Width = 400;
        int Height = 20;
        int x = (1680 - Width)/2;
        int y = 1050/2 - 70;
        
        long RemainingTime = TotalDurationTime - DurationTime;
        int BarWidth = (int)(Width * (RemainingTime/(double)TotalDurationTime));
        //W("IN PROGRESS BAR");
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x, y, Width, Height);
        g2.setStroke(new BasicStroke(1));
        g2.fillRect(x, y, BarWidth, Height);
               
    }
 
    public void DisplayMessage(int Xcenter, int Ycenter, String Message) {
                
        String MessageString[] = new String[30];
        
        for (int i = 0; i < 30; i++) MessageString[i] = "";
        
        int NoOfMessages = 0;
        
        //break message into strings where there is a # sign
        for (int i = 0; i < Message.length(); i++) {
            
            char C = Message.charAt(i);
            
            if (C == '#')
                NoOfMessages++;
            else
                MessageString[NoOfMessages] += C;
            
        }
        
        //account for the first message
        NoOfMessages++;
        
        //find left side of messages by finding string width
        int MessageWidth;
        
        int Ytop = Ycenter - (NoOfMessages * (TextHeight + TextYSpacing) - TextYSpacing)/2;
        
        int Y = 0;
 
        for (int i = 0; i < NoOfMessages; i++) {
            FontMetrics Fm = g2.getFontMetrics();
            MessageWidth = Fm.stringWidth(MessageString[i]);
            g2.drawString(MessageString[i], Xcenter - MessageWidth/2, Ytop + Y);
            Y += TextHeight + TextYSpacing;
        }
        
    }

    public boolean Update(Graphics2D g2in, int MouseX, int MouseY, boolean Button1) {
        
        g2 = g2in;
 
        SurveyBtn.NoOfButtons = NoOfButtons;
                
        if (JpgSlideViewer) {
            
            if (JpgFirstTime) {
             
                //make button red and bigger
                DoneWithQuestionBtn.buttonColor = Color.RED;
                DoneWithQuestionBtn.buttonWidth += 6;
                DoneWithQuestionBtn.buttonHeighth += 4;
                DoneWithQuestionBtn.LabelIndentY += 3;
                DoneWithQuestionBtn.BtnFont = new Font("Helvetica", Font.BOLD, 16);
                DoneWithQuestionBtn.buttonLabel[0] = "NEXT";
                DoneWithQuestionBtn.Yo = 970 + 50;
                JpgNo = 1;
                JpgFirstTime = false;
                LoadJpg(1);
                
            }
            
            if (JpgNo == JpgNoOfSlides) 
                DoneWithQuestionBtn.buttonLabel[0] = "DONE";
                
            g2.drawImage(BuffImage[0],
                  JpgX, JpgY,
                  ImageWidth, ImageHeight,
                  ControlCode.Frame);
            
            int DoneNo = DoneWithQuestionBtn.Update(g2, MouseX, MouseY, Button1);
            
            if (DoneNo == 0) {
        
                JpgNo++;
            
                if (JpgNo > JpgNoOfSlides) return true;
                
                LoadJpg(JpgNo);
                
                DoneWithQuestionBtn.SelectBtn(-1);
                
                //C.TransmitToServer("MONITOR,START_MASTER_TIMER");
                
            }
            
            //return finished if is model
            //if (Client.IsModel | ControlCode.SubjectHasLeftEarly) return true;
            
            return false;
            
        }
        
        if (BreakBeforeSession) {
            
            g2.setPaint(Color.BLACK);
        
            //g2.fillRect(0, 0, 1920, ControlCode.StageHeight);
              
            if (StartBreakCountdown) {
                
                StartBreakCountdown = false;
                
                WaitingForOtherPlayersFlag = false;
                
            }
            
//            if (WaitingForOtherPlayersFlag) {
//                
//                g2.setPaint(Color.GREEN);
//                
//                g2.setFont(BigFont);
//                
//                DrawString("PLEASE WAIT", 1920/2, 1080/2);
//                
//                if (!SendReadyForBreakFlag) {
//                    
//                    SendReadyForBreakFlag = true;
//                                    
//                    C.TransmitToServer(Client.MyName + ",READY_FOR_BREAK");
//
//                }
//                
//                return false;
//                
//            }
//            
//            if (!GetProgressBarStartTime) {
//                
//                GetProgressBarStartTime = true;
//                ProgressBarStartTime = CT.currentTimeMillis();
//                
//            }
//            
            long ElapseTime = CT.currentTimeMillis() - ProgressBarStartTime;
            
            if (ElapseTime >= (BreakBeforeSessionTime * 1000)) {
                W("RETURN TRUE 1");
                DisplayPlayersPoints = false;
                return true;
            }
                
            g2.setPaint(Color.GREEN);    
            g2.setFont(BigFont);
                
            if (DuringSchoolTime) 
                DrawString("ENTERING CLASS TIME - PREPARE TO STUDY", 1680/2, 200);
            else
                DrawString("ENTERING HOME TIME - PLEASE CHOOSE AN ACTIVITY", 1680/2, 200);
            
            SurveyMessageMode = 0;
//            W("--------------------------");
//            W("ENTERING CLASS TIME");
//            W("");
            
            
            if (BreakMessageMode <= 3) {
                ControlCode.Frame.setState(JFrame.ICONIFIED);
                BreakMessageMode++;
            }
             
            DisplayPlayersPoints = true;
            
            g2.setPaint(Color.GREEN);
            DrawProgressBar(ElapseTime, BreakBeforeSessionTime * 1000);
            
//            C.DisplayScoresToPlayer(g2);
            
            return false;
            
        } else
            
            BreakMessageMode = 0;


/*
        if (SurveyInstructions) {
            
            if (SurveyMessageMode <= 3) {
                ControlCode.Frame.setState(JFrame.ICONIFIED);
                SurveyMessageMode++;
            }
            
            if (Client.IsModel | ControlCode.SubjectHasLeftEarly) return true;
            
            if (SI.Update(g2, MouseX, MouseY, Button1)) {
                W("RETURN TRUE 2");
                return true;   
            }
            
            return false;
            
        }
        
        //return finished if is model
        if (Client.IsModel | ControlCode.SubjectHasLeftEarly) return true;
            
        if (!AnswerAllPlayers) {
            DoneWithQuestionBtn.Xo = (int)(1160 - DoneWithQuestionBtn.buttonWidth/2);
            //put the done button 40 pixels below the survey button 
            DoneWithQuestionBtn.Yo = SurveyButton[0].BottomY + 40;
        }
        */
        g2.setFont(new Font("Helvetica", Font.BOLD, SurveyBtn.TextHeight));
        
        SurveyBtn.ButtonWidth = 0;
        
        for (int i = 0; i < SurveyBtn.NoOfButtons; i++) {
            FontMetrics Fm = g2.getFontMetrics();
            int StringWidth = Fm.stringWidth(SurveyBtn.ButtonLabel[i]);
            if (StringWidth > SurveyBtn.ButtonWidth) 
                SurveyBtn.ButtonWidth = StringWidth;
        }
        
        SurveyBtn.ButtonWidth += 26;
        
//        W("SurveyBtn.ButtonWidth="+SurveyBtn.ButtonWidth);
        
        g2.setPaint(Color.BLACK);
        
        //g2.fillRect(0, 0, 1920, ControlCode.StageHeight);
              
        int MessageX = 420 + 100;
        //if (AnswerAllPlayers) MessageX = 840;
        
        //draw instructions
        g2.setPaint(Color.WHITE);
        DisplayMessage(MessageX, 206, Instructions);
                
        //draw question
        g2.setFont(new Font("Helvetica", Font.BOLD, TextHeight));
        g2.setPaint(Color.MAGENTA);
        DisplayMessage(MessageX, 512, (String)QuestionList.get(Order[QuestionNo]));
        
        if (SendReady & (QuestionNo == (QuestionList.size() - 1)))
            DoneWithQuestionBtn.buttonLabel[0] = "DONE";
        /*
        //draw answer buttons
        int Stop = 1;
        if (AnswerAllPlayers) {
            Stop = Client.NoOfPlayers; //use to be 4
            SurveyBtn.ShowPlayerNo = true;
        } else SurveyButton[0].SetOrigin(1160, 512);
        
        for (int i = 0; i < Stop; i++)
            if (!AnswerAllPlayers | (Client.MyPlayerIndex != i)) {
                SurveyButton[i].Update(g2, MouseX, MouseY, Button1);
                //W("UPDATING SURVEY BUTTONS NO="+SurveyBtn.NoOfButtons);
//                SurveyBtn.NoOfButtons = 5;
            }
        */
        boolean AllQuestionsAnswered = true;
        /*
        for (int i = 0; i < Stop; i++)
            if ((!AnswerAllPlayers | (Client.MyPlayerIndex != i))
                    & (SurveyButton[i].ButtonSelected == -1))
                AllQuestionsAnswered = false;
        */
        boolean GoToNextQuestion = false;
        
        //if (AllQuestionsAnswered & AnswerAllPlayers) {
        if (AllQuestionsAnswered) {
            
            int DoneNo = DoneWithQuestionBtn.Update(g2, MouseX, MouseY, Button1);
            
            if (DoneNo == 0) {
        
                GoToNextQuestion = true;
                
                DoneWithQuestionBtn.SelectBtn(-1);
                
            }
            
        }
        
//        if (AllQuestionsAnswered & (Stop == 1)) GoToNextQuestion = true;
        
        if (GoToNextQuestion) {
            
            //for (int i = 0; i < Stop; i++)
            //    Answers[Order[QuestionNo]] += "," + (SurveyButton[i].ButtonSelected + 1);
            
            QuestionNo++;
            
            int NoOfAnswers = QuestionList.size();
/*            
            if (AnswerAllPlayers)
                NoOfAnswers = QuestionList.size() * Client.NoOfPlayers;
            
            if (QuestionNo == QuestionList.size()) {
                
                String Message = Client.MyName + ",SENDING_SURVEY_ANSWERS," +
                    FileName + "," + SessionNo + "," + 
                    MySurveyNo + "," + QuestionList.size() + "," + NoOfAnswers;
                
                for (int i = 0; i < QuestionList.size(); i++) 
                    Message += Answers[i];
                
                C.TransmitToServer(Message);
                
                W("RETURN TRUE 3");
                return true;
            
            }
 */           
            for (int i = 0; i < 4; i++)
                SurveyButton[i].SelectBtn(-1);
                
        }
        
//        if (!Flag2) {
//            
//            Flag2 = true;
//
//            C.TransmitToServer(Client.MyName + "," + FileName + " AT END OF LOOP  NO OF BUTTONS="+SurveyBtn.NoOfButtons);
//
//        }
        
        return false;
                
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
    
    public boolean NotAFloatingPointNumber(String Integer) {
        
        for (int i = 0; i < Integer.length(); i++) {
            char c = Integer.charAt(i);
            if ((c != '.') & ((c < '0') | (c > '9'))) return true;
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
        
    public double GetFloat(int I) {
        
        if (NotAFloatingPointNumber(GetString(I))) {
            ErrorCode = "LINE " + LineNo + "   '" + GetString(I)+"'   IS NOT A FLOATING POINT NUMBER";
            return 0;
        }
        
        return Double.parseDouble(GetString(I));
        
    }

    public void DrawString(String S, int X, int Y) {
        
        FontMetrics Fm = g2.getFontMetrics();
        int w = Fm.stringWidth(S);
        
        g2.drawString(S, X - w/2, Y);
        
    }
    
    public void LoadJpg(int JpgNo) {

       // String FileName = ControlCode.MainDir + 
       //     "/SURVEYS/" + JpgFolder + "/" + JpgFileName + JpgNo + ".jpg";

        System.out.println("FILENAME="+FileName);

        ImageIcon icon0 = new ImageIcon(FileName);
        Image i0 = icon0.getImage();
        // draw the Image into a BufferedImage
        int w = i0.getWidth(null), h = i0.getHeight(null);
        ImageWidth = (int)(JpgScaleFactor * w);
        ImageHeight = (int)(JpgScaleFactor * h);
        //W("w="+w+" h="+h);
        BuffImage[0] = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D imageGraphics = BuffImage[0].createGraphics();
        //W("buf="+BuffImage[i]);
        imageGraphics.drawImage(i0, 0, 0, null);

    }

}
