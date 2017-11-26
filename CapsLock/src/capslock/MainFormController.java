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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import trivial_common_logger.LogHandler;

/**
 * メインフォームのFXMLコントローラークラス.
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
            LogHandler.inst.DumpStackTrace(ex);
        } catch (IOException ex) {
            LogHandler.inst.severe("Failed to open " + DB_FILE_NAME);
            LogHandler.inst.DumpStackTrace(ex);
        } catch(JSONException ex){
            ex.printStackTrace();
            LogHandler.inst.severe("JSONException : " + DB_FILE_NAME + " must be wrong.");
            LogHandler.inst.DumpStackTrace(ex);
        } catch(Exception ex){
            GameList = null;
            return;
        }

        LogHandler.inst.fine(DB_FILE_NAME + "loading succeeded.");

        LogHandler.inst.finer("Panel sorting is started.");

        Collections.shuffle(ListBuilder);

        //ApologizeToYamara
        for(int i=0;i<ListBuilder.size();i++) {
        	String hoo=ListBuilder.get(i).getExecutablePath().toFile().toString();
        	if(hoo.indexOf("SkyClimb")!=-1) {
        		System.err.println("serched");
        		GameCertification temp=ListBuilder.get(i);
        		ListBuilder.remove(i);
        		ListBuilder.add(0, temp);
        		break;
        	}
        }

        LogHandler.inst.finer("Panel sorting is complete.");
        GameList = ListBuilder;
        LogHandler.inst.fine(GameList.size() + "件のゲームを検出");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        ImageTimeLine = new Timeline(new KeyFrame(
        Duration.millis(2500),
        ae -> UpdateImage(ae)));
        ImageTimeLine.setCycleCount(Animation.INDEFINITE);//タイマーを無限ループさせる.
    }

    @FXML
    protected void onButtonClick(ActionEvent evt) {
        if(GameIsAlive()){
            LogHandler.inst.finest("PlayButton is clicked, but another game is still alive.");
            return;
        }

        final String ExePathString = game.getExecutablePath().toString();
        final ProcessBuilder pb = new ProcessBuilder(ExePathString);
        File gameDir = new File(System.getProperty("user.dir")+"\\"+ExePathString);
        pb.directory(new File(gameDir.getParent	()));
        pb.redirectErrorStream(true);
        
        LogHandler.inst.fine("Try to launch " + ExePathString);
        
        try {
            GameProcess = null;
            GameProcess = pb.start();
            if(GameProcess != null){
                LogHandler.inst.finest("StopMovie");
                playstop.stop();
            }
        } catch (SecurityException ex){//セキュリティソフト等に読み込みを阻害されたとき
            LogHandler.inst.severe("File-loading is blocked by security manager");
            LogHandler.inst.DumpStackTrace(ex);
        } catch (IOException ex) {
            LogHandler.inst.severe("Can't open exe of the game.");
            LogHandler.inst.DumpStackTrace(ex);
        }
    }

    public void onLoad(WindowEvent event){
        if(IsGameMapped)return;

        final double PanelImageSideLength;

        LogHandler.inst.finest("Start calculation of dynamic UI.");
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
            RightVBox.setMaxWidth(FullScreenWidth - LeftSize);

            NameLabel.setFont(Font.font(FullScreenHeight / 20));
            DescriptionLabel.setFont(Font.font(FullScreenHeight/40));
        }
        LogHandler.inst.finest("Finished calculation of dynamic UI.");


        final ColorSequencer sequencer = new ColorSequencer();
        final Tooltip tooltip = new Tooltip("ダブルクリックでゲーム起動");
        for(final GameCertification game : GameList){
            final Image PanelImage;

            if(Files.isRegularFile(game.getPanelPath())){
                PanelImage = new Image(game.getPanelPath().toUri().toString());
            }else{//パネル画像が設定されていないとき
                PanelImage = CharPanelGenerator.generate(game.getName().charAt(0), sequencer.get());
                LogHandler.inst.warning("game's UUID : " + game.getUUID().toString() + " doesn't have panel image.");
            }

            final ImageView view = new ImageView(PanelImage);
            view.setPreserveRatio(false);
            view.setFitWidth(PanelImageSideLength);
            view.setFitHeight(PanelImageSideLength);
            view.setOnMouseClicked(eve -> onPanelClicked(eve));
            Tooltip.install(view, tooltip);
            view.setUserData(game);
            PanelTilePane.getChildren().add(view);
        }

        LogHandler.inst.finest("MainForm window is displayed.");
        System.gc();
    }

    class onMovieEndClass implements Runnable{
        @Override
        public void run(){
            try{
                PlayMovie(MovieIterator.next());
            }catch(NoSuchElementException e){//次の動画がリストにない
                if(DisplayState == State.MediaOnly){//
                    MovieIterator = MovieList.iterator();
                    PlayMovie(MovieIterator.next());
                }else{
                    DisplayState = State.Both_Image;
                    ImageIterator = ImageList.iterator();
                    StackedImageView.setImage(ImageIterator.next());
                    ImageTimeLine.play();
                    SwapDisplayImage();
                }
            }
        }
    }
    final Runnable onMovieEnd = new onMovieEndClass();

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

    void onPanelClicked(MouseEvent event){
        if(!event.getButton().equals(MouseButton.PRIMARY))return;//右クリックじゃない
    	System.err.println("is clicked");

        final ImageView view = (ImageView)event.getSource();//クリックされたパネルの取得
        final GameCertification NextGame = (GameCertification)view.getUserData();//パネルが示すゲーム
                
        if(game != NextGame){//前と別のゲームがクリックされた

            PanelTilePane.getChildren().stream()
                    .peek(panel -> panel.setScaleX(1))
                    .peek(panel -> panel.setScaleY(1))
                    .forEach(panel -> panel.setEffect(null));

            {//選択されたパネルにエフェクトを適応
                view.setScaleX(1.15);
                view.setScaleY(1.15);

                final DropShadow effect = new DropShadow(20, Color.BLUE);//影つけて
                effect.setInput(new Glow(0.5));//光らせる
                view.setEffect(effect);
            }

            if(game != null){
                ReleasePreviousGameContents();
            }

            game = NextGame;

            String gameName = NextGame.getName();
            NameLabel.setText("[P-"+String.valueOf(NextGame.getGameID())+"]"+gameName);
            DescriptionLabel.setText(NextGame.getDescription());

            NextGame.getImagesPathList().forEach(path -> ImageList.add(new Image(path.toUri().toString())));
            NextGame.getMoviePathList().forEach(path -> MovieList.add(new Media(path.toUri().toString())));

            DisplayState = getFirstState();
            
            System.err.println(DisplayState);
            
            switch(DisplayState){
                case ImageOnly:
                    ImageSet();
                case Both_Media:
                    ImageIterator = ImageList.iterator();
                case MediaOnly:
                    MovieIterator = MovieList.iterator();
                    PlayMovie(MovieIterator.next());
                    StackedImageView.setVisible(false);
                    break;
            }
            
            DescriptionLabel.setPadding(Insets.EMPTY);
            DescriptionLabel.autosize();
        }
        
        if(event.getClickCount() != 2)return;//ダブルクリックじゃない

        if(GameIsAlive()){
            LogHandler.inst.finest("Panel is double clicked, but another game is still alive.");
            return;
        }

        final String ExePathString = game.getExecutablePath().toString();
        final ProcessBuilder pb = new ProcessBuilder(ExePathString);
        File gameDir = new File(System.getProperty("user.dir")+"\\"+ExePathString);
        pb.directory(new File(gameDir.getParent()));
        pb.redirectErrorStream(true);
                   
        LogHandler.inst.fine("Try to launch " + ExePathString);
        
        try {
            GameProcess = null;
            GameProcess = pb.start();
            if(GameProcess != null){
                LogHandler.inst.finest("StopMovie");
                playstop.stop();
            }
        } catch (SecurityException ex){//セキュリティソフト等に読み込みを阻害されたとき
            LogHandler.inst.severe("File-loading is blocked by security manager");
            LogHandler.inst.DumpStackTrace(ex);
        } catch (IOException ex) {
            LogHandler.inst.severe("Can't open exe of the game.");
            LogHandler.inst.DumpStackTrace(ex);
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
    }

    private void ImageSet(){
        ImageIterator = ImageList.iterator();
        DisplayImage();
        ImageTimeLine.play();
        SwapDisplayImage();
    }

    private void PlayMovie(Media movie){
        MediaPlayer player = new MediaPlayer(movie);
        playstop = player;
        player.setOnEndOfMedia(onMovieEnd);
        player.setAutoPlay(true);
        player.setCycleCount(1);
        player.setMute(true);
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
    
    private final State getFirstState(){
        if(MovieList.isEmpty() && ImageList.isEmpty())return State.None;
        if(MovieList.isEmpty())return State.ImageOnly;
        if(ImageList.isEmpty())return State.MediaOnly;
        return State.Both_Media;
    }
    
    final void ShufflePanels(){
        System.out.println("Shuffle called");
        
        final int last = PanelTilePane.getChildren().size();   
        final Node FirstView = PanelTilePane.getChildren().get(0);
        final List<Node> views = PanelTilePane.getChildren().subList(1, last).stream()
                .map(node -> (ImageView)node)
                .collect(Collectors.toList());
        
        PanelTilePane.getChildren().clear();
        Collections.shuffle(views);
        PanelTilePane.getChildren().add(FirstView);   
        PanelTilePane.getChildren().addAll(views);
        
        System.out.println("shuffle end");
    }
}
