package io.ami2018.ntmy.recyclerviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.ami2018.ntmy.MainActivity;
import io.ami2018.ntmy.R;
import io.ami2018.ntmy.model.Event;
import io.ami2018.ntmy.network.RequestHelper;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private static final String TAG = EventAdapter.class.getSimpleName();

    private SortedList<Event> list;
    private EventClickListener showListener;
    private Context context;

    public EventAdapter(Context context, EventClickListener showListener) {
        this.list = new SortedList<Event>(Event.class, new SortedList.Callback<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                String start = e1.getStart();
                String start1 = e2.getStart();
                Date date = new Date();
                Date date1 = new Date();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
                    date = sdf.parse(start);
                    date1 = sdf.parse(start1);
                } catch (ParseException p) {
                    p.printStackTrace();
                }
                return date.compareTo(date1);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Event oldItem, Event newItem) {
                return oldItem.getEventId().equals(newItem.getEventId());
            }

            @Override
            public boolean areItemsTheSame(Event item1, Event item2) {
                return item1.getEventId().equals(item2.getEventId());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
        this.showListener = showListener;
        this.context = context;
    }

    public void addElement(Event event) {
        this.list.add(event);
    }

    public void clear() {
        list.beginBatchedUpdates();
        while (list.size() > 0) {
            list.removeItemAt(list.size() - 1);
        }
        list.endBatchedUpdates();
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
        return new EventViewHolder(view, showListener, context);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        Event event = list.get(holder.getAdapterPosition());
        Log.d(TAG, "Evento: " + event.getEventId());
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
        holder.mColor.setBackgroundColor(event.getColor().getColor());
        if (event.getRoom() != null)
            holder.mRoom.setText(event.getRoom().getName());
        else
            holder.mRoom.setText(context.getString(R.string.not_assigned));
        holder.setEvent(event);
        holder.loadParticipation();
    }

    protected static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitle;
        private TextView mCategories;
        private TextView mDate;
        private TextView mTime;
        private TextView mRoom;
        private ImageView mParticipate;
        private LinearLayout mColor;

        private Event mEvent;
        private boolean userIsParticipating;

        private EventClickListener mShowListener;
        private Context context;

        private EventViewHolder(View itemView, EventClickListener showListener, Context context) {
            super(itemView);
            this.mTitle = itemView.findViewById(R.id.item_event_card_tv_title);
            this.mCategories = itemView.findViewById(R.id.item_event_card_tv_categories);
            this.mDate = itemView.findViewById(R.id.item_event_card_tv_date);
            this.mTime = itemView.findViewById(R.id.item_event_card_tv_time);
            this.mRoom = itemView.findViewById(R.id.item_event_card_tv_room);
            this.mParticipate = itemView.findViewById(R.id.item_event_card_iv_participate);
            this.mColor = itemView.findViewById(R.id.item_event_card_ll_color);
            this.mShowListener = showListener;
            this.context = context;
            this.mParticipate.setOnClickListener(this);
            itemView.findViewById(R.id.item_event_card_btn_show).setOnClickListener(this);
        }

        private void setEvent(Event event) {
            this.mEvent = event;
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.item_event_card_btn_show:
                    mShowListener.onClick(view, mEvent);
                    break;
                case R.id.item_event_card_iv_participate:
                    toggleParticipation();
                    break;
                default:
                    break;
            }
        }

        private void loadParticipation() {
            RequestHelper.getJson(context, "events/" + mEvent.getEventId() + "/participants/" + MainActivity.mUser.getUserId(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mParticipate.setImageResource(R.drawable.ic_favorite);
                    userIsParticipating = true;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mParticipate.setImageResource(R.drawable.ic_favorite_border);
                    userIsParticipating = false;
                }
            });
        }

        private void toggleParticipation() {
            if (!userIsParticipating) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userID", MainActivity.mUser.getUserId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHelper.postJson(context, "events/" + mEvent.getEventId() + "/participants", jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        userIsParticipating = true;
                        mParticipate.setImageResource(R.drawable.ic_favorite);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            } else {
                RequestHelper.delete(context, "events/" + mEvent.getEventId() + "/participants/" + MainActivity.mUser.getUserId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        userIsParticipating = false;
                        mParticipate.setImageResource(R.drawable.ic_favorite_border);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }
        }
    }
}
