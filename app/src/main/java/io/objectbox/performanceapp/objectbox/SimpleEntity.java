package io.objectbox.performanceapp.objectbox;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Generated;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NotNull;

/**
 * Entity mapped to table "SIMPLE_ENTITY_NOT_NULL".
 */
@Entity
public class SimpleEntity {

    @Id
    private long id;
    private boolean simpleBoolean;
    private byte simpleByte;
    private short simpleShort;
    private int simpleInt;
    private long simpleLong;
    private float simpleFloat;
    private double simpleDouble;

    private String simpleString;
    
    private byte[] simpleByteArray;

    @Generated(hash = 1682830787)
    public SimpleEntity() {
    }

    public SimpleEntity(long id) {
        this.id = id;
    }

    @Generated(hash = 1181757593)
    public SimpleEntity(long id, boolean simpleBoolean, byte simpleByte, short simpleShort, int simpleInt, long simpleLong, float simpleFloat, double simpleDouble, String simpleString, byte[] simpleByteArray) {
        this.id = id;
        this.simpleBoolean = simpleBoolean;
        this.simpleByte = simpleByte;
        this.simpleShort = simpleShort;
        this.simpleInt = simpleInt;
        this.simpleLong = simpleLong;
        this.simpleFloat = simpleFloat;
        this.simpleDouble = simpleDouble;
        this.simpleString = simpleString;
        this.simpleByteArray = simpleByteArray;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getSimpleBoolean() {
        return simpleBoolean;
    }

    public void setSimpleBoolean(boolean simpleBoolean) {
        this.simpleBoolean = simpleBoolean;
    }

    public byte getSimpleByte() {
        return simpleByte;
    }

    public void setSimpleByte(byte simpleByte) {
        this.simpleByte = simpleByte;
    }

    public short getSimpleShort() {
        return simpleShort;
    }

    public void setSimpleShort(short simpleShort) {
        this.simpleShort = simpleShort;
    }

    public int getSimpleInt() {
        return simpleInt;
    }

    public void setSimpleInt(int simpleInt) {
        this.simpleInt = simpleInt;
    }

    public long getSimpleLong() {
        return simpleLong;
    }

    public void setSimpleLong(long simpleLong) {
        this.simpleLong = simpleLong;
    }

    public float getSimpleFloat() {
        return simpleFloat;
    }

    public void setSimpleFloat(float simpleFloat) {
        this.simpleFloat = simpleFloat;
    }

    public double getSimpleDouble() {
        return simpleDouble;
    }

    public void setSimpleDouble(double simpleDouble) {
        this.simpleDouble = simpleDouble;
    }

    @NotNull
    public String getSimpleString() {
        return simpleString;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSimpleString(@NotNull String simpleString) {
        this.simpleString = simpleString;
    }

    @NotNull
    public byte[] getSimpleByteArray() {
        return simpleByteArray;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSimpleByteArray(@NotNull byte[] simpleByteArray) {
        this.simpleByteArray = simpleByteArray;
    }

}
