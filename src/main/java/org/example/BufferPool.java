package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BufferPool {

    // array of 100 frames to hold the records
    private Frame[] buffers;

    // constuctor creates object
    public BufferPool(int size) {
        initialize(size);
    }

    // initialize insantiates the array of frames
    public void initialize(int size) {
        buffers = new Frame[size];
        for (int i = 0; i < size; i++) {
            buffers[i] = new Frame();
        }
    }

    // takes in block id and return its position in the buffer pool
    public int isFrameInBlock(int blockID) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockID() == blockID) {
                return i;
            }
        }
        return -1;
    }

    // populates a free frame in the buffer pool
    public int populateFreeFrame(int blockID) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockID() == -1) {
                buffers[i].setBlockID(blockID);
                return 0;
            }
        }
        return -1;
    }

    // return the content of a given blockID
    public byte[] getContent(int blockID, int frameNumber) {
        if (isFrameInBlock(blockID) != -1) {
            return this.buffers[frameNumber].getContent();
        } else {
            System.out.println("Block not available in buffer pool");
            return null;
        }
    }

    // set the content of a given blockID
    public void setContent(int blockID, byte[] content, int frameNumber) {
        if (isFrameInBlock(blockID) != -1) {
            this.buffers[frameNumber].setContent(content);
        } else {
            System.out.println("Block not available in buffer pool");
        }
    }

    // get record of a frame from a given blockID
    public String getRecord(int blockID, int recordNumber, int frameNumber) {
        if (isFrameInBlock(blockID) != -1) {
            return this.buffers[frameNumber].getRecord(recordNumber);
        } else {
            return null;
        }
    }

    // evict frame from buffer pool
    public void evictFrame(int blockID) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].isDirty() == false && buffers[i].isPinned() == false) {
                int fileNum = buffers[i].getBlockID();
                buffers[i].resetFrame();
                int frameNum = i + 1;
                System.out.println("File " + fileNum + " evicted from Frame " + frameNum);
                return;
            } else if (buffers[i].isDirty() == true && buffers[i].isPinned() == false) {
                // get file number
                int fileNum = buffers[i].getBlockID();
                // write content of frame to disk
                String content = new String(buffers[i].getContent());
                System.out.println("Content of frame: " + content);
                writeToFile(fileNum, content.getBytes());
                System.out.println("File updated successfully!");
                // set frame to empty
                buffers[i].resetFrame();
                int frameNum = i + 1;
                System.out.println("File " + fileNum + "evicted from Frame " + frameNum);
                return;

                // if all frames are pinned, print out error message
            } else if (areAllPinned(buffers) == true) {
                System.out.println("The corresponding block # " + blockID
                        + " cannot be accessed from the disk because the memory buffers are all full.");
                return;
            }
        }

    }

    public static void writeToFile(int fileNumber, byte[] content) {
        String filePath = "src\\Files\\F" + fileNumber + ".txt";
        try {
            Files.write(Paths.get(filePath), content);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public boolean isBufferFull() {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockID() == -1) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllPinned(Frame[] buffers) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].isPinned() == false) {
                return false;
            }
        }
        return true;
    }

    public void GET(BufferPool bufferPool, int recordNumber, int fileNumber, byte[] fileBytes) {
        // check if the frame is in the buffer pool
        int inMem = bufferPool.isFrameInBlock(fileNumber);
        if (inMem != -1) {
            // if the frame is in the block get the content of the record
            String content = bufferPool.getRecord(fileNumber, recordNumber, inMem);
            // print out the content of the record
            System.out.println("Output: " + content);
            // print out file and frame number
            int frameNumber = inMem + 1;
            System.out.println("  File " + fileNumber + " already in memory; Located in Frame " + frameNumber);

        } else if (inMem == -1 && bufferPool.isBufferFull() == false) {
            // populate a free frame with the block file
            bufferPool.populateFreeFrame(fileNumber);
            // set content of the frame
            int frameNumber = bufferPool.isFrameInBlock(fileNumber);
            bufferPool.setContent(fileNumber, fileBytes, frameNumber);
            // get content of the record
            String content = bufferPool.getRecord(fileNumber, recordNumber, frameNumber);
            // print out content
            System.out.println("Output: " + content);
            // print out file and frame number
            frameNumber = frameNumber + 1;
            System.out.println("  Brought File " + fileNumber + " from disk; Placed in Frame " + frameNumber);
        } else if (areAllPinned(buffers) == true) {
            return;
        } else if (bufferPool.isBufferFull() == true) {
            // evict a frame from the buffer pool
            bufferPool.evictFrame(fileNumber);
            // populate a free frame with the block file
            bufferPool.populateFreeFrame(fileNumber);
            // set content of the frame
            int frameNumber = bufferPool.isFrameInBlock(fileNumber);
            bufferPool.setContent(fileNumber, fileBytes, frameNumber);
            // get content of the record
            String content = bufferPool.getRecord(fileNumber, recordNumber, frameNumber);
            // print out content
            System.out.println("Output: " + content);
            // print out file and frame number
            frameNumber = frameNumber + 1;
            System.out.println("  Brought File " + fileNumber + " from disk; Placed in Frame " + frameNumber);
        }

    }

    public void SET(BufferPool bufferPool, int recordNumber, int fileNumber, byte[] fileBytes) {
        // check if the frame is in the buffer pool
        int inMem = bufferPool.isFrameInBlock(fileNumber);
        // if the frame is in the block, call the setRecord method
        if (inMem != -1) {
            // set the content of the record
            this.buffers[inMem].setRecord(recordNumber, fileBytes);
            // print out file and frame number
            int frameNumber = inMem + 1;
            System.out.println(
                    "Output: Write was successful; File " + fileNumber + " already in memory; Located in Frame "
                            + frameNumber);
        } else if (inMem == -1 && bufferPool.isBufferFull() == false) {
            // populate a free frame with the block file
            bufferPool.populateFreeFrame(fileNumber);
            // set content of the frame
            int frameNumber = bufferPool.isFrameInBlock(fileNumber);
            bufferPool.setContent(fileNumber, fileBytes, frameNumber);
            // set the content of the record
            this.buffers[frameNumber].setRecord(recordNumber, fileBytes);
            // print out file and frame number
            frameNumber = frameNumber + 1;
            System.out
                    .println("Output: Write was successful; Brought File " + fileNumber + " from disk; Placed in Frame "
                            + frameNumber);
        } else if (areAllPinned(buffers) == true) {
            System.out.println("The corresponding block # " + fileNumber
                    + " cannot be accessed from the disk because the memory buffers are all full; Write was unsuccessful");
            return;
        } else if (bufferPool.isBufferFull() == true) {
            // evict a frame from the buffer pool
            bufferPool.evictFrame(fileNumber);
            // populate a free frame with the block file
            bufferPool.populateFreeFrame(fileNumber);
            // set content of the frame
            int frameNumber = bufferPool.isFrameInBlock(fileNumber);
            bufferPool.setContent(fileNumber, fileBytes, frameNumber);
            // set the content of the record
            this.buffers[frameNumber].setRecord(recordNumber, fileBytes);
            // print out file and frame number
            frameNumber = frameNumber + 1;
            System.out
                    .println("Output: Write was successful; Brought File " + fileNumber + " from disk; Placed in Frame "
                            + frameNumber);
        }
    }

    public void PIN(BufferPool bufferPool, int fileNumber, byte[] fileBytes) {
        // check if all frames are pinned
        if (areAllPinned(buffers) == true) {
            System.out.println("The corresponding block # " + fileNumber
                    + " cannot be accessed from the disk because the memory buffers are all full; Write was unsuccessful");
        }
        // check if the frame is in the buffer pool
        int inMem = bufferPool.isFrameInBlock(fileNumber);
        // if the frame is in the block
        if (inMem != -1) {
            // check if already pinned
            if (bufferPool.buffers[inMem].isPinned() == true) {
                int frameNumber = inMem + 1;
                System.out
                        .println("Output: File " + fileNumber + " pinned in Frame " + frameNumber + "; Already pinned");
                return;
            } else if (bufferPool.buffers[inMem].isPinned() == false) {
                // if the frame is in the block set the frame to pinned
                bufferPool.buffers[inMem].setPinned(true);
                // print out file and frame number
                int frameNumber = inMem + 1;
                System.out.println(
                        "Output: File " + fileNumber + " pinned in Frame " + frameNumber + "; Not already pinned");
            }
            // if the frame is not in the block
        } else if (inMem == -1) {
            // check if buffer is full and if so evict a frame
            if (bufferPool.isBufferFull() == true) {
                bufferPool.evictFrame(fileNumber);
                // populate a free frame with the block file
                bufferPool.populateFreeFrame(fileNumber);
                // set content of the frame
                int frameNumber = bufferPool.isFrameInBlock(fileNumber);
                bufferPool.setContent(fileNumber, fileBytes, frameNumber);
                bufferPool.buffers[frameNumber].setPinned(true);

                // print out file and frame number
                frameNumber = frameNumber + 1;
                System.out.println(
                        "Output: File " + fileNumber + " pinned in Frame " + frameNumber + "; Not already pinned");
            } else if (bufferPool.isBufferFull() == false) {
                // populate a free frame with the block file
                bufferPool.populateFreeFrame(fileNumber);
                // set content of the frame
                int frameNumber = bufferPool.isFrameInBlock(fileNumber);
                bufferPool.setContent(fileNumber, fileBytes, frameNumber);
                bufferPool.buffers[frameNumber].setPinned(true);
                // print out file and frame number
                frameNumber = frameNumber + 1;
                System.out.println(
                        "Output: File " + fileNumber + " pinned in Frame " + frameNumber + "; Not already pinned");
            }
        }
    }

    public void UNPIN(BufferPool bufferPool, int fileNumber) {
        // check if the frame is in the buffer pool
        int inMem = bufferPool.isFrameInBlock(fileNumber);
        // if the frame is in the block set pinned flag to false
        if (inMem != -1) {
            if (bufferPool.buffers[inMem].isPinned() == false) {
                int frameNumber = inMem + 1;
                System.out.println("Output: File " + fileNumber + " unpinned in Frame " + frameNumber
                        + "; Already unpinned");
                return;
            } else {
                bufferPool.buffers[inMem].setPinned(false);
                // print out file and frame number
                int frameNumber = inMem + 1;
                System.out.println(
                        "Output: File " + fileNumber + " unpinned in Frame " + frameNumber + "; Not already unpinned");
            }
        } else if (inMem == -1) {
            System.out.println(
                    "The corresponding block " + fileNumber + " cannot be unpinned because it is not in memory");
        }

    }
}
