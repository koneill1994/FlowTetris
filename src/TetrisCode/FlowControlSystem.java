/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TetrisCode;

import static TetrisCode.Tetris.PersistentDelay;

/**
 *
 * @author Kevin
 */
public class FlowControlSystem {
    
    char Tab = ',';
    int PreviousScoreSystemControl;
    Boolean PlayerControl = true;
    ControlSystemLogFile CSLF = new ControlSystemLogFile();
    
    
        private void LogSystemChange(String s){
            CSLF.OutputData(s);
        }       
    // <A>
        // will set the delay and score based on the level
        public void SetDelaySystemControl(int score, long delay, Double percent, int PreviousScoreSystemControl, long speed){
            double extreme =.1;
               
            double usable_percent = percent-(2*extreme);
            double usable_percent_range = 1.0-(2*extreme);
            
            //PreviousScoreSystemControl
            long output = delay;
            
            speed = (long) Math.max(Math.min(Math.floor(score/100.0),Parameters.MaxLevels),0);            
            
            if(score<PreviousScoreSystemControl){
                
                PersistentDelay = (long) (GetDelayFromLevel(speed) + GetIterationDelay() * (score%100)/100.0);
            }
            else{
                PersistentDelay = delay;
            }
            
            PreviousScoreSystemControl=score;
            
            speed = GetLevelFromDelay(PersistentDelay);
        }
        
        // level_delay : the normal delay that would be in place for a given level
        // iteration_delay : the difference between level_delay's for adjacent levels (assuming the level-to-delay function is linear
        
        
        // will set the delay and score based on the keypress percentage 
        // will change level up or down if at an extreme end
        public void SetDelayPlayerControl(int score, long delay, long level_delay, long iteration_delay, Double percent, long speed){
            double extreme =.1;
               
            double usable_percent = percent-(2*extreme);
            double usable_percent_range = 1.0-(2*extreme);
            
            //make sure to validate to make sure levels doesn't go outside bounds
            if(percent<extreme){
                if(speed>0) speed--;
                PersistentDelay=delay;
            }
            else if(percent>(1.0-extreme)){
                if(speed<Parameters.MaxLevels) speed++;
                PersistentDelay=delay;
            }
            else{
                PersistentDelay = level_delay + (iteration_delay* ((long) (usable_percent - (usable_percent_range/2))));
            }
            
            score = (int) (100*(speed + (usable_percent - (usable_percent_range/2))));
            
        }
        
        
        
        public long GetDelayFromLevel(long level){
            int minD = Parameters.MinimumDelayMilliseconds;
            int maxD = 1000; // maximum delay is set to 1000 milliseconds for now
            
            return (long) ((maxD-minD)*(1.0-1.0*level/Parameters.MaxLevels)+minD);
        }
        
        public long GetLevelFromDelay(long delay){
            int minD = Parameters.MinimumDelayMilliseconds;
            int maxD = 1000; // maximum delay is set to 1000 milliseconds for now
            
            return (long) (Parameters.MaxLevels * (1.0 - (((float) (delay - minD))/((float) (maxD-minD)))));
        }
        
        public long GetIterationDelay(){
            int minD = Parameters.MinimumDelayMilliseconds;
            int maxD = 1000; // maximum delay is set to 1000 milliseconds for now
            
            return (long) ((maxD-minD)*(1.0-1.0/Parameters.MaxLevels));
        }
        
        public void ControlSystem(Double UnpressPercent, long LastDelayControlSwitchTime, int score, long speed, long CurrentTime){
            if(UnpressPercent != null){
                if(System.nanoTime() - LastDelayControlSwitchTime > 50 * 1000000){ //50 ms
                    long last_delay = PersistentDelay;
                    long last_score = score;
                    double last_kppercent = UnpressPercent;
                    long last_level = speed;
                    String switchto = PlayerControl ? "Player" : "System";
                    
                    LastDelayControlSwitchTime = System.nanoTime();
                    if(PlayerControl){
                        SetDelayPlayerControl(score, PersistentDelay, GetDelayFromLevel(speed), GetIterationDelay(), UnpressPercent, speed);
                    }
                    else{
                        SetDelaySystemControl(score, PersistentDelay, UnpressPercent, PreviousScoreSystemControl, speed);
                    }
                    LogSystemChange(""+CurrentTime+Tab+last_delay+Tab+last_score+Tab+
                            last_kppercent+Tab+last_level+Tab+switchto+Tab+
                            PersistentDelay+Tab+score+Tab+UnpressPercent+Tab+speed+Tab);
                    
                    
                    
                    PlayerControl=!PlayerControl;
                }
            }
        }     
 
        //</A>
        
}
