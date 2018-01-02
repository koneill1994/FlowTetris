/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TetrisCode;

//import static TetrisCode.Tetris.PersistentDelay;
//no idea why this is here or what its for
// NetBeans could be a bit more explicit about what it does and why

/**
 *
 * @author Kevin
 */
public class FlowControlSystem {
    
    char Tab = ',';
    int PreviousScoreSystemControl;
    Boolean PlayerControl = true;
    ControlSystemLogFile CSLF = new ControlSystemLogFile();
    
    long LastDelayControlSwitchTime = 0;
    long PersistentDelay; // FCS keeps its own persistent delay value
    // which is sent to Tetris.java to order the new delay
    
    int FCS_score;
    long FCS_speed; // grab our own copies of these
    // so we can just set them without worrying about functions returning them
    
    private void W(String s){
        System.out.println(s);
    }
    
        private void LogSystemChange(String s){
            CSLF.OutputData(s);
        }       
    // <A>
        // will set the delay and score based on the level
        private void SetDelaySystemControl(long delay, int PreviousScoreSystemControl){
            
            //PreviousScoreSystemControl
            
            // set speed to either the corresponding speed for the given score
            // bounded by zero and the maximum level
            FCS_speed = (long) Math.max(Math.min(Math.floor(FCS_score/100.0),Parameters.MaxLevels),0);            
            
            // only decrease speed, never increase it
            if(FCS_score<PreviousScoreSystemControl){
                PersistentDelay = (long) (GetDelayFromLevel(FCS_speed) + GetIterationDelay() * (FCS_score%100)/100.0);
                // the delay for the given level + the delay corresponding to the progress towards the next level
            }
            else{
                PersistentDelay = delay;
            }
            
            PreviousScoreSystemControl=FCS_score;
            
            FCS_speed = GetLevelFromDelay(PersistentDelay);
        }
        
        // level_delay : the normal delay that would be in place for a given level
        // iteration_delay : the difference between level_delay's for adjacent levels (assuming the level-to-delay function is linear
        
        
        // will raise or lower the delay and score based on the keypress percentage 
        // will change level up or down if at an extreme end
        // DEPRECATED as of current experimental design
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
        
        // Raise or lower the delay based on foot pedal input
        // currently input is set to the minus key ["-"], VK_MINUS
        private void SetDelayFootPedal(long delay){
            //Tetris.MinusKeyPressed
            //The analogy is of an accelerator in a car
            // but for simplicity's sake we'll start off with a linear relationship
            // instead of a second-order one such as would be implied by "acceleration"

            long accel_interval = 10; // amount to increase fall speed by every tick that pedal is pressed
            long accel_friction = 5;  // amount to decrease fall speed by every tick that pedal is not pressed
            
            if(Tetris.MinusKeyPressed){
                PersistentDelay = delay - accel_interval; 
            }
            else{
                PersistentDelay = delay + accel_friction;
            }
            
            FCS_speed = GetLevelFromDelay(PersistentDelay);
            
        }
        
        private long GetDelayFromLevel(long level){
            int minD = Parameters.MinimumDelayMilliseconds;
            int maxD = 1000; // maximum delay is set to 1000 milliseconds for now
            
            return (long) ((maxD-minD)*(1.0-1.0*level/Parameters.MaxLevels)+minD);
        }
        
        private long GetLevelFromDelay(long delay){
            int minD = Parameters.MinimumDelayMilliseconds;
            int maxD = 1000; // maximum delay is set to 1000 milliseconds for now
            
            return (long) (Parameters.MaxLevels * (1.0 - (((float) (delay - minD))/((float) (maxD-minD)))));
        }
        
        // difference in milliseconds in delay between two adjacent levels
        // i.e. the m in y=mx+b for the function that maps speed/level to delay
        // (technically returns -m; m is negative because level and delay have an inverse relation)
        private long GetIterationDelay(){
            int minD = Parameters.MinimumDelayMilliseconds;
            int maxD = 1000; // maximum delay is set to 1000 milliseconds for now
            
            return (long) ((maxD-minD)/(Parameters.MaxLevels-1.0));
        }
        
        public void ControlSystem(Double UnpressPercent, int score, long speed, long CurrentTime){
            //if(UnpressPercent != null){
                //if(System.nanoTime() - LastDelayControlSwitchTime > 50 * 1000000){ //50 ms
                    //long last_delay = PersistentDelay;
                    //long last_score = score;
                    //double last_kppercent = UnpressPercent;
                    //long last_level = speed;
                    
                    FCS_score=score;
                    FCS_speed=speed;
                    
                    SetDelayFootPedal(PersistentDelay);
                    SetDelaySystemControl(PersistentDelay, PreviousScoreSystemControl);
                    
                    ConsoleLogStatus(FCS_score, FCS_speed, CurrentTime, PersistentDelay);
                    
                    SetTetrisValues(FCS_score, FCS_speed, PersistentDelay);
                    
                    /*
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
                    */
                    
                //}
            //}
        }     
 
        
        // FCS.ControlSystem(UnpressPercent, LastDelayControlSwitchTime, score, speed, CurrentTime());

        private void ConsoleLogStatus(int score, long speed, long time, long delay){
            W("FCS status at "+time+" ms"); //current time is in ms
            W("  score: "+score);
            W("  level: "+speed);
            W("  delay: "+delay);
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
