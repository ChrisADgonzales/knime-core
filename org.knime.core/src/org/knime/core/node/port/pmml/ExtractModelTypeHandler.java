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
 * ------------------------------------------------------------------------
 */
package org.knime.core.node.port.pmml;

import java.util.Set;
import java.util.TreeSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author Fabian Dill, University of Konstanz
 */
public class ExtractModelTypeHandler extends PMMLContentHandler {
    /** Public ID .*/
    public static final String ID = "ExtractModel";

    private PMMLModelType m_type = null;

    private boolean m_hasNamespace = false;

    /**
     *
     * @return the type of valid PMML models
     */
    public PMMLModelType getModelType() {
        return m_type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException {
        // ignore -> we are only searching for the model type
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endDocument() throws SAXException {
        if (m_type == null) {
            m_type = PMMLModelType.None;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(final String uri, final String localName,
            final String name) throws SAXException {
        for (PMMLModelType t : PMMLModelType.values()) {
            if (t.name().equals(name)) {
                if (m_type != null) {
                    throw new SAXException("Multiple PMML models in one PMML "
                            + "file are not yet supported. Found "
                            + m_type.name() + " and " + name + ".");
                }
                m_type = t;
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri, final String localName,
            final String name, final Attributes atts) throws SAXException {
        // leave empty -> we are only searching for the model type
        if (name.equals("PMML")) {
            if (atts.getValue("xmlns") != null
                    && atts.getValue("xmlns").startsWith(
                            "http://www.dmg.org/PMML-3")) {
                m_hasNamespace = true;
            }
        }
    }

    /**
     *
     * @return true if there is a PMML namespace declaration
     */
    public boolean hasNamespace() {
        return m_hasNamespace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<String> getSupportedVersions() {
        Set<String> versions = new TreeSet<String>();
        versions.add(PMMLPortObject.PMML_V3_0);
        versions.add(PMMLPortObject.PMML_V3_1);
        versions.add(PMMLPortObject.PMML_V3_2);
        return versions;
    }

}
