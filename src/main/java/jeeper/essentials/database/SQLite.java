package jeeper.essentials.database;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.sqlite.SQLiteDataSource;

public class SQLite {

    public static DSLContext databaseSetup(String dirtlandsDbPath) {
        String dbUrl = "jdbc:sqlite:"+dirtlandsDbPath+"/essentials.db";

        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl(dbUrl);

        Flyway flyway = Flyway.configure(SQLite.class.getClassLoader())
                .dataSource(ds)
                .load();

        flyway.repair();

        try{
            flyway.migrate();
        } catch (FlywayException e) {
            e.printStackTrace();
        }


        Settings settings = new Settings()
                .withExecuteLogging(false); // Defaults to true

        Configuration configuration = new DefaultConfiguration()
                .set(ds)
                .set(SQLDialect.SQLITE)
                .set(settings);

        System.getProperties().setProperty("org.jooq.no-logo", "true");

        return DSL.using(configuration);
    }

}
