Project 1 DB 2 - Tia Mehta 272326254

Section 1: Compilation and Running
I wrote this project in VSCode. To run the project, go to the Main.java class and run it. The program should
compile and execute. In the Main.java class, the program assumes that the files are located in the src folder
in a folder called Files. 

Section 2: After using the given tests, my program runs until PIN 7, and for some reason gets this error: 
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: -1
        at org.example.BufferPool.PIN(BufferPool.java:261)
        at org.example.Main.main(Main.java:86)
It does print out the necessary output before it crashes: The corresponding block # 7 cannot be accessed from 
the disk because the memory buffers are all full; Write was unsuccessful

Also, when writing back to File 1 on the disk instead of writing the entirety of the content array, it just
writes the record that has been changed. I didn't have time to figure out why this is happening, but I think 
it's because I need to change it to read/write the file. 

The program does run successfully for most of the commands.

Section 3: 
