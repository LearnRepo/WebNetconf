package horizon.app.broker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import com.jcraft.jsch.*;

public class SSHNetconf {

	public static void main_3(String[] args) {
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
        
        String command3 = "<rpc>\n<close-session/>\n</rpc>\n]]>]]>\n";
        
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
            while(true){
              while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                System.out.print(new String(tmp, 0, i));
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
            }
            
            tmp=new byte[1024];
            while(true){
              while(err.available()>0){
                int i=err.read(tmp, 0, 1024);
                if(i<0)break;
                System.out.print(new String(tmp, 0, i));
              }
              if(subsystem.isClosed()){
                if(err.available()>0) continue; 
                System.out.println("exit-status: "+subsystem.getExitStatus());
                break;
              }
              try{
            	  Thread.sleep(1000);
            	  }
              catch(Exception ee){
            	  
              }
            }
            out.write(command3.getBytes());
            
        } catch (JSchException |  IOException e) {
            e.printStackTrace();
        }
        finally{
        	subsystem.disconnect();
            session.disconnect();
        }

	}

}
