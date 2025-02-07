package xperience;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class XPerienceTestClient {
    public static void main(String[] args) {
        String address = "127.0.0.1"; //server IP localhost
        int port = 3000; //port number where server is running

        try (Socket socket = new Socket(address, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Welcome to XPerience.");
            System.out.println("Enter event in format: <Name>#<Date>#<Time>#<Description>#");

            while(true){
                String input = scanner.nextLine().trim();
                if(input.equals("exit")){
                    System.out.println("Thank you for using XPerience. Closing connection");
                    break;
                }
                    out.println(input);
                    String response = in.readLine();
                    System.out.println(response);
            }

        } catch (IOException e) {
            System.err.println("Unable to connect to server");
            e.printStackTrace();
        }
    }
}
