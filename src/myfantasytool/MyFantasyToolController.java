/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myfantasytool;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import static myfantasytool.MyFantasyTool.CurrDir;
import static myfantasytool.MyFantasyTool.Item3Clicked;
import static myfantasytool.MyFantasyTool.LoadRankingFile;
import static myfantasytool.MyFantasyTool.ReloadList;
import static myfantasytool.MyFantasyTool.SaveTierList;
import static myfantasytool.MyFantasyTool.devMode;

/**
 *
 * @author chenjk
 */
public class MyFantasyToolController implements Initializable {
    
    @FXML
    private TableView<Playerlist> Tableview;
    @FXML
    private TableColumn Tableview_RANK;
    @FXML
    private TableColumn Tableview_Player;
    @FXML
    private TableColumn Tableview_POS;
    @FXML
    private TableColumn Tableview_TEAM;
    @FXML
    private TableColumn Tableview_BYE;
    
    Boolean startStatus=false;
    ObservableList<Playerlist> data = FXCollections.observableArrayList();
    ObservableList<String> DraftData = FXCollections.observableArrayList();
    ObservableList<Playerlist> CurrentTableviewData = FXCollections.observableArrayList();
    
    ObservableList<String> TierlistRBdata = FXCollections.observableArrayList();
    ObservableList<String> TierlistWRdata = FXCollections.observableArrayList();
    ObservableList<String> TierlistQBdata = FXCollections.observableArrayList();
    ObservableList<String> TierlistTEdata = FXCollections.observableArrayList();
    
    String Selectedplayer="";
    
    private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();
    @FXML
    private TableColumn Tableview_Status;
    @FXML
    private MenuButton Position;
    List<String> keeperlist = new ArrayList<>();
    @FXML
    private ListView<String> DraftListView;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn Tableview_Num;
    private String DisplayMode;
    @FXML
    private TextArea Textfield;
    @FXML
    private Button AddTierBTN;
    @FXML
    private ListView<String> TierListviewRB;
    @FXML
    private ListView<String> TierListviewWR;
    @FXML
    private ListView<String> TierListviewQB;
    @FXML
    private ListView<String> TierListviewTE;
    @FXML
    private Button RemoveTierBTN;
    static String RankingFileAdddress="";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {     
        if(!devMode){        
            if(System.getProperty("os.name").toLowerCase().contains("mac")){
                CurrDir= Paths.get(".").toAbsolutePath().normalize().toString();    
                System.out.println("cur = "+CurrDir);
                CurrDir=CurrDir.substring(0,CurrDir.lastIndexOf("/",CurrDir.lastIndexOf("/",CurrDir.lastIndexOf("/")-1)-1)+1);  
                 System.out.println("cur = "+CurrDir);                 
            }
        }
        SaveTierList.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {				
                            Item3Clicked=false;
                            saveRBTierList();
                            saveWRTierList();
                            saveQBTierList();
                            saveTETierList();                                    
                    }
            });
        ReloadList.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {				
                            Item3Clicked=false;
                            TierListviewRB.getItems().clear();
                            TierListviewWR.getItems().clear();
                            TierListviewQB.getItems().clear();
                            TierListviewTE.getItems().clear();
                            
                            MakeRBTierList();
                            MakeWRTierList();
                            MakeQBTierList();
                            MakeTETierList();                                    
                    }
            });
        LoadRankingFile.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {		
                          Stage stage = new Stage();
                          FileChooser fileChooser = new FileChooser();  
                          fileChooser.setTitle("Load csv File");
                          fileChooser.getExtensionFilters().addAll(
                                new FileChooser.ExtensionFilter("csv file", "*.csv")                                
                            );
                          RankingFileAdddress = fileChooser.showOpenDialog(stage).toString();
                          if(RankingFileAdddress.equals("")){
                              AlertWin("Load csv file");                              
                          }
                          SaveRankingFileAddress();
                    }
            });
        DisplayMode="All";
        setAllposition();
        System.out.println("You clicked me!");
        try {
            readKeepersList();
        } catch (Exception e) {
        }
        LoadRankingFileAddress();              

//        Tableview.getStyleClass().add("Tableview_Player_Drafted");
        
