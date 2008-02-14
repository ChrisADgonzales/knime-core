/*
 * -------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2007
 * University of Konstanz, Germany
 * Chair for Bioinformatics and Information Mining (Prof. M. Berthold)
 * and KNIME GmbH, Konstanz, Germany
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.org
 * email: contact@knime.org
 * -------------------------------------------------------------------
 * 
 * History
 *   21.08.2005 (gabriel): created
 */
package org.knime.base.node.io.database;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.DatabasePortObject;
import org.knime.core.node.DatabasePortObjectSpec;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.GenericNodeModel;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.PortObject;
import org.knime.core.node.PortObjectSpec;
import org.knime.core.node.PortType;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * 
 * @author Thomas Gabriel, University of Konstanz
 */
class DBQueryNodeModel extends GenericNodeModel {
    
    private final SettingsModelString m_query = 
        DBQueryNodeDialogPane.createQueryModel();
    
    /** Place holder for the database input view. */
    static final String VIEW_PLACE_HOLDER = "#view#";

    /**
     * Creates a new database reader.
     */
    DBQueryNodeModel() {
        super(new PortType[]{DatabasePortObject.TYPE}, 
                new PortType[]{DatabasePortObject.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_query.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        SettingsModelString query = 
            m_query.createCloneWithValidatedValue(settings);
        String queryString = query.getStringValue();
        if (queryString != null && !queryString.contains(VIEW_PLACE_HOLDER)) {
            throw new InvalidSettingsException(
                    "Database view place holder should not be replaced.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_query.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) 
            throws CanceledExecutionException, Exception {
        DatabasePortObject dbObj = (DatabasePortObject) inData[0];
        DBQueryConnection conn = new DBQueryConnection();
        conn.loadValidatedConnection(dbObj.getConnectionModel());
        // replace view place holder
        String newQuery = m_query.getStringValue().replaceAll(
                VIEW_PLACE_HOLDER, "(" + conn.getQuery() + ")");
        conn.setQuery(newQuery);
        DBReaderConnection load = 
            new DBReaderConnection(conn, conn.getQuery(), 10);
        BufferedDataTable data = exec.createBufferedDataTable(load, exec);
        return new PortObject[]{new DatabasePortObject(data, 
                conn.createConnectionModel())};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir,
            final ExecutionMonitor exec) throws IOException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir,
            final ExecutionMonitor exec) throws IOException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        DatabasePortObjectSpec spec = (DatabasePortObjectSpec) inSpecs[0];
        DBQueryConnection conn = new DBQueryConnection();
        conn.loadValidatedConnection(spec.getConnectionModel());
        // replace view place holder
        String newQuery = m_query.getStringValue().replaceAll(
                VIEW_PLACE_HOLDER, "(" + conn.getQuery() + ")");
        conn.setQuery(newQuery);
        try {
            conn.createConnection();
        } catch (Exception e) {
            throw new InvalidSettingsException(e.getMessage());
        }
        return new PortObjectSpec[]{
                new DatabasePortObjectSpec(null, conn.createConnectionModel())};
    }

}
