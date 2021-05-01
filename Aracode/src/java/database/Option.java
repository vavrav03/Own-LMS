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
import java.util.ArrayList;

/**
 *
 * @author Vavra
 */
public abstract class Option {
    
    private int selectedOption = -1;
    protected int subquestionNumber;
    
    public Option(int subquestionNumber){
        this.subquestionNumber = subquestionNumber;
    }
    
    public void setSelectedOption(int selectedOption){
        this.selectedOption = selectedOption;
    }
    
    public int getSelectedOption(){
        return selectedOption;
    }
    
    public int getSubquestionNumber(){
        return subquestionNumber;
    }
    
    /**
     *
     * @return true if student answered right
     */
    public abstract boolean isRight();

    @Override
    public String toString() {
        return "Option{" + "selectedOption=" + selectedOption + ", subquestionNumber=" + subquestionNumber + '}';
    }
    
    /**
     *
     * @param question Question, for which are these questions for
     * @param con Connection to the database
     * @return all options asociated with passed question
     */
    public static ArrayList<Option> createOptions(Question question, Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".`options_" + question.getType() + "` WHERE `question_id`=?" + (question.getType() == 1 ? " ORDER BY subquestion_number, option_number" : ""))) {
            statement.setInt(1, question.getId());
            ArrayList<Option> options = new ArrayList();
            try (ResultSet r = statement.executeQuery()) {
                switch (question.getType()) {
                    case 0:
                        r.next();
                        options.add(new BooleanOption(r.getBoolean("answer")));
                        break;
                    case 1:
                        int subquestionNumber = 0;
                        ArrayList<String> wrongWords = new ArrayList();
                        r.next();
                        String rightWord = r.getString("text");
                        while (r.next()) {
                            if (subquestionNumber != r.getInt("subquestion_number")) {
                                options.add(new FillInTextOption(subquestionNumber, wrongWords, rightWord));
                                subquestionNumber++;
                                rightWord = r.getString("text");
                                wrongWords = new ArrayList();
                            } else {
                                wrongWords.add(r.getString("text"));
                            }
                        }
                        options.add(new FillInTextOption(subquestionNumber, wrongWords, rightWord));
                    case 2:
                        while (r.next()) {
                            options.add(new ConnectOption(r.getString("definition"), r.getInt("option_number"), r.getString("text")));
                        }
                        break;
                    case 3:
                        while (r.next()) {
                            options.add(new OrderOption(r.getInt("option_number"), r.getString("answer")));
                        }
                }
                return options;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException();
    }

    public static class BooleanOption extends Option {

        private boolean rightAnswer;

        public BooleanOption(boolean rightAnswer) {
            super(0);
            this.rightAnswer = rightAnswer;
        }

        public boolean getRightAnswer() {
            return rightAnswer;
        }

        @Override
        public boolean isRight(){
            return rightAnswer == (super.selectedOption == 1);
        }
        
        @Override
        public String toString() {
            return "BooleanOption{" + "rightAnswer=" + rightAnswer + '}';
        }
    }

    public static class FillInTextOption extends Option {

        private ArrayList<String> wrongWords;
        private String rightWord;

        public FillInTextOption(int subquestionNumber, ArrayList<String> wrongWords, String rightWord) {
            super(subquestionNumber);
            this.wrongWords = wrongWords;
            this.rightWord = rightWord;
        }

        public String getRightWord() {
            return rightWord;
        }

        public ArrayList<String> getWrongWords() {
            return wrongWords;
        }

        public void addWrongWord(String wrongWord) {
            this.wrongWords.add(wrongWord);
        }
        
        @Override
        public boolean isRight(){
            return super.selectedOption == 0;
        }

        @Override
        public String toString() {
            return "FillInTextOption{" + "wrongWords=" + wrongWords + ", rightWord=" + rightWord + '}';
        }
    }

    public static class OrderOption extends Option {

        private String text;

        public OrderOption(int subquestionNumber, String text) {
            super(subquestionNumber);
            this.text = text;
        }
        
        public String getText() {
            return text;
        }

        @Override
        public boolean isRight(){
            return super.selectedOption == super.subquestionNumber;
        }

        @Override
        public String toString() {
            return "OrderOption{" + "text=" + text + '}';
        }
    }

    public static class ConnectOption extends OrderOption {

        private String definition;

        public ConnectOption(String definition, int subquestionNumber, String text) {
            super(subquestionNumber, text);
            this.definition = definition;
        }

        public String getDefinition() {
            return definition;
        }

        @Override
        public String toString() {
            return "ConnectOption{" + "definition=" + definition + '}';
        }
    }

}
