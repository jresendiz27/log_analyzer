package log_analyzer

import groovy.time.TimeCategory

import java.nio.file.Paths
import java.time.LocalDateTime

class LogGenerator {
    static void main(def args) {
        generateLogs()
    }

    static void generateLogs() {
        Integer numFiles = System.getProperty('NUM_RANDOM_FILES', '4') as Integer
        Random rand = new Random()
        Integer logLimit = System.getProperty('LOG_LIMIT', '10') as Integer
        String targePath = System.getProperty('TARGET_PATH', '/home/jresendiz/logs')
        use(TimeCategory) {
            (0..numFiles).each { Integer fileNumber ->
                Integer minutes = rand.nextInt(30)
                File file = new File(Paths.get(targePath, "server-${fileNumber}.log").toString())
                if (file.exists()) {
                    file.delete()
                } else {
                    file.createNewFile()
                }
                (0..logLimit).each {
                    LocalDateTime localDate = null
                    if(rand.nextBoolean()) {
                        localDate = LocalDateTime.now().plusMinutes(minutes)
                    } else {
                        localDate = LocalDateTime.now().minusMinutes(minutes)
                    }
                    file.append("${localDate.toString()}, Server-${fileNumber}, This is an awesome log ${rand.nextInt(255)}\n")
                }
            }
        }
    }
}
