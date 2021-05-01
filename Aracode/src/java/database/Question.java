/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.util.ArrayList;
import java.util.List;
import database.Option.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;

/**
 *
 * @author Vavra
 */
public class Question {

    private int id;
    private String text;
    private int rightOptionPoints;
    private int wrongOptionPoints;
    private int type;
    private List<Option> options;
    private int reachedPoints = -1;

    /**
     * Constructor for frontend data
     *
     * @param questionText
     * @param rightOptionPoints
     * @param wrongOptionPoints
     * @param type
     * @throws NullPointerException
     */
    public Question(String questionText, String rightOptionPoints, String wrongOptionPoints, String type) throws NullPointerException {
        if (questionText == null || rightOptionPoints == null || wrongOptionPoints == null || type == null) {
            throw new NullPointerException();
        }
        this.text = questionText;
        this.rightOptionPoints = Integer.parseInt(rightOptionPoints);
        this.wrongOptionPoints = Integer.parseInt(wrongOptionPoints);
        if (type.equals("booleanQuestion")) {
            this.type = 0;
        } else if (type.equals("fillInTextQuestion")) {
            this.type = 1;
        } else if (type.equals("connectQuestion")) {
            this.type = 2;
        } else if (type.equals("orderQuestion")) {
            this.type = 3;
        }
        this.options = new ArrayList();
    }

    public Question(int questionId, String questionText, int rightOptionPoints, int wrongOptionPoints, int type) {
        this.id = questionId;
        this.text = questionText;
        this.rightOptionPoints = rightOptionPoints;
        this.wrongOptionPoints = wrongOptionPoints;
        this.type = type;
        this.options = new ArrayList();
    }

    public int getId() {
        return id;
    }

    public void setQuestionId(int questionId) {
        this.id = questionId;
    }

    public String getText() {
        return text;
    }

    public int getRightOptionPoints() {
        return rightOptionPoints;
    }

    public int getWrongOptionPoints() {
        return wrongOptionPoints;
    }

