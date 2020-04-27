package jp.mcinc.imesh.type.ipphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import jp.mcinc.imesh.type.ipphone.util.Validation;

import jp.mcinc.imesh.type.ipphone.R;

import jp.mcinc.imesh.type.ipphone.database.DBManager;

public class AddContactActivity extends AppCompatActivity {
    private DBManager dbManager;
    private LinearLayout mLinearSave, mLinearCancel;
    private Button mButtonSave, mButtonCancel;
    private EditText mEditName, mEditNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        getSupportActionBar().hide();

        mLinearSave = findViewById(R.id.linear_save);
        mLinearCancel = findViewById(R.id.linear_cancel);

        mButtonSave = findViewById(R.id.button_save);
        mButtonCancel = findViewById(R.id.button_cancel);

        mEditName = findViewById(R.id.edit_name);
        mEditNumber = findViewById(R.id.edit_number);

        //Open Database connection
        dbManager = new DBManager(this);
        dbManager.open();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLinearSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValidateAddContactToList();
            }
        });

        mLinearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValidateAddContactToList();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void isValidateAddContactToList() {
        if (Validation.validateString(mEditName.getText().toString())) {
            if (Validation.validateNameLength(mEditName.getText().toString())) {
                if (Validation.validateString(mEditNumber.getText().toString()) && Validation.isMobileNumberValid(mEditNumber.getText().toString())) {
                    dbManager.insert(mEditName.getText().toString().trim(), mEditNumber.getText().toString().trim());
                    Intent i = new Intent(AddContactActivity.this, ContactSavedActivity.class);
                    i.putExtra("contactstate",0);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Enter mobile number properly", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Enter minimum 3 character name", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Enter name properly", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //BACK
                finish();
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //Center key
//                View newView= this.getCurrentFocus();
//                if (newView!= null && newView.getId() != R.id.button_save) {
                    isValidateAddContactToList();
//                }if (newView!= null && newView.getId() != R.id.button_cancel) {
//                    finish();
//                }
                return true;
            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
                finish();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

}
