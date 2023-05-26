import java.util.*;

public class Testi {
    public static List<Integer> findShortestPath(List<Hex> hexagons, Hex startingHex, Hex targetHex) {
        Map<Integer, Hex> hexMap = new HashMap<>();
        for (Hex hex : hexagons) {
            hexMap.put(hex.getIndex(), hex);
        }

        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (Hex hex : hexagons) {
            int hexIndex = hex.getIndex();
            distances.put(hexIndex, Integer.MAX_VALUE);
            previous.put(hexIndex, null);
        }

        int startingIndex = startingHex.getIndex();
        distances.put(startingIndex, 0);
        queue.add(startingIndex);

        while (!queue.isEmpty()) {
            int currentHexIndex = queue.poll();
            Hex currentHex = hexMap.get(currentHexIndex);

            if (currentHexIndex == targetHex.getIndex()) {
                break; // Found the shortest path, exit the loop
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

                    if (distances.get(neighborIndex) == null || distance < distances.get(neighborIndex)) {
                        distances.put(neighborIndex, distance);
                        previous.put(neighborIndex, currentHexIndex);
                        queue.add(neighborIndex);
                    }
                }
            }
        }

        List<Integer> shortestPath = new ArrayList<>();
        Integer currentIndex = targetHex.getIndex();

        while (currentIndex != null) {
            shortestPath.add(0, currentIndex);
            currentIndex = previous.get(currentIndex);
        }

        return shortestPath;
    }

    public static void main(String[] args) {
        // Example usage
        List<Hex> hexagons = new ArrayList<>();
        hexagons.add(new Hex(8, 0, 0, 18, -1, -1, 10, 0, 6, 0, 0, 0, 0));
        hexagons.add(new Hex(28, 0, 21, -1, -1, -1, 18, -1, -1, 0, 0, 0, 0));
        hexagons.add(new Hex(18, 0, 11, 28, -1, -1, 8, 6, -1, 0, 0, 0, 0));
        hexagons.add(new Hex(0, 0, 0, 6, 8, 10, -1, -1, -1, 0, 0, 0, 0));
        hexagons.add(new Hex(6, 0, 42, 18, 8, 0, -1, -1, -1, 0, 0, 0, 0));
        hexagons.add(new Hex(10, 0, 0, 8, -1, -1, -1, -1, 0, 0, 0, 0, 0));

        Hex startingHex = hexagons.get(0);
        Hex targetHex = hexagons.get(1);

        List<Integer> shortestPath = findShortestPath(hexagons, startingHex, targetHex);
        System.out.println("Shortest path: " + shortestPath);
    }
}
