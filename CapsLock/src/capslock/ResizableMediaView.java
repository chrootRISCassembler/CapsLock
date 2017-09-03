package capslock;

import javafx.geometry.Bounds;
import javafx.scene.media.MediaView;

/**
 *
 * @author RISCassembler
 */
public class ResizableMediaView extends MediaView{
    public ResizableMediaView(){
    }
    
    @Override
    public boolean isResizable(){
        return true;
    }
    
    @Override
    public void resize(double width, double height){
        System.err.println("resize called");
        final Bounds ParentsBounds = getParent().getBoundsInLocal();
        setFitWidth(ParentsBounds.getWidth());
        setFitHeight(ParentsBounds.getHeight());
    }
}
