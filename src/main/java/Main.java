import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        try (ServerSocket serverSocket = new ServerSocket(8989);) { // стартуем сервер один(!) раз
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream())
                ) {
                    System.out.println(engine.search("блокчейн"));
                    String request = in.readLine();
                    String answer = engine.search(request).toString();
                    out.println(answer);
                    // здесь создайте сервер, который отвечал бы на нужные запросы
                    // слушать он должен порт 8989
                    // отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}