package com.tigerzone.fall2016server.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by lenovo on 11/17/2016.
 */
public class Client {

    String host;
    int port;
    BufferedReader in;
    PrintWriter out;
    Socket clientSocket;
    BufferedReader stdIn;


    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            clientSocket = new Socket(this.host, this.port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            System.out.println("Some ioexception");
        }
    }


    public void login() {
        try {
            String fromServer;
            String userInput;

            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                if (fromServer.equals("NOPE GOOD BYE")) {
                    System.out.println("Server says goodbye");
                    break;
                }
                if (fromServer.startsWith("WELCOME")) {
                    System.out.println("Server says WELCOME");
                    break;
                }
                userInput = stdIn.readLine();
                if (userInput != null) {
                    out.println(userInput);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }
    }

    public void waitForGame() {
        try {
            String fromServer;

            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                if (fromServer.startsWith("MATCH BEGINS")) {
                    break;
                }
                if (fromServer.equals("NOPE GOOD BYE")) {
                    System.out.println("Server says goodbye");
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }

    }


    public void playGame() {
        try {
            String fromServer;
            String userInput = null;

            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                if (fromServer.startsWith("MAKE")) {
                    userInput = "GAME 1 MOVE 1 PLACE JJJJX AT -1 0 0 NONE";
                    System.out.println(userInput);
                    out.println(userInput);
                }
                if (fromServer.equals("NOPE GOOD BYE")) {
                    System.out.println("Server says goodbye");
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }

    }

    public void playGame2() {
        try {
            String fromServer;
            String userInput = null;
            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                if (fromServer.startsWith("MAKE MOVE IN GAME 2")) {
                    String[] split = fromServer.split(" ");
                    int moveNumber = Integer.parseInt(split[10]);
                    switch (moveNumber) {
                        case 1:
                            userInput = "GAME 2 MOVE 1 PLACE JJJJX AT -1 0 0 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 3:
                            userInput = "GAME 2 MOVE 3 PLACE LLLL- AT 1 0 0 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 5:
                            userInput = "GAME 2 MOVE 5 PLACE TJTJ- AT -1 1 90 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 7:
                            userInput = "PLACE LJLJ- AT 2 0 90 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 9:
                            userInput = "PLACE TLLLC AT -3 0 270 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 11:
                            userInput = "PLACE TJJT- AT 0 -1 0 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 13:
                            userInput = "PLACE LJJJ- AT 3 0 90 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 15:
                            userInput = "PLACE TLTTP AT 4 -1 90 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 17:
                            userInput = "PLACE TLTJ- AT -2 -1 90 TIGER 2";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 19:
                            userInput = "PLACE JLTTB AT 3 -2 270 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                    }

                } else if (fromServer.startsWith("MAKE MOVE IN GAME 1")) { //the player calling playGame2() will be player2 in game1
                    String[] split = fromServer.split(" ");
                    int moveNumber = Integer.parseInt(split[10]);
                    switch (moveNumber) {
                        case 2:
                            userInput = "PLACE JLTT- AT -2 0 270 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 4:
                            userInput = "PLACE TJJT- AT 0 1 90 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 6:
                            userInput = "PLACE LLJJ- AT 1 -1 0 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 8:
                            userInput = "PLACE LLJJ- AT 1 1 270 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 10:
                            userInput = "PLACE TLLTB AT -2 1 180 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 12:
                            userInput = "PLACE JLLL- AT 2 -1 0 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 14:
                            userInput = "PLACE TLJT- AT 3 -1 180 NONE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 16:
                            userInput = "PLACE TJTJ- AT -1 -1 90 TIGER 4";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 18:
                            userInput = "PLACE LJJJ- AT 4 0 180 TIGER 8";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                        case 20:
                            userInput = "PLACE JLLJ- AT 2 1 180 CROCODILE";
                            System.out.println(userInput);
                            out.println(userInput);
                            break;
                    }
                }
                if (fromServer.equals("NOPE GOOD BYE")) {
                    System.out.println("Server says goodbye");
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }

    }

    public void livePlay() {
        try {
            String fromServer;
            String userInput = null;

            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);

                if (fromServer.equals("NOPE GOOD BYE")) {
                    System.out.println("Server says goodbye");
                    break;
                }

//                clientSocket.setSoTimeout(1000);

                userInput = stdIn.readLine();
                if (userInput != null) {
                    out.println(userInput);
                }

            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
//            System.exit(1);
        } catch (IOException e) {
            System.err.println("In client: Couldn't get I/O for the connection to " + host);
            //System.exit(1);
        }

    }


    public void defaultLogin() throws Exception {
        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            if (fromServer.equals("NOPE GOOD BYE")) {
                System.out.println("Server says goodbye");
                break;
            }
            if (fromServer.equals("THIS IS SPARTA!")) {
                out.println("JOIN TIGERZONE");
            } else if (fromServer.startsWith("HELLO!")) {
                out.println("I AM PLAYER1 PASSWORD1");
            } else if (fromServer.startsWith("WELCOME")) {
                System.out.println("ACCEPTED TO THE TOURNEY");
                break;
            }

        }
    }

    public void defaultLogin2() throws Exception {
        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            if (fromServer.equals("NOPE GOOD BYE")) {
                System.out.println("Server says goodbye");
                break;
            }
            if (fromServer.equals("THIS IS SPARTA!")) {
                out.println("JOIN TIGERZONE");
            } else if (fromServer.startsWith("HELLO!")) {
                out.println("I AM PLAYER2 PASSWORD2");
            } else if (fromServer.startsWith("WELCOME")) {
                System.out.println("ACCEPTED TO THE TOURNEY");
                break;
            }

        }
    }

}

