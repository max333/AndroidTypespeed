package com.blank.androidtypespeed.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.content.res.AssetManager;

/**
 * 
 */

public interface RandomWordIterator extends Iterator<String> {

	/**
	 * 
	 */
	public static class Scowl10 implements RandomWordIterator {
		private List<String> words;
		private int counter = 0;

		/**
		 * @throws IOException 
		 * 
		 */
		// TODO factory create instead since does io.
		public Scowl10(AssetManager assetManager) throws IOException {
			InputStream is = assetManager.open("english-words.35");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			words = new ArrayList<String>();
			{
				while ((line = br.readLine()) != null) {
					if (!line.contains("'"))
						words.add(line);
				}
			}
			poorRandomize(words);
			is.close(); // TODO closes all streams??
		}

		/**
		 * Mixes the list.
		 */
		// TODO poor.
		private static void poorRandomize(List<String> words) {
			Random random = new Random(2342);
			for (int i = 0; i <= words.size(); i++) {
				int iSwap1 = random.nextInt(words.size());
				int iSwap2 = random.nextInt(words.size());
				if (iSwap1 != iSwap2) {
					String temp = words.get(iSwap1);
					words.set(iSwap1, words.get(iSwap2));
					words.set(iSwap2, temp);
				}
			}
		}

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public String next() {
			int index = counter++ % words.size();
			return words.get(index);
		}

		@Override
		public void remove() {

		}
	}
}
