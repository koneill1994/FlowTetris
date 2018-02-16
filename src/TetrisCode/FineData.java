package TetrisCode;

import LDS.ControlCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 *
 * @author rfgreen
 * 
 * modified by Kevin O'Neill
 * oneill.45@wright.edu
 */

/*

write a timestamp and an action

Sample code:
F.LogEvent(""+(TotalRunTime + System.nanoTime() - StartTime)/1000000 + Tab + "spawn_new_piece"+Tab
                    +Parameters.RemoveBiggestPartialRowIfBlockInRow+Tab
                    +computeAccumulationHeight()+Tab+speed);

*/

public class FineData {
    
    String SubjNo = ControlCode.SubjNo;
    char Tab = ',';
    boolean FirstWriteToFile = true;
    String ExpCond = "NONE";

    
    // these are the header columns to be used
    // edit this to add more columns
    // make sure you also change the logging to make sure
    // it logs the same number of items as this list's length
    List<String> HeaderLabels = Arrays.asList("SubjectID","IsAdaptive",
            "TimeStamp", "EventLabel",
            "SizeLastRowRemoved","SizeLastRowRemovedVariance",
            "SpeedLevel",
            "AccumulationHeight","HeightVariance",
            "LastDropDuration","DropDurationVariance",
            "DropPercentage","SubjNo");
    
    String FileName = "EXPERIMENT/DATA/" + "TETRIS_FINEDATA_" + Tetris.Subject_ID + ".csv";

    File file = new File(FileName);
    /*
    FileWriter fileWritter;
    BufferedWriter BW;
*/    

    // NB: row removal threshold is measuring the variable
    // Parameters.RemoveBiggestPartialRowIfBlockInRow
    
    public FineData() {
        CreateLogHeader();
    }
    
    // This function writes S to system output
    // i think randy was using it for debug purposes
    public void W(String S) {
        System.out.println(S);
    }
    
    // [The process cannot access the file because it is being used by another process]
    //   vvv 
    
    // this will be run once when the program starts
    // to create the headers on the data file
    public boolean CreateLogHeader(){
        String Line = "";
        for(String Label: HeaderLabels){
            Line+=Label+Tab;
        }
        Line+="\r\n";        
        try{            
            file.delete();
            file.createNewFile();
            
            FileWriter fileWritter = new FileWriter(FileName,true);
            BufferedWriter BW = new BufferedWriter(fileWritter);
            
            BW.write(Line);
            BW.close();
            
        }catch(IOException e){
            e.printStackTrace();
    	}
        
        
        return true;
    }
    
    // this will be called by the program any time a significant event happens
    // the data to log will be passed in as an argument,
    // formatted, and then added as a line in the data file
    public boolean LogEvent(String Data){
        try{          
            
            FileWriter fileWritter = new FileWriter(FileName,true);
            BufferedWriter BW = new BufferedWriter(fileWritter);

            BW.write(Data+"\r\n");
            BW.close();
            
        }catch(IOException e){
            e.printStackTrace();
    	}
        return true;
    }    
    
    
    
    // randy's code includes all of the above in a single function
    // which then checks to see if it is the first time logging something
    // to see if it should put in the headers first
    
    // i think this is because he needed to open a log file to write to it   
    // or maybe its just a java convention I don't know
    
    // may also be to keep all the stuff that could throw an exception
    // in a single try/catch block
    
    
    // NB: Subject Number cannot be accurately set when the file is created
    // because the file is created before ControlCode knows what the subjID will be
    // I can't come up with an elegant solution, the multithreading has muddied the waters
    // So as a (probably permanent) stopgap, the subjID is now a column in the log file
    
}
