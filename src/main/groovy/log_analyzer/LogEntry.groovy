package log_analyzer

import groovy.transform.ToString

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ToString
class LogEntry {
    LocalDateTime time
    String content

    LogEntry(LocalDateTime time, String content) {
        this.time = time
        this.content = content
    }

    static LogEntry fromRow(String row) {
        List<String> columns = row.split(',')
        new LogEntry(
                LocalDateTime.parse(columns[0] as CharSequence, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                row
        )
    }
}
