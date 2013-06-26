package com.gn.util.findfastfood.util;

import java.util.Comparator;

import com.gn.util.findfastfood.model.Restaurant;

import android.content.Context;


public class MyComparator {
	private static class AlphabetComparator implements Comparator<Restaurant>{
		Context ctx;
		String term;
		boolean isAscending;
		public AlphabetComparator(Context c, String t, boolean a) {
			ctx = c;
			term = t;
			isAscending = a;
		}

		public int compare(Restaurant lhs, Restaurant rhs) {
//			if(term.equals(ctx.getResources().getString(R.string.header_track_artist))){
//				if(isAscending){
//					return lhs.getArtist().compareToIgnoreCase(rhs.getArtist());
//				}else{
//					return rhs.getArtist().compareToIgnoreCase(lhs.getArtist());
//				}
//			}else if(term.equals(ctx.getResources().getString(R.string.header_track_name))){
//				if(isAscending){
//					return lhs.getTrackName().compareToIgnoreCase(rhs.getTrackName());
//				}else{
//					return rhs.getTrackName().compareToIgnoreCase(lhs.getTrackName());
//				}
//			}
			
			return 0;
		}
	}
	private static class NumberComparator implements Comparator<Restaurant>{
		boolean isAscending;
		public NumberComparator(boolean a) {
			isAscending = a;
		}

		public int compare(Restaurant lhs, Restaurant rhs) {
//			float sizeA = Float.parseFloat(lhs.getTrackPrice());
//			float sizeB = Float.parseFloat(rhs.getTrackPrice());
//			if(sizeA < sizeB){
//				return isAscending?-1:1;
//			}else if(sizeA > sizeB){
//				return isAscending?1:-1;
//			}else{
//				return lhs.getTrackName().compareToIgnoreCase(rhs.getTrackName());
//			}
			
			return 0;
		}
	}

	public static AlphabetComparator alphabetComparator(Context ctx, String term, boolean isAscending){
		return new AlphabetComparator(ctx, term, isAscending);
	}
	
	public static NumberComparator numberComparator(boolean isAscending){
		return new NumberComparator(isAscending);
	}
}
