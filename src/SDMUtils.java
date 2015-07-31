import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Utility class for analysis of the results from a spatial decision making study
 * @author ckinkeldey
 *
 */
public class SDMUtils {
	
	private static Logger LOG = Logger.getLogger(SDMUtils.class);

	private static void extractGeoJSONFiles(FileInputStream in, String outPath) throws IOException {
		outPath += "geojson/";
		XSSFWorkbook workbook = new XSSFWorkbook(in);
		XSSFSheet sheet = workbook.getSheet("scenarios");
		int numRows = sheet.getLastRowNum()+1;
		for (int i = 0; i < numRows; i++) {
			XSSFRow row = sheet.getRow(i);
			String userId = ((int)row.getCell(2).getNumericCellValue())+"";
			String scenarioId = row.getCell(5).getStringCellValue();
			String geoJSON = row.getCell(6).getStringCellValue();
			String crsString = "\"crs\": { \"type\": \"name\", \"properties\": { \"name\": \"urn:ogc:def:crs:EPSG::3395\" } },";
			geoJSON = geoJSON.replaceFirst(",", ",\n"+crsString+"\n");
			LOG.info(geoJSON);
			String filename = userId + "_" + scenarioId + ".geojson";
			String subdir = scenarioId.contains("-1") ? "/1/" : "/2/";
			writeToFile(geoJSON, filename, outPath + subdir);
			LOG.info("written geoJSON to " + outPath + filename);
		}
		workbook.close();
	}

	private static void writeToFile(String geoJSON, String filename, String outPath) throws IOException {
		File outFile = new File(outPath + filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
		writer.write(geoJSON);
		writer.close();
	}

	public static void main(String[] args) {
		String homeDir = System.getProperty("user.home");
		String directory = homeDir + "/Dropbox/hcu/study-spatial-decisionmaking/results/experiments/";
		String filename = "user_data-150730.xlsx";
		File excelFile = new File(directory + filename);
		try {
			FileInputStream in = new FileInputStream(excelFile);
			extractGeoJSONFiles(in, directory);
		} catch (FileNotFoundException e) {
			LOG.error(e, e);
		} catch (IOException e) {
			LOG.error(e, e);
		}
	}
}
