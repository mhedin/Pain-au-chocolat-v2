package com.morgane.painauchocolatv2.interfaces;

import com.morgane.painauchocolatv2.models.Participant;

/**
 * The interface allowing to manipulate the database.
 */
public interface DatabaseManipulation {

    /**
     * Insert a new participant in the database.
     * @param name The name of the new participant.
     */
    public void insertInDatabase(String name);

    /**
     * Update the status of a participant in the database.
     * @param participant The participant to update.
     * @param newHasAlreadyBringValue The new status of bring of the participant.
     * @param newIsTheActualBringerValue The new value to know if the participant is the next one to bring the breakfast or not.
     */
    public void updateInDatabase(Participant participant, boolean newHasAlreadyBringValue, boolean newIsTheActualBringerValue);

    /**
     * Delete an exisiting participant from the database.
     * @param participant The participant to delete.
     */
    public void removeFromDatabase(Participant participant);
}
