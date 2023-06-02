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

    public static void chekIfThereIsEnoughAntsToCoverPaths(GameState state, Map<Hex, List<Hex>> paths) {
        //Take all Lists of hexes out of paths and put them in one list-----Map<Hex, List<Hex>>
        List<Hex> allHexes = new ArrayList<>();
        for (Map.Entry<Hex, List<Hex>> entry : paths.entrySet()) {
            allHexes.addAll(entry.getValue());
        }
        //put the paths in a set to remove duplicates
        Set<Hex> uniqueHexes = new HashSet<>(allHexes);
        //get all ants I have
        int myAnts = state.getMyAnts();
        double antsPerHex = myAnts / uniqueHexes.size();
        System.err.println("possible antsPerHex:" + antsPerHex);
    }

    public static void resetState(GameState state) {
        state.setTotalCrystals(0);
        state.setTotalEggs(0);
        state.setMyAnts(0);
        state.setOpponentAnts(0);
    }

    public static boolean isVerySafeTimeToAttack(GameState state) {
        if (state.getMyAnts() / 2 > state.getOpponentAnts()) {
            //TODO: check if the game is ending anyway
            //TODO: check somehow how many crystals opponent has?? (maybe not neededhere but somewhere else)
            return true;
        } else {
            return false;
        }
    }

    public static Hex checkIfOpponentIsHarvestingThisCrystalAndGiveTheNeighbourHexToAttackIfItIsGoodIdea(List<Hex> hexes, Hex target) {
        System.err.println(target.getIndex() + " checking if worth to protect crystal");
        if (target.getResources() < 50) {
            System.err.println(target.getIndex() + " not enough resources on crystal");
            return null;
        } else if (target.getOppAnts() == 0) {
            System.err.println(target.getIndex() + " no opponent ants on crystal");
            return null;
        } else {
            List<Hex> oneNeighbourWithOpponentAnts = getOneNeighbourWithOpponentAnts(hexes, target);
            if (oneNeighbourWithOpponentAnts != null) {
                return oneNeighbourWithOpponentAnts.get(0);
            } else {
                return null;
            }
        }
    }

    public static List<Hex> getOneNeighbourWithOpponentAnts(List<Hex> neighbours, Hex target) {
        List<Hex> neighboursWithOpponentAnts = new ArrayList<>();
        for (Hex neighbour : neighbours) {
            if (neighbour.getOppAnts() > 0) {
                neighboursWithOpponentAnts.add(neighbour);
            }
        }
        if (neighboursWithOpponentAnts.size() == 1) {
            return neighboursWithOpponentAnts;
        } else if (neighboursWithOpponentAnts.size() > 1) {
            System.err.println(target.getIndex() + " more than one neighbour with opponent ants will not attack");
            return null;
        } else {
            return null;
        }
    }

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

        List<Hex> crystalHexes = new ArrayList<>();
        boolean eggsCloseToBaseFocus = false;
        for (Hex hex : hexes) {
            if (hex.getType() == 2 && hex.getResources() > 0) {
                crystalHexes.add(hex);
            }
        }

        List<Hex> eggHexes = new ArrayList<>();
        for (Hex hex : hexes) {
            if (hex.getType() == 1 && hex.getResources() > 0) {
                eggHexes.add(hex);
            }
        }

        boolean mostOfCrystalsHarvested = areMostOfCrystalsHarvested(gameState);
        boolean isGameRunningOutOfTurns = isGameRunningOutOfTurns(gameState, hexes);
        if (mostOfCrystalsHarvested) {
            System.err.println("mostOfCrystalsHarvested");
            gameState.setEggsValue(1);
            gameState.setCrystalValue(2);
        }
        if (isGameRunningOutOfTurns) {
            System.err.println("GameRunningOutOfTurns");
            gameState.setEggsValue(1);
            gameState.setCrystalValue(2);
        }
        if (gameState.totalCrystals < gameState.totalEggs) {
            System.err.println("totalCrystals < totalEggs");
            gameState.setEggsValue(1);
            gameState.setCrystalValue(2);
        }

        Map<Hex, List<Hex>> optimalTargetHexesWithPaths = new HashMap<>();

        boolean eggNextToBase = false;
        boolean eggsCloseToBase = false;
        if (!mostOfCrystalsHarvested) {
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
                        if (neighbourHex.getResources() > neighbourHex.getMyAnts()) {
                            neighbourHex.setValue((int) (neighbourHex.getResources() * gameState.getEggsValue() + 9999));

                        } else {
                            neighbourHex.setValue((int) (neighbourHex.getResources() * gameState.getEggsValue()));
                        }
                        eggNextToBase = true;
                        eggShortestPaths.put(neighbourHex, shortestPath);
                    }
                }
            }

            if (!eggNextToBase) {
                for (Hex eggHex : eggHexes) {
                    List<Hex> shortestPath = getShortestPath(hexes, hexes.get(homeBaseIndex), eggHex);
                    if (shortestPath.size() <= 4) {
                        System.err.println("found egg close to base" + eggHex.getIndex());
                        if (isGameRunningOutOfTurns || mostOfCrystalsHarvested) {
                            eggHex.setValue((int) (eggHex.getResources() * gameState.getEggsValue() / shortestPath.size()));
                        } else {
                            // HUOMAA TÄÄLLÄ MAGIC number, siivoa
                            eggHex.setValue((int) ((eggHex.getResources() * 10) * gameState.getEggsValue() / shortestPath.size()));
                        }
                        eggsCloseToBase = true; // lippuvaluet kyl huonoja :< Pitäis vaan kylvää kertoimia.

                        eggShortestPaths.put(eggHex, shortestPath);
                    }
                }
            }
            optimalTargetHexesWithPaths.putAll(eggShortestPaths);
        }

        if (!eggNextToBase) {
            Map<Hex, List<Hex>> crystalShortestPaths = new HashMap<>();
            for (Hex crystalHex : crystalHexes) {
                List<Hex> shortestPath = getShortestPath(hexes, hexes.get(homeBaseIndex), crystalHex);
                if (shortestPath.size() <= 4 && !isGameRunningOutOfTurns && !mostOfCrystalsHarvested) {
                    System.err.println("found crystal close to base" + crystalHex.getIndex());
                    crystalHex.setValue(crystalHex.getResources() / 10);
                    crystalShortestPaths.put(crystalHex, shortestPath);
                } else {
                    System.err.println("found crystal far from base" + crystalHex.getIndex());

                    if (eggsCloseToBase) {
                        crystalHex.setValue(crystalHex.getResources() + gameState.getCrystalValue() / shortestPath.size());
                    } else {
                        crystalHex.setValue(crystalHex.getResources() * gameState.getCrystalValue() / shortestPath.size());
                    }
                    crystalShortestPaths.put(crystalHex, shortestPath);
                }
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

    public static boolean areMostOfCrystalsHarvested(GameState gameState) {
        boolean mostOfCrystalsHarvested = gameState.getTotalCrystals() < gameState.getInitialCrystals() / 1.9;
        if (mostOfCrystalsHarvested) {
            System.err.println("MOST OF CRYSTALS HARVESTED");
        }
        return mostOfCrystalsHarvested;
    }

    public static boolean isGameRunningOutOfTurns(GameState gameState, List<Hex> hexes) {
        int maxTheoreticalTurns = (hexes.size() / 2) + (gameState.getInitialCrystals() / (gameState.getInitialEggs()));
        boolean gameRunningOutOfTurns = gameState.getTurn() > (maxTheoreticalTurns / 2);
        System.err.println("maxTheoreticalTurns: " + maxTheoreticalTurns);
        if (gameRunningOutOfTurns) {
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
            } else if (resources == mostResources) {
                //if resources are equal, the winner is the path with most own ants
                int ownAntsOnPath = 0;
                int ownAntsOnShortestPathWithMostResources = 0;
                for (Hex hex : path) {
                    ownAntsOnPath += hex.getMyAnts();
                }
                for (Hex hex : shortestPathWithMostResources) {
                    ownAntsOnShortestPathWithMostResources += hex.getMyAnts();
                }
                if (ownAntsOnPath > ownAntsOnShortestPathWithMostResources) {
                    shortestPathWithMostResources = path;
                }
            }
        }
        return shortestPathWithMostResources;
    }

}