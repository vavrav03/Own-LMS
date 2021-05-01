/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * represents Class of studnets. There are 3s to avoid name colision with Class
 * class.
 *
 * @author Vavra
 */
public class Classs {

    private int classId;
    private int grade;
    private String suffix;
    private ArrayList<Student> students;
    private Teacher teacher;

    public Classs() {

    }

    public Classs(int classId, int grade, String suffix, ArrayList<Student> students, Teacher teacher) {
        this(grade, suffix, students, teacher);
        this.classId = classId;
    }

    public Classs(int grade, String suffix, ArrayList<Student> students, Teacher teacher) {
        this.grade = grade;
        this.suffix = suffix;
        this.students = students;
        this.teacher = teacher;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getClassId() {
        return classId;
    }

    public int getGrade() {
        return grade;
    }

    public String getSuffix() {
        return suffix;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        this.students.add(student);
    }

    @Override
    public String toString() {
        return grade + "." + suffix;
    }

    /**
     * Adds class to the database
     *
     * @param con Connection to the database
     * @return true if class has been added
     */
    public boolean addClasss(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("INSERT INTO `" + con.getCatalog() + "`.`classes` (`grade`, `suffix`, `teacher_id`) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getGrade());
            statement.setString(2, this.getSuffix());
            statement.setInt(3, this.getTeacher().getId());
            statement.executeUpdate();
            try (ResultSet id = statement.getResultSet()) {
                this.classId = id.getInt("1");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mines all students form the database and stores them in students list
     *
     * @param con Connection to the database
     */
    public void loadStudents(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT users.id, users.first_name, users.last_name, users.email, students.class_id FROM " + con.getCatalog() + ".students INNER JOIN " + con.getCatalog() + ".users ON users.id=students.user_id WHERE students.class_id=?")) {
            statement.setInt(1, this.classId);
            try (ResultSet r = statement.executeQuery()) {
                while (r.next()) {
                    Student student = new Student(r.getInt("id"), r.getString("first_name"), r.getString("last_name"), r.getString("email"), null, this);
                    student.loadEngagedTests(con);
                    this.addStudent(student);
                }
            }
        } catch (SQLException ex) {
        }
    }

    /**
     * HTML produced when creating test-engaging site
     *
     * @param con Connection to database
     * @param students result of student.engageStudentHTML()
     * @return StringBuilder used to produce HTML
     */
    public StringBuilder engageClassHTML(Connection con, StringBuilder students) {
        StringBuilder classString = new StringBuilder();
        classString.append("<div class=\"class-chooser row ml-2 mr-0\">");
        classString.append("<label>");
        classString.append("<a class=\"test-expand2 px-0\" href=\"#\" title=\"Nová otázka\">");
        classString.append("<img src=\"images/plus.png\" class=\"question-creator choose-toggle-icon\">");
        classString.append("<span>\n");
        classString.append(this).append("\n");
        classString.append("</span>");
        classString.append("</a>");
        classString.append("<input type=\"checkbox\">");
        classString.append("</label>");
        classString.append("<div class=\"col collapse students-chooser px-0 ml-2\">");
        classString.append(students);
        classString.append("</div>");
        classString.append("</div>\n");
        return classString;
    }

    public static void main(String[] args) {
        Classs classs = new Classs(1, 2, "E", new ArrayList(), null);
        DbManager db = new DbManager();
        classs.loadStudents(db.getConnection());
    }
}
