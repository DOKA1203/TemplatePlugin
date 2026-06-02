package kr.doka.template;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

class TemplatePluginLoader implements PluginLoader {

    private static final String PLUGIN_NAME = "TemplatePlugin";

    @Override
    public void classloader(final PluginClasspathBuilder builder) {
        Properties libs = loadLibraries();
        String dbType = readDatabaseType();

        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder(
                "central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
        ).build());

        String kotlin = libs.getProperty("kotlin");
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib-jdk8:" + kotlin), null));

        String coroutines = libs.getProperty("coroutines");
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlinx:kotlinx-coroutines-core:" + coroutines), null));

        String exposed = libs.getProperty("exposed");
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.exposed:exposed-core:" + exposed), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.exposed:exposed-dao:" + exposed), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.exposed:exposed-jdbc:" + exposed), null));

        switch (dbType) {
            case "mysql" ->
                resolver.addDependency(new Dependency(new DefaultArtifact("com.mysql:mysql-connector-j:" + libs.getProperty("mysql")), null));
            case "postgresql" ->
                resolver.addDependency(new Dependency(new DefaultArtifact("org.postgresql:postgresql:" + libs.getProperty("postgresql")), null));
            default ->
                resolver.addDependency(new Dependency(new DefaultArtifact("org.xerial:sqlite-jdbc:" + libs.getProperty("sqlite-jdbc")), null));
        }

        builder.addLibrary(resolver);
    }

    private static String readDatabaseType() {
        Path configPath = Paths.get("plugins", PLUGIN_NAME, "config.yml");
        if (!Files.exists(configPath)) return "sqlite";

        try {
            boolean inDatabaseSection = false;
            for (String line : Files.readAllLines(configPath)) {
                if (line.trim().startsWith("#")) continue;

                if (line.startsWith("database:")) {
                    inDatabaseSection = true;
                    continue;
                }
                if (inDatabaseSection) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("type:")) {
                        return trimmed.substring(5).trim().toLowerCase();
                    }
                    // top-level key → left the database section
                    if (!line.startsWith(" ") && !line.startsWith("\t") && line.contains(":")) {
                        break;
                    }
                }
            }
        } catch (IOException ignored) {}

        return "sqlite";
    }

    private static Properties loadLibraries() {
        Properties props = new Properties();
        try (InputStream in = TemplatePluginLoader.class.getResourceAsStream("/libraries.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load libraries.properties", e);
        }
        return props;
    }
}
