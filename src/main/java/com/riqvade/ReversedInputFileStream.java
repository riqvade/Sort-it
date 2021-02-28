package com.riqvade;

import java.io.*;

public class ReversedInputFileStream extends InputStream {
    RandomAccessFile in;

    long currentLineStart = -1;
    long currentLineEnd = -1;
    long currentPos = -1;
    long lastPosInFile = -1;
    int lastChar = -1;


    public ReversedInputFileStream(File file) throws FileNotFoundException {
        in = new RandomAccessFile(file, "r");
        currentLineStart = file.length();
        currentLineEnd = file.length();
        lastPosInFile = file.length() -1;
        currentPos = currentLineEnd;

    }

    private void findPrevLine() throws IOException {
        if (lastChar == -1) {
            in.seek(lastPosInFile);
            lastChar = in.readByte();
        }

        currentLineEnd = currentLineStart;

        if (currentLineEnd == 0) {
            currentLineEnd = -1;
            currentLineStart = -1;
            currentPos = -1;
            return;
        }
        long filePointer = currentLineStart -1;
        while ( true) {
            filePointer--;
            if (filePointer < 0) {
                break;
            }

            in.seek(filePointer);
            int readByte = in.readByte();

            if (readByte == 0xA && filePointer != lastPosInFile ) {
                break;
            }
        }
        currentLineStart = filePointer + 1;
        currentPos = currentLineStart;
    }

    public int read() throws IOException {

        if (currentPos < currentLineEnd ) {
            in.seek(currentPos++);
            int readByte = in.readByte();
            return readByte;
        } else if (currentPos > lastPosInFile && currentLineStart < currentLineEnd) {
            findPrevLine();
            if (lastChar != '\n' && lastChar != '\r') {
                return '\n';
            } else {
                return read();
            }
        } else if (currentPos < 0) {
            return -1;
        } else {
            findPrevLine();
            return read();
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
