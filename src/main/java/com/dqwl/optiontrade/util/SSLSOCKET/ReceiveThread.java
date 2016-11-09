package com.dqwl.optiontrade.util.SSLSOCKET;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.nio.CharBuffer;

class ReceiveThread extends Thread{
        private Socket socket;
          
        public ReceiveThread(Socket socket) {  
            this.socket = socket;  
        }  
  
        @Override  
        public void run() {  
            while(true){  
                try {                     
                    Reader reader = new InputStreamReader(socket.getInputStream());
                    CharBuffer charBuffer = CharBuffer.allocate(8192);
                    int index = -1;  
                    while((index=reader.read(charBuffer))!=-1){  
                        charBuffer.flip();  
                        System.out.println("client:"+charBuffer.toString());  
                    }  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  