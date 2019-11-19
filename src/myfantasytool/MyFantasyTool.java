/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myfantasytool;

import de.codecentric.centerdevice.MenuToolkit;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 *
 * @author chenjk
 */
public class MyFantasyTool extends Application {
    static Stage mainstage=null;
    static boolean Item3Clicked=false;
    public static MyFantasyToolController MyFantasyToolController; 
    static public MenuItem LoadRankingFile = new MenuItem("Load ranking file");
    static public MenuItem SaveTierList = new MenuItem("Save TierList");
    static public MenuItem ReloadList = new MenuItem("Clear TierLists");
    static String appIcon="fantasyfootball.jpg";
     public static String CurrDir="";
     static boolean devMode=false ; 
    @Override
    public void start(Stage stage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("MyFantasyTool.fxml"));
        mainstage = new Stage();
        //mainstage.getIcons().add(new Image(getClass().getResourceAsStream(appIcon)));
        FXMLLoader rootwin_loader =new FXMLLoader(getClass().getResource("MyFantasyTool.fxml"));                 
        Parent root = (Parent)rootwin_loader.load();
        MyFantasyToolController=(MyFantasyToolController) rootwin_loader.getController();
       
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("MyFantasyTool.css");
        mainstage.setScene(scene);
        mainstage.setTitle("IBM Fantasy Football Tool");
        mainstage.show();
        if(devMode){
            System.out.println("dev mode");
        }
        else{                 
            if(System.getProperty("os.name").toLowerCase().contains("mac")){
                CurrDir= Paths.get(".").toAbsolutePath().normalize().toString();
                System.out.println("cur = "+CurrDir);
                CurrDir=CurrDir.substring(0,CurrDir.lastIndexOf("/",CurrDir.lastIndexOf("/",CurrDir.lastIndexOf("/")-1)-1)+1);  
                 System.out.println("cur = "+CurrDir);

            }
        }
		MenuToolkit tk = MenuToolkit.toolkit();

		MenuBar bar = new MenuBar();
                //----------------------------------------------
		MenuItem item1 = new MenuItem("Item1");
		MenuItem item2 = new MenuItem("Item2");
//		
//		
//
		MenuItem item4 = tk.createQuitMenuItem("my app");
//
		Menu menu2 = new Menu("Menu2");
		menu2.getItems().add(item2);
//		
                Menu menu1 = new Menu("Menu1");
		menu1.getItems().addAll( item4);
                //----------------------------------------------

		Menu MenuList = new Menu("File");
		MenuList.getItems().addAll(LoadRankingFile,SaveTierList,ReloadList);
                
            
                
		bar.getMenus().addAll(menu1, MenuList);

		tk.setMenuBar(mainstage, bar);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
