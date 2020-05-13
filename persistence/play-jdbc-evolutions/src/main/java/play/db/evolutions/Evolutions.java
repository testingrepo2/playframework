/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

package play.db.evolutions;

import play.api.db.evolutions.DatabaseEvolutions;
import play.db.Database;
import play.libs.Scala;

import java.util.*;

/** Utilities for working with evolutions. */
public class Evolutions {

  /**
   * Create an evolutions reader that reads evolution files from this class's own classloader.
   *
   * <p>Only useful in simple classloading environments, such as when the classloader structure is
   * flat.
   *
   * @return the evolutions reader.
   */
  public static play.api.db.evolutions.EvolutionsReader fromClassLoader() {
    return fromClassLoader(Evolutions.class.getClassLoader());
  }

  /**
   * Create an evolutions reader that reads evolution files from a classloader.
   *
   * @param classLoader The classloader to read from.
   * @return the evolutions reader.
   */
  public static play.api.db.evolutions.EvolutionsReader fromClassLoader(ClassLoader classLoader) {
    return fromClassLoader(classLoader, "");
  }

  /**
   * Create an evolutions reader that reads evolution files from a classloader.
   *
   * @param classLoader The classloader to read from.
   * @param prefix A prefix that gets added to the resource file names, for example, this could be
   *     used to namespace evolutions in different environments to work with different databases.
   * @return the evolutions reader.
   */
  public static play.api.db.evolutions.EvolutionsReader fromClassLoader(
      ClassLoader classLoader, String prefix) {
    return new play.api.db.evolutions.ClassLoaderEvolutionsReader(classLoader, prefix);
  }

  /**
   * Create an evolutions reader based on a simple map of database names to evolutions.
   *
   * @param evolutions The map of database names to evolutions.
   * @return the evolutions reader.
   */
  public static play.api.db.evolutions.EvolutionsReader fromMap(
      Map<String, List<Evolution>> evolutions) {
    return new SimpleEvolutionsReader(evolutions);
  }

