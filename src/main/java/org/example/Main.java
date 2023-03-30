package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {

		// ask user to input a number for buffer size
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the buffer size: ");
		int bufferSize = input.nextInt();
		// System.out.println("Buffer size is: " + bufferSize);

		// create a buffer pool object
		BufferPool bufferPool = new BufferPool(bufferSize);
		bufferPool.initialize(bufferSize);
		System.out.println("Buffer pool created.");

		Scanner command = new Scanner(System.in);

		while (true) {
			// ask user for command
			String commandInput = "";
			System.out.println("The program is ready for the next command: ");
			if (command.hasNextLine()) {
				commandInput = command.nextLine();
			} else if (commandInput.equals("EXIT")) {
				System.out.println("Program terminated.");
				break;
			}

			// split the command into parts and get rid of the " in parts[2]
			String[] parts = commandInput.split(" ");
			String firstWord = parts[0];

			// get record number
			int recordNumber = Integer.parseInt(parts[1]);

			// calculate file number from record number
			int recordsPerFile = 100;
			int fileNumber = (recordNumber - 1) / recordsPerFile + 1;

			// format string to get the correct file and record
			String filePath = "src\\Files\\F" + fileNumber + ".txt";
			byte[] fileBytes = null;
			try {
				fileBytes = Files.readAllBytes(Paths.get(filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}

			// check if the command is valid and run the correct methods (has to be GET,
			// SET, PIN or UNPIN)
			if (firstWord.equals("GET")) {
				GET(bufferPool, recordNumber, fileNumber, fileBytes);

			} else if (firstWord.equals("SET")) {

				// get the letters after the " in parts[2]
				// String value = parts[2].substring(1, parts[2].length() - 1);
				// take first three letters of value and put them in a string
				// String firstThree = value.substring(0, 3);

				System.out.println("Command is SET");
			} else if (firstWord.equals("PIN")) {
				System.out.println("Command is PIN");
			} else if (firstWord.equals("UNPIN")) {
				System.out.println("Command is UNPIN");
			} else {
				System.out.println("Invalid command. Please enter GET, SET, PIN or UNPIN.");
			}
		}
		input.close();
		command.close();
	}

	public static void GET(BufferPool bufferPool, int recordNumber, int fileNumber, byte[] fileBytes) {
		// check if the block file is in memory (buffer pool)
		// if it is in memory, get the content of the block file
		// if it isn't in memory, read the block file from disk
		int inMem = bufferPool.isFrameInBlock(fileNumber);
		if (inMem != -1) {
			byte[] content = bufferPool.getContent(fileNumber, inMem);
			System.out.println("Content of block is: " + content);

		} else if (inMem == -1) {
			// populate a free frame with the block file
			bufferPool.populateFreeFrame(fileNumber);
			// set content of the frame
			int frameNumber = bufferPool.isFrameInBlock(fileNumber);
			bufferPool.setContent(fileNumber, fileBytes, frameNumber);
			// get content of the record
			String content = bufferPool.getRecord(fileNumber, recordNumber, frameNumber);
			// convert to string
			System.out.println("Content of block is: " + content);

		} else {
			System.out.println("The corresponding block # " + recordNumber
					+ " cannot be accessed from the disk because the memory buffers are all full.");
		}
		// System.out.println("Command is GET");
	}

}