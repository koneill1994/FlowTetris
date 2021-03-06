package TetrisCode;

/*
Modification of Tetris Applet 
by Melinda Green
based on Guido Pellegrini's Summer 2000 term project
Modified by Randy Green and Kevin O'Neill
Under supervision by Ion Juvina
Wright State University 2016-2017

Use this code for anything you like.
If you use it in a mission critical application and 
a bug in this code causes a global nuclear war, I will
take full responsibility and will fix the bug for free.
*/

import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.util.Random;
import java.net.URL;
import java.net.MalformedURLException;
import LDS.ControlCode;
import java.util.*;  // queue


public class Tetris extends Applet{
       
    
    private void W(String text){
        System.out.println(text);
    }
	
	//
	// STATIC MEMBERS
	//
	
	int totalBlocks;
        int totalTrappedSpaces;
        int accumulationHeight;
        public static int SessionNo = 1;
        Data D = new Data();
        FineData F = new FineData();
        char Tab = ',';
        public static boolean isAdaptive = true; // this is the variable we're going to tie all the experimental/control condition stuff to
                        
        //logging
        static int queue_history =10; // how many items back the queue remembers
        Queue<Integer> HeightQueue = new LinkedList<Integer>();
        Queue<Integer> RowRemovalQueue = new LinkedList<Integer>();
        int SizeLastRowRemoved = -1;
        long DropStartTime=0;
        Queue<Integer> DropDurationQueue = new LinkedList<Integer>();
        long LongMin = Long.MIN_VALUE;
        long DownStartTime = LongMin;
        long DownEndTime = LongMin;

        long LastButtonPressTime =0;
        long LastButtonUnpressTime = 0;
        Double UnpressPercent=0.0;
        
        static String Adaptive_Delay_Mode=null;
                
        static Double AdaptiveFallSpeedLowerBound = 0.5;
        static Double AdaptiveFallSpeedUpperBound = 0.5;
        
        static long PersistentDelay=1000; //start at 1 second initially
                
        private class Tuple<X,Y>{
            public final X x;
            public final Y y;
            public Tuple(X x, Y y){
                this.x=x;
                this.y=y;
            }
        }
        
        LinkedList<Tuple<Long,Long>> DownQueue = new LinkedList<Tuple<Long,Long>>();
        LinkedList<Tuple<Long,Long>> KeyUpQueue = new LinkedList<Tuple<Long,Long>>();
        // tuple (start_time, end_time)
        static long DropPercentageTimeWindow = 10*1000; // in ms
        static long KeyUpTimeWindow = 10*1000; // in ms
        
        public static String SwitchCondition_Measure;
        public static Double SwitchCondition_Value = 0D;
        public static char   SwitchCondition_Comparison = 'a';        
        
        boolean OutputTheDataWhenFirstFast;
        public static Frame frame;
        private final static int INITIAL_DELAY = 1000;
	public static byte ROWS = 38; //18
	public static byte COLUMNS = 10;
        
        static String Param_Name = "PILOT_TEST_PARAMETERS";
        
        //THIS IS CALLED BEFORE CONTROL CODE PARAMETERS CHANGE WHAT IT SHOULD BE
	static Parameters P = new Parameters(Param_Name);  // creating this will set the values to that in paramters (i think) so set defaults before this)
        TimeInLevelData TILData = new TimeInLevelData(); //uses parameters, so make sure it comes afterwards
        public ArrayList<ArrayList<Double>> TimeInLevelList;

        long tmp_start_time=0;
        long tmp_unpress_time=0;
        
        FlowControlSystem FCS = new FlowControlSystem();
        Sound snd = new Sound();
        
        boolean SoundKeyPressed[] = {false,false,false,false}; // keycodes 37, 38, 39, and 40, respectively
        
        private final static int EMPTY = -1;
	//private final static int DELETED_ROWS_PER_LEVEL = 5;
	private final static Color PIECE_COLORS[] = {
		new Color(0xFF00FF), // fucia
		new Color(0xDC143C), // crimson
		new Color(0x00CED1), // dark turquoise
		new Color(0xFFD700), // gold
		new Color(0x32CD32), // lime green
		new Color(0x008080), // teal
		new Color(0xFFA500), // orange
	};
	private final static Color BACKGROUND_COLORS[] = {
		new Color(0xFFDAB9), // peachpuff
		new Color(0xFFC0CB), // pink
		new Color(0xFF99CC), // hot pink
		new Color(0x0099CC), // sky blue
		new Color(0x9966CC), // lavender
	};
	private final static Color BACKGROUND_COLOR = new Color(0x99FFCC);

	//   *    **   *    *     *   *
	//   *    *    *    **   **   **   **
	//   *    *    **    *   *    *    **
	//   *
	//   0    1    2    3    4    5    6   	
	private final static boolean PIECE_BITS[][][] = {
		{
			{false, true, false, false},
			{false, true, false, false},
			{false, true, false, false},
			{false, true, false, false},
		},
		{
			{false, false, false, false},
			{false, true, true, false},
			{false, true, false, false},
			{false, true, false, false},
		},
		{
			{false, false, false, false},
			{false, true, false, false},
			{false, true, false, false},
			{false, true, true, false},
		},
		{
			{false, false, false, false},
			{false, true, false, false},
			{false, true, true, false},
			{false, false, true, false},
		},
		{
			{false, false, false, false},
			{false, false, true, false},
			{false, true, true, false},
			{false, true, false, false},
		},
		{
			{false, false, false, false},
			{false, true, false, false},
			{false, true, true, false},
			{false, true, false, false},
		},
		{
			{false, false, false, false},
			{false, false, false, false},
			{false, true, true, false},
			{false, true, true, false},
		},
	};
	private static boolean tmp_grid[][] = new boolean[4][4]; // scratch space
	private static Random random = new Random();
	
	private static class TetrisLabel extends Label {
		private final static Font LABEL_FONT = new Font("Serif", Font.BOLD, 18);
		private TetrisLabel(String text) {
			super(text);
			setFont(LABEL_FONT);
		}
		private void addValue(int val) {
			setText(Integer.toString((Integer.parseInt(getText())) + val ));
		}
	}
	
	//
	// INSTANCE DATA
	//
		
