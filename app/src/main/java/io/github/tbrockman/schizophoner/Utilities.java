package io.github.tbrockman.schizophoner;

import java.nio.ByteBuffer;

public class Utilities {

    public static String LOG_TAG = "Schizophoner_v0";

    public static double rms(byte[] bytes) {
        double ret = 0;
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int i = 0; i < bytes.length / 2; i++) {
            short full = bb.getShort();
            ret += (full * full);
        }
        return Math.sqrt(ret * 2 / bytes.length);
    }

    // https://stackoverflow.com/questions/10324355/how-to-convert-16-bit-pcm-audio-byte-array-to-double-or-float-array
    // @mwengler

    public static short[] byteArrayToShort(byte[] bytes) {
        short[] out = new short[bytes.length / 2]; // will drop last byte if odd number
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int i = 0; i < out.length; i++) {
            out[i] = bb.getShort();
        }
        return out;
    }
}
