import java.util.*;
import java.util.stream.Collectors;

public class Helpers {
        static int dividerForAntTargets = 5; // has worked with 5(rank 400 wood1)

       //Idea with this is to limit spreading ants too thin
        public static int getMaxOptimalTargetsCount(GameState state) {
            int targets = 0;
            int myAnts = state.getMyAnts();
            targets = myAnts / dividerForAntTargets;
            //round down to nearest integer and return targets
            return targets;
        }

        public static void resetState(GameState state) {
            state.setTotalCrystals(0);
            state.setTotalEggs(0);
            state.setMyAnts(0);
            state.setOpponentAnts(0);
        }

        //method takes a hex and returns an array of integers of the neighbours of the hex
        public static int[] getNeighbours(Hex hex) {
            int[] neighbours = new int[6];
            neighbours[0] = hex.getNeigh0();
            neighbours[1] = hex.getNeigh1();
            neighbours[2] = hex.getNeigh2();
            neighbours[3] = hex.getNeigh3();
            neighbours[4] = hex.getNeigh4();
            neighbours[5] = hex.getNeigh5();
            return neighbours;
        }



        //get shortest path using PathFinder between two hexes and if there is more than one shortest path, return the one with most resources on the way or most own ants on the way
        public static List<Hex> getShortestPath(List<Hex> hexagons, Hex startingHex, Hex targetHex) {
            List<List<Hex>> allShortestPaths = PathFinder.findAllShortestPaths(hexagons, startingHex, targetHex);
            return getShortestPathWithMostResources(allShortestPaths);
        }

    public static Map<Hex, List<Hex>> getOptimalTargets(GameState gameState, List<Hex> hexes, int maxOptimalTargetsCount, int homeBaseIndex) {
            //get all hexes with crystal resources
            List<Hex> crystalHexes = new ArrayList<>();
            boolean eggsCloseToBaseFocus = false;
            for (Hex hex : hexes) {
                if (hex.getType() == 2 && hex.getResources() > 0) {
                    crystalHexes.add(hex);
                }
            }
            //get all hexes with eggs
            List<Hex> eggHexes = new ArrayList<>();
            for (Hex hex : hexes) {
                if (hex.getType() == 1 && hex.getResources() > 0) {
                    eggHexes.add(hex);
                }
            }
            //here should find the best targets and return top 3 or 4 or 5 depending on maxOptimalTargetsCount
            //I think of making an endgame switch. If only some amount of crystals left then should stop collecting eggs and only collect crystals
            //if there is eggs close to base, then should focus on collecting them first. Range is 3 hexes from base
            //if there is no eggs close to base, then should focus on collecting crystals. First should collect crystals that are close to base

            boolean stopCollectingEggs = areMostOfCrystalsHarvested(gameState);
            boolean isGameRunningOutOfTurns = isGameRunningOutOfTurns(gameState);
            if(stopCollectingEggs){
                System.err.println("stopCollectingEggs");
                gameState.setEggsValue(1);
                gameState.setCrystalValue(2);
            }
            if(isGameRunningOutOfTurns){
                System.err.println("GameRunningOutOfTurns");
                gameState.setEggsValue(1);
                gameState.setCrystalValue(2);
            }
            if(gameState.totalCrystals < gameState.totalEggs){
                System.err.println("totalCrystals < totalEggs");
                gameState.setEggsValue(1);
                gameState.setCrystalValue(2);
            }


            Map<Hex, List<Hex>> optimalTargetHexesWithPaths = new HashMap<>();

            boolean eggNextToBase = false;
            if(!stopCollectingEggs) {
                int eggCount = 0;
                Map<Hex, List<Hex>> eggShortestPaths = new HashMap<>();

                //TODO: this close to base can be made faster and in one for loop
                int[] neighbours = getNeighbours(hexes.get(homeBaseIndex));

                for (int neighbourIndex : neighbours) {
                    if (neighbourIndex != -1) {
                        Hex neighbourHex = hexes.get(neighbourIndex);
                        if (neighbourHex.getType() == 1 && neighbourHex.getResources() > 0) {
                            System.err.println("found egg NEXT to base" + neighbourHex.getIndex());
                            List<Hex> shortestPath = getShortestPath(hexes, hexes.get(homeBaseIndex), neighbourHex);
                            if(neighbourHex.getResources() > neighbourHex.getMyAnts()){
                                neighbourHex.setValue(neighbourHex.getResources() * gameState.getEggsValue() + 9999);

                            }else {
                                neighbourHex.setValue(neighbourHex.getResources() * gameState.getEggsValue() );
                            }
                            eggNextToBase = true;
                            eggShortestPaths.put(neighbourHex, shortestPath);

                        }
                    }
                }


                    for (Hex eggHex : eggHexes) {
                        List<Hex> shortestPath = getShortestPath(hexes, hexes.get(homeBaseIndex), eggHex);
                        if (shortestPath.size() <= 4) {
                            System.err.println("found egg close to base" + eggHex.getIndex());
                            eggHex.setValue(eggHex.getResources() * gameState.getEggsValue() / shortestPath.size() );
                            eggShortestPaths.put(eggHex, shortestPath);


                        }
                    }

                optimalTargetHexesWithPaths.putAll(eggShortestPaths);
            }
            //if only some amount of crystals left then should stop collecting eggs and only collect crystals
            //if there is no eggs close to base, then should focus on collecting crystals. First should collect crystals that are close to base
            if(!eggNextToBase) {


                Map<Hex, List<Hex>> crystalShortestPaths = new HashMap<>();
                for (Hex crystalHex : crystalHexes) {
                    List<Hex> shortestPath = getShortestPath(hexes, hexes.get(homeBaseIndex), crystalHex);
                    crystalHex.setValue(crystalHex.getResources() * gameState.getCrystalValue() / shortestPath.size());
                    crystalShortestPaths.put(crystalHex, shortestPath);
                }
                //get the shortest path to each crystal and add the shortest path to optimalTargets
                optimalTargetHexesWithPaths.putAll(crystalShortestPaths);
            }
            //System.err out all the optimal target hex indexes and their values
            for (Map.Entry<Hex, List<Hex>> entry : optimalTargetHexesWithPaths.entrySet()) {
                System.err.println("optimalTargetHexesWithPaths: " + entry.getKey().getIndex() + " " + entry.getKey().getValue());
            }

            //sort the optimalTargetHexesWithPaths by getValue of the key hex
            Map<Hex, List<Hex>> sortedOptimalTargetHexesWithPathsByValue = optimalTargetHexesWithPaths.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(Hex::getValue).reversed()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            for (Map.Entry<Hex, List<Hex>> entry : sortedOptimalTargetHexesWithPathsByValue.entrySet()) {
                System.err.println("sortedoptimalTargetHexesWithPaths: " + entry.getKey().getIndex() + " " + entry.getKey().getValue());
            }

            //get the first maxOptimalTargetsCount of optimalTargetHexesWithPaths and return them
            Map<Hex, List<Hex>> sortedOptimalTargetHexesWithPathsPicks = new LinkedHashMap<>();
            int count = 0;
            System.err.println("Picking top ones");
            for (Map.Entry<Hex, List<Hex>> entry : sortedOptimalTargetHexesWithPathsByValue.entrySet()) {
                if (count < maxOptimalTargetsCount) {
                    sortedOptimalTargetHexesWithPathsPicks.put(entry.getKey(), entry.getValue());
                    System.err.println("optimalTargetHexesWithPathsPick: " + entry.getKey());
                    count++;
                }
            }

            return sortedOptimalTargetHexesWithPathsPicks;

    }

