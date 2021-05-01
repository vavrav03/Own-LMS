/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.CallableStatement;
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
public class EngagedTest extends Test {

    private int userId;
    private boolean volunteer;
    private Timestamp beginDate;
    private Timestamp deadline;
    private Timestamp submitTime;
    private boolean alterable = true;
    private static final int SUBMIT_TOLERANCE = 10_000;
    private int term;
    private int reachedPoints;
    protected int percent;

    public EngagedTest(int id, int userId, String name, int grade, int timeToComplete, List<Question> questions, boolean practice, boolean volunteer, Timestamp beginDate, Timestamp deadline, Timestamp submitTime, boolean alterable, int term) {
        super(id, name, grade, timeToComplete, questions, practice);
        this.userId = userId;
        this.volunteer = volunteer;
        this.beginDate = beginDate;
        this.deadline = deadline;
        this.alterable = alterable;
        this.submitTime = submitTime;
        this.alterable = alterable;
        this.term = term;
    }

    /**
     * Mines test with parameters from the database
     *
     * @param testId
     * @param userId
     * @param con Connection to the database
     * @return Engaged test with given parameters
     */
    public static EngagedTest retrieveById(int testId, int userId, Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT tests.*, engaged_tests.begin_date, engaged_tests.volunteer, engaged_tests.user_id, engaged_tests.deadline, engaged_tests.submit_time, engaged_tests.alterable, engaged_tests.term FROM "
                + con.getCatalog() + ".engaged_tests INNER JOIN " + con.getCatalog() + ".tests ON engaged_tests.test_id=tests.test_id WHERE engaged_tests.test_id=? AND engaged_tests.user_id=?")) {
            statement.setInt(1, testId);
            statement.setInt(2, userId);
            try (ResultSet r = statement.executeQuery()) {
                r.next();
                return new EngagedTest(testId, userId, r.getString("name"), r.getInt("grade"), r.getInt("time_to_complete"), new ArrayList(), r.getBoolean("practice"), r.getBoolean("volunteer"), r.getTimestamp("begin_date"), r.getTimestamp("deadline"), r.getTimestamp("submit_time"), r.getBoolean("alterable"), r.getInt("term"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Saves all student answers contained in Question objects to the database.
     * If answer is present, it is updated. If not, it is added
     *
     * @param con Connection to the database
     */
    public boolean saveAnswers(Connection con) {
        if (!alterable) {
            return false;
        }
        try (CallableStatement s = con.prepareCall("{call " + con.getCatalog() + ".update_student_answer(?, ?, ?, ?, ?)}")) {
            try (PreparedStatement st = con.prepareStatement("DELETE FROM " + con.getCatalog() + ".`student_answers` WHERE user_id=? AND test_id=? AND question_id=? AND subquestion_number=?")) {
                for (Question q : this.getQuestions()) {
                    for (Option o : q.getOptions()) {
                        int selectedOption = o.getSelectedOption();
                        if (selectedOption != -1) {
                            s.setInt(1, this.userId);
                            s.setInt(2, this.id);
                            s.setInt(3, q.getId());
                            s.setInt(4, o.getSubquestionNumber());
                            s.setInt(5, selectedOption);
                            s.addBatch();
                        } else {
                            st.setInt(1, this.userId);
                            st.setInt(2, this.id);
                            st.setInt(3, q.getId());
                            st.setInt(4, o.getSubquestionNumber());
                            st.addBatch();
                        }
                    }
                }
                st.executeBatch();
            }
            s.executeBatch();

            try (PreparedStatement st = con.prepareStatement("UPDATE " + con.getCatalog() + ".`engaged_tests` SET submit_time=? WHERE test_id=? AND user_id=?")) {
                st.setTimestamp(1, submitTime);
                st.setInt(2, this.id);
                st.setInt(3, userId);
                st.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isVolunteer() {
        return volunteer;
    }

    public Timestamp getBeginDate() {
        return beginDate;
    }

    public void startTest() {
        this.deadline = getBeginDate();
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public boolean isAlterable() {
        if (submitTime != null && deadline != null && this.submitTime.after(deadline)) {
            alterable = false;
            setDatabaseAlterableFalse(new DbManager().getConnection());
        }
        return alterable;
    }

    public boolean getIsAlterable() {
        return alterable;
    }

    public void setSubmitTime(Timestamp submitTime) {
        this.submitTime = submitTime;
    }

    public int getReachedPoints() {
        return reachedPoints;
    }
    
    public int getTerm(){
        return term;
    }

    /**
     * Assigns alterable to this.alterable and checks whether submitTime is
     * later than deadline. If yes, alterable is set to false. If database says
     * that test is unalterable, then it remains false
     *
     * @param alterable explicit command to set alterable
     */
    public void setAlterable(boolean alterable, Connection con) {
        if (this.alterable) {
            this.alterable = alterable;
            if (!alterable) {
                setDatabaseAlterableFalse(con);
            }
        }
    }

    /**
     * Checks if deadline has been passed. There is tollerance 5 seconds to
     * catch automatic sending by timer on frontend
     *
     * @param con
     */
    public void checkDeadline(Connection con) {
        if (this.submitTime.after(new Timestamp(deadline.getTime() + 5_000))) {
            setDatabaseAlterableFalse(con);
            alterable = false;
        }
    }

    private void setDatabaseAlterableFalse(Connection con) {
        try (PreparedStatement st = con.prepareStatement("UPDATE " + con.getCatalog() + ".`engaged_tests` SET alterable=? WHERE test_id=? AND user_id=?")) {
            st.setBoolean(1, false);
            st.setInt(2, this.id);
            st.setInt(3, userId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads points from written test. The ammount is stored in reachedPoints.
     * Answers need to be loaded before this method
     *
     * @param con Connection to the database
     */
    public void loadReachedPoints(Connection con) {
        reachedPoints = 0;
        for (Question q : this.getQuestions()) {
            q.setReachedPoints(q.getStartPoints());
            for (Option o : q.getOptions()) {
                if (o.getSelectedOption() == -1) {

                } else if (o.isRight()) {
                    q.setReachedPoints(q.getReachedPoints() + q.getRightOptionPoints());
//                    reachedPoints += q.getRightOptionPoints();
                } else {
                    q.setReachedPoints(q.getReachedPoints() - q.getWrongOptionPoints());
//                    reachedPoints -= q.getWrongOptionPoints();
                }
            }
            reachedPoints += q.getReachedPoints();
        }
        loadPercent();
    }

    /**
     * Loads all answers from the database to this test. They are not evaluated
     *
     * @param con Connection to the database
     */
    public void loadAnswers(Connection con) {
        try (PreparedStatement s = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".`student_answers` WHERE `user_id`=? AND `test_id`=? ORDER BY `question_id`, `subquestion_number`")) {
            s.setInt(1, this.userId);
            s.setInt(2, this.id);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    int lastQuestionId = -1;
                    int questionId = r.getInt("question_id");
                    int probableIndex = 0;
                    Question q = null;
                    if (lastQuestionId != questionId) {
                        q = this.getQuestionById(probableIndex++, questionId);
                    }
                    int subquestionNumber = r.getInt("subquestion_number");
                    Option o = q.getOption(subquestionNumber);
                    o.setSelectedOption(r.getInt("selected_option"));
                    lastQuestionId = questionId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPercent() {
        this.percent = percent(this.reachedPoints, this.getMaxPointsWithStart());
    }

    public static int percent(int points, int maxPoints) {
        return (int) Math.round((double) points / maxPoints * 100);
    }

    public int getPercent() {
        return percent;
    }

    /**
     * @return CSS class used to determine the color of row based on test result
     */
    public String classByPercent() {
        return Test.classByPercent(this.percent);
    }

    /**
     * Mines data from database
     *
     * @param con Connection to the database
     * @return all students who hasn't stated their tests (deadline is null).
     * List of student is sorted in ascending order by their classId
     */
    public static ArrayList<Student> getStudentsNotStartedTests(Connection con) {
        ArrayList<Student> students = new ArrayList();
        try (PreparedStatement statement = con.prepareStatement("SELECT tests.*, engaged_tests.begin_date, engaged_tests.volunteer, engaged_tests.user_id, engaged_tests.deadline, engaged_tests.submit_time, engaged_tests.alterable, engaged_tests.term  FROM " + con.getCatalog() + ".engaged_tests INNER JOIN " + con.getCatalog() + ".tests ON engaged_tests.test_id=tests.test_id WHERE `deadline` IS NULL ORDER BY `user_id`")) {
            try (ResultSet r = statement.executeQuery()) {
                r.next();
                int userId = r.getInt("user_id");
                students.add(new Student(new User(con, userId), con));
                students.get(0).addTest(new EngagedTest(r.getInt("test_id"), r.getInt("user_id"), r.getString("name"), r.getInt("grade"), r.getInt("time_to_complete"), new ArrayList(), r.getBoolean("practice"), r.getBoolean("volunteer"), r.getTimestamp("begin_date"), r.getTimestamp("deadline"), r.getTimestamp("submit_time"), r.getBoolean("alterable"), r.getInt("term")));
                while (r.next()) {
                    userId = r.getInt("user_id");
                    if (userId != students.get(students.size() - 1).getId()) {
                        students.add(new Student(new User(con, userId), con));
                    }
                    students.get(students.size() - 1).addTest(new EngagedTest(r.getInt("test_id"), r.getInt("user_id"), r.getString("name"), r.getInt("grade"), r.getInt("time_to_complete"), new ArrayList(), r.getBoolean("practice"), r.getBoolean("volunteer"), r.getTimestamp("begin_date"), r.getTimestamp("deadline"), r.getTimestamp("submit_time"), r.getBoolean("alterable"), r.getInt("term")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        students.sort((s1, s2) -> {
            return s1.getClasss().getClassId() < s2.getClasss().getClassId() ? -1 : (s1.getClasss().getClassId() == s2.getClasss().getClassId() ? 0 : 1);
        });
        return students;
    }

    /**
     *
     * @return HTML in teacher's test running website
     */
    public StringBuilder runHTML() {
        StringBuilder classString = new StringBuilder();
        classString.append("<div>");
        classString.append("<label>");
        classString.append("<strong>").append(this.name).append("</strong> Zadání: ").append(this.beginDate).append("\n");
        classString.append("<input type=\"checkbox\" class=\"ml-1\" name=\"student-").append(userId).append("/test-").append(this.id).append("\">");
        classString.append("</label>");
        classString.append("</div>");
        return classString;
    }

    /**
     *
     * @return true if there is still time to the deadline
     */
    public boolean isBeforeDeadline() {
        return this.deadline.after(Calendar.getInstance().getTime());
    }

    /**
     *
     * @return StringBuilder with extra information for
     * super.testDescriptionHTML(false)
     */
    public StringBuilder testDescriptionHTML() {
        StringBuilder s = super.testDescriptionHTML(false);
        s.append("<div>Test vám byl přidělen: <b>").append(this.beginDate).append("</b></div>");
        s.append("<div>");
        if (deadline != null) {
            s.append("Test musíte odevzdat do: <b>").append(this.deadline).append("</b>");
        } else {
            s.append("Test jste ještě nespustili");
        }
        s.append("</div>");
        s.append("<div>Test ").append(volunteer ? "je" : "není").append(" dobrovolný</div>");
        s.append("</div>");
        return s;
    }

    /**
     *
     * @return StringBuilder with row in test result table
     */
    public StringBuilder resultTableRow() {
        StringBuilder row = new StringBuilder();
        if (this.isAlterable()) {
            row.append("<tr><td>");
            row.append(this.testDescriptionHTML());
            row.append("</td>");
            row.append("<td colspan=\"2\" class=\"p-0 cell-with-button\">");
            row.append("<form action=\"").append(this.deadline == null ? "/RunTest" : "/Main/test.jsp").append("\"><input type=\"hidden\" name=\"test\" value=\"").append(this.getId()).append("\"></input>");
            row.append("<input type=\"submit\" class=\"px-0\" value=\"").append(this.deadline == null ? "Spustit test" : "Pokračovat v testu").append("\">Spustit test</input></form>");
            row.append("</td>");
        } else {
            row.append("<tr class=\"");
            row.append(this.classByPercent());
            row.append("\"><td>");
            row.append(this.testDescriptionHTML());
            row.append("</td>");
            row.append("<td class=\"d-none d-sm-table-cell\">");
            row.append(this.getDeadline());
            row.append("</td>");
            row.append("<td>");
            row.append("<form action=\"").append("/Main/test.jsp").append("\"><input type=\"hidden\" name=\"test\" value=\"").append(this.getId()).append("\"></input><input type=\"hidden\" name=\"revealSolution\" value=\"true\"></input>");
            row.append("<input type=\"submit\" class=\"px-0 ").append(this.classByPercent()).append("\" value=\"").append(this.reachedPoints).append("/").append(super.getMaxPointsWithStart()).append("b ").append(this.getPercent()).append("%").append("\"></input></form>");
        }
        row.append("</td>");
        row.append("</tr>");
        return row;
    }

    /**
     *
     * @param points sum of acquired points from all tests in table
     * @param maximum sum of maximum points that student could acquire from all
     * tests in table
     * @return HTML representing sum row in result table
     */
    public static StringBuilder resultTableSum(int points, int maximum) {
        StringBuilder row = new StringBuilder();
        int percent = EngagedTest.percent(points, maximum);
        String color = "";
        if (maximum > 0) {
            color = Test.classByPercent(percent);
        }
        row.append("<tr class=\"").append(color).append("\">");
        row.append("<td>Celkem</td>");
        row.append("<td class=\"d-none d-sm-table-cell\"></td>");
        row.append("<td>");
        row.append(points).append("/").append(maximum).append("b ").append(percent).append("%");
        row.append("</td>");
        row.append("</tr>");
        return row;
    }
}
