
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class Client extends Application implements Runnable {
	
	TextField text ;
	Label lblPrint;
	Button btn;
	TextArea ta;
	
	@Override // Override the start method in the Application class
	  public void start(Stage primaryStage) {
		  
	    // Panel p to hold the label and text field
	    BorderPane paneForTextField = new BorderPane();
	    paneForTextField.setPadding(new Insets(5, 5, 5, 5)); 
	    paneForTextField.setStyle("-fx-border-color: white");
	    paneForTextField.setLeft(new Label("File name: "));
	    
	    text = new TextField();
	    text.setAlignment(Pos.BOTTOM_RIGHT);
	    paneForTextField.setCenter(text);
	    
	    //button 
	    btn = new Button("Analyse");
	    paneForTextField.setBottom(btn);
	    
	    BorderPane mainPane = new BorderPane();
	    // Text area to display contents
	    ta = new TextArea();
	    mainPane.setCenter(new ScrollPane(ta));
	    mainPane.setTop(paneForTextField);
	    
	    //Create File chooser
	    FileChooser fileChooser = new FileChooser();

        Button selectFile = new Button("Select File");
        selectFile.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
            	text.setText(file.getPath());
            }
        });

        //VBox vBox = new VBox(button);
        paneForTextField.setRight(selectFile);
        
	    
	 // Create a scene and place it in the stage
	    Scene scene = new Scene(mainPane, 450, 200, Color.BEIGE);
	    primaryStage.setTitle("Client"); // Set the stage title
	    primaryStage.setScene(scene); // Place the scene in the stage
	    primaryStage.show(); // Display the stage
	    
	    btn.setOnAction(e -> {
	    	action();
	    	text.clear();
	    });;
	    
	}

//	@Override
//	public void actionPerformed(java.awt.event.ActionEvent e) {
//		if (e.getSource().equals(btn)) {
//	        action();
//	    }
//	}

	private void action() {
		Socket socket = null;
		InputStreamReader inputReader = null;
		OutputStreamWriter outputWriter = null;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		
		try {
			socket = new Socket("localhost", 1234);
			inputReader = new InputStreamReader(socket.getInputStream());
			outputWriter = new OutputStreamWriter(socket.getOutputStream());
			
			bufferedReader = new BufferedReader(inputReader);
			bufferedWriter = new BufferedWriter(outputWriter);

				String msgToSend = text.getText().trim();
				//System.out.println(msgToSend);
				ta.appendText("File is " + msgToSend + '\n');
				
				bufferedWriter.write(msgToSend); //send message to the server
				bufferedWriter.newLine();
				bufferedWriter.flush(); //Flush the buffer when it's full.
				
				String response = bufferedReader.readLine();
				
				System.out.println("Server: " + response); // Waite for response from the server
				ta.appendText(response + '\n');
				
				if(msgToSend.equalsIgnoreCase("bye")) {
					System.out.println("Bye! You are disconnected");
					socket.close();
				}
			
		} catch(IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(socket != null) {
					socket.close();
				}
				if(bufferedReader != null) {
					bufferedReader.close();
				}
				if(bufferedWriter != null) {
					bufferedWriter.close();
				}
				if(inputReader != null) {
					inputReader.close();
				}
				if(outputWriter != null) {
					outputWriter.close();
				}
				
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) throws IOException{
		launch(args);
	}

	@Override
	public void run() {
		start(new Stage());
		
	}


}

