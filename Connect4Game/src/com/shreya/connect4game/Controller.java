package com.shreya.connect4game;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 82;
	private static final String discColor1 = "#660011";
	private static final String discColor2 = "#6A0080";
	String PLAYER_ONE;
	String PLAYER_TWO;
	private boolean isPlayerOneTurn = true;
	private Disk[][] insertedDiskArray = new Disk[ROWS][COLUMNS];
	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane InsertedDiskPane;
	@FXML
	public Label playerNameLabel;
	@FXML
	public TextField playerOneTextField, playerTwoTextField;
	@FXML
	public Button setNamesButton;
	private boolean isAllowedToInsert = true;

	public void createPlayground(){
		setNamesButton.setOnAction(event -> {
				PLAYER_ONE = playerOneTextField.getText();
				PLAYER_TWO = playerTwoTextField.getText();
		});

		Shape rectangleWithHoles = gameStructuralGrid();
		rootGridPane.add(rectangleWithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableColumn();
		for (Rectangle rectangle: rectangleList){
			rootGridPane.add(rectangle, 0,1);
		}
	}
	private  Shape gameStructuralGrid(){
		Shape rectangleWithHoles = new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER, (ROWS+1)*CIRCLE_DIAMETER);

		for (int row=0; row<ROWS; row++){
			for (int col=0; col<COLUMNS; col++){
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER/2);
				circle.setCenterX(CIRCLE_DIAMETER/2);
				circle.setCenterY(CIRCLE_DIAMETER/2);
				circle.setSmooth(true);

				circle.setTranslateX(col*(CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER/4);
				circle.setTranslateY(row*(CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER/4);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}
	private List<Rectangle>  createClickableColumn(){
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col=0; col<COLUMNS; col++){
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS+1)*CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER/4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert) {
					isAllowedToInsert = false;
					insertDisk(new Disk(isPlayerOneTurn), column);
				}
			});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}
	private void insertDisk(Disk disk, int column){
		int row = ROWS-1;
		while (row>=0){
		 	if ( getDiskIfPresent(row, column)==null)
		 		break;
		 	row--;
		}
		if (row<0)
		 	return;
		insertedDiskArray [row][column] = disk;
		InsertedDiskPane.getChildren().add(disk);
		disk.setTranslateX(column*(CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER/4);
        int currentRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disk);
		translateTransition.setToY(row*(CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER/4);
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true;
			if (gameEnded(currentRow, column)){
				// have to do
				gameOver();
			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
		});
		translateTransition.play();
	}
	private boolean gameEnded(int row, int column){
		List<Point2D> verticalPoints = IntStream.rangeClosed( row-3, row+3)
				                           .mapToObj(r-> new Point2D(r, column))
				                           .collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed( column-3, column+3)
				                            .mapToObj(c-> new Point2D(row, c))
				                            .collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row-3, column+3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
				                            .mapToObj(i -> startPoint1.add(i, -i))
				                            .collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row-3, column-3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startPoint2.add(i, i))
				.collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints)
				           || checkCombinations(horizontalPoints)
				           || checkCombinations(diagonal1Points)
				           || checkCombinations(diagonal2Points);

		return isEnded;
	}
	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point: points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disk disk =  getDiskIfPresent(rowIndexForArray, columnIndexForArray);
			if(disk != null && disk.isPlayerOneMove==isPlayerOneTurn){
				chain++;
				if (chain==4){
					return true;
				}

			} else {
				chain = 0;
			}

		}
		return false;
	}
	private  Disk getDiskIfPresent(int row, int column){
		if (row>=ROWS || row<0 || column>=COLUMNS || column<0)
			return null;
		return insertedDiskArray[row][column];
	}
	private void gameOver(){
		String winner = isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO;
		System.out.println("Winner is :" + winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect 4 Game");
		alert.setHeaderText("Winner is :" + winner);
		alert.setContentText("Want to play again ?");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No, Exit.");
		alert.getButtonTypes().setAll(yesBtn, noBtn);

		Platform.runLater( () -> {
			Optional<ButtonType> clickedBtn = alert.showAndWait();
			if (clickedBtn.isPresent() && clickedBtn.get() == yesBtn){
				resetGame();
			}
			else{
				Platform.exit();
				System.exit(0);
			}
		});
	}
	public void resetGame() {
		InsertedDiskPane.getChildren().clear();
		for (int row=0; row<insertedDiskArray.length; row++){
			for (int column=0; column<insertedDiskArray.length; column++){
				insertedDiskArray[row][column] = null;
			}
			isPlayerOneTurn = true;
			playerNameLabel.setText(PLAYER_ONE);
			createPlayground();
		}
	}
	private static class Disk extends Circle{
		private final boolean isPlayerOneMove;
		public Disk(boolean isPlayerOneMove) {
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER/2);
			setFill(isPlayerOneMove? Color.valueOf(discColor1): Color.valueOf(discColor2));
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}

	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
