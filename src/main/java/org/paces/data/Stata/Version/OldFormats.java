package org.paces.data.Stata.Version;

import org.paces.data.Stata.Readers.FileElements.*;
import org.paces.data.Stata.Readers.StConvert;
import org.paces.data.Stata.Readers.StataByteOrder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by billy on 12/27/15.
 */
public abstract class OldFormats extends FileConstants {

	protected Integer K;
	protected Integer N;
	protected DtaCharacteristics characteristics;
	protected DtaData thedata;
	protected DtaDisplayFormats displayFormats;
	protected DtaMap fileMap;
	protected DtaSortOrder dataSortOrder;
	protected DtaValueLabel valueLabels;
	protected DtaValueLabelNames vallabNames;
	protected DtaVariableLabels varLabels;
	protected DtaVariableNames varnames;
	protected DtaVarTypes dataTypes;
	protected DtaStrLs stBlobs;

	/***
	 * Number of bytes to reserve for reading the release ID from the file
	 */
	protected static final int RELEASE = 1;

	/***
	 * Number of bytes to reserve for reading the endianness indicator from
	 * the file header
	 */
	protected static final int BYTEORDER = 1;

	/***
	 * Number of bytes to reserve for reading the filetype field from the
	 * file header
	 */
	protected static final int FILETYPE = 1;

	/***
	 * Number of bytes to reserve for padding in the file header
	 */
	protected static final int BLANK = 1;

	/***
	 * Number of bytes to reserve for reading the number of variables in the
	 * data set
	 */
	protected static final int NVARS = 2;

	/***
	 * Number of bytes to reserve for reading the number of observations in
	 * the data set
	 */
	protected static final int NOBS = 4;

	/***
	 * Number of bytes to reserve for reading the dataset label from the file
	 */
	protected static final int DATA_LABEL = 81;

	/***
	 * Number of bytes to reserve for reading the file's timestamp field
	 */
	protected static final int TIME_STAMP = 18;


	/***
	 * Method to return a Map object for type map prior to the introduction
	 * of Binary Large OBject types in Stata
	 * @return A map with integer keys and string values
	 */
	public Map<Integer, Integer> getDataTypes() {
		Map<Integer, Integer> stataTypes = new HashMap<>();
		for (int i = 1; i < 244; i++) {
			stataTypes.put(i, i);
		}
		stataTypes.put(251, 1);
		stataTypes.put(252, 2);
		stataTypes.put(253, 4);
		stataTypes.put(254, 4);
		stataTypes.put(255, 8);
		return stataTypes;
	}

	/**
	 * Static method used to parse the file header from files created from
	 * versions 8, 10, or 12 of Stata
	 * @param stdata The representation of the .dta file on the JVM
	 * @param byteReader A List of byte arrays created by the Load class
	 *                      based on the release version of the file
	 * @return A list of objects that contain the header data and will need
	 * to be recast when being decoded
	 * @throws IOException This will be caught by the class constructor for
	 * the Load class
	 */
	public static List<Object> readHeader(RandomAccessFile stdata, List<byte[]>
			byteReader) throws IOException {

		// List object used to return the header values
		List<Object> values = new ArrayList<>();

		// Set the position of the file at the beginning
		stdata.seek(0);

		// Loop over the elements  of the list to read all of the header
		// elements
		for(int i = 0; i < byteReader.size(); i++) {

			// Reads the first four bytes sequentially
			stdata.read(byteReader.get(i));

		} // End Loop to read the values

		// Parse the first four elements as int values from the 1 byte
		// elements
		byte[] val = byteReader.get(0);

		// Add each of these elements to the list object
		values.add((int) val[0]);

		// Get the byte array containing the byte order
		byte[] bo = byteReader.get(1);

		// 0x01 is HILO or "MSF" in later versions of the file spec
		if ((int) bo[0] == 1) values.add("MSF");

		// 0x02 is LOHI or "LSF" in later versions of the file spec
		else values.add("LSF");

		// Create a byte order variable using the byte array in position 1 of
		// the bytereader (this is the header variable passed to the method)
		StataByteOrder sbo = new StataByteOrder(byteReader.get(1));

		// Parse the number of variables
		Short K = StConvert.toStata(byteReader.get(4), sbo.swapto, (short) 0);

		// Parse the number of observations
		Integer N = StConvert.toStata(byteReader.get(5), sbo.swapto, (int) 0);

		// Parse the datalabel
		String datalabel = StConvert.toStata(byteReader.get(6), sbo.swapto, "");

		// Parse the dataset timestamp
		String timestamp = StConvert.toStata(byteReader.get(7), sbo.swapto, "");

		// Used to store the starting position of the file map
		Long offset = stdata.getFilePointer();

		// Add the number of variables to the list object
		values.add(K);

		// Add the number of observations to the list object
		values.add(N);

		// Add the dataset label to the list object
		values.add(datalabel);

		// Add the time stamp to the list object
		values.add(timestamp);

		// Adds the offset to the file map in the last position
		values.add(offset);

		// Return the list object
		return values;

	} // End of Method declaration

}