        public static long StartTimeInLevel;            
        double TimeInLevel;
	int SecondsInCurrentLevel;
        static int score;
        int KeyCounter[] = new int[4];
        public static long StartTime = System.nanoTime();
        long TotalRunTime;
        static long speed = 0;
        long old_speed = 0;
        boolean GameStarted;
        //private int count;
        private final static Font LABEL_FONT = new Font("Serif", Font.BOLD, 18);
	private int grid[][] = new int[ROWS][COLUMNS];
	private int next_piece_grid[][] = new int[4][4];
	private int num_rows_deleted = 0;
	private GridCanvas game_grid = new GridCanvas(grid, true);
	private GridCanvas next_piece_canvas = new GridCanvas(next_piece_grid, false);
	public static Timer timer;
	private TetrisPiece cur_piece;
	private TetrisPiece next_piece = randomPiece();
	private TetrisSound sounds;// = new TetrisSound(this);
	private TetrisLabel blank_label = new TetrisLabel("");
	private TetrisLabel rows_deleted_label = new TetrisLabel("0");
	private TetrisLabel level_label = new TetrisLabel("1");
	private TetrisLabel level_duration_label = new TetrisLabel("0");
	private TetrisLabel speed_label = new TetrisLabel("0");
	private TetrisLabel score_label = new TetrisLabel("0");
	private TetrisLabel high_score_label = new TetrisLabel("");
	final Button start_newgame_butt = new TetrisButton("Start");
	//public static final Button pause_resume_butt = new TetrisButton("Pause");									
	final Button pause_resume_butt = new TetrisButton("Pause");									
	public static String Subject_ID;
        boolean ControlKeyPressed = false;
        
        static boolean MinusKeyPressed = false;
        
        boolean FineDatLogHeader_Made = false;
        
        public void SetSubjectID(String SubjNo){
            Subject_ID = SubjNo;
        }
        
        static public void NameParameters(String Param){
            Param_Name = Param;
            P = new Parameters(Param_Name);
        }
        
        private void LogEvent(String event){
            
            if(!FineDatLogHeader_Made){
                F.CreateLogHeader();
                FineDatLogHeader_Made=true;
            }

            Double drop_percent =  DropPercentSanitized(DownQueue, DropPercentageTimeWindow, (System.nanoTime()-StartTime)/1000000);

            F.LogEvent("" + Subject_ID + Tab + isAdaptive + Tab
                    + CurrentTime() + Tab + event+Tab
                +SizeLastRowRemoved+Tab+computeVariance(RowRemovalQueue,queue_history)+Tab
                +speed+Tab+accumulationHeight+Tab+computeVariance(HeightQueue,queue_history)
                +Tab+DropDurationQueue.peek()+Tab+ computeVariance(DropDurationQueue,queue_history)
                +Tab+drop_percent+Tab+ControlCode.SubjNo);

        }
        
 
        
        //returns current time from start in ms
        private long CurrentTime(){
            return (TotalRunTime + System.nanoTime() - StartTime)/1000000;
        }
        
        public ArrayList<ArrayList<Double>> CreateTimeInLevelList(int levelmax){
            ArrayList<ArrayList<Double>> TILList = new ArrayList<ArrayList<Double>>();
            for(int i=0; i<=levelmax; i++){
                System.out.println("Adding Level "+i+" of "+levelmax +" to TILList");
                TILList.add(new ArrayList<Double>());
            }
            return TILList;
        }
        
	//
	// INNER CLASSES
	//
	
	private class TetrisButton extends Button {
		public TetrisButton(String label) {
			super(label);
		}
		public Dimension getPreferredSize() {
			return new Dimension(120, super.getPreferredSize().height);
		}
	}
			
	private class TetrisPiece {
		private boolean squares[][];
		private int type;
		private Point position = new Point(3, -4); // -4 to start above top row
		public int getX() { return position.x; }
		public int getY() { return position.y; }
		public void setX(int newx) { position.x = newx; }
		public void setY(int newy) { position.y = newy; }
		public void setPosition(int newx, int newy) { setX(newx); setY(newy); }
		
		public TetrisPiece(int type) {
                        totalBlocks = countBlocks();
                        totalTrappedSpaces = countTrappedSpaces();
                        accumulationHeight = computeAccumulationHeight();
			this.type = type;
			this.squares = new boolean[4][4];
			for(int i=0; i<4; i++)
				for(int j=0; j<4; j++)
					this.squares[i][j] = PIECE_BITS[type][i][j];


		}
		
		public boolean canStepDown() {
			synchronized(timer) {
				cut();
				position.y++;
				boolean OK = canPaste();
				position.y--;
				paste();
				return OK;
			}
		}
		
		public boolean canPaste() {
			for(int i=0; i<4; i++) {
				for(int j=0; j<4; j++) {
					int to_x = j + position.x;
					int to_y = i + position.y;
					if(squares[i][j]) { // piece contains this square?
						if(0 > to_x || to_x >= COLUMNS // square too far left or right?
							|| to_y >= ROWS) // square off bottom?
						{
							return false;
							// note: it's always considered OK to paste a square
							// *above* the grid though attempting to do so does nothing.
							// This allows the user to move a piece before it drops
							// completely into view.
						}
						if(to_y >= 0 && grid[to_y][to_x] != EMPTY)
							return false;
					}
				}
			}
			return true;
		}
		
		public void stepDown() {
			position.y++;
		}
		
		public void cut() {
			for(int i=0; i<4; i++)
				for(int j=0; j<4; j++)
					if(squares[i][j] && position.y+i>=0)
						grid[position.y + i][position.x + j] = EMPTY;
		}
		
		/**
		 * Paste the color info of this piece into the given grid
		 */
		public void paste(int into[][]) {
			for(int i=0; i<4; i++)
				for(int j=0; j<4; j++)
					if(squares[i][j] && position.y+i>=0)
						into[position.y + i][position.x + j] = type;
		}
		
		/**
		 * No argument version assumes pasting into main game grid
		 */
		public void paste() {
			paste(grid);
		}
		
		public void rotate() {
			// copy the piece's data into a temp array
			for(int i=0; i<4; i++)
				for(int j=0; j<4; j++)
					tmp_grid[i][j] = squares[i][j];
			// copy back rotated 90 degrees
			for(int i=0; i<4; i++)
				for(int j=0; j<4; j++)
					squares[j][i] = tmp_grid[i][3-j];
                        
                        //log rotation
                        LogEvent("rotate");
		}
		public void rotateBack() {
			// copy originally saved version back
			// this of course assumes this call was preceeded by
			// a call to rotate() for the same piece
			for(int i=0; i<4; i++)
				for(int j=0; j<4; j++)
					squares[i][j] = tmp_grid[i][j];
		}
		
		// this method is a bit of a hack to check for the case
		// where a piece may be safely on the grid but have one or more
		// rows of empty squares that are above the grid and therefore OK
		public boolean isTotallyOnGrid() {
			for(int i=0; i<4; i++) {
				if(position.y + i >= 0)
					return true; //everything from here down is on grid
				// this row is above grid so look for non-empty squares
				for(int j=0; j<4; j++)
					if(squares[i][j])
						return false;
			}
			System.err.println("TetrisPiece.isTotallyOnGrid internal error");
			return false;
		}
	} // end class TetrisPiece
	

