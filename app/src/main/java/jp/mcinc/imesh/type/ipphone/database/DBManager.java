package jp.mcinc.imesh.type.ipphone.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import jp.mcinc.imesh.type.ipphone.model.ContactListItemModel;
import jp.mcinc.imesh.type.ipphone.model.HistroyListItemModel;

import java.util.ArrayList;
import java.util.Collections;

public class DBManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private ArrayList<ContactListItemModel> mContactListItemModels;
    private ArrayList<HistroyListItemModel> mHistroyListItemModels;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String name, String number) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.OWNERNAME, name);
        contentValue.put(DatabaseHelper.OWNERNUMBER, number);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    // 1. MissCall
    // 2. incoming
    // 3. outgoing
    public void insertHistory(int type, String name, String number, String callDate, String callTime) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.CALLTYPE, type);
        contentValue.put(DatabaseHelper.OWNERNAME, name);
        contentValue.put(DatabaseHelper.OWNERNUMBER, number);
        contentValue.put(DatabaseHelper.CONTACTDATE, callDate);
        contentValue.put(DatabaseHelper.CONTACTTIME, callTime);
        database.insert(DatabaseHelper.TABLE_HISTORY_NAME, null, contentValue);
    }

    public ArrayList<ContactListItemModel> getContactListItem() {
        mContactListItemModels = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.OWNERNAME, DatabaseHelper.OWNERNUMBER};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ContactListItemModel contactListItemModel = new ContactListItemModel();
                    contactListItemModel.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID)));
                    contactListItemModel.setOwnerName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.OWNERNAME)));
                    contactListItemModel.setOwnerNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.OWNERNUMBER)));
                    contactListItemModel.setCheck(false);
                    mContactListItemModels.add(contactListItemModel);
                } while (cursor.moveToNext());
            }
            if (mContactListItemModels.size() > 0)
                mContactListItemModels.get(0).setCheck(true);
        }
        return mContactListItemModels;
    }

    public ArrayList<HistroyListItemModel> getHistoryListItem() {
        mHistroyListItemModels = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.CALLTYPE, DatabaseHelper.OWNERNAME, DatabaseHelper.OWNERNUMBER, DatabaseHelper.CONTACTDATE, DatabaseHelper.CONTACTTIME};
        Cursor cursor = database.query(DatabaseHelper.TABLE_HISTORY_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    HistroyListItemModel histroyListItemModel = new HistroyListItemModel();
                    histroyListItemModel.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID)));
                    histroyListItemModel.setCallerType(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CALLTYPE)));
                    histroyListItemModel.setOwnerName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.OWNERNAME)));
                    histroyListItemModel.setOwnerNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.OWNERNUMBER)));
                    histroyListItemModel.setContactDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONTACTDATE)));
                    histroyListItemModel.setContactTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONTACTTIME)));
                    histroyListItemModel.setCheck(false);
                    mHistroyListItemModels.add(histroyListItemModel);
                } while (cursor.moveToNext());
            }
            if (mHistroyListItemModels.size() > 0) {
                mHistroyListItemModels.get(mHistroyListItemModels.size() - 1).setCheck(true);
                Collections.reverse(mHistroyListItemModels);
            }
        }

        return mHistroyListItemModels;
    }

    public int updateHistory(String name, String newName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.OWNERNAME, newName);
        int i = database.update(DatabaseHelper.TABLE_HISTORY_NAME, contentValues, DatabaseHelper.OWNERNAME + " = ?",  new String[] {name});
        return i;
    }

    public int update(long _id, String name, String number) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper._ID, _id);
        contentValues.put(DatabaseHelper.OWNERNAME, name);
        contentValues.put(DatabaseHelper.OWNERNUMBER, number);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void deleteHistory(long _id) {
        database.delete(DatabaseHelper.TABLE_HISTORY_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}
