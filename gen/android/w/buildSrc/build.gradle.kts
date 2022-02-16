plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

gradlePlugin {
    plugins {
        create("pluginsForCoolKids") {
            id = "rustPlugin"
            implementationClass = "com.example.RustPlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    implementation("com.android.tools.build:gradle:7.1.0")
}
