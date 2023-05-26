import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        int myBaseIndex = 0;
        List<Hex> hexList = new ArrayList<>();
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

            Hex hex = new Hex(i, type, initialResources, neigh0, neigh1, neigh2, neigh3, neigh4, neigh5, 0, 0, 0, 0);
            //System.err.println(hex);
            hexList.add(hex);

        }
        int numberOfBases = in.nextInt();
        for (int i = 0; i < numberOfBases; i++) {
            myBaseIndex = in.nextInt();
        }
        for (int i = 0; i < numberOfBases; i++) {
            int oppBaseIndex = in.nextInt();
        }

        Hex myBaseHex = new Hex (0,0,0,0,0,0,0,0,0,0,0,0,0);
        for (Hex hex : hexList) {
            if (hex.getIndex() == myBaseIndex) {
                myBaseHex = hex;
                System.err.println("My base is" + hex.getIndex());
            }
        }

        // game loop
        while (true) {

            for (int i = 0; i < numberOfCells; i++) {
                int resources = in.nextInt(); // the current amount of eggs/crystals on this cell
                int myAnts = in.nextInt(); // the amount of your ants on this cell
                int oppAnts = in.nextInt(); // the amount of opponent ants on this cell

                for (Hex hex : hexList) {
                    if (hex.getIndex() == i) {
                        hex.setResources(resources);
                        hex.setMyAnts(myAnts);
                        hex.setOppAnts(oppAnts);
                    }
                }

            }





            for (int i = 0; i < hexList.size(); i++) {
                if (hexList.get(i).getResources() == 0) {
                    hexList.remove(i);
                }
            }
            //TODO: fix egg calculations
            int eggs = 0;
            int crystals = 0;
            for (Hex hex : hexList) {
                if (hex.getType() == 1&& hex.getResources() > 10) {
                    eggs++;
                } else if (hex.getType() == 2 && hex.getResources() > 10) {
                    crystals++;
                }
            }

            if(eggs + 1 > crystals || crystals < 6) {

                for (int i = 0; i < hexList.size(); i++) {
                    if (hexList.get(i).getType() == 1 && !Helpers.isNeighbour(myBaseHex, hexList.get(i))) {
                        hexList.remove(i);
                    }
                }
            }

            //int strength =  hexList.size();
            int strength = 1;


            List<Hex> neighbours = new ArrayList<>();
            List<Hex> neighboursNeighbours = new ArrayList<>();




            for (Hex hex : hexList) {
                if (hex.getResources() == 0) {
                    continue;
                }
                if (Helpers.isNeighbour(myBaseHex, hex) && hex.getType() == 1) {
                    System.err.println("Found N1 " + hex.getIndex());
                    double currnetValue = hex.getValue();
                    neighbours.add(hex);
                    hex.setValue(currnetValue + 10);
                }else {
                    hex.setValue(strength);
                }
                if (Helpers.isNeighBoursNeighbour(myBaseHex, hex) && hex.getType() == 1) {
                    System.err.println("Found N2 " + hex.getIndex());
                    double currnetValue = hex.getValue();
                    neighboursNeighbours.add(hex);
                    hex.setValue(currnetValue + 5);
                } else {
                    hex.setValue(strength);
                }


            }


            List<Hex> hexListForOutput = new ArrayList<>();

            // neighbours.size() > 2
            if (!neighbours.isEmpty() ) {

                System.err.println("Fneighbours.size " + neighbours.size());
                hexListForOutput = neighbours;
            } else {
                hexList.addAll(neighbours);
                hexListForOutput = hexList;
            }

            for (Hex hex : hexListForOutput) {
                if (hex.getResources() > 0) {
                    int strenght = (int) (hex.getValue() + (hex.getResources() / 10));
                    double currnetValue = hex.getValue();
                    hex.setValue(currnetValue + strenght);
                }
            }




            String output = "";
            for (Hex hex : hexListForOutput) {
                if (hex.getResources() > 0) {
                    int strenght = (int) (hex.getValue());
                    output += "LINE " + myBaseIndex + " " + hex.getIndex() + " " + strenght + ";";
                }
            }
            if(hexListForOutput.isEmpty()){
                System.out.println("WAIT");
            }else{
                System.out.println(output);
            }
        }
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
    int myAnts; // the current amount of your ants on this cell
    int oppAnts; // the current amount of opponent ants on this cell

    double value;

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
                '}';
    }

    public List<Integer> getNeighbours() {
        return Arrays.asList(neigh0, neigh1, neigh2, neigh3, neigh4, neigh5);
    }
}

class Helpers {

    public static boolean isNeighbour(Hex hex1, Hex hex2){
        if (hex1.getNeigh0() == hex2.getIndex() || hex1.getNeigh1() == hex2.getIndex() || hex1.getNeigh2() == hex2.getIndex() || hex1.getNeigh3() == hex2.getIndex() || hex1.getNeigh4() == hex2.getIndex() || hex1.getNeigh5() == hex2.getIndex()){
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isNeighBoursNeighbour(Hex hex1, Hex hex2) {
        List < Integer > neighbours = hex1.getNeighbours();
        List < Integer > neighbours2 = hex2.getNeighbours();



        for (int i = 0; i < neighbours.size(); i++) {
            if (neighbours2.contains(neighbours.get(i)) && neighbours.get(i) != -1) {
                System.err.println("Found this Integer in both lists: " + neighbours.get(i));
                return true;
            }
        }
        return false;
    }


    public static double UpdateHexValue(Hex hex, Hex stamyBase , List<Hex> allHexes) {
        hex.setValue(hex.getResources() / DistanceBetweenHexes(hex, stamyBase, allHexes));
        return hex.getValue();

    }


}