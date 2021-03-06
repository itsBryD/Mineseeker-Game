package cmpt276.as3.assignment3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cmpt276.as3.assignment3.model.GameManager;
import cmpt276.as3.assignment3.model.OptionsData;

/**
 * Option Screen: let the user choose board size
 * and number of mines for the next game.
 * Has reset button to reset the number of games played and best scores to 0.
 */
public class OptionsActivity extends AppCompatActivity {

    private final int SIZE_OPTIONS = 3;
    private final int MINE_OPTIONS = 4;
    private final String TAG = "TAG_MSG";

    private OptionsData optionsData = OptionsData.getInstance();
    private GameManager gameManager = GameManager.getInstance();

    public static Intent launchIntent(Context c) {
        Intent intent = new Intent(c, OptionsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeInitialBars();
        setContentView(R.layout.activity_options);

        displayAllSpinners();
        setUpResetButton();
    }

    /**
     * Method to implement the reset button that allow user to reset the number of games played,
     * and best score for each game configuration.
     */
    private void setUpResetButton() {
        Button resetButton = findViewById(R.id.resetButton);

        resetButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(OptionsActivity.this);

            builder.setMessage("Are you sure to reset the number of game played and the best scores?")
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        gameManager.resetAllGames();
                        GameActivity.saveNumGames(0,this);
                        Toast.makeText(OptionsActivity.this,
                                "Number of games played reset to 0!",
                                Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Back", null);

            AlertDialog warning = builder.create();
            warning.show();
        });
    }

    /**
     * Remove the title bar at the top of the screen that contains
     * notifications, battery, etc.
     */
    //Source: https://www.youtube.com/watch?v=jOWW95u15S0&ab_channel=TechProjects
    private void removeInitialBars() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Calling method to create both size and mineNum Spinner
     */
    private void displayAllSpinners() {
        setUpSpinner(R.id.sizeSpinner, R.array.size);
        setUpSpinner(R.id.mineSpinner, R.array.mines);

        setSpinnerOptions();
        setSpinnerColor(R.id.sizeSpinner);
        setSpinnerColor(R.id.mineSpinner);
    }

    /**
     * Create a Spinner and fill it using a string array in strings.xml\
     * @param spinnerId is the spinner to fill
     * @param stringArrayId is the array of strings to fill the Spinner
     */
    private void setUpSpinner(int spinnerId, int stringArrayId) {
        Spinner spinner = findViewById(spinnerId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, stringArrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * When starting the activity, display the options that the user
     * chose previously (or default) on the spinner
     */
    private void setSpinnerOptions() {
        int currRowNum = getSaveNumRows(this);
        int currMineNum = getSaveNumMines(this);

        Spinner sizeSpinner = findViewById(R.id.sizeSpinner);
        Spinner mineSpinner = findViewById(R.id.mineSpinner);

        final int size4x6 = 0;
        final int size5x10 = 1;
        final int size6x15 = 2;

        switch(currRowNum) {
            case 4:
                sizeSpinner.setSelection(size4x6);
                break;
            case 5:
                sizeSpinner.setSelection(size5x10);
                break;
            case 6:
                sizeSpinner.setSelection(size6x15);
                break;
            default:
                Log.d(TAG, "Something went wrong with reading size");
                break;
        }

        final int sixMines = 0;
        final int tenMines = 1;
        final int fifteenMines = 2;
        final int twentyMines = 3;

        switch(currMineNum) {
            case 6:
                mineSpinner.setSelection(sixMines);
                break;
            case 10:
                mineSpinner.setSelection(tenMines);
                break;
            case 15:
                mineSpinner.setSelection(fifteenMines);
                break;
            case 20:
                mineSpinner.setSelection(twentyMines);
                break;
            default:
                Log.d(TAG, "Something went wrong with reading mine number");
                break;
        }
    }

    /**
     * Set the currently selected option of the Spinner to be white
     */
    private void setSpinnerColor(int spinnerId) {
        Spinner spinner = findViewById(spinnerId);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    /**
     * Return the index in the array of strings that matches with the String parameter
     * @param arrayId is the array of strings to check with
     * @param chosenItem is the String being checked
     */
    private int getStringArrayIdx(int arraySize, int arrayId, String chosenItem) {
        int chosenIdx = 0;
        for(int i = 0; i < arraySize; i++) {
            String currOption = getResources().getStringArray(arrayId)[i];
            if(chosenItem.equals(currOption)) {
                chosenIdx = i;
            }
        }
        return chosenIdx;
    }

    /**
     * Calling method to use Singleton class' setters for both mines and size
     */
    private void setOptions() {
        setSizeOptions();
        setMineOptions();
    }

    /**
     * Use the setters from the Singleton class to set the values for rows
     * and columns depending on the Spinner option chosen
     */
    private void setSizeOptions() {
        Spinner sizeSpinner = findViewById(R.id.sizeSpinner);
        String chosenSize = sizeSpinner.getSelectedItem().toString();
        int sizeIdx = getStringArrayIdx(SIZE_OPTIONS, R.array.size, chosenSize);

        int numRowChosen = 0;
        switch(sizeIdx) {
            case 0:
                optionsData.setRowNum(4);
                optionsData.setColumnNum(6);
                numRowChosen = 4;
                break;
            case 1:
                optionsData.setRowNum(5);
                optionsData.setColumnNum(10);
                numRowChosen = 5;
                break;
            case 2:
                optionsData.setRowNum(6);
                optionsData.setColumnNum(15);
                numRowChosen = 6;
                break;
            default:
                Log.d(TAG, "Something went wrong with reading the size index");
                break;
        }

        saveNumRows(numRowChosen);
    }

    private void saveNumRows(int numRowChosen) {
        SharedPreferences prefs = this.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Num Rows Chosen", numRowChosen);
        editor.apply();
    }

    public static int getSaveNumRows(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("Num Rows Chosen", 5);
    }

    /**
     * Use the setters from the Singleton class to set the value for
     * the number of mines depending on the Spinner option chosen
     */
    private void setMineOptions() {
        Spinner mineSpinner = findViewById(R.id.mineSpinner);
        String chosenMineNum = mineSpinner.getSelectedItem().toString();
        int mineNumIdx = getStringArrayIdx(MINE_OPTIONS, R.array.mines, chosenMineNum);

        int numMinesChosen = 0;
        switch(mineNumIdx) {
            case 0:
                optionsData.setMineNum(6);
                numMinesChosen = 6;
                break;
            case 1:
                optionsData.setMineNum(10);
                numMinesChosen = 10;
                break;
            case 2:
                optionsData.setMineNum(15);
                numMinesChosen = 15;
                break;
            case 3:
                optionsData.setMineNum(20);
                numMinesChosen = 20;
                break;
            default:
                Log.d(TAG, "Something went wrong with reading the mine index");
                break;
        }

        saveNumMines(numMinesChosen);
    }

    private void saveNumMines(int numMineChosen) {
        SharedPreferences prefs = this.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Num Mines Chosen", numMineChosen);
        editor.apply();
    }

    public static int getSaveNumMines(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("Num Mines Chosen", 10);
    }

    /**
     *  Call the setters of the Singleton class to save the options
     *  when leaving the options activity
     */
    @Override
    public void onBackPressed() {
        setOptions();
        super.onBackPressed();
    }
}