package scratch.java;

import org.junit.Test;

import java.io.*;

public class Demo {

	@Test
	public void write() throws IOException {
		Per person = new Per("test");
		FileOutputStream fileOutput = new FileOutputStream("person.txt");
		ObjectOutputStream output = new ObjectOutputStream(fileOutput);
		output.writeObject(person);
		output.flush();
		output.close();
		fileOutput.flush();
		fileOutput.close();
	}

	@Test
	public void read() throws IOException, ClassNotFoundException {
		FileInputStream fileInput = new FileInputStream("person.txt");
		ObjectInputStream objectInput = new ObjectInputStream(fileInput);
		Per person = (Per) objectInput.readObject();
		System.out.println(person);
	}


}
