package log_analyzer

import spock.lang.Specification

import java.nio.file.Paths

class LogAnalyzerSplitterSpec extends Specification {
    def "Should validate a file is splitted by days"() {
        setup: "Creating a file"
        File tmpDir = File.createTempDir()
        File tmpTargetDir = File.createTempDir()
        File tmpFile = new File(Paths.get(tmpDir.path, "${System.currentTimeMillis().toString()}.log").toString())
        tmpFile.createNewFile()
        tmpFile.append("2020-03-24T21:14:14.367, Server4, This is an awesome log 193\n")
        tmpFile.append("2020-03-18T21:14:14.389, Server4, This is an awesome log 125\n")

        System.setProperty('ORIGIN_PATH', tmpDir.path)
        System.setProperty('SPLIT_STRATEGY', 'DAYS')
        System.setProperty('TARGET_LOGS_PATH', tmpTargetDir.path)

        when: "File is read"
        LogAnalyzer.splitLogFiles()

        then: "Two files appear"
        tmpTargetDir.listFiles().findAll { it.isFile() }.size() == 2

        cleanup: "Remove tmp dirs"
        tmpDir.deleteOnExit()
        tmpTargetDir.deleteDir()
        System.clearProperty('TARGET_LOGS_PATH')
        System.clearProperty('ORIGIN_PATH')
        System.clearProperty('SPLIT_STRATEGY')
    }
}
