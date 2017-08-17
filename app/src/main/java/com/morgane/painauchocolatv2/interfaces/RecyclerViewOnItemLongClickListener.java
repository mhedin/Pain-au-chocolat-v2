package com.morgane.painauchocolatv2.interfaces;

import android.view.View;

import com.morgane.painauchocolatv2.models.Participant;

/**
 * The interface allowing to manage the OnItemLongClick on an element of a RecyclerView.
 */
public interface RecyclerViewOnItemLongClickListener {

    /**
     * Manage the action to do after an ItemLongClick on an element of a RecyclerView.
     * @param v The view clicked.
     * @param participant The participant on which the view was displayed.
     */
    public void onItemLongClick(View v, Participant participant);
}
