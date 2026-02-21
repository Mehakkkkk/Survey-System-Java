/*import java.util.Scanner;

public class App {
public static void main(String[] args) {
Scanner scanner = new Scanner(System.in);
SurveyDAO dao = new SurveyDAO();

    while (true) {
        System.out.println("1. Naya Survey banayein");
        System.out.println("2. Saare Surveys dekhein");
        System.out.println("3. Exit");
        System.out.println("4. Survey Fill karein (Jawab dein)");
        System.out.println("5. Survey Delete karein");
        System.out.println("6. Survey ka Title badlein (Update)");
        System.out.print("Apna option chunein: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Buffer clear karne ke liye

        if (choice == 1) {
    System.out.print("Survey ka title: ");
    String title = scanner.nextLine();
    int surveyId = dao.createSurvey(title);

    if (surveyId != -1) {
        String addMore = "y";
        while (addMore.equalsIgnoreCase("y")) {
            System.out.print("Sawal likhiye: ");
            String question = scanner.nextLine();
            dao.addQuestion(surveyId, question);

            System.out.print("Ek aur sawal add karna hai? (y/n): ");
            addMore = scanner.nextLine();
        }
    }
}
        else if (choice == 2) {
            dao.fetchSurveysWithQuestions();
        } 
        else if (choice == 3) {
            System.out.println("Bye Bye!");
            break;
        }
        else if (choice == 4) {
    System.out.print("Kaunse Survey ka jawab dena hai? (ID likhiye): ");
    int sId = scanner.nextInt();
    scanner.nextLine(); 

    // Database se sawal dhoondna (Iske liye humein ek naya method chahiye hoga)
    dao.takeSurvey(sId, scanner);
}
else if (choice == 5) {
    System.out.print("Kaunse Survey ko delete karna hai? (ID likhiye): ");
    int deleteId = scanner.nextInt();
    scanner.nextLine(); 
    
    dao.deleteSurvey(deleteId);
}
else if (choice == 6) {
    System.out.print("Kaunse Survey ka title badalna hai? (ID likhiye): ");
    int updateId = scanner.nextInt();
    scanner.nextLine(); // Buffer clear
    
    System.out.print("Naya Title likhiye: ");
    String newTitle = scanner.nextLine();
    
    dao.updateSurveyTitle(updateId, newTitle);
}
    }
    scanner.close();
}
}*/
public class App {
    public static void main(String[] args) {
        // Terminal wala purana code delete ya comment kar dein
        GUI myApp = new GUI();
        myApp.showMainPage();
    }
}