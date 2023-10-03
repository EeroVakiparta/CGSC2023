import java.util.*;

public class PathFinder {
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
