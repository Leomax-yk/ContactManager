package jp.mcinc.imesh.type.ipphone.function;

import jp.mcinc.imesh.type.ipphone.model.Binding;
import jp.mcinc.imesh.type.ipphone.model.CreateBindingResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class TwilioAPI {
    public final static String BASE_SERVER_URL = "https://api.twilio.com/2010-04-01/Accounts/";

    /**
     * A resource defined to register Notify bindings using the Twilio Notify Quickstart Template
     */
    interface FunctionsService {
        @POST("/AvailablePhoneNumbers")
        Call<CreateBindingResponse> register(@Body Binding binding);

    }

    private static TwilioFunctionsAPI.FunctionsService functionsService = new Retrofit.Builder()
            .baseUrl(BASE_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwilioFunctionsAPI.FunctionsService.class);

    public static Call<CreateBindingResponse> registerBinding(final Binding binding) {
        return functionsService.register(binding);
    }

}