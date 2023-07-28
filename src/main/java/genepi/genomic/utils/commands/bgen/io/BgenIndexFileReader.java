package genepi.genomic.utils.commands.bgen.io;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BgenIndexFileReader {

	private Connection connection;

	private PreparedStatement query;

	private ResultSet result;

	private Variant variant;

	public BgenIndexFileReader(String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			throw new IOException("Bgenix file not found: " + file.getPath());
		}

		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());

			//  ORDER BY chromosome,position ASC is not needed, since in primary key. see docs.
			query = connection
					.prepareStatement("SELECT chromosome,position FROM Variant");
			result = query.executeQuery();

		} catch (SQLException ex) {
			throw new IOException("Unable to load bgenix file. Error: " + ex.getMessage(), ex);
		}

	}

	public boolean next() {
		try {
			boolean hasNext = result.next();
			if (!hasNext) {
				return false;
			}

			variant = new Variant();
			variant.setContig(result.getString("chromosome"));
			variant.setStart(result.getInt("position"));

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Variant get() {
		return variant;
	}

	public void close() throws IOException {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

}
