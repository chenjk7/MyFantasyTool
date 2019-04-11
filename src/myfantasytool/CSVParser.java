/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myfantasytool;
/**
 *
 * @author chenjk
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static myfantasytool.MyFantasyToolController.RankingFileAdddress;
public class CSVParser {
    
    public ObservableList<Playerlist> ExtractCSV(String filename) throws Exception{   
        int count = 0;
        String file = RankingFileAdddress;//CurrDir+"Fantasy_ranking.csv";
        ObservableList<Playerlist> content = FXCollections.observableArrayList();
        int cnt=1;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            while ((line = br.readLine()) != null) {                
                
                String [] playerlist = line.split(",");
                if (playerlist.length >=5){
//                System.out.println(playerlist[5]);
                    int i=0;
                    while(i<7){
                        playerlist[i]=playerlist[i].replaceAll("\"", "");
                        i++;
                    }
                    if (!playerlist[0].equals("Rank")){
                        Integer rank=Integer.parseInt(playerlist[0]);
                        String None=" ";
                        content.add(new Playerlist(rank, playerlist[2], playerlist[4], playerlist[3], playerlist[5], None,cnt));
                        cnt++;
                    }
                }
            }
            System.out.print("done");
        } catch (FileNotFoundException e) {
          //Some error logging
            System.out.println(e);
        }
        return content;
    }
}
