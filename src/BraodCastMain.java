import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BraodCastMain extends Thread  {
    ArrayList<Socket> clients=new ArrayList<>();
    int nbClient=0;
    public static void main(String[] args) throws Exception{
        new BraodCastMain().start();
    }

    @Override
    public void run(){
        System.out.println("DEMARRAGE DU SERVEUR ....");
        try {
            ServerSocket ss = new ServerSocket(1234);
            while (true){
                Socket socket=ss.accept();
                clients.add(socket);
                nbClient ++;
                Conversation conversation=new Conversation(socket,nbClient);
                conversation.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true){
            System.out.println("");

        }
    }

    private class Conversation extends Thread{
        private Socket socket;
        private int numClient;
        public Conversation(Socket socket, int numClient) {
            super();
            this.socket=socket;
            this.numClient=numClient;
        }

        @Override
        public void run(){
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader br= new BufferedReader(isr);

                OutputStream os=socket.getOutputStream();
                PrintWriter pw=new PrintWriter(os,true);

                System.out.println("Connexion du client numéro "+numClient);
                String IP=socket.getRemoteSocketAddress().toString();
                pw.println("Connexion du client numéro "+numClient+" avec pour address IP "+IP);

                while (true){
                    String req=br.readLine();
                    if (req!=null){
                        System.out.println("Le client numero "+numClient+" a envoyé '"+req+"'.");
                        int lonChar=req.length();
                        //pw.println("Longueur : "+lonChar);
                        braodCastMsg(req,numClient,socket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void braodCastMsg(String msg, int numClient, Socket socket) throws IOException {
            //System.out.println(msg);
            for (Socket client : clients){
                if(socket!=client){
                    OutputStream os = client.getOutputStream();
                    PrintWriter pw = new PrintWriter(os, true);
                    pw.println("Client num " + numClient + " : " + msg);                }
            }
        }
    }
}