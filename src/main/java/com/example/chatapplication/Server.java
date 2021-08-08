package com.example.chatapplication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class Server {

    private static ArrayList<String> users = new ArrayList<>();
    private static ArrayList<MessagingThread> clients = new ArrayList<>();
    public static void main(String[] args) throws IOException, SQLException {

        ServerSocket serverSocket = new ServerSocket(8081,10);
        System.out.println("Server Started\n");

        DbOperations.createUserTable("Users");
        DbOperations.createChatTable("Chat_Backup");


        while (true)
        {
            Socket clientSocket = serverSocket.accept();
            MessagingThread thread = new MessagingThread(clientSocket);
            clients.add(thread);
            thread.start();
        }
    }
    public static void brodcast(String user_name, String message)
    {
        for (MessagingThread client : clients)
        {
            if (client.user_name.equals(user_name))
            {
                client.SendToMe(message);
            }
            else
            {
                client.SendToOthers(user_name,message);
            }
        }
    }
    static class MessagingThread extends Thread{

        BufferedReader input;
        PrintWriter output;
        String user_name;

        public MessagingThread(Socket clientSocket) throws IOException, SQLException {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(),true);

            user_name = input.readLine();

            users.add(user_name);
            DbOperations.addUserInDb(user_name);

        }

        public void SendToMe(String message)
        {
            output.println("Me : " + message);
        }

        public void SendToOthers(String user_name, String message)
        {
            output.println(user_name + " : " + message);
        }

        public void saveInDb(String user_name, String message) throws SQLException {
            String id = user_name + " -> " + "100";
            DbOperations.saveMessage(id,user_name,message);
        }

        @Override
        public void run() {

            String line;

            try{

                while (true)
                {
                    line = input.readLine();
                    if (line.equals("end"))
                    {
                        users.remove(user_name);
                        clients.remove(this);
                        break;
                    }
                    else
                    {
                        brodcast(user_name,line);
                        saveInDb(user_name,line);
                    }
                }

            }catch (Exception exception){
                System.out.println(exception.getMessage());

            }
        }
    }

}
