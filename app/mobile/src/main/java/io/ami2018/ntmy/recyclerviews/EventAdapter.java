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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> list;
    private EventClickListener listener;

    public EventAdapter(EventClickListener listener) {
        this.list = new ArrayList<>();
        this.listener = listener;
    }

    public void addElement(Event event) {
        if (!this.contains(event.getEventId())) {
            this.list.add(event);
            //TODO sort
            this.notifyDataSetChanged();
        }
    }

    public void clear() {
        this.list.clear();
        this.notifyDataSetChanged();
    }

    private boolean contains(Integer eventId) {
        for (Event e : this.list) {
            if (e.getEventId().intValue() == eventId.intValue()) return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
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
        if (event.getRoom() != null)
            holder.mRoom.setText(event.getRoom().getName());
        else
            holder.mRoom.setText("Not Assigned");

        holder.setEvent(event);
    }

    protected static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitle;
        private TextView mCategories;
        private TextView mDate;
        private TextView mTime;
        private TextView mRoom;

        private Event mEvent;

        private EventClickListener mListener;

        private EventViewHolder(View itemView, EventClickListener listener) {
            super(itemView);
            this.mTitle = itemView.findViewById(R.id.item_event_card_tv_title);
            this.mCategories = itemView.findViewById(R.id.item_event_card_tv_categories);
            this.mDate = itemView.findViewById(R.id.item_event_card_tv_date);
            this.mTime = itemView.findViewById(R.id.item_event_card_tv_time);
            this.mRoom = itemView.findViewById(R.id.item_event_card_tv_room);
            this.mListener = listener;
            itemView.setOnClickListener(this);
        }

        private void setEvent(Event event) {
            this.mEvent = event;
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, mEvent);
        }
    }
}
