import java.util.List;

public class Helpers {

    public static boolean isNeighbour(Hex hex1, Hex hex2) {
        if (hex1.getNeigh0() == hex2.getIndex() || hex1.getNeigh1() == hex2.getIndex() || hex1.getNeigh2() == hex2.getIndex() || hex1.getNeigh3() == hex2.getIndex() || hex1.getNeigh4() == hex2.getIndex() || hex1.getNeigh5() == hex2.getIndex()) {
            return true;
        } else {
            return false;
        }
    }

        //Checks if two hexes have common neighbour which means they are 1 hex away from each other
        public static boolean isNeighBoursNeighbour(Hex hex1, Hex hex2) {
            //get all neighbours of hex1 and put them in list of integers
            List < Integer > neighbours = hex1.getNeighbours();
            //get all neighbours of hex2 and put them in list of integers
            List < Integer > neighbours2 = hex2.getNeighbours();

            //check if any of the neighbours of hex1 are neighbours of hex2 except -1
            for (int i = 0; i < neighbours.size(); i++) {
                if (neighbours2.contains(neighbours.get(i)) && neighbours.get(i) != -1) {
                    return true;
                }
            }
            return false;
        }


        //method which takes a hex, calculates and sets its value
        public static double UpdateHexValue (Hex hex, Hex stamyBase, List < Hex > allHexes){
            // value is the sum of resources combined with the closenes to the base. Use to determine which hexes to send ants to.
            hex.setValue(hex.getResources() / DistanceBetweenHexes(hex, stamyBase, allHexes));
            return hex.getValue();

        }


