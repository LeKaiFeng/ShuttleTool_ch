package galaxis.lee;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import galaxis.lee.senceControl.FXSceneController;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LeeApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(LeeApplication.class, FXSceneController.class,args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("穿梭车单机测试工具");

        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        FXSceneController fc= new FXSceneController(stage);
        fc.setPrimaryStage(stage);
        super.start(stage);
    }
}
