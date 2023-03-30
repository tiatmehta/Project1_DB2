package org.example;


public class BufferPool {

    //array of 100 frames to hold the records
    private Frame[] buffers;

    //constuctor creates object
    public BufferPool(int size) {
        initialize(size);
    }

    //initialize insantiates the array of frames
    public void initialize(int size) {
        buffers = new Frame[size];
        for (int i = 0; i < size; i++) {
            buffers[i] = new Frame();
        }
    }

    //takes in block id and return its position in the buffer pool
    public int isFrameInBlock(int blockID) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockID() == blockID) {
                return i;
            }
        }
        return -1;
    }

    //populates a free frame in the buffer pool
    public int populateFreeFrame(int blockID) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockID() == -1) {
                buffers[i].setBlockID(blockID);

                return 0;
            }
        }

        return -1;
    }

    //return the content of a given blockID
    public byte[] getContent(int blockID, int frameNumber) {
        if (isFrameInBlock(blockID) != -1) {
            return this.buffers[frameNumber].getContent();
        } else {
            System.out.println("Block not available in buffer pool");
            return null;
        }
    }

    //set the content of a given blockID
    public void setContent(int blockID, byte[] content, int frameNumber) {
        if (isFrameInBlock(blockID) != -1) {
            this.buffers[frameNumber].setContent(content);
        } else {
            System.out.println("Block not available in buffer pool");
        }
    }

    //get record of a frame from a given blockID
    public String getRecord(int blockID, int recordNumber, int frameNumber) {
        if (isFrameInBlock(blockID) != -1) {
            return this.buffers[frameNumber].getRecord(recordNumber);
        } else {
            System.out.println("Block not available in buffer pool");
            return null;
        }
    }

    /*
    public void readBlockFromDisk(int blockID) {
        //find empty frame in buffer pool
        int frameNumber = findFreeFrame();
        if (frameNumber == -1) {
            //if no empty frame, evict a frame
            evictFrame();
            frameNumber = findFreeFrame();
        }

        //read block from disk (Files)
        
    }
    */

    /* 
    public void evictFrame() {
        for (int i = 0; i < buffers.length; i++) {
            if (!buffers[i].isPinned()) {
                if (buffers[i].isDirty()) {
                    writeBlockToDisk(buffers[i].getBlockID());
                }
                buffers[i].setBlockID(-1);
                return;
            }
        }
    }
    */
     
}
