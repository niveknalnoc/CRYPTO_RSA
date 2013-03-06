import java.math.*;
import java.util.*;
import java.security.*;
import java.io.*;

class CryptoAssignment2 {

	final static BigInteger zero = BigInteger.ZERO;
	final static BigInteger one = BigInteger.ONE;
	final static int bitLength = 512;
	final static BigInteger e = new BigInteger("65537");
	
	//Algorithm for calculating the GCD of two numbers a,b (based on euclidean algorithm)
	static BigInteger gcd(BigInteger a, BigInteger b) {
       
       //Make sure a > b 
        if(a.compareTo(b) < 0) {
        		BigInteger tmp_b = b;
        		b = a;
        		a = tmp_b;
        }
        //Compute GCD of a and b
        if(b.compareTo(zero) == 0) {
            return a;
        }else {
			return gcd(b,a.mod(b));
		}
    }
    
    //Extended euclidean Algorithm -> multiplicative inverse e(mod phi_N)
    static BigInteger[] mul_inv(BigInteger a, BigInteger b) {
     
     	//Create a BigINteger array to store the results of the algorithm
        BigInteger[] results = new BigInteger[3];
        BigInteger w,z; //Temp variable which holds result of a.divide.(b)

        if (b.compareTo(zero) == 0)  { //If b == 0 return base case (a,1,0)
            results[0] = a;
            results[1] = one;
            results[2] = zero;
        }
        else {     
               results = mul_inv(b, a.mod(b)); //Recursive call on (b, a.mod(b))
               z = a.divide(b); //Store a.divide(b) result in z for use below
               w = results[2].multiply(z); //Store results[2].multiply(a.divide(b));
               BigInteger temp = results[1].subtract(w); // results[1] - w;
               results[1] = results[2]; // results[1] becomes results[2]
               results[2] = temp; // results[2] becomes temp -> RESULT WE WANT
            }
        return results;
    }
    
    //CRT
    static BigInteger decryption_method(BigInteger d, BigInteger p, BigInteger q, BigInteger h) {
    		
    		//Results - Signed Hash
    		BigInteger result_signedHash;
    		//calculate h^d(mod p)
    		BigInteger p1 = CryptoAssignment2.mod_pow(h,d,p);
    		//calculate h^d(mod q)
    		BigInteger q1 = CryptoAssignment2.mod_pow(h,d,q);
    		//calculate inverse p
    		BigInteger [] results_p = CryptoAssignment2.mul_inv(p,q);
    		
    		if(results_p[2].compareTo(zero)  < 0) {
			results_p[2] = results_p[2].add(p);
		}
		//result of invers of p
		BigInteger inv_p = results_p[2];
    		//generate tmp
    		BigInteger tmp = (inv_p.multiply(p1.subtract(q1))).mod(p);
		//calculate result 
		result_signedHash = q1.add(tmp.multiply(q));    
    
    		return result_signedHash;
    }
    
    //MOD POW
	static BigInteger mod_pow(BigInteger x, BigInteger y, BigInteger z) {
		
		BigInteger tmp = BigInteger.ZERO;
		BigInteger ans = BigInteger.ONE;
		BigInteger two = new BigInteger("2");
		
		while(y.compareTo(zero) != 0) { // While the exponent not equal zero continue
			tmp = y.mod(two); // mod the exponent with 2
			if(tmp.compareTo(zero) != 0) { // If this result not equal 0 continue
				ans = ans.multiply(x); // ans * base
				ans = ans.mod(z); // mod result of last with the prime
			}
			x = x.multiply(x); // base * base
			x = x.mod(z); // result mod with prime
			y =  y.divide(two); // divide b by 2
		} 	
		return ans;
	}
	
