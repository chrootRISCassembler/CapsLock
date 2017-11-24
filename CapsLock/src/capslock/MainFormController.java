package capslock;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author RISCassembler
 */
public class MainFormController implements Initializable {

    /** Constants */
    private static final String DB_FILE_NAME = "GamesInfo.json";
    private static final double PANEL_RATIO = 0.25;
    private static final double PANEL_GAP_RATIO = 0.03;

    private static final Path DB_PATH = Paths.get("./GamesInfo.json");

    private enum State{
        None,
        ImageOnly,
        MediaOnly,
        Both_Image,
        Both_Media
    }

    private State DisplayState;

    private GameCertification game;

    private Timeline ImageTimeLine;
    private List<Image> ImageList = new ArrayList<>();
    private Iterator<Image> ImageIterator;
    private List<Media> MovieList = new ArrayList<>();
    private Iterator<Media> MovieIterator;
    private boolean IsGameMapped = false;
    private final List<GameCertification> GameList;

    private MediaPlayer playstop;

    private WarningTimer warning=new WarningTimer();
    private static Process GameProcess;

    /** FXML binding */
    @FXML private ScrollPane LeftScrollPane;
        @FXML private TilePane PanelTilePane;
    @FXML private VBox RightVBox;
        @FXML private StackPane ViewStackPane;
            @FXML private ImageView StackedImageView;
            @FXML private MediaView StackedMediaView;
    @FXML private Label NameLabel;
    @FXML private Label DescriptionLabel;
    @FXML private Button playButton;

    public MainFormController() {
        List<GameCertification> ListBuilder = new ArrayList<>();

        try{
            final String JSON_String = Files.newBufferedReader(DB_PATH).lines()
                    .collect(Collectors.joining());
            new JSONArray(JSON_String).forEach(record -> ListBuilder.add(new GameCertification((JSONObject) record)));

        } catch (SecurityException ex) {//セキュリティソフト等に読み込みを阻害されたとき
            LogHandler.inst.severe("File-loading is blocked by security manager");
            LogHandler.inst.severe(ex);
        } catch (IOException ex) {
            LogHandler.inst.severe("Failed to open " + DB_FILE_NAME);
            LogHandler.inst.severe(ex);
        } catch(JSONException ex){
            ex.printStackTrace();
            LogHandler.inst.warning("JSONException : " + DB_FILE_NAME + " must be wrong.");
            LogHandler.inst.severe(ex.toString());
        } catch(Exception ex){
            GameList = null;
            return;
        }

        for(int i=0;i<ListBuilder.size();i++) {
        	for(int j=0;j<ListBuilder.size()-1;j++) {
        		File hoo=ListBuilder.get(j).getPanelPath().toFile();
        		if(!hoo.exists()) {
        			int next=j++;
        			GameCertification x=ListBuilder.set(j,ListBuilder.get(next));
        			ListBuilder.set(next,x);
        		}
        	}
        }

        GameList = Collections.unmodifiableList(ListBuilder);
        LogHandler.inst.fine(GameList.size() + "件のゲームを検出");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        ImageTimeLine = new Timeline(new KeyFrame(
        Duration.millis(2500),
        ae -> UpdateImage(ae)));
        ImageTimeLine.setCycleCount(Animation.INDEFINITE);
    }

	@FXML
	protected void onButtonClick(ActionEvent evt) {
		if(GameIsAlive())return;

        final ProcessBuilder pb = new ProcessBuilder(game.getExecutablePath().toString());
        File gameDir = new File(System.getProperty("user.dir")+"\\"+game.getExecutablePath().toString());
        pb.directory(new File(gameDir.getParent	()));
        pb.redirectErrorStream(true);
        try {
            playstop.stop();
        	warning.Start();


            GameProcess = pb.start();
        } catch (IOException ex) {
            System.out.println(ex);
        }
	}

    public void onLoad(WindowEvent event){
        if(IsGameMapped)return;

        final double PanelImageSideLength;

        {
            final Rectangle2D ScreenRect = Screen.getPrimary().getVisualBounds();
            final double FullScreenWidth = ScreenRect.getWidth();
            final double FullScreenHeight = ScreenRect.getHeight();
            final double LeftSize = FullScreenWidth / 5 * 2;

            LeftScrollPane.setPrefViewportWidth(LeftSize);
            LeftScrollPane.setMinViewportWidth(LeftSize);

            PanelImageSideLength = LeftSize * PANEL_RATIO;

            final double Gap = LeftSize * PANEL_GAP_RATIO;
            PanelTilePane.setPadding(new Insets(LeftSize / 12));
            PanelTilePane.setVgap(Gap);
            PanelTilePane.setHgap(Gap);

            final double RightContentPadding = (FullScreenWidth - LeftSize) / 20;
            RightVBox.setPadding(new Insets(RightContentPadding));

            NameLabel.setFont(Font.font(FullScreenHeight / 20));
            DescriptionLabel.setFont(Font.font(FullScreenHeight/40));
        }

        for(GameCertification game : GameList){
            final Image PanelImage;

            if(Files.isRegularFile(game.getPanelPath())){
                PanelImage = new Image(game.getPanelPath().toUri().toString());
            }else{
                PanelImage = CharPanelGenerator.generate(game.getName().charAt(0));
                LogHandler.inst.warning("game's UUID : " + game.getUUID().toString() + " doesn't have panel image.");
            }

            final ImageView view = new ImageView(PanelImage);
            view.setPreserveRatio(false);
            view.setFitWidth(PanelImageSideLength);
            view.setFitHeight(PanelImageSideLength);
            view.setOnMouseEntered((eve) -> {
                final ImageView TriggerView = (ImageView)eve.getSource();
                onImageFocused(TriggerView);
            });

            view.setOnMouseClicked(eve -> onPanelDoubleClicked(eve));

            view.setUserData(game);
            PanelTilePane.getChildren().add(view);
        }
    }

