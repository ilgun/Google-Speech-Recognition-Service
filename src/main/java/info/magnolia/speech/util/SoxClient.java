package info.magnolia.speech.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.protobuf.ByteString;

import ie.corballis.sox.SoXEffect;
import ie.corballis.sox.Sox;
import ie.corballis.sox.WrongParametersException;

/**
 * .TODO:WIll come later.
 */
public class SoxClient {

    /**
     * Converts stereo audio to mono by using {@link Sox} client.
     */
    public ByteString convertWavToMono(byte[] audioBytes) {
        File inputFile;
        try {
            inputFile = File.createTempFile("tempStereoFile", ".wav");
            FileUtils.writeByteArrayToFile(inputFile, audioBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File outputFile = inputFile.toPath().getParent().resolve(FilenameUtils.getBaseName(inputFile.toPath().getFileName().toString()) + "-converted.wav").toFile();

        Sox sox = new Sox("/usr/local/bin/sox");
        try {
            sox.inputFile(inputFile.getAbsolutePath());
            sox.outputFile(outputFile.getAbsolutePath());
            sox.effect(SoXEffect.REMIX, "1");
            sox.execute();
        } catch (WrongParametersException | IOException e) {
            throw new RuntimeException(e);
        }

        try {
            return ByteString.copyFrom(Files.readAllBytes(outputFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            inputFile.delete();
            outputFile.delete();
        }
    }
}
