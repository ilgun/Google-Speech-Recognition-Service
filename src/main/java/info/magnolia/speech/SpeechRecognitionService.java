package info.magnolia.speech;


import java.util.Optional;

public interface SpeechRecognitionService {

    Optional<String> recognise(byte[] wavData);
}
