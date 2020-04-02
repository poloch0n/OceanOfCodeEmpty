import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Remarque : beaucoup de variables/fonctions sont en static pour la seule est unique raison suivante : flemme. Wesh.
 * Dirty but quicky, pls don't judge me
 **/
class Player {
    public static Scanner listener;

    public static int width;
    public static int height;
    public static int myId;

    public static int myTorpedoCoolDown;
    public static int mySonarCoolDown;
    public static int mySilenceCoolDown;

    public static int lastSonarZoneCalled;

    public static int[][] globalReferenceMap;
    public static int[][] allyMap;
    public static int[][] ennemyMap;

    public static List<Carte> potentialEnnemyMaps;

    public static List<Action> AllyListMove;
    public static List<Action> EnnemyListMove;

    public static final int mapTerre = 0;
    public static final int mapMer = 1;
    public static final int mapChemin = 2;
    public static final int sousmarinAlly = 5;
    public static final int sousmarinEnnemy = 6;
    
    public static List<Direction> allDirectionsAvailible;

    public static List<String> myOrders;

    public static void main(String args[]) {

        init();
        release();

        // game loop
        while(true) {
            round();
            release();
        }
    }

    public static void init() {
        listener = new Scanner(System.in);
        width = listener.nextInt();
        height = listener.nextInt();
        myId = listener.nextInt();
        if (listener.hasNextLine()) {
            listener.nextLine();
        }

        globalReferenceMap = new int[width][height];
        // map(x, y)
        // 0 = mer
        // 1 = terre
        // 2 = déjà passé par là
        for (int y = 0; y < height; y++) {
            String line = listener.nextLine();
            System.err.println(line);
            for(int x = 0; x<line.length() ; x++) {
                if(line.charAt(x) == '.') {
                    globalReferenceMap[x][y] = mapMer;
                } else if(line.charAt(x) == 'X') {
                    globalReferenceMap[x][y] = mapTerre;
                }
            }
        }
        initAllyMap();

        // initialise all potentials paths
        initEnnemyMap();


        myOrders = new ArrayList();

        // initialise the position 
        chooseInitialPosition();

        // initialise cooldown
        myTorpedoCoolDown = 0;
        mySonarCoolDown = 0;
        mySilenceCoolDown = 0;
        lastSonarZoneCalled = 0;

        allDirectionsAvailible = new ArrayList();
        allDirectionsAvailible.add(Direction.W);
        allDirectionsAvailible.add(Direction.E);
        allDirectionsAvailible.add(Direction.N);
        allDirectionsAvailible.add(Direction.S);
    }

    public static void round() {
        int x = listener.nextInt();
        int y = listener.nextInt();
        int myLife = listener.nextInt();
        int oppLife = listener.nextInt();
        int torpedoCooldown = listener.nextInt();
        int sonarCooldown = listener.nextInt();
        int silenceCooldown = listener.nextInt();

        System.err.println("list cooldown :");
        System.err.println("Torpille : "+torpedoCooldown);
        // Attention, si vous n'avez pas encore débloquer les armes ,potentiel décalage de ligne
        System.err.println("Sonar :"+sonarCooldown);
        System.err.println("Silence "+silenceCooldown);
        int mineCooldown = listener.nextInt();
        System.err.println("Mine "+mineCooldown);

        String sonarResult = listener.next();
        System.err.println("reultat sonar"+ " "+sonarResult);
        if (listener.hasNextLine()) {
            listener.nextLine();
        }

        // Affichage des positions possible // Debug
        System.err.println("");
        if(potentialEnnemyMaps.size() == 1 ) {
            System.err.println("position ennemy : " + potentialEnnemyMaps.get(0).x+" "+potentialEnnemyMaps.get(0).y);
        } else {
            System.err.println("nombre de position possible : "+potentialEnnemyMaps.size());
            if(potentialEnnemyMaps.size() == 0) {
                System.err.println("ALERTE RADAR PERDU");
                initEnnemyMap();
            }
        }

        List<Carte> temporaryPotentialEnnemyMaps = null;

        // résultat SONAR
        updatePotentialEnnemyMapsWithSonarResult(sonarResult);


        ////// RADAR

        String opponentOrders = listener.nextLine();

        // parser par "|""
        // pour chaque ordre vérifier si y a move
        String[] ordersEnnemy = opponentOrders.split("\\|");
        updatePotentialEnnemyMapsWithOrder(ordersEnnemy);

        // show current position
        markMoveAllyMap( x,  y);

        // show ally map :
        showAllyMap("show ally map");

        myOrders();

    }

    public static void release() {
        System.out.println(String.join(" | ", myOrders));
        myOrders = new ArrayList();
    }

    public static void chooseInitialPosition() {
        // v1
        myOrders.add("7 7");
    }
    public static void markMoveAllyMap(int x, int y) {
        allyMap[x][y] = mapChemin;
    }

