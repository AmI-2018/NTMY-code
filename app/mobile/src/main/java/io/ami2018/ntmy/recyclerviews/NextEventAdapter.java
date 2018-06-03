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
        Event event = list.get(position);
        StringBuffer categories = new StringBuffer();
        String time = event.getStart().split(" ")[1] + " - " + event.getEnd().split(" ")[1];
        for (int i = 0; i < event.getCategories().size(); i++) {
            categories.append(event.getCategories().get(event.getCategories().keyAt(i)).getName());
            if (i != event.getCategories().size() - 1) categories.append(", ");
        }
        holder.mTitle.setText(event.getName());
        holder.mCategories.setText(categories);
        holder.mDate.setText(event.getStart().split(" ")[0]);
        holder.mTime.setText(time);
        holder.mRoom.setText(event.getRoom().getName());
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public static class NextEventViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mCategories;
        private TextView mDate;
        private TextView mTime;
        private TextView mRoom;

        public NextEventViewHolder(View itemView) {
            super(itemView);
            this.mTitle = itemView.findViewById(R.id.item_event_card_tv_title);
            this.mCategories = itemView.findViewById(R.id.item_event_card_tv_categories);
            this.mDate = itemView.findViewById(R.id.item_event_card_tv_date);
            this.mTime = itemView.findViewById(R.id.item_event_card_tv_time);
            mRoom = itemView.findViewById(R.id.item_event_card_tv_room);
        }
    }
}
