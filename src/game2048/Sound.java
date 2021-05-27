package game2048;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    private Clip clip;
    
    public Sound(File path){
        try{
            AudioInputStream ais;
            ais = AudioSystem.getAudioInputStream(path);
            AudioFormat baseFormat = ais.getFormat();
            AudioFormat decodeFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels()*2,
                    baseFormat.getSampleRate(),
                    false
            );
            AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
            clip = AudioSystem.getClip(); 
            clip.open(dais);
        }catch(Exception e){}
    }
    
    public void play(){
        if(clip !=null){
            stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void stop(){
        if(clip.isRunning()) clip.stop();
    }
    
    public void close(){
        clip.close();
    }
    
    //public void setVolume(boolean isOn) {
	//boolean on = isOn;
	//for(Clip c : assets.values()) {
            //FloatControl volume = (FloatControl)c.getControl(FloatControl.Type.MASTER_GAIN);
            //if(on) volume.setValue(0);
            //else volume.setValue(volume.getMinimum());
	//}
    //}
}
