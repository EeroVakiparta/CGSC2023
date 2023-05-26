import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
public class Player {

    //Kirjotetaanpa uusiksi.
    //Edellisestä yrityksestä hyviä löytöjä:
    // - LINE tekee huonoja valintoja jos on useampi yhtä pitkä reitti
    // - Munia ei kannata kerätä loppuun asti, jos resurssit on jo muutenkin vähissä
    // - ehkei kaikkia murkkuja kannata levittää vaan käyttää muutamaa lonkeroa aluksi ja lisätä kun murkkuja tulee lisää
    // - Älä lankea -1 bugiin :D Muista että -1 on no neighbour

    //Uuteen versioon TODO:
    // Lisää kartta luokka
    // total crystals total
    // total eggs
    // Joku syy lopettaa munien kerääminen
    // Joku raja targeteille. Esim murkut /5
    // silloin 10 /5 = 2
    // 20/5 = 4
    // 60/5 = 12
    // esto sille ettei tuu huonoja laneja
    // lyhin reittihaku ? Pitäiskö tehä reittejä ja tallentaa ne ettei tartte aina laskee
    // Ton Linen käyttö vähän mauton. Paras tehä oma.

    public static void main(String args[]) {
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
        }
        int numberOfBases = in.nextInt();
        for (int i = 0; i < numberOfBases; i++) {
            int myBaseIndex = in.nextInt();
        }
        for (int i = 0; i < numberOfBases; i++) {
            int oppBaseIndex = in.nextInt();
        }

        // game loop
        while (true) {
            for (int i = 0; i < numberOfCells; i++) {
                int resources = in.nextInt(); // the current amount of eggs/crystals on this cell
                int myAnts = in.nextInt(); // the amount of your ants on this cell
                int oppAnts = in.nextInt(); // the amount of opponent ants on this cell
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>
            System.out.println("WAIT");
        }
    }
}