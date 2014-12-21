package com.ssof.dbm;

import com.ssof.twitter.SingleTweet;
import com.ssof.utils.StringUtils;
import com.ssof.utils.TextAnalizer;
import org.joda.time.DateTime;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.User;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
	private String workingDirectory = System.getProperty("user.dir") + File.separator + "database";
	
	public DBManager(){
		
	}
	
	/**
	 * Aggiunge al database i tweet passati come argomento.
	 * @param tweetBuf 
	 * @return true in caso di successo, false altrimenti
	 */
	public boolean addTweets(List<Status> tweetBuf){
		String filename = workingDirectory;
		DateTime gc = new DateTime();
		
		if(filename.length() > 0)filename += File.separator;
		filename += gc.getDayOfMonth() + "-" + gc.getMonthOfYear() + "-" + gc.getYear() + ".tws";

		try {
			FileOutputStream fos = new FileOutputStream(filename, true); //true == append
			DataOutputStream dos = new DataOutputStream(fos);

			for (Status st : tweetBuf) { //questo for aggiunge ogni tweet al file, uno per uno
				GeoLocation location = st.getGeoLocation();

				dos.writeLong(st.getCreatedAt().getTime());
				dos.writeBoolean(st.isRetweet());
				if (location != null) {
					dos.writeDouble(location.getLatitude());
					dos.writeDouble(location.getLongitude());
				} else {
					dos.writeDouble(0.0);
					dos.writeDouble(0.0);
				}

				String text = st.getText();
				if (text != null) {
					byte[] tb = text.getBytes();
					dos.writeShort(tb.length);
					dos.write(tb);
				} else {
					byte[] tb = "null".getBytes();
					dos.writeShort(tb.length);
					dos.write(tb);
				}

				User usr = st.getUser();
				if (usr != null) {
					String username = usr.getName();
					if (username != null) {
						byte[] ub = username.getBytes();
						dos.writeShort(ub.length);
						dos.write(ub);
					} else {
						byte[] ub = "null".getBytes();
						dos.writeShort(ub.length);
						dos.write(ub);
					}
				} else {
					byte[] ub = "null".getBytes();
					dos.writeShort(ub.length);
					dos.write(ub);
				}

				String source = getSource(st.getSource());
				byte[] sb = source.getBytes();
				dos.writeShort(sb.length);
				dos.write(sb);

				Place place = st.getPlace();
				if (place != null) {
					String pname = place.getName();
					if (pname != null) {
						byte[] pb = pname.getBytes();
						dos.writeShort(pb.length);
						dos.write(pb);
					} else {
						byte[] pb = "null".getBytes();
						dos.writeShort(pb.length);
						dos.write(pb);
					}
				} else {
					byte[] pb = "null".getBytes();
					dos.writeShort(pb.length);
					dos.write(pb);
				}
			}
			
			dos.flush();
			dos.close();
			
		} catch (IOException x) {
		    System.err.println(x);
		    return false;
		}
		
		return true;
	}
	
	/**
	 * Aggiunge al database i tweet passati come argomento.
	 * @param tweetBuf 
	 * @param file
	 * @return true in caso di successo, false altrimenti
	 */
	private boolean addTweets_singletweet(List<SingleTweet> tweetBuf, File file){
		try {
			FileOutputStream fos = new FileOutputStream(file, true);
			DataOutputStream dos = new DataOutputStream(fos);

			for (SingleTweet st : tweetBuf) {
				dos.writeLong(st.getMillisSinceEpoch());
				dos.writeBoolean(st.isRetweet());
				dos.writeDouble(st.getLatitude());
				dos.writeDouble(st.getLongitude());

				byte[] tb = st.getText().getBytes();
				dos.writeShort(tb.length);
				dos.write(tb);

				byte[] ub = st.getUser().getBytes();
				dos.writeShort(ub.length);
				dos.write(ub);

				byte[] sb = st.getSource().getBytes();
				dos.writeShort(sb.length);
				dos.write(sb);

				byte[] pb = st.getPlace().getBytes();
				dos.writeShort(pb.length);
				dos.write(pb);
			}
			
			dos.flush();
			dos.close();
			
		} catch (IOException x) {
		    System.err.println(x);
		    return false;
		}
		
		return true;
	}


	/**
	 * Legge i tweet dai file specificati e li mette in una List. 
	 * @param mainWindow
	 * @param files
	 * @return
	 */
	
	public List<SingleTweet> loadTweets(File[] files) {
		ArrayList <SingleTweet> tweets = new ArrayList<>();
		
		try {
			for (final File file : files) { //per ogni file..
				FileChannel roChannel = new RandomAccessFile(file, "r").getChannel();
				MappedByteBuffer roBuf = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) roChannel.size());

				while (true) { //..leggo fino alla fine del file
					long millisSinceEpoch;
					boolean isRetweet;
					double latitude;
					double longitude;
					byte b;
					String text;
					short tl;
					String user;
					short ul;
					String source;
					short sl;
					String place;
					short pl;

					try {
						millisSinceEpoch = roBuf.getLong();
						b = roBuf.get(); //leggo un byte
						isRetweet = (b != 0);

						latitude = roBuf.getDouble();
						longitude = roBuf.getDouble();

						tl = roBuf.getShort();
						byte[] tb = new byte[tl];
						roBuf.get(tb);
						text = new String(tb).toLowerCase();

						ul = roBuf.getShort();
						byte[] ub = new byte[ul];
						roBuf.get(ub);
						user = new String(ub);

						sl = roBuf.getShort();
						byte[] sb = new byte[sl];
						roBuf.get(sb);
						source = new String(sb);

						pl = roBuf.getShort();
						byte[] pb = new byte[pl];
						roBuf.get(pb);
						place = new String(pb);

						tweets.add(new SingleTweet(millisSinceEpoch, isRetweet, latitude, longitude, text, user, source, place));

					} catch (BufferUnderflowException e) {
						break; //se ho raggiunto la fine del file, esco dal while
					} catch (Exception e) {
						System.err.println("Errore durante il caricamento del file " + file);
						System.err.println("Sono riuscito a leggere " + tweets.size() + " tweet.");
						break;
					}
				}

				//ho finito di leggere un file, lo chiudo e passo al prossimo [prossima iterazione del for]
				roChannel.close();
			}
			
		} catch (IOException x) {
		    System.err.println(x);
		    return null;
		}
		
		return tweets;
	}
	
	/**
	 * Carica i tweet dai file specificati, restituendo due liste di tweet contemporanei tra di loro.
	 * @param files
	 * @return
	 */
	public List<List<SingleTweet>> loadContTweets(File[] files) {
		List<List<SingleTweet>> list = new ArrayList<>();
		ArrayList <SingleTweet> tweets1 = new ArrayList<>();
		ArrayList <SingleTweet> tweets2 = new ArrayList<>();
		boolean choice = true;
		
		try {
			for (final File file : files) { //per ogni file..
				FileChannel roChannel = new RandomAccessFile(file, "r").getChannel();
				MappedByteBuffer roBuf = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) roChannel.size());

				while (true) { //..leggo fino alla fine del file
					long millisSinceEpoch;
					boolean isRetweet;
					double latitude;
					double longitude;
					byte b;
					String text;
					short tl;
					String user;
					short ul;
					String source;
					short sl;
					String place;
					short pl;

					try {
						millisSinceEpoch = roBuf.getLong();
						b = roBuf.get(); //leggo un byte
						isRetweet = (b != 0);

						latitude = roBuf.getDouble();
						longitude = roBuf.getDouble();

						tl = roBuf.getShort();
						byte[] tb = new byte[tl];
						roBuf.get(tb);
						text = new String(tb).toLowerCase();

						ul = roBuf.getShort();
						byte[] ub = new byte[ul];
						roBuf.get(ub);
						user = new String(ub);

						sl = roBuf.getShort();
						byte[] sb = new byte[sl];
						roBuf.get(sb);
						source = new String(sb);

						pl = roBuf.getShort();
						byte[] pb = new byte[pl];
						roBuf.get(pb);
						place = new String(pb);

					} catch (BufferUnderflowException e) {
						break; //se ho raggiunto la fine del file, esco dal while
					}

					SingleTweet tweet = new SingleTweet(
						millisSinceEpoch, isRetweet, latitude, longitude, text, user, source, place
					);

					if (choice) {
						tweets1.add(tweet);
					}
					else {
						tweets2.add(tweet);
					}

					choice = !choice;
				}

				//ho finito di leggere un file, lo chiudo e passo al prossimo [prossima iterazione del for]
				roChannel.close();
			}
			
		} catch (IOException x) {
		    System.err.println(x);
		    return null;
		}
		
		list.add(tweets1);
		list.add(tweets2);
		
		return list;
	}
	
	/**
	 * Prende dei file tws e li riunisce in un unico file dalle seguenti caratteristiche:
	 * - Accenti rimossi [dal testo dei tweet]
	 * - Punteggiatura rimossa [dal testo dei tweet]
	 * - Articoli rimossi [dal testo dei tweet]
	 * - Tweet non italiani rimossi
	 * 
	 * @param inputfile File originali
	 * @param outputfile File destinazione
	 */
	public void preprocessTWSFile(TextAnalizer ta, File [] inputfiles, File outputfile){
		System.out.println("Entrato nella preprocessTWSFile(), carico i tweet dai file selezionati");
		
		for(File file : inputfiles){
			File [] arr = new File[1];
			arr[0] = file;
			
			List <SingleTweet> tweets = loadTweets(arr);
		
			List <SingleTweet> buf = new ArrayList<>();
		
			System.out.println("Tweet caricati dal file " + file + ", filtro i tweet non italiani");
			tweets = ta.filterItalianTweets(tweets);
			System.out.println("Tweet non italiani filtrati");
		
			for(SingleTweet t : tweets){
				String [] w = StringUtils.getTweetWordsNoArticlesNoAccents(t.text);
				String newText = "";
			
				for(int i = 0 ; i < w.length ; i++){
					if(i == w.length-1){
						newText += w[i];
					} else {
						newText += w[i] + " ";
					}
				}

				buf.add(new SingleTweet(t.millisSinceEpoch, t.isRetweet, t.latitude, t.longitude, newText, t.user, t.source, t.place));

				if(buf.size()>100){
					addTweets_singletweet(buf, outputfile);
					buf.clear();
				}

			}

			if(!buf.isEmpty())
				addTweets_singletweet(buf, outputfile);
		}
		
		System.out.println("Preprocessamento finito");
	}

	/**
	 * Specifica in quale directory si trova il database.
	 * @param abswd Path assoluto della directory nella quale si trova il database
	 * @return true in caso di successo, false altrimenti
	 */
	public boolean setWorkingDirectory(String abswd){
		workingDirectory = abswd;
		System.out.println("'"+abswd+"'");
		return true;
	}

	public String getWorkingDirectory(){
		return workingDirectory;
	}
	
	private String getSource(String source) {
		if(source == null)
			return "null";
		
		if(source.charAt(0) != '<')
			return source;
		
		
		int len = source.length();
		int i, j;
		
		for(i = 0 ; i < len ; i++){
			if(source.charAt(i) == '>')
				break;
		}

		for(j = i ; j < len ; j++){
			if(source.charAt(j) == '<')
				break;
		}
		
		if(j < len)
			return source.substring(i+1, j);
		else return "null";
	}
}
