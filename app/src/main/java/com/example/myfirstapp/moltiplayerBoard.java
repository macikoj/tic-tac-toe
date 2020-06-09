package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class moltiplayerBoard extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons =new Button[10][10];
    private boolean player1Turn=true;
    int playerType; // 0 to X, 1 to O
    int x;
    int y;
    int moves=0;
    private int playerScore;
    private int enemyScore;
    boolean madeMove=false;
    boolean jufstfinished=false;

    private static final int SERVERPORT = 5553;
    private static final String SERVER_IP = "13.48.204.168";
//    private static final String SERVER_IP ="192.168.245.187";
    private TextView textViewPlayerScore;
    private TextView textViewEnemyScore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moltiplayer_board);
        textViewPlayerScore = findViewById(R.id.textViewPlayerScore);
        textViewEnemyScore = findViewById(R.id.textViewEnemyScore);

        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                String buttonID="Button"+i+j;
                int resID=getResources().getIdentifier(buttonID,"id",getPackageName());
                buttons[i][j]=findViewById(resID);
                buttons[i][j].setOnClickListener((this));

            }

        }
        new Thread(new Client()).start();

    }


    class Client extends Thread {
        Handler handler = new Handler();
        public void run()
        {

            try
            {
                Socket socket = new Socket(SERVER_IP, SERVERPORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                DataInputStream din = new DataInputStream(socket.getInputStream());
                InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                final String playerNumber = din.readUTF();
                playerType=Integer.parseInt(playerNumber);
                while(true) {

                    Thread.sleep(100);
//                    System.out.println(madeMove);
                    if(madeMove) {
                        dout.writeUTF("," + String.valueOf(playerType) + "," + String.valueOf(x) + "," + String.valueOf(y));
                        madeMove = false;
                        System.out.println("wyslano");
                    }

                    if (din.available()!=0) {

                        String response = din.readUTF();

                        if (response != null) {

                            final String[] responserParted = response.split(",");
                            if (Integer.valueOf(responserParted[0]) != playerType) {
                                System.out.println("odebranno ruch przeciwnika "+responserParted[1]+","+responserParted[2]);
                                String b = "Button" + responserParted[1] + responserParted[2];
                                int resID = getResources().getIdentifier(b, "id", getPackageName());
                                final Button button = findViewById(resID);

                                handler.post(new Runnable(){
                                    public void run() {
                                        if(playerType==0)button.setText("O");
                                        else button.setText("X"); ;
                                        moves++;
                                        if (checkForWin(Integer.valueOf(responserParted[1]), Integer.valueOf(responserParted[2]))) {
                                            addEnemyVictory();
                                            resetBoard();
                                            jufstfinished=true;



                                        }
                                        if(moves==100){
                                            resetBoard();
                                            jufstfinished=true;
                                        }
                                    }
                                });
                                Thread.sleep(100);
                                if(jufstfinished){
                                    jufstfinished=false;
                                    player1Turn=true;
                                    System.out.println("cos");
                                    continue;
                                }
                                player1Turn = !player1Turn;
                            }
                        }
                    }
                }
            }
            catch(Exception e) {
               System.out.println(e);
                e.printStackTrace();
            }
        }
    }


    public void onClick(View v) {
        if((player1Turn && playerType==0) || (!player1Turn && playerType==1)) {
            if (!((Button) v).getText().toString().equals("")) {
                return;
            }
            if (player1Turn) {
                ((Button) v).setText("X");
            } else {
                ((Button) v).setText("O");
            }
            char[] idAsArray = ((Button) v).getResources().getResourceName(v.getId()).toCharArray();
            System.out.println(((Button) v).getResources().getResourceName(v.getId()));
            x = Character.getNumericValue(idAsArray[idAsArray.length - 2]);
            y = Character.getNumericValue(idAsArray[idAsArray.length - 1]);
            System.out.println(x);
            System.out.println(y);

            if (checkForWin(Character.getNumericValue(idAsArray[idAsArray.length - 2]), Character.getNumericValue(idAsArray[idAsArray.length - 1]))) {
                addVictory();

//        showVictoryToast();
                resetBoard();

            } else {
                player1Turn = !player1Turn;

            }
            madeMove = true;
        }
    }
    public void addVictory(){

        playerScore+=1;
        textViewPlayerScore.setText(Integer.toString(playerScore));



    }
    public void addEnemyVictory(){
        enemyScore+=1;
        textViewEnemyScore.setText(Integer.toString(enemyScore));
    }
    public void resetBoard(){
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                buttons[i][j].setText("");

            }
        }
        player1Turn=true;
        moves=0;
        if(playerType==0)playerType=1;
        else playerType=0;
    }
















    //    public void showVictoryToast(){Zyciężył gracz numer 1",Toast.LENGTH_SHORT).show();