	public class Timer extends Thread {
		private long m_delay;
		private boolean m_paused = true;
		private boolean m_fast = false;
		private ActionListener m_cb;
		public Timer(long delay, ActionListener cb) { 
			setDelay(delay);
			m_cb = cb;
		}
		public void setPaused(boolean pause) { 
			m_paused = pause;
			if(m_paused) {
				//sounds.stopSoundtrack(); // sounds is left null (until I can fix it), so this throws errors
			}
			else {
				//sounds.playSoundtrack();
				synchronized(this) {
					this.notify();
				}
			}
		}
		public boolean isPaused() { return m_paused; }
		public void setDelay(long delay) { m_delay = delay; }
		public boolean isRunning() { return !m_paused; }
		public void setFast(boolean fast) {
			m_fast = fast;
			if(m_fast) {
				try {
					this.checkAccess();
					this.interrupt(); // no exception, so OK to interrupt
				} catch(SecurityException se) {
                                System.exit(11);
				}
			}
		}
		public boolean isFast() { return m_fast; }
                //RFG1 removed
//		public void faster() {
//			m_delay = (int)(m_delay * .9); //increase the speed exponentially in reverse
//		}
		public void run() {
			while(true) {

                                //char Tab = ',';
                                //timer.setFast(false);  // reset drop speed from last tick
                                                                
                                if ((!m_fast & !m_paused) | OutputTheDataWhenFirstFast) D.OutputData(""+((TotalRunTime + System.nanoTime() - StartTime)/1000000) + Tab
                                               + SessionNo + Tab + totalBlocks + Tab + totalTrappedSpaces + Tab +
                                               + score + Tab + m_delay + Tab + speed + Tab 
                                               + KeyCounter[0] + Tab + KeyCounter[1] + Tab + KeyCounter[2] + Tab + KeyCounter[3] + Tab
                                               + System.getProperty("user.dir"));
                                
                                OutputTheDataWhenFirstFast = false;
                                
                                //update accum variance queue
                                //HeightQueue=addToQueue(HeightQueue, computeAccumulationHeight(), queue_history);
        
                                //NB really this should only run when a piece is placed
                                // it counts the falling block as part of the accumulation
                                
                                
                                
                                //clear key counters
                                for (int i = 0; i < 4; i++)
                                    KeyCounter[i] = 0;
                            
                                try { 
					sleep(m_fast ? Math.min(30,m_delay) : m_delay);  
				} catch (Exception e) {}
				if(m_paused) {
					try {
						synchronized(this) {
							this.wait();
						}
					} catch(InterruptedException ie) {}
				}
				synchronized(this) {
					m_cb.actionPerformed(null);
				}
			}
		}
	} // end class Timer

	private class GridCanvas extends DoubleBufferedCanvas {
		private int grid[][];
		private boolean paint_background;
		public GridCanvas(int[][] grid, boolean do_background) {
			this.grid = grid;
			paint_background = do_background;
			clear();
		}
	
		private void clear() {
			for(int i=0; i<grid.length; i++)
				for(int j=0; j<grid[0].length; j++)
					grid[i][j] = EMPTY;
		}		
		public Dimension getPreferredSize() {
			return new Dimension(grid[0].length * 30, grid.length * 30);
		}
		public void paint(Graphics g) {
			g = this.startPaint(g); // returned g paints into offscreen image
                     
			int width = this.getSize().width;
			int height = this.getSize().height;
			g.clearRect(0, 0, width, height);
                        int cell_size, xstart, ystart;
			double panel_aspect_ratio = (double)width/height;
			double grid_aspect_ratio = (double)grid[0].length/grid.length;
			if(panel_aspect_ratio > grid_aspect_ratio) { 
				// extra space on sides
				cell_size = (int)((double)height/grid.length + 0.5);
				xstart = (int)(width/2 - (grid[0].length/2.0 * cell_size + 0.5));
				ystart = 0;
			}
			else { 
				// extra vertical space
				cell_size = (int)((double)width/grid[0].length + 0.5);
				xstart = 0;
				ystart = (int)(height/2 - (grid.length/2.0 * cell_size + 0.5));
			}
			if(paint_background) {
//				g.setColor(BACKGROUND_COLORS[(num_rows_deleted / DELETED_ROWS_PER_LEVEL) % BACKGROUND_COLORS.length]);
				g.setColor(BACKGROUND_COLORS[0]);
				g.fillRect(xstart, ystart, COLUMNS*cell_size, ROWS*cell_size);
			}
			for(int i=0; i<grid.length; i++) {
				for(int j=0; j<grid[0].length; j++) {
					if(grid[i][j] != EMPTY) {
						g.setColor(PIECE_COLORS[grid[i][j]]);
						int x = xstart + j*cell_size;
						int y = ystart + i*cell_size;
						g.fill3DRect(x, y, cell_size, cell_size, true);
					}
				}
			}
                        g.setColor(Color.BLACK);
                        g.setFont(LABEL_FONT);
			long RunTime = TotalRunTime + System.nanoTime() - StartTime;
                        if (GameStarted) { 
                            //only end program if LDS is not used
                            if ((Parameters.MaxSecondsInLevel == 0) & (RunTime / 1000000000) >= (Parameters.TimeLimitInSeconds)) {
                                System.out.println("MaxSecondsInLevel: "+Parameters.MaxSecondsInLevel);
                                System.out.println("TimeLimit="+(Parameters.TimeLimitInSeconds)); // * 1000000000));
                                System.out.println("RunTime="+RunTime);
                                SwitchToFocusTask();
                                //System.exit(121);
                            }
                        }
                        frame.setSize(489, 841);
                        this.endPaint(); // paints accumulated image in one shot
		}
	} // end class GridCanvas
	
	private class TetrisSound {
		private AudioClip soundTrack = null;
		private AudioClip destroyRowSounds[] = new AudioClip[4];
		private AudioClip gameOverSound = null;
		private AudioClip getClip(String name) throws MalformedURLException {
			System.out.println("INSIDE GETCLIP for " + name);
                        URL soundFileUrl = new URL("file://./src/TetrisCode/"+name);
                        //URL soundFileUrl = new URL(getCodeBase(), "file:///C:/Users/rfgreen/Desktop/"+name);
			System.out.println("INSIDE GETCLIP 2");
                        try {
				AudioClip clip = getAudioClip(soundFileUrl);
                                System.out.println("FOUND AUDIOCLIP URL, "+soundFileUrl.getPath());
				return clip;
			} catch(NullPointerException npe) { 
                            System.out.println("CAN'T FIND URL");
				System.err.println("exception " + npe); 
                                System.exit(10);
				return null; 
			}
//                        System.out.println("INSIDE GETCLIP 3");
                        
		}
		public TetrisSound() {
			//load sound files
			try {
                            System.out.println("LOADING URLS");
				soundTrack          = getClip("theme.au");
                            System.out.println("LOADING URLS 2");
				destroyRowSounds[0] = getClip("file://C:/Users/rfgreen/Desktop/quiteImpressive.au");
				destroyRowSounds[1] = getClip("file://C:/Users/rfgreen/Desktop/smashing.au");
				destroyRowSounds[2] = getClip("file://C:/Users/rfgreen/Desktop/yeahbaby.au");
				destroyRowSounds[3] = getClip("file://C:/Users/rfgreen/Desktop/great.au");
				gameOverSound       = getClip("file://C:/Users/rfgreen/Desktop/groovy.au");
			}
			catch (Exception e) {
				System.err.println("MESSAGE="+e.getMessage());
                                e.printStackTrace();
			}
		}
		public void playSoundtrack() {
			if(soundTrack == null) {
                            System.out.println("SOUNDTRACK");
                            return;
                        }
			soundTrack.loop();
		}
		public void playDestroyRows(int rows) {
			int soundid = rows - 1;
			if(0 > soundid || soundid >= destroyRowSounds.length || destroyRowSounds[soundid] == null)
				return;
			destroyRowSounds[soundid].play();
		}
		public void playGameOverSound() {
			if(gameOverSound == null)
				return;
			gameOverSound.play();
		}
		public void stopSoundtrack() {
			if(soundTrack == null)
				return;
			soundTrack.stop();
		}
	} // end class TetrisSound
	
	
	//
	// INSTANCE METHODS
	//
	