//        Tableview_Player.setCellFactory(column -> {
//            return new TableCell<Playerlist, String>() {
//            int i=0;
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                getStyleClass().remove("Tableview_Player_Drafted");
//                Hyperlink myHyperlink = new Hyperlink();
//                if (item != null){
//                    myHyperlink = new Hyperlink();
//                    myHyperlink.setText(item);
//                    
//                    myHyperlink.setOnAction(e -> {
//                        if(Desktop.isDesktopSupported())
//                        {
//                            try {
//                                Desktop.getDesktop().browse(new URI("https://www.fantasypros.com/nfl/players/"));
//                            } catch (IOException e1) {
//                                e1.printStackTrace();
//                            } catch (URISyntaxException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    });
////                    setGraphic(empty ? null : myHyperlink);
//                    }
//               if (item == null || empty) {
//                    setText(null);
//                    setGraphic(null);
//                    //setStyle("");
//                } else {
//                 //                      
//                    setGraphic(myHyperlink);              
//                    for (Iterator<String> i = keeperlist.iterator(); i.hasNext();) {
//                        String keepername = i.next();
//                       
//                        Pattern pattern = Pattern.compile(keepername);
//                        Matcher matcher = pattern.matcher(item);
//                        if (matcher.find()) {
//                           //setStyle("-fx-background-color: red");
//                           getStyleClass().add("Tableview_Player_Drafted");
//                        }      
//                    }
//                }
//            }
//            };                
//        });
//                         
    } 
    private  void LoadRankingFileAddress(){
        String File = "Default.file";
        try(BufferedReader br = new BufferedReader(new FileReader(File))) {
            String line = "";  
            if ((line = br.readLine()) != null) {                
                RankingFileAdddress=line;
            }                        
        }
        catch (Exception e) {
        }
        
    }
    private void DisplayTableList(){
        // Tableview.getColumns().addAll(Tableview_RANK,Tableview_Player,Tableview_POS,Tableview_TEAM,Tableview_BYE);
        
        //Step 4: add data inside table
        Tableview_RANK.setCellValueFactory(new PropertyValueFactory<Playerlist,Integer>("Rank"));    

        Tableview_Player.setCellValueFactory(new PropertyValueFactory<Playerlist,String>("Player"));

        Tableview_POS.setCellValueFactory(new PropertyValueFactory<Playerlist,String>("POS"));

        Tableview_TEAM.setCellValueFactory(new PropertyValueFactory<Playerlist,String>("Team"));

        Tableview_BYE.setCellValueFactory(new PropertyValueFactory<Playerlist,String>("Bye"));

        Tableview_Status.setCellValueFactory(new PropertyValueFactory<Playerlist,String>("Status"));

        Tableview_Num.setCellValueFactory(new PropertyValueFactory<Playerlist,Integer>("LineNum"));
            
        Tableview.setItems(data);
        CurrentTableviewData = FXCollections.observableArrayList(data);
        
        DraftListView.setCellFactory(new Callback<ListView<String>, 
                           ListCell<String>>() {
                               @Override 
                               public ListCell<String> call(ListView<String> stringListView) {
                                   return new ColorDraftList();
                               }
                           }
                        );
        Readmynotes();
        
        
    //ListView<String> list = new ListView<String>( FXCollections.observableArrayList( "Single", "Double", "Suite", "Family App" ) );

    DraftListView.setCellFactory( new Callback<ListView<String>, ListCell<String>>()
    {
        @Override
        public ListCell<String> call( ListView<String> param )
        {
            ListCell<String> listCell = new ListCell<String>()
            {
                @Override
                protected void updateItem( String item, boolean empty )
                {
                    super.updateItem( item, empty );
                    setText( item );                                       
                    getStyleClass().remove("WR_Player_Color");
                    getStyleClass().remove("RB_Player_Color");
                    getStyleClass().remove("TE_Player_Color");
                    getStyleClass().remove("QB_Player_Color");
                    getStyleClass().remove("DST_Player_Color");
                    getStyleClass().remove("K_Player_Color");
                 try{                       
                    if (empty) {                       
                        setText(null);
                        setGraphic(null);
                       } else {                            
                            setText(item);
                            Pattern pattern = Pattern.compile("^WR");
                            Matcher matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("WR_Player_Color");
                            }
                            pattern = Pattern.compile("^RB");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("RB_Player_Color");
                            }     
                            pattern = Pattern.compile("^TE");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("TE_Player_Color");
                            }    
                            pattern = Pattern.compile("^QB");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("QB_Player_Color");
                            }  
                            pattern = Pattern.compile("^DST");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("DST_Player_Color");
                            }  
                            pattern = Pattern.compile("^K");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("K_Player_Color");
                            }  
                       }
                    }catch(Exception e){

                    } 
                }
            };
            
            
            listCell.setOnDragDetected( ( MouseEvent event ) ->
            {
                String newaddString="";
                System.out.println( "listcell setOnDragDetected" );
                Dragboard db = listCell.startDragAndDrop( TransferMode.MOVE );
                ClipboardContent content = new ClipboardContent();
                
                content.putString( listCell.getItem() );
                db.setContent( content );
                event.consume();           
                dragSource.set(listCell);
                 
            } );
            
            listCell.setOnDragOver( ( DragEvent event ) ->
            {
                Dragboard db = event.getDragboard();
                if ( db.hasString() )
                {
                    event.acceptTransferModes( TransferMode.MOVE );
                    
                }
                event.consume();
            } );
            
            // listCell.setOnDragDone(event -> DraftListView.getItems().remove(listCell.getItem()));
             
             listCell.setOnDragDropped(event -> {
                   Dragboard db = event.getDragboard();
                   //if (db.hasString() && dragSource.get() != null) {
                    if (db.hasString() && listCell.getItem()!= null) {
                       // in this example you could just do
                       //DraftListView.getItems().add(db.getString());
                       // but more generally:

//                       ListCell<String> dragSourceCell = dragSource.get();
//                       DraftListView.getItems().add(dragSourceCell.getItem());
//                       event.setDropCompleted(true);
//                       dragSource.set(null);
                        String MovesString=db.getString();
                        System.out.println("remove = "+ MovesString);
                        String DroptorowitemString=listCell.getItem();
                        System.out.println("target:"+ DroptorowitemString);
               
                        //DraftData.remove(MovesString);
                        ObservableList<String> NewDraftData = FXCollections.observableArrayList();
                        NewDraftData.clear();
                        NewDraftData=null;                  
                        NewDraftData= FXCollections.observableArrayList();

                          for (Iterator<String> i = DraftData.iterator(); i.hasNext();) {
                              String Listnext = i.next();
                              System.out.println("draftdata= "+Listnext);
                            //  if (!Listnext.equals(db.getString())){
                                    //NewDraftData.add(Listnext);
                              //}
                                 if(Listnext.equals(MovesString)){
                                    NewDraftData.add(DroptorowitemString);
                                 }
                                 else if (Listnext.equals(DroptorowitemString)){
                                    NewDraftData.add(MovesString);
                                  //NewDraftData.add(Listnext);
                                    System.out.println("add = "+ MovesString);
                                }else{
                                    NewDraftData.add(Listnext);
                                }
                             }
                          if(DroptorowitemString==null){
                              NewDraftData.add(MovesString);
                          }
                          DraftListView.getItems().clear();
                          DraftData=null;                  
                          DraftData= FXCollections.observableArrayList();
                          DraftData=FXCollections.observableArrayList(NewDraftData);
                         DraftListView.setItems(DraftData);
                        System.err.println(DraftListView.getItems());
                   } 
                    else if ( db.hasString() && listCell.getItem() == null){
                        String MovesString=db.getString();
                        System.out.println("remove = "+ MovesString);
                        String DroptorowitemString=listCell.getItem();
                        System.out.println("target:"+ DroptorowitemString);
                         ListCell<String> dragSourceCell = dragSource.get();
                         DraftListView.getItems().add(dragSourceCell.getItem());
                         DraftListView.getItems().remove(MovesString);
                         
                         //DraftData.remove(MovesString);
                          ObservableList<String> NewDraftData = FXCollections.observableArrayList();
                          NewDraftData.clear();
                          NewDraftData=null;                  
                          NewDraftData= FXCollections.observableArrayList();
                          
                        System.err.println(DraftListView.getItems());
                    }
                   else {
                       event.setDropCompleted(false);
                   }
               });
            return listCell;
            }
        } );
        
    }
    private void LoadRakingFile(){
        CSVParser CSVParser= new CSVParser();
        try {
            data=CSVParser.ExtractCSV("");
        } catch (Exception e) {
            System.err.print(e);
        }
    }
    
    boolean TierlistEn=false;
    @FXML
    private void Draft_BTN(MouseEvent event) {
        try {               
            Playerlist Playerlst = Tableview.getSelectionModel().getSelectedItem();        
            String Selectedplayer = Playerlst.getPlayer();
            String tempDraftdata=Selectedplayer.replaceAll(" -DR", "");
                if(DraftData.isEmpty()){
                    String Pos=Playerlst.getPOS();
                    String Bye=" - "+Playerlst.getBye();;
                        String tempPos="";
                        if (Pos.contains("RB")){
                            tempPos = "RB - ";                
                        }else if(Pos.contains("WR")){
                            tempPos = "WR - ";
                        }else if(Pos.contains("QB")){
                            tempPos = "QB - ";
                        }else if(Pos.contains("TE")){
                            tempPos = "TE - ";
                        }else if(Pos.contains("K")){
                            tempPos = "K  - ";
                        }else if(Pos.contains("DST")){
                            tempPos = "DST- ";
                        }

                        DraftData.add(tempPos+tempDraftdata+Bye);   
                }              
            Boolean Exist=false;
            for (Iterator<String> i = DraftData.iterator(); i.hasNext();) {        
                String DraftDataeach=i.next();
                Pattern pattern = Pattern.compile(tempDraftdata);
                Matcher matcher = pattern.matcher(DraftDataeach);
                if (matcher.find()) {  
                    Exist =true;                              
                }
            }
            if (Exist==false){
                String Pos=Playerlst.getPOS();
                String Bye=" - "+Playerlst.getBye();;
                    String tempPos="";
                    if (Pos.contains("RB")){
                        tempPos = "RB - ";                
                    }else if(Pos.contains("WR")){
                        tempPos = "WR - ";
                    }else if(Pos.contains("QB")){
                        tempPos = "QB - ";
                    }else if(Pos.contains("TE")){
                        tempPos = "TE - ";
                    }else if(Pos.contains("K")){
                        tempPos = "K  - ";
                    }else if(Pos.contains("DST")){
                        tempPos = "DST- ";
                    }

                    DraftData.add(tempPos+tempDraftdata+Bye);  
            }
            DraftListView.setItems(DraftData);
        
         } catch (Exception e) {
        }
    }
    
    @FXML
    private void Undraft_BTN(MouseEvent event) {
        String Line=DraftListView.getSelectionModel().getSelectedItem();
        DraftData.remove(Line);
        
            
    }
    String TierlistSelectedline="";
    String TierModeSelect="";
    private void FromTiertolist(String PlayerName){
         TierlistSelectedline=PlayerName;
         System.out.println(DisplayMode + "  " + TierModeSelect);
         if (DisplayMode.equals("ALL") || DisplayMode.equals("QB") && TierModeSelect.equals("QB") ||
             DisplayMode.equals("WR")&&TierModeSelect.equals("WR") || DisplayMode.equals("RB") && TierModeSelect.equals("RB")||
             DisplayMode.equals("TE") && TierModeSelect.equals("TE") || DisplayMode.equals("RBWR")&&TierModeSelect.equals("RB") ||
             DisplayMode.equals("RBWR") && TierModeSelect.equals("WR")
            ){
            for (Iterator<Playerlist> i = CurrentTableviewData.iterator(); i.hasNext();) {
                   Playerlist playerLine = i.next();

                   if (playerLine.getPlayer().contains(PlayerName)){
                       int FromTiertolistlineNum= playerLine.getLineNum();
                       System.out.println("scroll");
                       Tableview.scrollTo(FromTiertolistlineNum-3);
                       Tableview.getSelectionModel().select(FromTiertolistlineNum-1, Tableview_Player);                
                       Tableview.getFocusModel().focus(0);
                   }
            }
         }
    }
    int curSelected=0;
    int newSelected=0;
    private void TierlistPlayerDrafted(String PlayerName){
        
        Pattern pattern = Pattern.compile(" -DR");
        Matcher matcher = pattern.matcher(PlayerName);
        PlayerName=PlayerName.replaceAll(" -DR", "");
        if (!PlayerName.equals("")){
            
            ObservableList<String> NewTierlistRBdata = FXCollections.observableArrayList();
            for (Iterator<String> i = TierlistRBdata.iterator(); i.hasNext();) {
                String playerLine = i.next();
                
                if (playerLine.contains(PlayerName)){
                    if (playerLine.contains(" -DR")){
                        NewTierlistRBdata.add(PlayerName);
                    }else{
                        NewTierlistRBdata.add(playerLine+" -DR");
                    }
                }else{
                    NewTierlistRBdata.add(playerLine);
                }
            }
            TierlistRBdata=FXCollections.observableArrayList(NewTierlistRBdata);
            TierListviewRB.setItems(TierlistRBdata);
        }
        setAvailableList();
    }
    
    private void TierlistPlayerDraftedWR(String PlayerName){
        
        Pattern pattern = Pattern.compile(" -DR");
        Matcher matcher = pattern.matcher(PlayerName);
        PlayerName=PlayerName.replaceAll(" -DR", "");
        if (!PlayerName.equals("")){            
            ObservableList<String> NewTierlistdataTEMP = FXCollections.observableArrayList();
            for (Iterator<String> i = TierlistWRdata.iterator(); i.hasNext();) {
                String playerLine = i.next();
                
                if (playerLine.contains(PlayerName)){
                    if (playerLine.contains(" -DR")){
                        NewTierlistdataTEMP.add(PlayerName);
                    }else{
                        NewTierlistdataTEMP.add(playerLine+" -DR");
                    }
                }else{
                    NewTierlistdataTEMP.add(playerLine);
                }
            }
            TierlistWRdata=FXCollections.observableArrayList(NewTierlistdataTEMP);
            TierListviewWR.setItems(TierlistWRdata);
        }
        setAvailableList();
    }
    private void TierlistPlayerDraftedQB(String PlayerName){
        
        Pattern pattern = Pattern.compile(" -DR");
        Matcher matcher = pattern.matcher(PlayerName);
        PlayerName=PlayerName.replaceAll(" -DR", "");
        if (!PlayerName.equals("")){            
            ObservableList<String> NewTierlistdataTEMP = FXCollections.observableArrayList();
            for (Iterator<String> i = TierlistQBdata.iterator(); i.hasNext();) {
                String playerLine = i.next();
                
                if (playerLine.contains(PlayerName)){
                    if (playerLine.contains(" -DR")){
                        NewTierlistdataTEMP.add(PlayerName);
                    }else{
                        NewTierlistdataTEMP.add(playerLine+" -DR");
                    }
                }else{
                    NewTierlistdataTEMP.add(playerLine);
                }
            }
            TierlistQBdata=FXCollections.observableArrayList(NewTierlistdataTEMP);
            TierListviewQB.setItems(TierlistQBdata);
        }
        setAvailableList();
    }
    
    private void TierlistPlayerDraftedTE(String PlayerName){
        
        Pattern pattern = Pattern.compile(" -DR");
        Matcher matcher = pattern.matcher(PlayerName);
        PlayerName=PlayerName.replaceAll(" -DR", "");
        if (!PlayerName.equals("")){            
            ObservableList<String> NewTierlistdataTEMP = FXCollections.observableArrayList();
            for (Iterator<String> i = TierlistTEdata.iterator(); i.hasNext();) {
                String playerLine = i.next();
                
                if (playerLine.contains(PlayerName)){
                    if (playerLine.contains(" -DR")){
                        NewTierlistdataTEMP.add(PlayerName);
                    }else{
                        NewTierlistdataTEMP.add(playerLine+" -DR");
                    }
                }else{
                    NewTierlistdataTEMP.add(playerLine);
                }
            }
            TierlistTEdata=FXCollections.observableArrayList(NewTierlistdataTEMP);
            TierListviewTE.setItems(TierlistTEdata);
        }
        setAvailableList();
    }
    private void TablelistPlayerDrafted(String PlayerName){
            Playerlist Playerlst = Tableview.getSelectionModel().getSelectedItem();
            Pattern pattern = Pattern.compile(" -DR");
            Matcher matcher = pattern.matcher(PlayerName);
            PlayerName=PlayerName.replaceAll(" -DR", "");
//            if (matcher.find()) {
//                Playerlst.setPlayer(PlayerName.replaceAll(" -DR", ""));
//            }
//            else{
//                Playerlst.setPlayer(PlayerName+" -DR");               
//            }
//            
//            if (Playerlst.getStatus().equals("Drafted")){
//                Playerlst.setStatus(" ");
//            }else if(Playerlst.getStatus().equals("Keeper")){
//                
//            }
//            else{
//                Playerlst.setStatus("Drafted");                
//            }
//       
            ObservableList<Playerlist> newdata = FXCollections.observableArrayList();
            for (Iterator<Playerlist> i = data.iterator(); i.hasNext();) {
                Playerlist playerLine = i.next();
                if (playerLine.getPlayer().contains(PlayerName)){
                    if (playerLine.getPlayer().contains(" -DR")){
                        playerLine.setPlayer(PlayerName);
                        playerLine.setStatus("");
                        newdata.add(playerLine);
                    }else{
                        playerLine.setPlayer(PlayerName+" -DR");
                        playerLine.setStatus("Drafted");
                        newdata.add(playerLine);                        
                    }                   
                }else{
                    newdata.add(playerLine);
                }
            }
            
            data=FXCollections.observableArrayList(newdata);
            Tableview.setItems(data);
            TableviewPlaercallback();
            setAvailableList();
    }
    @FXML
    private void PlayerListClick(MouseEvent event) throws Exception,IOException,NullPointerException {
        try {
            
        TierlistEn=false;
        
        if (event.getClickCount()<2){
             Playerlist Playerlst = Tableview.getSelectionModel().getSelectedItem();            
             Selectedplayer = Playerlst.getPlayer();
//            final Clipboard clipboard = Clipboard.getSystemClipboard();
//            final ClipboardContent content = new ClipboardContent();
//            Playerlist Playerlst = Tableview.getSelectionModel().getSelectedItem();
//            content.putString(Playerlst.getPlayer());
//            clipboard.setContent(content);
        }

        if(event.getClickCount()>=2){
            System.out.println(">> Listing view is clicked <<");
            // listing_line_selected
            curSelected=Tableview.getSelectionModel().getSelectedIndex();
            System.out.println(curSelected);
            
            Tableview.getSelectionModel().setCellSelectionEnabled(true);
           
            Playerlist Playerlst = Tableview.getSelectionModel().getSelectedItem();
            System.out.println();    
            Selectedplayer = Playerlst.getPlayer();
            
            TierlistPlayerDrafted(Selectedplayer);
            TierlistPlayerDraftedWR(Selectedplayer);
            TierlistPlayerDraftedQB(Selectedplayer);
            TierlistPlayerDraftedTE(Selectedplayer);
            
            TablelistPlayerDrafted(Selectedplayer);
            //setup the status 
            
           
//            try {                       
//                Tableview.layout(); 
//                Tableview.setItems(FXCollections.observableList(data)); 
//                } catch (NullPointerException e) {
//            }
            //Tableview.getSelectionModel().select(curSelected);
        //Drafted
        
        
            if (posAvailable==true){
                removeDraftedPlayer();
            }
        }
        
        } catch (Exception e) {
        }
        
    }
