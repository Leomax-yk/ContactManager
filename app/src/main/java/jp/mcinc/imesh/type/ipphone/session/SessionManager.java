package jp.mcinc.imesh.type.ipphone.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private String TAG = getClass().getSimpleName();
    private Context _context;
    private int PRIVATE_MODE = 0;
    private SharedPreferences prefGpsOdo;
    private SharedPreferences.Editor editorGpsOdo;
    public static final String KEY_CONTACT = "KEY_CONTACT";
    public static final String KEY_LANGUAGE = "KEY_LANGUAGE";
    public static final String KEY_LANGUAGE_SET = "KEY_LANGUAGE_SET";
    public static final String KEY_PURCHASE = "KEY_PURCHASE";
    public static final String KEY_NUMBER = "KEY_NUMBER";
    public static final String KEY_CUSTOMER_CD = "KEY_CUSTOMER_CD";
    public static final String KEY_NUMBER_SID = "KEY_NUMBER_SID";
    public static final String KEY_PURCHASE_RESPONSE = "KEY_PURCHASE_RESPONSE";
    public static final String KEY_IMENUMBER = "KEY_IMENUMBER";
    public static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";
    public static final String KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN";
    public static final String KEY_ID_TOKEN = "KEY_ID_TOKEN";
    public static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
    public static final String KEY_CALL_START = "KEY_CALL_START";
    public static final String KEY_CALL_IMESH_START = "KEY_CALL_IMESH_START";
    public static final String KEY_OPEN = "KEY_OPEN";
    public SessionManager(Context context) {
        this._context = context;
        prefGpsOdo = _context.getSharedPreferences(KEY_CONTACT, PRIVATE_MODE);
        editorGpsOdo = prefGpsOdo.edit();
    }

    public void setPurchase(boolean isPurchase) {
        editorGpsOdo.putBoolean(KEY_PURCHASE, isPurchase);
        editorGpsOdo.apply();
    }

    public boolean isPurchase(){
        return prefGpsOdo.getBoolean(KEY_PURCHASE,false);
    }

    public void setLanguageSet(boolean languageSet) {
        editorGpsOdo.putBoolean(KEY_LANGUAGE_SET, languageSet);
        editorGpsOdo.apply();
    }

    public boolean isLanguageSet(){
        return prefGpsOdo.getBoolean(KEY_LANGUAGE_SET,false);
    }

    public void setNumber(String number) {
        editorGpsOdo.putString(KEY_NUMBER, number);
        editorGpsOdo.apply();
    }

    public String getNumber(){
        return prefGpsOdo.getString(KEY_NUMBER,"-");
    }

    public void setCustomerCd(String CustomerCd) {
        editorGpsOdo.putString(KEY_CUSTOMER_CD, CustomerCd);
        editorGpsOdo.apply();
    }

    public String getCustomerCd(){
        return prefGpsOdo.getString(KEY_CUSTOMER_CD,"-");
    }


    public void setNumberSid(String numberSid) {
        editorGpsOdo.putString(KEY_NUMBER_SID, numberSid);
        editorGpsOdo.apply();
    }

    public String getNumberSid(){
        return prefGpsOdo.getString(KEY_NUMBER_SID,"-");
    }


    public void setLanguage(String language) {
        editorGpsOdo.putString(KEY_LANGUAGE, language);
        editorGpsOdo.apply();
    }

    public String getLanguage(){
        return prefGpsOdo.getString(KEY_LANGUAGE,"JAPANESE");
    }

    public void setPurchaseResponse(String purchaseResponse) {
        editorGpsOdo.putString(KEY_PURCHASE_RESPONSE, purchaseResponse);
        editorGpsOdo.apply();
    }

    public String getPurchaseResponse(){
        return prefGpsOdo.getString(KEY_PURCHASE_RESPONSE,"");
    }


    public void setImenumber(String imenumber) {
        editorGpsOdo.putString(KEY_IMENUMBER, imenumber);
        editorGpsOdo.apply();
    }

    public String getImenumber(){
        return prefGpsOdo.getString(KEY_IMENUMBER,"");
    }


    public void setDeviceId(String deviceId) {
        editorGpsOdo.putString(KEY_DEVICE_ID, deviceId);
        editorGpsOdo.apply();
    }

    public String getDeviceId(){
        return prefGpsOdo.getString(KEY_DEVICE_ID,"");
    }


    public void setRefreshToken(String refreshToken) {
        editorGpsOdo.putString(KEY_REFRESH_TOKEN, refreshToken);
        editorGpsOdo.apply();
    }

    public String getRefreshToken(){
        return prefGpsOdo.getString(KEY_REFRESH_TOKEN,"");
    }


    public void setAccessToken(String accessToken) {
        editorGpsOdo.putString(KEY_ACCESS_TOKEN, accessToken);
        editorGpsOdo.apply();
    }

    public String getAccessToken(){
        return prefGpsOdo.getString(KEY_ACCESS_TOKEN,"");
    }

    public void setIdToken(String idToken) {
        editorGpsOdo.putString(KEY_ID_TOKEN, idToken);
        editorGpsOdo.apply();
    }

    public String getIdToken(){
        return prefGpsOdo.getString(KEY_ID_TOKEN,"");
    }

    public boolean isCallStart(){
        return prefGpsOdo.getBoolean(KEY_CALL_START,false);
    }

    public void setCallStart(boolean callStart) {
        editorGpsOdo.putBoolean(KEY_CALL_START, callStart);
        editorGpsOdo.apply();
    }

    public boolean isCallImeshStart(){
        return prefGpsOdo.getBoolean(KEY_CALL_IMESH_START,false);
    }

    public void setCallImeshStart(boolean callStart) {
        editorGpsOdo.putBoolean(KEY_CALL_IMESH_START, callStart);
        editorGpsOdo.apply();
    }

    public void setOpen(boolean open) {
        editorGpsOdo.putBoolean(KEY_OPEN, open);
        editorGpsOdo.apply();
    }

    public boolean isOpen(){
        return prefGpsOdo.getBoolean(KEY_OPEN,false);
    }


}
