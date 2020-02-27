import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import javafx.scene.image.*;

public class SpawnFinder {
    public static Point2D[] getSpawns(Image map) {
        PixelReader spawnReader = map.getPixelReader();
        Point2D[] spawns = new Point2D[2];
        for(int x = 0; x < 1280; x+=1) {
            for(int y = 0; y < 720; y+=1) {
                if (spawnReader.getColor(x, y).equals(Color.BLUE)) {
                    spawns[0] = new Point2D(x, y);
                } else if (spawnReader.getColor(x, y).equals(Color.RED)) {
                    spawns[1] = new Point2D(x, y);
                }
            }
        }
        
        if(Math.random() > .50) {
            Point2D tempPoint = new Point2D(spawns[0].getX(), spawns[0].getY());
            spawns[0] = new Point2D(spawns[1].getX(), spawns[1].getY());
            spawns[1] = new Point2D(tempPoint.getX(), tempPoint.getY());
        }
        return spawns;
    }
}