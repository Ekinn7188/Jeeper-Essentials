import java.net.URI
import org.gradle.process.internal.ExecException

plugins {
    `java-library`
    java

    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("nu.studer.jooq") version "7.2"
    id("org.flywaydb.flyway") version "9.20.0"
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "jeeper.essentials"
version = 1.0

java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenLocal()
    mavenCentral()
    maven ("https://papermc.io/repo/repository/maven-public/")
    maven {
        name = "sonatype-oss-snapshots"
        url = URI("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    maven ( "https://repo.codemc.io/repository/maven-snapshots/" )
    maven ( "https://repo.dmulloy2.net/repository/public/" )
}

dependencies {

    //minecraft
    paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly ("net.luckperms:api:5.4")
    implementation ("org.reflections:reflections:0.10.2")
    implementation ("jeeper.utils:PaperPluginUtils:1.4")
    implementation ("net.kyori:adventure-text-serializer-plain:4.14.0")
    implementation ("net.wesjd:anvilgui:1.7.0-SNAPSHOT")

    //database
    implementation ("org.jooq:jooq:3.16.6")
    implementation ("org.flywaydb:flyway-core:8.5.12")
    implementation ("ch.qos.logback:logback-classic:1.2.11")
    compileOnly   ("org.xerial:sqlite-jdbc:3.42.0.0")
    jooqGenerator ("org.xerial:sqlite-jdbc:3.42.0.0")

    //discord
    implementation ("net.dv8tion:JDA:5.0.0-beta.12")
}

flyway {
    url = "jdbc:sqlite:${buildDir}/generate-source.db"
//    locations = ['classpath:db/migration']
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.sqlite.JDBC"
                    url = "jdbc:sqlite:${buildDir}/generate-source.db"
                }
                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.sqlite.SQLiteDatabase"
                        excludes = "flyway_schema_history|sqlite_master|sqlite_sequence"
                    }
                    target.apply {
                        packageName = "essentials.db"
                    }
                }
            }
        }
    }
}

val cleanGenerateSourceDB by tasks.registering {
    delete(file("${buildDir}/generate-source.db"))
}

val cleanServer by tasks.registering {
    delete(files("${projectDir}/essentials.db"))
    delete(files("${projectDir}/run/usercache.json"))
    delete(files("${projectDir}/run/plugins/Jeeper-Essentials"))
    delete(files("${projectDir}/run/world/playerdata"))
}

val emptyConfig by tasks.registering {
    delete(
        fileTree(file("${projectDir}/run/plugins/Jeeper-Essentials")) {
            include("**.yml")
        }
    )
}

tasks {

    runServer {
        //serverJar(file ("${projectDir}/run/server.jar"))
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        setExecutable("C:/Program Files/Java/jbr_dcevm-17_0_1-windows-x64-b164.8/")
        println(executable)
        jvmArgs = listOf("-Xmx5G", "-XX:+AllowEnhancedClassRedefinition", "-XX:+AllowRedefinitionToAddDeleteMethods")
        println(runDirectory.asFile)
        minecraftVersion("1.20.1")

    }

    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    flywayMigrate {
        dependsOn(cleanGenerateSourceDB)
    }

    named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") {
        dependsOn(flywayMigrate)
    }

    classes {
        dependsOn(named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq"))
    }


}

