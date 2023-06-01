import java.awt.*;
import java.util.*;
import java.io.*;
import java.math.*;
import java.util.List;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
public class Player {

    public static void main(String args[]) {
        List<Hex> hexes = new ArrayList<>();
        //home hexes
        int myBaseIndex = 0;
        GameState gameState = new GameState();
        Scanner in = new Scanner(System.in);
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
            // initial hexes
            Hex hex = new Hex(i, type, initialResources, neigh0, neigh1, neigh2, neigh3, neigh4, neigh5, 0, 0, 0, 0);
            hexes.add(hex);
            if(type == 1){
                gameState.setInitialEggs(gameState.getInitialEggs() + initialResources);
            }else if(type == 2){
                gameState.setInitialCrystals(gameState.getInitialCrystals() + initialResources);
            }
        }
        //System.err.println("initialEggs: " + gameState.getInitialEggs());
        //System.err.println("initialCrystals: " + gameState.getInitialCrystals());

        gameState.setTotalEggs(gameState.getInitialEggs());
        gameState.setTotalCrystals(gameState.getInitialCrystals());
        Hex homeHex = hexes.get(myBaseIndex);

        ///// BASES /////
        int numberOfBases = in.nextInt();
        // int array of myBaseIndex
        int[] myBaseIndexes = new int[numberOfBases];
        int[] oppBaseIndexes = new int[numberOfBases];
        System.err.println("numberOfBases: " + numberOfBases);
        for (int i = 0; i < numberOfBases; i++) {
            myBaseIndex = in.nextInt();
            myBaseIndexes[i] = myBaseIndex;
        }
        for (int i = 0; i < numberOfBases; i++) {
            int oppBaseIndex = in.nextInt();
            oppBaseIndexes[i] = oppBaseIndex;
        }
        //int homeBaseIndex = myBaseIndex;  // this propably changes in higher levels to more than one base

        int turn = 0;
        /////// GAME LOOP ///////
        while (true) {
            System.err.println("turn: " + turn);
            gameState.setTurn(turn);
            Helpers.resetState(gameState);
            for (int i = 0; i < numberOfCells; i++) {
                int resources = in.nextInt(); // the current amount of eggs/crystals on this cell
                int myAnts = in.nextInt(); // the amount of your ants on this cell
                int oppAnts = in.nextInt(); // the amount of opponent ants on this cell

                // update hexes
                Hex hex = hexes.get(i);
                hex.setResources(resources);
                hex.setMyAnts(myAnts);
                hex.setOppAnts(oppAnts);
                //count total crystals and eggs, (they are reseted to 0 every turn)
                if (hex.getType() == 1) {
                    gameState.setTotalEggs(gameState.getTotalEggs() + resources);
                } else if (hex.getType() == 2) {
                    gameState.setTotalCrystals(gameState.getTotalCrystals() + resources);
                }
                //count ants
                gameState.setMyAnts(gameState.getMyAnts() + myAnts);
                gameState.setOpponentAnts(gameState.getOpponentAnts() + oppAnts);
            }

            // Define an array or list to store the home base indexes

//Figure out how many targets is good to approach. Depends on the number of ants. Should not spread out too thin
            int maxOptimalTargetsCount = Helpers.getMaxOptimalTargetsCount(gameState);

//Now let's find the best targets for each home base index
            Map<Integer, Map<Hex, List<Hex>>> filteredOptimalTargetsMap = new HashMap<>();
            for (int baseIndex : myBaseIndexes) {
                Map<Hex, List<Hex>> filteredOptimalTargets = Helpers.getOptimalTargets(gameState, hexes, maxOptimalTargetsCount, baseIndex);
                filteredOptimalTargetsMap.put(baseIndex, filteredOptimalTargets);
            }

            Helpers.chekIfThereIsEnoughAntsToCoverPaths(gameState, filteredOptimalTargets);



            //Now populate with BEACONs. The command is BEACON <cellIdx> <strength> and is separated by ;
            //Collect all filteredOptimalTargets and build a string from the indexes of the hexes
            StringBuilder beaconString = new StringBuilder();
            System.err.println("Building beacon string from " + maxOptimalTargetsCount + " max targets");

            for (int baseIndex : myBaseIndexes) {
                Map<Hex, List<Hex>> filteredOptimalTargets = filteredOptimalTargetsMap.get(baseIndex);
                // Reverse filteredOptimalTargets
                Map<Hex, List<Hex>> reversedFilteredOptimalTargets = new LinkedHashMap<>();
                List<Hex> reversedHexes = new ArrayList<>(filteredOptimalTargets.keySet());
                Collections.reverse(reversedHexes);
                for (Hex hex : reversedHexes) {
                    reversedFilteredOptimalTargets.put(hex, filteredOptimalTargets.get(hex));
                }

                for (Map.Entry<Hex, List<Hex>> entry : reversedFilteredOptimalTargets.entrySet()) {
                    Hex startHex = entry.getKey();
                    //iterate through the list of hexes and add them to the string
                    System.err.println("Adding hexes to beacon string" + entry.getValue().size());
                    for (Hex hex : entry.getValue()) {
                        // BEACON <cellIdx> <strength> and is separated by ; strength is hex.getValue for now but do not end with ;
                        //if the hex is the last in the list change the BEACON to BEACONLAST
                        if (hex.equals(entry.getValue().get(entry.getValue().size() - 1))) {
                            beaconString.append("BEACON ").append(hex.getIndex()).append(" ").append((int) (startHex.getValue() * 1.1)).append(";");
                        } else {
                            beaconString.append("BEACON ").append(hex.getIndex()).append(" ").append(startHex.getValue()).append(";");
                        }
                    }
                }
            }


            //remove last ; from string
            //beaconString.deleteCharAt(beaconString.length() - 1);
            System.out.println(beaconString);

            //if optimalTargets is empty print WAIT
            if (beaconString == null || beaconString.length() == 0) {
                System.out.print("WAIT;MESSAGE no optimal targets;");
            }

            turn++;
        }
    }
}