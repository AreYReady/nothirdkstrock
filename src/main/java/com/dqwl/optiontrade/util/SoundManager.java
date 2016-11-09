package com.dqwl.optiontrade.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.dqwl.optiontrade.R;


/**
 * 播放声音管理器
 */
public final class SoundManager {
    private static final int CALL_PUT = R.raw.call_put_voice;
    private static final int LOSS = R.raw.loss_voice;
    private static final int WIN = R.raw.win_voice;
    private static Context mContext;
    private static SoundPool soundPool = null;
    private static SparseIntArray soundPoolMap = null;
    private static float volume = 1.0F;

    private static int getForPhoneRingerMode() {
        int i = 2;
        if (mContext != null) {
            i = ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE)).getRingerMode();
        }
        return i;
    }

    public static void initSoundManager(Context paramContext) {
        mContext = paramContext;
        if ((soundPool == null) || (soundPoolMap == null)) {
            soundPool = new SoundPool(2, 3, 100);
            soundPoolMap = new SparseIntArray(3);
            soundPoolMap.put(CALL_PUT, soundPool.load(paramContext, CALL_PUT, 1));
            soundPoolMap.put(LOSS, soundPool.load(paramContext, LOSS, 2));
            soundPoolMap.put(WIN, soundPool.load(paramContext, WIN, 3));
        }
    }

    public static void playCallPutSound() {
        if ((soundPool != null) && (soundPoolMap != null) && (getForPhoneRingerMode() == 2)) {
            soundPool.play(soundPoolMap.get(CALL_PUT), volume, volume, 1, 0, 1.0F);
        }
    }

    public static void playLostSound() {
        if ((soundPool != null) && (soundPoolMap != null) && (getForPhoneRingerMode() == 2)) {
            soundPool.play(soundPoolMap.get(LOSS), volume, volume, 1, 0, 1.0F);
        }
    }

    public static void playWonSound() {
        if ((soundPool != null) && (soundPoolMap != null) && (getForPhoneRingerMode() == 2)) {
            soundPool.play(soundPoolMap.get(WIN), volume, volume, 1, 0, 1.0F);
        }
    }

    public static void releaseResources() {
        if (soundPool != null) {
            soundPool.release();
        }
        soundPool = null;
        soundPoolMap = null;
    }
}