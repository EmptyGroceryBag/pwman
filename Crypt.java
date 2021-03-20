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

		// random salt and IV
		byte[] seed = rand.generateSeed(16);
		byte[] initVector = new byte[16];
		rand.nextBytes(initVector);
		AlgorithmParameterSpec ivspec = new IvParameterSpec(initVector);

		Scanner kbd = new Scanner(System.in);
		System.out.println("\nEnter password");
		String pw = kbd.nextLine();
		kbd.close();

		try{
			KeySpec pwkeyspec = new PBEKeySpec(pw.toCharArray(), seed, 128 * 8, 128);
			AlgorithmParameterSpec pwparams = new PBEParameterSpec(seed, 128 * 8, ivspec); // might not have to do this
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
			SecretKey key = keyFactory.generateSecret(pwkeyspec);
			
			File f = new File("message.txt");
			File encryptedFile = encryptFileAES(f, key, pwparams);
			File decryptedFile = decryptFileAES(encryptedFile, key, pwparams);

		} catch(Exception e) { e.printStackTrace(); }
	}

	public static File encryptFileAES(File inputFile, Key key, AlgorithmParameterSpec pwparams) throws Exception
	{
		/*
		 * open new file for encrypted data
		 * Read each byte from original file
		 * encrypt each byte
		 * write encrypted bytes
		 * @TODO delete original file
		 * */
		Cipher encrypt = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
		encrypt.init(Cipher.ENCRYPT_MODE, key, pwparams);

		InputStream istream = new FileInputStream(inputFile);
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

	public static File decryptFileAES(File inputFile, Key key, AlgorithmParameterSpec pwparams) throws Exception
	{
		Cipher decrypt = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
		decrypt.init(Cipher.DECRYPT_MODE, key, pwparams);

		InputStream istream = new FileInputStream(inputFile);
		File outputFile = new File("message.txt.decrypt");
		OutputStream ostream = new CipherOutputStream(new FileOutputStream(outputFile), decrypt);

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