	private TetrisPiece randomPiece() {
		int rand = Math.abs(random.nextInt());
		return new TetrisPiece(rand % (PIECE_COLORS.length));
	}
	
	private void installNewPiece() {
		next_piece_canvas.clear();
		cur_piece = next_piece;
                if(isAdaptive) RemoveRowsIfStackedTooHigh();
		cur_piece.setPosition(3, -4); //-4 to start above top of grid
		if (timer != null) ComputeScoreAndDelay(-4 * Parameters.PointsSubtractedPerNewBlock);
                if(cur_piece.canPaste()) {
                        //update accum variance queue before next piece appears, to capture only accum
                        HeightQueue=addToQueue(HeightQueue, computeAccumulationHeight(), queue_history);
        
			next_piece = randomPiece();
			next_piece.setPosition(0, 0);
			next_piece.paste(next_piece_grid);
			next_piece_canvas.repaint();
                        LogEvent("spawn_new_piece");
		}
		else
			gameOver();
	}
	
	private void gameOver() {
		System.out.println("Game Over!");
		timer.setPaused(true);
		pause_resume_butt.setEnabled(false);
//		int score = Integer.parseInt(score_label.getText());
		// sounds.playGameOverSound();
                LogEvent("game_over");
                // add a way here to show the player that they got a game over
                startGame();
	}
	
	private boolean rowIsFull(int row) {
		for(int i=0; i<COLUMNS; i++)
			if(grid[row][i] == EMPTY)
				return false;
		return true;
	}
	
	private int countFullRows() {
		int n_full_rows = 0;
		for(int i=0; i<ROWS; i++)
			if(rowIsFull(i))
				n_full_rows++;
		return n_full_rows;
	}	
	
	private void removeRow(int row) {
		for(int j=0; j<COLUMNS; j++)
			grid[row][j] = EMPTY;
		for(int i=row; i>0; i--) {
			for(int j=0; j<COLUMNS; j++) {
				grid[i][j] = grid[i-1][j];
			}
		}
	}
        
        // a queue of values which will be used to compute variance 
        // of those values
        // Make sure they're evenly distributed in time
        // queue to you can add to front and take off from end
        private double computeVariance(Queue<Integer> q, int maxLength){   
            if(q.size() < maxLength-1)   return Double.NaN;  //we dont have a full queue yet, so dont output a valid variance
            Queue<Integer> q_tmp=new LinkedList(q);
            int sum = 0;
            int n=q.size();
            while(q_tmp.size()>0){
                sum = sum + q_tmp.remove();
            }
            double mean= sum/n;
            double vsum = 0;
            q_tmp=new LinkedList(q); // NB you have to do this to copy the queue and not just make a reference to it
            while(q_tmp.size()>0){
                int k = q_tmp.remove();
                vsum = vsum + Math.pow(k-mean,2);
            }
            return vsum/(n-1);
        }
        
        //  work on this later
	private Queue<Integer> addToQueue(Queue<Integer> q, int item, int maxLength){
            q.add(item);
            while(q.size() > maxLength){
                q.remove();  //make sure the queue size isn't larger than its supposed to be
            }
            //System.out.println("queue_after_added " + q);
            return q;
        }       
        
	private int countTrappedSpaces() {
            
                int spaceCount = 0;
            
		for(int j=0; j<COLUMNS; j++) {
                    
                    int tempSpaceCount = 0;	
		    
                    for(int i=ROWS-1; i>=0; i--) {
		
                        if (grid[i][j] == EMPTY) tempSpaceCount++;
                        else {
                            spaceCount += tempSpaceCount;
                            tempSpaceCount = 0;
                        }
                                                
                    }
                
                }
                
                return spaceCount;
                
	}
	
        LinkedList<Tuple<Long,Long>> removeOldFromQueue(LinkedList<Tuple<Long,Long>> q, long current_time, long time_window){
            LinkedList<Tuple<Long,Long>> output = new LinkedList<Tuple<Long,Long>>();
            for(Tuple<Long,Long> e: q){
                // if the drop ends after the time window starts
                // add it to the output
                if(e.y>=(current_time-time_window)){
                    output.add(new Tuple<Long,Long>(e.x,e.y));
                }
            }
//            //DisplayDropPercentList(output, time_window);
            return output;
        }
        
        //only for calculation purposes, don't use this on the actual queue. just on a copy
        LinkedList<Tuple<Long,Long>> ContainWithinTimeWindow(LinkedList<Tuple<Long,Long>> q, long current_time, long time_window){
            LinkedList<Tuple<Long,Long>> output = new LinkedList<Tuple<Long,Long>>();
            for(Tuple<Long,Long> e: q){
                //if it starts after the start of the window:
                if(e.x>current_time-time_window){
                    if(e.y==LongMin || e.y==0){
                        output.add(new Tuple<Long,Long>(e.x,current_time));
                    }
                    // if it ends before the current time
                    else{
                        output.add(new Tuple<Long,Long>(e.x,e.y));
                    }
                }
                //if it starts before the window but ends within it
                else if(e.y>current_time-time_window){
                    output.add(new Tuple<Long,Long>(current_time-time_window,e.y));
                }
            }
            //DisplayDropPercentList(q, time_window,"q");
            //DisplayDropPercentList(output, time_window,"output");
            return output;
        }
        
        float DropPercentageCalculate(LinkedList<Tuple<Long,Long>> q, long time_window){
            float sum=0;
            for(Tuple<Long,Long> e: q){
                sum+=(e.y-e.x); //duration of a drop event                
            }
            //System.out.println(sum);
            //System.out.println(time_window);
            return sum/time_window; //divide by time span to get drop percentage (between 0 and 1)
        }
        
