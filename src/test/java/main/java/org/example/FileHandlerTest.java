import static org.junit.jupiter.api.Assertions.*;

import main.java.org.example.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;



class FileHandlerTest {
    File file;
    FileHandler fileHandler;
    @BeforeEach
    public void init(){
        file = new File("contactsTest.txt");
        fileHandler = new FileHandler();


    }
    @Test
    void addContactTest() {
        fileHandler.addContact("Alma");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            assertTrue(file.exists());
        }
    }

    @Test
    void checkIfRegistered() {
    }

    @Test
    void getContactsFromFile() {
    }

    @Test
    void saveMessageToFile() {
    }

    @Test
    void sendMessagesToClient() {
    }
}