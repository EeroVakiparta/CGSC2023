// Bundle uploaded at Fri Jun  2 08:07:35 EEST 2023
import java.util.*;
import java.util.stream.Collectors;
class Helpers {
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
class Hex {
    int index;
    int type;// 0 for empty, 1 for eggs, 2 for crystal
    int initialResources;// the initial amount of eggs/crystals on this cell
    int neigh0;// the index of the neighbouring cell for each direction
    int neigh1;
    int neigh2;
    int neigh3;
    int neigh4;
    int neigh5;
    int resources;// the current amount of eggs/crystals on this cell
    int myAnts; // the amount of your ants on this cell
    int oppAnts; // the amount of opponent ants on this cell
    int value;
    int distanceToMyBase;
    public Hex(int index, int type, int initialResources, int neigh0, int neigh1, int neigh2, int neigh3, int neigh4, int neigh5, int resources, int myAnts, int oppAnts, int value) {
        this.index = index;
        this.type = type;
        this.initialResources = initialResources;
        this.neigh0 = neigh0;
        this.neigh1 = neigh1;
        this.neigh2 = neigh2;
        this.neigh3 = neigh3;
        this.neigh4 = neigh4;
        this.neigh5 = neigh5;
        this.resources = resources;
        this.myAnts = myAnts;
        this.oppAnts = oppAnts;
        this.value = value;
    }
    public Hex() {
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getInitialResources() {
        return initialResources;
    }
    public void setInitialResources(int initialResources) {
        this.initialResources = initialResources;
    }
    public int getNeigh0() {
        return neigh0;
    }
    public void setNeigh0(int neigh0) {
        this.neigh0 = neigh0;
    }
    public int getNeigh1() {
        return neigh1;
    }
    public void setNeigh1(int neigh1) {
        this.neigh1 = neigh1;
    }
    public int getNeigh2() {
        return neigh2;
    }
    public void setNeigh2(int neigh2) {
        this.neigh2 = neigh2;
    }
    public int getNeigh3() {
        return neigh3;
    }
    public void setNeigh3(int neigh3) {
        this.neigh3 = neigh3;
    }
    public int getNeigh4() {
        return neigh4;
    }
    public void setNeigh4(int neigh4) {
        this.neigh4 = neigh4;
    }
    public int getNeigh5() {
        return neigh5;
    }
    public void setNeigh5(int neigh5) {
        this.neigh5 = neigh5;
    }
    public int getResources() {
        return resources;
    }
    public void setResources(int resources) {
        this.resources = resources;
    }
    public int getMyAnts() {
        return myAnts;
    }
    public void setMyAnts(int myAnts) {
        this.myAnts = myAnts;
    }
    public int getOppAnts() {
        return oppAnts;
    }
    public void setOppAnts(int oppAnts) {
        this.oppAnts = oppAnts;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public int getDistanceToMyBase() {
        return distanceToMyBase;
    }
    public void setDistanceToMyBase(int distanceToMyBase) {
        this.distanceToMyBase = distanceToMyBase;
    }
    @Override
    public String toString() {
        return "Hex{" +
                "index=" + index +
                ", type=" + type +
                ", initialResources=" + initialResources +
                ", neigh0=" + neigh0 +
                ", neigh1=" + neigh1 +
                ", neigh2=" + neigh2 +
                ", neigh3=" + neigh3 +
                ", neigh4=" + neigh4 +
                ", neigh5=" + neigh5 +
                ", resources=" + resources +
                ", myAnts=" + myAnts +
                ", oppAnts=" + oppAnts +
                ", value=" + value +
                ", distanceToMyBase=" + distanceToMyBase +
                '}';
    }
    //retrun array of integers of the neighbours of the hex
    public List<Integer> getNeighbours() {
        return Arrays.asList(neigh0, neigh1, neigh2, neigh3, neigh4, neigh5);
    }
}
/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
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
            if (type == 1) {
                gameState.setInitialEggs(gameState.getInitialEggs() + initialResources);
            } else if (type == 2) {
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
            //TODO: make this work again
            //Helpers.chekIfThereIsEnoughAntsToCoverPaths(gameState, filteredOptimalTargets);
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
class PathFinder {
    public static List<List<Hex>> findAllShortestPaths(List<Hex> hexagons, Hex startingHex, Hex targetHex) {
        Map<Integer, Hex> hexMap = new HashMap<>();
        for (Hex hex : hexagons) {
            hexMap.put(hex.getIndex(), hex);
        }
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, List<List<Hex>>> paths = new HashMap<>();
        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        for (Hex hex : hexagons) {
            int hexIndex = hex.getIndex();
            distances.put(hexIndex, Integer.MAX_VALUE);
            paths.put(hexIndex, new ArrayList<>());
        }
        int startingIndex = startingHex.getIndex();
        distances.put(startingIndex, 0);
        paths.get(startingIndex).add(Collections.singletonList(startingHex));
        queue.add(startingIndex);
        while (!queue.isEmpty()) {
            int currentHexIndex = queue.poll();
            Hex currentHex = hexMap.get(currentHexIndex);
            if (currentHexIndex == targetHex.getIndex()) {
                break; // Found the shortest paths, exit the loop
            }
            Integer[] neighbors = {currentHex.getNeigh0(), currentHex.getNeigh1(), currentHex.getNeigh2(), currentHex.getNeigh3(), currentHex.getNeigh4(), currentHex.getNeigh5()};
            for (Integer neighborIndex : neighbors) {
                if (neighborIndex != null && neighborIndex != -1) {
                    Hex neighborHex = hexMap.get(neighborIndex);
                    int distance = distances.get(currentHexIndex) + 1; // Assuming each connection has a distance of 1
                    if (distance < distances.get(neighborIndex)) {
                        distances.put(neighborIndex, distance);
                        List<List<Hex>> neighborPaths = paths.get(neighborIndex);
                        neighborPaths.clear();
                        for (List<Hex> path : paths.get(currentHexIndex)) {
                            List<Hex> newPath = new ArrayList<>(path);
                            newPath.add(neighborHex);
                            neighborPaths.add(newPath);
                        }
                        queue.add(neighborIndex);
                    } else if (distance == distances.get(neighborIndex)) {
                        List<List<Hex>> neighborPaths = paths.get(neighborIndex);
                        for (List<Hex> path : paths.get(currentHexIndex)) {
                            List<Hex> newPath = new ArrayList<>(path);
                            newPath.add(neighborHex);
                            neighborPaths.add(newPath);
                        }
                    }
                }
            }
        }
        return paths.get(targetHex.getIndex());
    }
/*    public static void main(String[] args) {
        // Example usage
        List<Hex> hexagons = new ArrayList<>();
        hexagons.add(new Hex(2, 0, 0, 0, 10, -1, -1, -1, -1, 0, 0, 0, 0));
        hexagons.add(new Hex(8, 0, 0, 18, -1, -1, 10, 0, 6, 0, 0, 0, 0));
        hexagons.add(new Hex(18, 0, 11, 28, -1, -1, 8, 6, -1, 0, 0, 0, 0));
        hexagons.add(new Hex(0, 0, 0, 6, 8, 10, -1, -1, -1, 0, 0, 0, 0));
        hexagons.add(new Hex(6, 0, 42, 18, 8, 0, -1, -1, -1, 0, 0, 0, 0));
        hexagons.add(new Hex(28, 0, 21, -1, -1, -1, 18, -1, -1, 0, 0, 0, 0));
        hexagons.add(new Hex(10, 0, 0, 8, -1, -1, -1, -1, 0, 0, 0, 0, 0));
        Hex startingHex = hexagons.get(1);
        Hex targetHex = hexagons.get(5);
        List<List<Hex>> shortestPaths = findAllShortestPaths(hexagons, startingHex, targetHex);
        System.out.println("Shortest paths: " + shortestPaths);
    }*/
}
class GameState {
    int initialCrystals;
    int initialEggs;
    int totalCrystals;
    int totalEggs;
    int myAnts;
    int opponentAnts;
    int strategy = 0; //0 = eggs, 1 = rush, 2 = ??
    double eggsValue = 2.1; // 2 alunperin
    int crystalValue = 1;
    int turn = 0;
    public GameState(int initialCrystals, int initialEggs, int totalCrystals, int totalEggs, int myAnts, int opponentAnts) {
        this.initialCrystals = initialCrystals;
        this.initialEggs = initialEggs;
        this.totalCrystals = totalCrystals;
        this.totalEggs = totalEggs;
        this.myAnts = myAnts;
        this.opponentAnts = opponentAnts;
    }
    public GameState() {
    }
    public int getInitialCrystals() {
        return initialCrystals;
    }
    public void setInitialCrystals(int initialCrystals) {
        this.initialCrystals = initialCrystals;
    }
    public int getInitialEggs() {
        return initialEggs;
    }
    public void setInitialEggs(int initialEggs) {
        this.initialEggs = initialEggs;
    }
    public int getTotalCrystals() {
        return totalCrystals;
    }
    public void setTotalCrystals(int totalCrystals) {
        this.totalCrystals = totalCrystals;
    }
    public int getTotalEggs() {
        return totalEggs;
    }
    public void setTotalEggs(int totalEggs) {
        this.totalEggs = totalEggs;
    }
    public int getMyAnts() {
        return myAnts;
    }
    public void setMyAnts(int myAnts) {
        this.myAnts = myAnts;
    }
    public int getOpponentAnts() {
        return opponentAnts;
    }
    public void setOpponentAnts(int opponentAnts) {
        this.opponentAnts = opponentAnts;
    }
    public int getStrategy() {
        return strategy;
    }
    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }
    public double getEggsValue() {
        return eggsValue;
    }
    public void setEggsValue(int eggsValue) {
        this.eggsValue = eggsValue;
    }
    public int getCrystalValue() {
        return crystalValue;
    }
    public void setCrystalValue(int crystalValue) {
        this.crystalValue = crystalValue;
    }
    public int getTurn() {
        return turn;
    }
    public void setTurn(int turn) {
        this.turn = turn;
    }
    @Override
    public String toString() {
        return "GameState{" +
                "initialCrystals=" + initialCrystals +
                ", initialEggs=" + initialEggs +
                ", totalCrystals=" + totalCrystals +
                ", totalEggs=" + totalEggs +
                ", myAnts=" + myAnts +
                ", opponentAnts=" + opponentAnts +
                ", strategy=" + strategy +
                ", eggsValue=" + eggsValue +
                ", crystalValue=" + crystalValue +
                ", turn=" + turn +
                '}';
    }
}
