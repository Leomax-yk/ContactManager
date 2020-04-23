package jp.mcinc.imesh.type.ipphone.controller;

public class Controller {
//        implements Callback<List<Change>> {

//    static final String BASE_URL = "https://api.twilio.com/2010-04-01/Accounts/";
//
//    public void start() {
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        IPPhoneAPI ipPhoneAPI = retrofit.create(IPPhoneAPI.class);
//
//        Call<List<Change>> call = ipPhoneAPI.loadChanges("status:open");
//        call.enqueue(this);
//
//    }
//
//    @Override
//    public void onResponse(Call<List<Change>> call, Response<List<Change>> response) {
//        if(response.isSuccessful()) {
//            List<Change> changesList = response.body();
//            changesList.forEach(change -> System.out.println(change.subject));
//        } else {
//            System.out.println(response.errorBody());
//        }
//    }
//
//    @Override
//    public void onFailure(Call<List<Change>> call, Throwable t) {
//        t.printStackTrace();
//    }
}