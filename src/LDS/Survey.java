
package LDS;

import java.awt.*;
import java.io.*;
import java.util.*;

public class Survey {
    
    SurveyBtn SurveyButton = new SurveyBtn(420-50, 206); 
    
    DoneBtn DoneWithQuestionBtn = new DoneBtn(0, 0);
  
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
    String FileName = "";
    int MySurveyNo;
    int SessionNo;
    int Order[] = new int[1000];
    
    int NoOfButtons;
    int NoOfAnswers;
    
    public int BEFORE_LDS = 0;
    public int BEFORE_TETRIS = 1;
    int BeforeMode;
    
    boolean RandomizeQuestions;
    
    public Survey(String FileNameIn, String BeforeLDSorTETRIS, int SessionNoIn) {
        
        SessionNo = SessionNoIn;
        FileName = FileNameIn;
        ReadSurvey();
        DoneWithQuestionBtn.OldButton1 = true;
        DoneWithQuestionBtn.AddBtn("NEXT");
        DoneWithQuestionBtn.SelectBtn(-1);
        DoneWithQuestionBtn.Xo = (1920 - DoneWithQuestionBtn.buttonWidth)/2;
        DoneWithQuestionBtn.Yo = 970;
            
        if (BeforeLDSorTETRIS.equals("BEFORE_LDS"))
            BeforeMode = BEFORE_LDS;
        
        if (BeforeLDSorTETRIS.equals("BEFORE_TETRIS"))
            BeforeMode = BEFORE_TETRIS;
     
    }
    
    public void W(String S) {
        System.out.println(S);
    }
    
    public void RandomizeTheQuestions() {

        int N = QuestionList.size();
        
        for (int n = 0; n < 1000; n++) {
            Answers[n] = "";
            Order[n] = n;
        }
            
        if (!RandomizeQuestions) return;
        
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
    
       
       try {
            FileInputStream fstream = new FileInputStream("SURVEYS/" + FileName + ".txt");
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
                                                                                               
                if (Cmd.equals("ANSWER")) {
                    SurveyButton.AddBtn(GetString(2));
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
                                        
                if (Cmd.charAt(0) == '/') continue;
                
                else {
                    Parameters.ErrorCode = "ERROR: DO NOT RECOGNIZE SURVEY FILE LINE "+LineNo+" LINE = '"+Arg+"'";
                    W("ErrorCode "+Parameters.ErrorCode);
                    return;
                }
                   
            }
 
            in.close();
            
            
            if (QuestionList.size() > 0) RandomizeTheQuestions();
            
        } catch (Exception e) {
            Parameters.ErrorCode = "Parameter File Error: " + e.getMessage();
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
        
        DoneWithQuestionBtn.Xo = (int)(1160 - DoneWithQuestionBtn.buttonWidth/2);
        //put the done button 40 pixels below the survey button 
        DoneWithQuestionBtn.Yo = SurveyButton.BottomY + 40;
        
        g2.setFont(new Font("Helvetica", Font.BOLD, SurveyBtn.TextHeight));
        
        SurveyBtn.ButtonWidth = 0;
        
        for (int i = 0; i < SurveyBtn.NoOfButtons; i++) {
            FontMetrics Fm = g2.getFontMetrics();
            int StringWidth = Fm.stringWidth(SurveyBtn.ButtonLabel[i]);
            if (StringWidth > SurveyBtn.ButtonWidth) 
                SurveyBtn.ButtonWidth = StringWidth;
        }
        
        SurveyBtn.ButtonWidth += 26;
                
        g2.setPaint(Color.BLACK);
        
        g2.fillRect(0, 0, 1920, 1080);
              
        int MessageX = 840;
        
        //draw instructions
        g2.setPaint(Color.WHITE);
        DisplayMessage(MessageX, 206, Instructions);
                
        //draw question
        g2.setFont(new Font("Helvetica", Font.BOLD, TextHeight));
        g2.setPaint(Color.MAGENTA);
        DisplayMessage(MessageX, 512, (String)QuestionList.get(Order[QuestionNo]));
        
        if (QuestionNo == (QuestionList.size() - 1))
            DoneWithQuestionBtn.buttonLabel[0] = "DONE";
        
        //draw answer buttons
        SurveyButton.SetOrigin(1160, 512);
        
        SurveyButton.Update(g2, MouseX, MouseY, Button1);
        
        boolean AllQuestionsAnswered = true;
        
        if (SurveyButton.ButtonSelected == -1)
                AllQuestionsAnswered = false;
        
        boolean GoToNextQuestion = false;
                    
        int DoneNo = DoneWithQuestionBtn.Update(g2, MouseX, MouseY, Button1);
            
        if (DoneNo == 0) {
        
             GoToNextQuestion = true;
                
             DoneWithQuestionBtn.SelectBtn(-1);
                
        }
            
//        if (AllQuestionsAnswered & (Stop == 1)) GoToNextQuestion = true;
        
        if (GoToNextQuestion) {
            
            Answers[Order[QuestionNo]] += "," + (SurveyButton.ButtonSelected + 1);
            
            QuestionNo++;
            
            NoOfAnswers = QuestionList.size();
            
            if (QuestionNo == QuestionList.size()) {
                                
                String Message = "";
                
                for (int i = 0; i < QuestionList.size(); i++) 
                    Message += Answers[i];
                
                OutputData(Message);
                
                return true;
            
            }
            
            SurveyButton.SelectBtn(-1);
                
        }
                
        return false;
                
    }
    
    public boolean OutputData(String Data) {
 
        char Tab = ',';
        
    	try{

            String PreambleStr =
                   ControlCode.SubjNo + Tab + ControlCode.ExpCond + Tab + Parameters.ParameterFile + Tab
                   + "" + SessionNo + Tab;
            
            String OutputFileName = "EXPERIMENT/DATA/" + FileName + " " + ControlCode.SubjNo + ".csv";

            File file = new File(OutputFileName);

            W("DELTING DATA");
            file.delete();
            file.createNewFile();

            //true = append file
            FileWriter fileWritter = new FileWriter(FileName,true);
            BufferedWriter BW = new BufferedWriter(fileWritter);

            String S = "DATA_FILE"+Tab+"DATA_TAG"+Tab+"PARAMETER_FILE" + Tab + "SESSION_NO";
                 
            for (int i = 0; i < NoOfAnswers; i++) S += "ANSWER " + (i+1);

            S += "\r\n";

            BW.write(S);

            S = PreambleStr + Data + "\r\n";

            BW.write(S);

            BW.close();

            W("Done");

    	}catch(IOException e){
    		e.printStackTrace();
    	}
        
        //does not matter what you return, if data
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
            Parameters.ErrorCode = "LINE " + LineNo + "   '" + GetString(I)+"'   IS NOT AN INTEGER";
            return 0;
        }
        
        return Integer.parseInt(GetString(I));
    
    }
        
    public double GetFloat(int I) {
        
        if (NotAFloatingPointNumber(GetString(I))) {
            Parameters.ErrorCode = "LINE " + LineNo + "   '" + GetString(I)+"'   IS NOT A FLOATING POINT NUMBER";
            return 0;
        }
        
        return Double.parseDouble(GetString(I));
        
    }

    public void DrawString(String S, int X, int Y) {
        
        FontMetrics Fm = g2.getFontMetrics();
        int w = Fm.stringWidth(S);
        
        g2.drawString(S, X - w/2, Y);
        
    }
    
}
