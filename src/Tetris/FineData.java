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
            "RowRemovalThreshold","AccumulationHeight",
            "SpeedLevel");

    
    public FineData() {
        
    }
    
    public void W(String S) {
        System.out.println(S);
    }
    
    // this will be run once when the program starts
    // to create the headers on the data file
    public boolean CreateLogHeader(){
        String Line = "";
        for(String Label: HeaderLabels){
            Line+=Label+Tab;
        }
        return true;
    }
    
    // this will be called by the program any time a significant event happens
    // the data to log will be passed in as an argument,
    // formatted, and then added as a line in the data file
    public boolean LogEvent(String Data){
        
        return true;
    }    
    
    
    // this is randy's code
    // using it for reference
    // delete later
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
