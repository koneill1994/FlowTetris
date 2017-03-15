package LDS;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import TetrisCode.Tetris;

public class ControlCode extends JComponent
        implements MouseMotionListener, Runnable, 
         KeyListener,
         WindowListener,
         WindowFocusListener, 
         WindowStateListener {
  
  Tetris TetrisProgram = new Tetris();
    
  int IndentX = 300;
  
  public static Utilities Util = new Utilities();
    
  public static String SubjNo = "";
  public static String ExpCond = "";
  public static long StartTime;
    
  Parameters D = null;
    
  CurrentTime CT = new CurrentTime();
  
  public static int Ymax = 1200;
  
  String ParameterFileName = "";
  
  Color ForeColor = Color.CYAN;
  
  public static long FrameCounter = 0;
   
  public static Random R = new Random();
  
  FileOutputStream fout; //for writing printed reports		
  String FileName = "";
  PrintStream P = null;
  boolean FirstTime = true;
  public static boolean Running = true;
  DoneBtn StartBtn1 = new DoneBtn(270, 550);
  DoneBtn StartBtn2 = new DoneBtn(270, 750);
//  public static int ConditionNo = 0;
//  public static int ExperimentSelector = 0;
  int StartNo = -1;
  FileInputStream fstream = null;
  DataInputStream in = null;
  BufferedReader br = null;
  String Arg;
  Font TextFont = new Font("Helvetica", Font.BOLD, 14);
  
  public static JFrame Frame;
  
  int MouseX, MouseY;
  public static int MouseXin, MouseYin;
  TextField text;
  Font BigFont = new Font("Helvetica", Font.BOLD, 15);
  Graphics2D g2;
  boolean Button1;
  boolean Button2;
  boolean Button3;
  public static boolean Button1in;
  public static boolean Button2in;
  public static boolean Button3in;
//  public static int Finish = -1;
  long RandomSeed = 0;
  boolean ControlKeyPressed = false;
  boolean ShiftKeyPressed = false;
  
  public static String EntryStr[] = new String[4];
  boolean EntryUsed[] = new boolean[4];
  int EntryNo = 0;
  
  long FrameTime = 0;
  long FrameStartTime = 0;
  long LongestFrameTime = 0;
  long LongestFrameTimeCounter = 0;
  
  static int ScreenSizeX = 1300;
  static int ScreenSizeY = 700;

  public static int OverideConditionNo = 1;
    
  boolean SpaceKeyPressed;
  boolean StartExperiment;
  Font StartFont = new Font("Helvetica", Font.PLAIN, 25);
  
  public static Thread t = null;
  
  public class MyKeyListener extends KeyAdapter{
      
	public void keyPressed(KeyEvent ke) {
        	char i = ke.getKeyChar();
                System.out.println("KeyChar="+(char)i+"//");
        }
  
  }

    public void windowClosing(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
        Frame.setState(java.awt.Frame.NORMAL);
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowGainedFocus(WindowEvent e) {
     }

    public void windowLostFocus(WindowEvent e) {
    }

    public void windowStateChanged(WindowEvent e) {
    }

    void displayStateMessage(String prefix, WindowEvent e) {
    }

      
  private class MyDispatcher implements KeyEventDispatcher {
      
    //@Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        
      if (e.getID() == KeyEvent.KEY_PRESSED) {
        //java.awt.Toolkit.getDefaultToolkit()
        //    .setLockingKeyState(KeyEvent.VK_CAPS_LOCK, true);

//        KeyPressTime = CT.currentTimeMillis() - KeyPressStartTime;
        
        int key = e.getKeyCode();
        System.out.println("KeyChar="+(char)key+" INT="+(int)key);
//        Block.KeyChar = (char)key;
        
        if (key == KeyEvent.VK_ESCAPE) {
        }
        
        String New = "";
        
        if (key == KeyEvent.VK_CONTROL) ControlKeyPressed = true;
        
        if (key == KeyEvent.VK_SHIFT) ShiftKeyPressed = true;
          
        if ((key == KeyEvent.VK_Q) & ControlKeyPressed) {
              System.exit(0);
        }
                                                                      
        if (StartNo == -1) {
            
          if (key == KeyEvent.VK_DELETE) {
              
              EntryStr[EntryNo] = "";
          
          }
            
          if (key == KeyEvent.VK_BACK_SPACE) {
              
                for (int c = 0; c < EntryStr[EntryNo].length()-1; c++)
                    New += EntryStr[EntryNo].charAt(c);
                
                EntryStr[EntryNo] = New;
                
          }
          
          int MAX = 3;
              
          if (key == KeyEvent.VK_UP) {
              EntryNo--;
              //if ((EntryNo == 1) & (!EntryUsed[1])) EntryNo--;
              if (EntryNo < 0) EntryNo = MAX;
          }
                            
          if (key == KeyEvent.VK_DOWN) {
              EntryNo++;
              if (EntryNo > MAX) EntryNo = 0;
          }
          
          if ((key == KeyEvent.VK_MINUS) && ShiftKeyPressed)
              if (EntryStr[EntryNo].length() < 60) EntryStr[EntryNo] += "_";
          
          if ((key >= KeyEvent.VK_A) & (key <= KeyEvent.VK_Z))
              if (EntryStr[EntryNo].length() < 60) EntryStr[EntryNo] += "" + (char)key;
          
          if ((key >= KeyEvent.VK_NUMPAD0) & (key <= KeyEvent.VK_NUMPAD9))
              if (EntryStr[EntryNo].length() < 60) EntryStr[EntryNo] += "" + (key - KeyEvent.VK_NUMPAD0);
        
          if ((key >= KeyEvent.VK_0) & (key <= KeyEvent.VK_9))
              if (EntryStr[EntryNo].length() < 60) EntryStr[EntryNo] += "" + (key - KeyEvent.VK_0);
        
        }
        
        if (StartNo == 0) {
                      
          boolean KeyPressed = false;
          
          if (key == KeyEvent.VK_SPACE) SpaceKeyPressed = true;
          
          if ((key >= KeyEvent.VK_0) & (key <= KeyEvent.VK_9)) KeyPressed = true;
          
          //accept keypad entries
          if ((key >= KeyEvent.VK_NUMPAD0) & (key <= KeyEvent.VK_NUMPAD9)) {
              
              //translate to VK_0, etc.
              int Keys[] = { KeyEvent.VK_0, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, 
                             KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9 }; 
              
              KeyPressed = true;
              
              key = Keys[key - KeyEvent.VK_NUMPAD0];
              
          }
          
          if (KeyPressed) {
            
          }
          
        }
        
      } 
      
      if (e.getID() == KeyEvent.KEY_RELEASED) {

          int key = e.getKeyCode();
          System.out.println("KeyCharReleased="+(char)key+" INT="+(int)key);
          
          switch(key) {
            
            case KeyEvent.VK_CONTROL:
                ControlKeyPressed = false;
                break;
          
            case KeyEvent.VK_SHIFT:
                ShiftKeyPressed = false;
                break;
          
          }
               
        } else if (e.getID() == KeyEvent.KEY_TYPED) {
            //System.out.println("test");
        }
      
	return false;
    
    }
    
  }

   public ControlCode(JFrame FrameIn) throws AWTException, IOException, InterruptedException {

    EntryStr[0] = "JUNK";
    EntryStr[1] = "NONE";
    EntryStr[2] = "DEBUG_TUTORIAL";
    EntryStr[3] = "DEBUG_EXPERIMENT";
      
    ReadInStatus();
    
    RandomSeed = R.nextLong();
    
    R.setSeed(RandomSeed);
    
    Frame = FrameIn;

    Frame.addWindowListener(this);
    Frame.addWindowFocusListener(this);
    Frame.addWindowStateListener(this);


    StartBtn1.AddBtn("START");
    StartBtn1.SelectBtn(-1);
    StartBtn2.AddBtn("START");
    StartBtn2.SelectBtn(-1);
    
    KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    
    manager.addKeyEventDispatcher(new MyDispatcher());
    
    Container content = Frame.getContentPane();

        content.addKeyListener(new MyKeyListener());

        //set up keyboard text input
	text = new TextField(20);
	add(text);
	text.addKeyListener(this);
	
        addMouseMotionListener(this);
        
        addMouseListener(new MouseAdapter() {
            
		public void mousePressed(MouseEvent me) {
			if ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
                            Button1in = true;
                                
                        if ((me.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
                            Button2in = true;
                                
                        if ((me.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
                            Button3in = true;
                          
		}
                
                //mouse released
		public void mouseReleased(MouseEvent me) {
			if ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
                            Button1in = false;
                                
                        if ((me.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
                            Button2in = false;
                                
                        if ((me.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
                            Button3in = false;
                          
		}
                
	} );

    	setLayout(new BorderLayout());
       
 	setLayout(null);
            
 //       Frame.setVisible(true);

        t = new Thread(this);
        t.start();

        Frame.setVisible(true);
//        t.suspend();
        Tetris.TetrisMain();
        
  }
  
 public void keyPressed(KeyEvent evt) {
 }

 public void keyReleased(KeyEvent evt) {
        int key = evt.getKeyCode();
               System.out.println("Released KeyChar="+(char)key+"//");

 }
 
 public void keyTyped(KeyEvent evt) {
        int key = evt.getKeyCode();
               System.out.println("Typed KeyChar="+(char)key+"//");
 }

  public void mouseDragged(MouseEvent e) {     
        MouseXin = e.getX();
	MouseYin = e.getY();
  }

  public synchronized void mouseMoved(MouseEvent e) {
        //get mouse x and y values
	MouseXin = e.getX();
	MouseYin = e.getY();
  }
    
  //routine to set up runtime thread
  public void run() {
     try {
	   while (true) {
		 repaint(); //call paint component
		 Thread.sleep(1);
	   }
	}
	catch (InterruptedException ie) {}
  }
  
  public void W(String S) {
      System.out.println(S);
  }
  
  public boolean IsAnInteger(String S) {
      
      //System.out.println(S);
      int len = S.length();
      if (len == 0) return false;
      
      for (int i = 0; i < len; i++) {
          char c = S.charAt(i);
          if ((c < '0') | (c > '9')) return false;
      }
      
      return true;
      
  }
  
  public void DrawString(String S, int X, int Y, int Height) {
        
        FontMetrics Fm = g2.getFontMetrics();
        int w = Fm.stringWidth(S);
        
        g2.drawString(S, X - w/2, Y + Height/2);
        
    }
      
    public boolean CheckParameterFileExistance(int BoxNo) {
 
        String FileName = "EXPERIMENT/PARAMETERS/" + EntryStr[BoxNo] + ".txt";

        File file = new File(FileName);

        if (file.exists()) return true; //yes it exists

        return false;

    }
    
    public void RunEntryBoxes() {
        
        int w = 300;
        int h = 25;

        int x = 210 + IndentX;
        int y = 170;
        
        int spac = 100;
        
        g2.setPaint(ForeColor);
        g2.setFont(TextFont);
        
        for (int i = 0; i < 4; i++) {
            
            if (EntryUsed[i]) {
                
                if (i == EntryNo) g2.setStroke(new BasicStroke(4));
                g2.drawRect(x, y + i * spac, w, h);
            
                g2.drawString(EntryStr[i], x + 5, y + i * spac + 18);
                
                g2.setStroke(new BasicStroke(1));
            
            }
            
        }
        
        StartBtn1.Xo = 10 + IndentX;
        StartBtn2.Xo = 10 + IndentX;

        StartBtn1.Yo = y + 2 * spac + 2;
        StartBtn2.Yo = y + 3 * spac + 2;
        
    }
    
    public void ReadInStatus() {

       try {
            FileInputStream fstream = new FileInputStream("EXPERIMENT/SYSTEM/" + "SYSTEM_SETUP" + ".txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String S;
            String Cmd = "";
            
            //Read File Line By Line
            while ((S = br.readLine()) != null)   {
                
                // Print the content on the console
                System.out.println(S);
                
                Arg = S;

                Cmd = GetString(1);

                if (Cmd.equals("STOP")) {
                    break;
                }
                
                if (Cmd.equals("SCREEN_SIZE")) {
                    ScreenSizeX = GetInteger(2);
                    ScreenSizeY = GetInteger(3);
                }
               
                if (Cmd.equals("OVERIDE_EXPERIMENT_PARAMETER_FILE_1")) {
                    EntryStr[2] = GetString(2);
                    W("" + EntryStr[2]);
                }
               
                if (Cmd.equals("OVERIDE_EXPERIMENT_PARAMETER_FILE_2")) {
                    EntryStr[3] = GetString(2);
                    W("" + EntryStr[3]);
                }
               
                if (Cmd.equals("OVERIDE_DATA_FILE")) {
                    EntryStr[0] = GetString(2);
                    W("" + EntryStr[0]);
                }
               
                if (Cmd.charAt(0) == '/') continue;
                   
            }

            in.close();
            
        } catch (Exception e) { //Catch exception if any
            System.err.println("Parameter File Error: " + e.getMessage());
            System.exit(0);
        }
         
    }
    
    public void paintComponent(Graphics g) {
	
        Frame.setSize(ScreenSizeX, ScreenSizeY);
      
        g2 = (Graphics2D)g;

        MouseX = MouseXin;
        MouseY = MouseYin;
        Button1 = Button1in;
        Button2 = Button2in;
        Button3 = Button3in;
        
        g2.setPaint(Color.BLACK);
        g2.fillRect(0, 0, ScreenSizeX, ScreenSizeY);
                
        //W(" ***** FC="+FrameCounter++);
        
        if (StartNo == -1) {
            
            g2.setPaint(Color.CYAN);
            g2.setFont(TextFont);

            g2.drawString("USE ARROW KEYS TO SELECT BOXES.", IndentX + 150+60, 600);
            g2.drawString("USE DELETE KEY TO CLEAR BOX.", IndentX + 170+60, 640);
            
            RunEntryBoxes();
            
            boolean ShowStartButton1 = true;
            boolean ShowStartButton2 = true;
            
            int x = 110 + IndentX;
            int y = 170 + 20 - 2;
            int w = 400;
            int xsp = 30;
            int ysp = 100;
            
            g2.setFont(TextFont);

            EntryUsed[0] = true;
            EntryUsed[1] = true;
            EntryUsed[2] = true;
            EntryUsed[3] = true;

            g2.drawString("DATA FILE", x, y);
            g2.drawString("DATA TAG", x, y+100);
            g2.drawString("EXPER 1", x, y+200);
            g2.drawString("EXPER 2", x, y+300);
            
            y += 2;
            
            g2.setPaint(Color.RED);
                
            //exp cond
            if (EntryStr[1].length() == 0) {
                g2.drawString("REQUIRED FIELD", x + w + xsp, y + ysp);
                ShowStartButton1 = false;                
                ShowStartButton2 = false;                
            }
            
            //output file name
            if (EntryStr[0].length() == 0) {
                g2.drawString("REQUIRED FIELD", x + w + xsp, y);
                ShowStartButton1 = false;                
                ShowStartButton2 = false;                
            } 
            
            else
            
            if (EntryStr[0].equals("JUNK")) {
                g2.setPaint(Color.GREEN);
                g2.drawString("JUNK WILL BE OVERWRITTEN", x + w + xsp, y);
            } else
            
            {
                g2.setPaint(Color.GREEN);
                g2.drawString("DATA DOES NOT EXIST", x + w + xsp, y);
            }
            
            
            g2.setPaint(Color.RED);
            
            //tutorial parameter file
            if (EntryStr[2].length() == 0) {
                g2.drawString("REQUIRED FIELD", x + w + xsp, y + 2 * ysp);
                ShowStartButton1 = false;                
            } else
            if (!CheckParameterFileExistance(2)) {
                g2.drawString("PARAMETER FILE DOES NOT EXIST!", x + w + xsp, y + 2 * ysp);
                ShowStartButton1 = false;
            } else {
                g2.setPaint(Color.GREEN);
                g2.drawString("PARAMETER FILE EXISTS", x + w + xsp, y + 2 * ysp);
            }
            
            g2.setPaint(Color.RED);
            
            //experiment parameter file
            if (EntryStr[3].length() == 0) {
                g2.drawString("REQUIRED FIELD", x + w + xsp, y + 3 * ysp);
                ShowStartButton2 = false;                
            } else
            if (!CheckParameterFileExistance(3)) {
                g2.drawString("PARAMETER FILE DOES NOT EXIST!", x + w + xsp, y + 3 * ysp);
                ShowStartButton2 = false;
            } else {
                g2.setPaint(Color.GREEN);
                g2.drawString("PARAMETER FILE EXISTS", x + w + xsp, y + 3 * ysp);
            }
                            
            if ( (EntryStr[0].length() > 0) & 
                 (EntryStr[1].length() > 0) ) {
                    
                if ((EntryStr[2].length() > 0) & ShowStartButton1){
                    StartNo = StartBtn1.Update(g2, MouseX, MouseY, Button1);
                    if (StartNo == 0) ParameterFileName = EntryStr[2];
                }
                
                if ((StartNo == -1) & (EntryStr[3].length() > 0) & ShowStartButton2){
                    StartNo = StartBtn2.Update(g2, MouseX, MouseY, Button1);
                    if (StartNo == 0) ParameterFileName = EntryStr[3];
                }
                
                if (StartNo != -1) Running = true;
                
            } 
            
            DrawFrameTime();
            
            return;
            
        } else {
            
            if (FirstTime) {
            
                FirstTime = false;
            
                SubjNo = EntryStr[0];
                
                ExpCond = EntryStr[1];
                
                W("CREATING DISPLAY MENU");

                D = new Parameters(Frame, ParameterFileName);
                
            }
            
            if (!StartExperiment) {
                
                g2.setFont(StartFont);
                g2.setPaint(Color.CYAN);
                DrawString("PRESS SPACE BAR TO START", ControlCode.ScreenSizeX/2, ControlCode.ScreenSizeY/2, 0);
                if (SpaceKeyPressed) {
                    StartExperiment = true;
                    Task.MasterStartTime = CT.currentTimeMillis();
                }
                return;
      
            }
            
            if (D.Update(g2, MouseX, MouseY, Button1, Button2, Button3)) {
            }
            
        }
        
        DrawFrameTime();
        
    }
    
    public void DrawFrameTime() {
        
        g2.setFont(TextFont);
        g2.setPaint(Color.YELLOW);
        
        if (FrameCounter > 100) {
            
            long FrameTime = CT.currentTimeMillis() - FrameStartTime;
            
            if (FrameTime >= LongestFrameTime) {
                
                LongestFrameTime = FrameTime;
            
                LongestFrameTimeCounter = FrameCounter;
                
            }
            
        }
        
        FrameStartTime = CT.currentTimeMillis();
        
//        if (Parameters.DebugMode)
//            g2.drawString("COUNT : " + LongestFrameTimeCounter + "    TIME : " + LongestFrameTime,
//                20, 30);
                
        FrameCounter++;
        
  }
    
    public void DrawKeyPressTime() {
        
        g2.setFont(TextFont);
        g2.setPaint(Color.YELLOW);
        
//        if (Parameters.DebugMode) {
//            g2.drawString("KEY PRESS TIME : " + KeyPressTime, 20, 45);
//        }
        
    }
    
    public void DrawAnswerTimes() {
        
        g2.setFont(TextFont);
        g2.setPaint(Color.YELLOW);
        
//        if (Task.DebugMode) {
//            g2.drawString("FIRST ANS : " + LongestFirstAnswerTime, 20, 60);
//            g2.drawString("SECON ANS : " + LongestSecondAnswerTime, 20, 75);
//        }
        
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

    public int GetInteger(int I) {
        
        return Integer.parseInt(GetString(I));
    
    }
    
    public long GetLong(int I) {
        
        return Long.parseLong(GetString(I));
    
    }

    public static void main(String[] args) throws Exception {
        
        RepaintManager.currentManager(null).setDoubleBufferingEnabled(true);
        
        JFrame frame = new JFrame("A "+System.getProperty("user.dir"));
        
        frame.getContentPane().add(new ControlCode(frame));
        frame.setBackground(Color.WHITE);
        frame.setSize(1300, 700);
        frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultLookAndFeelDecorated(true);
        frame.requestFocus();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        
        
//        int Distance[] = new int[15];
//        
//        for (int A = 1; A <= 14; A++) {
//            for (int B = 1; B <= 14; B++) {
//                if (A == B) continue;  //skip if the same position
//                Distance[Math.abs(B-A)]++;
//            }
//        }
//        
//        //print the distances
//        System.out.println("Distance   Possibilities");
//        
//        //divide by 2 because
//        //  AxxxxxxxxxxxxB  is the same as  BxxxxxxxxxxxxA
//        
//        for (int D = 1; D <= 14; D++)
//            System.out.println("     "+D+"               "+Distance[D]/2);
//     
//        System.exit(0);
        
    }
 
}
