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

public class Btn {
    
    public static Color ExperimentColor = Color.BLACK;
    static Color BtnColor[] = { new Color(255, 255, 0), new Color(50,205,50), new Color(123,104,238) };
       
    String ExperType[] = {
        "TEAM BASED LEARNING",
        "INTERACTIVE LECTURE",
        "LECTURE"
    };
    boolean OldButton1;
    int Answer = 0;
    boolean EarlyReturn = false;
    boolean Border = false;
    boolean Square = true;
    int Xo;
    int Yo;
    String buttonLabel[] = new String[50];
    int NoOfButtons = 0;
    int buttonSelected = -1;
    int mouseX;
    int mouseY;
    Color buttonColor;
    Color textColor;
    int mouseInsideButton;
    boolean buttonLookedAt = false;
    boolean HandCursorOn = false;
    
    //constants
    int buttonWidth = 481+50;
    int buttonWidth2 = 200;
    int buttonHeighth = 66;
    int buttonHeighth2 = 100;
    int borderWidth = 0;
    int LabelIndentY = 46;
    Color borderColor = new Color(0, 0, 0);
    Color buttonLinerColor = new Color(255, 255, 255);
    Font BtnFont = new Font("Helvetica", Font.BOLD,30); //was 10
    public boolean StackVertically = true;
    
    boolean AllowBtn[] = new boolean[6];
  
    public Btn(int Xorigin, int Yorigin) {
        for (int i = 0; i < 6; i++)
            AllowBtn[i] = true;
            
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
        EarlyReturn = false;
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
        g2.setPaint(borderColor);
        setAlpha(1.0f, g2);
        if (Border) {
          
            if (StackVertically)
                g2.fillRect(Xo, Yo, 
                    //2*borderWidth  + buttonWidth, 2*borderWidth + NoOfButtons*buttonHeighth2-15);
                    buttonWidth, 2*borderWidth + NoOfButtons*buttonHeighth2-15);
            else
                g2.fillRect(Xo, Yo, 
                    2*borderWidth + NoOfButtons*buttonWidth, 2*borderWidth + buttonHeighth);
        
        }
        
        //scan buttons
        mouseInsideButton = -1;
        
        HandCursorOn = false;
        
        for (int i = 0; i < NoOfButtons; i++) {
            
            //establish button borders
            int bx1 = Xo + borderWidth;
            int by1 = Yo + borderWidth;
            
            if (!AllowBtn[i]) continue;
            
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
            if (mouseInside) {
                buttonLookedAt = true;
                HandCursorOn = true;
            }
            
            //color button
            if (i == buttonSelected) {
                ExperimentColor = BtnColor[i];
                g2.setStroke(new BasicStroke(5));
            }
            //else g2.setPaint(Color.LIGHT_GRAY);
            
            g2.setPaint(BtnColor[i]);
            
            g2.drawRect(bx1, by1, buttonWidth+1, buttonHeighth+1);
            g2.setStroke(new BasicStroke(1));
       
            //draw label
//            FontMetrics fm = g2.getFontMetrics();
//            int StringWidth = fm.stringWidth(buttonLabel[i]);
            g2.drawString(buttonLabel[i], bx1 + 30, by1 + LabelIndentY - 2);
            g2.drawString(ExperType[i], bx1 + 150, by1 + LabelIndentY - 2);
            
            //mouseInsideButton = -1;
            
            if (mouseInside) {
                
                mouseInsideButton = i;
                
                if (Button1 & !OldButton1) {
                    //if ((mouseInsideButton == buttonSelected) & IsCategoryBtn) {
                    //    EarlyReturn = true;
                    //    buttonSelected = -1;
                    //}
                    //else 
                    {
                        EarlyReturn = false;
                        buttonSelected = i;
                    }
                }
            
                //if (EarlyReturn) mouseInsideButton = i;

            } //else if (EarlyReturn) buttonSelected = -1;
            
          }
            
        }
        
        OldButton1 = Button1;
        
        if (EarlyReturn && (mouseInsideButton != -1)) {
            return mouseInsideButton;
        }
        
        return buttonSelected;
    }
    
}
