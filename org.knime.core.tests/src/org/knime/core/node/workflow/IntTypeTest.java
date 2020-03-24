/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 * ---------------------------------------------------------------------
 *
 * History
 *   Mar 3, 2020 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.core.node.workflow;

import static org.junit.Assert.assertEquals;
import static org.knime.core.node.workflow.VariableTypeTestUtils.CFG_VALUE;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.config.base.ConfigEntries;
import org.knime.core.node.util.SharedIcons;
import org.knime.core.node.workflow.VariableType.DoubleArrayType;
import org.knime.core.node.workflow.VariableType.DoubleType;
import org.knime.core.node.workflow.VariableType.IntArrayType;
import org.knime.core.node.workflow.VariableType.IntType;
import org.knime.core.node.workflow.VariableType.LongArrayType;
import org.knime.core.node.workflow.VariableType.LongType;
import org.knime.core.node.workflow.VariableType.StringArrayType;
import org.knime.core.node.workflow.VariableType.StringType;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

/**
 * Unit tests for {@link IntType}.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
public class IntTypeTest extends AbstractSimpleVariableTypeTest<Integer> {

    private static final Set<ConfigEntries> OVERWRITABLE = EnumSet.of(ConfigEntries.xbyte, ConfigEntries.xshort,
        ConfigEntries.xint, ConfigEntries.xlong, ConfigEntries.xfloat, ConfigEntries.xdouble, ConfigEntries.xchar,
        ConfigEntries.xstring, ConfigEntries.xtransientstring);

    private static final Set<ConfigEntries> CREATION_COMPATIBLE =
        EnumSet.of(ConfigEntries.xbyte, ConfigEntries.xshort, ConfigEntries.xint);

    /**
     * Constructor.
     */
    public IntTypeTest() {
        super(SharedIcons.FLOWVAR_INTEGER.get(), Integer.class, IntType.INSTANCE, OVERWRITABLE, CREATION_COMPATIBLE,
            Sets.newHashSet(IntType.INSTANCE, IntArrayType.INSTANCE, LongType.INSTANCE, LongArrayType.INSTANCE,
                DoubleType.INSTANCE, DoubleArrayType.INSTANCE, StringType.INSTANCE, StringArrayType.INSTANCE));
    }

    @Override
    public void testLoadValue() throws InvalidSettingsException {
        when(readSettings.getInt(CFG_VALUE)).thenReturn(1);
        assertEquals(Integer.valueOf(1), m_testInstance.loadValue(readSettings).get());
    }

    @Override
    public void testSaveValue() {
        m_testInstance.saveValue(writeSettings, m_testInstance.newValue(1));
        Mockito.verify(writeSettings).addInt(CFG_VALUE, 1);
    }

    @Override
    protected Integer getValueForTesting(final VariableType<?> type) {
        return 1;
    }

    @Override
    protected Integer getExpectedDefaultValue() {
        return 0;
    }

}
