package com.dqwl.optiontrade.util.SSLSOCKET;

import android.util.Log;

import com.dqwl.optiontrade.util.SocketUtil;
import com.dqwl.optiontrade.util.SystemUtil;

import java.nio.ByteBuffer;

/**
 * @author xjunda
 * @date 2016-09-01
 */
public class SSLEncodeImp implements Encoder<String> {
    @Override
    public void encode(String value, ByteBuffer buffer) throws EncoderException {
        Log.i(SystemUtil.getTAG(this.getClass()), "encode: Encoding request: " + value );
//        Log.i("123", "encode: buffer: " +buffer.remaining() + " strLengh" + value.length());
        synchronized (this){
            buffer.clear();
            buffer.put(SocketUtil.writePureByte(value));
        }
    }
}