	BigInteger hash_function() {
		
		BigInteger output = new BigInteger("1"); //Output will be stores in this variable
		Byte[] temp_input = new Byte[100000]; //New Byte array of size 100000
		int count = 0;
		try { //Read in the assignmentInput.txt file
				DataInputStream in = new DataInputStream(new FileInputStream("assignmentInput.txt"));
				while(in.available() != 0) { //While not at end of file 
					temp_input[count] = in.readByte(); //Read in each Byte into temp_input
					count++; //Raise count by one
				}
				//Create a byte array the size of the count (size of the code +1)
				byte[] input = new byte[count];
				for(int i = 0 ; i < count ; i++) { //loop to size of count -1 -> code is only occupying 0-count-1
					input[i] = temp_input[i]; //Transfer from Byte array to byte array
				}
				//System.out.println("BYTES COUNT = " + count ); //TESTING COUNT SIZE MATCHES FILE SIZE
				MessageDigest md = MessageDigest.getInstance("SHA-256"); //Hash SHA-256
				md.update(input); 
				byte [] output_hashed = md.digest(); //Output_hashed contains the hash of the input (code in txt file)
				output = new BigInteger(1,output_hashed); //BigInteger containing the hash
		
		} catch(FileNotFoundException fe) {
      		System.out.println("FileNotFoundException : " + fe);
    		} catch(IOException ioe) {
     		 System.out.println("IOException : " + ioe);
    		}catch (Exception e) {
			System.out.println("EXCEPTION!");
		}
		return output;
	}
	
	public static void main(String [] args) {
	
		CryptoAssignment2 c2 = new CryptoAssignment2();
		
		/******************************************************/
		//************ GENERATE p, q, n, phi_N, e ************//
		/******************************************************/
		
		//Generate 2, 512 bit probable primes p and q
		BigInteger p = BigInteger.probablePrime(bitLength, new Random());
		BigInteger q = BigInteger.probablePrime(bitLength, new Random());
		//Calculate the product of pq (N)
		BigInteger N = p.multiply(q);
		//Calculate phi(N) -> (p-1)(q-1)
		BigInteger phi_N = (p.subtract(one)).multiply(q.subtract(one));
		//Calculate the gcd of e and phi_N
		BigInteger gcd = CryptoAssignment2.gcd(phi_N,e);
		//While e and phi_N are not relatively prime (gcd e,phi_N != 1)
		while(gcd.compareTo(one) != 0) {
			//Generate 2, 512 bit probable primes p and q
			p = BigInteger.probablePrime(bitLength, new Random());
			q = BigInteger.probablePrime(bitLength, new Random());
			//Calculate the product of pq (N)
			N = p.multiply(q);
			//Calculate phi(N) -> (p-1)(q-1)
			phi_N = (p.subtract(one)).multiply(q.subtract(one));
			//Calculate the gcd of e and phi_N
			gcd = CryptoAssignment2.gcd(phi_N,e);
		}
		
		/***************************************************/
		//************ HASH AND DIGITALLY SIGN ************//
		/***************************************************/
		
		//Calculate d using extended euclidean algorithm (MOD INVERSE)
		BigInteger[] results_d = CryptoAssignment2.mul_inv(phi_N,e);
		//If result is negative add phi_N to this value
		if(results_d[2].compareTo(zero)  < 0) {
			results_d[2] = results_d[2].add(phi_N);
		}
		//BigInteger d
		BigInteger d = results_d[2];
		//Get hash of the code
		BigInteger hashed_message = c2.hash_function();
		//Digitally sign the hash of the code
		BigInteger signed_hash = c2.decryption_method(d, p, q, hashed_message);
		
		//Print to output
		System.out.println("probable prime p = " + p);
		System.out.println();
		System.out.println("probable prime q = " + q);
		System.out.println();
		System.out.println("product of pq (N) = " + N.toString(16));
		System.out.println();
		System.out.println("phi(N) = " + phi_N);
		System.out.println();
		System.out.println("gcd of (e and phi(N)) = " + gcd);
		System.out.println();
		System.out.println("d = " + d);
		System.out.println();
		System.out.println("Hased message = " + hashed_message);
		System.out.println();
		System.out.println("Digitally signed message = " + signed_hash);
		
		/*
		//TESTING CORRECT RESULTS ABOVE
		BigInteger a1 = e.modInverse(phi_N);
		BigInteger a2 = hashed_message.modPow(d,N);
		System.out.println();
		System.out.println();
		System.out.println("TEST RESULTS");
		System.out.println();
		System.out.println();
		System.out.println("d = " + d);
		System.out.println();
		System.out.println("d CORRECT = " + a1);
		System.out.println();
		System.out.println("hashed message = " + signed_hash);
		System.out.println();
		System.out.println("hashed message CORRECT  = " + a2);
		*/
	}
}
