import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.effect.*;
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

public class BoomBoomTank extends Application {

    Bullet[] playerOneBullets;
    Bullet[] playerTwoBullets;
    boolean[] keyStatusPlayerOne;
    boolean[] keyStatusPlayerTwo;

    PixelReader reader;
    Tank playerOne;
    Tank playerTwo;

    public static final int BULLET_LIMIT = 20;
    public static final int BULLET_SPEED = 4;
    
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    public class KeyBoard implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent e) {
            if (e.getEventType() == KeyEvent.KEY_PRESSED) {

                if (e.getCode() == KeyCode.RIGHT) {
                   keyStatusPlayerOne[3] = true;
                } else if (e.getCode() == KeyCode.LEFT) {
                   keyStatusPlayerOne[2] = true;
                } else if (e.getCode() == KeyCode.UP) {
                   keyStatusPlayerOne[0] = true;
                } else if (e.getCode() == KeyCode.DOWN) {
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

                if (e.getCode() == KeyCode.E) {
                    playerTwo.shoot();
                }
            }

            if (e.getEventType() == KeyEvent.KEY_RELEASED) {

                if (e.getCode() == KeyCode.RIGHT) {
                   keyStatusPlayerOne[3] = false;
                } else if (e.getCode() == KeyCode.LEFT) {
                   keyStatusPlayerOne[2] = false;
                } else if (e.getCode() == KeyCode.UP) {
                   keyStatusPlayerOne[0] = false;
                } else if (e.getCode() == KeyCode.DOWN) {
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
            } else if (keyStatusPlayerOne[3]) {
                playerOne.changeAngle(-3.5);
            }

            // forward and backward
            if (keyStatusPlayerOne[0]) {
                playerOne.go(true);
            } else if (keyStatusPlayerOne[1]) {
                playerOne.go(false);
            }

            playerOne.setPosition();

            for (Bullet bullet : playerOneBullets) {
                if (bullet.enabled) {
                    bullet.moveBullet();
                    bullet.doReflect();
                }
            }
            
            
            // PLAYER TWO
            
            
            // changing angles
            if (keyStatusPlayerTwo[2]) {
                playerTwo.changeAngle(3.5);
            } else if (keyStatusPlayerTwo[3]) {
                playerTwo.changeAngle(-3.5);
            }

            // forward and backward
            if (keyStatusPlayerTwo[0]) {
                playerTwo.go(true);
            } else if (keyStatusPlayerTwo[1]) {
                playerTwo.go(false);
            }

            playerTwo.setPosition();

            for (Bullet bullet : playerTwoBullets) {
                if (bullet.enabled) {
                    bullet.moveBullet();
                    bullet.doReflect();
                }
            }

        }
    }
    

    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage stage) {

        Group root = new Group();
        Scene scene = new Scene(root, 1280, 720, Color.WHITE);

        // TODO: new functions for different maps
        Image map = new Image("test.png");
        ImageView mapView = new ImageView();
        mapView.setImage(map);
        root.getChildren().add(mapView);
        reader = map.getPixelReader();

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

        playerOne = new Tank(root, playerOneBullets, reader);
        playerOne.setPlayerColor(Color.BLUE);
        playerOne.setX(640);
        playerOne.setY(300);
        
        playerTwo = new Tank(root, playerTwoBullets, reader);
        playerTwo.setPlayerColor(Color.RED);
        playerTwo.setX(800);
        playerTwo.setY(300);

        keyStatusPlayerOne = new boolean[4];
        keyStatusPlayerTwo = new boolean[4];
        stage.setTitle("Tanks Movement Test");
        stage.setScene(scene);
        stage.show();

        TanksAnimationTimer aniTimer = new TanksAnimationTimer();
        aniTimer.start();
    }
}