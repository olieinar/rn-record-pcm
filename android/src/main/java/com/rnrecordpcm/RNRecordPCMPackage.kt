package com.rnrecordpcm

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import java.util.HashMap

class RNRecordPCMPackage : BaseReactPackage() {
	override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
		return if (name == RNRecordPCMModule.NAME) {
			RNRecordPCMModule(reactContext)
		} else {
			null
		}
	}

	override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
		return ReactModuleInfoProvider {
		val moduleInfos: MutableMap<String, ReactModuleInfo> = HashMap()
		moduleInfos[RNRecordPCMModule.NAME] = ReactModuleInfo(
			RNRecordPCMModule.NAME,
			RNRecordPCMModule.NAME,
			false,  // canOverrideExistingModule
			false,  // needsEagerInit
			true,  // hasConstants
			false,  // isCxxModule
			true // isTurboModule
		)
		moduleInfos
		}
	}
}
