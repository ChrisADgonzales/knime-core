/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * --------------------------------------------------------------------- *
 *
 * History
 *   Sep 25, 2007 (wiswedel): created
 */
package org.knime.core.node.workflow;

import java.io.File;
import java.io.IOException;

import org.knime.core.internal.ReferencedFile;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.workflow.NodeContainer.State;
import org.knime.core.node.workflow.NodeMessage.Type;
import org.knime.core.util.FileUtil;

/**
 *
 * @author wiswedel, University of Konstanz
 */
class NodeContainerMetaPersistorVersion200 extends
        NodeContainerMetaPersistorVersion1xx {

    private static final String CFG_STATE = "state";
    private static final String CFG_IS_DELETABLE = "isDeletable";
    private static final String CFG_JOB_MANAGER_CONFIG = "job.manager";
    private static final String CFG_JOB_MANAGER_DIR = "job.manager.dir";
    private static final String CFG_JOB_CONFIG = "execution.job";

    /** @param baseDir The node container directory (only important while load)
     */
    NodeContainerMetaPersistorVersion200(final ReferencedFile baseDir) {
        super(baseDir);
    }

    /** {@inheritDoc} */
    @Override
    protected NodeExecutionJobManager loadNodeExecutionJobManager(
            final NodeSettingsRO settings) throws InvalidSettingsException {
        if (!settings.containsKey(CFG_JOB_MANAGER_CONFIG)) {
            return null;
        }
        return NodeExecutionJobManagerPool.load(
                settings.getNodeSettings(CFG_JOB_MANAGER_CONFIG));
    }

    /** {@inheritDoc} */
    @Override
    protected NodeSettingsRO loadNodeExecutionJobSettings(
            final NodeSettingsRO settings) throws InvalidSettingsException {
        if (!settings.containsKey(CFG_JOB_CONFIG)) {
            return null;
        }
        return settings.getNodeSettings(CFG_JOB_CONFIG);
    }

    /** {@inheritDoc} */
    @Override
    protected ReferencedFile loadJobManagerInternalsDirectory(
            final ReferencedFile parentDir, final NodeSettingsRO settings)
    throws InvalidSettingsException {
        if (!settings.containsKey(CFG_JOB_MANAGER_DIR)) {
            return null;
        }
        String dir = settings.getString(CFG_JOB_MANAGER_DIR);
        if (dir == null) {
            throw new InvalidSettingsException(
                    "Job manager internals dir is null");
        }
        return new ReferencedFile(parentDir, dir);
    }

    /** {@inheritDoc} */
    @Override
    protected State loadState(final NodeSettingsRO settings,
            final NodeSettingsRO parentSettings)
    throws InvalidSettingsException {
        String stateString = settings.getString(CFG_STATE);
        if (stateString == null) {
            throw new InvalidSettingsException("State information is null");
        }
        try {
            return State.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new InvalidSettingsException("Unable to parse state \""
                    + stateString + "\"");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean loadIsDeletable(final NodeSettingsRO settings) {
        return settings.getBoolean(CFG_IS_DELETABLE, true);
    }

    /** {@inheritDoc} */
    @Override
    protected String loadCustomName(final NodeSettingsRO settings,
            final NodeSettingsRO parentSettings)
    throws InvalidSettingsException {
        if (!settings.containsKey(KEY_CUSTOM_NAME)) {
            return null;
        }
        return settings.getString(KEY_CUSTOM_NAME);
    }

    /** {@inheritDoc} */
    @Override
    protected String loadCustomDescription(final NodeSettingsRO settings,
            final NodeSettingsRO parentSettings)
    throws InvalidSettingsException {
        if (!settings.containsKey(KEY_CUSTOM_DESCRIPTION)) {
            return null;
        }
        return settings.getString(KEY_CUSTOM_DESCRIPTION);
    }

    /** {@inheritDoc} */
    @Override
    protected NodeMessage loadNodeMessage(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        if (settings.containsKey("node_message")) {
            NodeSettingsRO sub = settings.getNodeSettings("node_message");
            String typeS = sub.getString("type");
            if (typeS == null) {
                throw new InvalidSettingsException(
                        "Message type must not be null");
            }
            Type type;
            try {
                type = Type.valueOf(typeS);
            } catch (IllegalArgumentException iae) {
                throw new InvalidSettingsException("Invalid message type: "
                        + typeS, iae);
            }
            String message = sub.getString("message");
            return new NodeMessage(type, message);
        }
        return null;
    }

    public void save(final NodeContainer nc, final NodeSettingsWO settings)
        throws IOException {
        synchronized (nc.m_nodeMutex) {
            saveCustomName(settings, nc);
            saveCustomDescription(settings, nc);
            saveNodeExecutionJobManager(settings, nc);
            boolean mustAlsoSaveExecutorSettings = saveState(settings, nc);
            if (mustAlsoSaveExecutorSettings) {
                saveNodeExecutionJob(settings, nc);
            }
            saveJobManagerInternalsDirectory(settings, nc);
            saveNodeMessage(settings, nc);
            saveIsDeletable(settings, nc);
        }
    }

    protected void saveNodeExecutionJobManager(final NodeSettingsWO settings,
            final NodeContainer nc) {
        NodeExecutionJobManager jobManager = nc.getJobManager();
        if (jobManager != null) {
            NodeSettingsWO s = settings.addNodeSettings(CFG_JOB_MANAGER_CONFIG);
            NodeExecutionJobManagerPool.saveJobManager(jobManager, s);
        }
    }

    protected void saveNodeExecutionJob(
            final NodeSettingsWO settings, final NodeContainer nc) {
        assert nc.findJobManager().canDisconnect(nc.getExecutionJob())
        : "Execution job can be saved/disconnected";
        nc.saveNodeExecutionJobReconnectInfo(
                settings.addNodeSettings(CFG_JOB_CONFIG));
    }

    protected void saveJobManagerInternalsDirectory(
            final NodeSettingsWO settings, final NodeContainer nc) {
        NodeExecutionJobManager jobManager = nc.getJobManager();
        if (jobManager != null && jobManager.canSaveInternals()) {
            String dirName = "job_manager_internals";
            ReferencedFile parentRefFile = getNodeContainerDirectory();
            if (parentRefFile == null) {
                // added this later, make it bullet proof
                // this if-statement can be deleted if there are no reports
                // until, let's say, end of 2009
                getLogger().coding("Node directory must not be null "
                        + "at this time");
                return;
            }
            File dir = new File(getNodeContainerDirectory().getFile(), dirName);
            if (dir.exists()) {
                getLogger().warn("Directory \"" + dir.getAbsolutePath() + "\""
                        + " already exists; deleting it");
                FileUtil.deleteRecursively(dir);
            }
            if (!dir.mkdirs()) {
                getLogger().error("Unable to create directory \""
                        + dir.getAbsolutePath() + "\"");
                return;
            }
            try {
                jobManager.saveInternals(
                        new ReferencedFile(parentRefFile, dirName));
                settings.addString(CFG_JOB_MANAGER_DIR, dirName);
            } catch (Throwable e) {
                if (!(e instanceof IOException)) {
                    getLogger().coding("Saving internals of job manager should "
                            + "only throw IOException, caught "
                            + e.getClass().getSimpleName());
                }
                String error = "Saving job manager internals failed: "
                    + e.getMessage();
                getLogger().error(error, e);
            }
        }
    }

    protected boolean saveState(final NodeSettingsWO settings,
            final NodeContainer nc) {
        String state;
        boolean mustAlsoSaveExecutorSettings = false;
        switch (nc.getState()) {
        case IDLE:
        case UNCONFIGURED_MARKEDFOREXEC:
            state = State.IDLE.toString();
            break;
        case EXECUTED:
            state = State.EXECUTED.toString();
            break;
        case EXECUTINGREMOTELY:
            if (nc.findJobManager().canDisconnect(nc.getExecutionJob())) {
                // state will also be CONFIGURED only ... we set executing later
                mustAlsoSaveExecutorSettings = true;
            }
            state = State.EXECUTINGREMOTELY.toString();
            break;
        default:
            state = State.CONFIGURED.toString();
        }
        settings.addString(CFG_STATE, state);
        return mustAlsoSaveExecutorSettings;
    }

    protected void saveCustomName(final NodeSettingsWO settings,
            final NodeContainer nc) {
        settings.addString(KEY_CUSTOM_NAME, nc.getCustomName());
    }

    protected void saveCustomDescription(final NodeSettingsWO settings,
            final NodeContainer nc) {
        settings.addString(KEY_CUSTOM_DESCRIPTION, nc.getCustomDescription());
    }

    protected void saveIsDeletable(final NodeSettingsWO settings,
            final NodeContainer nc) {
        if (!nc.isDeletable()) {
            settings.addBoolean(CFG_IS_DELETABLE, false);
        }
    }

    protected void saveNodeMessage(final NodeSettingsWO settings,
            final NodeContainer nc) {
        NodeMessage message = nc.getNodeMessage();
        if (message != null && !message.getMessageType().equals(Type.RESET)) {
            NodeSettingsWO sub = settings.addNodeSettings("node_message");
            sub.addString("type", message.getMessageType().name());
            sub.addString("message", message.getMessage());
        }
    }

}
