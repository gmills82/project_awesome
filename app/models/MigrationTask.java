package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 User: justin.podzimek
 Date: 9/18/15
 */
@Entity
@Table(name = "migration_tasks")
public class MigrationTask extends Model {

    /*************************************************************
     PROPERTIES
     ************************************************************/

    /** Primary note ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /** The name of the task */
    @Column(name = "task")
    private String taskName;

    /** Persistence finder */
    public static Model.Finder<Long, MigrationTask> finder = new Model.Finder<>(Long.class, MigrationTask.class);

    /*************************************************************
     CONSTRUCTORS
     ************************************************************/

    /**
     Constructor with provided task name

     @param taskName Migration task name
     */
    public MigrationTask(String taskName) {
        this.taskName = taskName;
    }

    /*************************************************************
     GETTERS & SETTERS
     ************************************************************/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /*************************************************************
     PERSISTENCE
     ************************************************************/

    /**
     Returns a migration task for the provided name

     @param name Migration name
     @return Migration task
     */
    public static MigrationTask getByTaskName(String name) {
        return finder
                .where()
                .eq("task", name)
                .findUnique();
    }
}
