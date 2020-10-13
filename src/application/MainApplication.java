package application;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;

public class MainApplication extends Application {
	
	@Override
	public void start(Stage primaryStage) {

		try {
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainUI.fxml"));
			// store the root element so that the controllers can use it
			BorderPane root = (BorderPane) loader.load();
			// create and style a scene
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("Phần mềm gửi xe");
			primaryStage.setScene(scene);

			primaryStage.setResizable(false);
			// show the GUI
			primaryStage.show();

			// set the proper behavior on closing the application
			MainController controller = loader.getController();
			controller.init(primaryStage);
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					controller.setClosed();
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// load the native OpenCV library
		 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
}
