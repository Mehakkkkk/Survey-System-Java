import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class GUI {
    private SurveyDAO dao = new SurveyDAO();
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;

    public void showMainPage() {
        frame = new JFrame("Survey System Pro");
        frame.setSize(750, 450); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // --- TOP PANEL (Add Survey) ---
        JPanel topPanel = new JPanel();
        JTextField titleField = new JTextField(20);
        JButton addButton = new JButton("Add Survey");
        topPanel.add(new JLabel("Title:"));
        topPanel.add(titleField);
        topPanel.add(addButton);

        // --- CENTER PANEL (Table) ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Survey Title"}, 0);
        table = new JTable(tableModel);
        refreshTable();

        // --- BOTTOM PANEL (Buttons) ---
        JPanel bottomPanel = new JPanel();
        JButton addQButton = new JButton("Add Questions");
        JButton viewButton = new JButton("View/Manage Questions");
        JButton takeButton = new JButton("Take Survey");
        JButton deleteButton = new JButton("Delete Survey");
        
        bottomPanel.add(addQButton);
        bottomPanel.add(viewButton);
        bottomPanel.add(takeButton);
        bottomPanel.add(deleteButton);

        // --- ACTIONS ---

        // Add Survey
        addButton.addActionListener(e -> {
            String title = titleField.getText();
            if (!title.isEmpty()) {
                dao.createSurvey(title);
                titleField.setText("");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(frame, "Title can't be empty!");
            }
        });

        // Add Questions
        addQButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int surveyId = (int) table.getValueAt(row, 0);
                String surveyTitle = (String) table.getValueAt(row, 1);
                openAddQuestionDialog(surveyId, surveyTitle);
            } else {
                JOptionPane.showMessageDialog(frame, "Select a survey from the table!");
            }
        });

        // View/Delete Questions
        viewButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int surveyId = (int) table.getValueAt(row, 0);
                String surveyTitle = (String) table.getValueAt(row, 1);
                openQuestionsDialog(surveyId, surveyTitle);
            } else {
                JOptionPane.showMessageDialog(frame, "Select a Survey first!");
            }
        });

        // Take Survey
        takeButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int surveyId = (int) table.getValueAt(row, 0);
                openTakeSurveyDialog(surveyId);
            } else {
                JOptionPane.showMessageDialog(frame, "Select a Survey!");
            }
        });

        // Delete Survey
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(frame, "Do you really want to delete this survey?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dao.deleteSurvey(id);
                    refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Select a Survey first!");
            }
        });

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/survey_system?allowPublicKeyRetrieval=true&useSSL=false", "root", "root1234")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM surveys");
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("title")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAddQuestionDialog(int surveyId, String title) {
        while (true) {
            String question = JOptionPane.showInputDialog(frame, 
                "Survey: " + title + "\nWrite a new question:", 
                "Add Questions", JOptionPane.QUESTION_MESSAGE);

            if (question == null || question.trim().isEmpty()) break;

            dao.addQuestion(surveyId, question);
            
            int choice = JOptionPane.showConfirmDialog(frame, "Question saved! Do you want to add another?", "Success", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.NO_OPTION) break;
        }
    }

    private void openTakeSurveyDialog(int surveyId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/survey_system?allowPublicKeyRetrieval=true&useSSL=false", "root", "root1234")) {
            String sql = "SELECT id, question_text FROM questions WHERE survey_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, surveyId);
            ResultSet rs = pstmt.executeQuery();

            boolean hasQuestions = false;
            while (rs.next()) {
                hasQuestions = true;
                int qId = rs.getInt("id");
                String qText = rs.getString("question_text");

                String answer = JOptionPane.showInputDialog(frame, "Question: " + qText, "Fill the survey", JOptionPane.QUESTION_MESSAGE);
                if (answer != null && !answer.trim().isEmpty()) {
                    dao.submitResponse(qId, answer);
                }
            }
            if (!hasQuestions) {
                JOptionPane.showMessageDialog(frame, "No questions found!");
            } else {
                JOptionPane.showMessageDialog(frame, "✅ Thank you! Responses recorded.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openQuestionsDialog(int surveyId, String title) {
        JDialog dialog = new JDialog(frame, "Manage Questions: " + title, true);
        dialog.setSize(450, 350);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> questionsList = new JList<>(listModel);
        ArrayList<Integer> questionIds = new ArrayList<>();

        Runnable loadQuestions = () -> {
            listModel.clear();
            questionIds.clear();
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/survey_system?allowPublicKeyRetrieval=true&useSSL=false", "root", "root1234")) {
                String sql = "SELECT id, question_text FROM questions WHERE survey_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, surveyId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    questionIds.add(rs.getInt("id"));
                    listModel.addElement("❓ " + rs.getString("question_text"));
                }
            } catch (Exception e) { e.printStackTrace(); }
        };

        loadQuestions.run();

        JButton deleteQBtn = new JButton("Delete Selected Question");
        deleteQBtn.setBackground(new Color(255, 200, 200));

        deleteQBtn.addActionListener(e -> {
            int index = questionsList.getSelectedIndex();
            if (index != -1) {
                int qId = questionIds.get(index);
                int confirm = JOptionPane.showConfirmDialog(dialog, "Delete this question?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dao.deleteQuestion(qId);
                    loadQuestions.run(); 
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a question to delete.");
            }
        });

        dialog.add(new JScrollPane(questionsList), BorderLayout.CENTER);
        JPanel btnPanel = new JPanel();
        btnPanel.add(deleteQBtn);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(al -> dialog.dispose());
        btnPanel.add(closeBtn);
        
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
}