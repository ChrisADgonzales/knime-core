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
 *   Feb 23, 2021 (benjamin): created
 */
package org.knime.core.data.v2;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.IDataRepository;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.collection.SetCell;
import org.knime.core.data.collection.SparseListCell;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.filestore.FileStore;
import org.knime.core.data.filestore.FileStoreKey;
import org.knime.core.data.filestore.internal.FileStoreProxy.FlushCallback;
import org.knime.core.data.filestore.internal.IWriteFileStoreHandler;
import org.knime.core.data.v2.value.BooleanListValueFactory;
import org.knime.core.data.v2.value.BooleanSetValueFactory;
import org.knime.core.data.v2.value.BooleanSparseListValueFactory;
import org.knime.core.data.v2.value.DoubleListValueFactory;
import org.knime.core.data.v2.value.DoubleSetValueFactory;
import org.knime.core.data.v2.value.DoubleSparseListValueFactory;
import org.knime.core.data.v2.value.IntListValueFactory;
import org.knime.core.data.v2.value.IntSetValueFactory;
import org.knime.core.data.v2.value.IntSparseListValueFactory;
import org.knime.core.data.v2.value.LongListValueFactory;
import org.knime.core.data.v2.value.LongSetValueFactory;
import org.knime.core.data.v2.value.LongSparseListValueFactory;
import org.knime.core.data.v2.value.StringListValueFactory;
import org.knime.core.data.v2.value.StringSetValueFactory;
import org.knime.core.data.v2.value.StringSparseListValueFactory;
import org.knime.core.data.v2.value.VoidRowKeyFactory;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;

@SuppressWarnings("javadoc") // TODO javadoc
public class StringListValueFactoryTest {

    // Double

    @Test
    public void testSchemaSaveLoadDoubleList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(ListCell.class, DoubleCell.TYPE), DoubleListValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadDoubleSet() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SetCell.class, DoubleCell.TYPE), DoubleSetValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadDoubleSparseList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SparseListCell.class, DoubleCell.TYPE), DoubleSparseListValueFactory.class);
    }

    // Int

    @Test
    public void testSchemaSaveLoadIntList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(ListCell.class, IntCell.TYPE), IntListValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadIntSet() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SetCell.class, IntCell.TYPE), IntSetValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadIntSparseList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SparseListCell.class, IntCell.TYPE), IntSparseListValueFactory.class);
    }

    // Long

    @Test
    public void testSchemaSaveLoadLongList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(ListCell.class, LongCell.TYPE), LongListValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadLongSet() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SetCell.class, LongCell.TYPE), LongSetValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadLongSparseList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SparseListCell.class, LongCell.TYPE), LongSparseListValueFactory.class);
    }

    // String

    @Test
    public void testSchemaSaveLoadStringList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(ListCell.class, StringCell.TYPE), StringListValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadStringSet() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SetCell.class, StringCell.TYPE), StringSetValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadStringSparseList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SparseListCell.class, StringCell.TYPE), StringSparseListValueFactory.class);
    }

    // Boolean

    @Test
    public void testSchemaSaveLoadBooleanList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(ListCell.class, BooleanCell.TYPE), BooleanListValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadBooleanSet() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SetCell.class, BooleanCell.TYPE), BooleanSetValueFactory.class);
    }

    @Test
    public void testSchemaSaveLoadBooleanSparseList() throws InvalidSettingsException {
        testSchemaSaveLoad(DataType.getType(SparseListCell.class, BooleanCell.TYPE),
            BooleanSparseListValueFactory.class);
    }

    private static void testSchemaSaveLoad(final DataType type, final Class<?> factoryClass)
        throws InvalidSettingsException {
        final DataColumnSpec columnSpec = new DataColumnSpecCreator("0", type).createSpec();
        final DataTableSpec tableSpec = new DataTableSpec(columnSpec);
        final IDataRepository dataRepository = null;
        final IWriteFileStoreHandler fileStoreHandler = new DummyWriteFileStoreHandler();

        // Create the schema and check
        final ValueSchema schema = ValueSchema.create(tableSpec, RowKeyType.NOKEY, fileStoreHandler);
        assertEquals(2, schema.getValueFactories().length);
        assertEquals(VoidRowKeyFactory.class, schema.getValueFactories()[0].getClass());
        assertEquals(factoryClass, schema.getValueFactories()[1].getClass());

        // Save to some note settings
        final NodeSettings settings = new NodeSettings("valueSchema");
        ValueSchema.Serializer.save(schema, settings);

        // Load back and check
        final ValueSchema loadedSchema = ValueSchema.Serializer.load(tableSpec, dataRepository, settings);
        assertEquals(VoidRowKeyFactory.class, schema.getValueFactories()[0].getClass());
        assertEquals(2, loadedSchema.getValueFactories().length);
        assertEquals(factoryClass, loadedSchema.getValueFactories()[1].getClass());
    }

    private static final class DummyWriteFileStoreHandler implements IWriteFileStoreHandler {

        @Override
        public IDataRepository getDataRepository() {
            throw getException();
        }

        @Override
        public void clearAndDispose() {
            throw getException();
        }

        @Override
        public FileStore getFileStore(final FileStoreKey key) {
            throw getException();
        }

        @Override
        public FileStore createFileStore(final String name) throws IOException {
            throw getException();
        }

        @Override
        public FileStore createFileStore(final String name, final int[] nestedLoopPath, final int iterationIndex)
            throws IOException {
            throw getException();
        }

        @Override
        public void open(final ExecutionContext exec) {
            throw getException();
        }

        @Override
        public void addToRepository(final IDataRepository repository) {
            throw getException();
        }

        @Override
        public void close() {
            throw getException();
        }

        @Override
        public void ensureOpenAfterLoad() throws IOException {
            throw getException();
        }

        @Override
        public FileStoreKey translateToLocal(final FileStore fs, final FlushCallback flushCallback) {
            throw getException();
        }

        @Override
        public boolean mustBeFlushedPriorSave(final FileStore fs) {
            throw getException();
        }

        @Override
        public UUID getStoreUUID() {
            throw getException();
        }

        @Override
        public File getBaseDir() {
            throw getException();
        }

        @Override
        public boolean isReference() {
            throw getException();
        }

        private static IllegalStateException getException() {
            return new IllegalStateException("Dummy should not be called");
        }
    }
}
