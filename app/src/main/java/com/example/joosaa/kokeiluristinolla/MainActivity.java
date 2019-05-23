package com.example.joosaa.kokeiluristinolla;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons = new Button [3][3];

    private boolean player1Turn = true;

    private int roundCount;

    private Switch switchMute;

    private int player1Points;
    private int player2Points;

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    private RelativeLayout relativeLayoutInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Tap player 1 or Player 2 to change background color", Toast.LENGTH_LONG).show();

        //background music
        final Switch switchMute = this.findViewById(R.id.switch_mute);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.zelda_2_palace_theme);
        switchMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the music is playing
                if(mediaPlayer.isPlaying() == true) {
                    // Pause the music player
                    mediaPlayer.pause();
                    switchMute.setTextOn("Play song On");
                    // If it's not playing
                }else {
                    // Resume the music player
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                    switchMute.setTextOn("Play song On");
                }
            }
        });

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        relativeLayoutInfo = findViewById(R.id.info_box);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j; //this loops all button id's
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID); //i = 00,01,02 and j=10,11,12
                buttons[i][j].setOnClickListener(this);
            }
        }

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();

                // TODO : change to reset preferences later
                findViewById(R.id.info_box).setBackgroundColor(Color.TRANSPARENT);
            }
        });

        textViewPlayer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutInfo.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                storeColor(getResources().getColor(R.color.colorGreen));
            }
        });

        textViewPlayer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutInfo.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                storeColor(getResources().getColor(R.color.colorPurple));
            }
        });
    }

    @Override
    public void onClick(View v) { // this is called if any button is pressed
        if (!((Button) v) .getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            ((Button) v).setText("X");
            ((Button) v).setTextColor(getResources().getColor(R.color.colorGreen));
        } else {
            ((Button) v).setText("0");
            ((Button) v).setTextColor(getResources().getColor(R.color.colorPurple));
        }

        roundCount++;

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn; //if no winner and no draw -->switch turns
        }
    }

    //color preferences
    private void storeColor(int color) {
        SharedPreferences mSharedPreferences = getSharedPreferences("BackgroundColor", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt("color", color);
        mEditor.apply();
    }

    private int getColor() {
        SharedPreferences mSharedPreferences = getSharedPreferences("BackgroundColor", MODE_PRIVATE);
        int selectedColor = mSharedPreferences.getInt("color", getResources().getColor(R.color.colorPrimary));
        return selectedColor;
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) { //goes through all buttons and saves them to this string array
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i ++) {
            if (field[i][0].equals(field[i][1])        //compare row fields
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {      //if there's an empty string, no winner yet
                return true;
            }
        }

        for (int i = 0; i < 3; i ++) {
            if (field[0][i].equals(field[1][i])        //compare column fields
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {      //if there's an empty string, no winner yet
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) //compare "top left-to-bottom right" diagonal fields
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {      //if there's an empty string, no winner yet
            return true;
        }

        if (field[0][2].equals(field[1][1]) //compare "bottom left-to-top right" diagonal fields
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")) {      //if there's an empty string, no winner yet
            return true;
        }

        return false; //if above doesn't happen, the game continues
    }

    private void player1Wins() {
        player1Points++;
        Toast.makeText(this, "Player 1 WINS!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void player2Wins() {
        player2Points++;
        Toast.makeText(this, "Player 2 WINS!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void draw() {
        Toast.makeText(this, "DRAW", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePointsText() {
        textViewPlayer1.setText("Player 1 : " + player1Points);
        textViewPlayer2.setText("Player 2 : " + player2Points);
    }

    private void resetBoard() { //all buttons to an empty string
        for (int i = 0; i < 3; i++) {
            for (int j= 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }

        roundCount = 0;
        player1Turn = true; // player 1 starts a new game
    }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        resetBoard();
        //reset color preferences back to default values

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //this method saves these values when device is turned
        outState.putInt("roundCount", roundCount);
        outState.putInt("player1Points", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putBoolean("player1Turn", player1Turn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        roundCount = savedInstanceState.getInt("roundCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Turn = savedInstanceState.getBoolean("player1Turn");
    }
}
