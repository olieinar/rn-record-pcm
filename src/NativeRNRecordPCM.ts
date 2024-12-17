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

RNRecordPCM.addRecordingEventListener = (listener: (event: { status: string }) => void) => {
  return eventEmitter.addListener('recording', listener);
};

export default RNRecordPCM;