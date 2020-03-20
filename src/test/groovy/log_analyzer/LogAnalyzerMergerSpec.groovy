package log_analyzer

import spock.lang.Shared
import spock.lang.Specification
import java.nio.file.Paths

class LogAnalyzerMergerSpec extends Specification {

    def "Should validate a file is ordered and condensed"() {
        setup: "Creating a file"
        File tmpTargetDir = File.createTempDir()
        File tmpFile1 = new File(Paths.get(tmpTargetDir.path, "2020_03_19").toString())
        tmpFile1.createNewFile()
        tmpFile1.append("2020-03-19T21:14:14.389, Server4, This is an awesome log 125\n")
        tmpFile1.append("2020-03-19T20:14:14.389, Server3, This is an awesome log 125\n")


        File tmpFile2 = new File(Paths.get(tmpTargetDir.path, "2020_03_20").toString())
        tmpFile2.createNewFile()
        tmpFile2.append("2020-03-20T21:14:14.389, Server4, This is an awesome log 125\n")
        tmpFile2.append("2020-03-20T20:14:14.389, Server3, This is an awesome log 125\n")

        System.setProperty('TARGET_LOGS_PATH', tmpTargetDir.path)
        System.setProperty('SPLIT_STRATEGY', 'DAYS')

        when: "Files are merged"
        LogAnalyzer.mergeFiles()

        then: "Condensed log is ordered"

        List<String> condensedContent = tmpTargetDir
                .listFiles()
                .findAll { it.isFile() && it.name =~ LogAnalyzer.TARGET_LOG_NAME }.first().readLines()
        List<String> expectedContent = [
                "2020-03-19T20:14:14.389, Server3, This is an awesome log 125",
                "2020-03-19T21:14:14.389, Server4, This is an awesome log 125",
                "2020-03-20T20:14:14.389, Server3, This is an awesome log 125",
                "2020-03-20T21:14:14.389, Server4, This is an awesome log 125"
        ]
        condensedContent == expectedContent


        cleanup: "Remove tmp dirs"
        tmpTargetDir.deleteOnExit()
        System.clearProperty('TARGET_LOGS_PATH')
        System.clearProperty('SPLIT_STRATEGY')
    }
}
