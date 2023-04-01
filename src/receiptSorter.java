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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.regex.*;

public class receiptSorter extends Application {
	private final double TEXTFIELD_MIN_WIDTH = 157.5, TEXTFIELD_MIN_HEIGHT = 25, 
			TEXTFIELD_MAX_WIDTH = 400, TEXTFIELD_MAX_HEIGHT = 100, 
			MIN_FONT_SIZE = 12.0, MAX_FONT_SIZE = 60.0, MAX_FIELD_FONT_SIZE = 24.0,
			MIN_MAINELEMENTS_SPACING = 30, MAX_MAINELEMENTS_SPACING = 120, 
			MIN_TOPBAR_SPACING = 20, MAX_TOPBAR_SPACING = 80, 
			MIN_GRID_VGAP = 10, MAX_GRID_VGAP = 40, MIN_GRID_HGAP = 20, MAX_GRID_HGAP = 80,
			MIN_LABEL_SPACING = 5, MAX_LABEL_SPACING = 20, 
			MIN_IMAGEVIEW_SIZE = 50, MAX_IMAGEVIEW_SIZE = 200;
	private final String defaultPriceText = "Enter price like XX.xx"; // These strings are the default values for price and date fields, to show the user the
	private final String defaultDateText = "Enter date like MM/DD/YY"; // format that is expected to input a price and date
	private final String namesFileName = "src/dataFiles/namesFile.txt"; // Final string for the names in the comboBox
	
	private ComboBox<String> names = new ComboBox<String>(); // Combo Box that will be interacted will to choose who/what's receipt are being kept track off, names are kept in a separate file 
	private Stage popUp = new Stage(); // The popUp stage for warning
	private HBox topBar; // The top bar of the main stage, holds the names combobox, add and deleter  person buttons, is a instance variable to allow it to be refreshed when updates are made to the combobox
	private TextField dateField; // These four are the textfields which are used to add data into the csv files.
	private TextField priceField; // The price and date fields are required and need to follow a numeric and date pattern respectively, checked through checkPrice and checkDate methods
	private TextField typeField;
	private TextField locField;
	private Boolean emptyLocCheck = false; // This boolean is used to check if the location field is empty and presents a warning once per receipt that the user should fill out location, however it is not required
	private int resizeCount = 0;
	
	public static void main(String[] args) { // Launches GUI
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("receiptSorter");
		names = loadNames(); // TODO create css file for combobox styles
		names.setMinWidth(100);
		names.setMinHeight(35);
		names.setMaxWidth(names.getMinWidth() * 4);
		names.setMaxHeight(names.getMinHeight() * 4);
		

		// This creates the add person button, and assigns its color, size and graphics.
		Button addName = new Button();
		Image addButtonImg = new Image("dataFiles/addIcon.png");
		ImageView addImageView = new ImageView(addButtonImg);
		addImageView.setFitWidth(MIN_IMAGEVIEW_SIZE);
		addImageView.setFitHeight(MIN_IMAGEVIEW_SIZE);
		addName.setGraphic(addImageView);
		addName.setOnAction(event -> addPersonHandler()); // Sets button action to add a person to the comboBox
		addName.setStyle("-fx-background-color: #50C878;");
		// Creation of the delete person button, as well as assigning its size, color,
		// and graphics
		Button deleteName = new Button();
		Image deleteButtonImg = new Image("dataFiles/deleteIcon.png");
		ImageView deleteImageView = new ImageView(deleteButtonImg);
		deleteName.setGraphic(deleteImageView);
		deleteName.setOnAction(event -> {
			if (!names.getItems().isEmpty()) { // If there is a name selected to be removed
				produceWarningPopUp("Are you sure you want to remove " + // Then is call produceWarningPopUp with
																			// removePerson bool as true
						names.getSelectionModel().getSelectedItem() + " from the program?", true);
			} else if (names.getItems().isEmpty() || names.getValue() == null) // Otherwise it call produceWarningPopUp
																				// for a warning message
				produceWarningPopUp("No names to remove", false);
		});
		deleteName.setStyle("-fx-background-color: #C70039;");

		topBar = new HBox(names, addName, deleteName);
		topBar.setSpacing(MIN_TOPBAR_SPACING);
		topBar.setAlignment(Pos.CENTER);

		// Assigns datefield, and adds a listener to replace the text with default if it
		// is blank and the selection leaves the field
		dateField = new TextField();
		dateField.setMinSize(TEXTFIELD_MIN_WIDTH, TEXTFIELD_MIN_HEIGHT);
		dateField.setText(defaultDateText);
		dateField.focusedProperty().addListener(new ChangeListener<Boolean>() { // This listener is assigned to the
																				// focusedProperty, so every time the
																				// field is selected or deselected it
																				// triggers
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (dateField.getText().isBlank() && !newValue) // This checks if the field is both blank and just
																// unselected
					dateField.setText(defaultDateText); // If so, resets the field to default text
				else if (dateField.getText().equals(defaultDateText) && newValue) // This checks if the field is the
																					// default and selected
					dateField.clear(); // If so it removes the default text
			}
		});
		Label dateLabel = new Label("Date:");
		dateLabel.setMinWidth(Region.USE_PREF_SIZE);
		HBox date = new HBox(dateLabel, dateField);
		date.setAlignment(Pos.CENTER_RIGHT);
		date.setSpacing(MIN_LABEL_SPACING);

