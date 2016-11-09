package com.dqwl.optiontrade.util.SSLSOCKET;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

class SendThread extends Thread{
        private Socket socket;
        public SendThread(Socket socket) {  
            this.socket = socket;  
        }  
        @Override  
        public void run() {  
            while(true){  
                try {  
                    String send = getSend();              
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    pw.write(send);  
                    pw.flush();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        public String getSend() throws InterruptedException{  
            Thread.sleep(1000);  
            return "<SOAP-ENV:Envelope>"+System.currentTimeMillis()+"</SOAP-ENV:Envelope>";  
        }  
    }  