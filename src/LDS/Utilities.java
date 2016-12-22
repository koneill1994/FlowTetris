package LDS;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Utilities {
    
    public static int SWITCHING = 0;
    public static int FOCUS = 2;
    
    Robot MouseRobot = null;
    
    Font TextFont = new Font("Helvetica", Font.BOLD, 12);

    public static BufferedImage BuffImage[] = new BufferedImage[4];
     
    boolean OldButton1;
    
    public Utilities() {
        
        LoadImages();    
        
        try {
            
            MouseRobot = new Robot();
            
        } catch (Exception e) {
            System.err.println("Robot Create Error: " + e.getMessage());
        }
    
    }
        
    public void W(String S) {
        System.out.println(S);
    }
    
    public void LoadImages() {
              
        int ImageNo = 0;
        
        for (int p = 0; p < 4; p++) {
            
            String ImageType = "SWITCHING";
            if (p > 1) ImageType = "FOCUS";
            
            String FileName = "EXPERIMENT/IMAGES/" + ImageType + "/IMAGE_" + ImageNo + ".jpg";

            ImageNo++;
            if (ImageNo == 2) ImageNo = 0;
            
            System.out.println("FILENAME="+FileName);

            ImageIcon icon0 = new ImageIcon(FileName);
            Image i0 = icon0.getImage();
            // draw the Image into a BufferedImage
            int w = i0.getWidth(null), h = i0.getHeight(null);
//                ImageWidth = w;
//                ImageHeight = h;
            //W("w="+w+" h="+h);
            BuffImage[p] = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D imageGraphics = BuffImage[p].createGraphics();
            //W("buf="+BuffImage[i]);
            imageGraphics.drawImage(i0, 0, 0, null);
            
        }
        
    }
    
    public void DrawImage(Graphics2D g2, int X, int Y, int ImageNo, int ImageType) {
        
//        W("IMAGE_NO="+ImageNo);
        
        g2.drawImage(
            BuffImage[ImageNo + ImageType],
            X - Task.IMAGE_RADIUS,
            Y - Task.IMAGE_RADIUS,
            2 * Task.IMAGE_RADIUS, 2 * Task.IMAGE_RADIUS, 
            ControlCode.Frame);
        
    }
    
    public boolean ReadNextButton(int X, int Y, Graphics2D g2, int MouseX, int MouseY, boolean Button1, boolean OLdButton1) {
        
        int R = 20;
        
        g2.setPaint(Color.GRAY);
        
        g2.fillOval(X - R, Y - R, 2 * R, 2 * R);
        
        g2.setPaint(Color.BLACK);
        
        g2.setFont(TextFont);
        
        g2.drawString("NEXT", X - 14, Y + 5);
        
        double MouseRadius = Math.sqrt(Math.pow(MouseX - X, 2) + Math.pow(MouseY - Y, 2));
        
        if ((MouseRadius <= R) & Button1 & !OldButton1)
            return true;
        
        OldButton1 = Button1;
        
        return false;
        
    }
       
    public void KeepMouseFromTarget(int MouseX, int MouseY, int XTarget, int YTarget) {
        
        int LastMouseX, LastMouseY;    
        
        if (!Parameters.DebugMode) ControlCode.Frame.setLocation(0, 0);

        int R = Task.IMAGE_RADIUS + 10;
        
        double MouseAngle = 0;
        
        double DeltaX = MouseX - XTarget;
        double DeltaY = MouseY - YTarget;
        
        if ((DeltaX != 0) | (DeltaY != 0)) 
            MouseAngle = Math.atan2(DeltaX, DeltaY);

        LastMouseX = XTarget + (int)(R * Math.sin(MouseAngle));
        LastMouseY = YTarget + (int)(R * Math.cos(MouseAngle));
    
        double Radius = Math.sqrt(Math.pow(XTarget - MouseX,2) + Math.pow(YTarget - MouseY,2));

        //W("Radius="+Radius);            
            
        //Mouse outside target circle?
        if (Radius > Task.IMAGE_RADIUS - 10) {
            W("OUTSIDE");
            
        } else {
            W("INSIDE");
            if (!Parameters.DebugMode) MouseRobot.mouseMove(LastMouseX + CalibrateMouse.XDifference, LastMouseY + CalibrateMouse.YDifference);        
        }
        
        W("LastMouseY="+LastMouseY+" LastMouseX="+LastMouseX+" MouseX="+MouseX+" MouseY="+MouseY);
    
    }
    
}
