import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BraodCastMain_ extends Thread  {
    ArrayList<Socket> clients=new ArrayList<>();
    //ArrayList<String> usersNames=new ArrayList<>();
    Map<String,Socket> usersNames=new HashMap<>();
    int nbClient=0;
    public static void main(String[] args) throws Exception{
        new BraodCastMain_().start();
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
                pw.println("Nom d'utilisateur : ");
                String userName= br.readLine();
                usersNames.put(userName,socket);
                String menu="1-Liste des utilisateurs connectés.\n2-Message ciblé.\n3-Diffusion.";
                pw.println(menu);

                while (true){
                    String req=br.readLine();
                    switch (Integer.valueOf(req)){
                        case 1:
                            for(Map.Entry name:usersNames.entrySet())
                                pw.println(name.getKey());
                            System.out.println("Le client "+userName+" a demandé le menu.");
                            break;
                        case 2:
                            pw.println("Entez le nom du destinataire : ");
                            String nom= br.readLine();
                            pw.println("Entrez votre message : ");
                            String msg= br.readLine();
                            monoCastMsg(nom,userName,msg);
                            System.out.println("Le client "+userName+" a envoyé '"+msg+"' au client "+nom);
                            break;
                        case 3:
                            pw.println("Entrez votre message : ");
                            String brCMsg= br.readLine();
                            System.out.println("Le client "+userName+" a diffusé '"+brCMsg+"'.");
                            braodCastMsg(brCMsg, userName, socket);
                            break;
                        default:
                            pw.println("Vous avez entré un mauvais numéro ...");
                            pw.println(menu);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void braodCastMsg(String msg, String userName, Socket socket) throws IOException {
            //System.out.println(msg);
            for (Socket client : clients){
                if(socket!=client){
                    OutputStream os = client.getOutputStream();
                    PrintWriter pw = new PrintWriter(os, true);
                    pw.println(userName + " : " + msg);
                }
            }
        }

        private void monoCastMsg(String nom, String userName, String msg) throws Exception {
            for (Map.Entry name:usersNames.entrySet()){
                String nn= (String) name.getKey();
                if (nom==nn) {
                    Socket ss = (Socket) name.getValue();
                    OutputStream os = ss.getOutputStream();
                    PrintWriter pw = new PrintWriter(os, true);
                    pw.println(userName + " : " + msg);
                }
            }
        }
    }
}