package jp.mcinc.imesh.type.ipphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import jp.mcinc.imesh.type.ipphone.model.ContactListItemModel;
import jp.mcinc.imesh.type.ipphone.util.Validation;

import com.google.gson.Gson;
import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.database.DBManager;

public class EditContactActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private DBManager dbManager;
    private LinearLayout mLinearSave, mLinearCancel;
    private EditText mEditName, mEditNumber;
    private ContactListItemModel mContactListItemModel;
    private Button mButtonSave, mButtonCancel;
    private int focus = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        getSupportActionBar().hide();
        Gson gson = new Gson();
        Log.e(TAG, "onCreate: " + getIntent().getStringExtra("user"));
        mContactListItemModel = gson.fromJson(getIntent().getStringExtra("user"), ContactListItemModel.class);

        mLinearSave = findViewById(R.id.linear_save);
        mLinearCancel = findViewById(R.id.linear_cancel);

        mButtonSave = findViewById(R.id.button_save);
        mButtonCancel = findViewById(R.id.button_cancel);

        mEditName = findViewById(R.id.edit_name);
        mEditNumber = findViewById(R.id.edit_number);

        mEditName.setText("" + mContactListItemModel.getOwnerName());
        mEditNumber.setText("" + mContactListItemModel.getOwnerNumber());
        mEditNumber.post(new Runnable() {
            @Override
            public void run() {
                mEditNumber.setSelection(mEditNumber.getText().length());
                mEditName.requestFocus();
            }
        });
        mEditName.post(new Runnable() {
            @Override
            public void run() {
                mEditName.setSelection(mEditName.getText().length());
                mEditName.requestFocus();
            }
        });
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
                    dbManager.updateHistory(mContactListItemModel.getOwnerName(), mEditName.getText().toString());
                    dbManager.update(mContactListItemModel.getId(), mEditName.getText().toString(), mEditNumber.getText().toString());
                    Intent i = new Intent(EditContactActivity.this, ContactSavedActivity.class);
                    i.putExtra("contactstate",1);
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
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //Center key
                if (focus == 1) {
                    mEditNumber.requestFocus();
                    focus = 2;
                } else if (focus == 2) {
                    mButtonSave.requestFocus();
                    focus = 3;
                } else if (focus == 3) {
                    isValidateAddContactToList();
                } else if (focus == 4) {
                    finish();
                }
                return true;
            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
                finish();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN: {
                if (focus == 1) {
                    mEditNumber.requestFocus();
                    focus = 2;
                } else if (focus == 2) {
                    mButtonSave.requestFocus();
                    focus = 3;
                } else if (focus == 3) {
                    mButtonCancel.requestFocus();
                    focus = 4;
                } else if (focus == 4) {
                    mEditName.requestFocus();
                    focus = 1;
                }
            }
            return true;
            case KeyEvent.KEYCODE_DPAD_UP: {
//                Toast.makeText(getApplicationContext(), "up "+focus, Toast.LENGTH_SHORT).show();
                if (focus == 1) {
                    mButtonCancel.requestFocus();
                    focus = 4;
                } else if (focus == 2) {
                    mEditName.requestFocus();
                    focus = 1;
                } else if (focus == 3) {
                    mEditNumber.requestFocus();
                    focus = 2;
                } else if (focus == 4) {
                    mButtonSave.requestFocus();
                    focus = 3;
                }
            }
            return true;
            case KeyEvent.KEYCODE_HOME:
                //END CALL

                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

}
