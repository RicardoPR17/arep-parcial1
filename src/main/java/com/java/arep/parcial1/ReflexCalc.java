package com.java.arep.parcial1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ReflexCalc {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35015);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35015.");
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
            String uriString = "";
            boolean firstLine = true;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (firstLine) {
                    uriString = inputLine.split(" ")[1];
                    break;
                }
                if (!in.ready()) {
                    break;
                }
            }

            URI uri = null;

            try {
                uri = new URI("localhost" + uriString);
            } catch (URISyntaxException e) {
            }

            String query = uri.getQuery().split("=")[1];
            String response = "";

            if (query != null) {
                if (query.contains("qck")) {
                    String list = query.replace("qck", "").replace("(", "").replace(")", "");
                    ArrayList<Integer> valid_list = new ArrayList<>();

                    for (String val : list.split(",")) {
                        valid_list.add(Integer.parseInt(val));
                    }
                    
                    response = quickSortCaller(valid_list).toString();
                    System.out.println(response);
                    outputLine = "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: application/json\r\n"
                            + "\r\n"
                            + response;
                } else { // reflexivo
                    String command = query.substring(0, query.indexOf("("));
                    String param = query.replace(command, "").replace("(", "").replace(")", "");
                    System.out.println(param);
                    Double param1 = 0.0;
                    Double param2 = 0.0;
                    boolean paramTwo = false;
                    if (param.contains(",")) {
                        paramTwo = true;
                        String[] params = param.split(",");
                        param1 = Double.parseDouble(params[0]);
                        param2 = Double.parseDouble(params[1]);
                    } else {
                        param1 = Double.parseDouble(param);
                    }

                    Class<?> clas = Math.class;
                    try {
                        if (paramTwo) {
                            Method met = clas.getMethod(command, Double.TYPE, Double.TYPE);
                            System.out.println("Params: " + met.getParameterCount());
                            response = met.invoke(null, param1, param2).toString();
                        } else { // method with one param
                            Method met = clas.getMethod(command, Double.TYPE);
                            response = met.invoke(null, param1).toString();
                        }
                        System.out.println("respusta: " + response);
                        outputLine = "HTTP/1.1 200 OK\r\n"
                                + "Content-Type: application/json\r\n"
                                + "\r\n"
                                + response;
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                            | IllegalArgumentException | InvocationTargetException e) {
                        System.out.println(e);
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
                                "<h1>method not found</h1>" +
                                "</body>" +
                                "</html>";
                    }
                }
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
                        "<h1>Query not received</h1>" +
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

    private static String quickSortCaller(List<Integer> unsorted_list) {
        return quickSort(unsorted_list, 0, unsorted_list.size() - 1).toString();
    }

    private static List<Integer> quickSort(List<Integer> list, int start, int end) {
        if (start < end) {
            int p = partition(list, start, end);
            quickSort(list, start, p - 1);
            quickSort(list, p + 1, end);
        }
        return list;
    }

    private static int partition(List<Integer> A, int start, int end) {
        int pivot = A.get(end);
        int pivotIndex = end;
        int swap = start;

        while (swap < end) {
            if (A.get(swap) > pivot) {
                int swapValue = A.get(swap);
                int swapIndex = A.indexOf(swap);
                A.set(swapIndex, pivot);
                A.set(pivotIndex, swapValue);
                pivotIndex = swapIndex;
            }
        }

        return pivotIndex;
    }
}
