package com.example.myfirstapp;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import java.net.Socket;

public class board extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons =new Button[10][10];
    private boolean player1Turn=true;
    private int count=0;
    private int moves=0;

    private int playerScore;
    private int enemyScore;

    private TextView textViewPlayerScore;
    private TextView textViewEnemyScore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }
        if (player1Turn) {
            ((Button) v).setText("X");
        } else {
            ((Button) v).setText("O");
        }
        char[] idAsArray = ((Button) v).getResources().getResourceName(v.getId()).toCharArray();
        if(checkForWin(Character.getNumericValue(idAsArray[idAsArray.length - 2]), Character.getNumericValue(idAsArray[idAsArray.length - 1]))){
        addVictory();

//        showVictoryToast();
            resetBoard();
        }else{
            player1Turn= !player1Turn;
            moves++;
            if(moves==100){
                resetBoard();
            }

        }

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    public void addVictory(){
        if(player1Turn) {
            if(count%2==0){
                playerScore+=1;
                textViewPlayerScore.setText(Integer.toString(playerScore));
            }
            else{
                enemyScore+=1;
                textViewEnemyScore.setText(Integer.toString(enemyScore));
            }
        }
        else{
            if(playerScore%2==1) {
                enemyScore += 1;
                textViewEnemyScore.setText(Integer.toString(enemyScore));
            }else{
                playerScore+=1;
                textViewPlayerScore.setText(Integer.toString(playerScore));
            }
        }

    }
    public void resetBoard(){
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                buttons[i][j].setText("");

            }
        }
        moves=0;
        player1Turn=true;
        count++;
    }

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
