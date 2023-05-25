import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        List<Hex> hexList = new ArrayList<>();
        int myBaseIndex = 0;
        int numberOfCells = in.nextInt(); // amount of hexagonal cells in this map
        for (int i = 0; i < numberOfCells; i++) {
            int type = in.nextInt(); // 0 for empty, 1 for eggs, 2 for crystal
            int initialResources = in.nextInt(); // the initial amount of eggs/crystals on this cell
            int neigh0 = in.nextInt(); // the index of the neighbouring cell for each direction
            int neigh1 = in.nextInt();
            int neigh2 = in.nextInt();
            int neigh3 = in.nextInt();
            int neigh4 = in.nextInt();
            int neigh5 = in.nextInt();
            Hex hex = new Hex(i, type, initialResources, neigh0, neigh1, neigh2, neigh3, neigh4, neigh5, 0, 0, 0);
            hexList.add(hex);
        }
        int numberOfBases = in.nextInt();
        for (int i = 0; i < numberOfBases; i++) {
            myBaseIndex = in.nextInt();
        }
        for (int i = 0; i < numberOfBases; i++) {
            int oppBaseIndex = in.nextInt();
        }

        // game loop
        while (true) {
            for (int i = 0; i < numberOfCells; i++) {
                int resources = in.nextInt(); // the current amount of eggs/crystals on this cell
                int myAnts = in.nextInt(); // the amount of your ants on this cell
                int oppAnts = in.nextInt(); // the amount of opponent ants on this cell

                //check if hex is in list
                for (Hex hex : hexList) {
                    if (hex.getIndex() == i) {
                        hex.setResources(resources);
                        hex.setMyAnts(myAnts);
                        hex.setOppAnts(oppAnts);
                    }
                }

            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            //if resources is zero remove from list
            for (int i = 0; i < hexList.size(); i++) {
                if (hexList.get(i).getResources() == 0) {
                    hexList.remove(i);
                }
            }

            int strength = 10 / hexList.size();


            String output = "";
            for (Hex hex : hexList) {
                if (hex.getResources() > 0) {
                    //put LINE on hex commands and separate with ;
                    output += "LINE " + myBaseIndex + " " + hex.getIndex() + " " + strength + ";";
                }
            }

            if(hexList.isEmpty()){
                System.out.println("WAIT");
            }else{
                System.out.println(output);
            }

            // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>

        }
    }
}