    public static boolean areMostOfCrystalsHarvested(GameState gameState){
        boolean mostOfCrystalsHarvested = gameState.getTotalCrystals() < gameState.getInitialCrystals() / 2 ;
        if(mostOfCrystalsHarvested){
            System.err.println("MOST OF CRYSTALS HARVESTED");
        }
        return mostOfCrystalsHarvested;
    }

    public static boolean isGameRunningOutOfTurns(GameState gameState){
        boolean gameRunningOutOfTurns = gameState.getTurn() > 50;
        if(gameRunningOutOfTurns){
            System.err.println("GAME RUNNING OUT OF TURNS");
        }
        return gameRunningOutOfTurns;
    }



        //get a list of list of hexes. And return the list of hexes with most resources on the way or most own ants on the way (do not include starting and target hexes)
        public static List<Hex> getShortestPathWithMostResources(List<List<Hex>> allShortestPaths) {
            List<Hex> shortestPathWithMostResources = new ArrayList<>();
            int mostResources = 0;
            for (List<Hex> path : allShortestPaths) {
                int resources = 0;
                for (Hex hex : path) {
                    resources += hex.getResources();
                }
                if (resources > mostResources) {
                    mostResources = resources;
                    shortestPathWithMostResources = path;
                }else if(resources == mostResources){
                    //if resources are equal, the winner is the path with most own ants
                    int ownAntsOnPath = 0;
                    int ownAntsOnShortestPathWithMostResources = 0;
                    for (Hex hex : path) {
                        ownAntsOnPath += hex.getMyAnts();
                    }
                    for (Hex hex : shortestPathWithMostResources) {
                        ownAntsOnShortestPathWithMostResources += hex.getMyAnts();
                    }
                    if(ownAntsOnPath > ownAntsOnShortestPathWithMostResources){
                        shortestPathWithMostResources = path;
                    }
                }
            }
            return shortestPathWithMostResources;
        }



}