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
            Hex hex = new Hex(i, type, initialResources, neigh0, neigh1, neigh2, neigh3, neigh4, neigh5, 0, 0, 0, 0);
            hexList.add(hex);
        }
        int numberOfBases = in.nextInt();
        for (int i = 0; i < numberOfBases; i++) {
            myBaseIndex = in.nextInt();
        }
        for (int i = 0; i < numberOfBases; i++) {
            int oppBaseIndex = in.nextInt();
        }

        //get the hex which has my base on it
        Hex myBaseHex = new Hex (0,0,0,0,0,0,0,0,0,0,0,0,0);
        for (Hex hex : hexList) {
            if (hex.getIndex() == myBaseIndex) {
                myBaseHex = hex;
            }
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





            //if resources is zero remove from list
            for (int i = 0; i < hexList.size(); i++) {
                if (hexList.get(i).getResources() == 0) {
                    hexList.remove(i);
                }
            }






            int eggs = 0;
            int crystals = 0;
            for (Hex hex : hexList) {
                if (hex.getType() == 1) {
                    eggs++;
                } else if (hex.getType() == 2) {
                    crystals++;
                }
            }
            //if eggs is greater than crystals
            if(eggs > crystals) {
                //remove eggs from list which are not neighbours with my base
                for (int i = 0; i < hexList.size(); i++) {
                    if (hexList.get(i).getType() == 1 && !Helpers.isNeighbour(myBaseHex, hexList.get(i))) {
                        hexList.remove(i);
                    }
                }
            }

            int strength = 1;

            //make list of neighbours
            List<Hex> neighbours = new ArrayList<>();
            //make list of all neighbours neighbours
            List<Hex> neighboursNeighbours = new ArrayList<>();

            //check if any hex is neighbours with my base using helper method in Helpers
            for (Hex hex : hexList) {
                //check if hex has resources
                if (hex.getResources() == 0) {
                    continue;
                }

                if (Helpers.isNeighbour(myBaseHex, hex)) {
                    System.err.println("Found N1 " + hex.getIndex());
                    double currnetValue = hex.getValue();
                    neighbours.add(hex);
                    hex.setValue(currnetValue + 10);
                } else if (Helpers.isNeighBoursNeighbour(myBaseHex, hex)) {
                    System.err.println("Found N2 " + hex.getIndex());
                    double currnetValue = hex.getValue();
                    neighboursNeighbours.add(hex);
                    hex.setValue(currnetValue + 5);
                } else {
                    hex.setValue(strength);
                }
            }

            //make Hex list for output
            List<Hex> hexListForOutput = new ArrayList<>();

            //check if hex has eggs
            for (Hex hex : hexList) {
                if (hex.getType() == 1) {
                    hexListForOutput.add(hex);
                }
            }


            //if neightbous is not empty use as output
            if (!neighbours.isEmpty()) {
                hexListForOutput = neighbours;
            } else if (!neighboursNeighbours.isEmpty()) {
                hexListForOutput = neighboursNeighbours;
            } else {
                //combine hexList and neighbours
                hexList.addAll(neighbours);
                hexListForOutput = hexList;

            }

            //for each hex in hexListForOutput add 1 to value for each 10 resources on it
            for (Hex hex : hexListForOutput) {
                if (hex.getResources() > 0) {
                    int strenght = (int) (hex.getValue() + (hex.getResources() / 10));
                    double currnetValue = hex.getValue();
                    hex.setValue(currnetValue + strenght);
                }
            }

            //if there is more than 3 hexes in hexListForOutput remove the one with the lowest value
            if (hexListForOutput.size() > 3) {
                double lowestValue = 1000;
                int lowestValueIndex = 0;
                for (int i = 0; i < hexListForOutput.size(); i++) {
                    if (hexListForOutput.get(i).getValue() < lowestValue) {
                        lowestValue = hexListForOutput.get(i).getValue();
                        lowestValueIndex = i;
                    }
                }
                hexListForOutput.remove(lowestValueIndex);
            }


            String output = "";
            for (Hex hex : hexListForOutput) {
                if (hex.getResources() > 0) {
                    int strenght = (int) (hex.getValue());
                    //put LINE on hex commands and separate with ;
                    output += "LINE " + myBaseIndex + " " + hex.getIndex() + " " + strenght + ";";
                }
            }

            if(hexListForOutput.isEmpty()){
                System.out.println("WAIT");
            }else{
                System.out.println(output);
            }

            // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>

        }
    }
}