package com.yoji.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yoji.notes.database.NoteData;

import java.util.ArrayList;
import java.util.List;

public class MainActivityAdapter extends BaseAdapter {
    private ItemOnClickListener itemOnClickListener;

    LayoutInflater inflater;
    List<NoteData> allNotesData;
    private TextView textTxtView;
//    private TextView continueTxtView;
    private Button deleteBtn;
    private Button editBtn;

    private View.OnClickListener deleteBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            itemOnClickListener.itemOnClickListener((Long) v.getTag(), true, false);
        }
    };

    private View.OnClickListener changeBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            itemOnClickListener.itemOnClickListener((Long) v.getTag(), false, true);
        }
    };

    public void setAllNotesData(List<NoteData> allNotesData) {
        this.allNotesData = allNotesData;
        notifyDataSetChanged();
    }

    public MainActivityAdapter(Context context, ItemOnClickListener itemOnClickListener){
        this.itemOnClickListener = itemOnClickListener;
        allNotesData = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return allNotesData.size();
    }

    @Override
    public Object getItem(int position) {
        return allNotesData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = inflater.inflate(R.layout.item_note, parent, false);
        }

        NoteData item = allNotesData.get(position);

        TextView titleTxtView = view.findViewById(R.id.titleTxtViewId);
        textTxtView = view.findViewById(R.id.textTxtViewId);
        textTxtView.setMaxLines(Integer.MAX_VALUE);
//        continueTxtView = view.findViewById(R.id.continueTxtViewId);
        TextView deadlineTxtView = view.findViewById(R.id.deadlineTxtViewId);
        deleteBtn = view.findViewById(R.id.deleteBtnId);
        deleteBtn.setTag(item.getNoteId());
        deleteBtn.setOnClickListener(deleteBtnOnClickListener);
        editBtn = view.findViewById(R.id.editBtnId);
        editBtn.setTag(item.getNoteId());
        editBtn.setOnClickListener(changeBtnOnClickListener);

        String title = item.getTitle();
        String deadline = item.getDeadline();

        setText(titleTxtView, title);
        textTxtView.setText(item.getText());
        setText(deadlineTxtView, deadline);

        return view;
    }

    private void setText (TextView textView, String text){
        if (!text.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }
}
