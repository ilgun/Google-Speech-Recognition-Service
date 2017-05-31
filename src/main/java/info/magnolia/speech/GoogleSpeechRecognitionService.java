package info.magnolia.speech;

import static java.util.Optional.empty;

import info.magnolia.speech.util.SoxClient;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.speech.spi.v1.SpeechClient;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

public class GoogleSpeechRecognitionService implements SpeechRecognitionService {

    private static final Logger log = LoggerFactory.getLogger(GoogleSpeechRecognitionService.class);

    private final SoxClient soxClient;

    public GoogleSpeechRecognitionService() {
        soxClient = new SoxClient();
    }

    /**
     * Does speech recognition by using {@link SpeechClient}.
     */
    @Override
    public Optional<String> recognise(byte[] wavData) {
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

                for (SpeechRecognitionResult result : results) {
                    List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
                    for (SpeechRecognitionAlternative alternative : alternatives) {
                        // TODO: should take care of other alternatives.
                        return Optional.of(alternative.getTranscript());
                    }
                }
                return empty();
            }
        } catch (Exception e) {
            log.error("Exception occurred while doing speech recognition.", e);
            return empty();
        }
    }
}
