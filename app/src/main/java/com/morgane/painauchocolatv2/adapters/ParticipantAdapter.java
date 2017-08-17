package com.morgane.painauchocolatv2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.morgane.painauchocolatv2.R;

import com.morgane.painauchocolatv2.interfaces.RecyclerViewOnItemLongClickListener;
import com.morgane.painauchocolatv2.models.Participant;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * The adapter used to display the list of the participants.
 */
public class ParticipantAdapter extends RealmRecyclerViewAdapter<Participant, ParticipantAdapter.ViewHolder> {

    /**
     * Listener used to manage the OnLongClick event on an item of the view.
     */
    private RecyclerViewOnItemLongClickListener onItemLongClickListener;

    /**
     * Constructor of the adapter.
     * @param items The list of participants to display.
     * @param onItemLongClickListener The listener to used to manage the OnLongClick events.
     */
    public ParticipantAdapter(OrderedRealmCollection<Participant> items, RecyclerViewOnItemLongClickListener onItemLongClickListener) {
        super(items, true);
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public ParticipantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ParticipantAdapter.ViewHolder holder, int position) {
        final Participant participant = getItem(position);

        holder.name.setText(participant.getName());

        // Select a different drawable if the patient is the next bringer, if he has already bring the breakfast or not yet.
        int leftDrawable = participant.isTheActualBringer() ? R.drawable.ic_actual_bringer :
                participant.hasAlreadyBring() ? R.drawable.ic_check_mark : R.mipmap.ic_launcher;
        holder.name.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, 0, 0, 0);

        holder.name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onItemLongClick(v, participant);
                return false;
            }
        });
    }

    /**
     * The ViewHolder of the view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * TextView used to display the name of the participant.
         */
        public TextView name;

        /**
         * Constructor of the ViewHolder.
         * @param itemView The view displayed to the user.
         */
        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.participant_textView);
        }
    }
}
