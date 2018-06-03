package io.ami2018.ntmy.recyclerviews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.ami2018.ntmy.R;
import io.ami2018.ntmy.model.Event;

public class NextEventAdapter extends RecyclerView.Adapter<NextEventAdapter.NextEventViewHolder> {

    private ArrayList<Event> list;

    public NextEventAdapter() {
        this.list = new ArrayList<>();
    }

    public NextEventAdapter(ArrayList<Event> list) {
        this.list = list;
    }

    public void addElement(Event event) {
        if (!this.contains(event.getEventId())) {
            this.list.add(event);
            this.notifyDataSetChanged();
        }
    }

    public boolean contains(Integer eventId) {
        for (Event e : this.list) {
            if (e.getEventId().intValue() == eventId.intValue()) return true;
        }
        return false;
    }

    @NonNull
    @Override
    public NextEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_event_card, parent, false);
        return new NextEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NextEventAdapter.NextEventViewHolder holder, int position) {
        holder.mTitle.setText(list.get(position).getName());
        holder.mDescr.setText(list.get(position).getDescription());
        holder.mDate.setText(list.get(position).getStart().split(" ")[0]);
        String time = list.get(position).getStart().split(" ")[1] + " - " + list.get(position).getEnd().split(" ")[1];
        holder.mTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public static class NextEventViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mDescr;
        private TextView mDate;
        private TextView mTime;

        public NextEventViewHolder(View itemView) {
            super(itemView);
            this.mTitle = itemView.findViewById(R.id.item_event_card_tv_title);
            this.mDescr = itemView.findViewById(R.id.item_event_card_tv_descr);
            this.mDate = itemView.findViewById(R.id.item_event_card_tv_date);
            this.mTime = itemView.findViewById(R.id.item_event_card_tv_time);
        }
    }
}
