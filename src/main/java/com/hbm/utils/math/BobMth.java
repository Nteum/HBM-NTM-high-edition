package com.hbm.utils.math;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnegative;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.ToIntFunction;

public class BobMth {
	
	public static double safeClamp(double val, double min, double max) {

		val = Mth.clamp(val, min, max);
		
		if(val == Double.NaN) {
			val = (min + max) / 2D;
		}
		
		return val;
	}
	
	public static Vec3 interpVec(Vec3 vec1, Vec3 vec2, float interp) {
		return new Vec3(
				interp(vec1.x,  vec2.x, interp),
				interp(vec1.y,  vec2.y, interp),
				interp(vec1.z,  vec2.z, interp)
				);
	}
	
	public static double interp(double x, double y, float interp) {
		return x + (y - x) * interp;
	}
	
	public static double getAngleFrom2DVecs(double x1, double z1, double x2, double z2) {
		
		double upper = x1 * x2 + z1 * z2;
		double lower = Math.sqrt(x1 * x1 + z1 * z1) * Math.sqrt(x2 * x2 + z2 * z2);
		
		double result = Math.toDegrees(Math.cos(upper / lower));
		
		if(result >= 180)
			result -= 180;
		
		return result;
	}
	
	public static double getCrossAngle(Vec3 vel, Vec3 rel) {
		
		vel.normalize();
		rel.normalize();

		double vecProd = rel.x * vel.x + rel.y * vel.y + rel.z * vel.z;
		double bot = rel.length() * vel.length();
		double angle = Math.acos(vecProd / bot) * 180 / Math.PI;
		
		if(angle >= 180)
			angle -= 180;
		
		return angle;
	}

	public static float remap(float num, float min1, float max1, float min2, float max2){
		return ((num - min1) / (max1 - min1)) * (max2 - min2) + min2;
	}
	
	public static float remap01(float num, float min1, float max1){
		return (num - min1) / (max1 - min1);
	}
	
	public static float remap01_clamp(float num, float min1, float max1){
		return Mth.clamp((num - min1) / (max1 - min1), 0, 1);
	}
	
	public static Direction[] getShuffledDirs() {
		
		Direction[] dirs = new Direction[6];
		List<Integer> indices = new ArrayList() {{ add(0); add(1); add(2); add(3); add(4); add(5); }};
		Collections.shuffle(indices);
		
		for(int i = 0; i < 6; i++) {
			dirs[i] = Direction.from3DDataValue(indices.get(i));
		}
		
		return dirs;
	}

	public static String toPercentage(float amount, float total) {
		return NumberFormat.getPercentInstance().format(amount / total);
	}
	
	public static String[] ticksToDate(long ticks) {
		
		int tickDay = 48000;
		int tickYear = tickDay * 100;
		
		final String[] dateOut = new String[3];
		long year = Math.floorDiv(ticks, tickYear);
		byte day = (byte) Math.floorDiv(ticks - tickYear * year, tickDay);
		float time = ticks - (tickYear * year + tickDay * day);
		time = (float) convertScale(time, 0, tickDay, 0, 10F);
		dateOut[0] = String.valueOf(year);
		dateOut[1] = String.valueOf(day);
		dateOut[2] = String.valueOf(time);
		return dateOut;
	}
	
	/**
	 * Rescale a number from one range to another
	 * @param toScale - The integer to scale
	 * @param oldMin - The current minimum value
	 * @param oldMax - The current maximum value
	 * @param newMin - The desired minimum value
	 * @param newMax - The desired maximum value
	 * @return The scaled number
	 */
	public static double convertScale(double toScale, double oldMin, double oldMax, double newMin, double newMax) {
		double prevRange = oldMax - oldMin;
		double newRange = newMax - newMin;
		return (((toScale - oldMin) * newRange) / prevRange) + newMin;
	}
	
	/**
	 * Rounds a number to so many significant digits
	 * @param num The number to round
	 * @param digits Amount of digits
	 * @return The rounded double
	 */
	public static double roundDecimal(double num, @Nonnegative int digits) {
		if(digits < 0)
			throw new IllegalArgumentException("Attempted negative number in non-negative field! Attempted value: " + digits);

		return new BigDecimal(num).setScale(digits, RoundingMode.HALF_UP).doubleValue();
	}

