package org.example;

import java.util.Arrays;

public class Frame {
    private byte[] content; // array of bytes to hold content of a frame
    private boolean dirty; // set to True if the content of this block has changed and need to be written
                           // to disk when this frame is taken out
    private boolean pinned; // true if there is a request to keep this block in memory and not take it out
    private int blockID; // id of the block stored in this frame

    public Frame() {
        // array of 40 bytes to hold the content of a record
        this.content = new byte[4096];
        this.dirty = false;
        this.pinned = false;
        this.blockID = -1;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public int getBlockID() {
        return blockID;
    }

    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }

    public void resetFrame() {
        this.content = null;
        this.dirty = false;
        this.pinned = false;
        this.blockID = -1;
    }

    // return a specified record from the block
    public String getRecord(int recordNumber) {
        // parse through the file (byte array) and return the correct record
        String bytes = new String(content);
        String recString = "Rec" + String.format("%03d", recordNumber);
        int index = bytes.indexOf(recString);

        if (index != -1) {
            int startIndex = bytes.lastIndexOf("F", index);
            int endIndex = bytes.indexOf(".", index) + 1;
            String record = bytes.substring(startIndex, endIndex);
            System.out.println("Record found: " + record);
            return record;
        } else {
            System.out.println("Record not found");
            return null;
        }
    }

    // set a specified record in the block
    public void setRecord(int recordNumber, byte[] newRecord) {
        // parse through the file (byte array) and find the location of the record to be changed
        int recordInArray = findValueInByteArray(this.content, newRecord);
        //System.out.println("Record found at index: " + recordInArray);
        // replace the record with the new record   
        if (recordInArray != -1) {
            for (int i = 0; i < newRecord.length; i++) {
                this.content[recordInArray + i] = newRecord[i];
            }
        }

        setDirty(true); // set dirty to true since the content of the block has changed
    }

    public static int findValueInByteArray(byte[] array, byte[] newRecord) {
        byte[] value = Arrays.copyOfRange(newRecord, 0, 10);
        for (int i = 0; i < value.length - value.length + 1; i++) {
            boolean found = true;
            for(int j = 0; j < value.length; j++) {
                if (array[i + j] != value[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }
}