private void TableviewPlaercallback(){
    Tableview.getFocusModel().focus(0);
    Tableview_Player.setCellFactory(column -> {
            return new TableCell<Playerlist, String>() {           
            @Override
            protected void updateItem(String item, boolean empty) {
                Hyperlink myHyperlink = new Hyperlink();
                
                try {                     
                    super.updateItem(item, empty);                                                
                    
                    if (item != null){
                        String tempitem=item.replaceAll("\\.", "");
                        tempitem=tempitem.replaceAll("\\'", "");
                        String [] name=tempitem.split(" ");
                        myHyperlink = new Hyperlink();
                        myHyperlink.setText(item);
                                                                        
                        myHyperlink.setOnAction(e -> {
                            if(Desktop.isDesktopSupported())
                            {
                                System.out.println("len "+name.length);
                                try {                             
                                    if(item.contains("David Johnson")){
                                        Desktop.getDesktop().browse(new URI("https://www.fantasypros.com/nfl/players/"+name[0].toLowerCase()+"-"+name[1].toLowerCase()+"-rb"+".php"));
                                    }
                                    else if(item.contains("Odell Beckham")){
                                        Desktop.getDesktop().browse(new URI("https://www.fantasypros.com/nfl/players/"+name[0].toLowerCase()+"-"+name[1].toLowerCase()+".php"));
                                    }
                                    else if (name.length >=3){
                                        String nameSuffix="";
                                        if(!name[2].toLowerCase().contains("-dr")){
                                            nameSuffix="-"+name[2].toLowerCase();
                                        }
                                        Desktop.getDesktop().browse(new URI("https://www.fantasypros.com/nfl/players/"+name[0].toLowerCase()+"-"+name[1].toLowerCase()+nameSuffix+".php"));
                                    }
                                    else{
                                        Desktop.getDesktop().browse(new URI("https://www.fantasypros.com/nfl/players/"+name[0].toLowerCase()+"-"+name[1].toLowerCase()+".php"));
                                    }

                                    final Clipboard clipboard = Clipboard.getSystemClipboard();
                                    final ClipboardContent content = new ClipboardContent();
                                    String cliptext = item.replaceAll(" -DR", "");
                                    content.putString(cliptext);
                                    clipboard.setContent(content);

                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                } catch (URISyntaxException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });

                    }
                    //setGraphic(empty ? null : myHyperlink);
                   
                
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                    //setStyle("");                    
                } else {
                 //        
                    setGraphic(myHyperlink);
                    getStyleClass().remove("Tableview_Player_Drafted");
                    getStyleClass().remove("Table_Listview_Player_Find");
                    Pattern pattern = Pattern.compile(" -DR");
                    Matcher matcher = pattern.matcher(item);
                    if (matcher.find()) {
                       //setStyle("-fx-background-color: red");
                       getStyleClass().add("Tableview_Player_Drafted");
                    }   
                    pattern = Pattern.compile(TierlistSelectedline);
                    matcher = pattern.matcher(item);
                    if (matcher.find()) {
                       //setStyle("-fx-background-color: red");
                       getStyleClass().add("Table_Listview_Player_Find");
                    }  
                    
                }
                 } catch (Exception e) {
                }
            }
            };                
        });
    
}
private void readKeepersList()throws Exception{
    String file = CurrDir+"Keepers";
      
    try(BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line = "";
        while ((line = br.readLine()) != null) {                           
            keeperlist.add(line);
        }
    } catch (FileNotFoundException e) {
      //Some error logging
        System.out.println(e);
    }
}
private void OpenPlayerProfile(String text, String link){
    Hyperlink myHyperlink = new Hyperlink();
    myHyperlink.setText(text);

    myHyperlink.setOnAction(e -> {
        if(Desktop.isDesktopSupported())
        {
            try {
                Desktop.getDesktop().browse(new URI("http://www.google.com"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    });
}
    @FXML
    private void setAllpos(ActionEvent event) {
        setAllposition();
    }    
    private void setAllposition() {        
        Position.setText("All");
        DisplayMode="ALL";
        data = updateNumLine(data);
        Tableview.setItems(data);
        CurrentTableviewData = FXCollections.observableArrayList(data);
        if(posAvailable==true){
            removeDraftedPlayer();
        }
        try {
                findCurrentPlayerLine();
             //   Tableview.scrollTo(newSelected-3);
                Tableview.getSelectionModel().select(newSelected, Tableview_Player);                
            } catch (Exception e) {
            }
    }
    @FXML
    private void setRBpos(ActionEvent event) {
        setRBposition();
    }
    ObservableList<Playerlist> dataRB = FXCollections.observableArrayList();
    private void setRBposition() {
        Position.setText("RB");
        DisplayMode="RB";
         dataRB = FXCollections.observableArrayList(data);
       for (Iterator<Playerlist> i = dataRB.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if (!playerLine.POS.contains("RB")) i.remove();
        }
        dataRB = updateNumLine(dataRB);
        Tableview.setItems(dataRB);
        CurrentTableviewData = FXCollections.observableArrayList(dataRB);
         if(posAvailable==true){
            removeDraftedPlayer();
        }
    }
    
    @FXML
    private void setposQB(ActionEvent event) {
        setpositionQB();
    }
    private void setpositionQB() {
        Position.setText("QB");
        DisplayMode="QB";
        ObservableList<Playerlist> dataQB = FXCollections.observableArrayList(data);
        for (Playerlist a:dataQB ){
           if (!a.getPOS().contains("QB")){
               //a.remove();
           }
        }
       
       for (Iterator<Playerlist> i = dataQB.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if (!playerLine.POS.contains("QB")) i.remove();
        }
        dataQB = updateNumLine(dataQB);
        Tableview.setItems(dataQB);        
        if(posAvailable==true){
            removeDraftedPlayer();
        }
    }

    @FXML
    private void setposWR(ActionEvent event) {
        setpositionWR();
    }
    private void setpositionWR() {
        Position.setText("WR");
        DisplayMode="WR";
        ObservableList<Playerlist> dataWR = FXCollections.observableArrayList(data);
        for (Iterator<Playerlist> i = dataWR.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if (!playerLine.POS.contains("WR")) i.remove();
        }
        dataWR = updateNumLine(dataWR);
        Tableview.setItems(dataWR);
        if(posAvailable==true){
            removeDraftedPlayer();
        }
    }

    @FXML
    private void setposTE(ActionEvent event) {
        setpositionTE();
    }
    private void setpositionTE() {
        Position.setText("TE");
        DisplayMode="TE";
        ObservableList<Playerlist> dataTE = FXCollections.observableArrayList(data);
        for (Iterator<Playerlist> i = dataTE.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if (!playerLine.POS.contains("TE")) i.remove();
        }
        dataTE = updateNumLine(dataTE);
        Tableview.setItems(dataTE);
        if(posAvailable==true){
            removeDraftedPlayer();
        }
    }
    @FXML
    private void setposDEF(ActionEvent event) {
        setpositionDEF();
    }
    private void setpositionDEF() {
        Position.setText("DST");
        DisplayMode="DST";
        ObservableList<Playerlist> dataDST = FXCollections.observableArrayList(data);
        
        for (Iterator<Playerlist> i = dataDST.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if (!playerLine.POS.contains("DST")) i.remove();
        }
        dataDST = updateNumLine(dataDST);
        Tableview.setItems(dataDST);
        if(posAvailable==true){
            removeDraftedPlayer();
        }
    }

    @FXML
    private void setposKicker(ActionEvent event) {
        setpositionKicker();
    }
    private void setpositionKicker() {
        Position.setText("K");
        DisplayMode="K";
        ObservableList<Playerlist> dataK = FXCollections.observableArrayList(data);
        for (Iterator<Playerlist> i = dataK.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if (!playerLine.POS.contains("K")) i.remove();
        }
        dataK = updateNumLine(dataK);
        if(posAvailable){
            removeDraftedPlayer();
        }
        Tableview.setItems(dataK);
        if(posAvailable==true){
            removeDraftedPlayer();
        }
    }
    @FXML
    private void setposRBWR(ActionEvent event) {
        setpositionRBWR();
    }
    public void saveRBTierList(){
        String file = CurrDir+"TierListFileRB";               
        try {
            FileWriter writer = new FileWriter(file);
            
            for (Iterator<String> i = TierlistRBdata.iterator(); i.hasNext();) {
                String playerLine = i.next();
                writer.write(playerLine+"\n");
            }
            
            writer.close(); 
        } catch (Exception e) {
        }
    }
    public void saveWRTierList(){
        String file = CurrDir+"TierListFileWR";               
        try {
            FileWriter writer = new FileWriter(file);
            for (Iterator<String> i = TierlistWRdata.iterator(); i.hasNext();) {
                String playerLine = i.next();
                writer.write(playerLine+"\n");
            }
            
            writer.close(); 
        } catch (Exception e) {
        }
    }
    public void saveQBTierList(){
        String file = CurrDir+"TierListFileQB";               
        try {
            FileWriter writer = new FileWriter(file);
            for (Iterator<String> i = TierlistQBdata.iterator(); i.hasNext();) {
                String playerLine = i.next();
                writer.write(playerLine+"\n");
            }
            
            writer.close(); 
        } catch (Exception e) {
        }
    }
    public void saveTETierList(){
        String file = CurrDir+"TierListFileTE";               
        try {
            FileWriter writer = new FileWriter(file);
            for (Iterator<String> i = TierlistTEdata.iterator(); i.hasNext();) {
                String playerLine = i.next();
                writer.write(playerLine+"\n");
            }
            
            writer.close(); 
        } catch (Exception e) {
        }
    }
    public void SaveRankingFileAddress(){
        String File = "Default.file";
         try {
            FileWriter writer = new FileWriter(File);           
            writer.write(RankingFileAdddress);            
            
            writer.close(); 
        } catch (Exception e) {
        }
    }
    public void loadRBTierList(){
        String file = CurrDir+"TierListFileRB";              
        ObservableList<String> newTierlistRBdata = FXCollections.observableArrayList();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";  
            while ((line = br.readLine()) != null) {                
                newTierlistRBdata.add(line);
                
            }            
            TierlistRBdata=FXCollections.observableArrayList(newTierlistRBdata);
            TierListviewRB.setItems(TierlistRBdata);
        }
        catch (Exception e) {
        }
    }
    public void loadWRTierList(){
        String file = CurrDir+"TierListFileWR";              
        ObservableList<String> newTierlistWRdata = FXCollections.observableArrayList();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";  
            while ((line = br.readLine()) != null) {                
                newTierlistWRdata.add(line);
                
            }            
            TierlistWRdata=FXCollections.observableArrayList(newTierlistWRdata);
            TierListviewWR.setItems(TierlistWRdata);
        }
        catch (Exception e) {
        }
    }
    public void loadQBTierList(){
        String file = CurrDir+"TierListFileQB";              
        ObservableList<String> newTierlistQBdata = FXCollections.observableArrayList();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";  
            while ((line = br.readLine()) != null) {                
                newTierlistQBdata.add(line);
                
            }            
            TierlistQBdata=FXCollections.observableArrayList(newTierlistQBdata);
            TierListviewQB.setItems(TierlistQBdata);
        }
        catch (Exception e) {
        }
    }
    public void loadTETierList(){
        String file = CurrDir+"TierListFileTE";              
        ObservableList<String> newTierlistTEdata = FXCollections.observableArrayList();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";  
            while ((line = br.readLine()) != null) {                
                newTierlistTEdata.add(line);
                
            }            
            TierlistTEdata=FXCollections.observableArrayList(newTierlistTEdata);
            TierListviewTE.setItems(TierlistTEdata);
        }
        catch (Exception e) {
        }
    }
    
    private void setpositionRBWR() {
        Position.setText("RB/WR");
        DisplayMode="RBWR";
        ObservableList<Playerlist> dataRBWR = FXCollections.observableArrayList(data);
       
        for (Iterator<Playerlist> i = dataRBWR.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if ((!playerLine.POS.contains("RB")) && (!playerLine.POS.contains("WR") )) i.remove();
        }
        dataRBWR = updateNumLine(dataRBWR);
        Tableview.setItems(dataRBWR);
        if(posAvailable==true){
            removeDraftedPlayer();
        }
    }
    Boolean posAvailable=false;
    @FXML
    private void Available_BTN(MouseEvent event) {
        posAvailable=!posAvailable;      
        setAvailableList();
        
    }
    private  void removeDraftedTierList(){
//        ObservableList<String> dataAvail = FXCollections.observableArrayList(TierlistRBdata);
//        for (Iterator<String> i = dataAvail.iterator(); i.hasNext();) {
//               String playerLine = i.next();
//               if (playerLine.contains(" -DR")) i.remove();
//           }      
//        TierListviewRB.setItems(dataAvail);
        TierListviewRB.setItems(removeDraftedTierListEach(TierlistRBdata));
        TierListviewWR.setItems(removeDraftedTierListEach(TierlistWRdata));
        TierListviewQB.setItems(removeDraftedTierListEach(TierlistQBdata));
        TierListviewTE.setItems(removeDraftedTierListEach(TierlistTEdata));
    }
    
    private  ObservableList<String> removeDraftedTierListEach(ObservableList<String> Tierlistdatacolumn){
        ObservableList<String> dataAvail = FXCollections.observableArrayList(Tierlistdatacolumn);
        for (Iterator<String> i = dataAvail.iterator(); i.hasNext();) {
               String playerLine = i.next();
               if (playerLine.contains(" -DR")) i.remove();
           }      
        return dataAvail;
    }        
    private void setALLTierList() {       
        TierListviewRB.setItems(TierlistRBdata);
        TierListviewWR.setItems(TierlistWRdata);
        TierListviewQB.setItems(TierlistQBdata);
        TierListviewTE.setItems(TierlistTEdata);
       
    }
    private void setAvailableList() {
        
        if (posAvailable==true) {            
           removeDraftedPlayer();   
           removeDraftedTierList();
           
        }else{
            
            setALLTierList();
            if(DisplayMode.equals("RB")){
                setRBposition();
            }
            else if(DisplayMode.equals("WR")){
                setpositionWR();
            }
            else if(DisplayMode.equals("QB")){
                setpositionQB();
            }
            else if(DisplayMode.equals("WR")){
                
            }
            else if(DisplayMode.equals("RBWR")){
                setpositionRBWR();
            }
            else if(DisplayMode.equals("TE")){
                setpositionTE();
            }
            else if(DisplayMode.equals("K")){
                setpositionKicker();
            }
            else if(DisplayMode.equals("DST")){
                setpositionDEF();
            }
            else if(DisplayMode.equals("ALL")){
                setAllposition();
            }
        }
            
    }
    
    
    private void removeDraftedPlayer(){
        
        ObservableList<Playerlist> dataAvail = FXCollections.observableArrayList(data);
        
        if (DisplayMode.equals("RB")) {                  
            for (Iterator<Playerlist> i = dataAvail.iterator(); i.hasNext();) {
                Playerlist playerLine = i.next();
                if (!playerLine.POS.contains("RB")) i.remove();
            }
            dataAvail = updateNumLine(dataAvail);
            Tableview.setItems(dataAvail);
        }
        else if (DisplayMode.equals("WR")) {                  
            for (Iterator<Playerlist> i = dataAvail.iterator(); i.hasNext();) {
                Playerlist playerLine = i.next();
                if (!playerLine.POS.contains("WR")) i.remove();
            }
            dataAvail = updateNumLine(dataAvail);
            Tableview.setItems(dataAvail);
        }
        else if (DisplayMode.equals("QB")) {                  
            for (Iterator<Playerlist> i = dataAvail.iterator(); i.hasNext();) {
                Playerlist playerLine = i.next();
                if (!playerLine.POS.contains("QB")) i.remove();
            }
            dataAvail = updateNumLine(dataAvail);
            Tableview.setItems(dataAvail);
        }
        else if (DisplayMode.equals("TE")) {                  
            for (Iterator<Playerlist> i = dataAvail.iterator(); i.hasNext();) {
                Playerlist playerLine = i.next();
                if (!playerLine.POS.contains("TE")) i.remove();
            }
            dataAvail = updateNumLine(dataAvail);
            Tableview.setItems(dataAvail);
        }
        else if (DisplayMode.equals("K")) {                  
            for (Iterator<Playerlist> i = dataAvail.iterator(); i.hasNext();) {
                Playerlist playerLine = i.next();
                if (!playerLine.POS.contains("K")) i.remove();
            }
            dataAvail = updateNumLine(dataAvail);
            Tableview.setItems(dataAvail);
        }
        else if (DisplayMode.equals("RBWR")) {                  
            for (Iterator<Playerlist> i = dataAvail.iterator(); i.hasNext();) {
                Playerlist playerLine = i.next();
                if ((!playerLine.POS.contains("RB"))&&(!playerLine.POS.contains("WR"))) i.remove();                
            }
            dataAvail = updateNumLine(dataAvail);
            Tableview.setItems(dataAvail);
        }
        else if (DisplayMode.equals("DST")) {                  
            for (Iterator<Playerlist> i = dataAvail.iterator(); i.hasNext();) {
                Playerlist playerLine = i.next();
                if (!playerLine.POS.contains("DST")) i.remove();
            }
            dataAvail = updateNumLine(dataAvail);
            Tableview.setItems(dataAvail);
        }else if (DisplayMode.equals("ALL")) {                              
            dataAvail = updateNumLine(dataAvail);
            Tableview.setItems(dataAvail);
        }              
        for (Iterator<Playerlist> i = dataAvail.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if (playerLine.Player.contains(" -DR")) i.remove();
        }
        dataAvail = updateNumLine(dataAvail);
        
        Tableview.setItems(dataAvail);
    }
    
    
    private ObservableList<Playerlist> updateNumLine(ObservableList<Playerlist> newdata){
        int tableLen=Tableview.getItems().size();      
        int cnt=1;
        for (Iterator<Playerlist> i = newdata.iterator(); i.hasNext();) {
                Playerlist lst = i.next();
                lst.setLineNum(cnt);
                cnt++; 
            }
        return newdata;
    }
    public void AlertWin(String ErrorMessage ){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(ErrorMessage);
        alert.showAndWait();
    }
    @FXML
    private void Start_BTN(MouseEvent event) throws Exception,NullPointerException{
        LoadRakingFile();
        DisplayTableList();
        if(!RankingFileAdddress.equals("")){
            startStatus=true;
            Tableview.getSelectionModel().setCellSelectionEnabled(true);
            for (Playerlist pl:data ){

                   String playn=pl.getPlayer();


            for (Iterator<String> i = keeperlist.iterator(); i.hasNext();) {
                String keepername = i.next();

                Pattern pattern = Pattern.compile(keepername.toLowerCase());
                Matcher matcher = pattern.matcher(playn.toLowerCase());
                if (matcher.find()) {
                    if (!playn.contains(" -DR")){
                        pl.setPlayer(playn+" -DR");
                    }
                    if (pl.getStatus().equals("Keeper")){
                        pl.setStatus(" ");
                    }else{
                        pl.setStatus("Keeper");
                    }
                }

                }
            }
   
    TableviewPlaercallback();
   
    Tableview_POS.setCellFactory(column -> {
        return new TableCell<Playerlist, String>() {           
        @Override
        protected void updateItem(String item, boolean empty) {                
                super.updateItem(item, empty);                                                                  
            getStyleClass().remove("WR_Player_Color");
            getStyleClass().remove("RB_Player_Color");
            getStyleClass().remove("TE_Player_Color");
            getStyleClass().remove("QB_Player_Color");
            getStyleClass().remove("DST_Player_Color");
            getStyleClass().remove("K_Player_Color");
            if (item == null || empty) {
                setText(null);
                setGraphic(null);
                //setStyle("");
            } else {
             //        
                setText(item);
                Pattern pattern = Pattern.compile("WR");
                Matcher matcher = pattern.matcher(item);
                if (matcher.find()) {
                   //setStyle("-fx-background-color: red");
                   getStyleClass().add("WR_Player_Color");
                }
                pattern = Pattern.compile("RB");
                matcher = pattern.matcher(item);
                if (matcher.find()) {
                   //setStyle("-fx-background-color: red");
                   getStyleClass().add("RB_Player_Color");
                }     
                pattern = Pattern.compile("TE");
                matcher = pattern.matcher(item);
                if (matcher.find()) {
                   //setStyle("-fx-background-color: red");
                   getStyleClass().add("TE_Player_Color");
                }    
                pattern = Pattern.compile("QB");
                matcher = pattern.matcher(item);
                if (matcher.find()) {
                   //setStyle("-fx-background-color: red");
                   getStyleClass().add("QB_Player_Color");
                }  
                pattern = Pattern.compile("DST");
                matcher = pattern.matcher(item);
                if (matcher.find()) {
                   //setStyle("-fx-background-color: red");
                   getStyleClass().add("DST_Player_Color");
                }  
                pattern = Pattern.compile("K");
                matcher = pattern.matcher(item);
                if (matcher.find()) {                    
                   getStyleClass().add("K_Player_Color");
                }  
            }                            
        }

        };                
        });
                       
        }else{
            AlertWin("Load the csv file");
        }
        //Setup Tierlist columns
        String file = CurrDir+"TierListFileRB";   
        File f=new File(file);      
        if (f.exists()){
            loadRBTierList();
        }else{
            MakeRBTierList();
        }
        file = CurrDir+"TierListFileWR";   
        f=new File(file);      
        if (f.exists()){
            loadWRTierList();
        }else{
            MakeWRTierList();
        }
        file = CurrDir+"TierListFileQB";   
        f=new File(file);      
        if (f.exists()){
            loadQBTierList();
        }else{
            MakeQBTierList();
        }
         file = CurrDir+"TierListFileTE";   
        f=new File(file);      
        if (f.exists()){
            loadTETierList();
        }else{
            MakeTETierList();
        }
        RBlistviewCallback();
        WRlistviewCallback();
        QBlistviewCallback();
        TElistviewCallback();
    }

    @FXML
    private void TextfieldKeyPress(KeyEvent event) {
        String file = CurrDir+"Mynotes";
        String allnotes = "";        
        allnotes=Textfield.getText();
       
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(allnotes);
            writer.close(); 
        } catch (Exception e) {
        }
         
    }
    private void Readmynotes(){
        
        String file = CurrDir+"Mynotes";
        ObservableList<Playerlist> content = FXCollections.observableArrayList();
        int cnt=1;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            String allnotes = "";
            while ((line = br.readLine()) != null) {                
                allnotes=allnotes+line+"\n";
                
            }
            Textfield.setText(allnotes);
        }
        catch (Exception e) {
        }
    }
    
    private void MakeRBTierList(){
        ObservableList<Playerlist> tempList = FXCollections.observableArrayList(data);
       
        for (Iterator<Playerlist> i = tempList.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if ((!playerLine.POS.contains("RB")) ) i.remove();
        }
        for (Iterator<Playerlist> i = tempList.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            TierlistRBdata.add(playerLine.getPlayer());
        }
        TierListviewRB.setItems(TierlistRBdata);
                        
    }
    private void MakeWRTierList(){
        ObservableList<Playerlist> tempList = FXCollections.observableArrayList(data);
       
        for (Iterator<Playerlist> i = tempList.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if ((!playerLine.POS.contains("WR")) ) i.remove();
        }
        for (Iterator<Playerlist> i = tempList.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            TierlistWRdata.add(playerLine.getPlayer());
        }
        TierListviewWR.setItems(TierlistWRdata);                        
    }
    private void MakeQBTierList(){
        ObservableList<Playerlist> tempList = FXCollections.observableArrayList(data);
       
        for (Iterator<Playerlist> i = tempList.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if ((!playerLine.POS.contains("QB")) ) i.remove();
        }
        for (Iterator<Playerlist> i = tempList.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            TierlistQBdata.add(playerLine.getPlayer());
        }
        TierListviewQB.setItems(TierlistQBdata);                        
    }
    private void MakeTETierList(){
        ObservableList<Playerlist> tempList = FXCollections.observableArrayList(data);
       
        for (Iterator<Playerlist> i = tempList.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            if ((!playerLine.POS.contains("TE")) ) i.remove();
        }
        for (Iterator<Playerlist> i = tempList.iterator(); i.hasNext();) {
            Playerlist playerLine = i.next();
            TierlistTEdata.add(playerLine.getPlayer());
        }
        TierListviewTE.setItems(TierlistTEdata);                        
    }
    
    
    Integer TierRBNum=1;
    Integer TierNum=1;
    int tempnum=0;
    boolean TierRBClicked=false;
    boolean TierWRClicked=false;
    boolean TierQBClicked=false;
    boolean TierTEClicked=false;
    @FXML
    private void AddTierBTNClicked(MouseEvent event) {
        if(TierRBClicked==true && posAvailable==false){
            TierRBClicked=false;
            try {
                String selectitem=TierListviewRB.getSelectionModel().getSelectedItem();
                int selectindex=TierListviewRB.getSelectionModel().getSelectedIndex();
                ObservableList<String> NewTierlistRBdata = FXCollections.observableArrayList();
                if(!selectitem.equals("")){
                    if ( (!TierlistRBdata.get(0).contains("Tier 1") && selectindex==0) || (TierlistRBdata.get(0).contains("Tier 1")) ){


                        for (Iterator<String> i = TierlistRBdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();

                            if(selectitem.contains("Tier") ){
                                if (selectitem.equals(playerLine)){

                                    NewTierlistRBdata.add("Tier "+TierNum);
                                    System.out.println("add tier= "+TierNum);
                                    TierNum++;

                                        NewTierlistRBdata.add("");

                                    NewTierlistRBdata.add(playerLine);
                                }                
                                else{
                                    NewTierlistRBdata.add(playerLine);
                                }
                            }else if (!selectitem.equals("")){
                                if (selectitem.equals(playerLine)){
                                    if (TierlistRBdata.get(0).contains("Tier 1")){
                                        NewTierlistRBdata.add("");
                                    }
                                    NewTierlistRBdata.add("Tier "+TierNum);
                                    System.out.println("add tier= "+TierNum);
                                    TierNum++;
                                    NewTierlistRBdata.add(playerLine);
                                }                
                                else{
                                    NewTierlistRBdata.add(playerLine);
                                }
                            }
                        }
                    
                        ObservableList<String> NewTierlistRBdata2 = FXCollections.observableArrayList();
                        TierNum=1;
                         for (Iterator<String> i = NewTierlistRBdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();

                            //check tier num
                            if (playerLine.contains("Tier")){
                                String [] linesplit=playerLine.split(" ");                                                                          
                                NewTierlistRBdata2.add("Tier "+TierNum);
                                TierNum++;
                            }
                            else{
                                NewTierlistRBdata2.add(playerLine);
                            }
                         }

                        TierlistRBdata=FXCollections.observableArrayList(NewTierlistRBdata2);
                        TierListviewRB.setItems(TierlistRBdata);
                    }
                }
                
            } catch (Exception e) {
            }
        }else if(TierWRClicked==true && posAvailable==false){
            TierWRClicked=false;
            try {
                String selectitem=TierListviewWR.getSelectionModel().getSelectedItem();
                int selectindex=TierListviewWR.getSelectionModel().getSelectedIndex();
                ObservableList<String> NewTierlistWRdata = FXCollections.observableArrayList();
                if(!selectitem.equals("")){
                    if ( (!TierlistWRdata.get(0).contains("Tier 1") && selectindex==0) || (TierlistWRdata.get(0).contains("Tier 1")) ){


                        for (Iterator<String> i = TierlistWRdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();

                            if(selectitem.contains("Tier") ){
                                if (selectitem.equals(playerLine)){

                                    NewTierlistWRdata.add("Tier "+TierNum);
                                    System.out.println("add tier= "+TierNum);
                                    TierNum++;

                                        NewTierlistWRdata.add("");

                                    NewTierlistWRdata.add(playerLine);
                                }                
                                else{
                                    NewTierlistWRdata.add(playerLine);
                                }
                            }else if (!selectitem.equals("")){
                                if (selectitem.equals(playerLine)){
                                    if (TierlistWRdata.get(0).contains("Tier 1")){
                                        NewTierlistWRdata.add("");
                                    }
                                    NewTierlistWRdata.add("Tier "+TierNum);
                                    System.out.println("add tier= "+TierNum);
                                    TierNum++;
                                    NewTierlistWRdata.add(playerLine);
                                }                
                                else{
                                    NewTierlistWRdata.add(playerLine);
                                }
                            }
                        }
                    
                        ObservableList<String> NewTierlistWRdata2 = FXCollections.observableArrayList();
                        TierNum=1;
                         for (Iterator<String> i = NewTierlistWRdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();

                            //check tier num
                            if (playerLine.contains("Tier")){
                                String [] linesplit=playerLine.split(" ");                                                                          
                                NewTierlistWRdata2.add("Tier "+TierNum);
                                TierNum++;
                            }
                            else{
                                NewTierlistWRdata2.add(playerLine);
                            }
                         }

                        TierlistWRdata=FXCollections.observableArrayList(NewTierlistWRdata2);
                        TierListviewWR.setItems(TierlistWRdata);
                    }
                }
                
            } catch (Exception e) {
            }
        }
        else if(TierQBClicked==true && posAvailable==false){
            TierQBClicked=false;
            try {
                String selectitem=TierListviewQB.getSelectionModel().getSelectedItem();
                int selectindex=TierListviewQB.getSelectionModel().getSelectedIndex();
                ObservableList<String> NewTierlistQBdata = FXCollections.observableArrayList();
                if(!selectitem.equals("")){
                    if ( (!TierlistQBdata.get(0).contains("Tier 1") && selectindex==0) || (TierlistQBdata.get(0).contains("Tier 1")) ){


                        for (Iterator<String> i = TierlistQBdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();

                            if(selectitem.contains("Tier") ){
                                if (selectitem.equals(playerLine)){

                                    NewTierlistQBdata.add("Tier "+TierNum);
                                    System.out.println("add tier= "+TierNum);
                                    TierNum++;

                                        NewTierlistQBdata.add("");

                                    NewTierlistQBdata.add(playerLine);
                                }                
                                else{
                                    NewTierlistQBdata.add(playerLine);
                                }
                            }else if (!selectitem.equals("")){
                                if (selectitem.equals(playerLine)){
                                    if (TierlistQBdata.get(0).contains("Tier 1")){
                                        NewTierlistQBdata.add("");
                                    }
                                    NewTierlistQBdata.add("Tier "+TierNum);
                                    System.out.println("add tier= "+TierNum);
                                    TierNum++;
                                    NewTierlistQBdata.add(playerLine);
                                }                
                                else{
                                    NewTierlistQBdata.add(playerLine);
                                }
                            }
                        }
                    
                        ObservableList<String> NewTierlistQBdata2 = FXCollections.observableArrayList();
                        TierNum=1;
                         for (Iterator<String> i = NewTierlistQBdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();

                            //check tier num
                            if (playerLine.contains("Tier")){
                                String [] linesplit=playerLine.split(" ");                                                                          
                                NewTierlistQBdata2.add("Tier "+TierNum);
                                TierNum++;
                            }
                            else{
                                NewTierlistQBdata2.add(playerLine);
                            }
                         }

                        TierlistQBdata=FXCollections.observableArrayList(NewTierlistQBdata2);
                        TierListviewQB.setItems(TierlistQBdata);
                    }
                }
                
            } catch (Exception e) {
            }
        }
        else if(TierTEClicked==true && posAvailable==false){
            TierTEClicked=false;
            try {
                String selectitem=TierListviewTE.getSelectionModel().getSelectedItem();
                int selectindex=TierListviewTE.getSelectionModel().getSelectedIndex();
                ObservableList<String> NewTierlistTEdata = FXCollections.observableArrayList();
                if(!selectitem.equals("")){
                    if ( (!TierlistTEdata.get(0).contains("Tier 1") && selectindex==0) || (TierlistTEdata.get(0).contains("Tier 1")) ){


                        for (Iterator<String> i = TierlistTEdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();

                            if(selectitem.contains("Tier") ){
                                if (selectitem.equals(playerLine)){

                                    NewTierlistTEdata.add("Tier "+TierNum);
                                    System.out.println("add tier= "+TierNum);
                                    TierNum++;

                                        NewTierlistTEdata.add("");

                                    NewTierlistTEdata.add(playerLine);
                                }                
                                else{
                                    NewTierlistTEdata.add(playerLine);
                                }
                            }else if (!selectitem.equals("")){
                                if (selectitem.equals(playerLine)){
                                    if (TierlistTEdata.get(0).contains("Tier 1")){
                                        NewTierlistTEdata.add("");
                                    }
                                    NewTierlistTEdata.add("Tier "+TierNum);
                                    System.out.println("add tier= "+TierNum);
                                    TierNum++;
                                    NewTierlistTEdata.add(playerLine);
                                }                
                                else{
                                    NewTierlistTEdata.add(playerLine);
                                }
                            }
                        }
                    
                        ObservableList<String> NewTierlistTEdata2 = FXCollections.observableArrayList();
                        TierNum=1;
                         for (Iterator<String> i = NewTierlistTEdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();

                            //check tier num
                            if (playerLine.contains("Tier")){
                                String [] linesplit=playerLine.split(" ");                                                                          
                                NewTierlistTEdata2.add("Tier "+TierNum);
                                TierNum++;
                            }
                            else{
                                NewTierlistTEdata2.add(playerLine);
                            }
                         }

                        TierlistTEdata=FXCollections.observableArrayList(NewTierlistTEdata2);
                        TierListviewTE.setItems(TierlistTEdata);
                    }
                }
                
            } catch (Exception e) {
            }
        }
    }

    @FXML
    private void TierListviewRBClicked(MouseEvent event) {
        TierRBClicked=true;
        TierlistEn=true;
        TierModeSelect="RB";
        //TierNum=TierRBNum;
        if (event.getClickCount()<2) {
            System.out.println("click");            
            String TierlistselectedPlayer=TierListviewRB.getSelectionModel().getSelectedItem();
            if(!TierlistselectedPlayer.contains("Tier") && !TierlistselectedPlayer.equals("")){
                    FromTiertolist(TierlistselectedPlayer);
            }
        }
        else if(event.getClickCount()>=2){
            String TierlistselectedPlayer=TierListviewRB.getSelectionModel().getSelectedItem();
            if(!TierlistselectedPlayer.contains("Tier") && !TierlistselectedPlayer.equals("")){
                    TierlistPlayerDrafted(TierlistselectedPlayer);
                    TablelistPlayerDrafted(TierlistselectedPlayer);
                    FromTiertolist(TierlistselectedPlayer);
            }
                if (posAvailable==true){ removeDraftedTierList();}
        }
        
    }

    @FXML
    private void TierListviewWRClicked(MouseEvent event) {
        TierWRClicked=true;
        TierlistEn=true;
        TierModeSelect="WR";
        if (event.getClickCount()<2) {
            System.out.println("click");            
            String TierlistselectedPlayer=TierListviewWR.getSelectionModel().getSelectedItem();
            if(!TierlistselectedPlayer.contains("Tier") && !TierlistselectedPlayer.equals("")){
                    FromTiertolist(TierlistselectedPlayer);
            }
        }
        else if(event.getClickCount()>=2){
            String TierlistselectedPlayer=TierListviewWR.getSelectionModel().getSelectedItem();
            if(!TierlistselectedPlayer.contains("Tier") && !TierlistselectedPlayer.equals("")){
                    TierlistPlayerDraftedWR(TierlistselectedPlayer);
                    TablelistPlayerDrafted(TierlistselectedPlayer);
                    FromTiertolist(TierlistselectedPlayer);
            }
                if (posAvailable==true){ removeDraftedTierList();}
        }
        
    }
            

    @FXML
    private void TierListviewQBClicked(MouseEvent event) {
         TierQBClicked=true;
         TierlistEn=true;
         TierModeSelect="QB";
         if (event.getClickCount()<2) {
            System.out.println("click");            
            String TierlistselectedPlayer=TierListviewQB.getSelectionModel().getSelectedItem();
            if(!TierlistselectedPlayer.contains("Tier") && !TierlistselectedPlayer.equals("")){
                    FromTiertolist(TierlistselectedPlayer);
            }
        }
        else if(event.getClickCount()>=2){
            String TierlistselectedPlayer=TierListviewQB.getSelectionModel().getSelectedItem();
            if(!TierlistselectedPlayer.contains("Tier") && !TierlistselectedPlayer.equals("")){
                    TierlistPlayerDraftedQB(TierlistselectedPlayer);
                    TablelistPlayerDrafted(TierlistselectedPlayer);
                    FromTiertolist(TierlistselectedPlayer);
            }
                if (posAvailable==true){ removeDraftedTierList();}
        }
    }

    @FXML
    private void TierListviewTEClicked(MouseEvent event) {
         TierTEClicked=true;
         TierlistEn=true;
         TierModeSelect="TE";
         if (event.getClickCount()<2) {
            System.out.println("click");            
            String TierlistselectedPlayer=TierListviewTE.getSelectionModel().getSelectedItem();
            if(!TierlistselectedPlayer.contains("Tier") && !TierlistselectedPlayer.equals("")){
                    FromTiertolist(TierlistselectedPlayer);
            }
        }
        else if(event.getClickCount()>=2){
            String TierlistselectedPlayer=TierListviewTE.getSelectionModel().getSelectedItem();
            if(!TierlistselectedPlayer.contains("Tier") && !TierlistselectedPlayer.equals("")){
                    TierlistPlayerDraftedTE(TierlistselectedPlayer);
                    TablelistPlayerDrafted(TierlistselectedPlayer);
                    FromTiertolist(TierlistselectedPlayer);
            }
                if (posAvailable==true){ removeDraftedTierList();}
        }
    }

    @FXML
    private void RemoveTierBTNClicked(MouseEvent event) {
        boolean TierExist=false;
        if(TierRBClicked==true && posAvailable==false){
            TierRBClicked=false;
            try {
                String selectitem=TierListviewRB.getSelectionModel().getSelectedItem();
                int selectindex=TierListviewRB.getSelectionModel().getSelectedIndex();               
                System.out.println("Remove Tier");
                if(selectitem.contains("Tier")){
                    if ( (!selectitem.contains("Tier 1") && selectindex!=0 ) ){
                        
                        TierlistRBdata.remove(selectitem);
                        if (selectindex!=0){
                            if (TierlistRBdata.get(selectindex-1).equals("")){
                                 TierlistRBdata.remove(selectindex-1);
                            }                            
                        }
                    }
                    else if (selectitem.contains("Tier 1")){   
                        for (Iterator<String> i = TierlistRBdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();
                            if (playerLine.contains("Tier 2")){
                                TierExist=true;
                            }
                        }
                        if(TierExist==false){
                            TierlistRBdata.remove(selectitem);
                        }
                    }                    
                        ObservableList<String> NewTierlistdataTEMPList = FXCollections.observableArrayList();
                        TierNum=1;
                         for (Iterator<String> i = TierlistRBdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();
                            //check tier num
                            if (playerLine.contains("Tier")){
                                String [] linesplit=playerLine.split(" ");                                                                          
                                NewTierlistdataTEMPList.add("Tier "+TierNum);
                                TierNum++;
                            }
                            else{
                                NewTierlistdataTEMPList.add(playerLine);
                            }
                         }
                        TierlistRBdata=FXCollections.observableArrayList(NewTierlistdataTEMPList);
                        TierListviewRB.setItems(TierlistRBdata);
                }
                
            } catch (Exception e) {
            }
        }
        else if(TierWRClicked==true && posAvailable==false){
            TierWRClicked=false;
            try {
                String selectitem=TierListviewWR.getSelectionModel().getSelectedItem();
                int selectindex=TierListviewWR.getSelectionModel().getSelectedIndex();               
                System.out.println("Remove Tier");
                if(selectitem.contains("Tier")){
                    if ( (!selectitem.contains("Tier 1") && selectindex!=0 ) ){
                        
                        TierlistWRdata.remove(selectitem);
                        if (selectindex!=0){
                            if (TierlistWRdata.get(selectindex-1).equals("")){
                                 TierlistWRdata.remove(selectindex-1);
                            }                            
                        }
                    }
                    else if (selectitem.contains("Tier 1")){   
                        for (Iterator<String> i = TierlistWRdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();
                            if (playerLine.contains("Tier 2")){
                                TierExist=true;
                            }
                        }
                        if(TierExist==false){
                            TierlistWRdata.remove(selectitem);
                        }
                    }                    
                        ObservableList<String> NewTierlistdataTEMPList = FXCollections.observableArrayList();
                        TierNum=1;
                         for (Iterator<String> i = TierlistWRdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();
                            //check tier num
                            if (playerLine.contains("Tier")){
                                String [] linesplit=playerLine.split(" ");                                                                          
                                NewTierlistdataTEMPList.add("Tier "+TierNum);
                                TierNum++;
                            }
                            else{
                                NewTierlistdataTEMPList.add(playerLine);
                            }
                         }
                        TierlistWRdata=FXCollections.observableArrayList(NewTierlistdataTEMPList);
                        TierListviewWR.setItems(TierlistWRdata);
                }
                
            } catch (Exception e) {
            }
        }
        else if(TierQBClicked==true && posAvailable==false){
            TierQBClicked=false;
            try {
                String selectitem=TierListviewQB.getSelectionModel().getSelectedItem();
                int selectindex=TierListviewQB.getSelectionModel().getSelectedIndex();               
                System.out.println("Remove Tier");
                if(selectitem.contains("Tier")){
                    if ( (!selectitem.contains("Tier 1") && selectindex!=0 ) ){
                        
                        TierlistQBdata.remove(selectitem);
                        if (selectindex!=0){
                            if (TierlistQBdata.get(selectindex-1).equals("")){
                                 TierlistQBdata.remove(selectindex-1);
                            }                            
                        }
                    }
                    else if (selectitem.contains("Tier 1")){   
                        for (Iterator<String> i = TierlistQBdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();
                            if (playerLine.contains("Tier 2")){
                                TierExist=true;
                            }
                        }
                        if(TierExist==false){
                            TierlistQBdata.remove(selectitem);
                        }
                    }                    
                        ObservableList<String> NewTierlistdataTEMPList = FXCollections.observableArrayList();
                        TierNum=1;
                         for (Iterator<String> i = TierlistQBdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();
                            //check tier num
                            if (playerLine.contains("Tier")){
                                String [] linesplit=playerLine.split(" ");                                                                          
                                NewTierlistdataTEMPList.add("Tier "+TierNum);
                                TierNum++;
                            }
                            else{
                                NewTierlistdataTEMPList.add(playerLine);
                            }
                         }
                        TierlistQBdata=FXCollections.observableArrayList(NewTierlistdataTEMPList);
                        TierListviewQB.setItems(TierlistQBdata);
                }
                
            } catch (Exception e) {
            }
        }
    else if(TierTEClicked==true && posAvailable==false){
            TierTEClicked=false;
            try {
                String selectitem=TierListviewTE.getSelectionModel().getSelectedItem();
                int selectindex=TierListviewTE.getSelectionModel().getSelectedIndex();               
                System.out.println("Remove Tier");
                if(selectitem.contains("Tier")){
                    if ( (!selectitem.contains("Tier 1") && selectindex!=0 ) ){
                        
                        TierlistTEdata.remove(selectitem);
                        if (selectindex!=0){
                            if (TierlistTEdata.get(selectindex-1).equals("")){
                                 TierlistTEdata.remove(selectindex-1);
                            }                            
                        }
                    }
                    else if (selectitem.contains("Tier 1")){   
                        for (Iterator<String> i = TierlistTEdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();
                            if (playerLine.contains("Tier 2")){
                                TierExist=true;
                            }
                        }
                        if(TierExist==false){
                            TierlistTEdata.remove(selectitem);
                        }
                    }                    
                        ObservableList<String> NewTierlistdataTEMPList = FXCollections.observableArrayList();
                        TierNum=1;
                         for (Iterator<String> i = TierlistTEdata.iterator(); i.hasNext();) {
                            String playerLine = i.next();
                            //check tier num
                            if (playerLine.contains("Tier")){
                                String [] linesplit=playerLine.split(" ");                                                                          
                                NewTierlistdataTEMPList.add("Tier "+TierNum);
                                TierNum++;
                            }
                            else{
                                NewTierlistdataTEMPList.add(playerLine);
                            }
                         }
                        TierlistTEdata=FXCollections.observableArrayList(NewTierlistdataTEMPList);
                        TierListviewTE.setItems(TierlistTEdata);
                }
                
            } catch (Exception e) {
            }
        }
    }
    class ColorDraftList extends ListCell<String> {
            @Override            
            public void updateItem(String item, boolean empty) {               
                
                super.updateItem(item, empty);
                getStyleClass().remove("WR_Player_Color");
                getStyleClass().remove("RB_Player_Color");
                getStyleClass().remove("TE_Player_Color");
                getStyleClass().remove("QB_Player_Color");
                getStyleClass().remove("DST_Player_Color");
                getStyleClass().remove("K_Player_Color");
                 try{                       
                    if (empty) {                       
                        setText(null);
                        setGraphic(null);
                       } else {                            
                            setText(item);
                            Pattern pattern = Pattern.compile("^WR");
                            Matcher matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("WR_Player_Color");
                            }
                            pattern = Pattern.compile("^RB");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("RB_Player_Color");
                            }     
                            pattern = Pattern.compile("^TE");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("TE_Player_Color");
                            }    
                            pattern = Pattern.compile("^QB");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("QB_Player_Color");
                            }  
                            pattern = Pattern.compile("^DST");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("DST_Player_Color");
                            }  
                            pattern = Pattern.compile("^K");
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                               //setStyle("-fx-background-color: red");
                               getStyleClass().add("K_Player_Color");
                            }  
                       }
                 }catch(Exception e){
                         
                 }                    
            }
           }
    
    
    @FXML
    private void SearchKeyPress(KeyEvent event) {
        System.out.println("Search key press");
        String searchText = searchField.getText().toLowerCase();
        Tableview.setItems(data);
        if(searchText.equals("")){
            Tableview.setItems(data);            
            newSelected=0;    
            
            try {
                findCurrentPlayerLine();
                Tableview.scrollTo(newSelected-3);
                Tableview.getSelectionModel().select(newSelected, Tableview_Player);                
                Tableview.getFocusModel().focus(0);
            } catch (Exception e) {
            }
        }else{
            
            searchText = searchField.getText().toLowerCase();
            ObservableList<Playerlist> dataSearch = FXCollections.observableArrayList(data);       
            for (Iterator<Playerlist> i = dataSearch.iterator(); i.hasNext();) {
                Playerlist lst = i.next();
                String player=lst.getPlayer().toLowerCase();
                if (player.contains(searchText)){
                  
                }
                else{
                    i.remove();
                }
              //  if (!lst.POS.contains(searchText)) i.remove();
            }
            Tableview.setItems(dataSearch);            
        }
        
        
    }

    public void findCurrentPlayerLine() throws Exception{
        int k=0;
            
            for (Iterator<Playerlist> i = data.iterator(); i.hasNext();) {
                Playerlist lst = i.next();
                String player=lst.getPlayer().toLowerCase();              
                if(player.contains(Selectedplayer.toLowerCase())){
                    newSelected=k;
                    break;
                }
                k++; 
            }
    }
    public static Set<Character> stringToCharacterSet(String s) {
    Set<Character> set = new HashSet<>();
    for (char c : s.toCharArray()) {
        set.add(c);
    }
    return set;
    }

    public static boolean containsAllChars (String container, String containee) {
        return stringToCharacterSet(container).containsAll
                   (stringToCharacterSet(containee));
    } 
    boolean dropToTlistRB = false;
    boolean dropToTlistWR = false;
    boolean dropToTlistTE = false;
    boolean dropToTlistQB = false;
    public void RBlistviewCallback(){
                TierListviewRB.setCellFactory( new Callback<ListView<String>, ListCell<String>>()
            {
                @Override
                public ListCell<String> call( ListView<String> param )
                {
                    ListCell<String> listCell = new ListCell<String>()
                    {
                        @Override
                        protected void updateItem( String item, boolean empty )
                        {
                            super.updateItem( item, empty );
                            //setText( item );
                            getStyleClass().add("TierListview_Player_RB");
                            getStyleClass().remove("TierListview_Color");
                            getStyleClass().remove("Tableview_Player_Drafted");
                            
                            getStyleClass().add("TierListview_Player_RB");
                            getStyleClass().remove("TierListview_Color");
                            getStyleClass().remove("Tableview_Player_Drafted");
                            
                            if (item == null || empty) {
                                setText(null);
                               // setGraphic(null);
                                //setStyle("");
                            } else {
                     
                                setText(item);
                                Pattern pattern = Pattern.compile("^Tier");
                                Matcher matcher = pattern.matcher(item);
                                if (matcher.find()){
                                    getStyleClass().add("TierListview_Color");
                                }      
                                pattern = Pattern.compile(" -DR");
                                matcher = pattern.matcher(item);
                                if (matcher.find()) {
                                   //setStyle("-fx-background-color: red");
                                   getStyleClass().add("Tableview_Player_Drafted");
                                }
                                
                                pattern = Pattern.compile("^Tier");
                                matcher = pattern.matcher(item);
                                if (matcher.find()){
                                    getStyleClass().add("TierListview_Color");
                                }      
                                pattern = Pattern.compile(" -DR");
                                matcher = pattern.matcher(item);
                                if (matcher.find()) {
                                   //setStyle("-fx-background-color: red");
                                   getStyleClass().add("Tableview_Player_Drafted");
                                }       
                                
                            }
                        }
                    };

                    
                    listCell.setOnDragDetected( ( MouseEvent event ) ->
                    {
                        
                        String newaddString="";
                        System.out.println( "listcell setOnDragDetected" );
                        Dragboard db = listCell.startDragAndDrop( TransferMode.MOVE );
                        ClipboardContent content = new ClipboardContent();
                        if (!(listCell.getItem().contains("Tier") || listCell.getItem()=="")){
                            content.putString( listCell.getItem() );
                            db.setContent( content );
                            event.consume();           
                            dragSource.set(listCell);
                            dropToTlistRB=true;
                            dropToTlistWR=false;
                            dropToTlistQB=false;
                            dropToTlistTE=false;
                        }

                    } );

                    listCell.setOnDragOver( ( DragEvent event ) ->
                    {
                        Dragboard db = event.getDragboard();
                        if ( db.hasString() )
                        {
                            event.acceptTransferModes( TransferMode.MOVE );

                        }
                        event.consume();
                    } );

                    // listCell.setOnDragDone(event -> DraftListView.getItems().remove(listCell.getItem()));

                     listCell.setOnDragDropped(event -> {
                         if(dropToTlistRB){
                            dropToTlistRB=false;
                            if (posAvailable==false){                                
                                Dragboard db = event.getDragboard();
                                String MovesString=db.getString();
                                String DroptorowitemString=listCell.getItem();
                                 System.err.println("DROPTO= "+DroptorowitemString);
                               //if (db.hasString() && dragSource.get() != null) {
                                if (MovesString!=null && !listCell.getItem().equals("")) {
                                    if (!DroptorowitemString.contains("Tier") && !DroptorowitemString.equals(MovesString)){

                                    System.out.println("remove = "+ MovesString);

                                    System.out.println("target:"+ DroptorowitemString);

                                    TierlistRBdata.remove(MovesString);

                                     // TierlistRBdata.remove(MovesString); TierlistRBdata.remove(MovesString);
                                      ObservableList<String> newTierlistRBdata = FXCollections.observableArrayList();
                                      newTierlistRBdata.clear();
                                      newTierlistRBdata=null;                  
                                      newTierlistRBdata= FXCollections.observableArrayList();

                                      for (Iterator<String> i = TierlistRBdata.iterator(); i.hasNext();) {
                                          String Listnext = i.next();
                                         // System.out.println("draftdata= "+Listnext);
                                        //  if (!Listnext.equals(db.getString())){
                                                //NewDraftData.add(Listnext);
                                          //}
                                            if (Listnext.equals(DroptorowitemString)){
                                                newTierlistRBdata.add(MovesString);
                                                newTierlistRBdata.add(DroptorowitemString);
                                              //NewDraftData.add(Listnext);
                                                System.out.println("add = "+ MovesString);
                                            }else{
                                                newTierlistRBdata.add(Listnext);
                                            }
                                         }
                                     // if(DroptorowitemString==null){
                                      //    newTierlistRBdata.add(MovesString);
                                     // }
                                      TierListviewRB.getItems().clear();
                                      TierlistRBdata=null;                                                    
                                      TierlistRBdata=FXCollections.observableArrayList(newTierlistRBdata);
                                     TierListviewRB.setItems(TierlistRBdata);

                                  }
                               } 
                                else if ( MovesString!=null && listCell.getItem().equals("")){
                                    System.err.println("Move to empty line");
                                    int x=listCell.getIndex()-1;
                                    DroptorowitemString=TierlistRBdata.get(x);

                                    MovesString=db.getString();
                                    if (!DroptorowitemString.equals(MovesString)){
                                        System.out.println("remove = "+ MovesString);

                                        System.out.println("target:"+ DroptorowitemString);

                                        TierlistRBdata.remove(MovesString);

                                          ObservableList<String> newTierlistRBdata = FXCollections.observableArrayList();
                                          newTierlistRBdata.clear();
                                          newTierlistRBdata=null;                  
                                          newTierlistRBdata= FXCollections.observableArrayList();

                                         for (Iterator<String> i = TierlistRBdata.iterator(); i.hasNext();) {
                                              String Listnext = i.next();
                                            //  System.out.println("draftdata= "+Listnext);
                                            //  if (!Listnext.equals(db.getString())){
                                                    //NewDraftData.add(Listnext);
                                              //}
                                                if (Listnext.equals(DroptorowitemString)){

                                                    newTierlistRBdata.add(DroptorowitemString);
                                                    newTierlistRBdata.add(MovesString);
                                                  //NewDraftData.add(Listnext);
                                                    System.out.println("add = "+ MovesString);
                                                }else{
                                                    newTierlistRBdata.add(Listnext);
                                                }
                                             }

                                         TierListviewRB.getItems().clear();
                                          TierlistRBdata=null;                                                    
                                          TierlistRBdata=FXCollections.observableArrayList(newTierlistRBdata);
                                         TierListviewRB.setItems(TierlistRBdata);
                                        System.err.println(TierListviewRB.getItems());
                                    }
                                }
                               else {
                                   event.setDropCompleted(false);
                               }
                            }
                         }
                               event.consume();
                           });


                    return listCell;
                }
                } );
    }
    public void WRlistviewCallback(){
        TierListviewWR.setCellFactory( new Callback<ListView<String>, ListCell<String>>()
            {
                @Override
                public ListCell<String> call( ListView<String> param )
                {
                    ListCell<String> listCell = new ListCell<String>()
                    {
                        @Override
                        protected void updateItem( String item, boolean empty )
                        {                            
                            super.updateItem( item, empty );
                            //setText( item );
                            getStyleClass().add("TierListview_Player_RB");
                            getStyleClass().remove("TierListview_Color");
                            getStyleClass().remove("Tableview_Player_Drafted");
                            
                            getStyleClass().add("TierListview_Player_RB");
                            getStyleClass().remove("TierListview_Color");
                            getStyleClass().remove("Tableview_Player_Drafted");
                            
                            if (item == null || empty) {
                                setText(null);
                               // setGraphic(null);
                                //setStyle("");
                            } else {
                     
                                setText(item);
                                Pattern pattern = Pattern.compile("^Tier");
                                Matcher matcher = pattern.matcher(item);
                                if (matcher.find()){
                                    getStyleClass().add("TierListview_Color");
                                }      
                                pattern = Pattern.compile(" -DR");
                                matcher = pattern.matcher(item);
                                if (matcher.find()) {
                                   //setStyle("-fx-background-color: red");
                                   getStyleClass().add("Tableview_Player_Drafted");
                                }
                                
                                pattern = Pattern.compile("^Tier");
                                matcher = pattern.matcher(item);
                                if (matcher.find()){
                                    getStyleClass().add("TierListview_Color");
                                }      
                                pattern = Pattern.compile(" -DR");
                                matcher = pattern.matcher(item);
                                if (matcher.find()) {
                                   //setStyle("-fx-background-color: red");
                                   getStyleClass().add("Tableview_Player_Drafted");
                                }       
                                
                            }
                        }
                    };

                    
                    listCell.setOnDragDetected( ( MouseEvent event ) ->
                    {                        
                        String newaddString="";
                        System.out.println( "listcell setOnDragDetected" );
                        Dragboard db = listCell.startDragAndDrop( TransferMode.MOVE );
                        ClipboardContent content = new ClipboardContent();
                        if (!(listCell.getItem().contains("Tier") || listCell.getItem()=="")){
                            content.putString( listCell.getItem() );
                            db.setContent( content );
                            event.consume();           
                            dragSource.set(listCell);
                            dropToTlistRB=false;
                            dropToTlistWR=true;
                            dropToTlistQB=false;
                            dropToTlistTE=false;
                        }

                    } );

                    listCell.setOnDragOver( ( DragEvent event ) ->
                    {
                        Dragboard db = event.getDragboard();
                        if ( db.hasString() )
                        {
                            event.acceptTransferModes( TransferMode.MOVE );

                        }
                        event.consume();
                    } );

                    // listCell.setOnDragDone(event -> DraftListView.getItems().remove(listCell.getItem()));

                     listCell.setOnDragDropped(event -> {
                         if (dropToTlistWR){
                             dropToTlistWR=false;
                            if (posAvailable==false){
                                Dragboard db = event.getDragboard();
                                String MovesString=db.getString();
                                String DroptorowitemString=listCell.getItem();
                                 System.err.println("DROPTO= "+DroptorowitemString);
                               //if (db.hasString() && dragSource.get() != null) {
                                if (MovesString!=null && !listCell.getItem().equals("")) {
                                    if (!DroptorowitemString.contains("Tier") && !DroptorowitemString.equals(MovesString)){
                                   // in this example you could just do
                                   //DraftListView.getItems().add(db.getString());
                                   // but more generally:

            //                       ListCell<String> dragSourceCell = dragSource.get();
            //                       DraftListView.getItems().add(dragSourceCell.getItem());
            //                       event.setDropCompleted(true);
            //                       dragSource.set(null);

                                    System.out.println("remove = "+ MovesString);

                                    System.out.println("target:"+ DroptorowitemString);

                                    TierlistWRdata.remove(MovesString);

                                     // TierlistWRdata.remove(MovesString); TierlistWRdata.remove(MovesString);
                                      ObservableList<String> newTierlistWRdata = FXCollections.observableArrayList();
                                      newTierlistWRdata.clear();
                                      newTierlistWRdata=null;                  
                                      newTierlistWRdata= FXCollections.observableArrayList();
                                        System.err.println("TierlistWRdata= "+TierlistWRdata);
                                      for (Iterator<String> i = TierlistWRdata.iterator(); i.hasNext();) {
                                          String Listnext = i.next();
                                         // System.out.println("draftdata= "+Listnext);
                                        //  if (!Listnext.equals(db.getString())){
                                                //NewDraftData.add(Listnext);
                                          //}
                                            if (Listnext.equals(DroptorowitemString)){
                                                newTierlistWRdata.add(MovesString);
                                                newTierlistWRdata.add(DroptorowitemString);
                                              //NewDraftData.add(Listnext);
                                                System.out.println("add = "+ MovesString);
                                            }else{
                                                newTierlistWRdata.add(Listnext);
                                            }
                                         }
                                     // if(DroptorowitemString==null){
                                      //    newTierlistWRdata.add(MovesString);
                                     // }
                                      TierListviewWR.getItems().clear();
                                      TierlistWRdata=null;                                                    
                                      TierlistWRdata=FXCollections.observableArrayList(newTierlistWRdata);
                                     TierListviewWR.setItems(TierlistWRdata);

                                  }
                               } 
                                else if ( MovesString!=null && listCell.getItem().equals("")){
                                    System.err.println("Move to empty line");
                                    int x=listCell.getIndex()-1;
                                    DroptorowitemString=TierlistWRdata.get(x);

                                    MovesString=db.getString();
                                    if (!DroptorowitemString.equals(MovesString)){
                                        System.out.println("remove = "+ MovesString);

                                        System.out.println("target:"+ DroptorowitemString);

                                        TierlistWRdata.remove(MovesString);

                                          ObservableList<String> newTierlistWRdata = FXCollections.observableArrayList();
                                          newTierlistWRdata.clear();
                                          newTierlistWRdata=null;                  
                                          newTierlistWRdata= FXCollections.observableArrayList();

                                         for (Iterator<String> i = TierlistWRdata.iterator(); i.hasNext();) {
                                              String Listnext = i.next();
                                            //  System.out.println("draftdata= "+Listnext);
                                            //  if (!Listnext.equals(db.getString())){
                                                    //NewDraftData.add(Listnext);
                                              //}
                                                if (Listnext.equals(DroptorowitemString)){

                                                    newTierlistWRdata.add(DroptorowitemString);
                                                    newTierlistWRdata.add(MovesString);
                                                  //NewDraftData.add(Listnext);
                                                    System.out.println("add = "+ MovesString);
                                                }else{
                                                    newTierlistWRdata.add(Listnext);
                                                }
                                             }

                                         TierListviewWR.getItems().clear();
                                          TierlistWRdata=null;                                                    
                                          TierlistWRdata=FXCollections.observableArrayList(newTierlistWRdata);
                                         TierListviewWR.setItems(TierlistWRdata);
                                        System.err.println(TierListviewWR.getItems());
                                    }
                                }
                               else {
                                   event.setDropCompleted(false);
                               }
                            }
                         }
                               event.consume();
                           });


                    return listCell;
                }
                } );
        
    }
    
    
    public void QBlistviewCallback(){
        TierListviewQB.setCellFactory( new Callback<ListView<String>, ListCell<String>>()
            {
                @Override
                public ListCell<String> call( ListView<String> param )
                {
                    ListCell<String> listCell = new ListCell<String>()
                    {
                        @Override
                        protected void updateItem( String item, boolean empty )
                        {
                            
                            super.updateItem( item, empty );
                            //setText( item );
                            getStyleClass().add("TierListview_Player_RB");
                            getStyleClass().remove("TierListview_Color");
                            getStyleClass().remove("Tableview_Player_Drafted");
                            
                            getStyleClass().add("TierListview_Player_RB");
                            getStyleClass().remove("TierListview_Color");
                            getStyleClass().remove("Tableview_Player_Drafted");
                            
                            if (item == null || empty) {
                                setText(null);
                               // setGraphic(null);
                                //setStyle("");
                            } else {
                     
                                setText(item);
                                Pattern pattern = Pattern.compile("^Tier");
                                Matcher matcher = pattern.matcher(item);
                                if (matcher.find()){
                                    getStyleClass().add("TierListview_Color");
                                }      
                                pattern = Pattern.compile(" -DR");
                                matcher = pattern.matcher(item);
                                if (matcher.find()) {
                                   //setStyle("-fx-background-color: red");
                                   getStyleClass().add("Tableview_Player_Drafted");
                                }
                                
                                pattern = Pattern.compile("^Tier");
                                matcher = pattern.matcher(item);
                                if (matcher.find()){
                                    getStyleClass().add("TierListview_Color");
                                }      
                                pattern = Pattern.compile(" -DR");
                                matcher = pattern.matcher(item);
                                if (matcher.find()) {
                                   //setStyle("-fx-background-color: red");
                                   getStyleClass().add("Tableview_Player_Drafted");
                                }       
                                
                            }
                        }
                    };

                    
                    listCell.setOnDragDetected( ( MouseEvent event ) ->
                    {
                        String newaddString="";
                        System.out.println( "listcell setOnDragDetected" );
                        Dragboard db = listCell.startDragAndDrop( TransferMode.MOVE );
                        ClipboardContent content = new ClipboardContent();
                        if (!(listCell.getItem().contains("Tier") || listCell.getItem()=="")){
                            content.putString( listCell.getItem() );
                            db.setContent( content );
                            event.consume();           
                            dragSource.set(listCell);
                            dropToTlistRB=false;
                            dropToTlistWR=false;
                            dropToTlistQB=true;
                            dropToTlistTE=false;
                        }

                    } );

                    listCell.setOnDragOver( ( DragEvent event ) ->
                    {
                        Dragboard db = event.getDragboard();
                        if ( db.hasString() )
                        {
                            event.acceptTransferModes( TransferMode.MOVE );

                        }
                        event.consume();
                    } );

                    // listCell.setOnDragDone(event -> DraftListView.getItems().remove(listCell.getItem()));

                     listCell.setOnDragDropped(event -> {
                         if(dropToTlistQB){
                            dropToTlistQB=false;                         
                            if (posAvailable==false){
                                Dragboard db = event.getDragboard();
                                String MovesString=db.getString();
                                String DroptorowitemString=listCell.getItem();
                                 System.err.println("DROPTO= "+DroptorowitemString);
                               //if (db.hasString() && dragSource.get() != null) {
                                if (MovesString!=null && !listCell.getItem().equals("")) {
                                    if (!DroptorowitemString.contains("Tier") && !DroptorowitemString.equals(MovesString)){
                                   // in this example you could just do
                                   //DraftListView.getItems().add(db.getString());
                                   // but more generally:

            //                       ListCell<String> dragSourceCell = dragSource.get();
            //                       DraftListView.getItems().add(dragSourceCell.getItem());
            //                       event.setDropCompleted(true);
            //                       dragSource.set(null);

                                    System.out.println("remove = "+ MovesString);

                                    System.out.println("target:"+ DroptorowitemString);

                                    TierlistQBdata.remove(MovesString);

                                     // TierlistQBdata.remove(MovesString); TierlistQBdata.remove(MovesString);
                                      ObservableList<String> newTierlistQBdata = FXCollections.observableArrayList();
                                      newTierlistQBdata.clear();
                                      newTierlistQBdata=null;                  
                                      newTierlistQBdata= FXCollections.observableArrayList();

                                      for (Iterator<String> i = TierlistQBdata.iterator(); i.hasNext();) {
                                          String Listnext = i.next();
                                         // System.out.println("draftdata= "+Listnext);
                                        //  if (!Listnext.equals(db.getString())){
                                                //NewDraftData.add(Listnext);
                                          //}
                                            if (Listnext.equals(DroptorowitemString)){
                                                newTierlistQBdata.add(MovesString);
                                                newTierlistQBdata.add(DroptorowitemString);
                                              //NewDraftData.add(Listnext);
                                                System.out.println("add = "+ MovesString);
                                            }else{
                                                newTierlistQBdata.add(Listnext);
                                            }
                                         }
                                     // if(DroptorowitemString==null){
                                      //    newTierlistQBdata.add(MovesString);
                                     // }
                                      TierListviewQB.getItems().clear();
                                      TierlistQBdata=null;                                                    
                                      TierlistQBdata=FXCollections.observableArrayList(newTierlistQBdata);
                                     TierListviewQB.setItems(TierlistQBdata);

                                  }
                               } 
                                else if ( MovesString!=null && listCell.getItem().equals("")){
                                    System.err.println("Move to empty line");
                                    int x=listCell.getIndex()-1;
                                    DroptorowitemString=TierlistQBdata.get(x);

                                    MovesString=db.getString();
                                    if (!DroptorowitemString.equals(MovesString)){
                                        System.out.println("remove = "+ MovesString);

                                        System.out.println("target:"+ DroptorowitemString);

                                        TierlistQBdata.remove(MovesString);

                                          ObservableList<String> newTierlistQBdata = FXCollections.observableArrayList();
                                          newTierlistQBdata.clear();
                                          newTierlistQBdata=null;                  
                                          newTierlistQBdata= FXCollections.observableArrayList();

                                         for (Iterator<String> i = TierlistQBdata.iterator(); i.hasNext();) {
                                              String Listnext = i.next();
                                            //  System.out.println("draftdata= "+Listnext);
                                            //  if (!Listnext.equals(db.getString())){
                                                    //NewDraftData.add(Listnext);
                                              //}
                                                if (Listnext.equals(DroptorowitemString)){

                                                    newTierlistQBdata.add(DroptorowitemString);
                                                    newTierlistQBdata.add(MovesString);
                                                  //NewDraftData.add(Listnext);
                                                    System.out.println("add = "+ MovesString);
                                                }else{
                                                    newTierlistQBdata.add(Listnext);
                                                }
                                             }

                                         TierListviewQB.getItems().clear();
                                          TierlistQBdata=null;                                                    
                                          TierlistQBdata=FXCollections.observableArrayList(newTierlistQBdata);
                                         TierListviewQB.setItems(TierlistQBdata);
                                        System.err.println(TierListviewQB.getItems());
                                    }
                                }
                               else {
                                   event.setDropCompleted(false);
                               }
                            }
                         }
                               event.consume();
                      });
                    return listCell;
                }
                } );
    }
    public void TElistviewCallback(){
        TierListviewTE.setCellFactory( new Callback<ListView<String>, ListCell<String>>()
            {
                @Override
                public ListCell<String> call( ListView<String> param )
                {
                    ListCell<String> listCell = new ListCell<String>()
                    {
                        @Override
                        protected void updateItem( String item, boolean empty )
                        {
                            
                            super.updateItem( item, empty );
                            //setText( item );
                            getStyleClass().add("TierListview_Player_RB");
                            getStyleClass().remove("TierListview_Color");
                            getStyleClass().remove("Tableview_Player_Drafted");
                            
                            getStyleClass().add("TierListview_Player_RB");
                            getStyleClass().remove("TierListview_Color");
                            getStyleClass().remove("Tableview_Player_Drafted");
                            
                            if (item == null || empty) {
                                setText(null);
                               // setGraphic(null);
                                //setStyle("");
                            } else {
                     
                                setText(item);
                                Pattern pattern = Pattern.compile("^Tier");
                                Matcher matcher = pattern.matcher(item);
                                if (matcher.find()){
                                    getStyleClass().add("TierListview_Color");
                                }      
                                pattern = Pattern.compile(" -DR");
                                matcher = pattern.matcher(item);
                                if (matcher.find()) {
                                   //setStyle("-fx-background-color: red");
                                   getStyleClass().add("Tableview_Player_Drafted");
                                }
                                
                                pattern = Pattern.compile("^Tier");
                                matcher = pattern.matcher(item);
                                if (matcher.find()){
                                    getStyleClass().add("TierListview_Color");
                                }      
                                pattern = Pattern.compile(" -DR");
                                matcher = pattern.matcher(item);
                                if (matcher.find()) {
                                   //setStyle("-fx-background-color: red");
                                   getStyleClass().add("Tableview_Player_Drafted");
                                }       
                                
                            }
                        }
                    };

                    
                    listCell.setOnDragDetected( ( MouseEvent event ) ->
                    {           
                        String newaddString="";
                        System.out.println( "listcell setOnDragDetected" );
                        Dragboard db = listCell.startDragAndDrop( TransferMode.MOVE );
                        ClipboardContent content = new ClipboardContent();
                        if (!(listCell.getItem().contains("Tier") || listCell.getItem()=="")){
                            content.putString( listCell.getItem() );
                            db.setContent( content );
                            event.consume();           
                            dragSource.set(listCell);
                            dropToTlistRB=false;
                            dropToTlistWR=false;
                            dropToTlistQB=false;
                            dropToTlistTE=true;
                        }

                    } );

                    listCell.setOnDragOver( ( DragEvent event ) ->
                    {
                        Dragboard db = event.getDragboard();
                        if ( db.hasString() )
                        {
                            event.acceptTransferModes( TransferMode.MOVE );

                        }
                        event.consume();
                    } );

                    // listCell.setOnDragDone(event -> DraftListView.getItems().remove(listCell.getItem()));

                     listCell.setOnDragDropped(event -> {
                         if(dropToTlistTE){
                            dropToTlistTE=false;
                            if (posAvailable==false){
                                Dragboard db = event.getDragboard();
                                String MovesString=db.getString();
                                String DroptorowitemString=listCell.getItem();
                                 System.err.println("DROPTO= "+DroptorowitemString);
                               //if (db.hasString() && dragSource.get() != null) {
                                if (MovesString!=null && !listCell.getItem().equals("")) {
                                    if (!DroptorowitemString.contains("Tier") && !DroptorowitemString.equals(MovesString)){
                                   // in this example you could just do
                                   //DraftListView.getItems().add(db.getString());
                                   // but more generally:

            //                       ListCell<String> dragSourceCell = dragSource.get();
            //                       DraftListView.getItems().add(dragSourceCell.getItem());
            //                       event.setDropCompleted(true);
            //                       dragSource.set(null);

                                    System.out.println("remove = "+ MovesString);

                                    System.out.println("target:"+ DroptorowitemString);

                                    TierlistTEdata.remove(MovesString);

                                     // TierlistTEdata.remove(MovesString); TierlistTEdata.remove(MovesString);
                                      ObservableList<String> newTierlistTEdata = FXCollections.observableArrayList();
                                      newTierlistTEdata.clear();
                                      newTierlistTEdata=null;                  
                                      newTierlistTEdata= FXCollections.observableArrayList();

                                      for (Iterator<String> i = TierlistTEdata.iterator(); i.hasNext();) {
                                          String Listnext = i.next();
                                         // System.out.println("draftdata= "+Listnext);
                                        //  if (!Listnext.equals(db.getString())){
                                                //NewDraftData.add(Listnext);
                                          //}
                                            if (Listnext.equals(DroptorowitemString)){
                                                newTierlistTEdata.add(MovesString);
                                                newTierlistTEdata.add(DroptorowitemString);
                                              //NewDraftData.add(Listnext);
                                                System.out.println("add = "+ MovesString);
                                            }else{
                                                newTierlistTEdata.add(Listnext);
                                            }
                                         }
                                     // if(DroptorowitemString==null){
                                      //    newTierlistTEdata.add(MovesString);
                                     // }
                                      TierListviewTE.getItems().clear();
                                      TierlistTEdata=null;                                                    
                                      TierlistTEdata=FXCollections.observableArrayList(newTierlistTEdata);
                                     TierListviewTE.setItems(TierlistTEdata);

                                  }
                               } 
                                else if ( MovesString!=null && listCell.getItem().equals("")){
                                    System.err.println("Move to empty line");
                                    int x=listCell.getIndex()-1;
                                    DroptorowitemString=TierlistTEdata.get(x);

                                    MovesString=db.getString();
                                    if (!DroptorowitemString.equals(MovesString)){
                                        System.out.println("remove = "+ MovesString);

                                        System.out.println("target:"+ DroptorowitemString);

                                        TierlistTEdata.remove(MovesString);

                                          ObservableList<String> newTierlistTEdata = FXCollections.observableArrayList();
                                          newTierlistTEdata.clear();
                                          newTierlistTEdata=null;                  
                                          newTierlistTEdata= FXCollections.observableArrayList();

                                         for (Iterator<String> i = TierlistTEdata.iterator(); i.hasNext();) {
                                              String Listnext = i.next();
                                            //  System.out.println("draftdata= "+Listnext);
                                            //  if (!Listnext.equals(db.getString())){
                                                    //NewDraftData.add(Listnext);
                                              //}
                                                if (Listnext.equals(DroptorowitemString)){

                                                    newTierlistTEdata.add(DroptorowitemString);
                                                    newTierlistTEdata.add(MovesString);
                                                  //NewDraftData.add(Listnext);
                                                    System.out.println("add = "+ MovesString);
                                                }else{
                                                    newTierlistTEdata.add(Listnext);
                                                }
                                             }

                                         TierListviewTE.getItems().clear();
                                          TierlistTEdata=null;                                                    
                                          TierlistTEdata=FXCollections.observableArrayList(newTierlistTEdata);
                                         TierListviewTE.setItems(TierlistTEdata);
                                        System.err.println(TierListviewTE.getItems());
                                    }
                                }
                               else {
                                   event.setDropCompleted(false);
                               }
                            }
                         }
                               event.consume();
                      });
                    return listCell;
                }
                } );
    }
}
