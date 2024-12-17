import { useState, useEffect } from 'react';
import { View, Button, PermissionsAndroid, Alert } from 'react-native';
import RNRecordPCM from 'rn-record-pcm';

export default function App() {
  	const [isRecording, setIsRecording] = useState(false);

  	// Request audio recording permissions on mount
  	useEffect(() => {
		const requestPermissions = async () => {
			const RECORD_AUDIO = PermissionsAndroid.PERMISSIONS.RECORD_AUDIO;
		  
			if (!RECORD_AUDIO) {
			  console.warn('RECORD_AUDIO permission is undefined.');
			  return;
			}
		  
			try {
				const result = await PermissionsAndroid.request(RECORD_AUDIO, {
					title: 'Audio Recording Permission',
					message: 'This app needs access to your microphone to record audio.',
					buttonPositive: 'OK',
				});
		  
				if (result === PermissionsAndroid.RESULTS.GRANTED) {
					console.log('Permission granted');
				} else {
					Alert.alert('Permissions Required', 'Audio recording permission is required to continue.');
				}
			} catch (err) {
			  console.warn('Permission request error:', err);
			}
		};		  

		requestPermissions();

		// Cleanup on unmount
		return () => {
			RNRecordPCM.stop();
			console.log('App unmounted');
		};
  	}, []);

	// Process audio data from the native module
	const processData = (data: any) => {
		const view = new Uint16Array(data);
		console.log('Audio Data:', view);
	};

	// Start recording
	const startRecording = () => {
		RNRecordPCM.init({
			bufferSize: 1280,
			sampleRate: 16000,
			bitsPerChannel: 16,
			channelsPerFrame: 1,
		});

		const listener = RNRecordPCM.addRecordingEventListener((data: any) => {
			processData(data);
		});

		RNRecordPCM.start();
		setIsRecording(true);

		// Remove listener on cleanup
		return () => listener.remove();
	};

	// Stop recording
	const stopRecording = () => {
		RNRecordPCM.stop();
		setIsRecording(false);
	};

	return (
		<View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
			{isRecording ? (
				<Button title="Stop Recording" onPress={stopRecording} />
			) : (
				<Button title="Start Recording" onPress={startRecording} />
			)}
		</View>
	);
};