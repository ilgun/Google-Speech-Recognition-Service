package info.magnolia.speech;

import info.magnolia.speech.util.SoxClient;

import java.util.List;

import com.google.cloud.speech.spi.v1.SpeechClient;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

public class GoogleSpeechRecognitionService implements SpeechRecognitionService {

    private final SoxClient soxClient;

    public GoogleSpeechRecognitionService() {
        soxClient = new SoxClient();
    }

    /**
     * Does speech recognition by using {@link SpeechClient}.
     */
    @Override
    public String recognise(byte[] wavData) {
        ByteString monoAudioBytes = soxClient.convertWavToMono(wavData);

        try {
            try (SpeechClient speechClient = SpeechClient.create()) {
                RecognitionConfig config = RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(44100)
                        .setLanguageCode("en-US")
                        .build();

                RecognitionAudio audio = RecognitionAudio.newBuilder()
                        .setContent(monoAudioBytes)
                        .build();

                RecognizeResponse response = speechClient.recognize(config, audio);
                List<SpeechRecognitionResult> results = response.getResultsList();

                String translation = "No translation found";
                for (SpeechRecognitionResult result : results) {
                    List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
                    for (SpeechRecognitionAlternative alternative : alternatives) {
                        translation = alternative.getTranscript();
                    }
                }
                return translation;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
