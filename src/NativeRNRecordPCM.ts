import type { TurboModule } from 'react-native';
import { TurboModuleRegistry, NativeEventEmitter, NativeModules } from 'react-native';

export interface Spec extends TurboModule {
  init: (options: { [key: string]: string | number }) => void;
  start: () => void;
  stop: () => void;
  addRecordingEventListener: (listener: (event: { status: string }) => void) => void;
}

const RNRecordPCM = TurboModuleRegistry.getEnforcing<Spec>('RNRecordPCM');
const eventEmitter = new NativeEventEmitter(NativeModules.RNRecordPCM);

// Define a type for the wrapped method with `remove`
type Subscription = { remove: () => void };

const addRecordingEventListener = (listener: (event: { status: string }) => void): Subscription => {
  const subscription = eventEmitter.addListener('recording', listener);
  return {
    remove: () => subscription.remove(),
  };
};

// Attach the wrapper method to RNRecordPCM (TypeScript-friendly override)
(RNRecordPCM as any).addRecordingEventListener = addRecordingEventListener;

export default RNRecordPCM as RNRecordPCMWithListener;

interface RNRecordPCMWithListener extends Spec {
  addRecordingEventListener: (listener: (event: { status: string }) => void) => Subscription;
}