        float UnPressPercentageCalculate(LinkedList<Tuple<Long,Long>> q, long time_window){
            float sum=0;
            for(Tuple<Long,Long> e: q){
                sum+=(e.y-e.x); //duration of a drop event                
            }
            //DisplayDropPercentList(q,time_window,"calc");
            //System.out.println("Unpress sum: " +sum);
            //System.out.println("Unpress time_window: "+time_window);
            //System.out.println("Unpress queuelength: "+LengthOfQueue(q));
            return sum/time_window; //divide by time span to get drop percentage (between 0 and 1)
        }
        
        Long LengthOfQueue(LinkedList<Tuple<Long,Long>> q){
            return q.get(q.size()-1).y-q.get(0).x;
        }
        
        // helper function to sanitize the q and return the percent
        Double DropPercentSanitized(LinkedList<Tuple<Long,Long>> q, long time_window, long current_time){
            
            // if we haven't had time_window's worth of gameplay yet
            // output null
            if((current_time-time_window)<=0){
                return null;
            }
            
            LinkedList<Tuple<Long,Long>> q_new = ContainWithinTimeWindow(q,current_time,time_window);
            //W("Q");
            //DisplayDropPercentList(q, time_window);
            //W("Q_NEW");
            //DisplayDropPercentList(q_new, time_window);
            
            return (double) DropPercentageCalculate(q_new, time_window);
        }
        
       // different function just to make debugging easier
        Double UnpressPercentSanitized(LinkedList<Tuple<Long,Long>> q, long time_window, long current_time){
            
            // if we haven't had time_window's worth of gameplay yet
            // output null
            if((current_time-time_window)<=0){
                return null;
            }
            
            LinkedList<Tuple<Long,Long>> q_new = ContainWithinTimeWindow(q,current_time,time_window);
            //DisplayDropPercentList(q_new, time_window,"f");
            //W("Q");
            //DisplayDropPercentList(q, time_window);
            //W("Q_NEW");
            //DisplayDropPercentList(q_new, time_window);
            
            return (double) UnPressPercentageCalculate(q_new, time_window);
        }
        
        void DisplayDropPercentList(LinkedList<Tuple<Long,Long>> q, long time_window){

            System.out.println("\nQueue List, size "+q.size());
            for(Tuple<Long,Long> e: q){
                System.out.println("("+e.x +"," +e.y+")");
            }
            if(q.size()>0){
                System.out.println("Length: "+ (q.getLast().y-q.getFirst().x));
            }
            else{
                System.out.println("Length: 0");
            }
            System.out.println("window: "+time_window);
            System.out.println("\n");
        }
        
        //overloaded method to add a tag
        void DisplayDropPercentList(LinkedList<Tuple<Long,Long>> q, long time_window, String tag){

            System.out.println(tag+"\n"+tag+" Queue List, size "+q.size());
            for(Tuple<Long,Long> e: q){
                System.out.println(tag+" ("+e.x +"," +e.y+")");
            }
            if(q.size()>0){
                System.out.println(tag+" Length: "+ (q.getLast().y-q.getFirst().x));
            }
            else{
                System.out.println(tag+" Length: 0");
            }
            System.out.println(tag+" window: "+time_window);
            System.out.println(tag+" \n");
        }
        
	private int computeAccumulationHeight() {
            for(int i=0; i<ROWS; i++) {
		for(int j=0; j<COLUMNS; j++) {
                    if (grid[i][j] != EMPTY) return ROWS-i;                                                
                }                
            }                
            return 0;                
	}
        
        
	private int countBlocks() {
                int count = 0;
		for(int j=0; j<COLUMNS; j++)
                    for(int i=0; i<ROWS; i++)
			if (grid[i][j] != EMPTY) count++;
		return count;
	}
	
	private boolean rowHasBlocks(int row) {
		for(int j=0; j<COLUMNS; j++)
			if (grid[row][j] != EMPTY) return true;
                return false;
	}
	
//	private int removePartialRow(int row) {
//                int boxCount = 0; 
//		for(int j=0; j<COLUMNS; j++) {
//		        if (grid[row][j] != EMPTY) boxCount++;
//                        grid[row][j] = EMPTY;
//                }
//                return boxCount;
//        }
        
	private int noOfBlocksInRow(int row) {
                int boxCount = 0;                
		for(int j=0; j<COLUMNS; j++) {
		        if (grid[row][j] != EMPTY) boxCount++;
                }
                return boxCount;
	}
	
	private void removeFullRows() {
		int n_full = countFullRows();
                //add points for each full row RFG1
		ComputeScoreAndDelay(COLUMNS * Parameters.PointsAddedPerRemovedBlockFromFullRow * n_full);
                if(n_full == 0)
		return;
		// sounds.playDestroyRows(n_full);
//		if(num_rows_deleted / DELETED_ROWS_PER_LEVEL != (num_rows_deleted+n_full) / DELETED_ROWS_PER_LEVEL) {
//			timer.faster();
//			level_label.addValue(n_full / DELETED_ROWS_PER_LEVEL + 1);
//			level_label.repaint();
//		}
		rows_deleted_label.addValue(n_full);
		num_rows_deleted += n_full;
		for(int i=ROWS-1; i>=0; i--)
			while(rowIsFull(i))
				removeRow(i);
		game_grid.repaint();
                
        }
        
        public void SwitchToFocusTask() {
                AddDataToTILData();
                SessionNo++;
                LDS.Parameters.Mode = LDS.Parameters.LDS_MODE;
                //System.exit(999);
                pauseGame();
                frame.setVisible(false);
                ControlCode.Frame.setVisible(true);
                ControlCode.t.resume();
                
	}
        
        // when the following is complete
        // it will be the only thing that directly calls the above
        // replace all function calls of the above with below
        
        public void SwitchBasedOnCondition(String measure, double CritValue, char Comparison){
            boolean switchTask = false;
            //a long else if then tree for every possible measure
            //for the correct measure, do the condition compare on it
            // if its true set switchTask to true
            //
            //System.nanoTime()-StartTime)/1000000
            if(measure==null){
                System.out.println("ERROR, measure has not been set");
            }
            
            
            if(measure.equals("SIZE_LAST_ROW_REMOVED")){
                switchTask=ConditionCompare(SizeLastRowRemoved,CritValue,Comparison);
            }
            else if(measure.equals("SPEED_LEVEL")){
                switchTask=ConditionCompare(speed,CritValue,Comparison);
            }
            else if(measure.equals("HEIGHT_VARIANCE")){
                switchTask=ConditionCompare(computeVariance(HeightQueue,queue_history),CritValue,Comparison);
            }
            else if(measure.equals("DROP_DURATION_VARIANCE")){
                switchTask=ConditionCompare(computeVariance(DropDurationQueue,queue_history),CritValue,Comparison);
            }
            else if(measure.equals("DROP_PERCENTAGE")){
                switchTask=ConditionCompare(DropPercentSanitized(DownQueue, DropPercentageTimeWindow, (System.nanoTime()-StartTime)/1000000),
                    CritValue,Comparison);
            }
            else if(measure.equals("TIME_IN_LEVEL")){
                switchTask=CheckTimeInLevel();
            }
            else if(measure.equals("RUN_UNTIL_TIME_LIMIT")){
                //time limit checker
                //W("critvalue "+CritValue+" < "+((System.nanoTime() - StartTime)/1000000000)+" time since start");
                switchTask= ( CritValue < ((System.nanoTime() - StartTime)/1000000000));
            }
            //final else to catch any errors
            else{
                System.out.println("ERROR: No match for Tetris end condition");
            }
            if(switchTask){
                SwitchToFocusTask();

                W("Mode="+LDS.Parameters.Mode);
                
                //System.exit(997);

            }
        }
        
