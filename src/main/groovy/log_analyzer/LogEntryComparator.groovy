package log_analyzer

class LogEntryComparator implements Comparator<LogEntry> {

    @Override
    int compare(LogEntry o1, LogEntry o2) {
        if (o1.time == o2.time) {
            return 0
        } else if (o1.time < o2.time) {
            return -1
        } else {
            return 1
        }
    }
}
