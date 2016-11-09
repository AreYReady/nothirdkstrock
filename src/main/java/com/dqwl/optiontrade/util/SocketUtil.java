package com.dqwl.optiontrade.util;

import android.content.Context;
import android.os.HandlerThread;
import android.util.Log;

import com.dqwl.optiontrade.BuildConfig;
import com.dqwl.optiontrade.bean.BeanUserLoginLogin;
import com.dqwl.optiontrade.handler.HandlerWrite;
import com.dqwl.optiontrade.constant.ServerIP;
import com.dqwl.optiontrade.util.SSLSOCKET.Decoder;
import com.dqwl.optiontrade.util.SSLSOCKET.Encoder;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLDecoderImp;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLEncodeImp;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * @author xjunda
 * @date 2016-07-18
 */
public class SocketUtil {
    /**
     * 校验值
     *
     * @param data 返回的message
     * @return
     */
    private static int getCheckSum(String data) {
        int checkSum = 0;
        for (int i = 0; i < data.length(); i++) {
            checkSum += (int) data.charAt(i);
        }
        checkSum = checkSum % 256;
        return checkSum;
    }

    public static int getCheck(char[] data) {
        int checkSum = 0;
        for (int i = 0; i < data.length; i++) {
            checkSum += getUnsignedIntt((int) data[i]);
        }
        checkSum = getUnsignedIntt(checkSum % 256);
        return checkSum;
    }

    public static int getUnSignedCheckSum(String data) {
        int checkSum = 0;
        char[] checkc = data.toCharArray();
        for (int i = 0; i < data.length(); i++) {
            checkSum += getUnsignedIntt((int) checkc[i]);
        }
        checkSum = getUnsignedIntt(checkSum % 256);
        return checkSum;
    }

    /**
     * 根据字节流算出checksum，可用
     *
     * @param data
     * @return
     */
    public static int getCheckSum(byte[] data) {
        int checkSum = 0;
        for (int i = 0; i < data.length; i++) {
            checkSum += getUnsignedIntt((int) data[i]);
        }
        checkSum = getUnsignedIntt(checkSum % 256);
        return checkSum;
    }

