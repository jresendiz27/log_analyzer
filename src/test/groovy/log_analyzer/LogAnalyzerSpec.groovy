package log_analyzer

import spock.lang.Specification

import java.nio.file.Paths

class LogAnalyzerSpec extends Specification {

    def "Should validate files are ordered"() {
        setup: "Creating server files"
        File tmpDir = File.createTempDir()
        File tmpTargetDir = File.createTempDir()


        File tmpFile = new File(Paths.get(tmpDir.path, "server.log").toString())
        tmpFile.createNewFile()
        tmpFile.append("2020-03-19T21:14:14.389, Server4, This is an awesome log 125\n")
        tmpFile.append("2020-03-19T20:14:14.389, Server3, This is an awesome log 125\n")


        File tmpFile2 = new File(Paths.get(tmpDir.path, "server2.log").toString())
        tmpFile2.createNewFile()
        tmpFile2.append("2020-03-20T21:14:14.389, Server4, This is an awesome log 125\n")
        tmpFile2.append("2020-03-20T20:14:14.389, Server3, This is an awesome log 125\n")

        System.setProperty('ORIGIN_PATH', tmpDir.path)
        System.setProperty('SPLIT_STRATEGY', 'DAYS')
        System.setProperty('TARGET_LOGS_PATH', tmpTargetDir.path)

        when: "Algorithm is used"
        LogAnalyzer.validateTargetFolder()
        LogAnalyzer.splitLogFiles()
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
        tmpDir.deleteOnExit()
        tmpTargetDir.deleteOnExit()
        System.clearProperty('TARGET_LOGS_PATH')
        System.clearProperty('SPLIT_STRATEGY')
    }
}
