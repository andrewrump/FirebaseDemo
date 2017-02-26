package dk.rump.andrew.firebasedemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDemoActivity extends AppCompatActivity implements View.OnClickListener {
    /*
    DISCLAIMER: This code is for demonstration only, i.e., it is simplified beyond usage
     */
    public final static String TAG = "FirebaseDemo";

    private final String RootDB = "dbRoot";
    private final String ClassDB = "dbClass";
    private final Integer rows = 3;
    private final Integer cols = 3;
    private final Integer deletes = 6;

    private Button commit_button;
    private Button fatal_button;
    private enum eChange { NoChange, LocalChange, RemoteChange };
    private EditText[][] editTexts = new EditText[cols][rows];
    private String[][] oldTexts = new String[cols][rows];
    //Button[] delete_buttons = new Button[deletes];
    private EditText stringEditText;
    private CheckBox booleanCheckBox;
    private EditText integerEditText;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRoot, dbClass;
    private FirebaseClass oldFirebaseClass, newFirebaseClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_demo);

        FirebaseCrash.log("Just testing Firebase exception handling...");
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));

        commit_button = (Button) findViewById(R.id.commit_button);
        fatal_button = (Button) findViewById(R.id.fatal_button);
        //delete_buttons[0] = (Button) findViewById(R.id.del11button);
        editTexts[0][0] = (EditText) findViewById(R.id.key11editText);
        editTexts[0][1] = (EditText) findViewById(R.id.value11key12editText);
        editTexts[0][2] = (EditText) findViewById(R.id.value12editText);
        //delete_buttons[1] = (Button) findViewById(R.id.del12button);
        //delete_buttons[2] = (Button) findViewById(R.id.del21button);
        editTexts[1][0] = (EditText) findViewById(R.id.key21editText);
        editTexts[1][1] = (EditText) findViewById(R.id.value21key22editText);
        editTexts[1][2] = (EditText) findViewById(R.id.value22editText);
        //delete_buttons[3] = (Button) findViewById(R.id.del22button);
        //delete_buttons[4] = (Button) findViewById(R.id.del31button);
        editTexts[2][0] = (EditText) findViewById(R.id.key31editText);
        editTexts[2][1] = (EditText) findViewById(R.id.value31key32editText);
        editTexts[2][2] = (EditText) findViewById(R.id.value32editText);
        //delete_buttons[5] = (Button) findViewById(R.id.del32button);

        stringEditText = (EditText) findViewById(R.id.class1editText);
        booleanCheckBox = (CheckBox) findViewById(R.id.class2checkBox);
        integerEditText = (EditText) findViewById(R.id.class3editText);

        oldFirebaseClass = new FirebaseClass();
        newFirebaseClass = new FirebaseClass();

        dbRoot = database.getReference(RootDB);
        dbRoot.addValueEventListener(new rootDBValueEventListener());
        dbClass = database.getReference(ClassDB);
        dbClass.addValueEventListener(new classDBValueEventListener());

        commit_button.setOnClickListener(this);
        fatal_button.setOnClickListener(this);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                oldTexts[i][j] = "";
            }
        }

        //for (int i = 0; i < deletes; i++) {
        //    delete_buttons[i].setOnClickListener(this);
        //}
    }

    @Override
    public void onClick(View view) {
        FirebaseCrash.log("FirebaseDemoActivity::onClick");

        if (view == commit_button) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    colourState(editTexts[i][j], editTexts[i][j].getText().toString().equals(oldTexts[i][j]) ? eChange.NoChange : eChange.LocalChange);
                    oldTexts[i][j] = editTexts[i][j].getText().toString();
                }
            }

            for (int i = 0; i < cols; i++) {
                if (editTexts[i][0].getText().length() > 0) {
                    DatabaseReference dbZero = dbRoot.child(editTexts[i][0].getText().toString());

                    if (editTexts[i][1].getText().length() > 0) {
                        if (editTexts[i][2].getText().length() > 0) {
                            dbZero
                                    .child(editTexts[i][1].getText().toString())
                                    .setValue(editTexts[i][2].getText().toString());
                        } else {
                            dbZero
                                    .setValue(editTexts[i][1].getText().toString());
                        }
                    }
                }
            }

            colourState(stringEditText, newFirebaseClass.getStringValue().equals(stringEditText.getText()) ? eChange.NoChange : eChange.LocalChange);
            colourState(booleanCheckBox, newFirebaseClass.getBooleanValue().equals(booleanCheckBox.isChecked()) ? eChange.NoChange : eChange.LocalChange);
            colourState(integerEditText, newFirebaseClass.getIntegerValue().toString().equals(integerEditText.getText()) ? eChange.NoChange : eChange.LocalChange);

            oldFirebaseClass = newFirebaseClass; // TODO Keep the value
            newFirebaseClass = null;
            newFirebaseClass = new FirebaseClass(
                    stringEditText.getText().toString(),
                    booleanCheckBox.isChecked(),
                    integerEditText.getText().toString()
                );
            dbClass.setValue(newFirebaseClass);
        } else {
            if (view == fatal_button) {
                newFirebaseClass = null;

                int x = newFirebaseClass.getIntegerValue();
            } else {
                //for (Integer i = 0; i < deletes; i++) {
                //    if (view == delete_buttons[i]) {
                //        // TODO Some clever math
                //        break;
                //    }
                //}
            }
        }
    }

    class rootDBValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChildren()) {
                for (DataSnapshot level1 : dataSnapshot.getChildren()) {
                    Log.d(TAG, "rootDBValueEventListener:onDataChange(1): " + level1.getKey());

                    // Find the row, where the value has been entered.
                    // DISCLAIMER Do not make programs like this!!! :-)
                    boolean found = false;
                    int row = rows - 1; // Start from the bottom
                    int empty = row;

                    for (; row >= 0; row--) { // Go backwards
                        if (editTexts[row][0].length() == 0) {
                            empty = row;
                        } else {
                            if (level1.getKey().equals(editTexts[row][0].getText().toString())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        row = empty; // We'll take the last anyways
                    }

                    editTexts[row][0].setText(level1.getKey().toString());
                    colourState(editTexts[row][0], level1.getKey().equals(oldTexts[row][0]) ? eChange.NoChange : eChange.RemoteChange);
                    oldTexts[row][0] = editTexts[row][0].getText().toString();

                    if (level1.hasChildren()) {
                        for (DataSnapshot level2 : level1.getChildren()) {
                            Log.d(TAG, "rootDBValueEventListener:onDataChange(2): " + level2.getKey());
                            editTexts[row][1].setText(level2.getKey().toString());
                            colourState(editTexts[row][1], level2.getKey().toString().equals(oldTexts[row][1]) ? eChange.NoChange : eChange.RemoteChange);
                            oldTexts[row][1] = editTexts[row][1].getText().toString();

                            if (level2.hasChildren()) { // Not supported  in this (badly written) demo code
                                for (DataSnapshot level3 : level2.getChildren()) {
                                    Log.w(TAG, "rootDBValueEventListener:onDataChange(3): " + level2.getKey());
                                }
                            } else {
                                Log.d(TAG, "rootDBValueEventListener:onDataChange(2): " + level2.getKey() + " = " + level2.getValue());
                                editTexts[row][2].setText(level2.getValue().toString());
                                colourState(editTexts[row][2], level2.getValue().toString().equals(oldTexts[row][2]) ? eChange.NoChange : eChange.RemoteChange);
                                oldTexts[row][2] = editTexts[row][2].getText().toString();
                            }
                        }
                    } else {
                        Log.d(TAG, "rootDBValueEventListener:onDataChange(1): " + level1.getKey() + " = " + level1.getValue());
                        editTexts[row][1].setText(level1.getValue().toString());
                        colourState(editTexts[row][1], level1.getValue().toString().equals(oldTexts[row][1]) ? eChange.NoChange : eChange.RemoteChange);
                        oldTexts[row][1] = editTexts[row][1].getText().toString();
                    }
                }
            } else {
                Log.e(TAG, "rootDBValueEventListener:onDataChange(0): " + dbClass + " has no children?!?");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "rootDBValueEventListener:onCancelled", databaseError.toException());
        }
    }

    class classDBValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                oldFirebaseClass = newFirebaseClass;

                newFirebaseClass = dataSnapshot.getValue(FirebaseClass.class);

                stringEditText.setText(newFirebaseClass.getStringValue());
                colourState(stringEditText, oldFirebaseClass.getStringValue().equals(newFirebaseClass.getStringValue()) ? eChange.NoChange : eChange.RemoteChange);
                booleanCheckBox.setChecked(newFirebaseClass.getBooleanValue());
                colourState(booleanCheckBox, oldFirebaseClass.getBooleanValue().equals(newFirebaseClass.getBooleanValue()) ? eChange.NoChange : eChange.RemoteChange);
                integerEditText.setText(newFirebaseClass.getIntegerValue().toString());
                colourState(integerEditText, oldFirebaseClass.getIntegerValue().equals(newFirebaseClass.getIntegerValue()) ? eChange.NoChange : eChange.RemoteChange);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "classDBValueEventListener:onCancelled", databaseError.toException());
        }
    }

    private void colourState(EditText editText, eChange change)
    {
        editText.setHintTextColor(change == eChange.NoChange ? Color.GRAY : Color.CYAN);
        editText.setTextColor(change == eChange.NoChange ? Color.BLACK : Color.WHITE);
        editText.setBackgroundColor(change == eChange.NoChange ? Color.GREEN : change == eChange.LocalChange ? Color.RED : Color.BLUE);
    }

    private void colourState(CheckBox checkBox, eChange change)
    {
        checkBox.setHintTextColor(change == eChange.NoChange ? Color.BLACK : Color.WHITE);
        checkBox.setTextColor(change == eChange.NoChange ? Color.BLACK : Color.WHITE);
        checkBox.setBackgroundColor(change == eChange.NoChange ? Color.GREEN : change == eChange.LocalChange ? Color.RED : Color.BLUE);
    }
}