	public static boolean getBlink() {
		return System.currentTimeMillis() % 1000 < 500;
	}

	public static String getShortNumber(long l) {
	
		if(l >= Math.pow(10, 18)) {
			double res = l / Math.pow(10, 18);
			res = Math.round(res * 100.0) / 100.0;
			return res + "E";
		}
		if(l >= Math.pow(10, 15)) {
			double res = l / Math.pow(10, 15);
			res = Math.round(res * 100.0) / 100.0;
			return res + "P";
		}
		if(l >= Math.pow(10, 12)) {
			double res = l / Math.pow(10, 12);
			res = Math.round(res * 100.0) / 100.0;
			return res + "T";
		}
		if(l >= Math.pow(10, 9)) {
			double res = l / Math.pow(10, 9);
			res = Math.round(res * 100.0) / 100.0;
			return res + "G";
		}
		if(l >= Math.pow(10, 6)) {
			double res = l / Math.pow(10, 6);
			res = Math.round(res * 100.0) / 100.0;
			return res + "M";
		}
		if(l >= Math.pow(10, 3)) {
			double res = l / Math.pow(10, 3);
			res = Math.round(res * 100.0) / 100.0;
			return res + "k";
		}
		
		return Long.toString(l);
	}
	
	/**
	 * Adjusted sqrt, approaches standard sqrt but sqrt(x) is never bigger than x
	 * 
	 *      ____________
	 *     /       1    |     1
	 * _  / x + ――――――――  - ―――――
	 *  \/      (x + 2)²    x + 2
	 * 
	 * @param x
	 * @return
	 */
	public static double squirt(double x) {
		return Math.sqrt(x + 1D / ((x + 2D) * (x + 2D))) - 1D / (x + 2D);
	}
	
	/** A convenient way to re-define the value of pi, should the laws of nature change. */
	public static void setPi(double pi) {
		try {
			Field field = Math.class.getDeclaredField("PI");
			field.setDouble(null,pi);
		}catch (Exception e){}
	}
	
	public static double angularDifference(double alpha, double beta) {
		double delta = (beta - alpha + 180) % 360 - 180;
		return delta < -180 ? delta + 360 : delta;
	}

	// I am sick of trying to remember the ridiculous quirks of Java 8
	// so I wrote this thing that can shit any int-ish list-ish into a regular fucking int[]
	// made by mellow, thrown here by 70k
	public static int[] intCollectionToArray(Collection<Integer> in) {
		return intCollectionToArray(in, i -> (int)i);
	}

	public static int[] intCollectionToArray(Collection<Integer> in, ToIntFunction<? super Object> mapper) {
		return Arrays.stream(in.toArray()).mapToInt(mapper).toArray();
	}

	public static int[] collectionToIntArray(Collection<? extends Object> in, ToIntFunction<? super Object> mapper) {
		return Arrays.stream(in.toArray()).mapToInt(mapper).toArray();
 }

	/** Soft peak sine */
	public static double sps(double x) {
		return Math.sin(Math.PI / 2D * Math.cos(x));
	}
	
	/** Square wave sine, make sure squarination is [0;1] */
	public static double sws(double x, double squarination) {
		double s = Math.sin(x);
		return Math.pow(Math.abs(s), 2 - squarination) / s;
	}

	/**
	 * 新添加
	 * */
	public static float degree2Radians(float degree){
		return Mth.wrapDegrees(degree) * Mth.PI / 180;
	}



	// 最小公倍数
	public static int lcm(int a, int b) {
		if (a == 0 || b == 0) return 0;
		// 先除后乘，防止 (a * b) 直接溢出
		return Math.abs(a / gcd(a, b) * b);
	}

	// 最大公因数
	public static int gcd(int a, int b) {
		while (b != 0) {
			int temp = a % b;
			a = b;
			b = temp;
		}
		return a;
	}

	// 2. 扩展：求多个数的 GCD (使用数组)
	public static int getGCD(int ... numbers) {
		if (numbers == null || numbers.length == 0) return 0;
		int result = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			result = gcd(result, numbers[i]);
			// 优化：如果中间结果已经变成 1，那么整体 GCD 必然是 1，可以直接结束
			if (result == 1) return 1;
		}
		return result;
	}
}
