package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

	static int bufferSize = 0;

	public static void main(String[] args) {

		// ask user to input a number for buffer size
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the buffer size: ");
		bufferSize = input.nextInt();
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
			byte[] fileBytesPin = null;
			try {
				fileBytes = Files.readAllBytes(Paths.get(filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}

			// check if the command is valid and run the correct methods (has to be GET,
			// SET, PIN or UNPIN)
			if (firstWord.equals("GET")) {

				bufferPool.GET(bufferPool, recordNumber, fileNumber, fileBytes);

			} else if (firstWord.equals("SET")) {

				int firstQuoteIndex = commandInput.indexOf("\"");
				int secondQuoteIndex = commandInput.indexOf("\"", firstQuoteIndex + 1);
				String textInQuotes = commandInput.substring(firstQuoteIndex + 1, secondQuoteIndex);
				byte[] byteArray = textInQuotes.getBytes();

				bufferPool.SET(bufferPool, recordNumber, fileNumber, byteArray);

			} else if (firstWord.equals("PIN")) {
				String fileForPin = parts[1];
				String filePathPin = "src\\Files\\F" + fileForPin + ".txt";

				try {
					fileBytesPin = Files.readAllBytes(Paths.get(filePathPin));
				} catch (Exception e) {
					e.printStackTrace();
				}

				// convert string to int
				int pinNumber = Integer.parseInt(fileForPin);

				bufferPool.PIN(bufferPool, pinNumber, fileBytesPin);
				// System.out.println("Command is PIN");
			} else if (firstWord.equals("UNPIN")) {

				String fileForUnpin = parts[1];
				int unpinNumber = Integer.parseInt(fileForUnpin);

				bufferPool.UNPIN(bufferPool, unpinNumber);

			} else {
				System.out.println("Invalid command. Please enter GET, SET, PIN or UNPIN.");
			}
		}
		input.close();
		command.close();
	}

}