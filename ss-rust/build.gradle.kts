import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.library")
    id("org.mozilla.rust-android-gradle.rust-android")
}

var targetAbi = ""
if (gradle.startParameter.taskNames.isNotEmpty()) {
    if (gradle.startParameter.taskNames.size == 1) {
        val targetTask = gradle.startParameter.taskNames[0].toLowerCase()
        if (targetTask.contains("arm64")) {
            targetAbi = "arm64"
        } else if (targetTask.contains("arm")) {
            targetAbi = "arm"
        }
    }
}

android {

    ndkVersion = "27.2.12479018"

    compileSdk = 34
    defaultConfig {
        minSdk = 23
        targetSdk = 34
    }
    buildToolsVersion = "34.0.0"
    namespace = "io.nekohasekai.ss_rust"

    if (targetAbi.isNotBlank()) splits.abi {
        reset()
        include(* when (targetAbi) {
            "arm" -> arrayOf("armeabi-v7a")
            "arm64" -> arrayOf("arm64-v8a")
            else -> arrayOf("x86", "x86_64")
        })
    }

}

cargo {
    val ndkDir = android.ndkDirectory
    module = "src/main/rust/shadowsocks-rust"
    libname = "ss-local"
    targets = when {
        targetAbi.isBlank() -> listOf("arm", "arm64", "x86", "x86_64")
        targetAbi == "arm" -> listOf("arm")
        targetAbi == "arm64" -> listOf("arm64")
        else -> listOf("arm", "arm64")
    }
    profile = findProperty("CARGO_PROFILE")?.toString() ?: "release"
    extraCargoBuildArguments = listOf("--bin", "sslocal")
    featureSpec.noDefaultBut(arrayOf(
            "stream-cipher",
            "logging",
            "local-flow-stat",
            "local-dns"))
    exec = { spec, toolchain ->
        if (toolchain.target == "i686-linux-android") {
			spec.environment("AR_i686-linux-android", "$ndkDir/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar")
		}
        if (toolchain.target == "x86_64-linux-android") {
			spec.environment("AR_x86_64-linux-android", "$ndkDir/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar")
		}
		if (toolchain.target == "armv7-linux-androideabi") {
			spec.environment("AR_armv7-linux-androideabi", "$ndkDir/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar")
		}
		if (toolchain.target == "aarch64-linux-android") {
			spec.environment("AR_aarch64-linux-android", "$ndkDir/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar")
		}
        spec.environment("RUST_ANDROID_GRADLE_LINKER_WRAPPER_PY", "$projectDir/$module/../linker-wrapper.py")
        spec.environment("RUST_ANDROID_GRADLE_TARGET", "target/${toolchain.target}/$profile/lib$libname.so")
    }
}

tasks.whenTaskAdded {
    when (name) {
        "mergeDebugJniLibFolders", "mergeReleaseJniLibFolders" -> dependsOn("cargoBuild")
    }
}

tasks.register<Exec>("cargoClean") {
    executable("cargo")     // cargo.cargoCommand
    args("clean")
    workingDir("$projectDir/${cargo.module}")
}

tasks.clean.dependsOn("cargoClean")