    class onMovieEndClass implements Runnable{
        @Override
        public void run(){
            try{
                PlayMovie(MovieIterator.next());
            }catch(NoSuchElementException e){
                if(DisplayState == State.MediaOnly){
                    MovieIterator = MovieList.iterator();
                    PlayMovie(MovieIterator.next());
                }else{
                    DisplayState = State.Both_Image;

                    ImageIterator = ImageList.iterator();
                    StackedImageView.setImage(ImageIterator.next());
                    ImageTimeLine.play();
                    SwapDisplayContentType();
                }
            }
        }
    }

    Runnable onMovieEnd = new onMovieEndClass();

    public void onImageFocused(ImageView view){
        if(game != null)ReleasePreviousGameContents();

        game = (GameCertification)view.getUserData();
        String gameName = game.getName();

//        try {
//        	gameName = new String(g.getBytes("SJIS"), "UTF-8")
//			System.out.println();
//		} catch (UnsupportedEncodingException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
        NameLabel.setText("[P-"+String.valueOf(game.getGameID())+"]"+gameName);
        DescriptionLabel.setText(game.getDescription());

        byte Flags = 0;

        game.getImagesPathList().forEach(path -> ImageList.add(new Image(path.toUri().toString())));
        game.getMoviePathList().forEach(path -> MovieList.add(new Media(path.toUri().toString())));

        if(!ImageList.isEmpty())Flags = 0b1;
        if(!MovieList.isEmpty())Flags += 0b10;

        switch(Flags){
            case 0:
                DisplayState = State.None;
                break;
            case 0b1:
                DisplayState = State.ImageOnly;
                ImageSet();
                break;
            case 0b10:
                DisplayState = State.MediaOnly;
                MovieIterator = MovieList.iterator();
                PlayMovie(MovieIterator.next());
                StackedImageView.setVisible(false);
                break;
            case 0b11:
                DisplayState = State.Both_Image;
                ImageSet();
                break;
            default:
                LogHandler.inst.severe("Unexpected flag! Call the developer!");
        }

        DescriptionLabel.setPadding(Insets.EMPTY);
        DescriptionLabel.autosize();
        double textwidth = DescriptionLabel.getWidth();
    }

    private void ReleasePreviousGameContents(){
        ImageTimeLine.stop();
        ImageList.clear();
        try{
            StackedMediaView.getMediaPlayer().stop();
        }catch(NullPointerException e){
        }
        MovieList.clear();
        StackedImageView.setImage(null);
        StackedMediaView.setMediaPlayer(null);
        game = null;
    }

    void onPanelDoubleClicked(MouseEvent event){
        if(!event.getButton().equals(MouseButton.PRIMARY))return;
        if(event.getClickCount() != 2)return;
    	System.err.println("is clicked");
        if(GameIsAlive())return;

        final ProcessBuilder pb = new ProcessBuilder(game.getExecutablePath().toString());
        File gameDir = new File(System.getProperty("user.dir")+"\\"+game.getExecutablePath().toString());
        pb.directory(new File(gameDir.getParent()));
        pb.redirectErrorStream(true);
        try {

        	playstop.stop();
        	playstop.dispose();
            warning.Start();


            GameProcess = pb.start();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void UpdateImage(ActionEvent event){
        try{
            DisplayImage();

        }catch(NoSuchElementException ex){
            if(DisplayState == State.ImageOnly){
                ImageIterator = ImageList.iterator();
        DisplayImage();
            }else{
                ImageTimeLine.stop();
                DisplayState = State.Both_Media;
                MovieIterator = MovieList.iterator();
                PlayMovie(MovieIterator.next());
                SwapDisplayMovie();
            }
        }
        System.err.println("timer");
        System.err.println(DescriptionLabel.getLayoutX());
    }

    private void ImageSet(){
        ImageIterator = ImageList.iterator();
        DisplayImage();
        ImageTimeLine.play();
        StackedMediaView.setVisible(false);
    }

    private void PlayMovie(Media movie){
        MediaPlayer player = new MediaPlayer(movie);
        playstop = player;
        player.setOnEndOfMedia(onMovieEnd);
        player.setAutoPlay(true);
        player.setCycleCount(1);
        StackedMediaView.setMediaPlayer(player);
        StackedMediaView.setFitWidth(ViewStackPane.getWidth());

        SwapDisplayMovie();
    }

    private void DisplayImage(){
    	Image image = ImageIterator.next();
        StackedImageView.setImage(image);
        StackedImageView.setFitWidth(ViewStackPane.getWidth());

        SwapDisplayImage();
    }

    private void SwapDisplayContentType(){
        StackedImageView.setVisible(!StackedImageView.isVisible());
        StackedMediaView.setVisible(!StackedMediaView.isVisible());
    }

    private void SwapDisplayMovie() {
    	StackedMediaView.setVisible(true);
        StackedImageView.setVisible(false);
    }

    private void SwapDisplayImage() {
    	playstop.dispose();
        StackedImageView.setVisible(true);
        StackedMediaView.setVisible(false);
    }

    public static boolean GameIsAlive() {
    	boolean res=false;
    	if(GameProcess!=null) {
        	if(GameProcess.isAlive())res=true;
        }
    	return res;
    }
}
