import java.util.*;

class Player {
    private Game game;

    public static void main(String args[]) {
        Game game = new Game();
        game.main();
    }
}

class Game {

    private Joueur ally;
    private Joueur ennemy;

    public void main() {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        int myId = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }

        this.ally = new Joueur(myId);
        this.ennemy = new Joueur(myId == 1 ? 0 : 1);

        Carte carte = new Carte(width, height);
        for (int y = 0; y < height; y++) {
            String line = in.nextLine();
            System.err.println(line);
            for(int x = 0; x<line.length() ; x++) {
                TypeCell typeCell = null;
                if(line.charAt(x) == '.') {
                    typeCell = TypeCell.MER;
                } else if(line.charAt(x) == 'X') {
                    typeCell = TypeCell.ILE;
                }
                Cell cell = new Cell( x, y, typeCell);
                carte.addCell(cell);
            }
        }
        ally.setCarte(carte);
        ennemy.setCarte(carte);

        carte.showCarte();
        // choose player position
        // V1
        ally.setX(7);
        ally.setY(7);


        // V2

        System.out.println(ally.getX()+" "+ally.getY());

        // game loop
        while (true) {
            int x = in.nextInt();
            ally.setX(x);
            int y = in.nextInt();
            ally.setY(y);
            int myLife = in.nextInt();
            ally.setLife(myLife);
            int oppLife = in.nextInt();
            ennemy.setLife(oppLife);
            int torpedoCooldown = in.nextInt();
            ally.setTorpedoCooldown(torpedoCooldown);
            int sonarCooldown = in.nextInt();
            ally.setSonarCooldown(sonarCooldown);
            int silenceCooldown = in.nextInt();
            ally.setSilenceCooldown(silenceCooldown);
            int mineCooldown = in.nextInt();
            ally.setMineCooldown(mineCooldown);

            String sonarResult = in.next();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            String opponentOrders = in.nextLine();

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("MOVE N TORPEDO");
        }
    }
}

// Joueur instead of Player because of game restriction object
class Joueur {

    private int id;
    private boolean ally;

    private int x;
    private int y;


    private int life;

    final int maxLife = 6;

    private int torpedoCooldown;
    private int sonarCooldown;
    private int silenceCooldown;
    private int mineCooldown;

    private Carte carte;
    private List<Carte> potentialCarte;

    public Joueur(int id) {
        this.id = id;
        this.life = maxLife;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getTorpedoCooldown() {
        return torpedoCooldown;
    }

    public void setTorpedoCooldown(int torpedoCooldown) {
        this.torpedoCooldown = torpedoCooldown;
    }

    public int getSonarCooldown() {
        return sonarCooldown;
    }

    public void setSonarCooldown(int sonarCooldown) {
        this.sonarCooldown = sonarCooldown;
    }

    public int getSilenceCooldown() {
        return silenceCooldown;
    }

    public void setSilenceCooldown(int silenceCooldown) {
        this.silenceCooldown = silenceCooldown;
    }

    public int getMineCooldown() {
        return mineCooldown;
    }

    public void setMineCooldown(int mineCooldown) {
        this.mineCooldown = mineCooldown;
    }

    public void setCarte(Carte carte) {
        this.carte = new Carte(carte);
    }

    public Carte getCarte() {
        return this.carte;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

// Carte instead of Map because of object Map
class Carte {
    private int width;
    private int height;
    List<Cell> cells;

    public Carte(int width, int height, List<Cell> cells) {
        this.width = width;
        this.height = height;
        this.cells = new ArrayList(cells);
    }

    public Carte(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new ArrayList();
    }

    public Carte(Carte carte) {
        this.width = carte.width;
        this.height = carte.height;
        this.cells = new ArrayList(carte.cells);
    }


    public void addCell(Cell cell) {
        this.cells.add(cell);
    }

    public void showCarte() {
        String[][] tempCarte = new String[width][height];
        // feed temp carte
        for(Cell cell : cells) {
            tempCarte[cell.getY()][cell.getX()] = cell.graphicCellV1();
        }
        // show temp carte
        for (int x = 0; x < this.height; x++) {
            StringBuilder line = new StringBuilder();
            for(int y = 0; y< this.width ; y++) {
                line.append(tempCarte[x][y]).append(" ");
            }
            System.err.println(line);
        }
    }
}

class Cell {
    private int x;
    private int y;
    private int zone;
    private TypeCell typeCell;

    private int mined;// 1 = y ; 2 = n; 3 = maybe
    private boolean pathUsed;

    private boolean containSubmarine;

    final int sizeCase = 5;
    final int nbColumn = 3;

    public Cell(int x, int y, TypeCell typeCell) {
        this.x = x;
        this.y = y;
        this.typeCell = typeCell;

        int a = x / sizeCase;
        int b = y / sizeCase;
        this.zone = ((a+nbColumn*b)+1);

        this.mined = 2;
    }

    public String graphicCellV1() {
        return (this.pathUsed ? "o" : this.typeCell == TypeCell.MER ? "X" : ".");
    }

    public String graphicCellV2() {
        return this.mined + (this.typeCell == TypeCell.MER ? "MER" : "ILE") + (this.pathUsed ? "y" : "n") + (this.containSubmarine ? "s" : "e");
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}

enum TypeCell {
    MER, ILE
}

enum ObjectCell {
    CHEMIN, MINE, SUBMARINE;
}

enum Direction {
    N, S, E, W;
}

enum TypeAction {
    SURFACE, SILENCE, MOVE, SONAR, TORPEDO, MINE;
}