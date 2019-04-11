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
public class Playerlist {
    Integer Rank;
    String Player;
    String POS;
    String Team;
    String Bye;
    String Status;
    Integer LineNum;
    
    public Playerlist(Integer Rank,String Player,String POS,String Team,String Bye,String Status,Integer LineNum){
        super();
        this.Rank=Rank;
        this.Player=Player;
        this.POS=POS;
        this.Team=Team;
        this.Bye=Bye;
        this.Status=Status;
        this.LineNum=LineNum;
        }

    public Integer getRank(){
        return Rank;
    }    
    public String getPlayer(){
        return Player;
    }
    public String getPOS(){
        return POS;
    }
    public String getTeam(){
        return Team;
    }
    public String getBye(){
        return Bye;
    }
    public String getStatus(){
        return Status;
    }
    public Integer getLineNum(){
        return LineNum;
    }
    public void setPlayer(String Player){
        this.Player=Player;
    }  
    public void setRank(Integer Rank){
        this.Rank=Rank;
    }    
    public void setPOS(String POS){
        this.POS=POS;
    }    
    public void setTeam(String Team){
        this.Team=Team;
    }    
    public void setBye(String Bye){
        this.Bye=Bye;
    }    
    public void setStatus(String Status){
        this.Status=Status;
    }  
    public void setLineNum(Integer LineNum){
        this.LineNum=LineNum;
    }  
}
