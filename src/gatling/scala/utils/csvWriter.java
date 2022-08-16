package utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class csvWriter {

    public static void main(String[] args) throws IOException, CsvException {
        csvWriter csvWriter = new csvWriter();
        csvWriter.writePatientCSVFile(csvWriter.readCSVData());
        csvWriter.writePatientNameCSVFile(csvWriter.readCSVData());
    }

    protected List<String[]> readCSVData() throws IOException, CsvException {
        String filepath= System.getProperty("user.dir")+"/src/gatling/resources/registrations.csv";
        FileReader patientRegFile = new FileReader(filepath);
        CSVReader csvReader = new CSVReaderBuilder(patientRegFile)
                .build();
        return csvReader.readAll();
    }

    protected void writePatientCSVFile(List<String[]> registrationCSVdata) throws IOException {
        IntStream.range(0, registrationCSVdata.size())
                .forEach(i -> registrationCSVdata.set(i,new String[]{registrationCSVdata.get(i)[0]}));
         writeDataIntoCSV(registrationCSVdata,System.getProperty("user.dir")+"/src/gatling/resources/patient.csv");
    }

    protected void writePatientNameCSVFile(List<String[]> registrationCSVdata) throws IOException {
        IntStream.range(0, registrationCSVdata.size())
                .forEach(i -> {
                    if(i==0)
                        registrationCSVdata.set(i,new String[]{"PATIENT_NAME"});
                        else
                        registrationCSVdata.set(i,new String[]{registrationCSVdata.get(i)[2]+" "+registrationCSVdata.get(i)[4]});
                        });
        writeDataIntoCSV(registrationCSVdata,System.getProperty("user.dir")+"/src/gatling/resources/patientName.csv");
    }

    void writeDataIntoCSV(List<String[]> patientIDs, String fileName) throws IOException {
        try{
            FileWriter outputFile = new FileWriter(fileName);
            CSVWriter writer = new CSVWriter(outputFile, CSVWriter.DEFAULT_SEPARATOR , CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(patientIDs);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
