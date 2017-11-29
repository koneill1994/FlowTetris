package LDS;

import java.awt.Graphics2D;
import java.awt.Robot;

public class CalibrateMouse {
    
    int Xo = ControlCode.ScreenSizeX/4;
    int Yo = ControlCode.ScreenSizeY/2;
    
    CurrentTime CT = new CurrentTime();
    
    Graphics2D g2;
    boolean Button1 = false;
    boolean Button3 = false;
    boolean OldButton1;
    boolean OldButton3; 
    int MouseX, MouseY;
  
    Robot MouseRobot = null;
    Task B = null;
    
    boolean FirstTime, SecondTime;
    
    static int MOUSE_START = 0;
    static int YDIFFERENCE_COMPUTED = 1;
    static int MOUSE_CALIBRATED = 2;
    static int CalibrateMouseMode = MOUSE_START;
    static int YDifference = 0;
    static int XDifference = 0;

    public CalibrateMouse() {
                
        try {
            
            MouseRobot = new Robot();
            
        } catch (Exception e) {
            System.err.println("Robot Create Error: " + e.getMessage());
            System.exit(1);
        }

    }
    
    public void W(String S) {
        System.out.println();
    }
    
    public void CenterMouse() {
        
        ControlCode.Frame.setLocation(0, 0);
    
        MouseRobot.mouseMove(Xo + XDifference, Yo + YDifference);
        
        W("MouseY="+MouseY);
    
    }
    
    public boolean Update(Graphics2D g2in, int mXin, int mYin, boolean Button1In, boolean Button3In,
            Task Bin) {
        
        MouseX = mXin;
        MouseY = mYin;
        g2 = g2in;
        B = Bin;

        Button1 = Button1In;
        Button3 = Button3In;

        if (Parameters.DebugMode) {
            Xo = ControlCode.ScreenSizeX/4;
        }
        
        if (FirstTime) {
            CenterMouse();
            FirstTime = false;
            SecondTime = true;
            return false;
        }

        if (SecondTime) {
            SecondTime = false;
            return false;
        }

        if (CalibrateMouseMode == MOUSE_START) {
            YDifference = Yo - MouseY;
            XDifference = Xo - MouseX;
            CalibrateMouseMode = YDIFFERENCE_COMPUTED;
            FirstTime = true;
            return false;
        }

        if (CalibrateMouseMode == YDIFFERENCE_COMPUTED) {
            
            if ((MouseY - Yo) != 0) {
                XDifference = 0;
                YDifference = 0;
                CalibrateMouseMode = MOUSE_START;
                FirstTime = true;
            } else {
                CalibrateMouseMode = MOUSE_CALIBRATED;
                Task.MasterStartTime = CT.currentTimeMillis();
                return true;
            }

        }

        return false;
        
    }
    
}
