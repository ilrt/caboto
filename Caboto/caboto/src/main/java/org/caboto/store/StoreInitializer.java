package org.caboto.store;

import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.store.StoreFormatter;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * This class ensures that the Caboto store is correctly initiated.
 * <p/>
 * The Caboto project uses an RDF store that is backed by a relational database. The
 * unerlying implementation uses Jena SDB which means we need to ensure that the database
 * is correctly formated before it is used.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class StoreInitializer {

    /**
     * Constructor.
     *
     * @param formatConfigFile  a configuration file that specifes whether or not the database
     *                          has already been formatted.
     * @param formatPropertyKey the property name used to hold a "true" or "false" value for
     *                          indicating if the database has been formatted.
     * @param sdbConfigFile     a configuration file that holds the database details.
     */
    public StoreInitializer(String formatConfigFile, String formatPropertyKey,
                            String sdbConfigFile) {
        this.formatConfigFile = formatConfigFile;
        this.formatPropertyKey = formatPropertyKey;
        this.sdbConfigFile = sdbConfigFile;
    }

    /**
     * Initialize the store
     */
    public final void initializeStore() {

        try {

            // load the properties file
            String path = getClass().getResource(formatConfigFile).getPath();
            PropertiesConfiguration config = new PropertiesConfiguration(path);

            // format database tables if necessary
            if (!config.getBoolean(formatPropertyKey)) {

                String storePath = this.getClass().getResource(sdbConfigFile).getPath();
                Store store = SDBFactory.connectStore(storePath);
                StoreFormatter storeFormatter = store.getTableFormatter();
                storeFormatter.format();

                // update the config file
                config.setProperty(formatPropertyKey, true);
                config.save();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private final String formatConfigFile;
    private final String formatPropertyKey;
    private final String sdbConfigFile;
}
