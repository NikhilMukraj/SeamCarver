package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

/**
 * Space for user interaction---where the main functions are run from
 * Contains print statements and cases for user responses
 * @see Graph for backend
 */
public class UserInterface {
    //used for image representation
    private static Graph graph;
    //used to keep track of edits
    private static Stack<Node[]> edits = new Stack<>();
    //for tempImg, edit tracking
    private static int editCount = 0;
    private static int imgLoadCount = 0;
    //for keeping track of whether a highlight is on the board or not
    private static Node[] highlighted = new Node[0];
    private static boolean highlightedBoard = false;

    //color println features
    public static final String ANSI_NO_COLOR = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    //gui representation holder
    public static GUIJFrame gui;

    /**
     * Checks if the filePath provided by the user exists
     * @exception IOException if the file does not exist
     */
    public static boolean fileCheck(String filename) throws IOException {
        try {
            File f = new File("src/resources/"+filename);
            if(f.isFile()) {
                graph = new Graph(ImageIO.read(f));
                graph.setEnergyGrid();
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
        System.out.println("v.) View current image");
        System.out.println("b.) Highlight the bluest seam");
        System.out.println("e.) Highlight the seam with the least energy");
        System.out.println("d.) Confirm deletion IF a seam is highlighted");
        System.out.println("u.) Undo last edit");
        System.out.println("q.) Quit");
    }

    /**
     * Print a response to the user, given their selection of mutations
     * Also takes an action based on user input
     * @param selection the value of the user's choice
     */
    public static void reactResponse(String selection) {
        //a file for saveImg in Image.java, tempImg
        File f = new File(
                "src/resources/tempImg" +
                        imgLoadCount+ "_0" +
                        editCount + ".png");
        //refers to a final image file, created upon quitting
        File fFinal = new File(
                "src/resources/finalImg" +
                        imgLoadCount+ "_0" +
                        editCount + ".png");
        //a switch that uses the user input
        switch(selection) {
            case "v":
                System.out.println("Viewing current image...");
                try {
                    gui = new GUIJFrame(f.getName(), graph.imgToByteArr());
                } catch(IOException ioException) {
                    System.out.println("Error: IO Exception");
                }
            break;
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
                    System.out.println("Now showing you the changed image...");
                    try {
                        gui = new GUIJFrame(f.getName(), graph.imgToByteArr());
                    } catch(IOException ioException) {
                        System.out.println("Error: IO Exception");
                    }
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
                    System.out.println("Now showing you the changed image...");
                    try {
                        gui = new GUIJFrame(f.getName(), graph.imgToByteArr());
                    } catch(IOException ioException) {
                        System.out.println("Error: IO Exception");
                    }
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
                if(!highlightedBoard) {
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

                    try {
                        graph.saveImg(f);
                    } catch(Exception e) {
                        System.out.println("File path not found");
                    }
                    editCount++;
                    System.out.println("Showing you the new image...");
                    try {
                        gui = new GUIJFrame(f.getName(), graph.imgToByteArr());
                    } catch(IOException ioException) {
                        System.out.println("Error: IO Exception");
                    }
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

    /**
     * Separate helper class to account for when a highlight is on the board.
     * if d is pressed, this will delete the highlight. if any other key is pressed,
     * it will cancel the operation
     * @param conf represents the confirmation "d" or any other key pressed
     */
    private static void confHighlight(String conf) {
        //new file with path for outputting a temp image
        File f = new File(
                "src/main/resources/tempImg" +
                        imgLoadCount+ "_0" +
                        editCount + ".png");
        if(conf.equals("d")) {
            System.out.println("Removing highlighted portion...");
            graph.delete(highlighted);
            graph.setEnergyGrid();
        } else {
            System.out.println("Cancelling operation...");
            graph.returnColor(highlighted);
            graph.setEnergyGrid();
        }

        try {
            graph.saveImg(new File(
                    "src/resources/tempImg" + imgLoadCount+ "_0" + editCount + ".png"));
        } catch(Exception e) {
            System.out.println("File path not found");
        }

        highlightedBoard = false;
        editCount++;
        System.out.println("Displaying new image...");
        try {
            gui = new GUIJFrame(f.getName(), graph.imgToByteArr());
        } catch(IOException ioException) {
            System.out.println("Error: IO Exception");
        }
    }



    public static void main(String[] args) throws IOException {

        /*
         a list of boolean switches for various loops within the user
         interface.
         */
        boolean fileSwitch = false;
        boolean shouldQuit = false;

        Scanner scan = new Scanner(System.in);

        /*
        prompts the user to enter a file. won't exit until it is a valid png.
         */
        while(!fileSwitch) {
            System.out.println("Please enter the name of a png within src/resources:");
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

            if(highlightedBoard) {
                System.out.println(ANSI_CYAN + "There is currently a highlighted portion on the board. " +
                        "Enter d to delete" + System.lineSeparator() + "or any other key to cancel" + ANSI_NO_COLOR);
            }

            // try and get user input, if input is ever invalid this will set choice to quit
            try {
                choice = scan.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("No input given");
                choice = "q";
            }

            if(highlightedBoard) {
                confHighlight(choice);
            } else {
                //outputs a response to the user input
                reactResponse(choice);

                //loop case to load a new file
                if(choice.equals("f")) {
                    fileSwitch = false;
                    while(!fileSwitch) {
                        System.out.println("Please enter the name of a png within src/resources:");
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
                    System.out.println("ensure that all display windows are closed...");
                    gui.kill();
                }
            }
        }
        scan.close();
    }
}
