
package TetrisCode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Error;

/**
 *
 * @author oneill.45
 */
public class TimeInLevelData {
    
    String Tab = ",";
    
    ArrayList<TimeInLevelSubject> TimeInLevelTable;
    
    ArrayList<Long> critpoints;
    
    private static String FileName = "DATA/" + "TimeInLevelData" + ".csv";
    
    private Long CritPoint_sd_coeff=(long)5;
    private Long CritPoint_constant=(long)10;
    
    
    public class TimeInLevelSubject{
        Long Subject_Number;
        ArrayList<Long> LevelsAvg;
        ArrayList<Long> LevelsCriterion;
        public TimeInLevelSubject(Long Subject_Number, ArrayList<Long> LevelsAvg, ArrayList<Long> LevelsCriterion){
            this.LevelsAvg = LevelsAvg;
            this.LevelsCriterion = LevelsCriterion;   
            this.Subject_Number = Subject_Number;
        }
    }
    
    public TimeInLevelData(){
        
        File TILfile = new File(FileName);
        
        if(!TILfile.exists()){
            CreateNewFile(FileName);
        }
        
        TimeInLevelTable = ReadFile(FileName); // if we had to create it it should still be empty
        
        critpoints = GenerateLevelsCriterion(TimeInLevelTable);
        
    }
    
    //randy's useful print-to-console shortcut function
    public void W(String S) {
        System.out.println(S);
    }
    
    public double Mean(ArrayList<Long> l){
        if(l.size()<=0) return 0;
        Long sum = Long.valueOf(0);
        for(Long val: l){
            sum+=val;
        }
        return sum/l.size();
    }
    
    //mean floating point, since netbeans doesn't like me overloading the function
    public double Meanfp(ArrayList<Double> l){
        double sum = 0;
        for(double val: l){
            sum+=val;
        }
        return sum/l.size();
    }
    
    public double StandardDeviation(ArrayList<Long> l){
        double mean = Mean(l);
        
        ArrayList<Double> sd_list = new ArrayList<Double>();
        
        for(Long val: l){
            sd_list.add(Math.pow((val-mean),2));
        }
        
        return Math.sqrt(Meanfp(sd_list));
    }
    
    public Long CriticalPoint(double mean, double sd, Long sd_coeff, Long constant){
        return (long)(Math.floor(mean+sd*sd_coeff+constant));
    }
    
    public ArrayList<Long> GenerateLevelsCriterion(ArrayList<TimeInLevelSubject> TimeInLevelTable){
        ArrayList<Long> LevelsCriterion = new ArrayList<Long>();
        
        // initialize an ArrayList (TILList) to hold all previous participants' average time in level values
        ArrayList<ArrayList<Long>> TILList = new ArrayList<ArrayList<Long>>();
        for(int i=0; i<Parameters.MaxLevels; i++){
            TILList.add(new ArrayList<Long>());
        }
        
        // populate TILList with all the avg level times of previous participants
        for(TimeInLevelSubject subj: TimeInLevelTable){
            for(int i=0; i<TILList.size(); i++){
                TILList.get(i).add(subj.LevelsAvg.get(i));
            }
        }
        
        // generate the mean and standard deviation for each level
        // and them use them to generate the critical points
        for(ArrayList<Long> l: TILList){
            double mean = Mean(l);
            double sd = StandardDeviation(l);
            Long critp = CriticalPoint(mean, sd, CritPoint_sd_coeff, CritPoint_constant);
            LevelsCriterion.add(critp);
        }
                
        return LevelsCriterion;
    }
    
    // if(!File(Filename).exists());
    public void CreateNewFile(String FileName){
        try{
            FileWriter fileWriter = new FileWriter(FileName,true);
            BufferedWriter BW = new BufferedWriter(fileWriter);
            W("TILData: Parameters.MaxLevels == "+Parameters.MaxLevels);
            
            String Header = "Subject"+Tab;
            for(int i=0; i<=Parameters.MaxLevels; i++){
                Header+="Level_"+i+Tab;
            }
            for(int i=0; i<=Parameters.MaxLevels; i++){
                Header+="CritValue_Level_"+i;
                if(i<Parameters.MaxLevels) Header+=Tab;
            }
            Header+="\r\n";
            
            BW.write(Header);
            BW.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //a function to access member functions from external locations
    // just to keep things squeaky clean
    public void AddSubjectData(Long Subject_Number, ArrayList<Long> LevelsAvg, ArrayList<Long> LevelsCriterion){
        AddLineToFile(FileName, Subject_Number, LevelsAvg, LevelsCriterion);
    }
    
    public void AddLineToFile(String FileName, Long Subject_Number, ArrayList<Long> LevelsAvg, ArrayList<Long> LevelsCriterion){
        try{
            FileWriter fileWriter = new FileWriter(FileName,true);
            BufferedWriter BW = new BufferedWriter(fileWriter);
        
            String Line = ""+Subject_Number+Tab;
            for(Long val : LevelsAvg){
               Line+=""+val+Tab;
            }
            int i = 0; // this is a messy way to do it I know
            for(Long val : LevelsCriterion){
                Line+=""+val;
                if(i<=LevelsCriterion.size())Line+=Tab;
                i++;
            }
            Line+="\r\n";
            
            BW.write(Line);
            BW.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    
    public TimeInLevelSubject InterpretLine(String CurrentLine){
        String[] items = CurrentLine.split(Tab);
        if(items.length!= (1 + 2 * (Parameters.MaxLevels+1))){
            //throw an error if its not the correct number
            // (Subject + num_levels(for level time) + num_levels (for crit point) )
            throw new Error("Not the correct number of columns");
        }
        
        ArrayList<Long> LevelsAvg=new ArrayList<Long>();
        ArrayList<Long> LevelsCriterion=new ArrayList<Long>();
        
        
        for(String val : Arrays.copyOfRange(items, 1, 1+Parameters.MaxLevels)){
            LevelsAvg.add(Long.valueOf(val));
        }
        
        for(String val : Arrays.copyOfRange(items, 1+Parameters.MaxLevels, 1+(2*Parameters.MaxLevels))){
            LevelsCriterion.add(Long.valueOf(val));
        }
        
        return new TimeInLevelSubject(Long.valueOf(items[0]),LevelsAvg,LevelsCriterion);
        
    }
    
    
    public ArrayList<TimeInLevelSubject> ReadFile(String FileName){
        try{
            
            ArrayList<TimeInLevelSubject> table = new ArrayList<TimeInLevelSubject>();
                    
            FileReader fr=new FileReader(FileName);
            BufferedReader br = new BufferedReader(fr);
            
            String CurrentLine;
            
            boolean firstline=true;
            
            while((CurrentLine = br.readLine()) != null){
                //skip the first line because its the header
                if(!firstline) table.add(InterpretLine(CurrentLine));
                else firstline = false;
            }
            
            return table;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        return null;
        
    }
    
    
    
}
