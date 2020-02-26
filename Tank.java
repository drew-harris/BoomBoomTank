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

/** Player controlled image*/
public class Tank {

    public static final int BULLET_LIMIT = 20;
    public static final int BULLET_SPEED = 4;
    
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
	Rectangle dispTank;
	Rectangle testBounds;

	double xPos;
	double yPos;
	double xVel;
	double yVel;

	/** The speed of the tank going in any direction*/
	double rawSpeed = 4;
	double angle;
    
    Bullet[] bullets;
    
    PixelReader reader;
    
	boolean[] collisionList;  // direction for the tank's collision, uses class constants
	
	boolean canShoot;
    
    Color playerColor;
	
	public Tank(Group root, Bullet[] bullets, PixelReader reader) {
		dispTank = new Rectangle(0, 0, 30, 55);
		root.getChildren().add(dispTank);
		angle = 0;
		xVel = 0;
		yVel = 0;
		canShoot = true;
		dispTank.setRotate(angle * -1 - 90);    // initally sets tank to face RIGHT
		this.bullets = bullets;
        this.reader = reader;
	}
	
    /** Sets the color of the player.
    *
    */
    public void setPlayerColor(Color color) {
        playerColor = color;
        dispTank.setFill(playerColor);
    }
    
    /** Returns the first bullet in the array that is not active.
    * TODO: make the array a parameter
    * @return the first index of a bullet that is not being used.
    */
    public int getFirstBullet() {
        for (int i = 0; i < bullets.length; i++) {
            if (!bullets[i].getEnabled()) {
                return i;
            }
       }

       return -1;
    }
    
    public boolean testCollision(Bullet[] otherBullets) {
        Bounds rectBounds = dispTank.localToScene(dispTank.getBoundsInLocal());
        for (Bullet bullet : otherBullets) {
            if (bullet.getEnabled()) {
                Shape intersection = Shape.intersect(dispTank, bullet.getBulletShape());
                if (intersection.getBoundsInParent().getWidth() > 0) {
                    return true;
                }
            }
        }
        
        for(Bullet bullet : bullets) {
            Shape intersection = Shape.intersect(dispTank, bullet.getBulletShape());
            if (intersection.getBoundsInParent().getWidth() > 0) {
                return true;
            }
        }
        return false;
    }
	

	/** Called whenever user presses END key.*/
    public void shoot() {
		int index = getFirstBullet();
		if (index != -1 && canShoot) {
		
			bullets[index].setStatus(true);
			bullets[index].setX(getMiddlePoint().getX() + 27.5 * Math.cos( Math.toRadians( angle)));
			bullets[index].setY(getMiddlePoint().getY() + 27.5 * -1 *Math.sin( Math.toRadians( angle)));
			
			bullets[index].setSpeed( (Math.cos( Math.toRadians( angle)) * BULLET_SPEED),
									   (Math.sin( Math.toRadians( angle )) * - BULLET_SPEED) );
									   
			canShoot = false;
			
			PauseTransition waitT = new PauseTransition(Duration.millis(200));
			waitT.setOnFinished((new EventHandler<ActionEvent>() {
				public void	handle(ActionEvent e){
					canShoot = true;
				}
			})); 
			waitT.play();
			
		}
	}

	/** Gets the center point of the tank in terms of its bounds.
	* @return Point2D middlePoint - the center of the tank
	*/
	public Point2D getMiddlePoint() {
		Bounds bound = dispTank.getBoundsInParent();
		return new Point2D(bound.getMinX() + bound.getWidth()/2, bound.getMinY() + bound.getHeight()/2);
	}

