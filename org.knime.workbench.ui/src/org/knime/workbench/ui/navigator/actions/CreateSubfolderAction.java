/* This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2008
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
 */
package org.knime.workbench.ui.navigator.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.workbench.ui.KNIMEUIPlugin;
import org.knime.workbench.ui.metainfo.model.MetaInfoFile;
import org.knime.workbench.ui.navigator.KnimeResourceNavigator;

/**
 * 
 * @author Fabian Dill, KNIME.com GmbH
 */
public class CreateSubfolderAction extends Action {

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(CreateSubfolderAction.class);

    private IContainer m_parent;

    private static ImageDescriptor icon;
    
    private boolean m_isWorkflow = false;
    
    private static final Path WF_PATH = new Path(
            WorkflowPersistor.WORKFLOW_FILE);

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        if (icon == null) {
            icon = KNIMEUIPlugin.imageDescriptorFromPlugin(
                    KNIMEUIPlugin.PLUGIN_ID, "icons/wf_group_new.png");
        }
        return icon;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return "Create Workflow Set...";
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Creates a hierarchical folder structure " 
            + "to group KNIME workflows in";
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        m_isWorkflow = false;
        // getSelection from navigator
        IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
        ISelection sel = page.getSelection(KnimeResourceNavigator.ID);
        if (!(sel instanceof IStructuredSelection)) {
            return false;
        }
        IStructuredSelection strucSel = (IStructuredSelection)sel;
        if (strucSel.size() > 1) {
            // multiple selection
            return false;
        }
        Object o = strucSel.getFirstElement();
        if (o instanceof IContainer) {
            IContainer cont = (IContainer)o;
            // check if its a KNIME workflow
            if (cont.exists(WF_PATH)) {
                m_isWorkflow = true;
                return true;
            } else if (cont.getParent() != null 
                        && cont.getParent().exists(WF_PATH)) {
                // then it is a node
                    return false;
            }
            m_parent = (IContainer)o;
            return true;
        } else if (o instanceof IFile) {
            return false;
        }
        return false;
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // if no folder is selected its root
        if (m_parent == null || m_isWorkflow) {
            m_parent = ResourcesPlugin.getWorkspace().getRoot();
        }
        InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                "Enter Workflow Set Name",
                "Name of new workflow group: ", "",
                new IInputValidator() {

                    @Override
                    public String isValid(final String newText) {
                        if (newText.trim().length() == 0) {
                           return "Please enter the name of the workflow group";
                        }
                        if (m_parent.findMember(newText) != null) {
                            return "File " + newText + " already exists in "
                                    + m_parent.getName();
                        }
                        return null;
                    }

                });
        /*
         * Uncommented: if a workflow was selected, the workflow group will be 
         * created in ROOT. We may or may not inform the user about it...
         */
// if (m_isWorkflow) {
// MessageDialog.openInformation(Display.getDefault().getActiveShell(),
// "Invalid Parent", 
// "A workflow set cannot be created inside a KNIME workflow." 
// + " Will be created in ROOT.");
//        }
        if (dialog.open() == Window.OK) { 
            String name = dialog.getValue();
            if (name == null) {
                throw new IllegalArgumentException(
                        "Name of the the subfolder must not be null!");
            }
            // create the workflow set here
            try {
                File parent = m_parent.getLocation().toFile();
                File subFolder;
                if (m_parent instanceof IWorkspaceRoot) {
                    // we have to create a project
                    IWorkspaceRoot root = ((IWorkspaceRoot)m_parent);
                    final IProject newProject = root.getProject(name);
                    PlatformUI.getWorkbench().getProgressService()
                            .busyCursorWhile(new IRunnableWithProgress() {
                                @Override
                                public void run(final IProgressMonitor monitor)
                                        throws InvocationTargetException,
                                        InterruptedException {
                                    try {
                                        newProject.create(monitor);
                                        newProject.open(monitor);
                                    } catch (CoreException e) {
                                        throw new InvocationTargetException(e);
                                    }
                                }
                            });
                    subFolder = new File(newProject.getLocationURI());
                } else {
                    subFolder = new File(parent, name);
                    subFolder.mkdir();
                }
                MetaInfoFile.createMetaInfoFile(subFolder, m_isWorkflow);
                PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
                        new IRunnableWithProgress() {
                            @Override
                            public void run(final IProgressMonitor monitor)
                                    throws InvocationTargetException,
                                    InterruptedException {
                                try {
                                    m_parent.refreshLocal(
                                            IResource.DEPTH_ONE, monitor);
                                } catch (CoreException ce) {
                                    System.err.println(ce);
                                }

                            }
                        });
            } catch (Exception e) {
                LOGGER.error("Error occured while creating KNIME workflow set "
                        + name);
            }
        }
    }

}
