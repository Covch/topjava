package ru.javawebinar.topjava;

import org.springframework.util.ClassUtils;

public class Profiles {
    public static final String
            JDBC = "jdbc",
            JPA = "jpa",
            DATAJPA = "datajpa";

    public static final String REPOSITORY_IMPLEMENTATION = DATAJPA;

    public static final String
            POSTGRES_DB = "postgres",
            HSQL_DB = "hsqldb";

    //  Get DB profile depending on DB driver in classpath
    public static String getActiveDbProfile() {
        if (ClassUtils.isPresent("org.postgresql.Driver", null)) {
            return POSTGRES_DB;
        } else if (ClassUtils.isPresent("org.hsqldb.jdbcDriver", null)) {
            return HSQL_DB;
        } else {
            throw new IllegalStateException("Could not find DB driver");
        }
    }

    public static String getActiveRepoProfile(Class<?> aClass) {
        if (aClass.getSimpleName().endsWith("JdbcTest")) {
            return JDBC;
        } else if (aClass.getSimpleName().endsWith("JpaTest")) {
            return JPA;
        } else if (aClass.getSimpleName().endsWith("DataJpaTest")) {
            return DATAJPA;
        } else {
            throw new IllegalStateException("Could not find repo implementation");
        }
    }
}
