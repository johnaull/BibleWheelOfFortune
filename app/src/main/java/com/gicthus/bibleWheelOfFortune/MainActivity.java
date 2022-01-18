package com.gicthus.bibleWheelOfFortune;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gicthus.spinningwheel.SpinningWheelView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SpinningWheelView.OnRotationListener<String> {

    private SpinningWheelView wheelView;
    String letter;
    // Names of the players
    String player1 = null;
    String player2 = null;
    String[] guessed = new String[27]; //Letters that were guessed
    String[] answerAry = new String[200]; //Answer to the puzzle as an array of characters
    String[] solvedAry = new String[200]; //Letters that were right and *'s
    String[] allNames = new String[400]; // All the names that can be in a puzzle
    String[] nameInfo = new String[400]; // All the name info that can be in a puzzle
    String[] places = new String[132]; // All the places that can be in a puzzle
    String[] placeInfo = new String[132]; // All the name info that can be in a puzzle
    String[] phrases = new String[591]; // All the phrases that can be in a puzzle
    String[] phraseRefs = new String[591]; // All the phrase verse references that can be in a puzzle
    String[] players = new String[1000]; // Names of players in the game
    String[] puzType = {"NAME","PLACE","NAME","PHRASE"};
    // Wheel values for producing random spin result without showing the wheel.
    String[] wheelItems = {"600","600.","200","200.","700","700.","BANKRUPT","100","100.",
            "500","500.","800","800.","400","400.","1000","1000.","LOSE TURN"};
    int iPuzType;
    int lengthAnswer = 0;
    int letterVal = 0;
    int guessCounter = 0;  // Used for debugging, e.g. to force BANKRUPT on 2ndguess
    boolean solving = false; //true when they choose to solve rather than spin
    String solution; //Solution typed in by the user
    private TextView   puzzle;
    private TextView   puzzleType;
    private TextView   playr1;
    private TextView   playr2;
    private TextView   curPlayer1;
    private TextView   curPlayer2;
    private TextView   curScore1;
    private TextView   curScore2;
    private TextView   totScore1;
    private TextView   totScore2;
    private TextView   wheelVal;
    private TextView   welcomeTV;
    private EditText editText;
    private Button spinBtn ;
    private Button spinNoWheelBtn ;
    private Button solveBtn ;
    private Button nextPuzzleBtn;
    private TextView pickLetterTV;
    private TextView spinnerPrompt;
    private Spinner spinner2;
    private Button btnGetYesNo1;
    private Button btnGetYesNo2;
    private Button btnSubmit;


    private int   curScore1Amt=0;
    private int   curScore2Amt=0;
    private int   totScore1Amt=0;
    private int   totScore2Amt=0;
    private int nn =0; //No. of names
    private int np =0; //No. of places
    private int ns =0; //No. of phrases
    int noPlayers = 0; //No of previous player names found in players.txt.
    int curPlayer = 1;
    String answer; // The hidden answer to the puzzle;
    String answerInfo;  // Info about the answer for user's edification when they solve
    RadioGroup rg1;
    RadioGroup rg2;
    String yesNo=" "; // Either y or n
    TextView pickName ;
    List<String> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        iPuzType=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wheelView = (SpinningWheelView) findViewById(R.id.wheel);
        wheelView.setVisibility(View.GONE);

        pickLetterTV = (TextView)  findViewById(R.id.pickLetterTV);
        pickName = (TextView)  findViewById(R.id.pickName);

        wheelView.setItems(R.array.dummy);
        wheelView.setOnRotationListener(this);

        // Read in all the names that can be guessed.
        Button hideBtn = (Button) findViewById(R.id.hide);
        btnGetYesNo1 = (Button) findViewById(R.id.btnGetYesNo1);
        btnGetYesNo2 = (Button) findViewById(R.id.btnGetYesNo2);
        puzzle =  (TextView) findViewById(R.id.textView);
        puzzleType =  (TextView) findViewById(R.id.puzzleType);
        welcomeTV =  (TextView) findViewById(R.id.welcomeText);

        playr1 = (TextView) findViewById(R.id.player1);
        playr2 = (TextView) findViewById(R.id.player2);
        curPlayer1 = (TextView) findViewById(R.id.curPlayer1);
        curPlayer2 = (TextView) findViewById(R.id.curPlayer2);

        curScore1 = (TextView) findViewById(R.id.curScore1);
        curScore2 = (TextView) findViewById(R.id.curScore2);
        totScore1 = (TextView) findViewById(R.id.totScore1);
        wheelVal = (TextView) findViewById(R.id.wheelVal);
        totScore2 = (TextView) findViewById(R.id.totScore2);
        spinBtn = (Button) findViewById(R.id.show);
        spinNoWheelBtn  = (Button) findViewById(R.id.spinNoWheel);
        solveBtn = (Button) findViewById(R.id.solve);
        nextPuzzleBtn = (Button) findViewById(R.id.nextPuzzle);

        spinnerPrompt = (TextView)  findViewById(R.id.spinnerPrompt);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        curScore1.setText("");
        curScore2.setText("");
        totScore1.setText("");
        totScore2.setText("");
        wheelVal.setText("");


        rg1 = (RadioGroup) findViewById(R.id.radioYesNo1);
        rg2 = (RadioGroup) findViewById(R.id.radioYesNo2);
        rg2.setVisibility(View.GONE);

        //Hide other objects until names are entered
        hideBtn.setVisibility(View.GONE);
        puzzle.setVisibility(View.GONE);
        puzzleType.setVisibility(View.GONE);
        playr1.setVisibility(View.GONE);
        playr2.setVisibility(View.GONE);
        curPlayer1.setVisibility(View.GONE);
        curPlayer2.setVisibility(View.GONE);

        curScore2.setVisibility(View.GONE);
        totScore1.setVisibility(View.GONE);
        wheelVal.setVisibility(View.GONE);
        totScore2.setVisibility(View.GONE);
        spinBtn.setVisibility(View.GONE);
        spinNoWheelBtn.setVisibility(View.GONE);
        solveBtn.setVisibility(View.GONE);
        nextPuzzleBtn.setVisibility(View.GONE);
        spinnerPrompt.setVisibility(View.GONE);
        spinner2.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
        btnGetYesNo2.setVisibility(View.GONE);
        Button pickLetterBtn = (Button) findViewById(R.id.pickLetter);
        pickLetterBtn.setVisibility(View.GONE);


        // Listen for ENTER key being pressed in the keyboard
        editText = (EditText) findViewById(R.id.editText_main);
        editText.setVisibility(View.GONE);
        editText.setHint("");

        // Get all bible names
        try {
            String tempStr = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("nameOnly.txt")));
            int i = 1;
            tempStr = reader.readLine();
            while (tempStr != null) {
                String[] arrSplit = tempStr.split("~");
                allNames[i]=arrSplit[0];
                nameInfo[i]=arrSplit[1];
                i++;
                tempStr = reader.readLine();
            }
            nn=i-1;
            reader.close();
        } catch (Exception e){
            String you = "are here";
        }

        // Get all bible places
        try {
            String tempStr = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("places.txt")));
            int i = 1;
            tempStr = reader.readLine();
            while (tempStr != null) {
                String[] arrSplit = tempStr.split("~");
                places[i]=arrSplit[0];
                placeInfo[i]=arrSplit[1];
                i++;
                tempStr = reader.readLine();
            }
            np=i-1;
            reader.close();
        } catch (Exception e){
            String you = "are here";
        }

        // Get all bible phrases
        try {
            String tempStr = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("phrases.txt")));
            int i = 1;
            tempStr = reader.readLine();
             while (tempStr != null) {
                 String[] arrSplit = tempStr.split("~");
                 phraseRefs[i]=arrSplit[0];
                 phrases[i]=arrSplit[1];
                 i++;
                 tempStr = reader.readLine();
            }
            ns=i-1;
            reader.close();
        } catch (Exception e){
            String you = "are here";
        }

        //Get player names
        try{
            String tempStr = null;
            File myDir = getFilesDir();
            String myPath = myDir.getPath();
            File fileEvents = new File(myPath+"/players.txt");
            //File fileEvents = new File(myPath+"/bogus.txt");
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            tempStr = br.readLine();
             while (tempStr != null) {
                 noPlayers++;
                 list.add(tempStr);
                tempStr = br.readLine();
            }
            br.close();
        } catch (Exception e){
            String you = e.getMessage();
        }


        View view = getWindow().findViewById(Window.ID_ANDROID_CONTENT);

        //Get player 1
        welcomeTV.setVisibility(View.VISIBLE);
        String welcomeText = "Created by \"Gaming In Christ To Help Us Serve\" (" +
                "gicthus.inc@gmail.com) ";
        welcomeTV.setText(welcomeText);

        pickName.setTextSize(16);
        pickName.setText("Enter a new name for first player?");
        curPlayer1.setVisibility(View.GONE);

        askYesNo1(view);

        String theRest = "is dead code";

    }

    public void getPuzzle(View v){
        //iPuzType=3;
        solveBtn.setVisibility(View.GONE);  // Can't solve until at least 1 letter is guessed
        nextPuzzleBtn.setVisibility(View.GONE);
        wheelVal.setVisibility(View.GONE);
        if (iPuzType == 0 || iPuzType == 2){
            // Choose name at random
            Random randomGenerator = new Random();
            int n = randomGenerator.nextInt(nn-1)+1;
            answer=allNames[n].toUpperCase();
            answer="MOSES";
            answerInfo=nameInfo[n];
            puzzleType.setText(puzType[iPuzType]);
            lengthAnswer = answer.length();
        }

        if (iPuzType == 1 ){
            // Choose place at random
            Random randomGenerator = new Random();
            int n = randomGenerator.nextInt(np-1)+1;
            answer=places[n].toUpperCase();
            answerInfo=placeInfo[n];
            //answer="BBB";
            puzzleType.setText(puzType[iPuzType]);
            lengthAnswer = answer.length();
        }

        if (iPuzType == 3){
            // Choose phrase at random
            Random randomGenerator = new Random();
            int n = randomGenerator.nextInt(ns-1)+1;
            answer=phrases[n].toUpperCase();
            //answer="ABA";
            answerInfo = phraseRefs[n];
            //answer="AAA";
            //puzzleType.setText(puzType[iPuzType]+" = "+answer+"ref="+answerInfo);
            puzzleType.setText(puzType[iPuzType]);
            lengthAnswer = answer.length();
        }

        // Make solved array
        for (int x = 0; x < lengthAnswer; x++) {
            String tempLetter = answer.substring(x,x+1);

            if (!Character.isLetter(tempLetter.charAt(0))){ //Show characters that aren't letters
                solvedAry[x]=tempLetter;
            } else {
                solvedAry[x]="*";
            }
        }
        // Make answerAry array
        for (int x = 0; x < lengthAnswer; x++) {
            answerAry[x]=answer.substring(x,x+1);
        }


        boolean solved = false;
        // Empty out the guessed array
        for(int i = 1; i <= 26; i++ ) {
            guessed[i]="";
        }

        String tempStr = "";
        for (int  x = 0; x < lengthAnswer; x++) {
            tempStr=tempStr+solvedAry[x];
        }


        puzzle.setText(tempStr);


    }

    public void playGame(View v){
        if(player1==null || player2==null){
            return;
        }
        if (player2.equals(player1)){
            puzzleType.setText("Player 2 cannot be the same as Player 1.");
            puzzleType.setVisibility(View.VISIBLE);
            Button hideBtn = (Button) findViewById(R.id.hide);
            hideBtn.setVisibility(View.VISIBLE);
            return;

        }

        // Put names in array for sorting
        int i=0;
        for (String name:list) {
            players[i]=name;
            if(i>=999) {
                break;
            }
            i++;
        }
        noPlayers = i;
        //Remove all entries in list;
        list.clear();

        //Sort the array

        for( i = 0; i<noPlayers; i++) {
            for (int j = i+1; j<noPlayers; j++) {
                if(players[i].compareTo(players[j])>0) {
                    String temp = players[i];
                    players[i] = players[j];
                    players[j] = temp;
                }
            }
        }

        String you = "are here";

        // Put the sorted names back into the list.
        for( i = 0; i<noPlayers; i++) {
            list.add(players[i]);
        }
        // Write the list to the players.txt file.
        try {

            OutputStream os = openFileOutput("players.txt", MODE_PRIVATE);
            for (String name:list){
                os.write( name.getBytes());
                os.write( "\n".getBytes());
            }
            os.close();
        } catch (Exception e2) {
            e2.getMessage();
        }


        //EditText editText = (EditText) findViewById(R.id.editText_main);
        editText.setVisibility(View.GONE);
        //puzzle.setText("Names are " + player1 + " | " + player2);
        //puzzle.setText("*** todo ***");
        playr1.setVisibility(View.VISIBLE);
        playr2.setVisibility(View.VISIBLE);
        playr1.setText(player1);
        playr2.setText(player2);
        curPlayer1.setText("  ^");
        curPlayer2.setText("  ^");
        if(curPlayer==1){
            curPlayer1.setVisibility(View.VISIBLE);
            curPlayer2.setVisibility(View.INVISIBLE);
        } else {
            curPlayer1.setVisibility(View.INVISIBLE);
            curPlayer2.setVisibility(View.VISIBLE);
        }

        //Show other objects
        puzzle.setVisibility(View.VISIBLE);
        puzzleType.setVisibility(View.VISIBLE);
        curScore2.setVisibility(View.VISIBLE);
        totScore1.setVisibility(View.VISIBLE);
        wheelVal.setVisibility(View.VISIBLE);
        totScore2.setVisibility(View.VISIBLE);
        spinBtn.setVisibility(View.VISIBLE);
        spinNoWheelBtn.setVisibility(View.VISIBLE);

        hideKeyboard(v);

        getPuzzle(v);



    }
    public void checkLetter(View v){

        pickLetterTV.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        hideKeyboard(v);

        spinBtn.setVisibility(View.VISIBLE);
        spinNoWheelBtn.setVisibility(View.VISIBLE);
        solveBtn.setVisibility(View.VISIBLE);

        boolean solved = false;
        //Pick a letter
        //If letter was already picked then say so and switch
        boolean alreadyGuessed = false;
        for(int i = 1; i <= 26; i++ ) {
            if(guessed[i].equals("")){
                guessed[i]=letter;
                break;
            }

            if (letter.equals(guessed[i])){
                MediaPlayer mp = MediaPlayer.create(this,R.raw.realbad);
                mp.start();
                Toast.makeText(this, letter+" has been guessed already", Toast.LENGTH_LONG).show();
               alreadyGuessed=true;
                if (curPlayer == 1) {
                    curPlayer = 2;
                    curPlayer1.setVisibility(View.INVISIBLE);
                    curPlayer2.setVisibility(View.VISIBLE);
                } else {
                    curPlayer = 1;
                    curPlayer1.setVisibility(View.VISIBLE);
                    curPlayer2.setVisibility(View.INVISIBLE);
                }

                break;
            }
        }
        if (!alreadyGuessed) { //Look for the letter
            boolean matched = false;
            for (int x = 0; x < lengthAnswer; x++) {
                if (letter.equals(answerAry[x])) {
                    matched = true;
                    solvedAry[x] = letter;
                    if (curPlayer == 1) {
                        curScore1Amt = curScore1Amt + letterVal;
                        curScore1.setText("Cur: $" + curScore1Amt);
                    } else {
                        curScore2Amt = curScore2Amt + letterVal;
                        curScore2.setText("Cur: $" + curScore2Amt);
                    }
                }
            }

            //If letter is right then display it.  Otherwise switch.
            if(matched){
                String tempStr = "";
                for ( int j = 0; j < lengthAnswer; j++) {
                    tempStr=tempStr+solvedAry[j];
                }
                MediaPlayer mp = MediaPlayer.create(this,R.raw.right);
                mp.start();
                puzzle.setText(tempStr);

            } else {
                MediaPlayer mp = MediaPlayer.create(this,R.raw.realbad);
                mp.start();
                Toast.makeText(this, "No " + letter + " You lose your turn", Toast.LENGTH_LONG).show();
                if (curPlayer == 1) {
                    curPlayer = 2;
                    curPlayer1.setVisibility(View.INVISIBLE);
                    curPlayer2.setVisibility(View.VISIBLE);
                } else {
                    curPlayer = 1;
                    curPlayer1.setVisibility(View.VISIBLE);
                    curPlayer2.setVisibility(View.INVISIBLE);

                }
            }
            //Check whether the puzzle has been solved
            solved = true;
            for ( int x = 0; x < lengthAnswer; x++) {
                if (solvedAry[x].equals("*")){
                    solved=false;
                    break;
                }
            }
            if (solved) {
                MediaPlayer mp = MediaPlayer.create(this,R.raw.solved);
                mp.start();
                Toast.makeText(this, "The puzzle is solved", Toast.LENGTH_LONG).show();
                // Add winner's score on the current game to his total score.
                if (curPlayer==1){
                    totScore1Amt=totScore1Amt+curScore1Amt;
                    totScore1.setText("Tot: $"+totScore1Amt);
                } else {
                    totScore2Amt=totScore2Amt+curScore2Amt;
                    totScore2.setText("Tot: $"+totScore2Amt);
                }
                curScore1Amt=0;
                curScore2Amt=0;
                curScore1.setVisibility(View.VISIBLE);
                curScore2.setVisibility(View.VISIBLE);
                curScore1.setText("Cur: $0");
                curScore2.setText("Cur: $0");
                totScore1.setVisibility(View.VISIBLE);
                totScore2.setVisibility(View.VISIBLE);
                //Switch players after a win
                if (curPlayer == 1) {
                    curPlayer = 2;
                    curPlayer1.setVisibility(View.INVISIBLE);
                    curPlayer2.setVisibility(View.VISIBLE);
                } else {
                    curPlayer = 1;
                    curPlayer1.setVisibility(View.VISIBLE);
                    curPlayer2.setVisibility(View.INVISIBLE);

                }
                puzzleType.setText(answerInfo);


                if(iPuzType==3){
                    //Display verse reference
                    puzzleType.setText(answerInfo+".  GAME OVER");
                    //return;
                }

                //    answer="BBB";
                iPuzType++;

                //If another game is to be played then randomly choose the puzzle
                if(iPuzType==4){
                    Button hideBtn = (Button) findViewById(R.id.hide);
                    hideBtn.setVisibility(View.VISIBLE);
                    spinBtn.setVisibility(View.GONE);
                    spinNoWheelBtn.setVisibility(View.GONE);
                    solveBtn.setVisibility(View.GONE);

                } else {
                    spinBtn.setVisibility(View.GONE);
                    spinNoWheelBtn.setVisibility(View.GONE);
                    solveBtn.setVisibility(View.GONE);
                    nextPuzzleBtn.setVisibility(View.VISIBLE);

                }

            } else {
                //playGame(v);
            }





        }

    }

    public void nextPuzzle(View v){
        getPuzzle(v);
        playGame(v);

    }
    public void checkSolution(View v) {

        pickLetterTV.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        hideKeyboard(v);

        playr1.setVisibility(View.VISIBLE);
        playr2.setVisibility(View.VISIBLE);
        curPlayer1.setVisibility(View.VISIBLE);
        curPlayer2.setVisibility(View.VISIBLE);
        curScore1.setVisibility(View.VISIBLE);
        curScore2.setVisibility(View.VISIBLE);
        totScore1.setVisibility(View.VISIBLE);
        totScore2.setVisibility(View.VISIBLE);
        spinBtn.setVisibility(View.VISIBLE);
        spinNoWheelBtn.setVisibility(View.VISIBLE);
        solveBtn.setVisibility(View.VISIBLE);
        puzzleType.setVisibility(View.VISIBLE);

        solveBtn.setVisibility(View.VISIBLE);

        if (solution.equals(answer)) {
            Toast.makeText(this, "The puzzle is solved", Toast.LENGTH_LONG).show();
            // Add winner's score on the current game to his total score.
            puzzle.setText(answer);
            if (curPlayer==1){
                totScore1Amt=totScore1Amt+curScore1Amt;
                totScore1.setText("Tot: $"+totScore1Amt);
            } else {
                totScore2Amt=totScore2Amt+curScore2Amt;
                totScore2.setText("Tot: $"+totScore2Amt);
            }
            curScore1Amt=0;
            curScore2Amt=0;
            curScore1.setVisibility(View.VISIBLE);
            curScore2.setVisibility(View.VISIBLE);
            curScore1.setText("Cur: $0");
            curScore2.setText("Cur: $0");
            totScore1.setVisibility(View.VISIBLE);
            totScore2.setVisibility(View.VISIBLE);
            //Switch players after a win
            if (curPlayer == 1) {
                curPlayer = 2;
                curPlayer1.setVisibility(View.INVISIBLE);
                curPlayer2.setVisibility(View.VISIBLE);
            } else {
                curPlayer = 1;
                curPlayer1.setVisibility(View.VISIBLE);
                curPlayer2.setVisibility(View.INVISIBLE);
            }

            puzzleType.setText(answerInfo);

            if(iPuzType==3){
                //Display verse reference
                puzzleType.setText(answerInfo+".  GAME OVER");
                //return;
            }

            iPuzType++;

            //If another game is to be played then randomly choose the puzzle
            if(iPuzType==4){
                Button hideBtn = (Button) findViewById(R.id.hide);
                hideBtn.setVisibility(View.VISIBLE);
                spinBtn.setVisibility(View.GONE);
                spinNoWheelBtn.setVisibility(View.GONE);
                solveBtn.setVisibility(View.GONE);

            } else {
                spinBtn.setVisibility(View.GONE);
                spinNoWheelBtn.setVisibility(View.GONE);
                solveBtn.setVisibility(View.GONE);
                nextPuzzleBtn.setVisibility(View.VISIBLE);
           }

        } else {//Solution was wrong
            Toast.makeText(this, "No.  Solution is wrong.  You lose your turn.", Toast.LENGTH_LONG).show();
            if (curPlayer == 1) {
                curPlayer = 2;
                curPlayer1.setVisibility(View.INVISIBLE);
                curPlayer2.setVisibility(View.VISIBLE);
            } else {
                curPlayer = 1;
                curPlayer1.setVisibility(View.VISIBLE);
                curPlayer2.setVisibility(View.INVISIBLE);
            }

        }
    }

    public void systemExit (View view){
        System.exit(0);
    }



    public void showWheel(View v){
        wheelView.setVisibility(View.VISIBLE);
        wheelView.rotate(50, 3000, 50);
        String freeze = "wheel";
        //wheelView.setVisibility(View.INVISIBLE);
    }

    public void spinNoWheel(View v){
        //Toast.makeText(this,"COMING SOON.......", Toast.LENGTH_LONG).show();

        Random randomGenerator = new Random();
        int n = randomGenerator.nextInt(15)+1;
        String item = wheelItems[n];
        onStopRotation(item);
    }

    public void pickLetter(View v){
        //EditText editText = (EditText) findViewById(R.id.editText_main);
        guessCounter++;
        editText.setVisibility(View.VISIBLE);

        editText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {

                    switch (keyCode)
                    {
                        //case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if(solving){
                                solution=editText.getText().toString().toUpperCase();
                                editText.setText(null);
                                checkSolution(v);
                            } else {
                                String showString = editText.getText().toString();
                                if(showString.length()<1){
                                    return false;
                                }
                                editText.setText(null);
                                letter=showString.substring(0,1).toUpperCase();
                                wheelVal.setVisibility(View.GONE);
                                checkLetter(v);
                            }

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onRotation() {

        Log.d("XXXX", "On Rotation");
    }

    @Override
    public void onStopRotation(String item) {
        //Toast.makeText(this, item, Toast.LENGTH_LONG).show();
        //Pause so wheel can be seen before it vanishes
        try{
            Thread.sleep(1000);
        } catch (Exception e) {
            // Can't sleep?
        }
        wheelView.setVisibility(View.GONE);
        if(item.equals("BANKRUPT")){
            MediaPlayer mp = MediaPlayer.create(this,R.raw.realbad);
            mp.start();
            Toast.makeText(this,  "BANKRUPT!!!", Toast.LENGTH_LONG).show();
            if (curPlayer == 1) {
                curScore1Amt=0;
                curScore1.setText("Cur: $"+curScore1Amt);
                curPlayer = 2;
                curPlayer1.setVisibility(View.INVISIBLE);
                curPlayer2.setVisibility(View.VISIBLE);
            } else {
                curScore2Amt=0;
                curScore2.setText("Cur: $"+curScore2Amt);
                curPlayer = 1;
                curPlayer1.setVisibility(View.VISIBLE);
                curPlayer2.setVisibility(View.INVISIBLE);

            }
            return;
        } else if (item.equals("LOSE TURN")){
            MediaPlayer mp = MediaPlayer.create(this,R.raw.realbad);
            mp.start();
            Toast.makeText(this,  "YOUR TURN IS OVER!!!", Toast.LENGTH_LONG).show();
            if (curPlayer == 1) {
                curPlayer = 2;
                curPlayer1.setVisibility(View.INVISIBLE);
                curPlayer2.setVisibility(View.VISIBLE);
            } else {
                curPlayer = 1;
                curPlayer1.setVisibility(View.VISIBLE);
                curPlayer2.setVisibility(View.INVISIBLE);
            }
            return;
        } else {
            //Take out decimal place which is used on items that are not displayed on the wheel
            item = item.replace(".","");
            letterVal = Integer.parseInt(item);
        }
        wheelVal.setText("Letter is worth $" + letterVal);
        wheelVal.setVisibility(View.VISIBLE);
        spinBtn.setVisibility(View.GONE);
        spinNoWheelBtn.setVisibility(View.GONE);
        solveBtn.setVisibility(View.GONE);
        TextView pickLetterTV = (TextView)  findViewById(R.id.pickLetterTV);
        pickLetterTV.setVisibility(View.VISIBLE);
        pickLetterTV.setText("Pick a letter.");
        View view = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        solving=false;
        pickLetter(view);


    }
    public void solve(View view){
        TextView pickLetterTV = (TextView)  findViewById(R.id.pickLetterTV);
        pickLetterTV.setVisibility(View.VISIBLE);
        pickLetterTV.setText("Solve the puzzle.");
        solving=true;
        // Get rid of names and scores so puzzle can be seen when entering solution
        playr1.setVisibility(View.GONE);
        playr2.setVisibility(View.GONE);
        curPlayer1.setVisibility(View.GONE);
        curPlayer2.setVisibility(View.GONE);
        curScore1.setVisibility(View.GONE);
        curScore2.setVisibility(View.GONE);
        totScore1.setVisibility(View.GONE);
        totScore2.setVisibility(View.GONE);
        spinBtn.setVisibility(View.GONE);
        spinNoWheelBtn.setVisibility(View.GONE);
        solveBtn.setVisibility(View.GONE);
        //pickLetterTV.setVisibility(View.GONE);
        puzzleType.setVisibility(View.GONE);


        pickLetter(view);

    }

    public void addItemsOnSpinner2() {

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        //list.add("Andy");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }


    // get the selected dropdown list value
    public void addListenerOnButton1(View v) {

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String yourChoice=null;
                yourChoice = String.valueOf(spinner2.getSelectedItem());
                player1=yourChoice;

                spinnerPrompt.setVisibility(View.GONE);
                spinner2.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                pickName.setTextSize(24);
                pickName.setText("Enter a new name for SECOND player?");
                askYesNo2(v);
            }

        });
    }
    // get the selected dropdown list value
    public void addListenerOnButton2(View v) {

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String yourChoice=null;
                yourChoice = String.valueOf(spinner2.getSelectedItem());
                player2=yourChoice;
                spinnerPrompt.setVisibility(View.GONE);
                spinner2.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                playGame(v);
            }

        });
    }

    public void askYesNo1 (View v) {
        pickName.setVisibility(View.VISIBLE);
        rg1.setVisibility(View.VISIBLE);
        btnGetYesNo1.setVisibility(View.VISIBLE);
        String should = "wait here for an answer";
    }


    public void askYesNo2 (View v) {
        editText.setText(null);
        editText.setVisibility(View.GONE);

        pickName.setVisibility(View.VISIBLE);
        rg2.setVisibility(View.VISIBLE);
        btnGetYesNo2.setVisibility(View.VISIBLE);
        String should = "wait here for an answer";
    }

    public void getYesNo1 (View v) {
        pickName.setVisibility(View.GONE);
        rg1.setVisibility(View.GONE);
        btnGetYesNo1.setVisibility(View.GONE);
        welcomeTV.setVisibility(View.GONE);

        int selectedId = rg1.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        RadioButton selectedButton = (RadioButton) findViewById(selectedId);
        String selectedText = (String) selectedButton.getText();
        if(selectedText.startsWith("Y")){
            yesNo="y";
        } else {
            yesNo="n";
        }
        if (yesNo.equals("n")){
            if(noPlayers==0){
                Toast.makeText(this, "No players have ever been entered.", Toast.LENGTH_LONG).show();
                pickName.setVisibility(View.VISIBLE);
                rg1.setVisibility(View.VISIBLE);
                btnGetYesNo1.setVisibility(View.VISIBLE);
                return;
            }
            yesNo="";
            spinnerPrompt.setVisibility(View.VISIBLE);
            spinner2.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            addItemsOnSpinner2();
            addListenerOnButton1(v);


            askYesNo2(v);
        } else {
            // Enter new name

            pickName.setVisibility(View.VISIBLE);
            pickName.setText("Enter a new name (15 characters or less).");
            editText.setVisibility(View.VISIBLE);
            editText.setOnKeyListener(new View.OnKeyListener()
            {
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {

                        switch (keyCode)
                        {
                            //case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                    player1 = editText.getText().toString().trim().toUpperCase();
                                    if(player1.length()>15) {
                                        player1 = player1.substring(0, 15);
                                    }
                                    editText.setText(null);
                                    editText.setVisibility(View.GONE);
                                    addPlayerToList(player1);
                                    hideKeyboard(v);
                                    //Save the player list
                                    try {
                                        OutputStream os = openFileOutput("players.txt", MODE_PRIVATE);
                                        for (String name:list){
                                            os.write( name.getBytes());
                                            os.write( "\n".getBytes());
                                        }
                                        os.close();
                                    } catch (Exception e2) {

                                    }


                                //pickName.setText("CEnter a new name for second player?");
                                pickName.setTextSize(24);
                                pickName.setText("Enter a new name for SECOND player?");
                                    askYesNo2(v);

                                return true;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });

        }
    }

    public void  addPlayerToList(String newPlayer){
        // If player is already in the list then don't add it.
        for (String name:list){
            if (newPlayer.equals(name)){
                Toast.makeText(this, "Name has already been entered", Toast.LENGTH_LONG).show();
                return;
            }
        }
        list.add(newPlayer);
    };

    public void getYesNo2 (View v) {
        pickName.setVisibility(View.GONE);
        rg2.setVisibility(View.GONE);
        btnGetYesNo2.setVisibility(View.GONE);
        playr1.setVisibility(View.GONE);
        curPlayer1.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);


        int selectedId = rg2.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        RadioButton selectedButton = (RadioButton) findViewById(selectedId);
        String selectedText = (String) selectedButton.getText();
        if(selectedText.startsWith("Y")){
            yesNo="y";
        } else {
            yesNo="n";
        }
        if (yesNo.equals("n")){
            yesNo="";
            spinnerPrompt.setVisibility(View.VISIBLE);
            spinner2.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            addItemsOnSpinner2();
            addListenerOnButton2(v);
            playGame(v);
        } else {
            // Enter new name
            pickName.setVisibility(View.VISIBLE);
            pickName.setText("Enter a new name (15 characters or less).");
            editText.setVisibility(View.VISIBLE);
            editText.setOnKeyListener(new View.OnKeyListener()
            {
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {

                        switch (keyCode)
                        {
                            //case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                player2 = editText.getText().toString().trim().toUpperCase();
                                if(player2.length()>15) {
                                    player2 = player2.substring(0, 15);
                                }
                                addPlayerToList(player2);
                                editText.setText(null);
                                editText.setVisibility(View.GONE);
                                pickName.setVisibility(View.GONE);

                                playGame(v);
                                return true;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });
        }



    }

}
