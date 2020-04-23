package jp.mcinc.imesh.type.ipphone.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PurchaseListItemModel implements Parcelable {
    private int id;
    private String OwnerNumber;
    private boolean check;
    private String friendlyName;
    private String phoneNumber;
    private boolean beta;
    private Object lata;
    private Object rateCenter;
    private Object latitude;
    private Object longitude;
    private String locality;
    private Object region;
    private Object postalCode;
    private String isoCountry;
    private String addressRequirements;
    private Capabilities capabilities;

    public PurchaseListItemModel(){

    }

    protected PurchaseListItemModel(Parcel in) {
        id = in.readInt();
        OwnerNumber = in.readString();
        check = in.readByte() != 0;
        friendlyName = in.readString();
        phoneNumber = in.readString();
        beta = in.readByte() != 0;
    }

    public static final Creator<PurchaseListItemModel> CREATOR = new Creator<PurchaseListItemModel>() {
        @Override
        public PurchaseListItemModel createFromParcel(Parcel in) {
            return new PurchaseListItemModel(in);
        }

        @Override
        public PurchaseListItemModel[] newArray(int size) {
            return new PurchaseListItemModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(OwnerNumber);
        parcel.writeByte((byte) (check ? 1 : 0));
        parcel.writeString(friendlyName);
        parcel.writeString(phoneNumber);
        parcel.writeByte((byte) (beta ? 1 : 0));
    }

    @JsonProperty("friendly_name")
    public String getFriendlyName() { return friendlyName; }
    @JsonProperty("friendly_name")
    public void setFriendlyName(String value) { this.friendlyName = value; }

    @JsonProperty("phone_number")
    public String getPhoneNumber() { return phoneNumber; }
    @JsonProperty("phone_number")
    public void setPhoneNumber(String value) { this.phoneNumber = value; }

    @JsonProperty("lata")
    public Object getLata() { return lata; }
    @JsonProperty("lata")
    public void setLata(Object value) { this.lata = value; }

    @JsonProperty("rate_center")
    public Object getRateCenter() { return rateCenter; }
    @JsonProperty("rate_center")
    public void setRateCenter(Object value) { this.rateCenter = value; }

    @JsonProperty("latitude")
    public Object getLatitude() { return latitude; }
    @JsonProperty("latitude")
    public void setLatitude(Object value) { this.latitude = value; }

    @JsonProperty("longitude")
    public Object getLongitude() { return longitude; }
    @JsonProperty("longitude")
    public void setLongitude(Object value) { this.longitude = value; }

    @JsonProperty("locality")
    public String getLocality() { return locality; }
    @JsonProperty("locality")
    public void setLocality(String value) { this.locality = value; }

    @JsonProperty("region")
    public Object getRegion() { return region; }
    @JsonProperty("region")
    public void setRegion(Object value) { this.region = value; }

    @JsonProperty("postal_code")
    public Object getPostalCode() { return postalCode; }
    @JsonProperty("postal_code")
    public void setPostalCode(Object value) { this.postalCode = value; }

    @JsonProperty("iso_country")
    public String getIsoCountry() { return isoCountry; }
    @JsonProperty("iso_country")
    public void setIsoCountry(String value) { this.isoCountry = value; }

    @JsonProperty("address_requirements")
    public String getAddressRequirements() { return addressRequirements; }
    @JsonProperty("address_requirements")
    public void setAddressRequirements(String value) { this.addressRequirements = value; }

    @JsonProperty("beta")
    public boolean getBeta() { return beta; }
    @JsonProperty("beta")
    public void setBeta(boolean value) { this.beta = value; }

    @JsonProperty("capabilities")
    public Capabilities getCapabilities() { return capabilities; }
    @JsonProperty("capabilities")
    public void setCapabilities(Capabilities value) { this.capabilities = value; }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerNumber() {
        return OwnerNumber;
    }

    public void setOwnerNumber(String ownerNumber) {
        OwnerNumber = ownerNumber;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
