package jp.mcinc.imesh.type.ipphone.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Available {
    private PurchaseListItemModel[] availablePhoneNumbers;
    private String uri;

    @JsonProperty("available_phone_numbers")
    public PurchaseListItemModel[] getAvailablePhoneNumbers() { return availablePhoneNumbers; }
    @JsonProperty("available_phone_numbers")
    public void setAvailablePhoneNumbers(PurchaseListItemModel[] value) { this.availablePhoneNumbers = value; }

    @JsonProperty("uri")
    public String getURI() { return uri; }
    @JsonProperty("uri")
    public void setURI(String value) { this.uri = value; }
}
