/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LDS;

import java.awt.*;
import java.io.*;
import java.util.*;
import TetrisCode.Tetris;

/**
 *
 * @author Kevin
 */
public class AttentionalBlinkTask {
    
    // goal: to implement the simple attentional blink task 
    //         and integrate it into the task code
    //
    // start off by copying a bunch of boilerplate from FocusTask.java
    // only grabbing what looks useful
    // remove anything that turns out to be useless later
    
    CurrentTime CT = new CurrentTime();

    int Xo = ControlCode.ScreenSizeX/2;
    int Yo = ControlCode.ScreenSizeY/2;

    Graphics2D g2;

    public static boolean IsTetrisTask;
    public static int SessionNo = 1;

    String CorrectAnswerStr = "";
    String SubjAnswerStr = "";

    long DisplayAnswerStartTime;

    
    boolean AnswerGiven;
    
    long StartTime[] = new long[5];
    long StopTime[] = new long[5];

    public static Random R = new Random();

    int Game;

    char Tab = ',';

    boolean FirstTime = true;

    boolean StartTimerNow;
    Task B = null;

    public AttentionalBlinkTask(Task B, int NoOfBlocks){
    
    
    
    }
}
