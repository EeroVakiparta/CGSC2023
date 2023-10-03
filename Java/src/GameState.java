public class GameState {

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