  /**
   * Create an evolutions reader for the default database from a list of evolutions.
   *
   * @param evolutions The list of evolutions.
   * @return the evolutions reader.
   */
  public static play.api.db.evolutions.EvolutionsReader forDefault(Evolution... evolutions) {
    Map<String, List<Evolution>> map = new HashMap<>();
    map.put("default", Arrays.asList(evolutions));
    return fromMap(map);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param reader The reader to read the evolutions.
   * @param autocommit Whether autocommit should be used.
   * @param schema The schema where all the play evolution tables are saved in
   */
  public static void applyEvolutions(
      Database database,
      play.api.db.evolutions.EvolutionsReader reader,
      boolean autocommit,
      String schema) {
    DatabaseEvolutions evolutions = new DatabaseEvolutions(database.asScala(), schema);
    evolutions.evolve(evolutions.scripts(reader), autocommit);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param reader The reader to read the evolutions.
   * @param autocommit Whether autocommit should be used.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   */
  public static void applyEvolutions(
      Database database,
      play.api.db.evolutions.EvolutionsReader reader,
      boolean autocommit,
      String schema,
      String metaTable) {
    DatabaseEvolutions evolutions = new DatabaseEvolutions(database.asScala(), schema, metaTable);
    evolutions.evolve(evolutions.scripts(reader), autocommit);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param reader The reader to read the evolutions.
   * @param autocommit Whether autocommit should be used.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   * @param substitutionsMappings Mappings of variables (without the prefix and curly braces) and
   *     their replacements.
   * @param substitutionsPrefix Prefix of the variable to substitute. Will be combined with curly
   *     braces, e.g. "${my_variable}".
   * @param substitutionsEscape Whetever escaping of variables is enabled via the syntax "${!...}".
   *     E.g. "${!my_variable}" ends up as "${my_variable}" in the final sql instead of replacing it
   *     with it's substitution.
   */
  public static void applyEvolutions(
      Database database,
      play.api.db.evolutions.EvolutionsReader reader,
      boolean autocommit,
      String schema,
      String metaTable,
      Map<String, String> substitutionsMappings,
      String substitutionsPrefix,
      boolean substitutionsEscape) {
    DatabaseEvolutions evolutions =
        new DatabaseEvolutions(
            database.asScala(),
            schema,
            metaTable,
            Scala.asScala(substitutionsMappings),
            substitutionsPrefix,
            substitutionsEscape);
    evolutions.evolve(evolutions.scripts(reader), autocommit);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param reader The reader to read the evolutions.
   * @param schema The schema where all the play evolution tables are saved in
   */
  public static void applyEvolutions(
      Database database, play.api.db.evolutions.EvolutionsReader reader, String schema) {
    applyEvolutions(database, reader, true, schema);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param reader The reader to read the evolutions.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   */
  public static void applyEvolutions(
      Database database,
      play.api.db.evolutions.EvolutionsReader reader,
      String schema,
      String metaTable) {
    applyEvolutions(database, reader, true, schema, metaTable);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param reader The reader to read the evolutions.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   * @param substitutionsMappings Mappings of variables (without the prefix and curly braces) and
   *     their replacements.
   * @param substitutionsPrefix Prefix of the variable to substitute. Will be combined with curly
   *     braces, e.g. "${my_variable}".
   * @param substitutionsEscape Whetever escaping of variables is enabled via the syntax "${!...}".
   *     E.g. "${!my_variable}" ends up as "${my_variable}" in the final sql instead of replacing it
   *     with it's substitution.
   */
  public static void applyEvolutions(
      Database database,
      play.api.db.evolutions.EvolutionsReader reader,
      String schema,
      String metaTable,
      Map<String, String> substitutionsMappings,
      String substitutionsPrefix,
      boolean substitutionsEscape) {
    applyEvolutions(
        database,
        reader,
        true,
        schema,
        metaTable,
        substitutionsMappings,
        substitutionsPrefix,
        substitutionsEscape);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param reader The reader to read the evolutions.
   * @param autocommit Whether autocommit should be used.
   */
  public static void applyEvolutions(
      Database database, play.api.db.evolutions.EvolutionsReader reader, boolean autocommit) {
    applyEvolutions(database, reader, autocommit, "");
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param reader The reader to read the evolutions.
   */
  public static void applyEvolutions(
      Database database, play.api.db.evolutions.EvolutionsReader reader) {
    applyEvolutions(database, reader, true);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param schema The schema where all the play evolution tables are saved in
   */
  public static void applyEvolutions(Database database, String schema) {
    applyEvolutions(database, fromClassLoader(), schema);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   */
  public static void applyEvolutions(Database database, String schema, String metaTable) {
    applyEvolutions(database, fromClassLoader(), schema, metaTable);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   * @param substitutionsMappings Mappings of variables (without the prefix and curly braces) and
   *     their replacements.
   * @param substitutionsPrefix Prefix of the variable to substitute. Will be combined with curly
   *     braces, e.g. "${my_variable}".
   * @param substitutionsEscape Whetever escaping of variables is enabled via the syntax "${!...}".
   *     E.g. "${!my_variable}" ends up as "${my_variable}" in the final sql instead of replacing it
   *     with it's substitution.
   */
  public static void applyEvolutions(
      Database database,
      String schema,
      String metaTable,
      Map<String, String> substitutionsMappings,
      String substitutionsPrefix,
      boolean substitutionsEscape) {
    applyEvolutions(
        database,
        fromClassLoader(),
        schema,
        metaTable,
        substitutionsMappings,
        substitutionsPrefix,
        substitutionsEscape);
  }

  /**
   * Apply evolutions for the given database.
   *
   * @param database The database to apply the evolutions to.
   */
  public static void applyEvolutions(Database database) {
    applyEvolutions(database, "");
  }

  /**
   * Cleanup evolutions for the given database.
   *
   * <p>This will run the down scripts for all the applied evolutions.
   *
   * @param database The database to apply the evolutions to.
   * @param autocommit Whether autocommit should be used.
   * @param schema The schema where all the play evolution tables are saved in
   */
  public static void cleanupEvolutions(Database database, boolean autocommit, String schema) {
    DatabaseEvolutions evolutions = new DatabaseEvolutions(database.asScala(), schema);
    evolutions.evolve(evolutions.resetScripts(), autocommit);
  }

  /**
   * Cleanup evolutions for the given database.
   *
   * <p>This will run the down scripts for all the applied evolutions.
   *
   * @param database The database to apply the evolutions to.
   * @param autocommit Whether autocommit should be used.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   */
  public static void cleanupEvolutions(
      Database database, boolean autocommit, String schema, String metaTable) {
    DatabaseEvolutions evolutions = new DatabaseEvolutions(database.asScala(), schema, metaTable);
    evolutions.evolve(evolutions.resetScripts(), autocommit);
  }

  /**
   * Cleanup evolutions for the given database.
   *
   * <p>This will run the down scripts for all the applied evolutions.
   *
   * @param database The database to apply the evolutions to.
   * @param autocommit Whether autocommit should be used.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   * @param substitutionsMappings Mappings of variables (without the prefix and curly braces) and
   *     their replacements.
   * @param substitutionsPrefix Prefix of the variable to substitute. Will be combined with curly
   *     braces, e.g. "${my_variable}".
   * @param substitutionsEscape Whetever escaping of variables is enabled via the syntax "${!...}".
   *     E.g. "${!my_variable}" ends up as "${my_variable}" in the final sql instead of replacing it
   *     with it's substitution.
   */
  public static void cleanupEvolutions(
      Database database,
      boolean autocommit,
      String schema,
      String metaTable,
      Map<String, String> substitutionsMappings,
      String substitutionsPrefix,
      boolean substitutionsEscape) {
    DatabaseEvolutions evolutions =
        new DatabaseEvolutions(
            database.asScala(),
            schema,
            metaTable,
            Scala.asScala(substitutionsMappings),
            substitutionsPrefix,
            substitutionsEscape);
    evolutions.evolve(evolutions.resetScripts(), autocommit);
  }

  /**
   * Cleanup evolutions for the given database.
   *
   * <p>This will run the down scripts for all the applied evolutions.
   *
   * @param database The database to apply the evolutions to.
   * @param autocommit Whether autocommit should be used.
   */
  public static void cleanupEvolutions(Database database, boolean autocommit) {
    cleanupEvolutions(database, autocommit, "");
  }

  /**
   * Cleanup evolutions for the given database.
   *
   * <p>This will run the down scripts for all the applied evolutions.
   *
   * @param database The database to apply the evolutions to.
   * @param schema The schema where all the play evolution tables are saved in
   */
  public static void cleanupEvolutions(Database database, String schema) {
    cleanupEvolutions(database, true, schema);
  }

  /**
   * Cleanup evolutions for the given database.
   *
   * <p>This will run the down scripts for all the applied evolutions.
   *
   * @param database The database to apply the evolutions to.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   */
  public static void cleanupEvolutions(Database database, String schema, String metaTable) {
    cleanupEvolutions(database, true, schema, metaTable);
  }

  /**
   * Cleanup evolutions for the given database.
   *
   * <p>This will run the down scripts for all the applied evolutions.
   *
   * @param database The database to apply the evolutions to.
   * @param schema The schema where all the play evolution tables are saved in
   * @param metaTable Table to keep evolutions' meta data
   * @param substitutionsMappings Mappings of variables (without the prefix and curly braces) and
   *     their replacements.
   * @param substitutionsPrefix Prefix of the variable to substitute. Will be combined with curly
   *     braces, e.g. "${my_variable}".
   * @param substitutionsEscape Whetever escaping of variables is enabled via the syntax "${!...}".
   *     E.g. "${!my_variable}" ends up as "${my_variable}" in the final sql instead of replacing it
   *     with it's substitution.
   */
  public static void cleanupEvolutions(
      Database database,
      String schema,
      String metaTable,
      Map<String, String> substitutionsMappings,
      String substitutionsPrefix,
      boolean substitutionsEscape) {
    cleanupEvolutions(
        database,
        true,
        schema,
        metaTable,
        substitutionsMappings,
        substitutionsPrefix,
        substitutionsEscape);
  }

  /**
   * Cleanup evolutions for the given database.
   *
   * <p>This will run the down scripts for all the applied evolutions.
   *
   * @param database The database to apply the evolutions to.
   */
  public static void cleanupEvolutions(Database database) {
    cleanupEvolutions(database, "");
  }
}
