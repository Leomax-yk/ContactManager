package jp.mcinc.imesh.type.ipphone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.mcinc.imesh.type.ipphone.adapter.HistoryListItemAdapter;
import jp.mcinc.imesh.type.ipphone.model.HistroyListItemModel;

import jp.mcinc.imesh.type.ipphone.R;

import jp.mcinc.imesh.type.ipphone.database.DBManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryListActivity extends AppCompatActivity {
    private DBManager dbManager;
    private RecyclerView mRecyclerView;
    private TextView mTextNoData, mTextDelete, mTextYes, mTextNo;
    private ArrayList<HistroyListItemModel> mHistroyListItemModels;
    private HistoryListItemAdapter mHistoryListItemAdapter;
    private String TAG = getClass().getSimpleName();
    private LinearLayout mLinearMenu, mLinearDelete;
    private int menuSelection = 0, deleteSelection = 0;
    private boolean deleteVisible = false, menuVisible = false;
    private int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);
        getSupportActionBar().hide();
        mLinearMenu = findViewById(R.id.linear_menu);
        mLinearDelete = findViewById(R.id.linear_delete);
        mRecyclerView = findViewById(R.id.recycler_view_contact);
        mTextNoData = findViewById(R.id.text_no_data);
        mTextNoData.setText(getResources().getString(R.string.no_history_avaiable));
        mTextDelete = findViewById(R.id.text_delete);
        mTextYes = findViewById(R.id.text_yes);
        mTextNo = findViewById(R.id.text_no);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dbManager = new DBManager(this);
        dbManager.open();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mLinearMenu.getVisibility() != View.VISIBLE) {
            menuVisible = false;
            menuSelection = 0;
        }
        setHistoryData();

        mTextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSelection = 0;
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
    }

    private void setHistoryData() {
        // Getting list of contact from the DATABASE
        mHistroyListItemModels = dbManager.getHistoryListItem();
        if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
            mHistoryListItemAdapter = new HistoryListItemAdapter(this, mHistroyListItemModels);
            mRecyclerView.setAdapter(mHistoryListItemAdapter);
            mHistoryListItemAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextNoData.setVisibility(View.GONE);
        } else {
            // No contact available in DATABASE
            mRecyclerView.setVisibility(View.GONE);
            mTextNoData.setVisibility(View.VISIBLE);
        }
    }

    private void callDeleteMethod() {
        if (deleteSelection == 0) {
            if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
                dbManager.deleteHistory(mHistroyListItemModels.get(pos).getId());
                Intent i = new Intent(HistoryListActivity.this, DeleteSplashActivity.class);
                startActivity(i);
            } else {
                makeCall("No Contact to delete");
            }
        }
        showDeleteMenu();
        deleteVisible = !deleteVisible;
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

    private void callEvent() {
        showMenu();
        menuSelection = 0;
        if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
            showDeleteMenu();
            setMenuDelete();
            deleteVisible = !deleteVisible;
        } else {
            makeCall("Add Contact to delete");
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

    private void makeCall(String str) {
        if (str.equalsIgnoreCase("CALL")) {
            Date date = new Date();
            Log.e(TAG, "makeCall: " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
            Log.e(TAG, "makeCall: " + DateFormat.getTimeInstance().format(date));
            dbManager.insertHistory(3, mHistroyListItemModels.get(mHistoryListItemAdapter.getPos()).getOwnerName(), mHistroyListItemModels.get(mHistoryListItemAdapter.getPos()).getOwnerNumber(), "" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date), "" + DateFormat.getTimeInstance().format(date));
            setHistoryData();
        } else
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    private void iJustWantToScroll() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(pos);
            }
        }, 100);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //BACK
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
                if (mLinearMenu.getVisibility() == View.VISIBLE) {
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

                return true;
            case KeyEvent.KEYCODE_MENU: {
                //PICK CONTACT AND CALL
                showMenu();
            }
            return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //Left Key

                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //Right Key

                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //Down Key
                if (!menuVisible && !deleteVisible) {
                    if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
                        mHistroyListItemModels.get(pos).setCheck(false);
                        if ((mHistroyListItemModels.size() - 1) == pos) {
                            pos = 0;
                            mHistoryListItemAdapter.setPos(0);
                            mHistroyListItemModels.get(pos).setCheck(true);
                        } else {
                            pos = pos + 1;
                            mHistoryListItemAdapter.setPos((pos + 1));
                            mHistroyListItemModels.get(pos).setCheck(true);
                        }
                        mHistoryListItemAdapter = new HistoryListItemAdapter(this, mHistroyListItemModels);
                        mRecyclerView.setAdapter(mHistoryListItemAdapter);
                        mHistoryListItemAdapter.notifyDataSetChanged();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        makeCall("No call history");
                    }
                } else {
                    if (deleteVisible)
                        keyUpDowndelete();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                //Up Key
                if (!menuVisible && !deleteVisible) {
                    if (mHistroyListItemModels != null && mHistroyListItemModels.size() > 0) {
                        mHistroyListItemModels.get(pos).setCheck(false);
                        if (pos == 0) {
                            pos = mHistroyListItemModels.size() - 1;
                            mHistoryListItemAdapter.setPos(pos);
                            mHistroyListItemModels.get(pos).setCheck(true);
                        } else {
                            pos = pos - 1;
                            mHistoryListItemAdapter.setPos((pos));
                            mHistroyListItemModels.get(pos).setCheck(true);
                        }
                        mHistoryListItemAdapter = new HistoryListItemAdapter(this, mHistroyListItemModels);
                        mRecyclerView.setAdapter(mHistoryListItemAdapter);
                        mHistoryListItemAdapter.notifyDataSetChanged();
                        if (pos > 0)
                            iJustWantToScroll();
                    } else {
                        makeCall("No call history");
                    }
                } else {
                    if (deleteVisible)
                        keyUpDowndelete();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //Up Key
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

            case KeyEvent.KEYCODE_ENDCALL:
                //END CALL
                finish();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
