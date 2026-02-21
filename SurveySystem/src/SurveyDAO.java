import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class SurveyDAO {
private String url = "jdbc:mysql://localhost:3307/survey_system?allowPublicKeyRetrieval=true&useSSL=false";
private String user = "root";
private String password = "root1234";

// Yeh method hona bahut zaroori hai, iske bina error aayega
public int createSurvey(String surveyTitle) {
    String sql = "INSERT INTO surveys (title) VALUES (?)";
    int generatedId = -1;

    // Statement.RETURN_GENERATED_KEYS likhna zaroori hai ID wapas lene ke liye
    try (Connection conn = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
        
        pstmt.setString(1, surveyTitle);
        pstmt.executeUpdate();

        // Database se wo ID maangna jo abhi-abhi bani hai
        var rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            generatedId = rs.getInt(1);
        }
        System.out.println("✅ Survey save ho gaya! ID: " + generatedId);
        
    } catch (Exception e) {
        System.out.println("❌ Error: " + e.getMessage());
    }
    return generatedId; 
}
public void addQuestion(int surveyId, String questionText) {
String sql = "INSERT INTO questions (survey_id, question_text) VALUES (?, ?)";
try (Connection conn = DriverManager.getConnection(url, user, password);
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
    pstmt.setInt(1, surveyId);
    pstmt.setString(2, questionText);
    pstmt.executeUpdate();
    System.out.println("✅ Sawal save ho gaya!");
    
} catch (Exception e) {
    System.out.println("❌ Error saving question: " + e.getMessage());
}
}
public void fetchSurveysWithQuestions() {
    // SQL JOIN: Surveys aur Questions dono tables se data nikalne ke liye
    String sql = "SELECT s.title, q.question_text " +
                 "FROM surveys s " +
                 "LEFT JOIN questions q ON s.id = q.survey_id";

    try (Connection conn = DriverManager.getConnection(url, user, password);
         java.sql.Statement stmt = conn.createStatement();
         java.sql.ResultSet rs = stmt.executeQuery(sql)) {

        System.out.println("\n--- Detailed Survey Report ---");
        String currentTitle = "";

        while (rs.next()) {
            String title = rs.getString("title");
            String question = rs.getString("question_text");

            // Agar naya survey title aaye toh use print karein
            if (!title.equals(currentTitle)) {
                System.out.println("\n📋 Survey: " + title);
                currentTitle = title;
            }
            
            // Us survey ke niche uska sawal dikhayein
            if (question != null) {
                System.out.println("   ❓ Question: " + question);
            } else {
                System.out.println("   (No questions added yet)");
            }
        }
        System.out.println("\n------------------------------\n");

    } catch (Exception e) {
        System.out.println("❌ Error: " + e.getMessage());
    }
}
public void submitResponse(int questionId, String answerText) {
    String sql = "INSERT INTO responses (question_id, answer_text) VALUES (?, ?)";

    try (Connection conn = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, questionId);
        pstmt.setString(2, answerText);
        pstmt.executeUpdate();
        
    } catch (Exception e) {
        System.out.println("❌ Error saving response: " + e.getMessage());
    }
}
public void takeSurvey(int surveyId, Scanner scanner) {
    String sql = "SELECT id, question_text FROM questions WHERE survey_id = ?";

    try (Connection conn = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, surveyId);
        java.sql.ResultSet rs = pstmt.executeQuery();

        System.out.println("\n--- Survey Shuru Ho Raha Hai ---");
        while (rs.next()) {
            int qId = rs.getInt("id");
            System.out.println("Q: " + rs.getString("question_text"));
            System.out.print("Aapka Jawab: ");
            String ans = scanner.nextLine();
            
            // Jawab ko save karein
            submitResponse(qId, ans);
        }
        System.out.println("✅ Survey submit karne ke liye dhanyawad!\n");

    } catch (Exception e) {
        System.out.println("❌ Error: " + e.getMessage());
    }
}
public void deleteSurvey(int surveyId) {
    String sql = "DELETE FROM surveys WHERE id = ?";

    try (Connection conn = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, surveyId);
        int rowsDeleted = pstmt.executeUpdate();
        
        if (rowsDeleted > 0) {
            System.out.println("✅ Survey (ID: " + surveyId + ") aur uske saare sawal/jawab delete ho gaye!");
        } else {
            System.out.println("❌ Is ID ka koi survey nahi mila.");
        }
        
    } catch (Exception e) {
        System.out.println("❌ Error deleting survey: " + e.getMessage());
    }
}
public void updateSurveyTitle(int surveyId, String newTitle) {
    String sql = "UPDATE surveys SET title = ? WHERE id = ?";

    try (Connection conn = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, newTitle);
        pstmt.setInt(2, surveyId);
        
        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("✅ Survey ka naam badal kar '" + newTitle + "' kar diya gaya hai!");
        } else {
            System.out.println("❌ Is ID ka koi survey nahi mila.");
        }
        
    } catch (Exception e) {
        System.out.println("❌ Error updating survey: " + e.getMessage());
    }
}
public void deleteQuestion(int questionId) {
    String sql = "DELETE FROM questions WHERE id = ?";
    try (Connection conn = DriverManager.getConnection(url, user, password);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, questionId);
        pstmt.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}