# AARs for use in other projects

This folder contains:

| File | Size | Description |
|------|------|-------------|
| `avif-coder-release.aar` | ~9.2 MB | AVIF/HEIC decoder (native libs included) |
| `avif-coder-coil-3-release.aar` | ~29 KB | Coil 3 integration (requires avif-coder + Coil 3) |

**You need both AARs** when using the Coil 3 integration. The coil-3 AAR depends on avif-coder.

---

## Quick setup in another project

### 1. Copy AARs

Copy both `.aar` files into your app’s `libs/` folder (create it if needed), e.g.:

```
YourProject/
  app/
    libs/
      avif-coder-release.aar
      avif-coder-coil-3-release.aar
```

### 2. Add flatDir in `settings.gradle`

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs "${projectDir}/app/libs"   // or "libs" if libs is at project root
        }
    }
}
```

### 3. Add dependencies in `app/build.gradle` (or your module)

```groovy
dependencies {
    // AVIF/HEIC decoder (required)
    implementation(name: 'avif-coder-release', ext: 'aar')

    // Coil 3 integration (optional; use if you load images with Coil 3)
    implementation(name: 'avif-coder-coil-3-release', ext: 'aar')

    // Coil 3 (required for avif-coder-coil-3)
    implementation 'io.coil-kt.coil3:coil:3.3.0'
}
```

### 4. Use in code

**Decode only (no Coil):**

```kotlin
val coder = HeifCoder()
val bitmap = coder.decode(byteArray)
```

**With Coil 3:**

```kotlin
val imageLoader = ImageLoader.Builder(context)
    .components {
        add(com.github.awxkee.avifcoil.decoder.HeifDecoder.Factory())
    }
    .build()

imageView.load("https://example.com/image.avif", imageLoader)
```

---

## Decoder-only

This build is decoder-only (no AVIF/HEIC encoding). Supported:

- AVIF and HEIC/HEIF decoding  
- HDR (10-bit, 12-bit)  
- Wide color gamut and ICC profiles  
- Animated AVIF  

Minimum SDK: 24.