    public static void showAllyMap(String message) {
        System.err.println(message);
        for (int ys = 0; ys < height; ys++) {
            String showLine = "";
            for(int xs = 0; xs < width ; xs++) {
                showLine += allyMap[xs][ys];
            }
            System.err.println(showLine);
        }
    }

    public static void updatePotentialEnnemyMapsWithSonarResult(String resultSonar) {
        // Do something here ...
    }

    public static void updatePotentialEnnemyMapsWithMove(Direction direction) {
        // Do something here ...
    }

    public static void updatePotentialEnnemyMapsWithSurface(int zone) {
        // Do something here ...
    }

    public static void updatePotentialEnnemyMapsWithTorpedo(int x, int y) {
        // Do something here ...
    }

    public static void updatePotentialEnnemyMapsWithSilence() {
        // Do something here ...
    }

    public static void updatePotentialEnnemyMapsWithOrder(String[] ennemyOrders) {

        for(String order : ennemyOrders) {
            System.err.println("update for ennemy order : "+order);

            if(order.startsWith("MOVE")) {
                updatePotentialEnnemyMapsWithMove(Direction.valueOf(""+order.charAt(5)));
            } else if (order.contains("SURFACE")) {
                updatePotentialEnnemyMapsWithSurface(Integer.parseInt(""+order.charAt(8)));
            } else if (order.startsWith("TORPEDO")) {
                String[] orderTorpedo = order.split(" ");
                int am =  Integer.parseInt(orderTorpedo[1]);
                int bm =  Integer.parseInt(orderTorpedo[2]);
                updatePotentialEnnemyMapsWithTorpedo(am, bm);
            } else if (order.startsWith("SILENCE")) {
                updatePotentialEnnemyMapsWithSilence();
            }
        }
    }

    public static void initEnnemyMap() {
        potentialEnnemyMaps = new ArrayList();
        for (int yp = 0; yp < height; yp++) {
            for(int xp = 0; xp < width; xp++) {
                if(globalReferenceMap[xp][yp] == mapMer){
                    Carte ennemyMap = new Carte(xp, yp, globalReferenceMap);
                    potentialEnnemyMaps.add( ennemyMap );
                }
            }
        }
    }

    public static void initAllyMap() {
        allyMap = new int[width][height];
        for (int ys = 0; ys < height; ys++) {
            for(int xs = 0; xs < width ; xs++) {
                allyMap[xs] = globalReferenceMap[xs].clone();
            }
        }
    }

    public static boolean isDirectionAuthorised(Direction direction, int x, int y, int[][] carte) {
        boolean allow = false;

        switch (direction) {
            case N:
            if((y-1) >= 0 && (carte[x][y-1] == mapMer || carte[x][y-1] == sousmarinEnnemy || carte[x][y-1] == sousmarinAlly)) {
                allow = true;
            }
            break;
            case E:
            if((x+1) < width && (carte[x+1][y] == mapMer || carte[x+1][y] == sousmarinEnnemy || carte[x+1][y] == sousmarinAlly)) {
                allow = true;
            }
            break;
            case S:
            if((y+1) < height && (carte[x][y+1] == mapMer || carte[x][y+1] == sousmarinEnnemy || carte[x][y+1] == sousmarinAlly)) {
                allow = true;
            }
            break;
            case W:
            if((x-1) >= 0 && (carte[x-1][y] == mapMer || carte[x-1][y] == sousmarinEnnemy || carte[x-1][y] == sousmarinAlly)) {
                allow = true;
            }
            break;
        }
        return allow;
    }

    public static void myOrders() {
        myOrders.add("MOVE N TORPEDO");
    }

}

enum Direction {
    N, S, E, W;
}

enum Type {
    SURFACE, SILENCE, MOVE, SONAR, TORPEDO;
}

class Action {
    Type type;
    public Action() {

    }
}

class Move extends Action {
    Direction direction;
    public Move(Direction direction) {
        this.type = Type.MOVE;
        this.direction = direction;
    }
}

class Power extends Action {
    public Power() {

    }
}

class Torpedo extends Power {
    int compteurMax = 3;
    int compteurActuel;
    public Torpedo() {
        this.type = Type.TORPEDO;
        this.compteurActuel = this.compteurActuel + 1;//erreur compilateur ?? this.compteurActuel++;
    }

    public void resetCompteur() {
        this.compteurActuel = 0;
    }
}

class Carte {
    int x;
    int y;
    int[][] carte;

    public Carte(int x, int y , int[][] globalReferenceMap) {
        this.x = x;
        this.y = y;

        this.carte = new int[15][15];// todo mettre 15 en taille du globalReferenceMap
        for (int ys = 0; ys < 15; ys++) {
            for(int xs = 0; xs < 15 ; xs++) {
                this.carte[xs] = globalReferenceMap[xs].clone();
            }
        }
    }

    public void setX(int x) {
        this.x = x;
    }

    public void  setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}