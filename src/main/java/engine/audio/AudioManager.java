package engine.audio;

import engine.scene.SceneManager;
import org.apache.commons.io.IOUtils;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.ALC10.*;

public class AudioManager {

    private static long device;

    private static long context;

    private static List<Integer> soundBuffers = new ArrayList<>();

    public static void init(){
        device = alcOpenDevice((ByteBuffer) null);
        if (device == -1) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer) null);
        if (context == -1) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    public static void setListenerData(){
        Vector3f pos = SceneManager.loadedScene.camera.getPosition();
        AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }

    public static int loadSound(String file) throws UnsupportedAudioFileException, IOException {
        int p = AL10.alGenBuffers();
        //shortcut finals:
        final int MONO = 1, STEREO = 2;

        AudioInputStream stream = null;
        stream = AudioSystem.getAudioInputStream(new File(file));

        AudioFormat format = stream.getFormat();
        if(format.isBigEndian()) throw new UnsupportedAudioFileException("Can't handle Big Endian formats yet");

        //load stream into byte buffer
        int openALFormat = -1;
        switch (format.getChannels()) {
            case MONO -> {
                switch (format.getSampleSizeInBits()) {
                    case 8 -> openALFormat = AL10.AL_FORMAT_MONO8;
                    case 16 -> openALFormat = AL10.AL_FORMAT_MONO16;
                }
            }
            case STEREO -> {
                openALFormat = switch (format.getSampleSizeInBits()) {
                    case 8 -> AL10.AL_FORMAT_STEREO8;
                    case 16 -> AL10.AL_FORMAT_STEREO16;
                    default -> openALFormat;
                };
            }
        }

        //load data into a byte buffer
        //I've elected to use IOUtils from Apache Commons here, but the core
        //notion is to load the entire stream into the byte array--you can
        //do this however you would like.
        byte[] b = IOUtils.toByteArray(stream);
        ByteBuffer data = BufferUtils.createByteBuffer(b.length).put(b);
        data.flip();

        //load audio data into appropriate system space....
        AL10.alBufferData(p, openALFormat, data, (int)format.getSampleRate());

        //and return the rough notion of length for the audio stream!
        return p;

    }

    public static void cleanUp(){
        soundBuffers.forEach(AL10::alDeleteBuffers);
        alcDestroyContext(context);
    }
}
