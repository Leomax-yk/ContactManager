package jp.mcinc.imesh.type.ipphone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.model.PurchaseListItemModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PurchaseListItemAdapter extends RecyclerView.Adapter<PurchaseListItemAdapter.PurchaseListItemHolder> {
    public String TAG = getClass().getSimpleName();
    private Context context;
    private List<PurchaseListItemModel> mPurchaseListItemModels;
    private int pos = 0;//LoadClassData.GP();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PurchaseListItemModel item);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public PurchaseListItemAdapter(Context context, List<PurchaseListItemModel> purchaseListItemModels) {
        this.context = context;
        this.mPurchaseListItemModels = purchaseListItemModels;
    }

    public PurchaseListItemAdapter(Context context, List<PurchaseListItemModel> purchaseListItemModels, OnItemClickListener listener) {
        this.context = context;
        this.mPurchaseListItemModels = purchaseListItemModels;
        this.listener = listener;
    }

    public int getItemCount() {
        return this.mPurchaseListItemModels.size();
    }

    public int getPosition() {
        return this.pos;
    }

    public PurchaseListItemAdapter.PurchaseListItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflatedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_purchase_list_item, viewGroup, false);
        return new PurchaseListItemAdapter.PurchaseListItemHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseListItemAdapter.PurchaseListItemHolder holder, int position) {
        try {
            holder.txtItemNumber.setText("" + mPurchaseListItemModels.get(position).getOwnerNumber());
            if (mPurchaseListItemModels.get(position).isCheck())
                holder.relativeContactListItem.setBackgroundColor(Color.parseColor("#CCCCCC"));
            else
                holder.relativeContactListItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
        }
    }

    protected class PurchaseListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtItemNumber;
        RelativeLayout relativeContactListItem;

        PurchaseListItemHolder(View view) {
            super(view);
            txtItemNumber = view.findViewById(R.id.text_number);
            relativeContactListItem = view.findViewById(R.id.relative_purchcase_list_item);
            view.setOnClickListener(this);
            setIsRecyclable(false);
        }

        public void onClick(View view) {
            pos = getAdapterPosition();
            if (pos != -1) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(mPurchaseListItemModels.get(pos));
                    }
                });
            }
        }
    }


}
