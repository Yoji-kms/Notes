package com.yoji.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yoji.notes.database.NoteData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteListActivityAdapter extends RecyclerView.Adapter<NoteListActivityAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<NoteData> allNotesData;
    private ItemOnClickListener itemOnClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View v;

        private boolean showWhole;

        private final TextView textTxtView;
        private final TextView continueTxtView;
        private final TextView titleTxtView;
        private final TextView deadlineTxtView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;

            textTxtView = v.findViewById(R.id.textTxtViewId);
            titleTxtView = v.findViewById(R.id.titleTxtViewId);
            deadlineTxtView = v.findViewById(R.id.deadlineTxtViewId);
            continueTxtView = v.findViewById(R.id.continueTxtViewId);
        }

        public TextView getTextTxtView() {
            return textTxtView;
        }

        public TextView getTitleTxtView() {
            return titleTxtView;
        }

        public TextView getDeadlineTxtView() {
            return deadlineTxtView;
        }

        public TextView getContinueTxtView() {
            return continueTxtView;
        }

        public boolean isWhole() {
            return showWhole;
        }

        public void setWhole(boolean showWhole) {
            this.showWhole = showWhole;
        }
    }

    public NoteListActivityAdapter(Context context, ItemOnClickListener itemOnClickListener) {
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
        NoteData itemNoteData = allNotesData.get(position);
        Context context;

        context = holder.v.getContext();

        View.OnLongClickListener onLongClickListener = v -> {
            itemOnClickListener.getItemId((Long) v.getTag());
            v.showContextMenu();
            return true;
        };

        View.OnClickListener onClickListener = v -> {
            if (holder.getTextTxtView().getLineCount() > 9) {
                holder.setWhole(true);
            }
        };

        ViewTreeObserver.OnPreDrawListener preDrawListener = () -> {
            if (holder.getTextTxtView().getLineCount() > 9 && !holder.isWhole()) {
                holder.getTextTxtView().setMaxLines(9);
                holder.getContinueTxtView().setVisibility(View.VISIBLE);
            } else {
                holder.getTextTxtView().setMaxLines(Integer.MAX_VALUE);
                holder.getContinueTxtView().setVisibility(View.GONE);
            }
            return true;
        };

        holder.getTextTxtView().setText(itemNoteData.getText());
        holder.getTextTxtView().getViewTreeObserver().addOnPreDrawListener(preDrawListener);

        setText(holder.getTitleTxtView(), itemNoteData.getTitle());
        if (itemNoteData.getDeadline() != null) {
            setText(holder.getDeadlineTxtView(), itemNoteData.getDeadline().toString());
        }

        holder.v.setOnLongClickListener(onLongClickListener);
        holder.v.setOnClickListener(onClickListener);
        holder.v.setTag(itemNoteData.getNoteId());

        CardView cardView = (CardView) holder.v;
        switch (compareDeadlineWithCurrentDate(itemNoteData.getDeadline())) {
            case Deadline.OK:
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.deadline_ok));
                break;
            case Deadline.CLOSE:
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.deadline_close));
                break;
            case Deadline.PASSED:
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.deadline_passed));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return allNotesData.size();
    }

    public void setAllNotesData(List<NoteData> allNotesData) {
        this.allNotesData = allNotesData;

        notifyDataSetChanged();
    }

    private void setText(TextView textView, String text) {
        if (!text.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.setWhole(false);
    }

    private int compareDeadlineWithCurrentDate(Date deadline) {
        if (deadline == null) {
            return Deadline.OK;
        }

        long difference = (deadline.getTime() - currentDate().getTime()) / (1000 * 60 * 60 * 24);
        if (difference < 0) {
            return Deadline.PASSED;
        } else if (difference < 4) {
            return Deadline.CLOSE;
        } else {
            return Deadline.OK;
        }
    }

    private Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
