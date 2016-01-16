package org.paces.data.Stata.Readers.FileElements;

import org.paces.data.Stata.Readers.StConvert;
import org.paces.data.Stata.Readers.StataByteOrder;
import org.paces.data.Stata.Version.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by billy on 12/25/15.
 */
public class DtaVarTypes {

	private List<Integer> stVariableTypes = new ArrayList<>();

	private FileVersion version = null;

	private List<Integer> reserveBytes = new ArrayList<>();

	DtaVarTypes(V113 stata8) {

	}

	DtaVarTypes(V114 stata10) {

	}

	DtaVarTypes(V115 stata12) {


	}

	DtaVarTypes(V117 stata13) {

	}

	DtaVarTypes(V118 stata14) {

	}

	DtaVarTypes(RandomAccessFile x, List<Long> offsets, Integer
			numberVariables, StataByteOrder sbo, FileVersion version) {

		try {

			x.seek(offsets.get(2));

			for (int i = 0; i < numberVariables; i++) {

				byte[] stvartypes = new byte[2];

				x.read(stvartypes);

				stVariableTypes.add(i, StConvert.toStata(stvartypes, sbo.swapto, (int) 0));

			}

			this.version = version;

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public List<Integer> getVariableTypes() {
		return this.stVariableTypes;
	}

	public void setReserveBytes(Integer vartype, Integer index) {
		if (version != null) {
			this.reserveBytes.add(index, (Integer) this.version.getDataTypes().get(vartype));
		}
	}

}
