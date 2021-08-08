package EmailApp.sender;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import EmailApp.Patient;

public class ExcelWriter {
	
	private static final String DATA_STORE = "data/generated_files/";

	public static List<Patient> createExcelForAll(Set<Patient> patientDetails) throws IOException{

		List<Patient> patientList = new ArrayList<>();
		
		Workbook wb = null;
		FileOutputStream output = null;
		Patient p;
		String destination;
		Sheet sheet;
		Row row;
		Cell cell;
		
		Iterator<Patient> iter = patientDetails.iterator();
		while (iter.hasNext()) {
			
			// get the patient and set the excel file location on that object
			p = iter.next();
			String fileName = p.getName() + "'s Details.xls";
			destination = DATA_STORE + fileName;
			p.setExcelFile(destination);
			patientList.add(p);
			
			// create a workbook in the same location and add data to it
			wb = new HSSFWorkbook();
			sheet = wb.createSheet(p.getName());

			row = sheet.createRow(0);
			row.createCell(0).setCellValue("Name");
			row.createCell(1).setCellValue(p.getName());

			row = sheet.createRow(1);
			row.createCell(0).setCellValue("Address");
			row.createCell(1).setCellValue(p.getAddress());

			row = sheet.createRow(2);
			row.createCell(0).setCellValue("Vaccinated");
			row.createCell(1).setCellValue("YES");

			row = sheet.createRow(3);
			row.createCell(0).setCellValue("Vaccine Center");
			row.createCell(1).setCellValue(p.getVacCenter());

			row = sheet.createRow(4);
			row.createCell(0).setCellValue("Time Vaccinated");
			row.createCell(1).setCellValue(p.getTime());
			
			row = sheet.createRow(5);
			row.createCell(0).setCellValue("Vaccine Name");
			row.createCell(1).setCellValue(p.getVacName());

			row = sheet.createRow(6);
			row.createCell(0).setCellValue("Email");
			row.createCell(1).setCellValue(p.getEmail());

			// Right align the values
			CellStyle style = wb.createCellStyle();
			style.setWrapText(true);
			style.setAlignment(HorizontalAlignment.RIGHT);
			for(int i=0; i<sheet.getPhysicalNumberOfRows(); i++) {
				cell = sheet.getRow(i).getCell(1);
				cell.setCellStyle(style);
			}
			
			sheet.autoSizeColumn(0);sheet.autoSizeColumn(1);
			
			output = new FileOutputStream(destination);
			wb.write(output);
		}

		System.out.println("Excel file successfully created for all the patients !!!");
		
		output.close();
		wb.close();
		return patientList;
	}

	public static void print(Set<Patient> patientDetails) {
		System.out.println("\n-----------------Printing Data-----------------------\n");
		Iterator<Patient> iter = patientDetails.iterator();
		while(iter.hasNext()) {
			Patient p = iter.next();
			System.out.printf("%s\t\t%s\t\t\t%s\t\t\t%s\t\t%s\t\t\t%s\n",
					p.getEmail(),p.getName(),p.getAddress(),p.getVacCenter(),p.getTime(),p.getVacName(),p.getExcelFile());
		}
	}
	
}