package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import login.Hasher;

/**
 *
 * @author Vavra
 */
public class Student extends User {

    private Classs classs;
    private List<EngagedTest> tests;
    public static final int CATEGORY = 2;

    /**
     * Creates object of student based on these values. ArrayList for
     * EngagedTest is prepared
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param email
     * @param hContainer
     * @param classs
     */
    public Student(int id, String firstName, String lastName, String email, Hasher.HashContainer hContainer, Classs classs) {
        super(id, firstName, lastName, email, hContainer, CATEGORY);
        this.tests = new ArrayList();
        this.classs = classs;
    }

    /**
     * Creates object of student based on user. ArrayList for EngagedTests is
     * created and class of student is mined from database
     *
     * @param user
     * @param con
     */
    public Student(User user, Connection con) {
        super(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getHashContainer(), CATEGORY);
        this.tests = new ArrayList();
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".`students` INNER JOIN " + con.getCatalog() + ".`classes` ON `classes`.`class_id`=`students`.`class_id` WHERE `user_id`=?")) {
            statement.setInt(1, user.getId());
            try (ResultSet r = statement.executeQuery()) {
                r.next();
                this.classs = new Classs(r.getInt("class_id"), r.getInt("grade"), r.getString("suffix"), new ArrayList(), null);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Classs getClasss() {
        return classs;
    }

    public List<EngagedTest> getEngagedTests() {
        return tests;
    }

    public void setTests(List<EngagedTest> tests) {
        this.tests = tests;
    }

    public void addTest(EngagedTest test) {
        tests.add(test);
    }

    /**
     *
     * @param testId
     * @return true if student has written a test with this ID
     */
    public boolean hasWrittenTest(int testId) {
        for (EngagedTest test : this.tests) {
            if (test.id == testId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mines all EngagedTests from database and stores them in tests variable.
     * Tests need to be loaded separately
     *
     * @param con Connection to database
     */
    public void loadEngagedTests(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT tests.*, engaged_tests.begin_date, engaged_tests.volunteer, engaged_tests.deadline, engaged_tests.submit_time, engaged_tests.alterable, engaged_tests.term FROM " + con.getCatalog() + ".engaged_tests INNER JOIN " + con.getCatalog() + ".tests ON engaged_tests.test_id=tests.test_id WHERE user_id=?")) {
            statement.setInt(1, super.getId());
            try (ResultSet r = statement.executeQuery()) {
                while (r.next()) {
                    this.addTest(new EngagedTest(r.getInt("test_id"), this.getId(), r.getString("name"), r.getInt("grade"), r.getInt("time_to_complete"), new ArrayList(), r.getBoolean("practice"), r.getBoolean("volunteer"), r.getTimestamp("begin_date"), r.getTimestamp("deadline"), r.getTimestamp("submit_time"), r.getBoolean("alterable"), r.getInt("term")));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Inserts student into students table, user table is unchanged
     * @param con Connection to database
     * @return
     */
    public boolean addStudentToDatabase(Connection con) {
        try (PreparedStatement statement = con.prepareStatement(
                "INSERT INTO `" + con.getCatalog() + "`.`students` (`user_id`, `class_id`) VALUES (?, ?);")) {
            statement.setInt(1, super.getId());
            statement.setInt(2, this.getClasss().getClassId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * HTML used when creating test-engaging site
     * @param testId id of test
     * @return StringBuilder used to build this HTML
     */
    public StringBuilder engageStudentHTML(int testId) {
        StringBuilder classString = new StringBuilder();
        classString.append("<div>");
        classString.append("<label>");
        classString.append(this.getFirstName()).append(" ").append(this.getLastName()).append(" (").append(this.getEmail()).append(")\n");
        classString.append("<input type=\"checkbox\" class=\"ml-1\" name=\"student-").append(this.getId()).append("/test-").append(testId).append("\">");
        classString.append("</label>");
        classString.append("</div>");
        return classString;
    }

    /**
     * HTML used when creating test-running site
     * @return StringBuilder used to build this HTML
     */
    public StringBuilder runStudentHTML() {
        StringBuilder classString = new StringBuilder();
        classString.append("<div class=\"class-chooser row ml-2 mr-0\">");
        classString.append("<label>");
        classString.append("<a class=\"test-expand2 px-0\" href=\"#\" title=\"Nová otázka\">");
        classString.append("<img src=\"images/plus.png\" class=\"question-creator choose-toggle-icon\">");
        classString.append("<span>\n");
        classString.append(this.getFirstName()).append(" ").append(this.getLastName()).append(" ").append(this.getEmail()).append("\n");
        classString.append("</span>");
        classString.append("</a>");
        classString.append("<input type=\"checkbox\">");
        classString.append("</label>");
        classString.append("<div class=\"col collapse students-chooser px-0 ml-2\">");
        for (EngagedTest test : this.getEngagedTests()) {
            classString.append(test.runHTML());
        }
        classString.append("</div>");
        classString.append("</div>\n");
        return classString;
    }
}
