package com.artillexstudios.axcrates.libraries;


import com.artillexstudios.axapi.libs.libby.Library;
import com.artillexstudios.axapi.libs.libby.relocation.Relocation;
import org.jetbrains.annotations.Nullable;

public enum Libraries {

    HIKARICP("com{}zaxxer:HikariCP:5.1.0", new Relocation("com{}zaxxer{}hikari", "com.artillexstudios.axcrates.libs.hikari")),

    MYSQL_CONNECTOR("com{}mysql:mysql-connector-j:9.0.0"),

    SQLITE_JDBC("org{}xerial:sqlite-jdbc:3.46.0.1"),

    H2_JDBC("com{}h2database:h2:2.3.232", new Relocation("org{}h2", "com.artillexstudios.axcrates.libs.h2")),

    POSTGRESQL("org{}postgresql:postgresql:42.7.3");

    private final Library library;

    @Nullable
    public Library getLibrary() {
        return this.library;
    }

    Libraries(String lib, Relocation relocation) {
        String[] split = lib.split(":");

        library = Library.builder()
                .groupId(split[0])
                .artifactId(split[1])
                .version(split[2])
                .relocate(relocation)
                .build();
    }

    Libraries(String lib) {
        String[] split = lib.split(":");

        library = Library.builder()
                .groupId(split[0])
                .artifactId(split[1])
                .version(split[2])
                .build();
    }
}
