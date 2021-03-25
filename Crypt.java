//Util
import java.util.Scanner;
import java.util.Arrays;

//Security
import java.security.Key;
import java.security.PrivateKey;
import java.security.NoSuchAlgorithmException;
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
	/**
	 * encryptStringAES - Encrypts a byte array using AES and a password. After
	 * the string is encrypted, the initialization vector and salt is appended.
	 *
	 * @param inputString		the string to be encrypted
	 * @param key						AES key
	 * @param pwparams			PBE parameters (object that has salt)
	 * @param ivspec				object encapsulating the initialization vector
	 *
	 * @return 							encrypted bytes
	 * **/
	public static byte[] encryptStringAES(
		byte[] inputString,
		Key key, 
		AlgorithmParameterSpec pwparams,
		AlgorithmParameterSpec ivspec) throws Exception
	{
		Cipher encrypt = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
		encrypt.init(Cipher.ENCRYPT_MODE, key, pwparams);

		byte[] encryptedString = encrypt.doFinal(inputString);

		byte[] header = appendByteArray(
				((PBEParameterSpec)pwparams).getSalt(), 
				((IvParameterSpec)ivspec).getIV()
				);
		return appendByteArray(header, encryptedString);
	}

	/**
	 * decryptStringAES - Decrypts a byte array. The first 32 bytes of the array
	 * must be the IV and salt arrays that were used to encrypt the array.
	 *
	 * @param inputString		byte array to be decrypted
	 * 
	 * @return 							decrypted bytes
	 * **/
	public static byte[] decryptStringAES(byte[] inputString) throws Exception
	{
		// get salt and IV from first 32 bytes of array
		byte[] salt = new byte[16];
		for(int i = 0; i < 16; i++)
			salt[i] = inputString[i];

		byte[] initVector = new byte[16];
		for(int i = 0; i < 16; i++)
			initVector[i] = inputString[i + 16];
		
		// remove IV and salt from string
		byte[] truncated = new byte[inputString.length - 32];
		for(int i = 32; i < inputString.length; i++)
			truncated[i - 32] = inputString[i];

		// @TODO HACK HACK HACK
		Cipher decrypt = null;

		try{
			// @TODO MAKE KEY
			/*
			KeySpec pwkeyspec = new PBEKeySpec("156ab2gdb".toCharArray(), salt, 128 * 8, 128);

			AlgorithmParameterSpec ivspec = new IvParameterSpec(initVector);

			AlgorithmParameterSpec pwparams = new PBEParameterSpec(salt, 128 * 8, ivspec);

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
			SecretKey key = keyFactory.generateSecret(pwkeyspec);

			decrypt = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
			decrypt.init(Cipher.DECRYPT_MODE, key, pwparams);
		} catch(Exception e) { e.printStackTrace(); }		

		return decrypt.doFinal(truncated);
	}
	*/

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