        public Boolean ConditionCompare(double v1,double v2,char comparison){
            if(comparison=='>'){
                return (v1>v2);
            }
            else if(comparison=='<'){
                return (v1<v2);
            }            
            return null;
        }
        
        public ArrayList<ArrayList<Double>> ReturnTILList(){
            System.out.println("returning list:");
            //DisplayTimeInLevelList(TimeInLevelList);
            return TimeInLevelList;
        }
        
	public void DisplayTimeInLevelList(ArrayList<ArrayList<Double>> TimeInLevelList){
            System.out.println("/");
            for(int i=0; i<TimeInLevelList.size(); i++){
                System.out.println("Level "+i);
                for(int j=0; j<TimeInLevelList.get(i).size(); j++){
                    System.out.println("  "+TimeInLevelList.get(i).get(j));
                }
            }
        }
        
        private Double Mean(ArrayList<Double> l){
            if(l.isEmpty()) return (double) 0;
            Double sum = (double) 0;
            for(Double val: l){
                sum+=val;
            }
            return (Double) (sum/l.size());
        }
        
        //remember to add a way to not include the outlier
        // it should probably be fine
        // times are added to the data structure every level transition
        // time in level is checked every frame
        // since level transitions reset the time to zero
        // the only real unknown edge case is when someone transitions exactly on the crit point        
        
        public void AddDataToTILData(){
            ArrayList<ArrayList<Double>> TILList = ReturnTILList();
            System.out.println("\nAdding Data:");
            DisplayTimeInLevelList(TILList);
            //first, get the average for each level and add them to an arraylist
            ArrayList<Double> LeveltimeAvg = new ArrayList<Double>();
            
            System.out.println("Leveltime average, size "+TILList.size());
            for(ArrayList<Double> tl : TILList){ // tl for time list
                LeveltimeAvg.add(Mean(tl));
                System.out.println(Mean(tl));
            }
                System.out.println("critpoints:");
            for(Long p: TILData.critpoints){
                System.out.println(""+p);
            }
            
            //then write a new line using the subject's id, avg's, and crit_points
            TILData.AddSubjectData(Subject_ID,LeveltimeAvg,TILData.critpoints);
            
        }
        
        
        public boolean CheckTimeInLevel(){
            // check time spent in level
            // (System.nanoTime() - StartTimeInLevel)/1000000000;
            // against critical point in the level
            // from TimeInLevelData
            
            Long comparetime = ((System.nanoTime() - StartTimeInLevel)/1000000000);
            
            return (comparetime>TILData.critpoints.get((int)speed));
            // stop tetris because this subject is an outlier for this level
            
            
        }
                

        //<A>
        // flow control system used to be here
        // moved to separate file for encapsulation and ease of maintaining
        //</A>
        
        public void ComputeScoreAndDelay(int AddedScore) {
            System.out.println(UnpressPercent);
            if(UnpressPercent != null && UnpressPercent>1.0) System.out.println("PERCENT ABOVE 1.0");
            
            score+=AddedScore;
            
            int high_score = high_score_label.getText().length() > 0 ?
		Integer.parseInt(high_score_label.getText()) : 0;
            if(score > high_score)
		high_score_label.setText("" + score);
	    
            //long delay=1000;  // should probably make this set in parameters at some point
            
            long delay = PersistentDelay;
            
            // speed and level code based on section 5.9 and 5.10 of Colin Fahey's Tetris article
            //   www.colinfahey.com/tetris/tetris.html
            
            // speed is equivalent to level number
            // delay is the amount of time it takes for a zoid to drop one level
            // score is the current number of points the player has
            
            //speed = (long) Math.max(Math.min(Math.floor(score/100.0),Parameters.MaxLevels),0);
            /*
            if(Adaptive_Delay_Mode==null){
                int minD = Parameters.MinimumDelayMilliseconds;
                int maxD = 1000; // maximum delay is set to 1000 milliseconds for now
                
                delay = (long) ((maxD-minD)*(1.0-1.0*speed/Parameters.MaxLevels)+minD);
            }
            if(Adaptive_Delay_Mode=="BOUNDED"){
                delay = DelayFromUnpressPercentBounds(PersistentDelay,UnpressPercent,AdaptiveFallSpeedLowerBound,AdaptiveFallSpeedUpperBound);
                PersistentDelay=delay;
            }
            else if(Adaptive_Delay_Mode=="Linear"){
                delay = DelayDromUnpressPercentLinear(PersistentDelay,UnpressPercent);
                PersistentDelay=delay;
            }
            */
            //W("speed/Parameters.MaxLevels"+(1.0-1.0*speed/Parameters.MaxLevels));
            
            
            if (old_speed != speed){
                TimeInLevel = (System.nanoTime() - StartTimeInLevel)/1000000000.0;
                W("old_speed:"+(int)old_speed);
                TimeInLevelList.get((int)old_speed).add(TimeInLevel);
                //DisplayTimeInLevelList(TimeInLevelList);
                StartTimeInLevel = System.nanoTime();
            }
            
//            

            /*
            control system console logging
            W("\nLEVEL: "+speed);
            W("DELAY: "+delay);
            W("KEY %: "+UnpressPercent);
            W("SCORE: "+score);
            */


            old_speed = speed;
            
            score_label.setText(""+score);
            speed_label.setText(""+speed);
            level_duration_label.setText(""+TimeInLevel);
            timer.setDelay(delay);
            
        } 
        
        public int eraseBiggestRowGreaterThan(int row) {
            int biggestRow = 0;
            int biggestRowAmount = 0;
            for (int i = row; i < ROWS; i++) {
                int noOfBlocks = noOfBlocksInRow(i);
                if (noOfBlocks >= biggestRowAmount) {
                    biggestRowAmount = noOfBlocks;
                    biggestRow = i;
                }
            }
            SizeLastRowRemoved=biggestRow;  //this is returning a number larger than the size of a row, TOO BIG PLS FIX
            RowRemovalQueue=addToQueue(RowRemovalQueue, biggestRowAmount, queue_history);
            removeRow(biggestRow);
            //System.out.println("REMOVING ROW "+biggestRow);
            return biggestRowAmount;
        }
        
