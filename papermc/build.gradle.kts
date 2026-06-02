plugins {
    kotlin("jvm")
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.shadow)
    alias(libs.plugins.resource.factory.paper)
}

dependencies {
    paperweight.paperDevBundle(
        libs.versions.paper.api
            .get(),
    )
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.core)
    implementation(project(":api"))
    implementation(project(":core"))
}

paperPluginYaml {
    main = "kr.doka.template.TemplatePlugin"
    bootstrapper = "kr.doka.template.TemplatePluginBootstrap"
    loader = "kr.doka.template.TemplatePluginLoader"
    apiVersion = "1.21"

    authors.addAll("DOKA1203")
    website = "https://doka.kr/"
    prefix = "TemplatePlugin"
}

kotlin {
    jvmToolchain(25)
}

tasks {
    processResources {
        val props =
            mapOf(
                "exposed" to libs.versions.exposed.get(),
                "sqliteJdbc" to libs.versions.sqliteJdbc.get(),
                "mysqlConnector" to libs.versions.mysqlConnector.get(),
                "postgresql" to libs.versions.postgresql.get(),
                "coroutines" to libs.versions.coroutines.get(),
                "kotlin" to libs.versions.kotlin.get(),
            )
        inputs.properties(props)
        filesMatching("libraries.properties") {
            expand(props)
        }
    }
    build {
        dependsOn(shadowJar)
    }
}
