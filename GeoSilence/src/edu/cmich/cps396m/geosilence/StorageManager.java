package edu.cmich.cps396m.geosilence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

/**
 * a class to store and access a {@link List} of {@link Rule}s. Rules are stored
 * serialized in a file After an instance of class is created with default
 * constructor, getRules(), addRule() and deleteRule() methods can be used. If
 * any rule is modified, call write() to save the changes. Use findRuleByName()
 * to make sure all rules have unique names
 */
public class StorageManager {
	public static final String filename = "rules.dat";

	private File storageFile;
	private ArrayList<Rule> rules;

	public StorageManager(Context c) {
		storageFile = new File(c.getFilesDir(), filename);
		read();
	}

	@SuppressWarnings("unchecked")
	public void read() {
		try {
			FileInputStream fis = new FileInputStream(storageFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			rules = (ArrayList<Rule>) ois.readObject();
			Log.i("GS", rules.size() + " records read from " + filename);
			ois.close();
			fis.close();
		} catch (Exception e) {
			Log.w("GS", "Failed to read from " + filename + ". File could be corrupted or non-existant");
			storageFile.delete();
			rules = new ArrayList<Rule>();
		}
	}

	public void write() {
		try {
			FileOutputStream fos = new FileOutputStream(storageFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(rules);
			Log.i("GS", rules.size() + " records written to " + filename);
			oos.close();
			fos.close();
		} catch (Exception e) {
			Log.e("GS", "Failed to write to " + filename + "! Suicide time.");
			e.printStackTrace();
		}
	}

	public void addRule(Rule rule) throws DataException {
		if (findRuleByName(rule.getName()) != null) {
			Log.e("GS", "Attempted to add a rule with non-unique way");
			throw new DataException("A rule with such name already exists!");
		}
		rules.add(rule);
		write();
	}

	public List<Rule> getRules() {
		if (rules == null)
			read();
		return rules;
	}

	public void deleteRule(Rule rule) {
		if (rule != null) {
			rules.remove(rule);
			write();
		}
	}

	public void deleteRule(int position) {
		rules.remove(position);
		write();
	}

	public Rule findRuleByName(String name) {
		for (Rule r : rules) {
			if (r.getName().equals(name))
				return r;
		}
		return null;
	}

	public void replaceRule(int index, Rule updatedRule) throws DataException {
		if (rules.get(index) == null)
			throw new DataException("Invalid index for rule editing");
		rules.set(index, updatedRule);
		write();
	}

	public void moveUp(Rule rule) {
		int index = rules.indexOf(rule);
		moveUp(index);
	}

	public void moveUp(int index) {
		if (index >= 0 && index < rules.size()) {
			rules.add(index + 1, rules.remove(index));
			write();
		}
	}

	public void moveDown(Rule rule) {
		int index = rules.indexOf(rule);
		moveDown(index);
	}

	public void moveDown(int index) {
		if (index > 0 && index <= rules.size()) {
			rules.add(index - 1, rules.remove(index));
			write();
		}
	}

	public class DataException extends Exception {
		private static final long serialVersionUID = 1L;

		public DataException(String m) {
			super(m);
		}
	}
}
