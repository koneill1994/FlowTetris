package LDS;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
public class CurrentTime {
    
    public CurrentTime() {
        
    }
    
    public long currentTimeMillis() {
        
        return System.nanoTime()/1000000;
    
    }
    
    public double floatCurrentTimeMillis() {
        
        return System.nanoTime()/1000000.0;
    
    }
    
}
