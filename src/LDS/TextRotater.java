package LDS;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class TextRotater {
    
    int RIGHT_SIDE_UP = 0;      
    int UPSIDE_DOWN = 1;

    double DTR = 2 * Math.PI / 360.0;
        
    double RTD = 360.0 / (2 * Math.PI);
        
    public TextRotater() {
        
    }
    
    public void RotateCharacter(int Position, Graphics2D g2, String Character, Font CharFont, int X, int Y) {
    
        int CharAngle = 0;
        int Yoffset = 3;
        
        if (Position == UPSIDE_DOWN) {
            CharAngle = 180;
            Yoffset = 0;
        }
        
        g2.setFont(CharFont);  //setting font of surface
        
        FontRenderContext frc = g2.getFontRenderContext();

        TextLayout layout = new TextLayout(Character, CharFont, frc);
        
        //getting width & height of the text
        double sw = layout.getBounds().getWidth();
        double sh = layout.getBounds().getHeight();
        
        //getting original transform instance 
        AffineTransform saveTransform = g2.getTransform();
        AffineTransform affineTransform = new AffineTransform(); //creating instance
        
        //set the translation to the mid of the component
        //affineTransform.setToTranslation((rect.width)/2,(rect.height)/2);
        //affineTransform.setToTranslation((rect.width)/2,(rect.height)/2);
        //affineTransform.setToTranslation((int)PT.Xb,(int)PT.Zb);
        
        int x = X - 10 + 13;
        if (CharAngle == 180) x = X + 16 + 13;
//        int x = X;
//        if (CharAngle == 180) x = X + StringWidth;
        
//        affineTransform.setToTranslation(x, Y + 20);
        affineTransform.setToTranslation(x, Y + 20 + Yoffset);
        
        //rotate with the anchor point as the mid of the text
        //affineTransform.rotate(Math.toRadians(angdeg), 0, 0);
        
        //double A = CharAngle * RTD;
        double A = CharAngle;
        int Offset = 0;
//        if (A > 180) A -= 360;
//        if ((A >= -90) & (A < 90)) {
//        } else {
//            A += 180;
//            Offset = -(int)sw;
//        }
        
        affineTransform.rotate(A * DTR, 0, 0);
        
        g2.setTransform(affineTransform);
        
//        FontMetrics Fm = g2.getFontMetrics();
//        int Width = Fm.stringWidth(""+Character);
        
        g2.drawString(Character, Offset, (int)sh/2);
        
        g2.setTransform(saveTransform); //restoring original transform
        
    }

}
