//Util
import java.util.Scanner;
import java.util.Arrays;
import java.util.Base64;

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
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.IvParameterSpec;

public class Crypt
{
	public static void main(String[] args)
	{
		System.out.println("\nEnter password");
		char[] pw = System.console().readPassword();

		// random salt and IV
		SecureRandom rand = new SecureRandom();

		byte[] initVector = new byte[16];
		rand.nextBytes(initVector);

		byte[] seed = rand.generateSeed(16);
		rand.nextBytes(seed);

		AlgorithmParameterSpec ivspec = new IvParameterSpec(initVector);

		try{
			//(byte[] password, byte[] salt, int iterationCount, int keyLength)
			KeySpec pwkeyspec = new PBEKeySpec(pw, seed, 128 * 8, 128);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
			SecretKey key = keyFactory.generateSecret(pwkeyspec);

			AlgorithmParameterSpec pwparams = new PBEParameterSpec(seed, 128 * 8, ivspec);

			String message = "Super secret message :D";
			byte[] encryptedMessage = encryptStringAES(message, key, pwparams, ivspec);
			String decryptedMessage = decryptStringAES(encryptedMessage);
		} catch(Exception e) { e.printStackTrace(); }
	}

	/**
	 * encryptStringAES - Encrypts a string using AES and a password. After the 
	 * string is encrypted, the initialization vector and salt is appended.
	 *
	 * @param inputString		the string to be encrypted
	 * @param key						AES key
	 * @param pwparams			PBE parameters (object that has salt)
	 * @param ivspec				object encapsulating the initialization vector
	 *
	 * @return 							encrypted bytes
	 * **/
	public static byte[] encryptStringAES(
		String inputString,
		Key key, 
		AlgorithmParameterSpec pwparams,
		AlgorithmParameterSpec ivspec) throws Exception
	{
		byte[] string = Base64.getEncoder().encode(toByteArray(inputString));
		byte[] header = appendByteArray(((PBEParameterSpec)pwparams).getSalt(), ((IvParameterSpec)ivspec).getIV());

		// this is the actual encryption process
		Cipher encrypt = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
		encrypt.init(Cipher.ENCRYPT_MODE, key, pwparams);

		byte[] encryptedString = encrypt.doFinal(string);

		return appendByteArray(header, encryptedString); 
	}

	public static String decryptStringAES(byte[] inputString, Key key, PBEParameterSpec pwparams, IvParameterSpec ivspec) throws Exception
	{
		/*// get salt and IV from first 32 bits of array
		byte[] initVector = new byte[16];
		for(int i = 0; i < 16; i++)
			initVector[i] = inputString[i];

		byte[] salt = new byte[16];
		for(int i = 0; i < 16; i++)
			salt[i] = inputString[i + 16];

		// truncate salt and IV to prepare string for decryption
		byte[] truncated = new byte[inputString.length - 32];
		for(int i = 32; i < inputString.length; i++)
			truncated[i - 32] = inputString[i];

		// encode as Base64 or else the cipher will bark at us...
		byte[] b64String = Base64.getEncoder().encode(truncated);
		*/

		// @TODO HACK HACK HACK
		Cipher decrypt = null;

		try{
			// @TODO don't hardcode password...
			KeySpec pwkeyspec = new PBEKeySpec("156ab2gdb".toCharArray(), salt, 128 * 8, 128);

			AlgorithmParameterSpec ivspec = new IvParameterSpec(initVector);

			AlgorithmParameterSpec pwparams = new PBEParameterSpec(salt, 128 * 8, ivspec);

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
			SecretKey key = keyFactory.generateSecret(pwkeyspec);

			decrypt = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
			decrypt.init(Cipher.DECRYPT_MODE, key, pwparams);
		} catch(Exception e) { e.printStackTrace(); }		

		return (decrypt.doFinal(b64String)).toString();
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
		}

		return pwInBytes;
	}

	public static byte[] appendByteArray(byte[] src, byte[] dest)
	{
		byte[] concatedArray = new byte[src.length + dest.length];
		
		for(int i = 0; i < src.length; i++){
			concatedArray[i] = src[i];
		}

		int offset = src.length;
		for(int i = 0; i < dest.length; i++){
			concatedArray[offset + i] = dest[i];
		}

		return concatedArray;
	}
}

