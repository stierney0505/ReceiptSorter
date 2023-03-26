import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class receiptSorter extends Application {
	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("receiptSorter");
		ComboBox names = new ComboBox();
		Button addName = new Button();
		Button deleteName = new Button();
		HBox topBar = new HBox(names, addName, deleteName);
		BorderPane mainPane = new BorderPane();
		mainPane.setTop(topBar);
		primaryStage.setScene(new Scene(mainPane));
		primaryStage.show();
	}
}