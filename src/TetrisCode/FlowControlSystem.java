/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TetrisCode;

/**
 *
 * @author Kevin
 */
public class FlowControlSystem {
    
    char Tab = ',';
    long PreviousDelaySystemControl = Parameters.MaximumDelayMilliseconds; //maxD in ms
    Boolean PlayerControl = true;
    ControlSystemLogFile CSLF = new ControlSystemLogFile();
    
    long LastDelayControlSwitchTime = 0;
    long PersistentDelay = Parameters.MaximumDelayMilliseconds; // FCS keeps its own persistent delay value
    // which is sent to Tetris.java to order the new delay
    
    int FCS_score;
    long FCS_speed; // grab our own copies of these
    // so we can just set them without worrying about functions returning them
    
    
    int minD = Parameters.MinimumDelayMilliseconds;
    int maxD = Parameters.MaximumDelayMilliseconds;
    
    int maxL = Parameters.MaxLevels;
    int minL = Parameters.MinLevels;
    
    // ld for "Level to Delay transfer function"
    double ld_slope = (double) (maxL-minL)/(minD-maxD);
    double ld_intercept = 0.5 * (maxL + minL - ld_slope*(minD+maxD));
    
    private void W(String s){
        System.out.println(s);
    }
    
        private void LogSystemChange(String s){
            CSLF.OutputData(s);
        }       
        
        // set game variables based on a modified version of vanilla tetris
        // as described in http://colinfahey.com/tetris/tetris.html
        // it works but the new level only kicks in after a new piece drops
        public void SetVarsVanillaTetris(int linesCompleted, int score){
            if (linesCompleted <=0){
                FCS_speed = minL;
            }
            else if ((linesCompleted >= 1) && (linesCompleted <= 90)){
                FCS_speed = minL + linesCompleted*(maxL-minL)/91;
            }
            else if (linesCompleted >=91){
                FCS_speed = maxL;
            }
            PersistentDelay = GetDelayFromLevel(FCS_speed);
            
            SetTetrisValues(score, FCS_speed, PersistentDelay);
 
       }
        
        
        // level_delay : the normal delay that would be in place for a given level
        // iteration_delay : the difference between level_delay's for adjacent levels (assuming the level-to-delay function is linear
        
        
        // will raise or lower the delay and score based on the keypress percentage 
        // will change level up or down if at an extreme end
        // DEPRECATED as of current experimental design
        /*
        private void SetDelayPlayerControl(int score, long delay, long level_delay, long iteration_delay, Double percent, long speed){
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
        */
        
        
        // will set the delay and score based on the level
        private void SetDelaySystemControl(long delay){
            
            //PreviousScoreSystemControl
            
            // set speed to either the corresponding speed for the given score
            // bounded by zero and the maximum level
            FCS_speed = (long) Math.max(Math.min(Math.floor(FCS_score/100.0),Parameters.MaxLevels),Parameters.MinLevels);            
            
            
            long PossibleDelay = (long) (GetDelayFromLevel(FCS_speed) + GetIterationDelay() * (FCS_score%100)/100.0);
            // the delay for the given level + the delay corresponding to the progress towards the next level
            
            // only decrease speed, never increase it
            if(PossibleDelay>PreviousDelaySystemControl) PersistentDelay = PossibleDelay;
            else PersistentDelay = delay;
            
            
            PersistentDelay = BoundDelay(PersistentDelay);
            
            FCS_speed = GetLevelFromDelay(PersistentDelay);
            
        }
        

        
        // Raise or lower the delay based on foot pedal input
        // currently input is set to the dash key ["-"], VK_SUBTRACT
        private void SetDelayFootPedal(long delay){
            //Tetris.MinusKeyPressed
            //The analogy is of an accelerator in a car
            // but for simplicity's sake we'll start off with a linear relationship
            // instead of a second-order one such as would be implied by "acceleration"

            W("delay "+PersistentDelay);
            if(Tetris.MinusKeyPressed){
                W("Minus key pressed");
                PersistentDelay = delay - Parameters.FootPedalAccelInterval; 
            }
            else{
                W("Minus key not pressed");
                PersistentDelay = delay + Parameters.FootPedalFrictionInterval;
            }
            PersistentDelay = BoundDelay(PersistentDelay);
            FCS_speed = GetLevelFromDelay(PersistentDelay);
            
        }
        
        private long BoundDelay(long delay){
            
            if(delay>maxD) return maxD;
            else if(delay<minD) return minD;
            else return delay;            
        }
        
        // better, but now its going to level 10 instead of level 20
        
        private long GetDelayFromLevel(long level){
            
            return (long) Math.floor( (level-ld_intercept) /ld_slope);
        }
        
        private long GetLevelFromDelay(long delay){
            
            return (long) Math.floor( (ld_slope*delay) + ld_intercept);
        }
        
        // difference in milliseconds in delay between two adjacent levels
        // i.e. the m in y=mx+b for the function that maps speed/level to delay
        // (technically returns -m; m is negative because level and delay have an inverse relation)
        private long GetIterationDelay(){
            
            return (long) ((maxD-minD)/(Parameters.MaxLevels-Parameters.MinLevels));
        }
        
        public void ControlSystem(Double UnpressPercent, int score, long speed, long CurrentTime){
            
            FCS_score=score;
            FCS_speed=speed;
            
            //ConsoleLogStatus(FCS_score, FCS_speed, CurrentTime, PersistentDelay, "0");
            
            SetDelayFootPedal(PersistentDelay);
            //ConsoleLogStatus(FCS_score, FCS_speed, CurrentTime, PersistentDelay, "1");
            
            // why is the delay going down without pressing vk_minus?
            SetDelaySystemControl(PersistentDelay);
            //ConsoleLogStatus(FCS_score, FCS_speed, CurrentTime, PersistentDelay, "2");
            
            PreviousDelaySystemControl=PersistentDelay;

            
            SetTetrisValues(FCS_score, FCS_speed, PersistentDelay);
            
        }
 
        
        private void ConsoleLogStatus(int score, long speed, long time, long delay, String marker){
            W(marker+":");
            W("FCS status at "+time+" ms"); //current time is in ms
            W("  score: "+score);
            W("  level: "+speed);
            W("  delay: "+delay);
            //W("    ld_slope: "+ld_slope);
            //W("    ld_intercept: "+ld_intercept);
        }
        
        private void SetTetrisValues(int score2, long speed, long delay){
            Tetris.score = score2;  
            Tetris.PersistentDelay = delay;
            Tetris.speed=speed;
            
            // where the HECK is it getting this static thing from
            // nothing is static here
            // what is making it think its static
            
            // I think because I don't have a Tetris object in FCS
            // probably just have to return these values
            // or access them from a method in Tetris
        }
       
        
        //</A>
        
}
