//Crypto
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

//Security
import java.security.Key;
import java.security.spec.KeySpec;
import java.security.SecureRandom;

public class KeyGen
{
	private byte[] initVector = new byte[16];
	private byte[] salt = new byte[16];

	PBEParameterSpec pwparams;

	// Known salt and IV
	public KeyGen()
	{
		SecureRandom rand = new SecureRandom();

		rand.nextBytes(this.initVector);
		rand.nextBytes(this.salt);
	}

	// Unknown salt and IV
	public KeyGen(byte[] iv, byte[] salt)
	{
		this.initVector = iv;
		this.salt = salt;

		IvParameterSpec ivspec = new IvParameterSpec(this.initVector);
		pwparams = new PBEParameterSpec(this.salt, 1024, ivspec);
	}

	private SecretKey key;

	public SecretKey makeKey()
	{
		try{
			// @TODO this does not work on Alacritty (Windows)
			char[] pw = System.console().readPassword();
			
			//(byte[] password, byte[] salt, int iterationCount, int keyLength)
			PBEKeySpec pwkeyspec = new PBEKeySpec(pw, this.salt, 1024, 128);

			SecretKeyFactory keyFactory = 
				SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
			key = keyFactory.generateSecret(pwkeyspec);

		} catch(Exception e) { e.printStackTrace(); }

		return key;
	}

	public byte[] getInitVector() { return initVector; }
	public byte[] getSalt() { return salt; }
	public PBEParameterSpec getPBESpec() { return pwparams; }
}
