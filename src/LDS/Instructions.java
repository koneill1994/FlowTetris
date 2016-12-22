/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LDS;

/**
 *
 * @author Student
 */

import java.awt.*;
import java.io.*;
import java.util.*;

public class Instructions {
    
  int MouseX, MouseY;
  Graphics2D g2;
  boolean Button1 = false;
  boolean Button3 = false;
  
  String Cmd = "";
  String Arg = "";

  public static ArrayList InstructionList = new ArrayList();
  
  int TextPositionX = 100;
  int TextPositionY = 100;
  int TextSpacingX = 50;
  int TextSpacingY = 25;
  
  boolean MessageMode;
  boolean TextMode;
  
  DoneBtn DoneWithInstructionsBtn = new DoneBtn(0, 0);
  
  public Instructions() {      
        
    DoneWithInstructionsBtn.AddBtn("DONE");
    DoneWithInstructionsBtn.SelectBtn(-1);
    DoneWithInstructionsBtn.Xo = (1680 - DoneWithInstructionsBtn.buttonWidth)/2;
    DoneWithInstructionsBtn.Yo = 970;
    
  }
    
  public void ReadInstructions(String FileNameIn) {
      
    try {
            String FileName = "EXPERIMENT/INSTRUCTIONS/" + FileNameIn + ".txt";
            
            File file = new File(FileName);
 
    	    if (!file.exists()) {
                Parameters.ErrorCode = "INSTRUCTION FILE " + FileNameIn + " DOES NOT EXIST";
                return;
            }
               
            FileInputStream fstream = new FileInputStream(FileName);
            
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String S;
            
            //Read File Line By Line
            while ((S = br.readLine()) != null)   {
                
                InstructionList.add(S);
                                   
            }

            
            //Close the input stream
            in.close();
//            ControlCode.FrameCounter = 0;  //DEC8 14
            
        } catch (Exception e) { //Catch exception if any
            System.err.println("Parameter File Error: " + e.getMessage());
        }
        
  }
   
  public void W(String S) {
      System.out.println(S);
  }
  
  public boolean Update(Graphics2D g2in, int mXin, int mYin, boolean Button1, boolean Button3) {
        
      MouseX = mXin;
      MouseY = mYin;
      g2 = g2in;
      
      int PosY = 0;
      
//      String ExperimentNoString = "" + ControlCode.ConditionNo;
      
      for (int i = 0; i < InstructionList.size(); i++) { 
          
          Arg = (String)InstructionList.get(i);
          
          //System.out.println(Arg);

          Cmd = GetCommaString(1);

          if (Cmd.equals("END_MESSAGE")) {
              MessageMode = false;
              TextMode = false;
              PosY = 0;
              continue;
          }
          
          if (Cmd.equals("BEGIN_MESSAGE")) {
              MessageMode = true;
//              if (GetCommaString(2).equals(ExperimentNoString))
//                  TextMode = true;
              continue;
          }
          
          if (MessageMode) {
              
              if (TextMode) {

                  String delims = "[~]";
                  String[] Tokens = Cmd.split(delims);
        
                  g2.drawString(Tokens[0], TextPositionX, TextPositionY + PosY);
                  
                  if (Tokens.length == 2) 
                      g2.drawString(Tokens[1],
                              TextPositionX + TextSpacingX, 
                              TextPositionY + PosY);
            
                  PosY += TextSpacingY;

              }
              
              continue;
              
          }
          
          if (Cmd.charAt(0) == '/') continue;
          
          if (Cmd.equals("STOP")) {
              break;
          }
                
          if (Cmd.equals("TEXT_POSITION_X")) {
              TextPositionX = GetCommaInteger(2);
          }
                 
          if (Cmd.equals("TEXT_POSITION_Y")) {
              TextPositionY = GetCommaInteger(2);
          }
                 
          if (Cmd.equals("TEXT_SPACING_X")) {
              TextSpacingX = GetCommaInteger(2);
          }
                 
          if (Cmd.equals("TEXT_SPACING_Y")) {
              TextSpacingY = GetCommaInteger(2);
          }
                 
          if (Cmd.equals("TEXT_COLOR")) {
              SetTextColor(GetCommaInteger(2),GetCommaInteger(3),GetCommaInteger(4));
          }
          
          if (Cmd.equals("TEXT_SIZE")) {
              SetTextSize(GetCommaInteger(2));
          }
          
      }
      
      int Done = DoneWithInstructionsBtn.Update(g2, MouseX, MouseY, Button1);

      //W("Task.SkipDoneButton="+Task.SkipDoneButton);

      //these instructions not used anymore
      if (1 == 1) return true;
      
      //if ((Done == 0) | Task.SkipDoneButton) return true;
      if (Done == 0) return true;
      
      return false;
      
  }
  
  public void SetTextColor(int R, int G, int B) {
      g2.setPaint(new Color(R,G,B));
  }

  public void SetTextSize(int Size) {
      g2.setFont(new Font("Helvetica", Font.BOLD, Size));
  }

  public String GetCommaString(int I) {
        
        int A = I-1;
        int c;
        int CommaCount = 0;
        
        //char Tab = (char)9;
        
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

    public int GetCommaInteger(int I) {
        
        //System.out.println("SXX="+GetSpacedString(I));
        return Integer.parseInt(GetCommaString(I));
    
    }
    
}
