package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

public class UserInterface {
    //used for image representation
    private static Graph graph;
    //used to keep track of edits
    private static Stack<Node[]> edits = new Stack<Node[]>();
    //for tempImg, edit tracking
    private static int editCount = 0;
    private static int imgLoadCount = 0;
    //for keeping track of whether a highlight is on the board or not
    private static Node[] highlighted = new Node[0];
    private static boolean highlightedBoard = false;

    /**
     * Checks if the filePath provided by the user exists
     * @exception IOException if the file does not exist
     */
    public static boolean fileCheck(String filename) throws IOException {
        try {
            File f = new File(filename);
            if(f.isFile()) {
                graph = new Graph(ImageIO.read(f));
                System.out.println("Image from " + filename + " was loaded!");
                return true;
            } else {
                System.out.println("File does not exist! Please try again.");
                return false;
            }
        }
        catch(IOException e) {
            throw new IOException("Error: File not found");
        }
    }

    /**
     * Prompts the user with all possible options
     */
    public static void printMenu() {
        System.out.println("What would you like to do?");
        System.out.println("f.) Choose a new file");
        System.out.println("b.) Delete the bluest seam");
        System.out.println("e.) Delete the seam with the least energy");
        System.out.println("d.) Confirm deletion IF a seam is highlighted");
        System.out.println("u.) Undo last edit");
        System.out.println("q.) Quit");
    }

    /**
     * Print a response to the user, given their selection of mutations
     * @param selection the value of the user's choice
     */
    public static void reactResponse(String selection) {
        //a file for saveImg in Image.java, tempImg
        File f = new File(
                "src/main/resources/tempImg" +
                        imgLoadCount+ "_0" +
                        editCount + ".png");
        //refers to a final image file, created upon quitting
        File fFinal = new File(
                "src/main/resources/finalImg" +
                        imgLoadCount+ "_0" +
                        editCount + ".png");
        //a switch that uses the user input
        switch(selection) {
            case "b":
                /*
                will use the highlight blue function from Graph.java
                and attempts to save it as an image, printing out the
                altered image
                */
                if(!highlightedBoard) {
                    System.out.println("Removing the bluest seam...");
                    try {
                        highlighted = graph.blueFinder();
                        edits.push(highlighted);
                        graph.highlightNodes(highlighted, Color.blue);
                        highlightedBoard = true;
                    } catch (Exception e) {
                        System.out.println("Image not loaded");
                    }
                    try {
                        graph.saveImg(f);
                    } catch(Exception e) {
                        System.out.println("File path not found");
                    }
                    System.out.println("Changed Image:");
                    System.out.println(graph);
                    editCount++;
                } else {
                    System.out.println("Error: please confirm changes on the board first");
                }
                break;
            case "e":
                /*
                will use the seamfinder function from graph.java
                and attempts to save it as an image, printing out
                the altered image
                */
                if(!highlightedBoard) {
                    System.out.println("Removing seam with lowest energy...");
                    try {
                        highlighted = graph.seamFinder();
                        edits.push(highlighted);
                        graph.highlightNodes(highlighted, Color.red);
                        highlightedBoard = true;
                    } catch (Exception e) {
                        System.out.println("Error: Energy values not assigned.");
                    }
                    try {
                        graph.saveImg(f);
                    } catch(Exception e) {
                        System.out.println("File path not found");
                    }
                    System.out.println("Changed Image:");
                    System.out.println(graph);
                    editCount++;
                } else {
                    System.out.println("Error: please confirm changes on the board first");
                }
                break;
            case "f":
                /*
                will trigger a file change
                */
                System.out.println("Changing files...");
                break;
            case "d":
                if(highlightedBoard) {
                    System.out.println("Removing highlighted portion...");
                    graph.delete(highlighted);
                    highlightedBoard = false;
                    editCount++;
                    System.out.println("New image:");
                    System.out.println(graph);
                } else {
                    System.out.println("Please make an edit first");
                }
                break;
            case "u":
                /*
                will attempt to undo, but if undo is impossible,
                the undoPossible switch is turned off
                */
                if(edits.empty()) {
                    System.out.println("Error: No edits made. Please make an edit before you undo.");
                    break;
                } else {
                    System.out.println("Undoing the last change.");
                    Node[] lastEdit = edits.pop();
                    graph.add(lastEdit);
                }
                break;
            case "q":
                /*
                will quit the project, and attempt to save the image
                to a final png.
                */
                System.out.println("Quitting program...");
                try {
                    graph.saveImg(fFinal);
                } catch(Exception e) {
                    System.out.println("File path not found");
                }
                break;
            default:
                /*
                catch case for improper inputs
                */
                System.out.println("That is not a valid option.");
                break;

        }
    }

    public static void main(String[] args) throws IOException {
        /*
         a list of boolean switches for various loops within the user
         interface.
         */
        boolean fileSwitch = false;
        boolean shouldQuit = false;
        boolean confMade = false;
        boolean sketchSwitch = true;

        Scanner scan = new Scanner(System.in);

        /*
        prompts the user to enter a file. won't exit until it is a valid png.
         */
        while(!fileSwitch) {
            System.out.println("Please enter a path to your file:");
            try {
                fileSwitch = fileCheck(scan.nextLine());
            } catch (IOException e) {
                System.out.println("File doesn't exist. Please enter an existing path");
            }
        }

        String choice = "";

        // while shouldQuit is false, keep going
        while(!shouldQuit) {

            // display options to the user
            printMenu();

            // try and get user input, if input is ever invalid this will set choice to quit
            try {
                choice = scan.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("No input given");
                choice = "q";
            }

            //outputs a response to the user input
            reactResponse(choice);

            //loop case to load a new file
            if(choice.equals("f")) {
                fileSwitch = false;
                while(!fileSwitch) {
                    System.out.println("Please enter a path to the image you want to switch to:");
                    try {
                        fileSwitch = fileCheck(scan.nextLine());
                        imgLoadCount++;
                    } catch (IOException e) {
                        System.out.println("No input found");
                    }
                }
            }

            // if choice is quit, exit the while-loop
            else if(choice.equals("q")) {
                shouldQuit = true;
            }
        }
        scan.close();
    }
}
}
