package com.parcialarep.servicefacade;
import java.net.*;
import java.io.*;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(HttpServer.getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + HttpServer.getPort());
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
            String inputLine, outputLine;
            boolean firstLine = true;
            String body = "";
            String path = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (firstLine) {
                    String[] receive = inputLine.split(" ");
                    path = receive[1];
                    if (path.startsWith("/calculadora")) {
                        body = getForm();
                    } else if (path.startsWith("/cos")) {
                        body = String.valueOf(HttpConnection.getCalculatorService("cos",Double.parseDouble(path.split("=")[1])));
                    } else if (path.startsWith("/sen")){
                        body = String.valueOf(HttpConnection.getCalculatorService("sen",Double.parseDouble(path.split("=")[1])));
                    } else if (path.startsWith("/tan")) {
                        body = String.valueOf(HttpConnection.getCalculatorService("tan",Double.parseDouble(path.split("=")[1])));
                    }else{
                        body = getFail();
                    }
                    firstLine = false;
                }


                System.out.printf("Thiis is receiveeeee" + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type:  text/html\r\n"
                    + "\r\n"
                    + body;
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    public static int getPort() {
        if (System.getenv("PORT") != null) {
            return new Integer(System.getenv("PORT"));
        } else {
            return 4567;
        }
    }
    public static String getFail(){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Error 404 file Not Found</h1>\n" +
                "    </body>\n" +
                "</html>";
    }
    public static String getForm(){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Digite la funcion(cos,sen,tan) y el valor</h1>\n" +
                "        <form action=\"/cos\">\n" +
                "            <label for=\"name\">Name:</label><br>\n" +
                "            <input type=\"text\" id=\"func\" name=\"Funcion\" value=\"cos\"><br><br>\n" +
                "            <input type=\"text\" id=\"value\" name=\"name\" value=\"90\"><br><br>\n" +
                "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsgCos()\">\n" +
                "        </form> \n" +
                "\n" +
                "<div id=\"getrespmsg\"></div>"+
                "        <script>\n" +
                "            function loadGetMsgCos() {\n" +
                "                let cosVal = document.getElementById(\"value\").value;\n" +
                "                let fun = document.getElementById(\"func\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/\"+fun+\"?val=\"+cosVal);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "    </body>\n" +
                "</html>";
    }
}