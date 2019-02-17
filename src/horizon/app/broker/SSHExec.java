package horizon.app.broker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.jcraft.jsch.*;

public class SSHExec {

	public static void main_2(String[] args) {
		// TODO Auto-generated method stub
		String user = "diag";
        String password = "ciena123";
        String host = "10.33.85.198";
        int port = 830;
        String command = "show flow-points";
        Session session = null;
        Channel execChannel = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Establishing Connection...");
            session.connect();
            System.out.println("Connection established.");
            System.out.println("Executing command");

            execChannel = session.openChannel("exec");
            ((ChannelExec)execChannel).setCommand(command);

            // X Forwarding
            // channel.setXForwarding(true);

            //channel.setInputStream(System.in);
            execChannel.setInputStream(null);

            //channel.setOutputStream(System.out);

            //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
            //((ChannelExec)channel).setErrStream(fos);
            ((ChannelExec)execChannel).setErrStream(System.err);

            InputStream in=execChannel.getInputStream();

            execChannel.connect();

            byte[] tmp=new byte[1024];
            while(true){
              while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                System.out.print(new String(tmp, 0, i));
              }
              if(execChannel.isClosed()){
                if(in.available()>0) continue; 
                System.out.println("exit-status: "+execChannel.getExitStatus());
                break;
              }
              try{Thread.sleep(1000);}catch(Exception ee){}
            }
            
        } catch (JSchException |  IOException e) {
            e.printStackTrace();
        }
        finally{
        	execChannel.disconnect();
            session.disconnect();
        }

	}

}
