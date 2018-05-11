package gui;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import backend.FileTokenizer;
import backend.LanguageHandler;
import backend.Vocabulary;
import backend.Word;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class MainGUI extends Application  {
	// Backend data
	private static Vocabulary emptyWords = new Vocabulary();
	private static Vocabulary stemmedWords = new Vocabulary();
	private static Word currentWord = new Word();
	private static FileTokenizer fileTokenizer;
	
	// Constants 
	private static final Integer contextsHeight = 70;
	private static final Integer listViewHeight = 180;
	private static final Integer listViewWidth = 200;
	private static final Integer letterLabelHeight = 30;
	private static final Integer arrowWidth = 60;
	private static final Integer arrowHeight = 40;
	private static final String baseLanguageFilename = "/properties/label_messages";
	private static final String defaultLanguage = "en";
	private static final Integer menuWidth = 150;
	
	// Control variables
	private static Boolean blockSelection = false;
	private static Integer maxWordLength = 0;
	private static Boolean sortedByStem = false;
	private static String baseInputFilename;
	
	// GUI components
	// Actual components
	private static ListView<String> stemmedWordsListView;
	private static ListView<String> stemmedStemsListView;
	private static ListView<String> emptyWordsListView;
	
	private static ListView<String> currentWordContextsListView = new ListView<>();;
	
	private static Button leftArrowButton;
	private static Button rightArrowButton;
	private static TextField currentWordTextField = new TextField();
	
	private static Button removeStemButton = new Button();
	private static Button stemButton = new Button();
	private static Button mergeButton = new Button();
	private static Button splitButton = new Button();
	
	private static Button sortWordButtonEmpty = new Button();
	private static Button sortWordButtonStemmed = new Button();
	private static Button sortStemButtonStemmed = new Button();
	private static ArrayList<Button> lettersWordStem = new ArrayList<Button>();
	
	// Lists sorted by stem
	//private static List<String> wordList = new ArrayList<String>();
	//private static List<String> stemList = new ArrayList<String>();
	
	// Menu items
	private static MenuBar menuBar = new MenuBar();
	private static Menu menuFile = new Menu();
	private static Menu menuView = new Menu();
	private static MenuItem menuFileOpen = new MenuItem();
	private static MenuItem menuFileSave = new MenuItem();
	private static MenuItem menuFileSaveAs = new MenuItem();
	private static Menu menuViewLanguage = new Menu();
	private static ToggleGroup manuViewLanguagesTG = new ToggleGroup();
	private static RadioMenuItem menuViewLanguageEn = new RadioMenuItem();
	private static RadioMenuItem menuViewLanguageSr = new RadioMenuItem();
	
	// Labels
	private static Text currentWordLabel = new Text();
	private static Text emptyWordsLabel = new Text();
	private static Text stemmedWordsLabel = new Text();
	private static Text currentWordContextsLabel = new Text();
	
	// Containers
	private static GridPane grid = new GridPane();
	private static GridPane lettersArrows = new GridPane();
	private static GridPane lettersPane = new GridPane();
	private static GridPane arrows = new GridPane();
	private static GridPane controlButtons = new GridPane();
	private static GridPane lists = new GridPane();
	private static GridPane listsLeft = new GridPane();
	private static GridPane listsRight = new GridPane();
	private static GridPane contextPane = new GridPane();
	
	private static void styleGrid() {
	    grid.getChildren().clear();
	    grid.setHgap(40);
	    grid.setVgap(20);
	    grid.setPadding(new Insets(40, 40, 40, 40));
	    GridPane.setHalignment(grid, HPos.CENTER);
	}

	private static void styleContexts() {
		contextPane.setVgap(10);
		contextPane.setAlignment(Pos.CENTER);
	}
	
	private static void addContextsLabel() {
		currentWordContextsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
	    GridPane.setHalignment(currentWordContextsLabel, HPos.CENTER);
	    
	    contextPane.add(currentWordContextsLabel, 0, 1);
	}
	
	private static void addContextList() {
		currentWordContextsListView.setPrefHeight(contextsHeight);
		currentWordContextsListView.setPrefWidth(listViewWidth * 3.5);
		currentWordContextsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		contextPane.add(currentWordContextsListView, 0, 2);
	}
	
	private static void addContextButton() {
		GridPane.setHalignment(splitButton, HPos.CENTER);
		splitButton.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
		splitButton.setAlignment(Pos.CENTER);
		splitButton.setPrefSize(130, 20);
		splitButton.setDisable(true);
		
		contextPane.add(splitButton, 0, 3);
	}
	
	private static void addContextsComponents() {
		addContextsLabel();
		addContextList();
		addContextButton();
		
		grid.add(contextPane, 0, 0);
	}
	
	private static void styleLetterArrows() {
	    lettersArrows.setHgap(10);
	    lettersArrows.setVgap(10);
	    GridPane.setHalignment(lettersArrows, HPos.CENTER);
	    lettersArrows.setAlignment(Pos.CENTER);
	}
	
	private static void addCurrentWordLabel() {
		currentWordLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
	    GridPane.setHalignment(currentWordLabel, HPos.CENTER);
	    
	    lettersArrows.add(currentWordLabel, 0, 0);
	}
	
	private static void addLettersPane() {
		lettersPane.setPrefHeight(letterLabelHeight);
		lettersArrows.add(lettersPane, 0, 1);
	}
	
	private static void addArrowButtons() {
		arrows.setHgap(10);
		
		leftArrowButton = new Button();
	    leftArrowButton.getStyleClass().add("arrow-button");
	    leftArrowButton.getStyleClass().add("arrow-button-left");
	    leftArrowButton.setPrefWidth(arrowWidth);
	    leftArrowButton.setPrefHeight(arrowHeight);
	    
	    rightArrowButton = new Button();
	    rightArrowButton.getStyleClass().add("arrow-button");
	    rightArrowButton.getStyleClass().add("arrow-button-right");
	    rightArrowButton.setPrefWidth(arrowWidth);
	    rightArrowButton.setPrefHeight(arrowHeight);
	    
	    arrows.add(leftArrowButton, 0, 0);
	    arrows.add(rightArrowButton, 1, 0);
	    arrows.setAlignment(Pos.CENTER);
	    
	    lettersArrows.add(arrows, 0, 2);
	}

	private static void addCurrentWordTextField() {
		currentWordTextField.setPrefWidth(200);
		currentWordTextField.setMaxWidth(200);
		currentWordTextField.setAlignment(Pos.CENTER);
		GridPane.setHalignment(currentWordTextField, HPos.CENTER);
		
		lettersArrows.add(currentWordTextField, 0, 3);
	}

	private static void addLetterArrowsComponents() {
		addCurrentWordLabel();
	    addLettersPane();
	    addArrowButtons();
	    addCurrentWordTextField();

	    grid.add(lettersArrows, 0, 1);
	}
	
	private static void styleControlButtons() {
		controlButtons.setHgap(10);
		controlButtons.setAlignment(Pos.CENTER);
	}
	
	private static void addControlButton(Button btn, Integer col, Integer row, Boolean bold) {
		btn.setFont(Font.font("Arial", bold? FontWeight.BOLD : FontWeight.NORMAL, 15));
		btn.setAlignment(Pos.CENTER);
		btn.setPrefSize(120, 20);
		
		controlButtons.add(btn, col, row);
	}
	
	private static void addControlButtons() {
		addControlButton(removeStemButton, 0, 0, false);
	    addControlButton(stemButton, 1, 0, true);
	    mergeButton.setDisable(true);
	    addControlButton(mergeButton, 2, 0, false);
	}
	
	private static void styleLists() {
		lists.setAlignment(Pos.CENTER);
		listsLeft.setPrefWidth(listViewWidth * 1.5);
	    listsRight.setPrefWidth(listViewWidth * 2);
	}
	
	private static void addControlButtonsComponents() {
		addControlButtons();
		
		grid.add(controlButtons, 0, 2);
	}
	
	private static void addLeftListComponents() {
		emptyWordsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		GridPane.setHalignment(emptyWordsLabel, HPos.CENTER);
		
		sortWordButtonEmpty.setPrefWidth(listViewWidth);
		sortWordButtonEmpty.setPrefHeight(letterLabelHeight);
		sortWordButtonEmpty.getStyleClass().add("sort-button-non-clickable");
		sortWordButtonEmpty.getStyleClass().add("sort-button-single");
		
		emptyWordsListView = new ListView<String>();
		emptyWordsListView.setPrefWidth(listViewWidth);
		emptyWordsListView.setPrefHeight(listViewHeight);
		emptyWordsListView.setItems(FXCollections.observableArrayList(emptyWords.getWordIds()));
		emptyWordsListView.getSelectionModel().select(0);
		emptyWordsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		listsLeft.add(emptyWordsLabel, 0, 0);
		listsLeft.add(sortWordButtonEmpty, 0, 1);
		listsLeft.add(emptyWordsListView, 0, 2);
		
		lists.add(listsLeft, 0, 0);
	}
	
	private static void addRightListComponents() {
	    stemmedWordsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
	    GridPane.setHalignment(stemmedWordsLabel, HPos.CENTER);
	    
	    GridPane right = new GridPane();
	    GridPane rightUp = new GridPane();
	    rightUp.setHgap(2);
	    GridPane rightDown = new GridPane();
	    
	    sortWordButtonStemmed.setPrefWidth(listViewWidth - 1);
	    sortWordButtonStemmed.setPrefHeight(letterLabelHeight);
	    sortWordButtonStemmed.getStyleClass().add("sort-button");
	    sortWordButtonStemmed.getStyleClass().add("sort-button-left");
	    
	    sortStemButtonStemmed.setPrefWidth(listViewWidth - 1);
	    sortStemButtonStemmed.setPrefHeight(letterLabelHeight);
	    sortStemButtonStemmed.getStyleClass().add("sort-button");
	    sortStemButtonStemmed.getStyleClass().add("sort-button-right");
	    
	    rightUp.add(sortWordButtonStemmed, 0, 0);
	    rightUp.add(sortStemButtonStemmed, 1, 0);
	    
	    stemmedWordsListView = new ListView<String>();
	    stemmedWordsListView.setPrefWidth(listViewWidth);
	    stemmedWordsListView.setPrefHeight(listViewHeight);
	    stemmedWordsListView.setItems(FXCollections.observableArrayList(stemmedWords.getWordIds()));
	    stemmedWordsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    
	    stemmedStemsListView = new ListView<String>();
	    stemmedStemsListView.setPrefWidth(listViewWidth);
	    stemmedStemsListView.setPrefHeight(listViewHeight);
	    stemmedStemsListView.setItems(FXCollections.observableArrayList(stemmedWords.getStems()));
	    //stemmedStemsListView.setFocusTraversable(false);
	    //stemmedStemsListView.setOnMouseClicked(null);
	    
	    rightDown.add(stemmedWordsListView, 0, 0);
	    rightDown.add(stemmedStemsListView, 1, 0);
	    
	    right.add(rightUp, 0, 0);
	    right.add(rightDown, 0, 1);
	    
	    listsRight.add(stemmedWordsLabel, 0, 0);
	    listsRight.add(rightUp, 0, 1);
	    listsRight.add(rightDown, 0, 2);

	    lists.add(listsRight, 1, 0);
	}
	
	private static void addListsComponents() {
		addLeftListComponents();
	    addRightListComponents();
	    
	    grid.add(lists, 0, 3);
	}

	// Add all components
	private static void addGridPane() {
		styleGrid();
		
		styleContexts();
		addContextsComponents();
		
	    styleLetterArrows();
	    addLetterArrowsComponents();
	    
	    styleControlButtons();
	    addControlButtonsComponents();
	    
	    styleLists();
	    addListsComponents();
	}
	
	private static void sortByStem() {
		/*List<Entry<String, String>> pairs = new ArrayList<Entry<String, String>>();
		wordList = new ArrayList<String>();
		stemList = new ArrayList<String>();
		
		for(Entry<String, String> entry: stemmedWords.getVocabulary().entrySet()) {
			pairs.add(new AbstractMap.SimpleEntry<String, String>(entry.getValue(), entry.getKey()));
		}
		
		Collections.sort(pairs, new Comparator<Entry<String, String>>(){
		   @Override
		   public int compare(Entry<String, String> a, Entry<String, String> b) {
			   int compared = a.getKey().compareTo(b.getKey());
			   return compared != 0 ? compared : a.getValue().compareTo(b.getValue());
		     }
		});
		
		for(Entry<String, String> pair: pairs) {
			wordList.add(pair.getValue());
			stemList.add(pair.getKey());
		}*/
	}
	
	// Refresh views of all listviews etc
	private static void refreshViews() {
		blockSelection = true;
	    emptyWordsListView.setItems(FXCollections.observableArrayList(emptyWords.getWordIds()));
	    /*if (!sortedByStem) {
	    	stemmedWordsListView.setItems(FXCollections.observableArrayList(stemmedWords.getWordIds()));
	    	stemmedStemsListView.setItems(FXCollections.observableArrayList(stemmedWords.getStems()));
	    }
	    else {
	    	stemmedWordsListView.setItems(FXCollections.observableArrayList(new ArrayList<String>(wordList)));
	    	stemmedStemsListView.setItems(FXCollections.observableArrayList(new ArrayList<String>(stemList)));
	    }*/
	    stemmedWordsListView.setItems(FXCollections.observableArrayList(stemmedWords.getWordIds()));
    	stemmedStemsListView.setItems(FXCollections.observableArrayList(stemmedWords.getStems()));
	    blockSelection = false;
	}
	
	// Make a stem, remove from empty list, add to stemmed list
	private static void makeStem() {
		try {
    		String wordId = currentWord.wordId;
    		
    		if (currentWord.word.length() < currentWordTextField.getText().length()) {
    			showInformationDialog(LanguageHandler.getMessage("errorMakeStemInfo"), LanguageHandler.getMessage("errorMakeStemTitle"));
    			return;
    		}
    		
    		if (wordId != null && !wordId.equals("")) {
    			currentWord.stem = currentWordTextField.getText();
    			stemmedWords.addWord(wordId, currentWord);
        	    
        		if (emptyWords.containsWordId(wordId)) {
        			if (emptyWords.getSize() > 1) {
        				currentWord = emptyWords.getNextWord(wordId);
        			}
        			else {
            			currentWord = new Word();
            		}
        			emptyWords.removeWord(wordId);
        		}
        		else { // if current word comes from the stemmed dictionary
	        		if (!emptyWords.isEmpty()) {
	        			currentWord = emptyWords.getFirstWord();
	        		}
	        		else {
	        			currentWord = new Word();
	        		}
        		}
        		
        		if (sortedByStem) {
	        		sortByStem();
	        	}
        		
        		refreshViews();
        	    moveWordStemToLabels();
        	    
        	    if (!emptyWords.isEmpty()) {
        	    	emptyWordsListView.requestFocus();
	        	    emptyWordsListView.getSelectionModel().select(currentWord.wordId);
        		}
    		}
		} catch (Exception eStem) {
			System.out.println(eStem.getMessage());
		}
	}
	
	// Remove (word, stem) from stemmed list, add to empty list
	private static void removeStem() {
		try {
    		String wordId = currentWord.wordId;
    		
    		if (wordId != null && !wordId.equals("") && stemmedWords.containsWordId(wordId)) {
    			currentWord.stem = currentWord.word;
        		emptyWords.addWord(wordId, currentWord);

        		if (stemmedWords.getSize() > 1) {
        			currentWord = stemmedWords.getNextWord(wordId);
        		}
        		else {
        			currentWord = new Word();
        		}
        		stemmedWords.removeWord(wordId);
        		
	        	if (sortedByStem) {
	        		sortByStem();
	        	}
	        	
	        	refreshViews();
        	    moveWordStemToLabels();
        	    grid.requestFocus();
        	    
        	    if (!stemmedWords.isEmpty()) {
        	    	stemmedWordsListView.requestFocus();
	        		stemmedWordsListView.getSelectionModel().select(currentWord.wordId);
        	    }
    		}
		} catch (Exception eStem) {
			System.out.println(eStem.getMessage());
		}
	}
	
	private static void moveStemLeft() {
		try {
			currentWord.moveStemLeft();
			moveWordStemToLabels();
		} catch (Exception eButton) {
			System.out.println(eButton.getMessage());
		}
	}
	
	private static void moveStemRight() {
		try {
			currentWord.moveStemRight();
			moveWordStemToLabels();
		} catch (Exception eButton) {
			System.out.println(eButton.getMessage());
		}
	}
	
	private static void selectionEmptyWords(String newValue) {
		try {
    		if (blockSelection == false) {
    			blockSelection = true;
	    		if (!emptyWords.isEmpty() && newValue != null) {
	    			currentWord = emptyWords.getWordById(newValue);
					moveWordStemToLabels();
					stemmedWordsListView.getSelectionModel().clearSelection();
					stemmedStemsListView.getSelectionModel().clearSelection();
	    		}
	    		blockSelection = false;
    		}
		} catch (Exception eEmpty) {
			System.out.println(eEmpty.getMessage());
		}
	}
	
	private static void selectionStemmedWords(String newValue) {
		try {
    		if (blockSelection == false) {
    			blockSelection = true;
	    		if (!stemmedWords.isEmpty() && newValue != null) {
	    			currentWord = stemmedWords.getWordById(newValue);

		    		stemmedStemsListView.getSelectionModel().select(stemmedWordsListView.getSelectionModel().getSelectedIndex());
		    		moveWordStemToLabels();
		    		
		    		emptyWordsListView.getSelectionModel().clearSelection();
	    		}
	    		blockSelection = false;
    		}
	    } catch (Exception eEmpty) {
			System.out.println(eEmpty.getMessage());
		}
	}
	
	private static void selectionStemmedStems() {
		try {
    		if (blockSelection == false) {
    			blockSelection = true;
	    		if (!stemmedWords.isEmpty()) { // has to be index because search by stem makes no sense
	    			Integer index = stemmedStemsListView.getSelectionModel().getSelectedIndex();
	    			currentWord = stemmedWords.getWordByIndex(index);
		    		
	    			stemmedWordsListView.getSelectionModel().clearSelection();
		    		stemmedWordsListView.getSelectionModel().select(stemmedStemsListView.getSelectionModel().getSelectedIndex());
		    		moveWordStemToLabels();
		    		
		    		emptyWordsListView.getSelectionModel().clearSelection();
	    		}
	    		blockSelection = false;
    		}
	    } catch (Exception eEmpty) {
			System.out.println(eEmpty.getMessage());
		}
	}
	
	private static void makeNewLabels(Integer wordLen) {
		for (int i = 0; i < wordLen; i++) {
			Button letter = new Button();
	    	letter.getStyleClass().add("label-word");
	    	if (i == 0 && i == wordLen - 1) {
	    		letter.getStyleClass().add("label-word-only");
	    	}
	    	else if (i == 0) {
	    		letter.getStyleClass().add("label-word-left");
	    	}
	    	else if (i == wordLen - 1) {
	    		letter.getStyleClass().add("label-word-right");
	    	}
	    	letter.setAlignment(Pos.CENTER);
	    	letter.setPrefHeight(letterLabelHeight);
	    	letter.setPrefWidth(letterLabelHeight);
	    	lettersWordStem.add(i, letter);
	    	lettersPane.add(letter, i, 0);
	    }
	}
	
	private static void addListenersToLabels(Integer wordLen) {
		for (int i = 0; i < wordLen; i++) {
			Button letter = lettersWordStem.get(i);
			final Integer iCopy = i;
			/*letter.setOnMouseEntered(new EventHandler<MouseEvent>() {
			    public void handle(MouseEvent me) {
			        
			    }
			});*/
			
			letter.setOnAction(new EventHandler<ActionEvent>() {
		        @Override public void handle(ActionEvent e) {
		        	for (int j = 0; j < iCopy + 1; j++) {
			        	lettersWordStem.get(j).setStyle("-fx-background-color: #EEEEFF; -fx-background-insets: 0 0 0 0;");
			        }
			        for (int j = iCopy + 1; j < wordLen; j++) {
			        	lettersWordStem.get(j).setStyle("-fx-background-color: #FF4D4D; -fx-background-insets: 0 0 0 0;");
			        }
			        try {
						currentWord.stem = currentWord.word.substring(0, iCopy + 1);
						moveWordStemToLabels();
						grid.requestFocus();
					} catch (Exception ee) {
						System.out.println(ee.getMessage());
					}
		        }
		    });
		}
	}
	
	// Init labels for current word
	private static void initLabels(Integer wordLen) {
		lettersWordStem.clear();
		lettersPane.getChildren().clear();
		lettersWordStem  = new ArrayList<Button>(wordLen);
		
		lettersPane.setAlignment(Pos.CENTER);
		lettersPane.setPrefHeight(letterLabelHeight);
		
		makeNewLabels(wordLen);
		addListenersToLabels(wordLen);
	}
	
	private static CornerRadii makeCornerRadius(Integer i, Integer wordLen) {
		CornerRadii cr = null;
		
		if (i == wordLen - 1) {
			cr = new CornerRadii(0, 5, 5, 0, false);
		}
		else if (i == 0) {
			cr = new CornerRadii(5, 0, 0, 5, false);
		}
		else {
			cr = new CornerRadii(0, 0, 0, 0, false);
		}
		
		return cr;
	}
	
	// Set text and color of labels based on current wordStem
	private static void moveWordStemToLabels() throws Exception {
		initLabels(currentWord.word.length());
		
		Boolean sameBase = currentWord.stem.equals(currentWord.word.substring(0, currentWord.stem.length()));
		
		for (int i = currentWord.word.length() - 1; i >= 0; i--) {
			lettersWordStem.get(i).setText(String.valueOf(currentWord.word.charAt(i)));
			CornerRadii cr = makeCornerRadius(i, currentWord.word.length());
			
			if (i >= currentWord.stem.length()) {
				if (sameBase) {
					lettersWordStem.get(i).setStyle("-fx-background-color: #FF4D4D; -fx-background-insets: 0 0 0 0;");
				}
				else {
					lettersWordStem.get(i).setStyle("-fx-background-color: #A9A9A9; -fx-background-insets: 0 0 0 0;");
				}
			}
			else {
				if (sameBase || currentWord.word.charAt(i) == currentWord.stem.charAt(i)) {
					Paint backgroundPaint = Paint.valueOf("#EEEEFF");
					lettersWordStem.get(i).setBackground(new Background(new BackgroundFill(backgroundPaint, cr, Insets.EMPTY)));
				}
				else {
					lettersWordStem.get(i).setStyle("-fx-background-color: #A9A9A9; -fx-background-insets: 0 0 0 0;");
				}
			}
		}
		
		currentWordContextsListView.setItems(FXCollections.observableArrayList(currentWord.contexts));
	    currentWordTextField.setText(currentWord.stem);
	}
	
	private static void showInformationDialog(String info, String title) {
		ButtonType confirmButton = new ButtonType(LanguageHandler.getMessage("confirmButton"), ButtonBar.ButtonData.OK_DONE);
		String fileLocation = "images/bad.png";
		Alert alert = new Alert(AlertType.INFORMATION, info, confirmButton);
		alert.setTitle(title);
		alert.setHeaderText("");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(fileLocation)); // To add an icon
		alert.showAndWait();
	}
	
	private static void makeSaveDialog(Boolean isExit, WindowEvent we) {
    	try {
    		ButtonType yesButton = new ButtonType(LanguageHandler.getMessage("yesButton"), ButtonBar.ButtonData.OK_DONE);
    		ButtonType noButton = new ButtonType(LanguageHandler.getMessage("noButton"), ButtonBar.ButtonData.CANCEL_CLOSE);
    		ButtonType cancelButton = new ButtonType(LanguageHandler.getMessage("cancelButton"), ButtonBar.ButtonData.CANCEL_CLOSE);
    		
    		Alert alert;
    		if (!isExit) {
    			alert = new Alert(AlertType.CONFIRMATION, LanguageHandler.getMessage("dialogSaveQuestion"), yesButton, noButton);
    		}
    		else {
    			alert = new Alert(AlertType.CONFIRMATION, LanguageHandler.getMessage("dialogSaveQuestion"), yesButton, noButton, cancelButton);
    		}
    		alert.setTitle(LanguageHandler.getMessage("dialogSaveTitle"));
    		alert.setHeaderText(LanguageHandler.getMessage("dialogSaveFilenames"));
    		
    		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
    		stage.getIcons().add(new Image("images/save.png")); // To add an icon
    		alert.showAndWait();
    		
    		if (alert.getResult() == yesButton) {
    			FileTokenizer.saveToFiles(baseInputFilename, stemmedWords);
        		grid.requestFocus();
        		//makeInformationDialog("Rezultat je uspešno sačuvan!", "Čuvanje uspešno", confirmButton, "images/good.png");
    		}
    		else {
    			if (isExit && alert.getResult() == cancelButton) {
    				we.consume();
    			}
    		}
		} catch (Exception eRemButton) {
			showInformationDialog(LanguageHandler.getMessage("errorSaveInfo"), LanguageHandler.getMessage("errorSaveTitle"));
		}
	}
	
	private static void showOpenFileDialog(Stage stage) {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(LanguageHandler.getMessage("dialogOpenFileTitle"));
			fileChooser.setInitialDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
			fileChooser.getExtensionFilters().addAll(
			         new ExtensionFilter("All Files", "*.*"));
			File selectedFile = fileChooser.showOpenDialog(stage);

			if (selectedFile != null) {
				emptyWords = new Vocabulary();
				stemmedWords = new Vocabulary();
				currentWord = new Word();
				baseInputFilename = selectedFile.getAbsolutePath();
				fileTokenizer = new FileTokenizer();
				fileTokenizer.readVocabularies(baseInputFilename, emptyWords, stemmedWords);
				maxWordLength = fileTokenizer.getMaxLength();
				if (!emptyWords.isEmpty()) {
	    			currentWord = emptyWords.getFirstWord();
	    		}
				Integer sceneWidth = listViewWidth / 10 * 35 + 100;
				if (maxWordLength * letterLabelHeight + 100 > sceneWidth) {
		        	sceneWidth = maxWordLength * letterLabelHeight + 100;
		        	lettersPane.setPrefWidth(maxWordLength * letterLabelHeight);
			        stage.setWidth(sceneWidth);
		        }
				moveWordStemToLabels();
				refreshViews();
			}
		} catch (Exception ex) {
			showInformationDialog(LanguageHandler.getMessage("errorOpenFile"), LanguageHandler.getMessage("errorOpenUnsuccess"));
		}
	}
	
	private static void doSplit(Vocabulary voc, ListView<String> lw, String wordId, ArrayList<String> contextsForSecondWord) throws Exception {
		currentWord = voc.getNextWord(wordId);
		voc.splitWordContext(wordId, contextsForSecondWord);
    	refreshViews();
	    moveWordStemToLabels();
	    lw.requestFocus();
	    lw.getSelectionModel().clearSelection();
	    lw.getSelectionModel().select(currentWord.wordId);
	}
	
	private static void splitGroups() {
		try {
        	String wordId = currentWord.wordId;
        	ArrayList<String> contextsForSecondWord = new ArrayList<String>(currentWordContextsListView.getSelectionModel().getSelectedItems());
        	if (emptyWords.getWordIds().contains(wordId)) {
        		doSplit(emptyWords, emptyWordsListView, wordId, contextsForSecondWord);
        	}
        	else if (stemmedWords.getWordIds().contains(wordId)) {
        		doSplit(stemmedWords, stemmedWordsListView, wordId, contextsForSecondWord);
        	}
    	} catch (Exception ex) {
    		showInformationDialog(LanguageHandler.getMessage("errorSplitInfo"), LanguageHandler.getMessage("errorSplitTitle"));
    	}
	}
	
	public static void doMerging(Vocabulary v, ArrayList<String> wordIds) throws Exception {
		v.mergeWords(wordIds);
	}
	
	public static void mergeGroups() {
		try {
			ArrayList<String> wordIds = new ArrayList<String>(emptyWordsListView.getSelectionModel().getSelectedItems());
			if (wordIds.size() >= 1) {
				doMerging(emptyWords, wordIds);
			} else {
				wordIds = new ArrayList<String>(stemmedWordsListView.getSelectionModel().getSelectedItems());
				if (wordIds.size() >= 1) {
					doMerging(stemmedWords, wordIds);
				}
			}
			refreshViews();
		} catch (Exception ex) {
			showInformationDialog(LanguageHandler.getMessage("errorMergeInfo"), LanguageHandler.getMessage("errorMergeTitle"));
    	}
	}
	
	private static void enableDisableMergeButton(ListView<String> lw) {
		ArrayList<String> wordIds = new ArrayList<String>(lw.getSelectionModel().getSelectedItems());
    	if (wordIds.size() > 1) {
    		String base = wordIds.get(0);
    		if (base.matches(".*_\\d+")) {
    			base = base.split("_")[0];
    		}
    		for (String wid: wordIds) { // if any of the selected is not in a form base_word_[digit]
    			if (!wid.equals(base) && !wid.matches(base + "_\\d+")) {
    				mergeButton.setDisable(true);
    				return;
    			}
    		}
    		
    		mergeButton.setDisable(false);
    	}
    	else {
    		mergeButton.setDisable(true);
    	}
	}
	
	private static void enableDisableSplitButton() {
		ArrayList<String> contextsForSecondWord = new ArrayList<String>(currentWordContextsListView.getSelectionModel().getSelectedItems());
		
		if (contextsForSecondWord.size() == 0 || currentWordContextsListView.getItems().size() - contextsForSecondWord.size() == 0) {
			splitButton.setDisable(true);
		}
		else {
			splitButton.setDisable(false);
		}
	}
	
	private static void addListeners(Stage stage) {
		// Split button
	    splitButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	splitGroups();
	        }
	    });
		
	    // Empty words list
	    emptyWordsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	selectionEmptyWords(newValue);
		    }
		});
	    
	    // Enable and disable mergeButton. Enable only when there is multiple selection and same base
	    emptyWordsListView.getSelectionModel().getSelectedItems().addListener((Change<? extends String> c) -> {
	    	enableDisableMergeButton(emptyWordsListView);
	    });
	    
	    // Enable and disable mergeButton. Enable only when there is multiple selection and same base
	    stemmedWordsListView.getSelectionModel().getSelectedItems().addListener((Change<? extends String> c) -> {
	    	enableDisableMergeButton(stemmedWordsListView);
	    });
	    
	    // Enable and disable splitButton
	    currentWordContextsListView.getSelectionModel().getSelectedItems().addListener((Change<? extends String> c) -> {
	    	enableDisableSplitButton();
	    });
	    
	    emptyWordsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        	selectionEmptyWords(emptyWordsListView.getSelectionModel().getSelectedItem());
	        }
	    });
	    
	    // Stemmed words list
	    stemmedWordsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	selectionStemmedWords(newValue);
		    }
		});
	    
	    stemmedWordsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        	selectionStemmedWords(stemmedWordsListView.getSelectionModel().getSelectedItem());
	        }
	    });
	    
	    // Stemmed stems list
	    stemmedStemsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	selectionStemmedStems();
		    }
		});
	    
	    stemmedStemsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        	selectionStemmedStems();
	        }
	    });
	    
	    stemmedStemsListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        @Override public void handle(KeyEvent ke) {
	            if (ke.getCode().equals(KeyCode.ENTER)) {
	            	makeStem();
	            }
	            else if (ke.getCode().equals(KeyCode.BACK_SPACE)) {
	            	removeStem();
	            }
	        }
	    });
	    
	    // Left arrow button
	    leftArrowButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	moveStemLeft();
	        }
	    });
	    
	    // Right arrow button
	    rightArrowButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	moveStemRight();
	        }
	    });
	    
	    // Stem button
	    stemButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	makeStem();
	        }
	    });
	    
	    // Remove stem button
	    removeStemButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	removeStem();
	        }
	    });
	    
	    // Save button
	    mergeButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
        		//makeSaveDialog(false, null);
	        	mergeGroups();
	        }
	    });
	    
	    // Sort stem button
	    /*sortStemButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	sortByStem();
	        	sortedByStem = true;
	        	refreshViews();
	        }
	    });*/
	    
	    // Sort word button
	    /*sortWordButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	sortedByStem = false;
	        	refreshViews();
	        }
	    });*/
	    
	    // Keys pressed
	    grid.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        @Override public void handle(KeyEvent ke) {
	            if (ke.getCode().equals(KeyCode.ENTER)) {
	            	makeStem();
	            }
	            else if (ke.getCode().equals(KeyCode.BACK_SPACE)) {
	            	removeStem();
	            }
	            else if (ke.getCode().equals(KeyCode.LEFT)) {
	            	moveStemLeft();
	            	ke.consume();
	            }
	            else if (ke.getCode().equals(KeyCode.RIGHT)) {
	            	moveStemRight();
	            	ke.consume();
	            }
	        }
	    });
	    
	    emptyWordsListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        @Override public void handle(KeyEvent ke) {
	            if (ke.getCode().equals(KeyCode.ENTER)) {
	            	makeStem();
	            }
	        }
	    });
	    
	    stemmedWordsListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        @Override public void handle(KeyEvent ke) {
	            if (ke.getCode().equals(KeyCode.ENTER)) {
	            	makeStem();
	            }
	            else if (ke.getCode().equals(KeyCode.BACK_SPACE)) {
	            	removeStem();
	            }
	        }
	    });
	    
	    // Menu
	    menuFileOpen.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	showOpenFileDialog(stage);
	        }
		});
	    
	    menuFileSave.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	makeSaveDialog(false, null);
	        }
		});
	    
	    menuViewLanguageEn.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	changeLanguage("en", stage);
	        }
		});
		
		menuViewLanguageSr.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	changeLanguage("sr", stage);
	        }
		});
	}
	
	private static void changeLanguage(String lang, Stage stage) {
		try {
			if (!LanguageHandler.getLanguage().equals(lang)) {
				LanguageHandler.setLanguage(lang);
				setComponentMessages();
				stage.setTitle(LanguageHandler.getMessage("annotationTool"));
			}
		} catch (Exception e1) {
			
		}
	}
	
	private static void setComponentMessages() {
		// Buttons
		//openButton.setText(LanguageHandler.getMessage("openButton"));
		stemButton.setText(LanguageHandler.getMessage("stemButton"));
		removeStemButton.setText(LanguageHandler.getMessage("removeStemButton"));
		mergeButton.setText(LanguageHandler.getMessage("mergeButton"));
		splitButton.setText(LanguageHandler.getMessage("splitButton"));
		sortWordButtonEmpty.setText(LanguageHandler.getMessage("sortWordButton"));
		sortWordButtonStemmed.setText(LanguageHandler.getMessage("sortWordButton"));
		sortStemButtonStemmed.setText(LanguageHandler.getMessage("sortStemButton"));

		// Labels
		currentWordLabel.setText(LanguageHandler.getMessage("currentWordLabel"));
		emptyWordsLabel.setText(LanguageHandler.getMessage("emptyWordsLabel"));
		stemmedWordsLabel.setText(LanguageHandler.getMessage("stemmedWordsLabel"));
		currentWordContextsLabel.setText(LanguageHandler.getMessage("currentWordContextsLabel"));
		
		// Menu
		menuFile.setText(LanguageHandler.getMessage("menuFile"));
		menuFileOpen.setText(LanguageHandler.getMessage("menuFileOpen"));
		menuFileSave.setText(LanguageHandler.getMessage("menuFileSave"));
		menuFileSaveAs.setText(LanguageHandler.getMessage("menuFileSaveAs"));
		menuView.setText(LanguageHandler.getMessage("menuView"));
		menuViewLanguage.setText(LanguageHandler.getMessage("menuViewLanguage"));
		menuViewLanguageEn.setText(LanguageHandler.getMessage("menuViewLanguageEnglish"));
		menuViewLanguageSr.setText(LanguageHandler.getMessage("menuViewLanguageSerbian"));
	}
	
	private static void addStylesheets(Scene scene) {
		scene.getStylesheets().add("css/word_labels.css");
		scene.getStylesheets().add("css/sort_buttons.css");
		scene.getStylesheets().add("css/arrow_buttons.css");
	}
	
	private static void styleStage(Stage stage, Scene scene) {
		stage.setTitle("Stemmer Tool");
		stage.setScene(scene);
		stage.getIcons().add(new Image(MainGUI.class.getResourceAsStream("/images/pencil.png")));
		stage.show();
		Integer sceneWidth = listViewWidth / 10 * 35 + 100;
        if (maxWordLength * letterLabelHeight + 100 > sceneWidth) {
        	sceneWidth = maxWordLength * letterLabelHeight + 100;
        	lettersPane.setPrefWidth(maxWordLength * letterLabelHeight);
        }
        stage.setWidth(sceneWidth);
        stage.setOnCloseRequest((WindowEvent we) -> {
        	makeSaveDialog(true, we);
        });
	}
	
	private static void styleScrollBars() {
		Node n1 = stemmedWordsListView.lookup(".scroll-bar");
        if (n1 instanceof ScrollBar) {
            final ScrollBar bar1 = (ScrollBar) n1;
            Node n2 = stemmedStemsListView.lookup(".scroll-bar");
            if (n2 instanceof ScrollBar) {
                final ScrollBar bar2 = (ScrollBar) n2;
                bar1.valueProperty().bindBidirectional(bar2.valueProperty());
                bar1.setStyle("-fx-scale-x: 0;");
                bar1.setPrefWidth(0);
            }
        }
	}
	
	private static void addMenus(Stage stage) {
		menuBar.setPrefWidth(menuWidth);

		menuFileOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
		menuFileSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
		
		menuFile.getItems().addAll(menuFileOpen, menuFileSave/*, menuFileSaveAs*/);
		
		menuViewLanguageEn.setToggleGroup(manuViewLanguagesTG);
		menuViewLanguageSr.setToggleGroup(manuViewLanguagesTG);
		menuViewLanguage.getItems().addAll(menuViewLanguageEn, menuViewLanguageSr);
		menuView.getItems().addAll(menuViewLanguage);
		
		menuBar.getMenus().addAll(menuFile, menuView);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		
		//initAll(rawArguments.get(0));
		LanguageHandler.initialize(baseLanguageFilename, defaultLanguage);
		setComponentMessages();
		addGridPane();
		addListeners(stage);
		
		root.getChildren().add(grid);
		root.getChildren().add(menuBar);
		
		Scene scene = new Scene(root, 1000, 680);
		addStylesheets(scene);
		styleStage(stage, scene);
		styleScrollBars();
		addMenus(stage);
		
		menuBar.prefWidthProperty().bind(stage.widthProperty());
        grid.requestFocus();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