	/** Returns the class constant for direction 
	* if the tank's bounds are touching a black pixel.
	* Will require access to Pixel reader in the future
	* @return The class constant for direction representing the direction of collision
	*/
	public boolean[] findCollision() {
		boolean[] collisionList = new boolean[6];   //up down left right front back
	
		Bounds bound = dispTank.getBoundsInParent();
		
		int cornerOffset = 30;
		int scannerSteps = 2;
		
		// up && down
		
		if (reader.getColor( (int)(bound.getMinX() + bound.getWidth()/2), (int)bound.getMinY() ).equals(Color.BLACK)) {
			 collisionList[UP] = true;
		} else if (reader.getColor( (int)(bound.getMinX() + bound.getWidth()/2), (int)bound.getMaxY() ).equals(Color.BLACK)) {
			collisionList[DOWN] = true;
		}
		
		
		// right && left
		if (reader.getColor( (int)bound.getMinX(), (int)(bound.getMinY() + bound.getHeight()/2) ).equals(Color.BLACK)) {
			collisionList[LEFT] = true;
		} else if (reader.getColor( (int)bound.getMaxX(), (int)(bound.getMinY() + bound.getHeight()/2) ).equals(Color.BLACK)) {
			collisionList[RIGHT] = true;
		}
		
		// front & back
		if (reader.getColor( (int)(getMiddlePoint().getX() + 27.5 *Math.cos( Math.toRadians( angle))) ,
		 (int) ((getMiddlePoint().getY() + 27.5 * -1 *Math.sin( Math.toRadians( angle))) ) ).equals(Color.BLACK)) {
			collisionList[4] = true;
		}else if (reader.getColor( (int)(getMiddlePoint().getX() - 27.5 * Math.cos( Math.toRadians( angle))) ,
		 (int) ((getMiddlePoint().getY() - 27.5 * -1 *Math.sin( Math.toRadians( angle))) ) ).equals(Color.BLACK)) {
			collisionList[5] = true;
		}

		
		return collisionList;
	}

	/** Updates the classes xPos and yPos but does not actually move image node.
	 * TODO: Change the way that the position and velocity interface
	 * @param fwd True if the user is moving foreward. (false for reverse)
	 */
	public void go(boolean fwd) {

		collisionList = findCollision();

		if (fwd) {
			xVel = Math.cos( Math.toRadians( angle ))* rawSpeed;
			yVel = Math.sin( Math.toRadians( angle ))* -rawSpeed;
		} else {
			xVel = Math.cos( Math.toRadians( angle ))* -rawSpeed;
			yVel = Math.sin( Math.toRadians( angle ))* rawSpeed;
		}
		
		if (collisionList[4] && fwd){
			
		} else if (collisionList[5] && !fwd){
			
		} else{
		
			if (!collisionList[UP] && yVel < 0){
				yPos += yVel;
			}else if (!collisionList[DOWN] && yVel > 0){
				yPos += yVel;
			}
			
			if (!collisionList[LEFT] && xVel < 0){
				xPos += xVel;
			}else if (!collisionList[RIGHT] && xVel > 0){
				xPos += xVel;
			}
			
		}
		

	}

	/** Called when user presses left or right arrow keys.
	 * Changes the angle of the tank.
	 * @param angleBy How many degrees to change the angle of the tank by.
	 */
	public void changeAngle(double angleBy) {
		collisionList = findCollision();
		double newAngle = angle + angleBy * 2;

		// TODO: make turning easier up against a wall
		if (collisionList[UP] || collisionList[DOWN]) {

			if (  (Math.sin( Math.toRadians(newAngle))) <= (Math.sin( Math.toRadians(angle))) ) {
					angle += angleBy;
			}

		} else if (collisionList[LEFT] || collisionList[RIGHT]) {

			if (  (Math.cos( Math.toRadians(newAngle))) <= (Math.cos( Math.toRadians(angle))) ) {
				angle += angleBy;
			}

		} else {  // if no collision found
			angle += angleBy;
		}

		// actually moves the image node
		dispTank.setRotate(angle * -1 - 90);
	}

	/** Actually moves the node to the classes x and y position doubles. */
	public void setPosition() {
		dispTank.setTranslateX(xPos);
		dispTank.setTranslateY(yPos);
	}

	/** Sets classes x position value.
	* @param x The x value you would like the tank moved to
	*/
	public void setX(double x) {
		xPos = x;
	}

	/** Sets classes y position value.
	* @param y The y value you would like the tank moved to
	*/
	public void setY(double y) {
		yPos = y;
	}

	public double getX() {
		return xPos;
	}

	public double getY() {
		 return yPos;
	}


}