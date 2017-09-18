
package TetrisCode;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 *
 * @author Kevin O'Neill
 */
public class ControlSystemLogFile {
    
    String SubjNo = "SUBJ1";
    char Tab = ',';
    boolean FirstWriteToFile = true;
    String ExpCond = "NONE";
    
    public ControlSystemLogFile(){
    }
    
    public void W(String S) {
        System.out.println(S);
    }
    
    
    public boolean OutputData(String Data) {
 
    	try{

            String PreambleStr =
                   SubjNo + Tab + ExpCond + Tab + Parameters.ParameterFile + Tab;

            String FileName = "EXPERIMENT/DATA/" + "CONTROL_LOG" + ".csv";

            File file = new File(FileName);

            if (FirstWriteToFile) {
                W("DELETING DATA");
                file.delete();
                file.createNewFile();
            }

            //true = append file
            FileWriter fileWritter = new FileWriter(FileName,true);
            BufferedWriter BW = new BufferedWriter(fileWritter);

            if (FirstWriteToFile) {

                String S = 
                        "DATA_FILE"+Tab+"DATA_TAG"+Tab+"PARAMETER_FILE"+Tab+
                        "TIME"+Tab+"DELAY_BEFORE"+Tab+"SCORE_BEFORE"+Tab+
                        "KEY_UNPRESS_PERCENT_BEFORE"+Tab+"LEVEL_BEFORE"+Tab+
                        "SWITCH_TO"+Tab+
                        "TIME_AFTER"+Tab+"DELAY_AFTER"+Tab+"SCORE_AFTER"+Tab+
                        "KEY_UNPRESS_PERCENT_AFTER"+Tab+"LEVEL_AFTER"
                        ;
                

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