        //method takes two hexes and returns the distance between them. Hexes are in a hexagonal grid. and neighbours are neigh0 to neigh5.
        //distance of 5 is the maximum calculated distance. If the distance is greater than 5, it is set to 6.
        public static int DistanceBetweenHexes (Hex hex1, Hex hex2, List < Hex > allHexes){
            int distance = 0;
            int index1 = hex1.getIndex();
            int index2 = hex2.getIndex();
            int neigh0 = hex1.getNeigh0();
            int neigh1 = hex1.getNeigh1();
            int neigh2 = hex1.getNeigh2();
            int neigh3 = hex1.getNeigh3();
            int neigh4 = hex1.getNeigh4();
            int neigh5 = hex1.getNeigh5();

            //if hex2 is a neighbour of hex1
            if (index2 == neigh0 || index2 == neigh1 || index2 == neigh2 || index2 == neigh3 || index2 == neigh4 || index2 == neigh5) {
                distance = 1;
            }
            //if hex2 is not a neighbour of hex1
            else {
                //if hex2 is not a neighbour of hex1, but is a neighbour of one of hex1's neighbours
                if (index2 == allHexes.get(neigh0).getNeigh0() || index2 == allHexes.get(neigh0).getNeigh1() || index2 == allHexes.get(neigh0).getNeigh2() || index2 == allHexes.get(neigh0).getNeigh3() || index2 == allHexes.get(neigh0).getNeigh4() || index2 == allHexes.get(neigh0).getNeigh5()) {
                    distance = 2;
                } else if (index2 == allHexes.get(neigh1).getNeigh0() || index2 == allHexes.get(neigh1).getNeigh1() || index2 == allHexes.get(neigh1).getNeigh2() || index2 == allHexes.get(neigh1).getNeigh3() || index2 == allHexes.get(neigh1).getNeigh4() || index2 == allHexes.get(neigh1).getNeigh5()) {
                    distance = 2;
                } else if (index2 == allHexes.get(neigh2).getNeigh0() || index2 == allHexes.get(neigh2).getNeigh1() || index2 == allHexes.get(neigh2).getNeigh2() || index2 == allHexes.get(neigh2).getNeigh3() || index2 == allHexes.get(neigh2).getNeigh4() || index2 == allHexes.get(neigh2).getNeigh5()) {
                    distance = 2;
                } else if (index2 == allHexes.get(neigh3).getNeigh0() || index2 == allHexes.get(neigh3).getNeigh1() || index2 == allHexes.get(neigh3).getNeigh2() || index2 == allHexes.get(neigh3).getNeigh3() || index2 == allHexes.get(neigh3).getNeigh4() || index2 == allHexes.get(neigh3).getNeigh5()) {
                    distance = 2;
                } else if (index2 == allHexes.get(neigh4).getNeigh0() || index2 == allHexes.get(neigh4).getNeigh1() || index2 == allHexes.get(neigh4).getNeigh2() || index2 == allHexes.get(neigh4).getNeigh3() || index2 == allHexes.get(neigh4).getNeigh4() || index2 == allHexes.get(neigh4).getNeigh5()) {
                    distance = 2;
                } else if (index2 == allHexes.get(neigh5).getNeigh0() || index2 == allHexes.get(neigh5).getNeigh1() || index2 == allHexes.get(neigh5).getNeigh2() || index2 == allHexes.get(neigh5).getNeigh3() || index2 == allHexes.get(neigh5).getNeigh4() || index2 == allHexes.get(neigh5).getNeigh5()) {
                    distance = 2;
                }
                //if hex2 is not a neighbour of hex1, but is a neighbour of one of hex1's neighbours' neighbours
                else {
                    if (index2 == allHexes.get(neigh0).getNeigh0() || index2 == allHexes.get(neigh0).getNeigh1() || index2 == allHexes.get(neigh0).getNeigh2() || index2 == allHexes.get(neigh0).getNeigh3() || index2 == allHexes.get(neigh0).getNeigh4() || index2 == allHexes.get(neigh0).getNeigh5()) {
                        distance = 3;
                    } else if (index2 == allHexes.get(neigh1).getNeigh0() || index2 == allHexes.get(neigh1).getNeigh1() || index2 == allHexes.get(neigh1).getNeigh2() || index2 == allHexes.get(neigh1).getNeigh3() || index2 == allHexes.get(neigh1).getNeigh4() || index2 == allHexes.get(neigh1).getNeigh5()) {
                        distance = 3;
                    } else if (index2 == allHexes.get(neigh2).getNeigh0() || index2 == allHexes.get(neigh2).getNeigh1() || index2 == allHexes.get(neigh2).getNeigh2() || index2 == allHexes.get(neigh2).getNeigh3() || index2 == allHexes.get(neigh2).getNeigh4() || index2 == allHexes.get(neigh2).getNeigh5()) {
                        distance = 3;
                    } else if (index2 == allHexes.get(neigh3).getNeigh0() || index2 == allHexes.get(neigh3).getNeigh1() || index2 == allHexes.get(neigh3).getNeigh2() || index2 == allHexes.get(neigh3).getNeigh3() || index2 == allHexes.get(neigh3).getNeigh4() || index2 == allHexes.get(neigh3).getNeigh5()) {
                        distance = 3;
                    } else if (index2 == allHexes.get(neigh4).getNeigh0() || index2 == allHexes.get(neigh4).getNeigh1() || index2 == allHexes.get(neigh4).getNeigh2() || index2 == allHexes.get(neigh4).getNeigh3() || index2 == allHexes.get(neigh4).getNeigh4() || index2 == allHexes.get(neigh4).getNeigh5()) {
                        distance = 3;
                    } else if (index2 == allHexes.get(neigh5).getNeigh0() || index2 == allHexes.get(neigh5).getNeigh1() || index2 == allHexes.get(neigh5).getNeigh2() || index2 == allHexes.get(neigh5).getNeigh3() || index2 == allHexes.get(neigh5).getNeigh4() || index2 == allHexes.get(neigh5).getNeigh5()) {
                        distance = 3;
                    }
                    //if hex2 is not a neighbour of hex1, but is a neighbour of one of hex1's neighbours' neighbours' neighbours
                    else {
                        if (index2 == allHexes.get(neigh0).getNeigh0() || index2 == allHexes.get(neigh0).getNeigh1() || index2 == allHexes.get(neigh0).getNeigh2() || index2 == allHexes.get(neigh0).getNeigh3() || index2 == allHexes.get(neigh0).getNeigh4() || index2 == allHexes.get(neigh0).getNeigh5()) {
                            distance = 4;
                        } else if (index2 == allHexes.get(neigh1).getNeigh0() || index2 == allHexes.get(neigh1).getNeigh1() || index2 == allHexes.get(neigh1).getNeigh2() || index2 == allHexes.get(neigh1).getNeigh3() || index2 == allHexes.get(neigh1).getNeigh4() || index2 == allHexes.get(neigh1).getNeigh5()) {
                            distance = 4;
                        } else if (index2 == allHexes.get(neigh2).getNeigh0() || index2 == allHexes.get(neigh2).getNeigh1() || index2 == allHexes.get(neigh2).getNeigh2() || index2 == allHexes.get(neigh2).getNeigh3() || index2 == allHexes.get(neigh2).getNeigh4() || index2 == allHexes.get(neigh2).getNeigh5()) {
                            distance = 4;
                        } else if (index2 == allHexes.get(neigh3).getNeigh0() || index2 == allHexes.get(neigh3).getNeigh1() || index2 == allHexes.get(neigh3).getNeigh2() || index2 == allHexes.get(neigh3).getNeigh3() || index2 == allHexes.get(neigh3).getNeigh4() || index2 == allHexes.get(neigh3).getNeigh5()) {
                            distance = 4;
                        } else if (index2 == allHexes.get(neigh4).getNeigh0() || index2 == allHexes.get(neigh4).getNeigh1() || index2 == allHexes.get(neigh4).getNeigh2() || index2 == allHexes.get(neigh4).getNeigh3() || index2 == allHexes.get(neigh4).getNeigh4() || index2 == allHexes.get(neigh4).getNeigh5()) {
                            distance = 4;
                        } else if (index2 == allHexes.get(neigh5).getNeigh0() || index2 == allHexes.get(neigh5).getNeigh1() || index2 == allHexes.get(neigh5).getNeigh2() || index2 == allHexes.get(neigh5).getNeigh3() || index2 == allHexes.get(neigh5).getNeigh4() || index2 == allHexes.get(neigh5).getNeigh5()) {
                            distance = 4;
                        }
                        //if hex2 is not a neighbour of hex1, but is a neighbour of one of hex1's neighbours' neighbours' neighbours' neighbours
                        else {
                            if (index2 == allHexes.get(neigh0).getNeigh0() || index2 == allHexes.get(neigh0).getNeigh1() || index2 == allHexes.get(neigh0).getNeigh2() || index2 == allHexes.get(neigh0).getNeigh3() || index2 == allHexes.get(neigh0).getNeigh4() || index2 == allHexes.get(neigh0).getNeigh5()) {
                                distance = 5;
                            } else if (index2 == allHexes.get(neigh1).getNeigh0() || index2 == allHexes.get(neigh1).getNeigh1() || index2 == allHexes.get(neigh1).getNeigh2() || index2 == allHexes.get(neigh1).getNeigh3() || index2 == allHexes.get(neigh1).getNeigh4() || index2 == allHexes.get(neigh1).getNeigh5()) {
                                distance = 5;
                            } else if (index2 == allHexes.get(neigh2).getNeigh0() || index2 == allHexes.get(neigh2).getNeigh1() || index2 == allHexes.get(neigh2).getNeigh2() || index2 == allHexes.get(neigh2).getNeigh3() || index2 == allHexes.get(neigh2).getNeigh4() || index2 == allHexes.get(neigh2).getNeigh5()) {
                                distance = 5;
                            } else if (index2 == allHexes.get(neigh3).getNeigh0() || index2 == allHexes.get(neigh3).getNeigh1() || index2 == allHexes.get(neigh3).getNeigh2() || index2 == allHexes.get(neigh3).getNeigh3() || index2 == allHexes.get(neigh3).getNeigh4() || index2 == allHexes.get(neigh3).getNeigh5()) {
                                distance = 5;
                            } else if (index2 == allHexes.get(neigh4).getNeigh0() || index2 == allHexes.get(neigh4).getNeigh1() || index2 == allHexes.get(neigh4).getNeigh2() || index2 == allHexes.get(neigh4).getNeigh3() || index2 == allHexes.get(neigh4).getNeigh4() || index2 == allHexes.get(neigh4).getNeigh5()) {
                                distance = 5;
                            } else if (index2 == allHexes.get(neigh5).getNeigh0() || index2 == allHexes.get(neigh5).getNeigh1() || index2 == allHexes.get(neigh5).getNeigh2() || index2 == allHexes.get(neigh5).getNeigh3() || index2 == allHexes.get(neigh5).getNeigh4() || index2 == allHexes.get(neigh5).getNeigh5()) {
                                distance = 5;
                            }
                            //if hex2 is not a neighbour of hex1, but is a neighbour of one of hex1's neighbours' neighbours' neighbours' neighbours' neighbours' neighbours
                            else {
                                if (index2 == allHexes.get(neigh0).getNeigh0() || index2 == allHexes.get(neigh0).getNeigh1() || index2 == allHexes.get(neigh0).getNeigh2() || index2 == allHexes.get(neigh0).getNeigh3() || index2 == allHexes.get(neigh0).getNeigh4() || index2 == allHexes.get(neigh0).getNeigh5()) {
                                    distance = 6;
                                } else if (index2 == allHexes.get(neigh1).getNeigh0() || index2 == allHexes.get(neigh1).getNeigh1() || index2 == allHexes.get(neigh1).getNeigh2() || index2 == allHexes.get(neigh1).getNeigh3() || index2 == allHexes.get(neigh1).getNeigh4() || index2 == allHexes.get(neigh1).getNeigh5()) {
                                    distance = 6;
                                } else if (index2 == allHexes.get(neigh2).getNeigh0() || index2 == allHexes.get(neigh2).getNeigh1() || index2 == allHexes.get(neigh2).getNeigh2() || index2 == allHexes.get(neigh2).getNeigh3() || index2 == allHexes.get(neigh2).getNeigh4() || index2 == allHexes.get(neigh2).getNeigh5()) {
                                    distance = 6;
                                } else if (index2 == allHexes.get(neigh3).getNeigh0() || index2 == allHexes.get(neigh3).getNeigh1() || index2 == allHexes.get(neigh3).getNeigh2() || index2 == allHexes.get(neigh3).getNeigh3() || index2 == allHexes.get(neigh3).getNeigh4() || index2 == allHexes.get(neigh3).getNeigh5()) {
                                    distance = 6;
                                } else if (index2 == allHexes.get(neigh4).getNeigh0() || index2 == allHexes.get(neigh4).getNeigh1() || index2 == allHexes.get(neigh4).getNeigh2() || index2 == allHexes.get(neigh4).getNeigh3() || index2 == allHexes.get(neigh4).getNeigh4() || index2 == allHexes.get(neigh4).getNeigh5()) {
                                    distance = 6;
                                } else if (index2 == allHexes.get(neigh5).getNeigh0() || index2 == allHexes.get(neigh5).getNeigh1() || index2 == allHexes.get(neigh5).getNeigh2() || index2 == allHexes.get(neigh5).getNeigh3() || index2 == allHexes.get(neigh5).getNeigh4() || index2 == allHexes.get(neigh5).getNeigh5()) {
                                    distance = 6;
                                } else {
                                    distance = 7;
                                }


                            }
                        }
                    }
                }

            }
            return distance;
        }
    }