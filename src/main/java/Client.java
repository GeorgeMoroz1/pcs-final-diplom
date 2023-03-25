import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String... args) {
        final String HOST = "localhost";
        final int PORT = 8989;

        try (Socket socket = new Socket(HOST, PORT)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("Выявить заинтересованность в проекте ключевых стейкхолдеров");
            System.out.println(reader.readLine());
            while (reader.ready()) {
                System.out.println(reader.readLine());
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }
}
