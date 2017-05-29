# Google Speech Recognition Service


Provides functionality to utilise google-speech-recognition services.

* Internally converts stereo audio to mono since currently google-speech-recognition service only accepts mono.
* Tries to delete the given audio file and also the converted mono version of it from the file system.