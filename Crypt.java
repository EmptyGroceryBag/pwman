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
import java.security.spec.AlgorithmParameterSpec;

//Crypto
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.IvParameterSpec;

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

		byte[] initVector = new byte[16];
		rand.nextBytes(initVector);
		AlgorithmParameterSpec ivspec = new IvParameterSpec(initVector);

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
		/*
		System.out.println("\nEnter password");
		String pw = kbd.nextLine();
		*/
		String pw = "1234";
		kbd.close();

		//SecretKey key = new SecretKeySpec(toByteArray(pw), "AES");

		// generate key
		try{
			KeySpec pwkeyspec = new PBEKeySpec(pw.toCharArray(), seed, 128 * 8, 128);
			AlgorithmParameterSpec pwparams = new PBEParameterSpec(seed, 128 * 8, ivspec); // might not have to do this
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
			SecretKey key = keyFactory.generateSecret(pwkeyspec);
			
			//printByteArray(key.getEncoded());

			// encrypt file
			//InputStream fstream = new FileInputStream("test.txt");
			// @MAYBE switch to a buffered stream. 
			//OutputStream eout = new FileOutputStream("test.txt"); 
			File f = new File("test.txt");
			File encryptedFile = encryptFileAES(f, key);
			File decryptedFile = decryptFileAES(encryptedFile, key, pwparams);

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
		Cipher encrypt = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
		encrypt.init(Cipher.ENCRYPT_MODE, key);

		InputStream istream = new BufferedInputStream(new FileInputStream(inputFile), 1024);
		File outputFile = new File(inputFile.getName() + ".aes");
		OutputStream ostream = new CipherOutputStream(new FileOutputStream(outputFile), encrypt);

		if(!outputFile.exists())
			outputFile.createNewFile();

		int b = 0;
		while((b = istream.read()) != -1)
			ostream.write(b);

		istream.close();
		ostream.flush();
		ostream.close();

		return outputFile;
	}

	//TODO: try javax.crypto.CipherOutputStream
	public static File decryptFileAES(File inputFile, Key key, AlgorithmParameterSpec pwparams) throws Exception
	{
		/*
		 * open new file for encrypted data
		 * Read each byte from original file
		 * encrypt each byte
		 * write encrypted bytes
		 * delete original file * */ Cipher decrypt = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
		decrypt.init(Cipher.DECRYPT_MODE, key, pwparams);

		InputStream istream = new BufferedInputStream(new FileInputStream(inputFile), 1024);
		File outputFile = new File("test.txt.decrypt");
		OutputStream ostream = new CipherOutputStream(new FileOutputStream(outputFile), decrypt);

		if(!outputFile.exists())
			outputFile.createNewFile();

		int b = 0;
		while((b = istream.read()) != -1)
			ostream.write(b);

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
