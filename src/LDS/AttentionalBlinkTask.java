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

    int BlockNo = 0;

    char Tab = ',';

    boolean FirstTime = true;

    boolean StartTimerNow;
    Task B = null;
    
    
    String TargetString = "";
    char TargetLetter = (char)0;
    int TargetIndex;


    public AttentionalBlinkTask(Task B, int NoOfBlocks){
    
        //reset timers
        for (int i = 0; i < 5; i++) StopTime[i] = -1;
    
        BlockNo = B.TaskCounter;
        
        B.TaskCounter++;
        
        W("MADE D");
        W("BlockNo="+BlockNo);

    
    }
    
    
    public void W(String S) {
        System.out.println(S);
    }
    
    public void StartTimer(int TimeMode) {
        StartTime[TimeMode] = CT.currentTimeMillis();
    } 
  
    public void StopTimer(int TimeMode) {
        StopTime[TimeMode] = CT.currentTimeMillis() - StartTime[TimeMode];
    } 
    
    public static String ReplaceCharAt(String str, int pos, char c) {
        //char c = character.charAt(0);
        StringBuffer buf = new StringBuffer(str);
        buf.setCharAt(pos-1, c);
        return buf.toString();
    }
    
        public void GetQue(int ImageNo) {
        
        
        //build TargetString of numbers
        
        TargetString = "";
        
        int Digit = 0;
        int NewDigit;
            
        for (int i = 0; i < B.NoOfSymbols; i++) {
            
            if (i == 0)
                
                Digit = R.nextInt(10);
            
            else {
                
                do {
                    NewDigit = R.nextInt(10);
                } while (Digit == NewDigit);
                
                Digit = NewDigit;
                
            }
                
            TargetString += "" + Digit;
            
        }
        
        TargetLetter = Task.FirstLettersUsed.charAt(R.nextInt(Task.FirstLettersUsed.length()));
        
//        do {
//            FirstTargetIndex = R.nextInt(B.NoOfSymbols);
//        } while ((FirstTargetIndex < Task.FirstLetterIndex[0]) | (FirstTargetIndex > Task.FirstLetterIndex[1]));
        /*
        int Distance = R.nextInt(Task.LetterIndex[1] - Task.LetterIndex[0]) + 1; //get a number from 1 to 13
        
        int LastPossibleStartingPosition = Task.LetterIndex[1] - Task.LetterIndex[0] - Distance;
        
        if (LastPossibleStartingPosition == 0)
            TargetIndex = 1;
        else
            TargetIndex = 1 + R.nextInt(LastPossibleStartingPosition + 1);
        
        TargetIndex += Task.LetterIndex[0] - 1;
        
        TargetString = ReplaceCharAt(TargetString, TargetIndex, TargetLetter);
        
        SecondTargetIndex = 0;
        SecondTargetLetter = '*';
        SecondLetterIsPresentInTarget = true;
        
        //one in three chance there is no target
        if (R.nextInt(3) == 0) SecondLetterIsPresentInTarget = false;
        
        FirstSecondLetterStr = "NO_SECOND_LETTER";
        FirstTargetLetterMemory = FirstTargetLetter;
            
        if (SecondLetterIsPresentInTarget) {
        
            do {
                SecondTargetLetter = Task.SecondLettersUsed.charAt(R.nextInt(Task.SecondLettersUsed.length()));
            } while (FirstTargetLetter == SecondTargetLetter);

//            do {
//                SecondTargetIndex = R.nextInt(B.NoOfSymbols);
//            } while ((SecondTargetIndex < Task.SecondLetterIndex[0]) | (SecondTargetIndex > Task.SecondLetterIndex[1]));

            SecondTargetIndex = FirstTargetIndex + Distance;
            
            TargetString = ReplaceCharAt(TargetString, SecondTargetIndex, SecondTargetLetter);

            SecondTargetLetterMemory = SecondTargetLetter;
            
            if (SecondTargetLetter < FirstTargetLetter) {
                CorrectAnswerStr = "LEFT";
                FirstSecondLetterStr = "SECOND_LETTER_FIRST";
            }
        
            if (SecondTargetLetter > FirstTargetLetter) {
                CorrectAnswerStr = "RIGHT";
                FirstSecondLetterStr = "FIRST_LETTER_FIRST";
            }
            
        } else CorrectAnswerStr = "MIDDLE";
         */       
    }
    
    
}
