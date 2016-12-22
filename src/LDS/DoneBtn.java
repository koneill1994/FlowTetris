/*
 * Btn.java
 *
 * Created on January 8, 2007, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package LDS;

import java.awt.*;

/**
 *
 * @author RGREEN
 */

public class DoneBtn {
    
    //variables
    boolean OldButton1;
    int Answer = 0;
    boolean Border = false;
    boolean Square = true;
    int Xo;
    int Yo;
    String buttonLabel[] = new String[50];
    int NoOfButtons = 0;
    int buttonSelected = -1;
    int mouseX;
    int mouseY;
    Color buttonColor = Color.CYAN;
    Color textColor;
    int mouseInsideButton;
    boolean buttonLookedAt = false;
    boolean HandCursorOn = false;
    
    //constants
    int buttonWidth = 60+10;
    int buttonWidth2 = 100;
    int buttonHeighth = 16+6;
    int buttonHeighth2 = 25;
    int borderWidth = 0;
    int LabelIndentY = 16+3;
    Color borderColor = new Color(0, 0, 0);
    Color buttonLinerColor = new Color(255, 255, 255);
    Font BtnFont = new Font("Helvetica", Font.BOLD, 14); //was 10
    public boolean StackVertically = false;
    
    public DoneBtn(int Xorigin, int Yorigin) {
        Xo = Xorigin;
        Yo = Yorigin;
    }
    
    public void SetOrigin(int Xorigin, int Yorigin) {
        Xo = Xorigin;
        Yo = Yorigin;
    }
    
    public void setAlpha(float a, Graphics2D g) {
        Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,a);
        g.setComposite(c);
    }

    public boolean mouseWithin(int X1, int Y1, int X2, int Y2) {
      if ( (mouseX >= X1) && (mouseY >= Y1) &&
           (mouseX <= X2) && (mouseY <= Y2) ) return true;
      else return false;
    }
    
    public void AddBtn(String str) {
        buttonLabel[NoOfButtons++] = str;
    }
    
    public void SelectBtn(int s) {
        buttonSelected = s;
    }
    
    public boolean ButtonAccessed() {
        boolean Answer = buttonLookedAt;
        buttonLookedAt = false;
        return Answer;
    }
    
    public int Update(Graphics2D g2, int mX, int mY, boolean Button1) {
        
        mouseX = mX;
        mouseY = mY;
        
        g2.setFont(BtnFont);
        
        //scan buttons
        for (int i = 0; i < NoOfButtons; i++) {
            
            //establish button borders
            int bx1 = Xo + borderWidth;
            int by1 = Yo + borderWidth;
            
            if (StackVertically)
                by1 += i * buttonHeighth2;
            else
                bx1 += i * buttonWidth2;
            
            int bx2 = bx1 + buttonWidth;
            int by2 = 0;
            
            //if (IsCategoryBtn) by2 = by1 + buttonHeighth2;
            //else 
            by2 = by1 + buttonHeighth;
            
          if (buttonLabel[i].length() > 0) { 
            
            //find out if mouse within button
            boolean mouseInside = mouseWithin(bx1+1, by1, bx2-1, by2);
            
            g2.setPaint(buttonColor);
            
            g2.drawRect(bx1, by1, buttonWidth+1, buttonHeighth+1);
            
            //draw label
            FontMetrics fm = g2.getFontMetrics();
            int StringWidth = fm.stringWidth(buttonLabel[i]);
            g2.drawString(buttonLabel[i], bx1 + buttonWidth/2 - StringWidth/2, by1 + LabelIndentY - 2);
            
            //mouseInsideButton = -1;
            
            if (mouseInside) {
                
                if (Button1 & !OldButton1) {
                    {
                        buttonSelected = i;
                    }
                }
            
            }
            
          }
            
        }
        
        OldButton1 = Button1;
        
        return buttonSelected;
    }
    
}
