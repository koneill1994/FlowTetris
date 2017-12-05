/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TetrisCode;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author Student
 */

class DoubleBufferedCanvas extends Canvas {
	private Image mActiveOffscreenImage = null;
	private Dimension mOffscreenSize = new Dimension(-1,-1);
	private Graphics mActiveOffscreenGraphics = null;
	private Graphics mSystemGraphics = null;
	private final static Font LABEL_FONT = new Font("Serif", Font.BOLD, 18);
		
	DoubleBufferedCanvas() {
		/*
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) { 
				repaint(); 
			}
		});*/
	}
	
 	/**
 	 * NOTE: when extending applets:
	 * this overrides update() to *not* erase the background before painting
	 */
	public void update(Graphics g) {
		paint(g);
                g.setColor(Color.BLACK);
                g.setFont(LABEL_FONT);
                //g.drawString("XYZ", 600, 100);
	}

	public Graphics startPaint (Graphics sysgraph) {
		mSystemGraphics = sysgraph;
		// Initialize if this is the first pass or the size has changed
		Dimension d = getSize();
		if ((mActiveOffscreenImage == null) ||
			(d.width != mOffscreenSize.width) ||
			(d.height != mOffscreenSize.height)) 
		{
			mActiveOffscreenImage = createImage(d.width, d.height);
			mActiveOffscreenGraphics = mActiveOffscreenImage.getGraphics();
			mOffscreenSize = d;
			mActiveOffscreenGraphics.setFont(getFont());
		}
		//mActiveOffscreenGraphics.clearRect(0, 0, mOffscreenSize.width, mOffscreenSize.height);
		return mActiveOffscreenGraphics;
	}
	
	public void endPaint () {
		// Start copying the offscreen image to this canvas
		// The application will begin drawing into the other one while this happens
		mSystemGraphics.drawImage(mActiveOffscreenImage, 0, 0, null);
	}
        
}

