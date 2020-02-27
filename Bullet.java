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

//-------------------------------------------------------------------------------------------
/** Represents a JavaFX circle node that can bounce off walls using black pixel detection.
* Shot by tank
*
*/
public class Bullet {
	double xPos;
	double yPos;
	double xVel;
	double yVel;
	
	int collisionCount; 
	
	boolean enabled;
    
    Color playerColor;

	int radius; // TODO: make radius a class final constant

	Circle displayCircle;
    
    PixelReader reader;

	/** Constuctor for the bullet.
	 * Used only once in the <code>start</code> method.
	 *
	 * @param root THIS SHOULD ONLY EVER BE THE ROOT NODE
	 */
	public Bullet(Group root, PixelReader pixelR, Color playerColor) {
        this.playerColor = playerColor;
		radius = 4;
		displayCircle = new Circle(0, 0, radius);
		displayCircle.setFill(playerColor);
		root.getChildren().add(displayCircle);
		reader = pixelR;
	}
    


	public void doEvaporate() {
		FillTransition waitT = new	FillTransition(Duration.millis(300), displayCircle, playerColor, Color.TRANSPARENT);
		waitT.setOnFinished((new EventHandler<ActionEvent>() {
			public void	handle(ActionEvent e){
				collisionCount = 0;
				displayCircle.setFill(Color.TRANSPARENT);
				setStatus(false);
			}
		})); 
		waitT.play();
	}

	/** Sets the status of the bullet.
	 * @param state true if the bullet is relevant
	 *
	 */
	public void setStatus(boolean state) {
		enabled = state;
		collisionCount = 0;
		if (state) {
			displayCircle.setFill(playerColor);
		} else {
			displayCircle.setFill(Color.TRANSPARENT);
			setSpeed(0, 0);
			setX(0);
            setY(0);
            displayCircle.setTranslateX(xPos);
    		displayCircle.setTranslateY(yPos);
		}
	}
    
    public boolean getEnabled() {
        return enabled;
    }

	/** Moves the bullet by adding velocities to class position integers than
	 * moves the node itself.
	 *
	 */
	public void moveBullet() {
		if(enabled){
    		xPos += xVel;
    		yPos += yVel;
    		displayCircle.setTranslateX(xPos);
    		displayCircle.setTranslateY(yPos);
        }
		
	}
	
	
	/** Tests to see if the point is a black pixel on the image.
	 * Try to keep the parameter point object not on a decimal place
	 * @param point The point that you want to check
	 * @return true if the pixel is black
	 */
	public boolean inBlack(Point2D point) {
		if (point.getX() > 0 && point.getX() < 1280 &&
			point.getY() > 0 && point.getY() < 720) {
			if (reader.getColor((int)point.getX(),
			   (int)point.getY()).equals(Color.BLACK)) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
    
    public void setReader(PixelReader inputReader) {
        reader = inputReader;
    }
	

	/** Tests to see if there is a collision then reflects the bullet accordingly.
	 * Ran on enabled bullets in the game loop
	 */
	public void doReflect() {
		Point2D homePoint = new Point2D(xPos, yPos);
		if (inBlack(homePoint)) {
			Point2D refX = homePoint.add(-1 * xVel, yVel);
			Point2D refY = homePoint.add(xVel, yVel * -1);

			if (!inBlack(refX) && inBlack(refY)) {    //reflecting x works
				xVel *= -1;
			} else if (!inBlack(refY) && inBlack(refX)) {
				yVel *= -1;
			} else if (inBlack(refY) && inBlack(refX)) {
				yVel *= -1;
				xVel *= -1;
			}
			collisionCount++;
			if (collisionCount == 9) {
				doEvaporate();
			}
		}
	}

	/** @param x the x value to move the bullet to */
	public void setX(double x) {
		xPos = x;
	}

	/** @param y the y value to move the bullet to */
	public void setY(double y) {
		yPos = y;
	}

	/** Gets the current x value of the bullet's position.
	 *  @return double - the x value of the bullet
	 */
	public double getX() {
		return xPos;
	}

	/** Gets the current y value of the bullet's position.
	 *  @return double - the y value of the bullet
	 */
	public double getY() {
		return yPos;
	}

	/** Sets the speed of the bullet.
	 * Mostly likely only used in the tanks' {@code shoot} method
	 * @param x horizontal speed
	 * @param y vertical speed
	 */
	public void setSpeed(double x, double y) {
		xVel = x;
		yVel = y;
	}
    
    public Shape getBulletShape() {
        return displayCircle;
    }
}