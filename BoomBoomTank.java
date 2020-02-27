import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.scene.paint.Color;
import javafx.animation.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import javafx.scene.shape.*;
import javafx.geometry.Point2D;
import javafx.geometry.Bounds;
import javafx.geometry.BoundingBox;
import javafx.scene.image.*;
import java.util.*;
import java.io.*;


// TODO: MAKE IT SO YOU CAN TURN AWAY FROM WALLS

public class BoomBoomTank extends Application {

    Bullet[] playerOneBullets;
    Bullet[] playerTwoBullets;
    boolean[] keyStatusPlayerOne;
    boolean[] keyStatusPlayerTwo;

    Image map;
    ImageView mapView;
    PixelReader reader;
    
    Tank playerOne;
    Tank playerTwo;
    
    int playerOneWins;
    int playerTwoWins;
    Text p1WinDisplay;
    Text p2WinDisplay;
    
    public static final int NUM_OF_LEVELS = 10;

    public static final int BULLET_LIMIT = 80;
    public static final int BULLET_SPEED = 4;
    
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int BLUE = 996;
    public static final int RED = 997;

    public class KeyBoard implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent e) {
            if (e.getEventType() == KeyEvent.KEY_PRESSED) {
                if (e.getCode() == KeyCode.NUMPAD6) {
                   keyStatusPlayerOne[3] = true;
                } else if (e.getCode() == KeyCode.NUMPAD4) {
                   keyStatusPlayerOne[2] = true;
                } else if (e.getCode() == KeyCode.NUMPAD8) {
                   keyStatusPlayerOne[0] = true;
                } else if (e.getCode() == KeyCode.NUMPAD5) {
                    keyStatusPlayerOne[1] = true;
                }

                if (e.getCode() == KeyCode.END) {
                    playerOne.shoot();
                }
                
                if (e.getCode() == KeyCode.D) {
                   keyStatusPlayerTwo[3] = true;
                } else if (e.getCode() == KeyCode.A) {
                   keyStatusPlayerTwo[2] = true;
                } else if (e.getCode() == KeyCode.W) {
                   keyStatusPlayerTwo[0] = true;
                } else if (e.getCode() == KeyCode.S) {
                    keyStatusPlayerTwo[1] = true;
                }

                if (e.getCode() == KeyCode.TAB || e.getCode() == KeyCode.SPACE) {
                    playerTwo.shoot();
                }
            }else if (e.getEventType() == KeyEvent.KEY_RELEASED) {
                if (e.getCode() == KeyCode.NUMPAD6) {
                   keyStatusPlayerOne[3] = false;
                } else if (e.getCode() == KeyCode.NUMPAD4) {
                   keyStatusPlayerOne[2] = false;
                } else if (e.getCode() == KeyCode.NUMPAD8) {
                   keyStatusPlayerOne[0] = false;
                } else if (e.getCode() == KeyCode.NUMPAD5) {
                   keyStatusPlayerOne[1] = false;
                }
                
                if (e.getCode() == KeyCode.D) {
                   keyStatusPlayerTwo[3] = false;
                } else if (e.getCode() == KeyCode.A) {
                   keyStatusPlayerTwo[2] = false;
                } else if (e.getCode() == KeyCode.W) {
                   keyStatusPlayerTwo[0] = false;
                } else if (e.getCode() == KeyCode.S) {
                   keyStatusPlayerTwo[1] = false;
                }
            }
        }
    }

    /** Contains the main game loop.*/
    public class TanksAnimationTimer extends AnimationTimer {

        /** Runs 60 times a second.*/
        public void handle(long e) {
        
            // changing angles
            if (keyStatusPlayerOne[2]) {
                playerOne.changeAngle(3.5);
            } 
            if (keyStatusPlayerOne[3]) {
                playerOne.changeAngle(-3.5);
            }

            // forward and backward
            if (keyStatusPlayerOne[0]) {
                playerOne.go(true);
            } 
            if (keyStatusPlayerOne[1]) {
                playerOne.go(false);
            }

            
            
            
            
            for (Bullet bullet : playerOneBullets) {
                if (bullet.enabled) {
                    bullet.moveBullet();
                    bullet.doReflect();
                }
            }
            
            
            
            // changing angles
            if (keyStatusPlayerTwo[2]) {
                playerTwo.changeAngle(3.5);
            } 
            if (keyStatusPlayerTwo[3]) {
                playerTwo.changeAngle(-3.5);
            }

            // forward and backward
            if (keyStatusPlayerTwo[0]) {
                playerTwo.go(true);
            } 
            if (keyStatusPlayerTwo[1]) {
                playerTwo.go(false);
            }


            for (Bullet bullet : playerTwoBullets) {
                if (bullet.enabled) {
                    bullet.moveBullet();
                    bullet.doReflect();
                }
            }
            
            boolean playerTwoWin = playerOne.testCollision(playerTwoBullets);
            boolean playerOneWin = playerTwo.testCollision(playerOneBullets);
            
            playerTwo.setPosition();
            playerOne.setPosition();
            playerOne.updateTankImage();
            playerTwo.updateTankImage();
            
            if (playerOneWin) {
                playerOneWins++;
                setUpMap();
            } else if (playerTwoWin) {
                playerTwoWins++;
                setUpMap();
            }
        }
    }
    
    public String getRandomLevel() {
        int random = (int)(Math.random() * (NUM_OF_LEVELS) + 1);
        return "maps/map" + random + ".png";
    }
    
    
    
    public void disableAllBullets() {
        for (Bullet bullet : playerOneBullets) {
            bullet.setStatus(false);
        }
        
        for (Bullet bullet : playerTwoBullets) {
            bullet.setStatus(false);
        }
    }
    
    
    public void setUpMap() {
        String level = getRandomLevel();
        System.out.println(level);
        Image map = new Image(level);
        reader = map.getPixelReader();
        playerOne.setReader(reader);
        playerTwo.setReader(reader);
        
        for (Bullet bullet : playerOneBullets) {
            bullet.setReader(reader);
        }
        
        for (Bullet bullet : playerTwoBullets) {
            bullet.setReader(reader);
        }
        mapView.setImage(map);
        
        
        
        p1WinDisplay.setText("" + playerOneWins);
        p2WinDisplay.setText("" + playerTwoWins);
        
        Point2D[] spawns = SpawnFinder.getSpawns(map);
        
        playerOne.setX(spawns[0].getX());
        playerOne.setY(spawns[0].getY());
        playerTwo.setX(spawns[1].getX());
        playerTwo.setY(spawns[1].getY());
        
        
        disableAllBullets();
    }
    

    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage stage) {

        Group root = new Group();
        Scene scene = new Scene(root, 1280, 720, Color.WHITE);

        // TODO: new functions for different maps
        map = new Image("test.png");
        mapView = new ImageView();
        mapView.setImage(map);
        root.getChildren().add(mapView);


        //signature rectangle 
        Rectangle rKey = new Rectangle(0,0,5,5);
        rKey.setFill(Color.RED);
        KeyBoard kb = new KeyBoard();
        root.getChildren().add(rKey);
        rKey.addEventHandler(KeyEvent.ANY, kb);
        rKey.requestFocus();

        playerOneBullets = new Bullet[BULLET_LIMIT];
        for(int i = 0; i < playerOneBullets.length; i++) {
           playerOneBullets[i] = new Bullet(root, reader, Color.BLUE);
           playerOneBullets[i].setStatus(false);
        }
        
        playerTwoBullets = new Bullet[BULLET_LIMIT];
        for(int i = 0; i < playerTwoBullets.length; i++) {
           playerTwoBullets[i] = new Bullet(root, reader, Color.RED);
           playerTwoBullets[i].setStatus(false);
        }

        playerOne = new Tank(root, playerOneBullets, reader, BLUE);
        
        playerTwo = new Tank(root, playerTwoBullets, reader, RED);

        keyStatusPlayerOne = new boolean[4];
        keyStatusPlayerTwo = new boolean[4];
        
        playerOneWins = 0;
        playerTwoWins = 0;
        
        stage.setTitle("Tanks Movement Test");
        stage.setScene(scene);
        stage.show();
        
        
        
        p1WinDisplay = new Text(1140, 30, "");
        p2WinDisplay = new Text(55, 30, "");
        
        Font pixelFont = new Font("Arial", 35);// Font.font("Candara", 25);
		p1WinDisplay.setFont(pixelFont);
		p1WinDisplay.setFill(Color.BLUE);
		root.getChildren().add(p1WinDisplay);
        
        p2WinDisplay.setFont(pixelFont);
		p2WinDisplay.setFill(Color.RED);
		root.getChildren().add(p2WinDisplay);
        
        setUpMap();
        
        TanksAnimationTimer aniTimer = new TanksAnimationTimer();
        aniTimer.start();
        
        
    }
}