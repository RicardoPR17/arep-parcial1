package com.java.arep.parcial1;

import java.net.*;
import java.io.*;

public class FacadeHttpServer {

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;

        while (running) {

            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            String outputLine = "";
            boolean firstLine = true;
            String uriString = "";
            while ((inputLine = in.readLine()) != null) {
                if (firstLine) {
                    uriString = inputLine.split(" ")[1];
                    System.out.println("Recibí: " + inputLine);
                    break;
                }
                if (!in.ready()) {
                    break;
                }
            }
            URI uri = new URI("http://localhost" + uriString);

            String query = uri.getQuery();

            if (query != null) {
                if (uri.getPath().contains("computar")) {
                    String result = HttpConnection.main(query);
                    if (result.contains("404")) {
                        outputLine = "HTTP/1.1 404 Not Found\r\n"
                                + "Content-Type: text/html\r\n"
                                + "\r\n"
                                + HttpConnection.main(query);
                    } else {
                        outputLine = "HTTP/1.1 200 OK\r\n"
                                + "Content-Type: text/html\r\n"
                                + "\r\n"
                                + HttpConnection.main(query);
                    }
                }
            } else if (uri.getPath().contains("calculadora")) {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>\r\n" + //
                        "<html>\r\n" + //
                        "\r\n" + //
                        "<head>\r\n" + //
                        "    <title>Calculadora Reflexiva y QuickSort</title>\r\n" + //
                        "    <meta charset=\"UTF-8\">\r\n" + //
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                        "</head>\r\n" + //
                        "\r\n" + //
                        "<body>\r\n" + //
                        "    <h1>Form for QuickSort or Math method</h1>\r\n" + //
                        "    <form action=\"/hello\">\r\n" + //
                        "        <label for=\"name\">Name:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"name\" name=\"name\"><br><br>\r\n" + //
                        "        <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\r\n" + //
                        "    </form>\r\n" + //
                        "    <div id=\"getrespmsg\"></div>\r\n" + //
                        "\r\n" + //
                        "    <script>\r\n" + //
                        "        function loadGetMsg() {\r\n" + //
                        "            let nameVar = document.getElementById(\"name\").value;\r\n" + //
                        "            const xhttp = new XMLHttpRequest();\r\n" + //
                        "            xhttp.onload = function () {\r\n" + //
                        "                document.getElementById(\"getrespmsg\").innerHTML = this.responseText;\r\n" + //
                        "            }\r\n" + //
                        "            xhttp.open(\"GET\", \"/computar?comando=\" + nameVar);\r\n" + //
                        "            xhttp.send();\r\n" + //
                        "        }\r\n" + //
                        "    </script>\r\n" + //
                        "</body>\r\n" + //
                        "</html>";
            } else {
                outputLine = "HTTP/1.1 404 Not Found\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n" +
                        "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<title>Error</title>\n" +
                        "</head>" +
                        "<body>" +
                        "<h1>Recurso no encontrado</h1>" +
                        "</body>" +
                        "</html>";
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}