//        else Toast.makeText(this,"Zyciężył gracz numer 2    ",Toast.LENGTH_SHORT).show();;
//    }
    public boolean checkForWin (int x, int y) {
        return (checkForWinUpAndDown(x,y)||checkForWinLeftAndRight(x,y)||
                checkForWinTopLeftAndDownRight(x,y)||checkForWinTopRightAndDownLeft(x,y));

    }
    public boolean checkForWinUpAndDown(int x, int y){
        int repeatTimes=0;
        boolean goDown=false;
        int i=1;
        while(repeatTimes<4){

            if(!goDown) {
                if (x - i < 0 || !buttons[x - i][y].getText().toString().equals(buttons[x][y].getText().toString())) {
                    goDown = true;
                    i = 1;
                    repeatTimes--;

                } else{
                    i++;
                }
            }else{
                if(x+i>9 || !buttons[x+ i][y].getText().toString().equals(buttons[x][y].getText().toString())){
                    return false;
                }else i++;
            }
            repeatTimes++;
        }
        return true;

    }
    public boolean checkForWinLeftAndRight(int x, int y){
        int repeatTimes=0;
        boolean goDown=false;
        int i=1;
        while(repeatTimes<4){

            if(!goDown) {
                if (y - i < 0 || !buttons[x][y-i].getText().toString().equals(buttons[x][y].getText().toString())) {
                    goDown = true;
                    i = 1;
                    repeatTimes--;
                } else{
                    i++;
                }
            }else{
                if(y+i>9 || !buttons[x][y+i].getText().toString().equals(buttons[x][y].getText().toString())){
                    return false;
                }else i++;
            }
            repeatTimes++;
        }
        return true;

    }
    public boolean checkForWinTopLeftAndDownRight(int x, int y){
        int repeatTimes=0;
        boolean goDown=false;
        int i=1;
        while(repeatTimes<4){

            if(!goDown) {
                if (y - i < 0 || x-i<0|| !buttons[x-i][y-i].getText().toString().equals(buttons[x][y].getText().toString())) {
                    goDown = true;
                    i = 1;
                    repeatTimes--;
                } else{
                    i++;
                }
            }else{
                if(y+i>9 || x+i>9|| !buttons[x+i][y+i].getText().toString().equals(buttons[x][y].getText().toString())){
                    return false;
                }else i++;
            }
            repeatTimes++;
        }
        return true;

    }
    public boolean checkForWinTopRightAndDownLeft(int x, int y){
        int repeatTimes=0;
        boolean goDown=false;
        int i=1;
        while(repeatTimes<4){

            if(!goDown) {
                if (y + i > 9 || x-i<0|| !buttons[x-i][y+i].getText().toString().equals(buttons[x][y].getText().toString())) {
                    goDown = true;
                    i = 1;
                    repeatTimes--;
                } else{
                    i++;
                }
            }else{
                if(y-i<0 || x+i>9|| !buttons[x+i][y-i].getText().toString().equals(buttons[x][y].getText().toString())){
                    return false;
                }else i++;
            }
            repeatTimes++;
        }
        return true;

    }
}

