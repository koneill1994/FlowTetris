/*
 * Btn.java
 *
 * Created on January 8, 2007, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package client;

import java.awt.*;

/**
 *
 * @author RGREEN
 */

public class SurveyBtn {
    
    boolean OldButton1;
    int Answer = 0;
    boolean Border = false;
    int Xo;
    int Yo;
    static String ButtonLabel[] = new String[50];
    static int NoOfButtons = 0;
    int ButtonSelected = -1;
    int mouseX;
    int mouseY;
    
    //constants
    static int TextHeight = 15;
    static int ButtonWidth = 0;
    int ButtonHeight = 40;
    int ButtonHeight2 = 50;
    int BorderWidth = 0;
    int LabelIndentY = 0;
    Color BorderColor = new Color(100, 100, 250);
    Color ButtonLinerColor = new Color(255, 255, 255);
    public boolean StackVertically = true;
    int Xorigin, Yorigin;
    int MyPlayerNumber = 0;
    static int PlayerCounter = 0;
    static boolean ShowPlayerNo;
    int BottomY = 0;
    
    public SurveyBtn(int Xorigin, int Yorigin) {
        this.Xorigin = Xorigin;
        this.Yorigin = Yorigin;
        MyPlayerNumber = ++PlayerCounter;
    }
    
    public void W(String S) {
        System.out.println(S);
    }
    
    public void SetOrigin(int Xorigin, int Yorigin) {
        this.Xorigin = Xorigin;
        this.Yorigin = Yorigin;
    }
    
    public boolean mouseWithin(int X1, int Y1, int X2, int Y2) {
      if ( (mouseX >= X1) && (mouseY >= Y1) &&
           (mouseX <= X2) && (mouseY <= Y2) ) return true;
      else return false;
    }
    
    public void AddBtn(String str) {
        ButtonLabel[NoOfButtons++] = str;
    }
    
    public void SelectBtn(int s) {
        ButtonSelected = s;
    }
    
    public int Update(Graphics2D g2, int mX, int mY, boolean Button1) {
        
        mouseX = mX;
        mouseY = mY;
        
        Xo = Xorigin - ButtonWidth/2;
        Yo = Yorigin - (NoOfButtons * ButtonHeight2 - ButtonHeight)/2;
        
        g2.setPaint(BorderColor);
        
        if (Border) {
          
            if (StackVertically)
                g2.fillRect(Xo, Yo, 
                    //2*borderWidth  + buttonWidth, 2*borderWidth + NoOfButtons*buttonHeighth2-15);
                    ButtonWidth, 2*BorderWidth + NoOfButtons*ButtonHeight2-15);
            else
                g2.fillRect(Xo, Yo, 
                    2*BorderWidth + NoOfButtons*ButtonWidth, 2*BorderWidth + ButtonHeight);
        
        }

        String PlayerStr = "PLAYER_"+MyPlayerNumber;
        
        g2.setPaint(Color.WHITE);
        g2.setFont(new Font("Helvetica", Font.BOLD, 25));
        FontMetrics PlayerFm = g2.getFontMetrics();
        int PlayerStringWidth = PlayerFm.stringWidth(PlayerStr);
        if (ShowPlayerNo)
            g2.drawString(PlayerStr, Xo + ButtonWidth/2 - PlayerStringWidth/2, Yo - 10);

        //W("UPDATING");
        
        //scan buttons
        for (int i = 0; i < NoOfButtons; i++) {
            
            //establish button borders
            int bx1 = Xo + BorderWidth;
            int by1 = Yo + BorderWidth;
            
            if (StackVertically)
                by1 += i * ButtonHeight2;
            else
                bx1 += i * ButtonWidth;
            
            int bx2 = bx1 + ButtonWidth;
            int by2 = 0;
            
            //if (IsCategoryBtn) by2 = by1 + buttonHeighth2;
            //else 
            by2 = by1 + ButtonHeight;
              
            //find the bottom of all the buttons
            BottomY = by2;
            
            //find out if mouse within button
            boolean mouseInside = mouseWithin(bx1+1, by1, bx2-1, by2);
                        
            if (i == ButtonSelected) g2.setStroke(new BasicStroke(3));
            
            g2.drawRect(bx1, by1, ButtonWidth+1, ButtonHeight+1);
            
            g2.setStroke(new BasicStroke(1));
            
            //draw label
            g2.setFont(new Font("Helvetica", Font.BOLD, TextHeight));
            FontMetrics fm = g2.getFontMetrics();
            int StringWidth = fm.stringWidth(ButtonLabel[i]);
            LabelIndentY = ButtonHeight/2 + TextHeight/2;
            
            g2.drawString(ButtonLabel[i], bx1 + ButtonWidth/2 - StringWidth/2, by1 + LabelIndentY);
            
            if (mouseInside) {
                
                if (Button1 & !OldButton1) {
                    {
                        ButtonSelected = i;
                    }
                }
            
            }
            
        }
        
        OldButton1 = Button1;
        
        return ButtonSelected;
        
    }
    
}
