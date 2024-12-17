# RNRecordPCM

A React Native module for recording audio in raw PCM format, providing real-time audio data for further processing or storage.

## Installation

```sh
npm install rn-record-pcm
```

## Usage


```ts
import { PermissionsAndroid } from "react-native";
import RNRecordPCM from "react-native-recording";

// Request permissions for RECORD_AUDIO
const requestPermissions = async () => {
  try {
    const result = await PermissionsAndroid.requestMultiple([
      PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
    ]);

    if (result[PermissionsAndroid.PERMISSIONS.RECORD_AUDIO] !== "granted") {
      console.error("Permission to record audio was denied");
      return false;
    }
    return true;
  } catch (err) {
    console.error("Permission request failed:", err);
    return false;
  }
};

const startRecording = async () => {
  // Check permissions
  const hasPermission = await requestPermissions();
  if (!hasPermission) return;

  // Initialize recording
  RNRecordPCM.init({
    bufferSize: 4096,
    sampleRate: 44100,
    bitsPerChannel: 16,
    channelsPerFrame: 1,
  });

  // Add a listener for recording events
  const listener = RNRecordPCM.addRecordingEventListener((data: number[]) => {
    console.log("Recording data:", data);
  });

  // Start recording
  RNRecordPCM.start();

  // Stop recording after a certain time (example: 5 seconds)
  setTimeout(() => {
    RNRecordPCM.stop();
    listener.remove();
    console.log("Recording stopped and listener removed.");
  }, 5000);
};

startRecording();
```
