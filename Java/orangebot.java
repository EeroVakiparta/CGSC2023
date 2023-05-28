//Bundle uploaded at 05/28/2023 13:44:55
import java.util.*;
import java.util.stream.Collectors;
class GameState {
    int initialCrystals;
    int initialEggs;
    int totalCrystals;
    int totalEggs;
    int myAnts;
    int opponentAnts;
    int strategy = 0; //0 = eggs, 1 = rush, 2 = ??
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
                '}';
    }
}
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
            boolean stopCollectingEggs = isMostOfCrystalsHarvested(gameState);
            Map<Hex, List<Hex>> optimalTargetHexesWithPaths = new HashMap<>();
            if(!stopCollectingEggs){ /// IF WE ARE NOT IN ENDGAME
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
                            eggShortestPaths.put(neighbourHex, shortestPath);
                            if(neighbourHex.getResources() > neighbourHex.getMyAnts()){
                                eggsCloseToBaseFocus = true;
                            }
                        }
                    }
                }
                if(!eggsCloseToBaseFocus) {
                    for (Hex eggHex : eggHexes) {
                        List<Hex> shortestPath = getShortestPath(hexes, hexes.get(homeBaseIndex), eggHex);
                        if (shortestPath.size() <= 4) {
                            System.err.println("found egg close to base" + eggHex.getIndex());
                            if(eggHex.getResources() > eggHex.getMyAnts()){
                                eggShortestPaths.put(eggHex, shortestPath);
                            }
                        }
                    }
                }
                if(eggShortestPaths.size() > 0 && gameState.strategy == 0) {
                    //if there is eggs close to base, then should focus on collecting them first. Range is 3 hexes from base
                    //get the shortest path to each egg and add the shortest path to optimalTargets
                    optimalTargetHexesWithPaths.putAll(eggShortestPaths);
                }else{
                    //if there is no eggs close to base, then should focus on collecting crystals. First should collect crystals that are close to base
                    Map<Hex, List<Hex>> crystalShortestPaths = new HashMap<>();
                    for (Hex crystalHex : crystalHexes) {
                        List<Hex> shortestPath = getShortestPath(hexes, hexes.get(homeBaseIndex), crystalHex);
                        crystalShortestPaths.put(crystalHex, shortestPath);
                    }
                    //get the shortest path to each crystal and add the shortest path to optimalTargets
                    optimalTargetHexesWithPaths.putAll(crystalShortestPaths);
                }
            }else{
                //if only some amount of crystals left then should stop collecting eggs and only collect crystals
                //if there is no eggs close to base, then should focus on collecting crystals. First should collect crystals that are close to base
                Map<Hex, List<Hex>> crystalShortestPaths = new HashMap<>();
                for (Hex crystalHex : crystalHexes) {
                    List<Hex> shortestPath = getShortestPath(hexes, hexes.get(homeBaseIndex), crystalHex);
                    crystalShortestPaths.put(crystalHex, shortestPath);
                }
                //get the shortest path to each crystal and add the shortest path to optimalTargets
                optimalTargetHexesWithPaths.putAll(crystalShortestPaths);
            }
            //sort the optimalTargetHexesWithPaths by shortest path length
            Map<Hex, List<Hex>> sortedOptimalTargetHexesWithPaths = new LinkedHashMap<>();
            System.err.println("Sorting optimalTargetHexesWithPaths");
            optimalTargetHexesWithPaths.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.comparing(List::size)))
                    .forEachOrdered(x -> sortedOptimalTargetHexesWithPaths.put(x.getKey(), x.getValue()));
            //get the first maxOptimalTargetsCount of optimalTargetHexesWithPaths and return them
            Map<Hex, List<Hex>> sortedOptimalTargetHexesWithPathsPicks = new LinkedHashMap<>();
            int count = 0;
            System.err.println("Picking top ones");
            for (Map.Entry<Hex, List<Hex>> entry : sortedOptimalTargetHexesWithPaths.entrySet()) {
                if (count < maxOptimalTargetsCount) {
                    sortedOptimalTargetHexesWithPathsPicks.put(entry.getKey(), entry.getValue());
                    System.err.println("optimalTargetHexesWithPathsPick: " + entry.getKey());
                    count++;
                }
            }
            return sortedOptimalTargetHexesWithPathsPicks;
    }
    public static boolean isMostOfCrystalsHarvested(GameState gameState){
        boolean mostOfCrystalsHarvested = gameState.getTotalCrystals() < gameState.getInitialCrystals() / 2;
        if(mostOfCrystalsHarvested){
            System.err.println("MOST OF CRYSTALS HARVESTED");
            gameState.strategy = 1;
        }
        if(gameState.getMyAnts() > gameState.getOpponentAnts() * 1.5){
            System.err.println("MUCH MORE ANTS THAN OPPONENT");
            gameState.strategy = 1;
        }
        return mostOfCrystalsHarvested;
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
    double value;
    int distanceToMyBase;
    public Hex(int index, int type, int initialResources, int neigh0, int neigh1, int neigh2, int neigh3, int neigh4, int neigh5, int resources, int myAnts, int oppAnts, double value) {
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
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
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
            Integer[] neighbors = {
                    currentHex.getNeigh0(),
                    currentHex.getNeigh1(),
                    currentHex.getNeigh2(),
                    currentHex.getNeigh3(),
                    currentHex.getNeigh4(),
                    currentHex.getNeigh5()
            };
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
        System.err.println("numberOfBases: " + numberOfBases);
        for (int i = 0; i < numberOfBases; i++) {
            myBaseIndex = in.nextInt();
        }
        for (int i = 0; i < numberOfBases; i++) {
            int oppBaseIndex = in.nextInt();
        }
        int homeBaseIndex = myBaseIndex;  // this propably changes in higher levels to more than one base
        int turn = 0;
        /////// GAME LOOP ///////
        while (true) {
            System.err.println("turn: " + turn);
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
            //Figure out how many targets is good to approach. Depends on the number of ants. Should not spread out too thin
            int maxOptimalTargetsCount = Helpers.getMaxOptimalTargetsCount(gameState);
            //Now lets find the best targets
            Map<Hex, List<Hex>> filteredOptimalTargets = Helpers.getOptimalTargets(gameState, hexes, maxOptimalTargetsCount, homeBaseIndex);
            //Now populate with BEACONs. The command is BEACON <cellIdx> <strength> and is separated by ;
            //Collect all filteredOptimalTargets and build a string from the indexes of the hexes
            StringBuilder beaconString = new StringBuilder();
            //Add LINE command to the string LINE index1 index2 strength
//            for (Map.Entry<Hex, List<Hex>> entry : filteredOptimalTargets.entrySet()) {
//                beaconString.append("LINE ").append(homeBaseIndex).append(" ").append(entry.getKey().getIndex()).append(" 1;");
//            }
            System.err.println("Building beacon string from " + filteredOptimalTargets.size() + " targets and " + maxOptimalTargetsCount + " max targets");
            for (Map.Entry<Hex, List<Hex>> entry : filteredOptimalTargets.entrySet()) {
                //iterate through the list of hexes and add them to the string
                System.err.println("Adding hexes to beacon string" + entry.getValue().size());
                for (Hex hex : entry.getValue()) {
                    // BEACON <cellIdx> <strength> and is separated by ; strength is 1 for now but do not end with ;
                    beaconString.append("BEACON ").append(hex.getIndex()).append(" 1;");
                }
            }
            //remove last ; from string
            //beaconString.deleteCharAt(beaconString.length() - 1);
            System.out.println(beaconString);
            //if optimalTargets is empty print WAIT
            if (filteredOptimalTargets.isEmpty()) {
                System.out.print("WAIT;MESSAGE no optimal targets;");
            }
            turn++;
        }
    }
}
