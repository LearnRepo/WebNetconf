package horizon.app.broker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import com.jcraft.jsch.*;

public class SSHNetconf3 {

	public String executeQuery(String host, int port, String user, String password, String query){
		Session session = null;
        ChannelSubsystem subsystem = null;
        long timeout = 60000;
        long prev = System.currentTimeMillis();
        
        String clientHello = "<hello xmlns='urn:ietf:params:xml:ns:netconf:base:1.0'><capabilities><capability>urn:ietf:params:netconf:base:1.0</capability><capability>urn:ietf:params:netconf:base:1.1</capability></capabilities></hello>]]>]]>";
        clientHello = clientHello.replace("'", "\"");
        String closeCommand = "<rpc><close-session/></rpc>]]>]]>";
        query = "<rpc xmlns='urn:ietf:params:xml:ns:netconf:base:1.0' message-id='13'><get><filter type='subtree'><oc-sys:system xmlns:oc-sys='http://openconfig.net/yang/system'><oc-sys:state><oc-sys:current-datetime/></oc-sys:state></oc-sys:system></filter></get></rpc>]]>]]>";
        query = query.replace("'", "\"");
        String rpc_reply = null;
        
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Establishing Connection...");
            session.connect();
            System.out.println("Connection established.");
            System.out.println("Executing command");

            subsystem = (ChannelSubsystem) session.openChannel("subsystem");
            subsystem.setSubsystem("netconf");
            ((ChannelSubsystem)subsystem).setPty(true);


            //channel.setInputStream(System.in);
            //channel.setOutputStream(System.out);
            //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
            //((ChannelExec)channel).setErrStream(fos);
            subsystem.setInputStream(null);
            subsystem.setErrStream(null);

            OutputStream out = subsystem.getOutputStream();
            InputStream in=subsystem.getInputStream();
            InputStream err=subsystem.getErrStream();

            subsystem.connect();

            byte[] tmp=new byte[1024];
            String cachedMessage = "";
            while(true){
              while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                String temp = new String(tmp, 0, i);
                cachedMessage += temp;
                //System.out.print(temp);
              }
               while(err.available()>0){
                  int i=err.read(tmp, 0, 1024);
                  if(i<0)break;
                  String temp = new String(tmp, 0, i);
                  cachedMessage += temp;
                  //System.out.print(new String(tmp, 0, i));
                }
               if(cachedMessage.contains("</hello>]]>]]>"))
               {
            	   System.out.println("shad1:"+cachedMessage);
            	   
            	   cachedMessage = "";
            	   
            	   PrintStream commander = new PrintStream(out, true);
            	   commander.println(clientHello+query);
               }
               if( cachedMessage.contains("</data>\n]]>]]>") ) 
            	{
            	    System.out.println("shad2:"+cachedMessage);
            	   	rpc_reply = cachedMessage;
            	   	
            	   	cachedMessage = "";
            	   	
            	   	PrintStream commander = new PrintStream(out, true);
            	   	commander.println(closeCommand);
            	}
               if(cachedMessage.contains("<ok/>\n</rpc-reply>\n]]>]]>"))
               {
            	   System.out.println("shad3:"+cachedMessage);
            	   subsystem.disconnect();
               }
               long now = System.currentTimeMillis();
               long deltaMillis = now - prev;
               if(subsystem.isClosed() || deltaMillis > timeout){
                if(in.available()>0 || err.available()>0) continue; 
                System.out.println("exit-status: "+subsystem.getExitStatus());
                break;
              }
            }
            
            
        } catch (JSchException |  IOException e) {
            e.printStackTrace();
        }
        finally{
        	subsystem.disconnect();
            session.disconnect();
        }
		
		return rpc_reply;
	}
	
	public String executeQuery(String host,  String user, String password, String query){
		int port = 830;
		return this.executeQuery(host, port, user, password, query);
	}

}
