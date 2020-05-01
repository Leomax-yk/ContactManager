package jp.mcinc.imesh.type.ipphone.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.adapter.ContactListItemAdapter;
import jp.mcinc.imesh.type.ipphone.contants.Constants;
import jp.mcinc.imesh.type.ipphone.database.DBManager;
import jp.mcinc.imesh.type.ipphone.model.ContactListItemModel;
import jp.mcinc.imesh.type.ipphone.session.SessionManager;

import static jp.mcinc.imesh.type.ipphone.contants.Constants.CALL_SID_KEY;

//import com.google.common.collect.Lists;

public class ContactListActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private DBManager dbManager;
    private SessionManager sessionManager;
    private RecyclerView mRecyclerView;
    private TextView mTextNoData, mTextAdd, mTextEdit, mTextDelete, mTextMaintanance, mTextYes, mTextNo;
    private Button mButtonCall, mButtonHistory, mButtonBack;
    private ArrayList<ContactListItemModel> mContactListItemModel;
    private ContactListItemAdapter mContactListItemAdapter;
    private LinearLayout mLinearMenu, mLinearDelete;
    private ImageView mImageMenu, mImageDail;
    private int menuSelection = 0, deleteSelection = 0, pos = 0;
    private boolean deleteVisible = false, menuVisible = false, isReceiverRegistered = false;

    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
    private RecyclerViewClickListener recyclerViewClickListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        CALL_SID_KEY = sessionManager.getNumberSid();

        mRecyclerView = findViewById(R.id.recycler_view_contact);
        mLinearMenu = findViewById(R.id.linear_menu);
        mLinearDelete = findViewById(R.id.linear_delete);

        mImageMenu = findViewById(R.id.image_menu);
        mImageDail = findViewById(R.id.image_dail);

        mTextNoData = findViewById(R.id.text_no_data);
        mTextNoData.setText(getResources().getString(R.string.no_contact_avaiable));
        mTextAdd = findViewById(R.id.text_add);
        mTextEdit = findViewById(R.id.text_edit);
        mTextDelete = findViewById(R.id.text_delete);
        mTextYes = findViewById(R.id.text_yes);
        mTextNo = findViewById(R.id.text_no);
        mTextMaintanance = findViewById(R.id.text_maintanance);

        mButtonCall = findViewById(R.id.button_call);
        mButtonHistory = findViewById(R.id.button_history);
        mButtonBack = findViewById(R.id.button_back);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpi = metrics.densityDpi;
        dpi = dpi - 8;
        dpi = dpi / 2;
        mButtonCall.setPadding(dpi, 0, 0, 0);
        mButtonHistory.setPadding(dpi, 0, 0, 0);
        mButtonBack.setPadding(dpi, 0, 0, 0);
        dbManager = new DBManager(this);
        dbManager.open();
}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
         * Check if microphone permissions is granted
         */
        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mRecyclerView,
                        "Microphone permissions needed. Please allow in your application settings.",
                        Snackbar.LENGTH_LONG).show();
            } else {
//                mAccessToken = ACCESS_TOKEN;
                //GetAccessToken();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewClickListener = (view, position) -> {
            pos = position;
            makeCall("CALL");
        };

        if (mLinearMenu.getVisibility() != View.VISIBLE) {
            menuVisible = false;
            menuSelection = 0;
            setMenu();
        }

        // Getting list of contact from the DATABASE
        mContactListItemModel = dbManager.getContactListItem();
        if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
            mContactListItemAdapter = new ContactListItemAdapter(this, mContactListItemModel, recyclerViewClickListener);
            mRecyclerView.setAdapter(mContactListItemAdapter);
            mContactListItemAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);
            mButtonHistory.setVisibility(View.VISIBLE);
            mTextNoData.setVisibility(View.GONE);
            mButtonBack.setVisibility(View.GONE);
        } else {
            // No contact available in DATABASE
            mRecyclerView.setVisibility(View.GONE);
            mTextNoData.setVisibility(View.VISIBLE);
            mButtonBack.setVisibility(View.GONE);
            mButtonHistory.setVisibility(View.VISIBLE);
        }

        mButtonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("History");
                Intent i = new Intent(ContactListActivity.this, HistoryListActivity.class);
                startActivity(i);
            }
        });

        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mImageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMenu();
                showMenu();
            }
        });

        mTextAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 0;
                callEvent();
            }
        });

        mTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 1;
                callEvent();
            }
        });

        mTextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 2;
                callEvent();
            }
        });

        mTextMaintanance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 3;
                callEvent();
            }
        });

        mTextYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelection = 0;
                callDeleteMethod();
            }
        });

        mTextNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelection = 1;
                callDeleteMethod();
            }
        });

        mButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall("CALL");
            }
        });
    }

    public void makeCall(String str) {
        if (str.equalsIgnoreCase("CALL")) {
            if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                Date date = new Date();
                dbManager.insertHistory(3, mContactListItemModel.get(pos).getOwnerName(), mContactListItemModel.get(pos).getOwnerNumber(), "" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date), "" + DateFormat.getTimeInstance().format(date));
                callNumber(mContactListItemModel.get(pos).getOwnerNumber());
            } else {
                Toast.makeText(getApplicationContext(), "Add Contact to CALL", Toast.LENGTH_SHORT).show();
                callDailPad("0");
            }
        }
    }

    private void showToast(String message){
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    private void keyUpDownn(boolean isUp) {
        if (isUp) {
            if (menuSelection == 0)
                menuSelection = 3;
            else
                menuSelection = menuSelection - 1;
        } else {
            if (menuSelection == 3)
                menuSelection = 0;
            else
                menuSelection = menuSelection + 1;
        }
        setMenu();
    }

    private void setMenu() {
        mTextAdd.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextDelete.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextMaintanance.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (menuSelection == 0) {
            mTextAdd.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else if (menuSelection == 1) {
            mTextEdit.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else if (menuSelection == 2) {
            mTextDelete.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else if (menuSelection == 3) {
            mTextMaintanance.setBackgroundColor(Color.parseColor("#CCCCCC"));
        }
    }

    private void showMenu() {
        if (menuVisible) {
            mLinearMenu.setVisibility(View.GONE);
        } else {
            mLinearMenu.setVisibility(View.VISIBLE);
        }
        menuVisible = !menuVisible;
    }

    private void showDeleteMenu() {
        if (menuVisible) {
            showMenu();
        }
        if (deleteVisible) {
            mLinearDelete.setVisibility(View.GONE);
            deleteSelection = 0;
        } else {
            mLinearDelete.setVisibility(View.VISIBLE);
        }
    }

    private void keyUpDowndelete() {
        if (deleteSelection == 0)
            deleteSelection = 1;
        else
            deleteSelection = 0;
        setMenuDelete();
    }

    private void setMenuDelete() {
        mTextYes.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTextNo.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (deleteSelection == 0) {
            mTextYes.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else {
            mTextNo.setBackgroundColor(Color.parseColor("#CCCCCC"));
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyUp: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_0:
            case KeyEvent.KEYCODE_0:
                callDailPad("0");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_1:
            case KeyEvent.KEYCODE_1:
                callDailPad("1");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_2:
            case KeyEvent.KEYCODE_2:
                callDailPad("2");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3:
            case KeyEvent.KEYCODE_3:
                callDailPad("3");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_4:
            case KeyEvent.KEYCODE_4:
                callDailPad("4");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_5:
            case KeyEvent.KEYCODE_5:
                callDailPad("5");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_6:
            case KeyEvent.KEYCODE_6:
                callDailPad("6");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_7:
            case KeyEvent.KEYCODE_7:
                callDailPad("7");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_8:
            case KeyEvent.KEYCODE_8:
                callDailPad("8");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_9:
            case KeyEvent.KEYCODE_9:
                callDailPad("9");
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //Left Key
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //Right Key
                showToast("History");
                Intent i = new Intent(ContactListActivity.this, HistoryListActivity.class);
                startActivity(i);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //Down Key
                if (!menuVisible && !deleteVisible) {
                    if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                        mContactListItemModel.get(pos).setCheck(false);
                        if ((mContactListItemModel.size() - 1) == pos) {
                            pos = 0;
                            mContactListItemAdapter.setPos(0);
                            mContactListItemModel.get(pos).setCheck(true);
                        } else {
                            pos = pos + 1;
                            mContactListItemAdapter.setPos((pos + 1));
                            mContactListItemModel.get(pos).setCheck(true);
                        }
                        mContactListItemAdapter = new ContactListItemAdapter(this, mContactListItemModel, recyclerViewClickListener);
                        mRecyclerView.setAdapter(mContactListItemAdapter);
                        mContactListItemAdapter.notifyDataSetChanged();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        showToast("Add Contact");
                    }
                } else {
                    if (deleteVisible)
                        keyUpDowndelete();
                    else
                        keyUpDownn(false);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                //Up Key
                if (!menuVisible && !deleteVisible) {
                    if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                        mContactListItemModel.get(pos).setCheck(false);
                        if (pos == 0) {
                            pos = mContactListItemModel.size() - 1;
                            mContactListItemAdapter.setPos(pos);
                            mContactListItemModel.get(pos).setCheck(true);
                        } else {
                            pos = pos - 1;
                            mContactListItemAdapter.setPos((pos));
                            mContactListItemModel.get(pos).setCheck(true);
                        }
                        mContactListItemAdapter = new ContactListItemAdapter(this, mContactListItemModel, recyclerViewClickListener);
                        mRecyclerView.setAdapter(mContactListItemAdapter);
                        mContactListItemAdapter.notifyDataSetChanged();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        showToast("Add Contact");
                    }
                } else {
                    if (deleteVisible)
                        keyUpDowndelete();
                    else
                        keyUpDownn(true);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //Up Key
                if (menuVisible) {
                    Log.e(TAG, "onKeyUp: VISIBLE MENU");
                    callEvent();
                } else if (deleteVisible) {
                    callDeleteMethod();
                } else {
                    Log.e(TAG, "onKeyUp: NOTVISIBLE MENU");
                    makeCall("CALL");
                }
                return true;
            case KeyEvent.KEYCODE_MENU: {
                setMenu();
                showMenu();
            }
            return true;
            case KeyEvent.KEYCODE_BACK:
                //BACK
                if (menuVisible) {
                    showMenu();
                } else if (deleteVisible) {
                    showDeleteMenu();
                    deleteVisible = !deleteVisible;
                } else
                    finish();
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                //CLEAR
                //BACK
                if (menuVisible) {
                    showMenu();
                } else if (deleteVisible) {
                    showDeleteMenu();
                    deleteVisible = !deleteVisible;
                }
                return true;
            case KeyEvent.KEYCODE_CALL:
                //PICK CONTACT AND CALL
                makeCall("CALL");
                return true;
            case KeyEvent.KEYCODE_HOME:
                //PICK CONTACT AND CALL
                showToast("HOME");
                return true;
            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
//                makeEndCall();
                finish();
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void callDailPad(String value) {
        startActivity(new Intent(ContactListActivity.this, VoiceActivity.class).putExtra("num", "" + value).putExtra("call",false).setAction(Constants.OUTGOING_CALL_INVITE));
    }

    private void callNumber(String value) {
        startActivity(new Intent(ContactListActivity.this, VoiceActivity.class).putExtra("num", "" + value).putExtra("call",true).setAction(Constants.OUTGOING_CALL_INVITE));
    }

    private void iJustWantToScroll() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(pos);
            }
        }, 500);
    }

    private void callDeleteMethod() {
        if (deleteSelection == 0) {
            if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                dbManager.updateHistory(mContactListItemModel.get(pos).getOwnerName(), "UNKNOWN");
                dbManager.delete(mContactListItemModel.get(pos).getId());
                Intent i = new Intent(ContactListActivity.this, DeleteSplashActivity.class);
                startActivity(i);
            } else {
                showToast("Add Contact to delete");
            }
        }
        showDeleteMenu();
        deleteVisible = !deleteVisible;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void callEvent() {
        showMenu();
        if (menuSelection == 0) {
            Intent i = new Intent(ContactListActivity.this, AddContactActivity.class);
            startActivity(i);
        } else if (menuSelection == 1) {
            menuSelection = 0;
            if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                Gson gson = new Gson();
                Log.e(TAG, "callEvent: " + mContactListItemModel.get(pos).getOwnerName());
                String myJson = gson.toJson(mContactListItemModel.get(pos));
                Log.e(TAG, "callEvent: " + myJson);
                Intent i = new Intent(ContactListActivity.this, EditContactActivity.class);
                i.putExtra("user", myJson);
                startActivity(i);
            } else {
                showToast("Add Contact to edit");
            }
        } else if (menuSelection == 2) {
            menuSelection = 0;
            if (mContactListItemModel != null && mContactListItemModel.size() > 0) {
                showDeleteMenu();
                setMenuDelete();
                deleteVisible = !deleteVisible;
            } else {
                showToast("Add Contact to delete");
            }
        } else if (menuSelection == 3) {
            menuSelection = 0;
            showMenu();
            setMenu();
            Intent i = new Intent(ContactListActivity.this, CancelConfirmActivity.class);
            startActivity(i);
        }
    }
}
