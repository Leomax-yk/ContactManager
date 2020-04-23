package jp.mcinc.imesh.type.ipphone.function;

import jp.mcinc.imesh.type.ipphone.model.Binding;
import jp.mcinc.imesh.type.ipphone.model.CreateBindingResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class TwilioFunctionsAPI {
    // The URL below should be the domain for your Twilio Functions, without the trailing slash:
    // Example: https://sturdy-concrete-1234.twil.io
    public final static String BASE_SERVER_URL = "https://toolbox-gnat-5377.twil.io";

    /**
     * A resource defined to register Notify bindings using the Twilio Notify Quickstart Template
     */
    interface FunctionsService {
        @POST("/register-binding")
        Call<CreateBindingResponse> register(@Body Binding binding);

    }

    private static FunctionsService functionsService = new Retrofit.Builder()
            .baseUrl(BASE_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FunctionsService.class);

    public static Call<CreateBindingResponse> registerBinding(final Binding binding) {
        return functionsService.register(binding);
    }

}
