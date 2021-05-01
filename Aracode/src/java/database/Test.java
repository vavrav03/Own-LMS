package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Vavra
 */
public class Test {

    protected int id;
    protected String name;
    protected int grade;
    protected int timeToComplete;
    protected List<Question> questions;
    protected boolean practice;
    /**
     * number of points in the beginning (same as maximum number of minus
     * points)
     */
    protected int startPoints;
    /**
     * maximum of plus points
     */
    protected int maxPoints;

    public Test(int id, String name, int grade, int timeToComplete, List<Question> questions, boolean practice) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.timeToComplete = timeToComplete;
        this.questions = questions;
        this.practice = practice;
    }

    public Test(String name, int grade, int days, int hours, int minutes, int seconds, boolean practice) {
        this.name = name;
        this.grade = grade;
        if (days < 0 || days > 365 || hours < 0 || hours > 24 || minutes < 0 || minutes > 60 || seconds < 0 || seconds > 60) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        this.timeToComplete = ((days * 24 + hours) * 60 + minutes) * 60 + seconds;
        this.questions = new ArrayList();
        this.practice = practice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }

    public int getTimeToComplete() {
        return timeToComplete;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public boolean isPractice() {
        return practice;
    }

    /**
     *
     * @return String in notation like this 01:02:11:15
     */
    public StringBuilder normalTimeNotation() {
        StringBuilder s = new StringBuilder();
        s.append(timeToComplete / 86400).append(":");
        int hours = timeToComplete % 86400 / 3600;
        if (hours < 10) {
            s.append("0");
        }
        s.append(hours).append(":");
        int minutes = this.timeToComplete % 3600 / 60;
        if (minutes < 10) {
            s.append("0");
        }
        s.append(minutes).append(":");
        int seconds = this.timeToComplete % 60;
        if (seconds < 10) {
            s.append("0");
        }
        s.append(seconds);
        return s;
    }

    @Override
    public String toString() {
        return "Test{" + "id=" + id + ", name=" + name + ", grade=" + grade + ", timeToComplete=" + timeToComplete + ", questions=" + questions + '}';
    }

    /**
     * Adds questions and new test to the database
     *
     * @param con Connection to the database
     * @param createQuestionsOnly if true, no test will be created
     * (used_questions table will not be changed)
     */
    public void addNewTest(Connection con, boolean createQuestionsOnly) {
        addQuestionsToDatabase(con);
        if (!createQuestionsOnly) {
            addTestToDatabase(con);
            try (PreparedStatement connectQuestions = con.prepareStatement("INSERT INTO " + con.getCatalog() + ".`used_questions` (`test_id`, `question_id`) VALUES (?, ?)")) {
                for (Question question : this.getQuestions()) {
                    connectQuestions.setInt(1, this.getId());
                    connectQuestions.setInt(2, question.getId());
                    connectQuestions.addBatch();
                }
                connectQuestions.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds record of new test to the tests table
     *
     * @param con Connection to the database
     */
    private void addTestToDatabase(Connection con) {
        try (PreparedStatement addTest = con.prepareStatement("INSERT INTO " + con.getCatalog() + ".`tests` (`name`, `grade`, `time_to_complete`, `practice`)"
                + " VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            addTest.setString(1, this.getName());
            addTest.setInt(2, this.getGrade());
            addTest.setInt(3, this.getTimeToComplete());
            addTest.setBoolean(4, this.isPractice());
            addTest.executeUpdate();
            try (ResultSet id = addTest.getGeneratedKeys()) {
                id.next();
                this.setId((int) id.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds all questions contained in questions list to the questions table in
     * database. Options in questions lists are also added to the database
     *
     * @param con Connection to the database
     */
    private void addQuestionsToDatabase(Connection con) {
        try (PreparedStatement addQuestion = con.prepareStatement("INSERT INTO " + con.getCatalog() + ".`questions` (`question_text`, `right_option_points`, `wrong_option_points`, `type`)"
                + " VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            for (Question question : questions) {
                addQuestion.setString(1, question.getText());
                addQuestion.setInt(2, question.getRightOptionPoints());
                addQuestion.setInt(3, question.getWrongOptionPoints());
                addQuestion.setInt(4, question.getType());
                addQuestion.addBatch();
            }
            addQuestion.executeBatch();
            try (ResultSet r = addQuestion.getGeneratedKeys();) {
                int i = 0;
                while (r.next()) {
                    questions.get(i).setQuestionId((int) r.getLong(1));
                    questions.get(i++).insertOptionsToDatabase(con);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * initializes maxPoints variable by iterating over questions and returns it
     *
     * @return maxPoints
     */
    public int loadMaxPoints() {
        for (Question question : this.getQuestions()) {
            maxPoints += question.getMaxPoints();
        }
        return maxPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    /**
     *
     * @return total number of points in test
     */
    public int getMaxPointsWithStart() {
        return maxPoints + startPoints;
    }

    /**
     * initializes startPoints variable by iterating over questions and returns
     * it
     *
     * @return startPoints
     */
    public int loadStartPoints() {
        for (Question question : this.getQuestions()) {
            startPoints += question.getStartPoints();
        }
        return startPoints;
    }

    public int getStartPoints() {
        return startPoints;
    }

    /**
     *
     * @param percent number of percent in test
     * @return CSS class used to determine the color of row based on test result
     */
    public static String classByPercent(int percent) {
        if (percent >= 85) {
            return "test-grade-1";
        } else if (percent >= 70) {
            return "test-grade-2";
        } else if (percent >= 55) {
            return "test-grade-3";
        } else if (percent >= 33) {
            return "test-grade-4";
        } else {
            return "test-grade-5";
        }
    }

    /**
     *
     * @param con Connection to database
     * @return ArrayList of all tests stored in database (you need to load
     * questions by yourself)
     */
    public static ArrayList<Test> retrieveTests(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".tests")) {
            try (ResultSet r = statement.executeQuery()) {
                ArrayList<Test> tests = new ArrayList();
                while (r.next()) {
                    tests.add(new Test(r.getInt("test_id"), r.getString("name"), r.getInt("grade"), r.getInt("time_to_complete"), new ArrayList(), r.getBoolean("practice")));
                }
                return tests;
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    /**
     * Loads questions from the database these are paired to this test
     *
     * @param con Connection to the database
     */
    public void loadQuestions(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".`used_questions` INNER JOIN " + con.getCatalog() + ".`questions` ON `used_questions`.`question_id`=`questions`.`question_id` WHERE `test_id`=?")) {
            statement.setInt(1, this.id);
            try (ResultSet r = statement.executeQuery()) {
                while (r.next()) {
                    Question question = new Question(r.getInt("question_id"), r.getString("question_text"), r.getInt("right_option_points"), r.getInt("wrong_option_points"), r.getInt("type"));
                    question.loadOptions(con);
                    this.addQuestion(question);
                }
            }
            loadStartPoints();
            loadMaxPoints();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Searches in questions list for quetsion by its ID
     *
     * @param probableIndex probable index of question in questions list.
     * Variable is present for bigger performance
     * @param id ID of wanted question
     * @return Question object if present or null
     */
    public Question getQuestionById(int probableIndex, int id) {
        if (probableIndex < this.getQuestions().size() && this.questions.get(probableIndex).getId() == id) {
            return this.questions.get(probableIndex);
        }
        for (Question q : this.getQuestions()) {
            if (q.getId() == id) {
                return q;
            }
        }
        return null;
    }

    /**
     * Helps to create EngageTest website
     *
     * @param classes classes StringBuilder when creating EngageTest website
     * @return HTML representing one test with classes in creating EngageTest
     * webiste
     */
    public StringBuilder testHTML(StringBuilder classes) {
        StringBuilder out = new StringBuilder();
        out.append("<div class=\"test-chooser row ml-2 mr-0 mb-2\">");
        out.append("<a class=\"test-expand px-0\" href=\"#\" title=\"Nová otázka\">");
        out.append("<img src=\"images/plus.png\" class=\"question-creator choose-toggle-icon\">");
        out.append("<span>" + "\n");
        out.append(this.getName()).append("\n");
        out.append("</span>");
        out.append("</a>");
        out.append("<div class=\"collapse col container-fluid classes-chooser px-0\">");
        out.append(classes);
        out.append("<div class=\"px-2\">");
        this.testDescriptionHTMLUncollapsable(out, true);
        out.append("</div>");
        out.append("</div>");
        return out;
    }

    /**
     * HTML of test description header in student result table row
     *
     * @param closeDiv - if true, </div> is added to the end of the String
     * @return HTML that helps to create result row in student result table
     */
    public StringBuilder testDescriptionHTML(boolean closeDiv) {
        StringBuilder test = new StringBuilder();
        test.append("<span class=\"test-description-title\"> <a class=\"collapsed\" data-toggle=\"collapse\" href=\"#collapse").append(this.id).append("\" aria-controls=\"collapse")
                .append(this.id).append("\">").append(this.name).append("</a></span>");
        test.append("<div id=\"collapse").append(this.id).append("\" class=\"collapse in\">");
        test.append("<div>");
        testDescriptionHTMLUncollapsable(test, closeDiv);

        return test;
    }

    /**
     * Description of test in HTML used in student result table row and test
     * engaging
     *
     * @param test StringBuilder to which text is added
     * @param closeDiv if true, </div> is added to the end of the String
     * @return test StringBuilder
     */
    public StringBuilder testDescriptionHTMLUncollapsable(StringBuilder test, boolean closeDiv) {
        test.append("<div>Počáteční počet bodů: <b>").append(startPoints).append("</b></div>");
        test.append("<div>Maximální počet bodů: <b>").append(this.getMaxPointsWithStart()).append("</b></div>");
        test.append("<div>Doba na vypracování testu:<b>").append(this.normalTimeNotation()).append("</b></div>");
        test.append("<div>Mohou vypracovávat studenti <b>").append(this.grade).append("</b>. ročníku a výše </div>");
        if (closeDiv) { //TODO odevzdano
            test.append("</div>");
        }
        return test;
    }
}
