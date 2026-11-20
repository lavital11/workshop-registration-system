package patmal.workshops.student.impl.logic;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.model.Student;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

// CSV import (bonus, sections 4.4/5.1): registers students exactly as if
// createStudent had been called for each data line, in file order, stopping
// at the first rejected line. Delegates the actual creation (and its
// validation/logging) to StudentManager, so the rules stay identical to a
// normal createStudent call.
final class CsvImporter {

    private static final Logger LOGGER = LogManager.getLogger(CsvImporter.class);

    private final StudentManager studentManager;

    CsvImporter(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    List<Student> importFrom(File source) {
        List<CSVRecord> records = parse(source);

        if (records.isEmpty() || !isExpectedHeader(records.get(0))) {
            LOGGER.warn("Rejected CSV import: missing or wrong header line.");
            throw new IllegalArgumentException(
                    "CSV file must start with the header line: id,name,email");
        }

        List<Student> registered = new ArrayList<>();
        for (int i = 1; i < records.size(); i++) {
            CSVRecord record = records.get(i);
            if (record.size() != 3) {
                LOGGER.warn("Rejected CSV import: line {} has {} value(s), expected 3.",
                        record.getRecordNumber(), record.size());
                throw new IllegalArgumentException(
                        "CSV line " + record.getRecordNumber() + " must have exactly 3 values.");
            }
            registered.add(studentManager.create(record.get(0), record.get(1), record.get(2)));
        }
        return registered;
    }

    private List<CSVRecord> parse(File source) {
        CSVFormat format = CSVFormat.DEFAULT.builder().setIgnoreEmptyLines(true).build();
        try (CSVParser parser = CSVParser.parse(
                Files.newBufferedReader(source.toPath(), StandardCharsets.UTF_8), format)) {
            return parser.getRecords();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isExpectedHeader(CSVRecord record) {
        return record.size() == 3
                && "id".equals(record.get(0))
                && "name".equals(record.get(1))
                && "email".equals(record.get(2));
    }
}
