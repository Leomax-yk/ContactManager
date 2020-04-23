package jp.mcinc.imesh.type.ipphone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jp.mcinc.imesh.type.ipphone.model.ContactListItemModel;

import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.database.DBManager;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ContactListItemAdapter extends RecyclerView.Adapter<ContactListItemAdapter.ContactListItemHolder> {
    public String TAG = getClass().getSimpleName();
    private Context context;
    private List<ContactListItemModel> mContactListItemModels;
    private int pos = 0;//LoadClassData.GP();
    private DBManager dbManager;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public ContactListItemAdapter(Context context, List<ContactListItemModel> contactListItemModels) {
        this.context = context;
        this.mContactListItemModels = contactListItemModels;
        dbManager = new DBManager(context);
        dbManager.open();
    }

    public int getItemCount() {
        return this.mContactListItemModels.size();
    }

    public int getPosition() {
        return this.pos;
    }

    public ContactListItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflatedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_contact_list_item, viewGroup, false);
        return new ContactListItemHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListItemHolder holder, int position) {
        try {
            holder.txtItemName.setText("" + mContactListItemModels.get(position).getOwnerName());
            holder.txtItemNumber.setText("" + mContactListItemModels.get(position).getOwnerNumber());
            if (mContactListItemModels.get(position).isCheck())
                holder.relativeContactListItem.setBackgroundColor(Color.parseColor("#CCCCCC"));
            else
                holder.relativeContactListItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
        }
    }

    protected class ContactListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtItemName, txtItemNumber;
        RelativeLayout relativeContactListItem;

        ContactListItemHolder(View view) {
            super(view);
            txtItemName = view.findViewById(R.id.text_name);
            txtItemNumber = view.findViewById(R.id.text_number);
            relativeContactListItem = view.findViewById(R.id.relative_contact_list_item);
            view.setOnClickListener(this);
            setIsRecyclable(false);
        }

        public void onClick(View view) {
            pos = getAdapterPosition();
            if (pos != -1) {
                makeCall();
            }
        }
    }

    private void makeCall() {
        if (mContactListItemModels != null && mContactListItemModels.size() > 0) {
            Date date = new Date();
            Log.e(TAG, "makeCall: " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
            Log.e(TAG, "makeCall: " + DateFormat.getTimeInstance().format(date));
            dbManager.insertHistory(3, mContactListItemModels.get(pos).getOwnerName(), mContactListItemModels.get(pos).getOwnerNumber(), "" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(date), "" + DateFormat.getTimeInstance().format(date));
        }
    }
}
