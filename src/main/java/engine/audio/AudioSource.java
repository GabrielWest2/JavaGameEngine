package engine.audio;

import org.lwjgl.openal.AL10;


public class AudioSource {
    private int id;

    public AudioSource(){
        id = AL10.alGenSources();
        AL10.alSourcef(id, AL10.AL_GAIN, 1);
        AL10.alSourcef(id, AL10.AL_PITCH, 1);
        AL10.alSource3f(id, AL10.AL_POSITION, 0, 0, 0);
    }

    public void play(int buffer){
        AL10.alSourcei(id, AL10.AL_BUFFER, buffer);
        AL10.alSourcePlay(id);
    }
}
