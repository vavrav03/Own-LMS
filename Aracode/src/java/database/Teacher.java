/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Vavra
 */
public class Teacher extends User {

    private List<Classs> classes;
    public static final int CATEGORY = 1;

    public Teacher(User user, List<Classs> classes) {
        super(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getHashContainer(), CATEGORY);
        this.classes = classes;
        if (classes == null) {
            this.classes = new ArrayList();
        }
    }

    public List<Classs> getClasses() {
        return classes;
    }

    /**
     * @param grade
     * @return all classes with same grade as given
     */
    public List<Classs> getClassesWithGrade(int grade) {
        int firstItem = firstOccurence(grade);
        if (firstItem == -1) {
            throw new IllegalArgumentException("rocnik neexistuje");
        }
        for (int i = firstItem; i < classes.size(); i++) {
            if (classes.get(i).getGrade() != grade) {
                return classes.subList(firstItem, i - 1);
            }
        }
        return classes.subList(firstItem, classes.size());
    }

    /**
     *
     * @param grade
     * @return all classes with bigger or same grade as given
     */
    public List<Classs> getClassesWithBiggerGrade(int grade) {
        try {
            return classes.subList(firstOccurence(grade), classes.size());
        } catch(Exception e){
            
        }
        return new ArrayList();
    }

    private int firstOccurence(int grade) {
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).getGrade() >= grade) {
                return i;
            }
        }
        return -1;
    }

    /**
     * adds class to classes list
     *
     * @param classs
     */
    public void addClasss(Classs classs) {
        classes.add(classs);
    }

    /**
     * Mines all students from database and stores them in classes list
     *
     * @param con Connection to database
     */
    public void loadClasses(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".classes WHERE teacher_id=?")) {
            statement.setInt(1, this.getId());
            try (ResultSet r = statement.executeQuery()) {
                while (r.next()) {
                    Classs classs = new Classs(r.getInt("class_id"), r.getInt("grade"), r.getString("suffix"), new ArrayList(), this);
                    classs.loadStudents(con);
                    this.addClasss(classs);
                }
                this.classes.sort((Classs a, Classs b) -> {
                    return Integer.compare(a.getGrade(), b.getGrade());
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The method iterates over all students in all classes and if he finds id
     * match returns true
     *
     * @param id studentId
     * @return Student instance if teacher teaches this student, otherwise null
     */
    public Student teachesStudent(int id) {
        for (Classs c : this.classes) {
            for (Student s : c.getStudents()) {
                if (s.getId() == id) {
                    return s;
                }
            }
        }
        return null;
    }
}
