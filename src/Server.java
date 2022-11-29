import java.io.BufferedReader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import javafx.application.Platform;

public class Server extends Application implements Runnable{
	
	TextField text ;
	Label lblPrint;
	Button btn;
	TextArea ta;
	
	@Override // Override the start method in the Application class
	  public void start(Stage primaryStage) throws IOException {
		  
	    // Panel p to hold the label and text field
	    BorderPane paneForTextField = new BorderPane();
	    paneForTextField.setPadding(new Insets(5, 5, 5, 5)); 
	    paneForTextField.setStyle("-fx-border-color: white");
	    //paneForTextField.setLeft(new Label("Enter a Number: "));
	    
	    BorderPane mainPane = new BorderPane();
	    // Text area to display contents
	    ta = new TextArea();
	    mainPane.setCenter(new ScrollPane(ta));
	    mainPane.setTop(paneForTextField);
	    
	  //Expand the textErea to fit the whole text
	    ta.textProperty().addListener((obs,old,niu)->{
            Text t = new Text(old+niu);
            t.setFont(ta.getFont());
            StackPane pane = new StackPane(t);
            pane.layout();
            double height = t.getLayoutBounds().getHeight();
            double padding = 25 ;
            ta.setMinHeight(height+padding);
        });
	    
	 // Create a scene and place it in the stage
	    Scene scene = new Scene(mainPane, 450, 450);
	    primaryStage.setTitle("Server"); // Set the stage title
	    primaryStage.setScene(scene); // Place the scene in the stage
	    primaryStage.show(); // Display the stage
	    
	    new Thread( () -> {
	    	try {
	    		Socket socket = null;
				boolean shutDown = false;
				InputStreamReader inputReader = null;
				OutputStreamWriter outputWriter = null;
				BufferedReader bufferedReader = null;
				BufferedWriter bufferedWriter = null;
				//String  msgFromClient = null;
				
				ServerSocket serverSocket = new ServerSocket(1234);
				 Platform.runLater(() ->
		          ta.appendText("Server started at " + new Date() + '\n'));
				
				while(!shutDown) {
					
					try {
						
						socket = serverSocket.accept();
						
						inputReader = new InputStreamReader(socket.getInputStream());
						outputWriter = new OutputStreamWriter(socket.getOutputStream());
						
						bufferedReader = new BufferedReader(inputReader);
						bufferedWriter = new BufferedWriter(outputWriter);

						//while(true) {
							
							String msgFromClient = bufferedReader.readLine();
							//System.out.println("hi");
							
							System.out.println("Client: " + msgFromClient);
							ta.appendText("Client: " + msgFromClient);
							
							Server TA = new Server();

							String content = Files.readString(Paths.get(msgFromClient));
							
							String newContent1 = Between(content, "START", "LICENSE"); // substring the text wanted
							
							String[] array = newContent1.split(" "); // split the string into words and put them in an array
							
					        Map<String, Integer> words = new HashMap<>(); // new hashmap
					        for (String str : array) { 					 // loop through the string array	
					            if (words.containsKey(str)) {			// check if the hashmap containes the str word
					                words.put(str, 1 + words.get(str));  //if the word exist increment it's frequency by 1
					            } else {
					                words.put(str, 1);					// if not add the new word to the hashmap and put its frequency to 1
					            }
					        }
					         
					        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
					        
					        reverseSortedMap = TA.reverseMapOrder(words); // 

					        //Print values
					        //reverseSortedMap.forEach((key, value)-> System.out.println(key + " : " + value));
							
//							int number = Integer.parseInt(msgFromClient);
//							String msg;
//					          if((number % 2) == 0) {
//					        	  msg = "prime";
//					          }
//					          else {
//					        	  msg = "not prime"; 
//					          }
							
							bufferedWriter.write(msgFromClient);
							bufferedWriter.newLine();
							bufferedWriter.flush();
							
							if(msgFromClient.equalsIgnoreCase("shutDown")) { //shutdown the client
								System.out.println("ShutDown.....");
								shutDown = true;
							}
							reverseSortedMap.forEach((key, value)-> ta.appendText('\n' + key + " : " + value));
							
							Platform.runLater(() -> {
								
					            //ta.appendText(number + " is " + msg + '\n'); 
					          });				      
							
						//}
						
						socket.close();
						//serverSocket.close();
						inputReader.close();
						outputWriter.close();
						bufferedReader.close();
						bufferedWriter.close();		
						
					} catch(IOException ex) {
						ex.printStackTrace();
					}
				}
	    	}
	    	catch(IOException ex) {
	    		ex.printStackTrace();
	    	}
	    		
	    }).start();
	       
	}

	public static void main(String[] args) throws IOException {
		launch(args);
		
	}
	
	/**
	 * reverseMapOrder is a function that takes a map as parameter, sort it, 
	 * and put just the first 20 words in a new map
	 * @param words is a map parameter
	 * @return returns a sorted map of 20 workds
	 */
	public LinkedHashMap<String, Integer> reverseMapOrder(Map<String, Integer> words){

        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
         
        //Use Comparator.reverseOrder() for reverse ordering
        words.entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .limit(20)
          .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
		
		return reverseSortedMap;	
	}
	
	
	//A function that substring a string between two strings.
	/**
	 * Between is a function that substratct a string from another string using start word and end word
	 * @param STR is a parameter represent the whole text
	 * @param FirstString is a parameter that represent the start word 
	 * @param LastString is a parameter that represent the end word
	 * @return a specific string
	 */
	public static String Between(String STR , String FirstString, String LastString)
    {       
           
        String FinalString = StringUtils.substringBetween(STR, FirstString, LastString);
        
        return FinalString;
    }

	@Override
	public void run() {
		try {
			start(new Stage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
