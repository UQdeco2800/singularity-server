/**
 * 
 */
package uq.deco2800.singularity.integration.test.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.dropwizard.jackson.Jackson;

/**
 * Helper Yaml Class to read and write yamls to files.
 * 
 * @author dloetscher
 * 
 */
public class YamlHelper<T> {

	/**
	 * Object Mapper used to map a POJO to and from a yaml file.
	 */
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper(new YAMLFactory());

	/**
	 * Used to create timestamped temporary files.
	 */
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss");

	/**
	 * The generic type used to construct this class. Used for {@link Jackson} parsing and telling the parser about what
	 * POJO is being parsed to/from.
	 */
	private Class<T> type;

	/**
	 * Constructor for this class. Simply used to store the class type that is meant to be parsed by this instance.
	 * 
	 * @param type
	 *            The Type that is specified by the Generic Type.
	 */
	public YamlHelper(Class<T> type) {
		this.type = type;
	}

	/**
	 * Takes a filename which will then be read and parse into a object of type T as set by the constructor.
	 * 
	 * @param filename
	 *            The file which is to be read.
	 * @return Object of type T which has been parsed from the file.
	 * @throws JsonParseException
	 *             Thrown if the file could not be parsed.
	 * @throws JsonMappingException
	 *             Thrown if the file could not be mapped to the POJO.
	 * @throws IOException
	 *             Thrown if there was a problem accessing the file.
	 * @throws IllegalArgumentException
	 *             Thrown if the file does not exist or the file is actually a directory.
	 */
	public T readFileToObject(String filename) throws JsonParseException, JsonMappingException, IOException {
		File file = new File(filename);
		if (!file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("Filename given must exist and be a file.");
		}
		T yaml = MAPPER.readValue(file, type);
		return yaml;
	}

	/**
	 * Takes a filename which will then be read and parse into a {@link JsonNode}.
	 * 
	 * @param filename
	 *            The file which is to be read.
	 * @return A {@link JsonNode} which encapsulates the data in the file
	 * @throws JsonParseException
	 *             Thrown if the file could not be parsed.
	 * @throws JsonMappingException
	 *             Thrown if the file could not be mapped to the POJO.
	 * @throws IOException
	 *             Thrown if there was a problem accessing the file.
	 * @throws IllegalArgumentException
	 *             Thrown if the file does not exist or the file is actually a directory.
	 */
	public JsonNode readFileToJson(URI filename) throws JsonParseException, JsonMappingException, IOException {
		// FIXME: Temporary fix 
		File file = new File(filename);
		if (!file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("File passed must exist and be a file.");
		}
		JsonNode yaml = MAPPER.readTree(file);
		return yaml;
	}

	/**
	 * Creates a temporary yaml file from the data of type T given in the constructor.
	 * 
	 * @param filePrefix
	 *            The prefix of the file
	 * @param data
	 *            The data that is to be parsed and stored in the file.
	 * @return The filename of the temporary file. It will be of the form
	 *         "/temp/file/dir/{prefix}{timestamp}{tempFileRandomChars}.yaml"
	 * @throws JsonGenerationException
	 *             Thrown if there is a problem generating the yaml data for the file.
	 * @throws JsonMappingException
	 *             Thrown if there is a problem mapping the data to a yaml file.
	 * @throws IOException
	 *             Thrown if there is an issue with the file that is being saved to.
	 */
	public String createTemporaryYamlFromObject(String filePrefix, T data)
			throws JsonGenerationException, JsonMappingException, IOException {
		filePrefix += dateFormatter.format(new Date());
		File file = File.createTempFile(filePrefix, ".yaml");
		MAPPER.writeValue(file, data);
		removeFirstLine(file.getAbsolutePath());
		return file.getAbsolutePath();
	}

	/**
	 * Creates a temporary yaml file from a {@link JsonNode} object
	 * 
	 * @param filePrefix
	 *            The prefix of the file
	 * @param data
	 *            The data that is to be parsed and stored in the file.
	 * @return The filename of the temporary file. It will be of the form
	 *         "/temp/file/dir/{prefix}{timestamp}{tempFileRandomChars}.yaml"
	 * @throws JsonGenerationException
	 *             Thrown if there is a problem generating the yaml data for the file.
	 * @throws JsonMappingException
	 *             Thrown if there is a problem mapping the data to a yaml file.
	 * @throws IOException
	 *             Thrown if there is an issue with the file that is being saved to.
	 */
	public String createTemporaryYamlFromJson(String filePrefix, JsonNode data)
			throws JsonGenerationException, JsonMappingException, IOException {
		filePrefix += dateFormatter.format(new Date()) + "-";
		File file = File.createTempFile(filePrefix, ".yaml");
		MAPPER.writeValue(file, data);
		removeFirstLine(file.getAbsolutePath());
		return file.getAbsolutePath();
	}

	/**
	 * There is an issue with the yaml generator in that it will always prepend "---" at the top of the yaml files.
	 * While this is valid yaml syntax, there has been some issue in that SingularityServer throws an exception if this
	 * occurs. So, this is a helper method to remove the first line as explained on
	 * <a href="http://stackoverflow.com/questions/13178397/how-to-remove-first-line-of-a-text-file-in-java">Stack
	 * Overflow</a>
	 * 
	 * @param fileName
	 *            The file to remove the first line from.
	 * @throws IOException
	 *             Thrown if there is an issue with the file given.
	 */
	private void removeFirstLine(String fileName) throws IOException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
		// Initial write position
		long writePosition = randomAccessFile.getFilePointer();
		randomAccessFile.readLine();
		// Shift the next lines upwards.
		long readPosition = randomAccessFile.getFilePointer();

		byte[] buffer = new byte[1024];
		int n;
		while (-1 != (n = randomAccessFile.read(buffer))) {
			randomAccessFile.seek(writePosition);
			randomAccessFile.write(buffer, 0, n);
			readPosition += n;
			writePosition += n;
			randomAccessFile.seek(readPosition);
		}
		randomAccessFile.setLength(writePosition);
		randomAccessFile.close();
	}

}