        // NB ^^^ This removes the row lower on the grid than Parameters.RemoveBiggestPartialRowIfBlockInRow
        // with the highest number of blocks
        // it does not have a changing threshhold for number of blocks removed
        
        public void RemoveRowsIfStackedTooHigh() {
            		
            while (rowHasBlocks(Parameters.RemoveBiggestPartialRowIfBlockInRow)) {
                int NoOfPiecesRemoved = 
                        eraseBiggestRowGreaterThan(Parameters.RemoveBiggestPartialRowIfBlockInRow);
                //remove points for each empty square
                ComputeScoreAndDelay(-(COLUMNS - NoOfPiecesRemoved) * Parameters.PointsSubtractedPerEmptySpaceFromPartialRow);
            
            }
            game_grid.repaint();
            LogEvent("row_removed");
        }
        

        
	public void start() {
		timer = new Timer(INITIAL_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				synchronized(timer) {
					if(cur_piece.canStepDown()) {
						cur_piece.cut();
						cur_piece.stepDown();
						cur_piece.paste();
                                                //removed RFG1
//						if(timer.isFast())
//							score_label.addValue(1); // a small reward for using fast mode
					}
					else { // it hit something
						timer.setFast(false);
						if( ! cur_piece.isTotallyOnGrid())
							gameOver();
						else {
							removeFullRows();
							installNewPiece();
						}
					}
				}
                                
                                TimeInLevel = (System.nanoTime() - StartTimeInLevel)/1000000000;
                                level_duration_label.setText(""+TimeInLevel);
                                
                                if (Parameters.MaxSecondsInLevel > 0) {
                                    // THIS IS THE SPOT THAT NEEDS TO CHECK TO SWITCH THE TASK
                                    //think about keeping this in or not vvv
                                    if ((speed == Parameters.MaxLevels) & (TimeInLevel >= Parameters.MaxSecondsInLevel)) {
                                        SwitchToFocusTask();
                                    }
                                    
                                }
                                // important ^^ does not trigger if you don't set MaxSecondsInLevel
                                SwitchBasedOnCondition(SwitchCondition_Measure, SwitchCondition_Value, SwitchCondition_Comparison); 
                                
                                if(isAdaptive) FCS.ControlSystem(UnpressPercent, score, speed, CurrentTime());
                                else FCS.SetVarsVanillaTetris(num_rows_deleted, score);
                                
				game_grid.repaint();
			}
		});
		timer.start(); // pauses immediately
                ComputeScoreAndDelay(-4);
	}
	
	public void stop() {
		pauseGame();
		synchronized(timer){
			timer.stop();
		}
		timer = null;
	}
	
	private void startGame() {
                GameStarted = true;
		timer.setDelay(INITIAL_DELAY);
		timer.setPaused(false);
                //StartTime = System.nanoTime();
                GameStarted = true;
                TotalRunTime = 0;
		start_newgame_butt.setLabel("Start New Game");
		pause_resume_butt.setEnabled(true); // stays enabled from here on
		pause_resume_butt.setLabel("Pause");
		pause_resume_butt.validate();
		// sounds.playSoundtrack();
                StartTimeInLevel = System.nanoTime();
                LogEvent("start_game");
	}
	
	private void newGame() {
		game_grid.clear();
		installNewPiece();
		num_rows_deleted = 0;
		rows_deleted_label.setText("0");
		level_label.setText("1");
		level_duration_label.setText("0");
                score_label.setText("0");
		speed_label.setText("0");
		//StartTime = System.nanoTime();
                GameStarted = true;
                TotalRunTime = 0;
                LogEvent("new_game");
		startGame();
	}
	
	private void pauseGame() {
		timer.setPaused(true);
                TotalRunTime += System.nanoTime() - StartTime;
		pause_resume_butt.setLabel("Resume");
		// sounds.stopSoundtrack();
                
                LogEvent("game_paused");
	}
	
	public void resumeGame() {
		timer.setPaused(false);
		//StartTime = System.nanoTime();
		pause_resume_butt.setLabel("Pause");
		// sounds.playSoundtrack();
                
                LogEvent("game_resumed");
	}
	
	public void init() {
		//sounds = new TetrisSound(); // NOTE: Must be initialized after Applet fully constructed!
		installNewPiece();
                W("\n\n\n\nMAX LEVEL "+P.MaxLevels+"\n\n\n\n");
                TimeInLevelList = CreateTimeInLevelList(P.MaxLevels);
                
		pause_resume_butt.setEnabled(false);
		start_newgame_butt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(start_newgame_butt.getLabel().equals("Start"))
					startGame();
				else
					newGame();
			}
		});		
		pause_resume_butt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(pause_resume_butt.getLabel().equals("Pause"))
					pauseGame();
				else
					resumeGame();
			}
		});
		
		//create key listener for rotating, moving left, moving right
		KeyListener key_listener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
                                long tmp = LastButtonPressTime;
                                LastButtonPressTime=(System.nanoTime()- StartTime)/1000000;
                                if(LastButtonPressTime!=0 && LastButtonUnpressTime!=0)
                                    KeyUpQueue.add(new Tuple<Long,Long>(LastButtonUnpressTime,LastButtonPressTime));
                                
                                LastButtonUnpressTime=0;  // this is just asking for trouble
                                LastButtonPressTime=0;    // but its for bugfixing purposes so its ok
                                //W(""+tmp+","+LastButtonUnpressTime);
                                
                                
                                if(StartTime != tmp_start_time)
                                    W("START TIME CHANGED\n\n\n"+StartTime+","+tmp_start_time+"\n\n\n\n\n");
                                tmp_start_time = StartTime;
                                
                                if(KeyUpQueue.size()>0){
                                  KeyUpQueue = ContainWithinTimeWindow(KeyUpQueue, (System.nanoTime()-StartTime)/1000000,KeyUpTimeWindow);
                                  //DisplayDropPercentList(KeyUpQueue, KeyUpTimeWindow,"outside");
                                  UnpressPercent=UnpressPercentSanitized(KeyUpQueue, KeyUpTimeWindow, (System.nanoTime()-StartTime)/1000000);
                                  //W("unpress_percent "+UnpressPercent);
                                }
                                
				if(timer.isPaused()) //don't do anything if game is paused
					return;
                                
                                /*              
                                // this code has problems
                                // we may still add sound in the future if we have time
                                // for now its dummied out
                                if (e.getKeyCode() >= 37 & e.getKeyCode() <= 40){
                                    if(!SoundKeyPressed[e.getKeyCode()-37] ){
                                        snd.PlayWave("bip");
                                        // this works correctly but when buttons are pressed a bunch in quick succession it slows down the main loop
                                    }
                                    SoundKeyPressed[e.getKeyCode()-37]=true;
                                }
                                */
				if (e.getKeyCode() == 37 || e.getKeyCode() == 39) { //left or right arrow pressed
                                        if (e.getKeyCode() == 37) KeyCounter[0]++;
					if (e.getKeyCode() == 39) KeyCounter[1]++;
					int dir = e.getKeyCode() == 37 ? -1 : 1;
					synchronized(timer) {
						cur_piece.cut();
						cur_piece.setX(cur_piece.getX() + dir); // try to move
						if( ! cur_piece.canPaste())
							cur_piece.setX(cur_piece.getX() - dir); // undo move
						cur_piece.paste();
					}
					game_grid.repaint();
                                        
				}
				else if (e.getKeyCode() == 38) { //rotate
                                        KeyCounter[2]++;
					synchronized(timer) {
						cur_piece.cut();
						cur_piece.rotate();
						if( ! cur_piece.canPaste())
							cur_piece.rotateBack();
						cur_piece.paste();
					}
					game_grid.repaint();
				}
				if (e.getKeyCode() == 40) { //down arrow pressed; drop piece
					KeyCounter[3]++;
                                        DropStartTime=System.nanoTime();
                                        DownStartTime=(System.nanoTime()- StartTime)/1000000;
                                        timer.setFast(true);
                                        OutputTheDataWhenFirstFast = true;
				} 
                                if (e.getKeyCode() == KeyEvent.VK_CONTROL) ControlKeyPressed = true;
                                if (e.getKeyCode() == KeyEvent.VK_F && ControlKeyPressed){
                                    ControlKeyPressed = false;
                                    SwitchToFocusTask();
                                    W("Mode="+LDS.Parameters.Mode);
                                    //return; // REMOVE LATER MAYBE
                                    //System.exit(998);
                                }
                                if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) MinusKeyPressed = true;
                                
                                
                                LogEvent("key_press_"+ e.getKeyText(e.getKeyCode()));
                                
			}
                        
                        public void keyReleased(KeyEvent e) {
                            
                            if (e.getKeyCode() >= 37 & e.getKeyCode() <= 40){
                                SoundKeyPressed[e.getKeyCode()-37]=false;
                            }                            
                            
                            LastButtonUnpressTime=(System.nanoTime()- StartTime)/1000000;
                            
                            if(LastButtonUnpressTime == tmp_unpress_time)
                                    W("UNPRESS IDENTICAL\n\n\n"+LastButtonUnpressTime+","+tmp_unpress_time+"\n\n\n\n\n");
                            tmp_unpress_time = LastButtonUnpressTime;
                            //W("Set unpress at "+LastButtonUnpressTime);
                            if (e.getKeyCode() == 40) { //down arrow pressed; drop piece
                                timer.setFast(false);
                                // down queue stuff
                                DropDurationQueue=addToQueue(DropDurationQueue, (int) (System.nanoTime()-DropStartTime)/1000000, queue_history); 
                                DownEndTime=(System.nanoTime()- StartTime)/1000000;
                                
                                // drop percent stuff
                                DownQueue.add(new Tuple<Long,Long>(DownStartTime,DownEndTime));
                                DownStartTime = LongMin; // reset them back to "not dropping"
                                DownEndTime = LongMin;
                                DownQueue = removeOldFromQueue(DownQueue,(System.nanoTime()-StartTime)/1000000,DropPercentageTimeWindow);
                            }
                            if (e.getKeyCode() == KeyEvent.VK_CONTROL) ControlKeyPressed = false;
                            if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) MinusKeyPressed = false;
                            LogEvent("key_release_"+ e.getKeyText(e.getKeyCode()));
                            
                        }
                        
		};
		
		// add the key listener to all components that might get focus
		// so that it'll work regardless of which has focus
		start_newgame_butt.addKeyListener(key_listener);
		pause_resume_butt.addKeyListener(key_listener);
		
		Panel right_panel = new Panel(new GridLayout(3, 1));	
		right_panel.setBackground(BACKGROUND_COLOR);
		
		Panel control_panel = new Panel();
		control_panel.add(start_newgame_butt);
		control_panel.add(pause_resume_butt);
		control_panel.setBackground(BACKGROUND_COLOR);
		right_panel.add(control_panel);
		
		Panel stats_panel1 = new Panel(new GridLayout(5, 2));
		stats_panel1.add(new TetrisLabel("Use Arrow Keys"));
                //stats_panel1.add(new TetrisLabel("Keys"));
		stats_panel1.add(blank_label);
                stats_panel1.add(new TetrisLabel(" Rows Deleted: "));
		//stats_panel1.add(blank_label);
                //stats_panel1.add(level_label);
                stats_panel1.add(rows_deleted_label);
                stats_panel1.add(new TetrisLabel("        Speed: "));
		stats_panel1.add(speed_label);
                stats_panel1.add(new TetrisLabel("     Duration: "));
		stats_panel1.add(level_duration_label);
                stats_panel1.add(new TetrisLabel("        Score: "));
		stats_panel1.add(score_label);
                //right_panel.add(stats_panel1);
		Panel tmp1 = new Panel(new BorderLayout());
		tmp1.setBackground(BACKGROUND_COLOR);
		tmp1.add("Center", stats_panel1);
		right_panel.add(tmp1);
		
                Panel tmp = new Panel(new BorderLayout());
		tmp.add("North", new TetrisLabel("    Next Piece:"));
		tmp.add("Center", next_piece_canvas);
		tmp.setBackground(BACKGROUND_COLOR);
		right_panel.add(tmp);
		
