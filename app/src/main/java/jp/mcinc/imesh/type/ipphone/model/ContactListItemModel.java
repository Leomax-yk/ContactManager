package jp.mcinc.imesh.type.ipphone.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactListItemModel implements Parcelable {
    private int id;
    private String OwnerName;
    private String OwnerNumber;
    private boolean check;

    public ContactListItemModel() {
    }

    protected ContactListItemModel(Parcel in) {
        id = in.readInt();
        OwnerName = in.readString();
        OwnerNumber = in.readString();
        check = in.readByte() != 0;
    }

    public static final Creator<ContactListItemModel> CREATOR = new Creator<ContactListItemModel>() {
        @Override
        public ContactListItemModel createFromParcel(Parcel in) {
            return new ContactListItemModel(in);
        }

        @Override
        public ContactListItemModel[] newArray(int size) {
            return new ContactListItemModel[size];
        }
    };

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(OwnerName);
        parcel.writeString(OwnerNumber);
        parcel.writeByte((byte) (check ? 1 : 0));
    }
}
