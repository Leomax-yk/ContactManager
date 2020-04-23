package jp.mcinc.imesh.type.ipphone.model;

import com.fasterxml.jackson.annotation.*;

public class Capabilities {
    private boolean voice;
    private boolean sms;
    private boolean mms;
    private boolean fax;

    @JsonProperty("voice")
    public boolean getVoice() { return voice; }
    @JsonProperty("voice")
    public void setVoice(boolean value) { this.voice = value; }

    @JsonProperty("SMS")
    public boolean getSMS() { return sms; }
    @JsonProperty("SMS")
    public void setSMS(boolean value) { this.sms = value; }

    @JsonProperty("MMS")
    public boolean getMms() { return mms; }
    @JsonProperty("MMS")
    public void setMms(boolean value) { this.mms = value; }

    @JsonProperty("fax")
    public boolean getFax() { return fax; }
    @JsonProperty("fax")
    public void setFax(boolean value) { this.fax = value; }
}
