//Util
import java.util.Scanner;

//I/O
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//Maps
import java.util.LinkedHashMap;

//Security
import java.security.Key;
import java.security.PrivateKey;
import java.security.KeyFactory;
import java.security.Security;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.InvalidKeyException;

//Crypto
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.interfaces.PBEKey;

/*
 * To generate an AES key:
 * -Set up random salt
 * -New instance of PBEKeySpec
 */

public class Crypt
{
	public static void main(String[] args)
	{
		SecureRandom rand = new SecureRandom();

		byte[] seed = rand.generateSeed(16);
		byte[] data = new byte[128];
		rand.nextBytes(data);

		System.out.println("Seed:");
		for(byte b : seed)
		{
			System.out.printf("%2x, ", b);
		}

		/*
		System.out.println("\nData:");
		for(byte b : data)
		{
			System.out.printf("%2x, ", b);
		}
		*/

		//KeyGenerator keygen = new KeyGenerator();
		Scanner kbd = new Scanner(System.in);
		System.out.println("\nEnter password");
		String pw = kbd.nextLine();
		kbd.close();

		//SecretKey key = new SecretKeySpec(toByteArray(pw), "AES");

		// generate key
		try{
			KeySpec pwkeyspec = new PBEKeySpec(pw.toCharArray(), seed, 128 * 8, 128);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");
			SecretKey key = keyFactory.generateSecret(pwkeyspec);
			
			printByteArray(key.getEncoded());


			// encrypt file
			//InputStream fstream = new FileInputStream("test.txt");
			// @MAYBE switch to a buffered stream. 
			//OutputStream eout = new FileOutputStream("test.txt"); 
			File f = new File("test.txt");
			File encryptedFile = encryptFileAES(f, key);

		} catch(Exception e) { e.printStackTrace(); }

		//encrypt.doFinal(toByteArray("secret message"));
	}

	//TODO: try javax.crypto.CipherOutputStream
	public static File encryptFileAES(File inputFile, Key key) throws Exception
	{
		/*
		 * open new file for encrypted data
		 * Read each byte from original file
		 * encrypt each byte
		 * write encrypted bytes
		 * delete original file
		 * */

		Cipher encrypt = Cipher.getInstance("PBEWithMD5AndTripleDES");
		encrypt.init(Cipher.ENCRYPT_MODE, key);

		InputStream istream = new BufferedInputStream(new FileInputStream("test.txt"), 1024);
		File outputFile = new File(inputFile.getName() + ".aes");
		OutputStream ostream = new FileOutputStream(outputFile);

		if(!outputFile.exists())
			outputFile.createNewFile();

		byte[] buffer = new byte[1024];

		while((istream.read(buffer, 0, 1024) != -1)){
			byte[] encryptedBuffer = encrypt.doFinal(buffer);
			ostream.write(encryptedBuffer);
		}

		ostream.flush();
		ostream.close();

		return outputFile;
	}
	
	public static void printByteArray(byte[] a)
	{
		for(byte b : a)
			System.out.print(b + ", ");
		System.out.println();
	}

	public static byte[] toByteArray(String s)
	{
		byte[] pwInBytes = new byte[s.length()];

		for(int i = 0; i < s.length(); i++)
		{
			if(s.charAt(i) < 255)
				pwInBytes[i] = (byte)s.charAt(i);
			else
				pwInBytes[i] = (byte)255;
			System.out.println(pwInBytes[i]);
		}

		return pwInBytes;
	}
}
