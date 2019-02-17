package horizon.app.broker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import com.jcraft.jsch.*;

public class SSHNetconf2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String user = "diag";
        String password = "ciena123";
        String host = "10.33.85.198";
        int port = 830;
        String command = "<get><filter type='subtree'> <mef-fp:fps xmlns:mef-fp='urn:ciena:params:xml:ns:yang:ciena-pn:ciena-mef-fp'/>  </filter></get>";
        
        String command2 = "<hello>\n<capabilities>\n";
        command2 += "<capability>urn:ietf:params:xml:ns:netconf:base:1.0</capability>\n";
        command2 += "<capability>urn:ietf:params:xml:ns:netconf:base:1.0#candidate</capability>\n";
        command2 += "<capability>urn:ietf:params:xml:ns:netconf:base:1.0#confirmed-commit</capability>\n";
        command2 += "<capability>urn:ietf:params:xml:ns:netconf:base:1.0#validate</capability>\n";
        command2 += "<capability>urn:ietf:params:xml:ns:netconf:base:1.0#url?protocol=http,ftp,file</capability>\n";
        command2 += "</capabilities>\n</hello>\n]]>]]>\n";
        
        String command3 = "<rpc>\n<close-session/>\n</rpc>\n]]>]]>";
        
        Session session = null;
        ChannelSubsystem subsystem = null;

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
            System.out.println("loop stream interation begin");
              while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                String temp = new String(tmp, 0, i);
                cachedMessage += temp;
                System.out.print(temp);
              }
               while(err.available()>0){
                  int i=err.read(tmp, 0, 1024);
                  if(i<0)break;
                  System.out.print(new String(tmp, 0, i));
                }
               if(cachedMessage.contains("</hello>]]>]]>"))
               {
            	   cachedMessage = "";
            	   PrintStream commander = new PrintStream(out, true);
            	   commander.println(command3);
               }
               if(cachedMessage.contains("<ok/>\n</rpc-reply>\n]]>]]>"))
               {
            	   subsystem.disconnect();
               }
              if(subsystem.isClosed()){
                if(in.available()>0) continue; 
                System.out.println("exit-status: "+subsystem.getExitStatus());
                break;
              }
              try{
            	  Thread.sleep(1000);
            	  }catch(Exception ee){
            		  
            	  }
              System.out.println("loop stream interation end");
            }
            
            
        } catch (JSchException |  IOException e) {
            e.printStackTrace();
        }
        finally{
        	subsystem.disconnect();
            session.disconnect();
        }

	}

	public String executeQuery(String host, int port, String user, String password, String query){
		Session session = null;
        ChannelSubsystem subsystem = null;
        long timeout = 60000;
        long prev = System.currentTimeMillis();
        
        String clientHello = "<hello xmlns='urn:ietf:params:xml:ns:netconf:base:1.0'>\n<capabilities>\n<capability>urn:ietf:params:netconf:base:1.0</capability>\n<capability>urn:ietf:params:netconf:base:1.1</capability>\n</capabilities></hello>\n]]>]]>";
        String command3 = "<rpc>\n<close-session/>\n</rpc>\n]]>]]>";
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
            	   commander.println(clientHello);
            	   
            	   commander = new PrintStream(out, true);
            	   commander.println(query);
               }
               if( cachedMessage.contains("</data>\n]]>]]>") ) 
            	{
            	    System.out.println("shad2:"+cachedMessage);
            	   	rpc_reply = cachedMessage;
            	   	
            	   	cachedMessage = "";
            	   	
            	   	PrintStream commander = new PrintStream(out, true);
            	   	commander.println(command3);
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
		Session session = null;
        ChannelSubsystem subsystem = null;
        long timeout = 60000;
        long prev = System.currentTimeMillis();
        
        String command3 = "<rpc>\n<close-session/>\n</rpc>\n]]>]]>";
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
            System.out.println("loop stream interation begin");
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
            	   System.out.println(cachedMessage);
            	   cachedMessage = "";
            	   PrintStream commander = new PrintStream(out, true);
            	   commander.println(query);
               }
               if(!(cachedMessage.contains("</hello>]]>]]>") || cachedMessage.contains("<ok/>\n</rpc-reply>\n]]>]]>")))
            	{
            	    System.out.println(cachedMessage);
            	   	rpc_reply = cachedMessage;
            	   	cachedMessage = "";
            	   	PrintStream commander = new PrintStream(out, true);
            	   	commander.println(command3);
            	}
               if(cachedMessage.contains("<ok/>\n</rpc-reply>\n]]>]]>"))
               {
            	   System.out.println(cachedMessage);
            	   subsystem.disconnect();
               }
               
              long now = System.currentTimeMillis();
              long deltaMillis = now - prev;
              if(subsystem.isClosed() || deltaMillis > timeout){
                if(in.available()>0) continue; 
                System.out.println("exit-status: "+subsystem.getExitStatus());
                break;
              }
              try{
            	  Thread.sleep(1000);
            	  }catch(Exception ee){
            		  
            	  }
              System.out.println("loop stream interation end");
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

}
