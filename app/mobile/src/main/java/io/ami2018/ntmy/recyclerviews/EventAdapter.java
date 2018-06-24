package io.ami2018.ntmy.recyclerviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import io.ami2018.ntmy.MainActivity;
import io.ami2018.ntmy.R;
import io.ami2018.ntmy.model.Event;
import io.ami2018.ntmy.network.RequestHelper;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> list;
    private EventClickListener showListener;
    private Context context;

    public EventAdapter(Context context, EventClickListener showListener) {
        this.list = new ArrayList<>();
        this.showListener = showListener;
        this.context = context;
    }

    public void addElement(Event event) {
        if (!this.contains(event.getEventId())) {
            this.list.add(event);
            this.list.sort(new Comparator<Event>() {
                @Override
                public int compare(Event event, Event t1) {
                    String start = event.getStart();
                    String start1 = t1.getStart();
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
            });
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
        return new EventViewHolder(view, showListener, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventAdapter.EventViewHolder holder, int position) {
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
        holder.mColor.setBackgroundColor(event.getColor().getColor());
        if (event.getRoom() != null)
            holder.mRoom.setText(event.getRoom().getName());
        else
            holder.mRoom.setText(context.getString(R.string.not_assigned));
        holder.setEvent(event);
        RequestHelper.getJson(context, "events/" + event.getEventId() + "/participants/" + MainActivity.mUser.getUserId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                holder.mParticipate.setImageResource(R.drawable.ic_favorite);
                holder.userIsParticipating = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                holder.userIsParticipating = false;
            }
        });
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

        private void toggleParticipation() {
            if (!userIsParticipating) {
                userIsParticipating = true;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userID", MainActivity.mUser.getUserId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHelper.postJson(context, "events/" + mEvent.getEventId() + "/participants", jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mParticipate.setImageResource(R.drawable.ic_favorite);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            } else {
                userIsParticipating = false;
                RequestHelper.delete(context, "events/" + mEvent.getEventId() + "/participants/" + MainActivity.mUser.getUserId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