//		Panel stats_panel = new Panel(new GridLayout(4, 2));
//		stats_panel.add(new TetrisLabel("Use Left Right Up "));
//		stats_panel.add(new TetrisLabel("Down Arrow Keys"));
		//stats_panel.add(rows_deleted_label);
//		stats_panel.add(new TetrisLabel("    Rows Deleted: "));
//		stats_panel.add(rows_deleted_label);
		//stats_panel.add(new TetrisLabel("    Level: "));
		//stats_panel.add(level_label);
//		stats_panel.add(new TetrisLabel("    Score: "));
//		stats_panel.add(score_label);
//		stats_panel.add(new TetrisLabel("    High Score: "));
//		stats_panel.add(high_score_label);
//		tmp = new Panel(new BorderLayout());
//		tmp.setBackground(BACKGROUND_COLOR);
//		tmp.add("Center", stats_panel);
		//right_panel.add(tmp);
		
		// finaly, add all the main panels to the applet panel
		this.setLayout(new GridLayout(1, 2));
		this.add(game_grid);
		this.add(right_panel);
		this.setBackground(BACKGROUND_COLOR);
		this.validate();
//                Parameters P = new Parameters("PARAMETERS");
	}

	
	public static void TetrisMain() { //main(String[] args) {
           
		frame = new Frame("Tetris");
		Tetris tetris = new Tetris();
		frame.add(tetris);
		tetris.init();
		tetris.start();

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//System.exit(0);
                                System.out.println("Closing window is disabled");
			}
		});

		frame.setSize(489, 841);
		frame.setResizable(false);
		frame.setVisible(false);
                
	}
} // end class Tetris

