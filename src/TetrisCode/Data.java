package TetrisCode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author rfgreen
 */
public class Data {
    
    String SubjNo = "SUBJ1";
    char Tab = ',';
    boolean FirstWriteToFile = true;
    String ExpCond = "NONE";
    public Data() {
        
    }
    
    public void W(String S) {
        System.out.println(S);
    }
       
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
