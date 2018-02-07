/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TetrisCode;

/**
 *
 * @author Randy
 */

import java.applet.*;
import java.net.*;
import java.util.Calendar;
import sun.audio.*;

public class Sound {
    
    Calendar Time = null;
    long StartTime = 0;

    URL snd[] = new URL[2];
    AudioStream as;

    /** Creates a new instance of Sound */
    public Sound() {
    }

    public void Play(int i) {
        Applet.newAudioClip(snd[i]).play(); //loop();
        System.out.println("PLAY");
    }

    public void StopPlay(int i) {
        Applet.newAudioClip(snd[i]).stop();
        System.out.println("STOP");
    }

    public void LoadURL(String Wave) {
        try {
            //snd[0] = new URL("file:" + ControlCode.MainDir + 
            //        "/WAVES/" + Wave + ".wav");
            snd[0] = new URL("file:" + 
                    "EXPERIMENT/AUDIO/" + Wave + ".wav");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    void NewPlayer() {
        try {
            as = new AudioStream(snd[0].openStream());
        } catch (Exception ignored)
        {
            System.err.println(ignored);
        }

        AudioPlayer.player.start(as);
    }


    public void StopWave() {
        AudioPlayer.player.stop(as);
    }

    public void PlayWave(String Wave) {

        Time = Calendar.getInstance();

        long SoundTime = Time.getTimeInMillis() - StartTime;

        StartTime = Time.getTimeInMillis();

        //if (SoundTime < 130) return;
        
        StopWave();
        
        System.out.println("Wave="+Wave);
        LoadURL(Wave);
        NewPlayer();
        
    }

}

                    //AudioPlayer.player.stop(as);






