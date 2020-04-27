package jp.mcinc.imesh.type.ipphone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import jp.mcinc.imesh.type.ipphone.R;
import jp.mcinc.imesh.type.ipphone.activity.HistoryListActivity;
import jp.mcinc.imesh.type.ipphone.model.HistroyListItemModel;

import java.util.List;

public class HistoryListItemAdapter extends RecyclerView.Adapter<HistoryListItemAdapter.HistoryListItemHolder> {
    public String TAG = getClass().getSimpleName();
    private Context context;
    private List<HistroyListItemModel> mHistroyListItemModels;
    private int pos = 0;//LoadClassData.GP();
    private HistoryListActivity.RecyclerViewClickListener mListener;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public HistoryListItemAdapter(Context context, List<HistroyListItemModel> histroyListItemModel, HistoryListActivity.RecyclerViewClickListener mListener) {
        this.mListener = mListener;
        this.context = context;
        this.mHistroyListItemModels = histroyListItemModel;
    }

    public int getItemCount() {
        return this.mHistroyListItemModels.size();
    }

    public int getPosition() {
        return this.pos;
    }

    public HistoryListItemAdapter.HistoryListItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflatedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_history_list_item, viewGroup, false);
        return new HistoryListItemAdapter.HistoryListItemHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryListItemAdapter.HistoryListItemHolder holder, int position) {
        try {
            holder.textItemName.setText("" + mHistroyListItemModels.get(position).getOwnerName());
            holder.textItemNumber.setText("" + mHistroyListItemModels.get(position).getOwnerNumber());
            holder.textItemDate.setText("" + mHistroyListItemModels.get(position).getContactDate());
            holder.textItemTime.setText("" + mHistroyListItemModels.get(position).getContactTime());
            if (mHistroyListItemModels.get(position).getCallerType() == 1)
                holder.imageCall.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_history_missed_call));
            else if (mHistroyListItemModels.get(position).getCallerType() == 2)
                holder.imageCall.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_history_incoming));
            else if (mHistroyListItemModels.get(position).getCallerType() == 3)
                holder.imageCall.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_history_outgoing));
            if (mHistroyListItemModels.get(position).isCheck())
                holder.relativeContactListItem.setBackgroundColor(Color.parseColor("#CCCCCC"));
            else
                holder.relativeContactListItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
        }
    }

    protected class HistoryListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textItemName, textItemNumber, textItemDate, textItemTime;
        ImageView imageCall;
        RelativeLayout relativeContactListItem;

        HistoryListItemHolder(View view) {
            super(view);
            relativeContactListItem = view.findViewById(R.id.relative_history_list_item);
            imageCall = view.findViewById(R.id.image_call);
            textItemName = view.findViewById(R.id.text_name);
            textItemNumber = view.findViewById(R.id.text_number);
            textItemDate = view.findViewById(R.id.text_date);
            textItemTime = view.findViewById(R.id.text_time);
            view.setOnClickListener(this);
            setIsRecyclable(false);
        }

        public void onClick(View view) {
            pos = getAdapterPosition();
            if (pos != -1) {
                mListener.onClick(view, getAdapterPosition());
            }
        }
    }
}
