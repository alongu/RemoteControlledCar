package guterman.alon.remotecontrolledcar;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by alongu on 09/12/2015.
 */
public class TCPClient {
    public  String ServerIP; //your computer IP address
    public  int ServerPort;
    private Socket socket;
    public boolean isReady = false;
    public String outputMessage;

    public PrintWriter out;
    public BufferedReader in;

    public void SendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
            outputMessage = message;
        }
    }

    public TCPClient(String serverIP, int serverPort){
        this.ServerIP = serverIP;
        this.ServerPort = serverPort;
    }
/*
    public String ReceiveMessage(){
        String serverMessage;
        try{
            if (in != null){
                while ((serverMessage = in.readLine()) != null)
                {
                        serverMessage
                        return "1";
                    }
            }
        }
        catch (IOException ioEx){

        }
        catch (Exception ex){

        }
        return "1";
    }
*/
    public void StopTCPClient(WriteToBTDeviceTimer writeToBTDeviceTimer) throws IOException, InterruptedException {
        if (socket!=null){
            writeToBTDeviceTimer.stoptimertask();
            SendMessage("Q");
            Thread.sleep(5000);
            socket.close();
        }
    }

    public void Init() {

        try {
            isReady = true;
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(ServerIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            socket = new Socket(serverAddr, ServerPort);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP Client", "C: Sent.");

                Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                SendMessage("Communication Started");

                isReady = true;

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);
        }

    }

}