		// Assigns values to price field
		priceField = new TextField();
		priceField.setMinSize(TEXTFIELD_MIN_WIDTH, TEXTFIELD_MIN_HEIGHT);
		priceField.setText(defaultPriceText);
		priceField.focusedProperty().addListener(new ChangeListener<Boolean>() { // This listener is the same as the
																					// previous, but for the price field
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (priceField.getText().isBlank() && !newValue) // This checks if the field is both blank and just
																	// unselected
					priceField.setText(defaultPriceText); // If so, resets the field to default text
				else if (priceField.getText().equals(defaultPriceText) && newValue) // This checks if the field is the
																					// default and selected
					priceField.clear(); // If so it removes the default text
			}
		});
		Label priceLabel = new Label("Price:");
		priceLabel.setMinWidth(Region.USE_PREF_SIZE);
		HBox price = new HBox(priceLabel, priceField);
		price.setAlignment(Pos.CENTER);
		price.setSpacing(MIN_LABEL_SPACING);

		// Assigns values to location field
		locField = new TextField();
		locField.setMinSize(TEXTFIELD_MIN_WIDTH, TEXTFIELD_MIN_HEIGHT);
		Label locLabel = new Label("Location:");
		locLabel.setMinWidth(Region.USE_PREF_SIZE);
		HBox location = new HBox(locLabel, locField);
		location.setAlignment(Pos.CENTER_RIGHT);
		location.setSpacing(MIN_LABEL_SPACING);

		// Assign values to type field
		typeField = new TextField();
		typeField.setMinSize(TEXTFIELD_MIN_WIDTH, TEXTFIELD_MIN_HEIGHT);
		Label typeLabel = new Label("Type:");
		typeLabel.setMinWidth(Region.USE_PREF_SIZE);
		HBox type = new HBox(typeLabel, typeField);
		type.setAlignment(Pos.CENTER_RIGHT);
		type.setSpacing(MIN_LABEL_SPACING);

		// This gridpane holds the previous four fields in it, it will be the middle of
		// the primary stage
		GridPane middle = new GridPane();
		middle.add(date, 0, 0);
		middle.add(price, 1, 0);
		middle.add(location, 0, 1);
		middle.add(type, 1, 1);
		middle.setHgap(20);
		middle.setVgap(10);
		middle.setAlignment(Pos.CENTER);

		// Button to add receipt
		Button addReceipt = new Button("Add Receipt");
		addReceipt.setMinHeight(50);
		addReceipt.setMinWidth(120);
		addReceipt.setMaxHeight(300);
		addReceipt.setMaxWidth(480);
		addReceipt.setFont(new Font(16));
		addReceipt.setOnAction(event -> addReceipt()); // Sets the action of the button to add a receipt
		addReceipt.setStyle("-fx-background-color: #C576F6;");

		// HBox to allow for the alignment of buttons
		HBox buttonAlignment = new HBox(addReceipt);
		buttonAlignment.setAlignment(Pos.CENTER);

		VBox mainElements = new VBox(topBar, middle, buttonAlignment);
		mainElements.setSpacing(30);
		mainElements.setAlignment(Pos.CENTER);
		mainElements.setPadding(new Insets(17.5, 20, 17.5, 20));
		mainElements.setFillWidth(true);

		Scene mainScene = new Scene(mainElements);
		primaryStage.setScene(mainScene);
		primaryStage.setMinHeight(330);
		primaryStage.setMinWidth(480);

		mainScene.heightProperty().addListener((obs, oldVal, newVal) -> {
			if(resizeCount < 5) {
				resizeCount++;
				return;
			}
			double hRatio = newVal.doubleValue() / primaryStage.getMinHeight();

			double newElementHeight = TEXTFIELD_MAX_HEIGHT * hRatio;
			newElementHeight = (newElementHeight > TEXTFIELD_MAX_HEIGHT) ? TEXTFIELD_MAX_HEIGHT : newElementHeight;
			newElementHeight = (newElementHeight < TEXTFIELD_MIN_HEIGHT) ? TEXTFIELD_MIN_HEIGHT : newElementHeight;
			priceField.setPrefHeight(newElementHeight);
			dateField.setPrefHeight(newElementHeight);
			locField.setPrefHeight(newElementHeight);
			typeField.setPrefHeight(newElementHeight);

			newElementHeight = names.getMinHeight() * hRatio;
			newElementHeight = (newElementHeight > names.getMaxHeight()) ? names.getMaxHeight() : newElementHeight;
			newElementHeight = (newElementHeight < names.getMinHeight()) ? names.getMinHeight() : newElementHeight;
			names.setPrefHeight(newElementHeight);

			double newButtonHeight = addReceipt.getMinHeight() * hRatio;
			newButtonHeight = (newButtonHeight > 160) ? 160 : newButtonHeight;
			newButtonHeight = (newButtonHeight < 50) ? 50 : newButtonHeight;
			addReceipt.setPrefHeight(newButtonHeight);

			newButtonHeight = MIN_IMAGEVIEW_SIZE * hRatio;
			newButtonHeight = (newButtonHeight > MAX_IMAGEVIEW_SIZE) ? MAX_IMAGEVIEW_SIZE : newButtonHeight;
			newButtonHeight = ((newButtonHeight) < MIN_IMAGEVIEW_SIZE) ? MIN_IMAGEVIEW_SIZE : newButtonHeight;
			addImageView.setFitHeight(newButtonHeight);
			deleteImageView.setFitHeight(newButtonHeight);

			double newSpacing = MIN_MAINELEMENTS_SPACING * hRatio;
			newSpacing = (newSpacing > MAX_MAINELEMENTS_SPACING) ? MAX_MAINELEMENTS_SPACING : newSpacing;
			newSpacing = (newSpacing < MIN_MAINELEMENTS_SPACING) ? MIN_MAINELEMENTS_SPACING : newSpacing;
			mainElements.setSpacing(newSpacing);

			newSpacing = MIN_GRID_VGAP * hRatio;
			newSpacing = (newSpacing > MAX_GRID_VGAP) ? MAX_GRID_VGAP : newSpacing;
			newSpacing = (newSpacing < MIN_GRID_VGAP) ? MIN_GRID_VGAP : newSpacing;
			middle.setVgap(newSpacing);
			resizeCount++;
		});

		primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
			if(resizeCount < 5) {
				resizeCount++;
				return;
			}
			double wRatio = newVal.doubleValue() / primaryStage.getMinWidth();

			double newElementWidth = TEXTFIELD_MIN_WIDTH * wRatio;
			newElementWidth = (newElementWidth > TEXTFIELD_MAX_WIDTH) ? TEXTFIELD_MAX_WIDTH : newElementWidth;
			newElementWidth = (newElementWidth < TEXTFIELD_MIN_WIDTH) ? TEXTFIELD_MIN_WIDTH : newElementWidth;
			priceField.setPrefWidth(newElementWidth);
			dateField.setPrefWidth(newElementWidth);
			locField.setPrefWidth(newElementWidth);
			typeField.setPrefWidth(newElementWidth);

			newElementWidth = names.getMinWidth() * wRatio;
			newElementWidth = (newElementWidth > names.getMaxWidth()) ? names.getMaxWidth() : newElementWidth;
			newElementWidth = (newElementWidth < names.getMinWidth()) ? names.getMinWidth() : newElementWidth;
			names.setPrefWidth(newElementWidth);

			double newButtonWidth = addReceipt.getMinWidth() * wRatio;
			newButtonWidth = (newButtonWidth > 480) ? 480 : newButtonWidth;
			newButtonWidth = ((newButtonWidth) < 120) ? 126 : newButtonWidth;
			addReceipt.setPrefWidth(newButtonWidth);

			newButtonWidth = MIN_IMAGEVIEW_SIZE * wRatio;
			newButtonWidth = (newButtonWidth > MAX_IMAGEVIEW_SIZE) ? MAX_IMAGEVIEW_SIZE : newButtonWidth;
			newButtonWidth = (newButtonWidth < MIN_IMAGEVIEW_SIZE) ? MIN_IMAGEVIEW_SIZE : newButtonWidth;
			addImageView.setFitWidth(newButtonWidth);
			deleteImageView.setFitWidth(newButtonWidth);

			String fontName = priceField.getFont().getName();
			double fontSize = MIN_FONT_SIZE * wRatio;
			fontSize = (fontSize > MAX_FONT_SIZE) ? MAX_FONT_SIZE : fontSize;
			fontSize = (fontSize < MIN_FONT_SIZE) ? MIN_FONT_SIZE : fontSize;
			priceLabel.setFont(new Font(fontName, fontSize));
			dateLabel.setFont(new Font(fontName, fontSize));
			locLabel.setFont(new Font(fontName, fontSize));
			typeLabel.setFont(new Font(fontName, fontSize));
			addReceipt.setStyle("-fx-font-size : " + fontSize + "pt; -fx-background-color: #C576F6;");
			names.setStyle("-fx-font-size : " + fontSize + " pts;");
			priceField.setStyle("-fx-font-size : " + fontSize + " pts;");
			dateField.setStyle("-fx-font-size : " + fontSize + " pts;");
			
			double newSpacing = MIN_TOPBAR_SPACING * wRatio;
			newSpacing = (newSpacing > MAX_TOPBAR_SPACING) ? MAX_TOPBAR_SPACING : newSpacing;
			newSpacing = (newSpacing < MIN_TOPBAR_SPACING) ? MIN_TOPBAR_SPACING : newSpacing;
			topBar.setSpacing(newSpacing);

			newSpacing = MIN_LABEL_SPACING * wRatio;
			newSpacing = (newSpacing > MAX_LABEL_SPACING) ? MAX_LABEL_SPACING : newSpacing;
			newSpacing = (newSpacing < MIN_LABEL_SPACING) ? MIN_LABEL_SPACING : newSpacing;
			price.setSpacing(newSpacing);
			date.setSpacing(newSpacing);
			location.setSpacing(newSpacing);
			type.setSpacing(newSpacing);

			newSpacing = MIN_GRID_HGAP * wRatio;
			newSpacing = (newSpacing > MAX_GRID_HGAP) ? MAX_GRID_HGAP : newSpacing;
			newSpacing = (newSpacing < MIN_GRID_HGAP) ? MIN_GRID_HGAP : newSpacing;
			middle.setHgap(newSpacing);
		});

		primaryStage.show(); // TODO add a listener to the primary stage to resize everything with the stage.
	}

	public ComboBox<String> loadNames() { // This method loads the names from the namesFile and puts them into the
											// combobox
		ComboBox<String> returnBox = new ComboBox<String>();
		try {
			File namesFile = new File(namesFileName);
			if (namesFile.createNewFile())// This returns true if it creates a new file, so if so returns the empty
											// combobox
				return returnBox; // This is because if there is no file to load names from, then the user needs
									// to inputs names through the add Person button
			BufferedReader br = new BufferedReader(new FileReader(namesFile)); // Reader for the file
			String line = br.readLine();
			while (line != null) { // This goes through each line and adds the item to the combobox
				returnBox.getItems().add(line.trim());
				line = br.readLine();
			}
			br.close();
			if (!returnBox.getItems().isEmpty()) // If there is more than one item in the return box, it sets the value
													// of the return box to its last(most recent) element
				returnBox.getSelectionModel().selectLast();
		} catch (IOException e) {
			produceWarningPopUp(
					e.getLocalizedMessage() + "\nIf you know the developer contact me, otherwise enjoy debugging :(",
					false);
		} // Prints warning message, This program was also made specifically for someone I
			// know, so the message is mostly for them.
		return returnBox;
	}

	public void addPersonHandler() { // This handles the adding of people to the combobox, such as checking that the
										// name is not duplicated and that it has valid characters
		Label addPersonDescriptor = new Label("Enter New Person's Name"); // These lines set up the stage and elements
																			// in the stage
		addPersonDescriptor.setWrapText(true);
		addPersonDescriptor.setTextAlignment(TextAlignment.CENTER);
		TextField addPersonField = new TextField(); // Textfield for the name of the person/organization that the receipts are being tracked for
		Button addPersonButton = new Button("AddPerson"); // Button to add the name
		Stage addPersonStage = new Stage(); // This stage is a small popUp where the user is allowed to enter the
											// person/organization that they are adding to this receipt tracker

		addPersonButton.setOnAction(event -> { // Creates an event for the addPersonButton in this addPerson Stage
			try {
				File namesFile = new File(namesFileName); // Gets the file with the names
				FileWriter fw = new FileWriter(namesFile, true); // Creates a filewriter (filewriter creates a new file
																	// if one doesn't already exist)
				BufferedReader br = new BufferedReader(new FileReader(namesFile)); // A reader used to read from the
																					// existing file, to check for
																					// duplicate names
				String line; // Line for the reader
				String regex = "^[a-zA-Z0-9_-]{255}"; // Regex string to check for invalid filename characters
				boolean nameExists = false; // Boolean that checks if a name already exists

				while ((line = br.readLine()) != null) { // This loop first grabs a line from the file and if not null
															// it procedes
					if (addPersonField.getText().trim().equals(line)) { // Checks if the line is equal to whats in the
																		// add person field
						produceWarningPopUp("Name Already Exists", false); // If so produces warning PopUp
						nameExists = true; // assigns namesExists to true and breaks;
						break;
					}
				}
				if (!Pattern.matches(regex, addPersonField.getText().trim())) { // This checks the field for invalid
																				// characters and if it has some,
																				// produces a warning popup
					produceWarningPopUp("Name has invalid characters, remove special characters and spaces", false);
					addPersonStage.close();
				} else if (!nameExists) { // If the name doesn't exist, and no invalid characters (because this is if
											// else) then adds the name to the file and combobox
					fw.write(addPersonField.getText().trim() + "\n"); // writes name
					fw.close(); // closes because its unneeded now
					names = loadNames(); // reloads the names in combo box
					topBar.getChildren().remove(0); // removes the first element from the topBar(the names combobox)
					topBar.getChildren().add(0, names); // re inserts the names into the first element in the combobox
														// (basically this refreshes the GUI to display the new value)
					addPersonStage.close();
				}
				br.close(); // Still reaches and closes because only the stage is closed, not this method
			} catch (IOException e) {
				produceWarningPopUp(e.getLocalizedMessage()
						+ "\nIf you know the developer contact me, otherwise enjoy debugging :(", false);
			}
		});
		// Creation of HBoxes to manages the layout of the stage better
		HBox topElements = new HBox(addPersonDescriptor, addPersonField);
		topElements.setSpacing(10);
		topElements.setAlignment(Pos.CENTER);
		VBox allElements = new VBox(topElements, addPersonButton);
		allElements.setSpacing(30);
		allElements.setAlignment(Pos.CENTER);
		// Creation of the popUp scene and setting of the stage
		Scene popUpScene = new Scene(allElements, 250, 125);
		addPersonStage.setScene(popUpScene);
		addPersonStage.show();
	}

	public void produceWarningPopUp(String message, boolean removePerson) {// This method produces a warning popUp,
		// Either a generic warning message with provides no further action beyond a
		// warning, or if the removePerson boolean is true then it provides two buttons
		// and allows the removal of a person in a combobox

		// Creates the elements that will be used in the warning popUp
		Label warning = new Label(message); // Label for the warning message
		warning.setTextAlignment(TextAlignment.CENTER); // Sets allignmnet, Font, and wrapping
		warning.setFont(new Font(18));
		warning.setWrapText(true);
		Button warningButton = new Button(); // Button and size
		warningButton.setMinSize(120, 40);
		warningButton.setFont(Font.font(warningButton.getFont().getName(), FontWeight.BOLD, 16));
		HBox buttons; // Hbox to hold the button(s) and align them

		if (removePerson) { // If removePerson is true, then creates a two-buttoned popup for removing a
							// person from the combobox
			warning.setPrefWidth(400);
			warning.setTextFill(Color.RED);
			warning.setFont(Font.font(warning.getFont().getName(), FontWeight.BOLD, 22));
			Button nevermind = new Button("Nevermind"); // Creates the second button for "nevermind don't remove name"
			nevermind.setStyle("-fx-background-color: #DAA520;"); // Sets styles, font, and size
			nevermind.setFont(Font.font(nevermind.getFont().getName(), FontWeight.BOLD, 16));
			nevermind.setPrefSize(120, 40);
			warningButton.setText("Remove"); // Sets text, style, and font for warningButton
			warningButton.setStyle("-fx-background-color: #B33A3A;");
			warningButton.setFont(Font.font(warningButton.getFont().getName(), FontWeight.BOLD, 16));
			buttons = new HBox(warningButton, nevermind); // Creates the HBox
			buttons.setSpacing(30);
			nevermind.setOnAction(event -> {
				popUp.close();
			}); // Sets action event to remove the popUp
			warningButton.setOnAction(event -> { // This warning button
				String removeName = names.getSelectionModel().getSelectedItem();
				// In order to remove names from the file, another file needs to be created
				// where it will add the names form the original except the removed name, then
				// the original file gets deleted
				try {
					File namesFile = new File(namesFileName); // Creates these two files
					File tempFile = new File("src/dataFiles/tempFile.txt");
					BufferedReader br = new BufferedReader(new FileReader(namesFile)); // A reader for the original
																						// files
					FileWriter fr = new FileWriter(tempFile); // Writer for the temporary file
					String line; // Line for the reader
					while ((line = br.readLine()) != null) { // Assigns the line and checks if its null
						if (removeName.equals(line.trim())) // If the name is equal to the line, then it iterates to the
															// next step in the loop
							continue;
						fr.write(line + "\n"); // Otherwise writes to the tempFiles
					}
					br.close(); // Closes the writer and readers
					fr.close();
					namesFile.delete(); // Delete the original file
					tempFile.renameTo(namesFile); // Rename the new file to the original
					popUp.close();
					names = loadNames(); // Loads the names from the updated file
					topBar.getChildren().remove(0); // Refreshes the GUI by removing and re-inserting the names combobox
					topBar.getChildren().add(0, names);
				} catch (IOException e) {
					produceWarningPopUp(e.getLocalizedMessage()
							+ "\nIf you know the developer contact me, otherwise enjoy debugging :(", false);
				}
			});
		} else {
			warning.setPrefWidth(250); // If the warning is not removing a person/organization from the tracker, then
										// it creates just a warning.
			warningButton.setText("I understand"); // Sets the color, style, and text of the button
			warningButton.setStyle("-fx-background-color: #6082B6;");
			warningButton.setOnAction(event -> {
				popUp.close();
			}); // Sets the warning button to remove the popup scene on click
			buttons = new HBox(warningButton); // HBox to allow for button alignment
		}
		// Sets alignment of the buttons hbox
		buttons.setAlignment(Pos.CENTER);
		VBox allElements = new VBox(warning, buttons); // VBox to hold all of the elements
		allElements.setPadding(new Insets(10, 15, 20, 15)); // Sets padding, spacing and alignment
		allElements.setAlignment(Pos.CENTER);
		allElements.setSpacing(20);

		popUp.setScene(new Scene(allElements)); // Sets scene and shows the stage
		popUp.setTitle("Warning");
		popUp.show();
	}

	public void addReceipt() { // Add receipt method, which checks if the values to be added to the receipt are
								// valid, else produces warning popups

		File receiptFile = new File("src/dataFiles/" + names.getValue() + "-Receipts.csv"); // the csv file for whatever
																							// name is in the combobox
		try {
			FileWriter fr;
			if (receiptFile.createNewFile()) { // This checks if the csv file is being created and if so, adds the first
												// 6 items to the csv for heading
				fr = new FileWriter(receiptFile, true); // This is need to write to the file
				fr.write("Date, Price, Location, Type, Sum Of Prices:,=SUM(b:b),\n");// This last item sums the 2nd row
																						// for excel, may need to be
																						// changed if you are using a
																						// different csv extension
			} else
				fr = new FileWriter(receiptFile, true);

			Float price; // Price variable for checking and inputting into the csv file
			String date; // String variable to represent the date for checking and inputting into the csv
							// file

			if (priceField.getText().isBlank() || dateField.getText().isBlank() || // This checks if either the date or
																					// price fields have default values
																					// or are blank as they require
																					// values to be inserted into the
																					// csv file
					priceField.getText().equals(defaultPriceText) || dateField.getText().equals(defaultDateText)) {
				produceWarningPopUp("Fill out date and price fields before adding a receipt", false); // Produces a
																										// warning popup
				fr.close(); // closes writer
				return;
			} else if ((price = checkPrice()) == null) { // This assigns price from checkPrice() and if it is null then
															// an user input error occurred
				produceWarningPopUp("Insert Correct Price Format, like: 19.99", false); // Produces a popup alerting the
																						// user to their input error
				fr.close(); // closes writer
				return;
			} else if ((date = checkDate()) == null) { // This assigns date from checkDate() and if it is null, then an
														// user input error occurred
				produceWarningPopUp("Insert Correct Date Format, like: 06/26/22", false); // Produces a popup warning
																							// alerting the use to their
																							// error
				fr.close(); // closes writer
				return;
			} else if (locField.getText().isBlank() && !emptyLocCheck) { // This checks if the emptyLockCheck is false
																			// and the location field is blank
				emptyLocCheck = true; // If so it switches emptyLocCheck to true
				produceWarningPopUp("It is strongly recommended to insert a location, but not required", false); // And produces a one-time warning popup
				fr.close(); // closes writer
				return;
			}

			fr.write(date + "," + price.toString() + "," // Writes the date, price, location and type to the csv file
					+ locField.getText() + "," + typeField.getText() + ",\n");
			dateField.clear(); // Clears the fields
			priceField.clear();
			typeField.clear();
			locField.clear();
			fr.close(); // closes writer
		} catch (IOException e) { // If
			produceWarningPopUp(e.getLocalizedMessage()
					+ "\n If a program is accessing the file you are trying to writer to, try closing that program "
					+ "before attempting to add another receipt", false);
		}
	}

	public Float checkPrice() { // This method checks the price and return it if it can be parsed
		String check = (priceField.getText()); // Gets the priceField data
		if (check.charAt(0) == '$') // Checks if the first value is $
			check = check.substring(1); // If so check becomes check(1-check.length()), if there is more non-numeric
										// characters then the user can't read and it really isn't my fault (hopefully)
		try {
			Float value = Float.parseFloat(check); // attempts to parse a float from the input
			check = String.format("%.2f", value); // Removes trailing values beyond the second decimal
			return Float.parseFloat(check); // Returns the parsed float of the formatted string
		} catch (Exception e) {
			return null; // If there is an exception while parsing it returns null
		}
	}

	public String checkDate() {
		SimpleDateFormat dateFormator = new SimpleDateFormat("MM/dd/YY"); // Uses the month/day/year format, check the
																			// API if you want to make changes to how it
																			// is stored into the csv file
		Date date; // date will be used to parse a date from the text
		try {
			date = dateFormator.parse(dateField.getText()); // parses the date from the text if possible
		} catch (Exception e) {
			return null; // If there is an error while parsing, returns null
		}
		return dateFormator.format(date); // returns a string of the formatted date.
	}

}
