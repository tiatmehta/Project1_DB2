package org.example;

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
        setDirty(true);
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
    public void setRecord(int recordNumber, String record) {
        if (recordNumber < 0 || recordNumber >= 100) { // check if record number is between 0 and 100
            throw new IllegalAccessError("Record number must be between 0 and 100");
        }

        int offset = recordNumber * 40; // calculate offset of the record

        // copy the record from the record array to the block
        for (int i = 0; i < 40; i++) {
            content[offset + i] = record.getBytes()[i];
        }

        setDirty(true); // set dirty to true since the content of the block has changed
    }
}
