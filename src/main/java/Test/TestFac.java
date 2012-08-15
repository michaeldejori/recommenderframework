package Test;

public class TestFac {
	public static void main(String[] args){
		System.out.println(String.valueOf(faculty(24)));
	}
	
	private static long faculty(int n) {
		long fac = 1;
		 
		for (int i = 1; i <= n; i++) {
			fac = fac * i;
		}
		return fac;
	}
}