    public int getType() {
        return type;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
    
    public void setReachedPoints(int reachedPoints){
        this.reachedPoints = reachedPoints;
    }
    
    public int getReachedPoints(){
        return reachedPoints;
    }
    
    public Option getOption(int subquestionNumber){
        int minus = this.type == 3 ? -1 : 0;
        if(this.getOptions().get(subquestionNumber + minus).subquestionNumber == subquestionNumber){
            return this.getOptions().get(subquestionNumber + minus);
        }
        for(Option o : this.getOptions()){
            if(o.subquestionNumber == subquestionNumber){
                return o;
            }
        }
        return null;
    }

    /**
     * Adds option to options list and checks whether that option has same type
     * as question type. If not, IllegalArgumentException is thrown
     *
     * @param option Option object
     */
    public void addOption(Option option) {
        switch (type) {
            case 0:
                if (!(option instanceof BooleanOption)) {
                    throw new IllegalArgumentException();
                }
                break;
            case 1:
                if (!(option instanceof FillInTextOption)) {
                    throw new IllegalArgumentException();
                }
                break;
            case 2:
                if (!(option instanceof ConnectOption)) {
                    throw new IllegalArgumentException();
                }
                break;
            case 3:
                if (!(option instanceof OrderOption)) {
                    throw new IllegalArgumentException();
                }
                break;
        }
        this.options.add(option);
    }

    @Override
    public String toString() {
        return "Question{" + "questionId=" + id + ", questionText=" + text + ", rightOptionPoints=" + rightOptionPoints + ", wrongOptionPoints=" + wrongOptionPoints + ", type=" + type + ", options=" + options + '}';
    }

    public int getMaxPoints() {
        return options.size() * this.rightOptionPoints;
    }

    public int getStartPoints() {
        return options.size() * this.wrongOptionPoints;
    }
    
    /**
     * Loads all options from the database
     * @param con Connection to the database
     */
    public void loadOptions(Connection con) {
        this.options = Option.createOptions(this, con);
    }

    /**
     * Adds all options from option list to the database
     *
     * @param con Connection to database
     */
    public void insertOptionsToDatabase(Connection con) throws SQLException {
        PreparedStatement addOption = null;
        try {
            switch (this.getType()) {
                case 0:
                    addOption = con.prepareStatement("INSERT INTO " + con.getCatalog() + ".`options_0` (`question_id`, `answer`) VALUES(?, ?)");
                    addOption.setInt(1, this.getId());
                    addOption.setBoolean(2, ((BooleanOption) this.getOptions().get(0)).getRightAnswer());
                    addOption.addBatch();
                    break;
                case 1:
                    addOption = con.prepareStatement("INSERT INTO " + con.getCatalog() + ".`options_1` (`question_id`, `subquestion_number`, `option_number`, `text`) VALUES(?, ?, ?, ?)");
                    for (int i = 0; i < this.getOptions().size(); i++) {
                        FillInTextOption option = (FillInTextOption) this.getOptions().get(i);
                        addOption.setInt(1, this.getId());
                        addOption.setInt(2, i);
                        addOption.setInt(3, 0);
                        addOption.setString(4, option.getRightWord());
                        addOption.addBatch();
                        for (int j = 0; j < option.getWrongWords().size(); j++) {
                            addOption.setInt(1, this.getId());
                            addOption.setInt(2, i);
                            addOption.setInt(3, j + 1);
                            addOption.setString(4, option.getWrongWords().get(j));
                            addOption.addBatch();
                        }
                    }
                    break;
                case 2:
                    addOption = con.prepareCall("INSERT INTO " + con.getCatalog() + ".`options_2` (`question_id`, `option_number`, `text`, `definition`) VALUES(?, ?, ?, ?)");
                    for (int i = 0; i < this.getOptions().size(); i++) {
                        ConnectOption option = (ConnectOption) this.getOptions().get(i);
                        addOption.setInt(1, this.getId());
                        addOption.setInt(2, option.getSubquestionNumber());
                        addOption.setString(3, option.getText());
                        addOption.setString(4, option.getDefinition());
                        addOption.addBatch();
                    }
                    break;
                case 3:
                    addOption = con.prepareCall("INSERT INTO " + con.getCatalog() + ".`options_3` (`question_id`, `option_number`, `answer`) VALUES(?, ?, ?)");
                    for (int i = 0; i < this.getOptions().size(); i++) {
                        OrderOption option = (OrderOption) this.getOptions().get(i);
                        addOption.setInt(1, this.getId());
                        addOption.setInt(2, option.getSubquestionNumber());
                        addOption.setString(3, option.getText());
                        addOption.addBatch();
                    }
                    break;
            }
            addOption.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            addOption.close();
        }
    }

    /**
     * Creates HTML of quesiton and it's options for students
     * @param questionNumber number of question determined during test form
     * creating
     * @return StringBuilder of question their options
     */
    public StringBuilder questionHTML(int questionNumber) {
        int startPoints = this.getStartPoints();
        String taskText = this.getType() == 1 ? "Doplňte do textu" : this.getText();
        StringBuilder s = new StringBuilder("<div id=\"question").append(questionNumber).append("\" class=\"question-UI\">")
                .append("<h6 class=\"mb-1\"><b>Otázka ").append(questionNumber)
                .append("<span data-toggle=\"tooltip\" data-placement=\"right\" title=\"Start: ")
                .append(startPoints)
                .append("B, Maximum: ").append(startPoints + this.getMaxPoints())
                .append("B, Správně +").append(this.rightOptionPoints)
                .append("B, Špatně -").append(this.wrongOptionPoints)
                .append("B\"> (").append(this.getStartPoints()).append(", ")
                .append(startPoints + this.getMaxPoints()).append("</span>)</b> - ")
                .append(taskText);
        
        if(this.reachedPoints != -1){
            s.append(" <span style=\"color:green\">").append(reachedPoints).append(" bodů</span>");
        }
        s.append("</h6>");
        switch (this.getType()) {
            case 0:
                s.append(booleanQuestionTaskHTML());
                break;
            case 1:
                s.append(fillInTextTaskHTML());
                break;
            case 2:
                s.append(connectTaskHTML());
                break;
            case 3:
                s.append(orderOption());
                break;
        }
        s.append("</div>");
        return s;
    }

    private StringBuilder booleanQuestionTaskHTML() {
        StringBuilder s = new StringBuilder();
        s.append("<label><input type=\"radio\" ").append(this.getOptions().get(0).getSelectedOption() == 1 ? "checked=\"checked\"" : "").append("name=\"question-").append(this.id).append("\" value=\"true\"> Pravda</label><br>");
        s.append("<label><input type=\"radio\" ").append(this.getOptions().get(0).getSelectedOption() == 0 ? "checked=\"checked\"" : "").append(" name=\"question-").append(this.id).append("\" value=\"false\"> Lež</label><br>");
        s.append("<label><input type=\"radio\" ").append(this.getOption(0).getSelectedOption() == -1 ? "checked=\"checked\"" : "").append(" name=\"question-").append(this.id).append("\" value=\"dont-know\"> Nevím</label><br>");
        return s;
    }

    private StringBuilder fillInTextTaskHTML() {
        StringBuilder s = new StringBuilder();
        String[] splitted = this.getText().split("___");
        for (int i = 0; i < this.getOptions().size(); i++) {
            s.append(splitted[i]);
            FillInTextOption f = (FillInTextOption) this.getOptions().get(i);
            s.append("<select class=\"mx-0 fillInTextQuestion\" name=\"").append("question-").append(this.id)
                    .append("-subquestion-number-").append(f.getSubquestionNumber()).append("\">");
            ArrayList<String> answers = new ArrayList();
            answers.add(f.getRightWord());
            answers.addAll(f.getWrongWords());
            ArrayList<String> options = new ArrayList();
            for (int j = 0; j < answers.size(); j++) {
                options.add("<option " + (f.getSelectedOption() == j ? "selected" : "") + " value=\"" + answers.get(j) + "\">" + answers.get(j) + "</option>");
            }
            Collections.shuffle(options);
            for(String option : options){
                s.append(option);
            }
            s.append("<option ").append(f.getSelectedOption() == -1 ? "selected" : "").append(" value=\"dont-know\">");
            s.append("</select>");
        }
        if (this.getOptions().size() != splitted.length) {
            s.append(splitted[splitted.length - 1]);
        }
        return s;
    }

    private StringBuilder connectTaskHTML() {
        StringBuilder s = new StringBuilder();
        s.append("<ol>");
        for (Option o : this.getOptions()) {
            s.append("<li>").append(((ConnectOption) o).getText()).append("</li>");
        }
        s.append("</ol><div>");
        Collections.shuffle(this.getOptions());
        for (Option o : this.getOptions()) {
            ConnectOption c = (ConnectOption) o;
            s.append("<div>");
            s.append("<select ").append("name=\"").append("question-").append(this.id).append("-").append(c.getDefinition()).append("\">");
            for (int i = 0; i < this.getOptions().size(); i++) {
                s.append("<option ").append(c.getSelectedOption() == i ? "selected" : "").append(" value=\"").append(i).append("\">").append(i + 1).append("</option>");
            }
            s.append("<option value=\"dont-know\" ").append(c.getSelectedOption() == -1 ? "selected" : "").append("></option>");
            s.append("</select><span>").append(c.getDefinition()).append("</span></div>");
        }
        s.append("</div>");
        return s;
    }

    private StringBuilder orderOption() {
        StringBuilder s = new StringBuilder();
        s.append("<div>");
        Collections.shuffle(this.getOptions());
        for (Option o : this.getOptions()) {
            OrderOption or = (OrderOption) o;
            s.append("<div><select name=\"").append("question-").append(this.id).append("-").append(or.getText()).append("\">");
            for (int i = 1; i <= this.getOptions().size(); i++) {
                s.append("<option ").append(or.getSelectedOption() == i ? "selected" : "").append(" value=\"").append(i).append("\">").append(i).append("</option>");
            }
            s.append("<option value=\"dont-know\" ").append(or.getSelectedOption() == -1 ? "selected" : "").append("></option>");
            s.append("</select>").append(or.getText()).append("</div>");
        }
        s.append("</div>");
        return s;
    }
}