    private static int getUnsignedIntt(int data) {     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data & 0x0FFFFFFFF;
    }

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] objectToByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object byteToObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    /**
     * int到byte[]
     *
     * @param i
     * @return
     */
    private static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * byte[]转int
     *
     * @param bytes
     * @return
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    // char转byte
    public static byte[] charToBytes(char[] chars) {
        Charset cs = Charset.forName("GB2312");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);

        return bb.array();
    }

    // byte转char
    public static char[] byteToChars(byte[] bytes) {
        Charset cs = Charset.forName("GB2312");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    //java 合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    //java 合并3个byte数组
    private static byte[] byteMerger3(byte[] byte_1, byte[] byte_2, byte[] byte_3) {
        byte[] byteFinal = new byte[byte_1.length + byte_2.length + byte_3.length];
        System.arraycopy(byte_1, 0, byteFinal, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byteFinal, byte_1.length, byte_2.length);
        System.arraycopy(byte_3, 0, byteFinal, byte_1.length + byte_2.length, byte_3.length);
        return byteFinal;
    }

    /**
     * 处理服务器返回的数据
     * @param result
     * @return
     */
    public static String handReceviceData(byte[] result) {
        byte[] checkSum = new byte[4];//校验码
        System.arraycopy(result, result.length - 4, checkSum, 0, 4);
        if(result.length-8<=0){
            return null;
        }
        byte[] byteMessage = new byte[result.length - 8];//消息字节
        System.arraycopy(result, 4, byteMessage, 0, result.length - 8);
        String message = null;//返回的message
        try {
            message = new String(byteMessage, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int serverCheckNum = SocketUtil.byteArrayToInt(checkSum);
        int appCheckNum = SocketUtil.getCheckSum(byteMessage);
//        Log.i("123", "result:::::" + new String(result));
//        Log.i("123", "message: " + message);
//        Log.i("123", "serverCheckSum: " + serverCheckNum + "appCheckNum: " + appCheckNum);
        if (serverCheckNum == appCheckNum) {
            return message;
        }
        return null;
    }

    /**
     * 把字符串转成mina session 可以使用的byte数据
     *
     * @param msg
     * @return
     */
//    public static IoBuffer writeByte(String msg) {
//        int checkSum = 0;
//        int dataLength = 0;
//        byte[] data = null;
//        String send = "";
//        byte[] finalDAta = null;
//        try {
//            data = msg.getBytes("GB2312");
//            dataLength = data.length + 4;
//            checkSum = SocketUtil.getCheckSum(msg);
//            finalDAta = SocketUtil.byteMerger3(SocketUtil.intToByteArray(dataLength), data, SocketUtil.intToByteArray(checkSum));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return IoBuffer.wrap(finalDAta);
//    }

    public static byte[] writePureByte(String msg) {
        int checkSum = 0;
        int dataLength = 0;
        byte[] data = null;
        String send = "";
        byte[] finalDAta = null;
        try {
            data = msg.getBytes("GB2312");
            dataLength = data.length + 4;
            Log.i("123", "writePureByteLenth: " + dataLength);
            checkSum = SocketUtil.getUnSignedCheckSum(msg);
            Log.i("123", "writePureByteCheckSum: " + checkSum);
            finalDAta = SocketUtil.byteMerger3(SocketUtil.intToByteArray(dataLength), data, SocketUtil.intToByteArray(checkSum));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return finalDAta;
    }

    /**
     * 读取流
     *
     * @param inStream
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        int checkLength = -1;
        byte[] lenthBytes = new byte[4];
        if ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
//            if(len<1024){
//                break;
//            }else {
//                System.arraycopy(outSteam.toByteArray(),0, lenthBytes, 0, 4);
//                checkLength = SocketUtil.byteArrayToInt(lenthBytes);
//                if(checkLength+4==outSteam.toByteArray().length){
//                    break;
//                }
//            }
        }
        outSteam.close();
//        inStream.close();
        return outSteam.toByteArray();
    }

    public static byte[] readStreamData(InputStream inputStream) throws Exception {
        String message = "";
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] lenthBytes = new byte[4];//头部信息，长度
        inputStream.read(lenthBytes, 0, 4);
        outSteam.write(lenthBytes);
        int dataLenth = SocketUtil.byteArrayToInt(lenthBytes);//服务器返回的数据长度
        Log.i("123", "lll: " + dataLenth);
        byte[] dataBytes = new byte[dataLenth];
        inputStream.read(dataBytes);
        message = new String(dataBytes);
        outSteam.write(dataBytes);
        Log.i("123", "ssssss: " + dataLenth);
        Log.i("123", "ssssss: " + message);
        outSteam.close();
        return outSteam.toByteArray();
    }

    public int getUnsignedByte(byte data) {      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data & 0x0FF;
    }

    public int getUnsignedByte(short data) {      //将data字节型数据转换为0~65535 (0xFFFF 即 WORD)。
        return data & 0x0FFFF;
    }
    public static SSLSocketChannel<String> connectToServer(Context context, HandlerThread handlerThread,
                                       HandlerWrite handlerWrite) {
        EventBus.getDefault().removeAllStickyEvents();
        SSLSocketChannel<String> sslSocketChannel = null;
        final SocketAddress address = new InetSocketAddress(BuildConfig.API_URL, ServerIP.PORT);
        Encoder<String> encoder = new SSLEncodeImp();
        Decoder<String> decoder = new SSLDecoderImp();
        Log.i("123", "doLogin: Opening channel");
        try {
            sslSocketChannel = SSLSocketChannel.open(address, encoder, decoder, 1024*1024, 1024*1024);
            Log.i("123", "doLogin: Channel opened, initial handshake done");
            Log.i("123", "doLogin: Sending request");
            String [] user = CacheUtil.getUserInfo(context);
            BeanUserLoginLogin userLogin = new BeanUserLoginLogin(Integer.valueOf(user[0]), user[1]);
            String loginStr = new Gson().toJson(userLogin, BeanUserLoginLogin.class);
            sslSocketChannel.send(loginStr);

            Log.i("123", "doLogin: Receiving response");
            handlerWrite.setSSLSocketChannel(sslSocketChannel);
            handlerWrite.sendEmptyMessage(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslSocketChannel;
    }
}