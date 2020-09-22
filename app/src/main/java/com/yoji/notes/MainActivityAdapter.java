package com.yoji.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yoji.notes.database.NoteData;

import java.util.ArrayList;
import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<NoteData> allNotesData;
    private ItemOnClickListener itemOnClickListener;

//    private static TextView textView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View v;

        protected final TextView textTxtView;
        private final TextView continueTxtView;
        private final TextView titleTxtView;
        private final TextView deadlineTxtView;
        private Button deleteBtn;
        private Button editBtn;


//        private View.OnLongClickListener onLongClickListener = v -> {
//            deleteBtn.setVisibility(View.VISIBLE);
//            editBtn.setVisibility(View.VISIBLE);
//            return true;
//        };

        private View.OnClickListener onClickListener = v -> {

        };

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
//            v.setOnLongClickListener(onLongClickListener);

            textTxtView = v.findViewById(R.id.textTxtViewId);
            titleTxtView = v.findViewById(R.id.titleTxtViewId);
            deadlineTxtView = v.findViewById(R.id.deadlineTxtViewId);
            deleteBtn = v.findViewById(R.id.deleteBtnId);
            editBtn = v.findViewById(R.id.editBtnId);
            continueTxtView = v.findViewById(R.id.continueTxtViewId);
        }

        public TextView getTextTxtView(){
            return textTxtView;
        }

        public TextView getTitleTxtView() {
            return titleTxtView;
        }

        public TextView getDeadlineTxtView() {
            return deadlineTxtView;
        }

        public Button getDeleteBtn() {
            return deleteBtn;
        }

        public Button getEditBtn() {
            return editBtn;
        }

        public TextView getContinueTxtView() {
            return continueTxtView;
        }
    }

    public MainActivityAdapter (Context context, ItemOnClickListener itemOnClickListener){
        this.itemOnClickListener = itemOnClickListener;
        allNotesData = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        View.OnLongClickListener onLongClickListener = v -> {
            holder.getDeleteBtn().setVisibility(View.VISIBLE);
            holder.getEditBtn().setVisibility(View.VISIBLE);
            return true;
        };

        ViewTreeObserver.OnPreDrawListener preDrawListener = () -> {
            if (holder.getTextTxtView().getLineCount() > 9){
                holder.getTextTxtView().setMaxLines(9);
                holder.getContinueTxtView().setVisibility(View.VISIBLE);
            }else {
                holder.getTextTxtView().setMaxLines(Integer.MAX_VALUE);
                holder.getContinueTxtView().setVisibility(View.GONE);
            }
            return true;
        };

        holder.getTextTxtView().setText(allNotesData.get(position).getText());
        holder.getTextTxtView().getViewTreeObserver().addOnPreDrawListener(preDrawListener);

        setText(holder.getTitleTxtView(), allNotesData.get(position).getTitle());
        setText(holder.getDeadlineTxtView(), allNotesData.get(position).getDeadline());

        holder.getDeleteBtn().setTag(allNotesData.get(position).getNoteId());
        holder.getEditBtn().setTag(allNotesData.get(position).getNoteId());

        holder.getDeleteBtn().setOnClickListener(deleteBtnOnClickListener);
        holder.getEditBtn().setOnClickListener(editBtnOnClickListener);

        holder.itemView.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public int getItemCount() {
        return allNotesData.size();
    }

    private View.OnClickListener deleteBtnOnClickListener = v ->
        itemOnClickListener.itemOnClickListener((Long) v.getTag(), true, false);

    private View.OnClickListener editBtnOnClickListener = v ->
        itemOnClickListener.itemOnClickListener((Long) v.getTag(), false, true);

    public void setAllNotesData(List<NoteData> allNotesData) {
        this.allNotesData = allNotesData;
        notifyDataSetChanged();
    }

    private void setText (TextView textView, String text){
        if (!text.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }else {
            textView.setVisibility(View.GONE);
        }
    }
}
