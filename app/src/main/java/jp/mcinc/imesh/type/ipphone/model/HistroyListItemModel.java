package jp.mcinc.imesh.type.ipphone.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HistroyListItemModel implements Parcelable {

    private int Id;
    private int CallerType;
    private boolean check;
    private String OwnerName;
    private String OwnerNumber;
    private String ContactDate;
    private String ContactTime;

    public HistroyListItemModel() {
    }

    protected HistroyListItemModel(Parcel in) {
        Id = in.readInt();
        CallerType = in.readInt();
        check = in.readByte() != 0;
        OwnerName = in.readString();
        OwnerNumber = in.readString();
        ContactDate = in.readString();
        ContactTime = in.readString();
    }

    public static final Creator<HistroyListItemModel> CREATOR = new Creator<HistroyListItemModel>() {
        @Override
        public HistroyListItemModel createFromParcel(Parcel in) {
            return new HistroyListItemModel(in);
        }

        @Override
        public HistroyListItemModel[] newArray(int size) {
            return new HistroyListItemModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Id);
        parcel.writeInt(CallerType);
        parcel.writeByte((byte) (check ? 1 : 0));
        parcel.writeString(OwnerName);
        parcel.writeString(OwnerNumber);
        parcel.writeString(ContactDate);
        parcel.writeString(ContactTime);
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getCallerType() {
        return CallerType;
    }

    public void setCallerType(int callerType) {
        CallerType = callerType;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getContactDate() {
        return ContactDate;
    }

    public void setContactDate(String contactDate) {
        ContactDate = contactDate;
    }

    public String getContactTime() {
        return ContactTime;
    }

    public void setContactTime(String contactTime) {
        ContactTime = contactTime;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getOwnerNumber() {
        return OwnerNumber;
    }

    public void setOwnerNumber(String ownerNumber) {
        OwnerNumber = ownerNumber;
    }

}
