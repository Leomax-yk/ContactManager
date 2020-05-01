package jp.mcinc.imesh.type.ipphone.contants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Constants {

    public static final String MCURL = "https://stg-ajt-mcaccount-eb.mcapps.jp/api/v1.0";
    public static final String VPTURL = "https://stg-ajt-vpt-eb.mcapps.jp/api/v1/";
    //    Orignianl : call project Dashboard
//    public static final String ACCOUNT_SID = "ACfec7df38e00a510a01919088b484bf3c";
//    public static final String AUTH_TOKEN = "4a3a4a35df79ddb969cb0cdd045aecaf";
//    public static final String ADDRESS_SID = "ADad22539896855fafc9900ed66d439b0d";
//    public static final String BUNDLE_SID = "BU27f074c5a7574db52580fab80beb0081";

//    public static final String BASEURL = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID;
//
//    public static final String AVAILABLE_PHONE_NUMBER = "AvailablePhoneNumbers.json";
//    public static final String AVAILABLE_COUNTRY = "AvailablePhoneNumbers/JP/Local.json";
//    public static final String ADDRESSES = "Addresses.json";
//    public static final String ADD_ADDRESSES = BASEURL + "/" + ADDRESSES;
//
//    public static final String AVAILABLE_JP_MOBILE_ALL = BASEURL + "/AvailablePhoneNumbers/JP/Mobile.json";
//    public static final String AVAILABLE_JP_TOLLFREE_ALL = BASEURL + "/AvailablePhoneNumbers/JP/TollFree.json";
//    public static final String AVAILABLE_JP_LOCAL_ALL = BASEURL + "/IncomingPhoneNumbers/Local.json";
//    public static final String AVAILABLE_JP_TOLLFREE = BASEURL + "/AvailablePhoneNumbers/JP/TollFree.json?VoiceEnabled=true";
//
//    public static final String INCOMINGPHONENUMBER_LOCAL = BASEURL + "/IncomingPhoneNumbers/Local.json";
//    public static final String AVAILABLE_JP_LOCAL_VOICE = BASEURL + "/AvailablePhoneNumbers/JP/Local.json?VoiceEnabled=true";
//
//    public static String AvailablePhoneNumbersURL = BASEURL + "/"+ AVAILABLE_PHONE_NUMBER;
//    public static String AvailablePhoneNumbersCountryURL = BASEURL + "/"+AVAILABLE_COUNTRY;
//
    public static String CALL_SID_KEY = "";
    public static final String VOICE_CHANNEL_LOW_IMPORTANCE = "notification-channel-low-importance";
    public static final String VOICE_CHANNEL_HIGH_IMPORTANCE = "notification-channel-high-importance";
    public static final String OUTGOING_CALL_INVITE = "OUTGOING_CALL_INVITE";
    public static final String INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE";
    public static final String CANCELLED_CALL_INVITE = "CANCELLED_CALL_INVITE";
    public static final String INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID";
    public static final String ACTION_ACCEPT = "ACTION_ACCEPT";
    public static final String ACTION_REJECT = "ACTION_REJECT";
    public static final String ACTION_INCOMING_CALL_NOTIFICATION = "ACTION_INCOMING_CALL_NOTIFICATION";
    public static final String ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL";
    public static final String ACTION_CANCEL_CALL = "ACTION_CANCEL_CALL";
    public static final String ACTION_FCM_TOKEN = "ACTION_FCM_TOKEN";

    public static final String CLIEENT_ID = "5sub397i24j94ue7iksskjlj4a";
    public static final String REFRESH_TOKEN = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.IaltoqMQxbV6i5coJWMIFlxhRnna_GnPY6SvA7jszEvWuOazVJ5vvztGLjYgDfbrDr-OJArTuZT-xwIb5caZrkytBk-eQukC30ugmekkB5iQt93i3sNP1fidzXL5p1XbUgr_YLgWCjIE3MY-zk95TLK6wBxCiM9jUFuxCGoZetyGhK7O6c0STjUakhgO0fQX3XE3lad8V2No0yCkBIcZFY37-1vNK4y3qsh7kUw88913HIrWMprT5KR7uxbsGnO8hkSLVhPLSlWYLKzSF3fs_m_n7KE-lbsE-HHKUROsO6pOAXxLJKaTVM8s3fQi1lpmnDOMtArgCnbuIAPqGBAYYg.rvRreUb_oKQFKxEI.08MW5GqvyqJYNB8T57nLWGnnAL6rzM2-z-4IhYmjARBnlpLIWzsz4faxKSvpmBsmRp5-YifFNaYfCgtteFbms3Sr4pguHXcN-72KOcsA0vNgt08Iujvk9rPmmrrSMF0KkFD4Nd6E5gBaA7Rwwj7RipEEagfew7Oh3tiJDw7LJn7r7K5AP5h3qGS9WU9EoFGc40eaCetd6I_0m4s7wTHKN80Jn6quifHIZCvGdr3z8Uq9U98oTFoMLHtuQS2eTociXLC_qk_-vIR6w5n74i32ueDkhbDZXdse7xGrJDcHcOUxIhDtxd6xD2Y44IZm0dCsJXz7cEE6LMjV2FY8faCyKHW5nb2sfd6bRCEWDzDbY1P82BQzRJnrnqv8xd3YIDu-q_-7m9gXiM4vl1_1TjcvfTxK8pCW4RN2Q-E-E63WaeRA_Fz10dZFdUhhMOmg-fBrk9lYbjI_rOFAxyKAOD09MCgLD_Zje5WaIOof-jVfYkWGOjsSrVhScsgjFQhpeNHCnUC4cX4C9YqgW0AaAh5A7VTkvieJfEQtesY4QT2fqO532NI4aGZLFQphuL4C0LW3Y5NkrqhooYldXX1S2MoORTyhHSnBcECDC1pRRff1aMnHnyHyGdFYETBAUWcqqewdWbyb2ZVuoloK95VMx4jugmL6TZh07wFWmvKOnnPpKaY_QC0rmLWwpYkI5wxiSfzLVc1ZOxSVYL--7wYWBCcY5hKuqw3Eu-ZWtelooxMdQLa9jgrPOoaVtBfAOQSlAMBncawgghKveQC5S3HXqidJE6TxWVnoaiwexf_bmGLwmc3H0Mf1gG7Fs_uxtoPlHaV-dispXuvrvoJg-0H2FlyF5iFnrfY8kUREcl1sq-3-BQvWdRM1z83oj47JuXDfDAjxJlPSnhVZa3KA8lVQonW_E_O7WGz5HnLb_3XRuZFtMmhNQSh4uGTnl4Z7Po8xcMj0uq9opQP7n25R_rSCijpmYtz6preponq7B9uAaqJ2ED7BSNeR9-uqLbcIJ3w_7tiSFUoIbgyWcVImmRPqswkesi1cCulYMBZAN0s_529kbeap72h0RF8EHWeCWsmXUJnoq3AG2owE1Cv9tU2ELsWh66ABeZi0NVKbbVAh53eOE0JjQ4Xd8PZ_338NZwfRv6iuWx8axhhbtlMYz_EA2mNNxdvbEwVtbRKCpRFrs0xdGBZeoUFQp4mmHYTpF1q0SY7AJUYLERl8-kJaX1YVIB8X0RwWjo8VwEk5makf38LjbcPCFwiKMscLtY4ZavaW6y3_99agTOB8NzGCCz6UuV5-vWVojKGrh38.djH5SQyyjHe4C-w91jpnfw";
    public static String ACCESS_TOKEN = "";
    public static String ID_TOKEN = "";
    public static String DEVICE_ID = "";

    //getting id token
    public static final String REFRESH_TOKEN_URL = MCURL + "/Token/refresh";

    //getting device id and purchasenumber and purchasenumbersid using idtoken
    public static final String CREATE_TWILLIO_URL = VPTURL + "TwilioIncommingPhones";

    //delete number
    public static final String DELETE_TWILLIO_URL = VPTURL + "TwilioIncommingPhones";

    //getting customerCd and purchasenumber and purchasenumbersid using idtoken
    public static final String GET_TWILLIO_URL = VPTURL + "TwilioIncommingPhones";

    //getting device id and number and sid using idtoken
    public static final String GET_ACCESS_TOKEN_URL = VPTURL + "TwilioAccessTokens/";

    public static void sendCallEndBroadcast(Context context){
        try{
            Intent shareIntent = new Intent();
            shareIntent.setAction("jp.mcinc.imesh.type.ipphone.action.TALK_END");
            shareIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            shareIntent.setPackage("jp.mcinc.imesh.type.m");
            context.sendBroadcast(shareIntent);
        }catch (Exception e){
            Log.e("Constant", "onClick: "+e.getMessage());
        }
    }

    public static void sendCallStartBroadcast(Context context){
        try{
            Intent shareIntent = new Intent();
            shareIntent.setAction("jp.mcinc.imesh.type.ipphone.action.TALK_STARTED");
            shareIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            shareIntent.setPackage("jp.mcinc.imesh.type.m");
            context.sendBroadcast(shareIntent);
        }catch (Exception e){
            Log.e("Constant", "onClick: "+e.getMessage());
        }
    }
    public static void sendBootCompletedStartBroadcast(Context context){
        try{
            Intent shareIntent = new Intent();
            shareIntent.setAction("jp.mcinc.imesh.type.ipphone.action.BOOT_COMPLETED");
            shareIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            shareIntent.setPackage("jp.mcinc.imesh.type.m");
            context.sendBroadcast(shareIntent);
        }catch (Exception e){
            Log.e("Constant", "onClick: "+e.getMessage());
        }
    }
}
