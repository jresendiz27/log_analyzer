package log_analyzer

import groovy.util.logging.Log

import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

@Log
class LogAnalyzer {

    static String TARGET_LOGS_PATH = System.getProperty('TARGET_LOGS_PATH', '/home/jresendiz/target_logs_path')
    static String ORIGIN_PATH = System.getProperty('ORIGIN_PATH', '/home/jresendiz/logs')
    static String TARGET_LOG_NAME = System.getProperty('TARGET_LOG_NAME', 'condensed.log')
    static String SPLIT_STRATEGY = System.getProperty('SPLIT_STRATEGY', 'MINUTES')
    static Map FORMATS = [
            'MINUTES': "YYYY_MM_dd_hh_mm",
            'HOURS'  : "YYYY_MM_dd_hh",
            'DAYS'   : "YYYY_MM_dd",
            'MONTHS' : "YYYY_MM",
    ]

    static void main(def args) {
        log.info("LogAnalyzer Started")
        validateTargetFolder()
        splitLogFiles()
        log.info("Finished splitting files")
        mergeFiles()
        log.info("Finished merging files")
        log.info("LogAnalyzer Finished. Condensed file at: ${TARGET_LOGS_PATH}")
    }

    static void mergeFiles() {
        File finalLog = new File(Paths.get(TARGET_LOGS_PATH, TARGET_LOG_NAME).toString())
        if (!finalLog.exists()) {
            finalLog.createNewFile()
        }

        PriorityQueue<LogEntry> queue = new PriorityQueue(new LogEntryComparator())

        List<File> logFiles = new File(TARGET_LOGS_PATH)
                .listFiles()
                .findAll { it.isFile() && it.name != TARGET_LOG_NAME }
                .sort { new SimpleDateFormat(FORMATS[SPLIT_STRATEGY]).parse(it.name) }
        logFiles.each { File file ->
            file.eachLine { String line ->
                queue.add(LogEntry.fromRow(line))
            }
            while (!queue.empty) {
                finalLog.append("${queue.poll().content}\n")
            }
        }
    }

    static List<File> splitLogFiles() {
        List<File> logFiles = new File(ORIGIN_PATH)
                .listFiles().findAll { it.name =~ '\\.log' }
        logFiles.each { File file ->
            file.eachLine { String line ->
                DateTimeFormatter pattern = DateTimeFormatter.ofPattern(FORMATS[SPLIT_STRATEGY])
                LogEntry entry = LogEntry.fromRow(line)
                String fileName = entry.time.format(pattern)
                File splittedLog = new File(Paths.get(TARGET_LOGS_PATH, fileName).toString())
                if (!splittedLog.exists()) {
                    splittedLog.createNewFile()
                }
                splittedLog.append("$line\n")
            }
        }
    }

    static void validateTargetFolder() {
        File targetPath = new File(TARGET_LOGS_PATH)
        if (!targetPath.exists()) {
            targetPath.mkdirs()
        }
    }
}
