//Util
import java.util.Scanner;

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

			Cipher encrypt = Cipher.getInstance("PBEWithMD5AndTripleDES");
			encrypt.init(Cipher.ENCRYPT_MODE, key);
		} catch(Exception e) { e.printStackTrace(); }

		//encrypt.doFinal(toByteArray("secret message"));
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
