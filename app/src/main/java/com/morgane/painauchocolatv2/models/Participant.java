package com.morgane.painauchocolatv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Class of Participant, used to store it in the database.
 */
public class Participant extends RealmObject implements Parcelable {

    @PrimaryKey
    private long id;

    /**
     * The name of the participant.
     */
    private String name;

    /**
     * Status of the participant : true if he has already bring the breakfast for this session, false otherwise.
     */
    private boolean hasAlreadyBring;

    /**
     * Flag indicating if the participant is the next one to bring the breakfast or not.
     */
    private boolean isTheActualBringer;

    /**
     * Default constructor.
     */
    public Participant() {

    }

    /**
     * Constructor of the participant with his name.
     * @param name The name of the participant.
     */
    public Participant(String name) {
        this.name = name;
        this.hasAlreadyBring = false;
    }

    /**
     * Constructor of the participant when he is created through Parcelable use.
     * @param in The Parcel containing all the information about the participant.
     */
    public Participant(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.hasAlreadyBring = in.readByte() == 1;
        this.isTheActualBringer = in.readByte() == 1;
    }

    /**
     * Get the id of the participant.
     * @return The id of the participant.
     */
    public long getId() {
        return id;
    }

    /**
     * Update the id of the participant.
     * @param id The new value of the id of the participant.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the name of the participant.
     * @return The id name the participant.
     */
    public String getName() {
        return name;
    }

    /**
     * Update the name of the participant.
     * @param name The new value of the name of the participant.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the status of the participant : has he already bring the breakfast for this session or not.
     * @return The status of the participant.
     */
    public boolean hasAlreadyBring() {
        return hasAlreadyBring;
    }

    /**
     * Update the status of the participant.
     * @param hasAlreadyBring The new value of the status of the participant.
     */
    public void setHasAlreadyBring(boolean hasAlreadyBring) {
        this.hasAlreadyBring = hasAlreadyBring;
    }

    /**
     * Used to know if the participant is the next to bring the breakfast.
     * @return True if the participant is the next one to bring the breakfast, false otherwise.
     */
    public boolean isTheActualBringer() {
        return isTheActualBringer;
    }

    /**
     * Update the flag indicating if the participant is the next one to bring the breakfast.
     * @param isTheActualBringer The new value of the flag indicating if the participant is the next one to bring the breakfast.
     */
    public void setIsTheActualBringer(boolean isTheActualBringer) {
        this.isTheActualBringer = isTheActualBringer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeByte((byte) (hasAlreadyBring ? 1 : 0));
        dest.writeByte((byte) (isTheActualBringer ? 1 : 0));
    }

    public static final Parcelable.Creator<Participant> CREATOR = new Parcelable.Creator<Participant>()
    {
        @Override
        public Participant createFromParcel(Parcel source)
        {
            return new Participant(source);
        }

        @Override
        public Participant[] newArray(int size)
        {
            return new Participant[size];
        }
    };

    /**
     * Get a random participant which has not bring the breakfast for this session yet.
     * @param realm The Realm instance.
     * @return A random participant which has not bring the breakfast for this session yet.
     */
    public static Participant getRandomPotentialBringer(Realm realm) {
        // Get all the potential bringers
        RealmResults<Participant> results = realm.where(Participant.class).equalTo("hasAlreadyBring", false).findAll();

        // Generate a random number to select a bringer randomly
        Random random = new Random(System.nanoTime());
        int randomPosition = random.nextInt(results.size());

        // Return the random bringer selected
        return results.get(randomPosition);
    }

    /**
     * Check if the name of the participant already exists in the database.
     * @param name The name of the participant to check.
     * @param realm The Realm instance.
     * @return True if the participant already exists, false otherwise.
     */
    public static boolean doesParticipantAlreadyExist(String name, Realm realm) {
        return !realm.where(Participant.class).equalTo("name", name).findAll().isEmpty();
    }
}
