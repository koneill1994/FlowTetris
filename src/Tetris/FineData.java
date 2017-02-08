package Tetris;

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
    
    String SubjNo = "SUBJ1";
    char Tab = ',';
    boolean FirstWriteToFile = true;
    String ExpCond = "NONE";

    
    // these are the header columns to be used
    // edit this to add more columns
    // make sure you also change the logging to make sure
    // it logs the same number of items as this list's length
    List<String> HeaderLabels = Arrays.asList("TimeStamp", "EventLabel",
            "SizeLastRowRemoved","SizeLastRowRemovedVariance",
            "SpeedLevel",
            "AccumulationHeight","HeightVariance");
    
    String FileName = "DATA/" + "FineData" + ".csv";

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
    
    
    
    // this is randy's code
    // using it for reference
    // delete later
    // also visible in Data.java for historical purposes
    public boolean OutputData(String Data) {
 
    	try{

            String PreambleStr =
                   SubjNo + Tab + ExpCond + Tab + Parameters.ParameterFile + Tab;

            String FileName = "DATA/" + "MYDATA" + ".csv";

            File file = new File(FileName);

//            if (Data.equals("CHECK_EXISTANCE")) {
//                if (file.exists()) return true; //yes it exists
//                else return false; //no, it does not
//            }

            if (FirstWriteToFile) {
                W("DELTING DATA");
                file.delete();
                file.createNewFile();
            }

            //true = append file
            FileWriter fileWritter = new FileWriter(FileName,true);
            BufferedWriter BW = new BufferedWriter(fileWritter);

            if (FirstWriteToFile) {

                String S = 
                      "DATA_FILE"+Tab+"DATA_TAG"+Tab+"PARAMETER_FILE"+Tab+"TIME"
                 +Tab+"SESSION_NO"
                 +Tab+"TOTAL_BLOCKS"+Tab+"TRAPPED_SPACES"
                 +Tab+"SCORE"+Tab+"DELAY"+Tab+"SPEED_LEVEL"
                 +Tab+"LEFT_KEY"+Tab+"RIGHT_KEY"+Tab+"UP_KEY"+Tab+"DOWN_KEY"
                 +Tab+"VERSION";

                S += "\r\n";

                BW.write(S);

            }

            String S = PreambleStr + Data + "\r\n";

            BW.write(S);

            BW.close();

            FirstWriteToFile = false;

    	}catch(IOException e){
    		e.printStackTrace();
    	}
        
        //does not matter what you return, if data
        return false;

    }
    
}
