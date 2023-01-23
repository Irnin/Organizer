package irnin.classes;

import irnin.organizer.QueryExecutor;

public class toDo {
    int id;
    int groupId;
    String subject;
    String status;
    int completedBy;
    String completedDate;

    public toDo(int id, int groupId, String subject, String status, int completedBy, String completedDate) {
        this.id = id;
        this.groupId = groupId;
        this.subject = subject;
        this.status = status;
        this.completedBy = completedBy;
        this.completedDate = completedDate;
    }

    public boolean isComplete()
    {
        if(status.equals("w trakcie")) {
            return false;
        }
        else {
            return true;
        }
    }

    public String getSubject(){
        return subject;
    }

    public static void removeItem(int groupId, String subject) {
        String query = String.format("DELETE FROM toDoList WHERE groupId = %d AND subject = '%s'", groupId, subject);
        QueryExecutor.executeQuery(query);
    }
}
