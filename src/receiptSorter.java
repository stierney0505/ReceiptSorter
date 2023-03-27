import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class receiptSorter extends Application {
	ComboBox<String> names = new ComboBox<String>();
	Stage popUp = new Stage();
	BorderPane mainPane;
	Scene mainScene;
	HBox topBar;
	TextField dateField;
	TextField priceField;
	TextField typeField;
	TextField locField;
	Boolean emptyLocCheck = false;
	final String defaultPriceText = "Enter price like XX.xx";
	final String defaultDateText = "Enter date like MM/DD/YY";

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("receiptSorter");
		names = loadNames();

		Button addName = new Button();
		Image addButtonImg = new Image("dataFiles/addIcon.png");
		ImageView addImageView = new ImageView(addButtonImg);
		addImageView.setFitHeight(80);
		addImageView.setPreserveRatio(true);
		addName.setGraphic(addImageView);
		addName.setOnAction(event -> addPersonHandler());

		Button deleteName = new Button();
		Image deleteButtonImg = new Image("dataFiles/deleteIcon.png");
		ImageView deleteImageView = new ImageView(deleteButtonImg);
		deleteImageView.setFitHeight(80);
		deleteImageView.setPreserveRatio(true);
		deleteName.setGraphic(deleteImageView);
		deleteName.setOnAction(event -> produceWarningPopUp(
				"Are you sure you want to remove " + names.getSelectionModel().getSelectedItem() + " from the program?",
				true));

		topBar = new HBox(names, addName, deleteName);
		topBar.setSpacing(20);
		topBar.setAlignment(Pos.CENTER);
		
		dateField = new TextField();
		dateField.setPrefWidth(157.5);
		dateField.setText(defaultDateText);
		dateField.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if(dateField.getText().isBlank() && !arg2) 
					dateField.setText(defaultDateText);
				else if(dateField.getText().equals(defaultDateText))
					dateField.clear();
			}
		});
		Label dateLabel = new Label("Date:");
		HBox date = new HBox(dateLabel, dateField);
		date.setAlignment(Pos.CENTER_RIGHT);

		priceField = new TextField();
		priceField.setText(defaultPriceText);
		priceField.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if(priceField.getText().isBlank() && !arg2) 
					priceField.setText(defaultPriceText);
				else if(priceField.getText().equals(defaultPriceText))
					priceField.clear();
			}
		});
		Label priceLabel = new Label("Price:");
		HBox price = new HBox(priceLabel, priceField);

		locField = new TextField();
		Label locLabel = new Label("Location:");
		HBox location = new HBox(locLabel, locField);

		typeField = new TextField();
		Label typeLabel = new Label("Type:");
		HBox type = new HBox(typeLabel, typeField);

		GridPane middle = new GridPane();
		middle.add(date, 0, 0);
		middle.add(price, 1, 0);
		middle.add(location, 0, 1);
		middle.add(type, 1, 1);
		middle.setHgap(10);
		middle.setVgap(10);
		middle.setAlignment(Pos.CENTER);

		Button addReceipt = new Button("Add Receipt");
		addReceipt.setPrefSize(120, 40);
		addReceipt.setFont(new Font(16));
		addReceipt.setOnAction(event -> addReceipt());
		HBox buttonAlignment = new HBox(addReceipt);
		buttonAlignment.setAlignment(Pos.CENTER);
		buttonAlignment.setPadding(new Insets(0, 0, 40, 0));

		mainPane = new BorderPane();
		mainPane.setTop(topBar);
		mainPane.setCenter(middle);
		mainPane.setBottom(buttonAlignment);
		mainScene = new Scene(mainPane, 500, 200);
		primaryStage.setScene(mainScene);
		primaryStage.setWidth(500);
		primaryStage.setHeight(500);
		primaryStage.show();

	}

	public ComboBox<String> loadNames() {
		ComboBox<String> returnBox = new ComboBox<String>();

		try {
			File namesFile = new File("src/dataFiles/namesFile.txt");
			if (namesFile.createNewFile())// This returns true if it creates a new file, so if so returns the empty
											// comboBox
				return returnBox;
			BufferedReader br = new BufferedReader(new FileReader(namesFile));
			String line = br.readLine();
			while (line != null) {
				returnBox.getItems().add(line.trim());

				line = br.readLine();
			}
			br.close();
			if (!returnBox.getItems().isEmpty())
				returnBox.getSelectionModel().selectLast();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnBox;
	}

	public void addPersonHandler() {
		Label addPersonDescriptor = new Label("Enter New Person's Name");
		TextField addPersonField = new TextField();
		Button addPersonButton = new Button("AddPerson");
		Stage addPersonStage = new Stage();
		
		addPersonButton.setOnAction(event -> {
			try {
				File namesFile = new File("src/dataFiles/namesFile.txt");
				namesFile.createNewFile();
				FileWriter fw = new FileWriter(namesFile, true);
				BufferedReader br = new BufferedReader(new FileReader(namesFile));
				String line;
				boolean nameExists = false;
				while ((line = br.readLine()) != null) {
					if (addPersonField.getText().trim().equals(line)) {
						produceWarningPopUp("Name Already Exists", false);
						nameExists = true;
						break;
					}
				}
				if (!nameExists) {
					fw.write(addPersonField.getText().trim() + "\n");
					fw.close();
					names = loadNames();
					topBar.getChildren().remove(0);
					topBar.getChildren().add(0, names);
					addPersonStage.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		HBox topElements = new HBox(addPersonDescriptor, addPersonField);
		topElements.setSpacing(10);
		topElements.setAlignment(Pos.CENTER);
		VBox allElements = new VBox(topElements, addPersonButton);
		allElements.setSpacing(30);
		allElements.setAlignment(Pos.CENTER);

		Scene popUpScene = new Scene(allElements, 250, 125);
		addPersonStage.setScene(popUpScene);
		addPersonStage.show();
	}

	public void produceWarningPopUp(String message, boolean removePerson) {

		Label warning = new Label(message);
		warning.setWrapText(true);
		Button warningButton;
		HBox buttons;
		if (removePerson) {
			Button nevermind = new Button("Nevermind");
			warningButton = new Button("Remove");
			buttons = new HBox(warningButton, nevermind);
			nevermind.setOnAction(event->{ popUp.close();});
			warningButton.setOnAction(event -> {
				String removeName = names.getSelectionModel().getSelectedItem();

				try {
					File namesFile = new File("src/dataFiles/namesFile.txt");
					File tempFile = new File("src/dataFiles/tempFile.txt");
					BufferedReader br = new BufferedReader(new FileReader(namesFile));
					FileWriter fr = new FileWriter(tempFile);
					String line;
					while((line = br.readLine()) != null) {
						if(removeName.equals(line.trim())) 
							continue;
						fr.write(line + "\n");
					}
					br.close();
					fr.close();
					namesFile.delete();
					tempFile.renameTo(namesFile);
					popUp.close();
					names = loadNames();
					topBar.getChildren().remove(0);
					topBar.getChildren().add(0, names);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} else {
			warningButton = new Button("I understand");
			warningButton.setOnAction(event->{ popUp.close();});
			buttons = new HBox(warningButton);
		}
		VBox allElements = new VBox(warning, buttons);
		popUp.setScene(new Scene(allElements));
		popUp.setTitle("Warning");
		popUp.show();
	}
	
	public void addReceipt() {
	
		File receiptFile = new File("src/dataFiles/" + names.getValue() + "-Receipts.csv");
		try {
			FileWriter fr;
			if(receiptFile.createNewFile()) {
				fr = new FileWriter(receiptFile, true);
				fr.write("Date, Price, Location, Type, Sum Of Prices:, =SUM(B2:B10000),\n");//This sums up to a ten thousand receipts if you need more I guess change it
			}
			else
				fr = new FileWriter(receiptFile, true);
			Float price;
			String date;
			if(priceField.getText().isBlank() || dateField.getText().isBlank() ||
					priceField.getText().equals(defaultPriceText) || dateField.getText().equals(defaultDateText)) {
				produceWarningPopUp("Fill out date and price fields before adding a receipt", false);
				fr.close();
				return;
			}
			else if((price = checkPrice()) == null) {
				produceWarningPopUp("Insert Correct Price Format, like: 19.99", false);
				fr.close();
				return;
			}
			else if((date = checkDate()) == null) {
				produceWarningPopUp("Insert Correct Date Format, like: 06/26/22", false);
				fr.close();
				return;
			}
			else if(locField.getText().isBlank() || emptyLocCheck) {
				emptyLocCheck = true;
				produceWarningPopUp("It is strongly recommended to insert a location", false);
				fr.close();
				return;
			}

			fr.write(date + "," + price.toString() + "," 
					+ locField.getText() + "," + typeField.getText() + ",\n");
			dateField.clear();
			priceField.clear();
			typeField.clear();
			locField.clear();
			fr.close();
		} catch (IOException e) {
			produceWarningPopUp(e.getLocalizedMessage() + "\n Close the program that is accessing the file before attempting to "
					+ "add another receipt", false);
		}
	}
	
	public Float checkPrice() {
		String check = (priceField.getText());
		if(check.charAt(0) == '$')
			check = check.substring(1);
		try {
			Float value = Float.parseFloat(check);
			check = String.format("%.2f", value);
			return Float.parseFloat(check);
		} catch(Exception e) {
			return null;
		}
	}
	
	public String checkDate() {
		SimpleDateFormat dateFormator = new SimpleDateFormat("MM/dd/YY");
		Date date;
		try {
			date = dateFormator.parse(dateField.getText());
		} catch(Exception e) {
				return null;	
		}
		return dateFormator.format(date);
	}
	
}
